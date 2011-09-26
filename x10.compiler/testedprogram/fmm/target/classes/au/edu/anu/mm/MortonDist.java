package au.edu.anu.mm;


final public class MortonDist
extends x10.array.Dist
  implements x10.util.concurrent.Atomic,
              x10.x10rt.X10JavaSerializable 
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, MortonDist.class);
    
    public static final x10.rtt.RuntimeType<MortonDist> $RTT = new x10.rtt.NamedType<MortonDist>(
    "au.edu.anu.mm.MortonDist", /* base class */MortonDist.class
    , /* parents */ new x10.rtt.Type[] {x10.array.Dist.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(MortonDist $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        x10.array.Dist.$_deserialize_body($_obj, $deserializer);
        x10.array.PlaceGroup pg = (x10.array.PlaceGroup) $deserializer.readRef();
        $_obj.pg = pg;
        $_obj.dimDigits = $deserializer.readInt();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        MortonDist $_obj = new MortonDist((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        if (pg instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.pg);
        } else {
        $serializer.write(this.pg);
        }
        $serializer.write(this.dimDigits);
        
    }
    
    // constructor just for allocation
    public MortonDist(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public int
          X10$object_lock_id0;
        
        
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public x10.util.concurrent.OrderedLock
                                                                                                        getOrderedLock(
                                                                                                        ){
            
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70623 =
              this.
                X10$object_lock_id0;
            
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.util.concurrent.OrderedLock t70624 =
              x10.util.concurrent.OrderedLock.getLock((int)(t70623));
            
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70624;
        }
        
        
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public static int
          X10$class_lock_id1 =
          x10.util.concurrent.OrderedLock.createNewLockID();
        
        
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public static x10.util.concurrent.OrderedLock
                                                                                                        getStaticOrderedLock(
                                                                                                        ){
            
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70625 =
              au.edu.anu.mm.MortonDist.X10$class_lock_id1;
            
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.util.concurrent.OrderedLock t70626 =
              x10.util.concurrent.OrderedLock.getLock((int)(t70625));
            
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70626;
        }
        
        
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
/**
     * The place group for this distribution
     */public x10.array.PlaceGroup
          pg;
        
//#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
/**
     * The number of binary digits per dimension in the Z-index. 
     */public int
          dimDigits;
        
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
/**
     * Cached restricted region for the current place.
     */public transient x10.array.Region
          regionForHere;
        
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public static class MortonSubregion
                                                                                                      extends x10.array.Region
                                                                                                        implements x10.util.concurrent.Atomic,
                                                                                                                    x10.x10rt.X10JavaSerializable 
                                                                                                      {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, MortonSubregion.class);
            
            public static final x10.rtt.RuntimeType<MortonSubregion> $RTT = new x10.rtt.NamedType<MortonSubregion>(
            "au.edu.anu.mm.MortonDist.MortonSubregion", /* base class */MortonSubregion.class
            , /* parents */ new x10.rtt.Type[] {x10.array.Region.$RTT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(MortonSubregion $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                x10.array.Region.$_deserialize_body($_obj, $deserializer);
                $_obj.start = $deserializer.readInt();
                $_obj.end = $deserializer.readInt();
                au.edu.anu.mm.MortonDist out$ = (au.edu.anu.mm.MortonDist) $deserializer.readRef();
                $_obj.out$ = out$;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                MortonSubregion $_obj = new MortonSubregion((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                super.$_serialize($serializer);
                $serializer.write(this.start);
                $serializer.write(this.end);
                if (out$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$);
                } else {
                $serializer.write(this.out$);
                }
                
            }
            
            // constructor just for allocation
            public MortonSubregion(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            
                
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public au.edu.anu.mm.MortonDist
                  out$;
                
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public int
                  X10$object_lock_id0;
                
                
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public x10.util.concurrent.OrderedLock
                                                                                                                getOrderedLock(
                                                                                                                ){
                    
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70627 =
                      this.
                        X10$object_lock_id0;
                    
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.util.concurrent.OrderedLock t70628 =
                      x10.util.concurrent.OrderedLock.getLock((int)(t70627));
                    
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70628;
                }
                
                
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public static int
                  X10$class_lock_id1 =
                  x10.util.concurrent.OrderedLock.createNewLockID();
                
                
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public static x10.util.concurrent.OrderedLock
                                                                                                                getStaticOrderedLock(
                                                                                                                ){
                    
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70629 =
                      au.edu.anu.mm.MortonDist.MortonSubregion.X10$class_lock_id1;
                    
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.util.concurrent.OrderedLock t70630 =
                      x10.util.concurrent.OrderedLock.getLock((int)(t70629));
                    
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70630;
                }
                
                
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public int
                  start;
                
//#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public int
                  end;
                
                
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
// creation method for java code
                public static au.edu.anu.mm.MortonDist.MortonSubregion $make(final au.edu.anu.mm.MortonDist out$,
                                                                             final int start,
                                                                             final int end){return new au.edu.anu.mm.MortonDist.MortonSubregion((java.lang.System[]) null).$init(out$,start,end);}
                
                // constructor for non-virtual call
                final public au.edu.anu.mm.MortonDist.MortonSubregion au$edu$anu$mm$MortonDist$MortonSubregion$$init$S(final au.edu.anu.mm.MortonDist out$,
                                                                                                                       final int start,
                                                                                                                       final int end) { {
                                                                                                                                               
//#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Region this70146 =
                                                                                                                                                 ((x10.array.Region)(this));
                                                                                                                                               
//#line 469 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
this70146.x10$lang$Object$$init$S();
                                                                                                                                               
//#line 472 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
this70146.rank = 3;
                                                                                                                                               
//#line 472 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
this70146.rect = false;
                                                                                                                                               
//#line 472 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
this70146.zeroBased = false;
                                                                                                                                               
//#line 472 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
this70146.rail = false;
                                                                                                                                               
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this.out$ = out$;
                                                                                                                                               
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"

                                                                                                                                               
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist.MortonSubregion this7014870981 =
                                                                                                                                                 this;
                                                                                                                                               
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this7014870981.X10$object_lock_id0 = -1;
                                                                                                                                               
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this.start = start;
                                                                                                                                               
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this.end = end;
                                                                                                                                           }
                                                                                                                                           return this;
                                                                                                                                           }
                
                // constructor
                public au.edu.anu.mm.MortonDist.MortonSubregion $init(final au.edu.anu.mm.MortonDist out$,
                                                                      final int start,
                                                                      final int end){return au$edu$anu$mm$MortonDist$MortonSubregion$$init$S(out$,start,end);}
                
                
                
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
// creation method for java code
                public static au.edu.anu.mm.MortonDist.MortonSubregion $make(final au.edu.anu.mm.MortonDist out$,
                                                                             final int start,
                                                                             final int end,
                                                                             final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.mm.MortonDist.MortonSubregion((java.lang.System[]) null).$init(out$,start,end,paramLock);}
                
                // constructor for non-virtual call
                final public au.edu.anu.mm.MortonDist.MortonSubregion au$edu$anu$mm$MortonDist$MortonSubregion$$init$S(final au.edu.anu.mm.MortonDist out$,
                                                                                                                       final int start,
                                                                                                                       final int end,
                                                                                                                       final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                                                                 
//#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Region this70155 =
                                                                                                                                                                                   ((x10.array.Region)(this));
                                                                                                                                                                                 
//#line 469 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
this70155.x10$lang$Object$$init$S();
                                                                                                                                                                                 
//#line 472 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
this70155.rank = 3;
                                                                                                                                                                                 
//#line 472 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
this70155.rect = false;
                                                                                                                                                                                 
//#line 472 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
this70155.zeroBased = false;
                                                                                                                                                                                 
//#line 472 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
this70155.rail = false;
                                                                                                                                                                                 
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this.out$ = out$;
                                                                                                                                                                                 
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"

                                                                                                                                                                                 
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist.MortonSubregion this7015770982 =
                                                                                                                                                                                   this;
                                                                                                                                                                                 
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this7015770982.X10$object_lock_id0 = -1;
                                                                                                                                                                                 
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this.start = start;
                                                                                                                                                                                 
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this.end = end;
                                                                                                                                                                                 
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70631 =
                                                                                                                                                                                   paramLock.getIndex();
                                                                                                                                                                                 
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this.X10$object_lock_id0 = ((int)(t70631));
                                                                                                                                                                             }
                                                                                                                                                                             return this;
                                                                                                                                                                             }
                
                // constructor
                public au.edu.anu.mm.MortonDist.MortonSubregion $init(final au.edu.anu.mm.MortonDist out$,
                                                                      final int start,
                                                                      final int end,
                                                                      final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$mm$MortonDist$MortonSubregion$$init$S(out$,start,end,paramLock);}
                
                
                
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public int
                                                                                                                size$O(
                                                                                                                ){
                    
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70632 =
                      end;
                    
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70633 =
                      start;
                    
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70634 =
                      ((t70632) - (((int)(t70633))));
                    
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70635 =
                      ((t70634) + (((int)(1))));
                    
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70635;
                }
                
                
//#line 47 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public int
                                                                                                                totalLength$O(
                                                                                                                ){
                    
//#line 47 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist t70636 =
                      this.
                        out$;
                    
//#line 47 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Region t70637 =
                      ((x10.array.Region)(t70636.
                                            region));
                    
//#line 47 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70638 =
                      t70637.size$O();
                    
//#line 47 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70638;
                }
                
                
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public boolean
                                                                                                                isConvex$O(
                                                                                                                ){
                    
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return true;
                }
                
                
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public boolean
                                                                                                                isEmpty$O(
                                                                                                                ){
                    
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70639 =
                      end;
                    
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70640 =
                      start;
                    
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t70641 =
                      ((t70639) < (((int)(t70640))));
                    
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70641;
                }
                
                
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public int
                                                                                                                indexOf$O(
                                                                                                                final x10.array.Point pt){
                    
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70642 =
                      pt.
                        rank;
                    
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t70643 =
                      ((int) t70642) !=
                    ((int) 3);
                    
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (t70643) {
                        
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return -1;
                    }
                    
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist t70644 =
                      this.
                        out$;
                    
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70645 =
                      t70644.getMortonIndex$O(((x10.array.Point)(pt)));
                    
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70646 =
                      start;
                    
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70647 =
                      ((t70645) - (((int)(t70646))));
                    
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70647;
                }
                
                
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public int
                                                                                                                indexOf$O(
                                                                                                                final int i0,
                                                                                                                final int i1,
                                                                                                                final int i2){
                    
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist t70648 =
                      this.
                        out$;
                    
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70649 =
                      t70648.getMortonIndex$O((int)(i0),
                                              (int)(i1),
                                              (int)(i2));
                    
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70650 =
                      start;
                    
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70651 =
                      ((t70649) - (((int)(t70650))));
                    
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70651;
                }
                
                
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public x10.array.Region
                                                                                                                boundingBox(
                                                                                                                ){
                    
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.lang.UnsupportedOperationException t70652 =
                      ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(((java.lang.String)("boundingBox()")))));
                    
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
throw t70652;
                }
                
                
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public x10.array.Region
                                                                                                                computeBoundingBox(
                                                                                                                ){
                    
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.lang.UnsupportedOperationException t70653 =
                      ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(((java.lang.String)("computeBoundingBox()")))));
                    
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
throw t70653;
                }
                
                
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>
                                                                                                                min(
                                                                                                                ){
                    
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t70654 =
                      ((x10.core.fun.Fun_0_1)(new au.edu.anu.mm.MortonDist.MortonSubregion.$Closure$72()));
                    
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70654;
                }
                
                
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>
                                                                                                                max(
                                                                                                                ){
                    
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t70657 =
                      ((x10.core.fun.Fun_0_1)(new au.edu.anu.mm.MortonDist.MortonSubregion.$Closure$73(this)));
                    
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70657;
                }
                
                
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public boolean
                                                                                                                contains$O(
                                                                                                                final x10.array.Region that){
                    
//#line 65 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t70667 =
                      au.edu.anu.mm.MortonDist.MortonSubregion.$RTT.instanceOf(that);
                    
//#line 65 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (t70667) {
                        
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist.MortonSubregion thatMS =
                          ((au.edu.anu.mm.MortonDist.MortonSubregion)(x10.rtt.Types.<au.edu.anu.mm.MortonDist.MortonSubregion> cast(that,au.edu.anu.mm.MortonDist.MortonSubregion.$RTT)));
                        
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70658 =
                          this.totalLength$O();
                        
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70659 =
                          thatMS.totalLength$O();
                        
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
boolean t70662 =
                          ((int) t70658) ==
                        ((int) t70659);
                        
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (t70662) {
                            
//#line 68 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70660 =
                              this.
                                start;
                            
//#line 68 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70661 =
                              thatMS.
                                start;
                            
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
t70662 = ((t70660) <= (((int)(t70661))));
                        }
                        
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
boolean t70665 =
                          t70662;
                        
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (t70665) {
                            
//#line 68 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70663 =
                              this.
                                end;
                            
//#line 68 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70664 =
                              thatMS.
                                end;
                            
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
t70665 = ((t70663) >= (((int)(t70664))));
                        }
                        
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t70666 =
                          t70665;
                        
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (t70666) {
                            
//#line 69 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return true;
                        }
                    }
                    
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return false;
                }
                
                
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public x10.array.Region
                                                                                                                intersection(
                                                                                                                final x10.array.Region that){
                    
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t70688 =
                      au.edu.anu.mm.MortonDist.MortonSubregion.$RTT.instanceOf(that);
                    
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (t70688) {
                        
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist.MortonSubregion thatMS =
                          ((au.edu.anu.mm.MortonDist.MortonSubregion)(x10.rtt.Types.<au.edu.anu.mm.MortonDist.MortonSubregion> cast(that,au.edu.anu.mm.MortonDist.MortonSubregion.$RTT)));
                        
//#line 78 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70668 =
                          this.totalLength$O();
                        
//#line 78 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70669 =
                          thatMS.totalLength$O();
                        
//#line 78 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t70671 =
                          ((int) t70668) !=
                        ((int) t70669);
                        
//#line 78 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (t70671) {
                            
//#line 60 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
final int rank70161 =
                              rank;
                            
//#line 60 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
final x10.array.EmptyRegion alloc2063770162 =
                              ((x10.array.EmptyRegion)(new x10.array.EmptyRegion((java.lang.System[]) null)));
                            
//#line 60 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
alloc2063770162.$init(((int)(rank70161)));
                            
//#line 60 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
final x10.array.Region t70670 =
                              ((x10.array.Region)(((x10.array.Region)
                                                    alloc2063770162)));
                            
//#line 78 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70670;
                        }
                        
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist.MortonSubregion alloc47913 =
                          ((au.edu.anu.mm.MortonDist.MortonSubregion)(new au.edu.anu.mm.MortonDist.MortonSubregion((java.lang.System[]) null)));
                        
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist t7067670983 =
                          this.
                            out$;
                        
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int a7041670984 =
                          start;
                        
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int b7041770985 =
                          thatMS.
                            start;
                        
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t7067270986 =
                          ((a7041670984) < (((int)(b7041770985))));
                        
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
int t7067370987 =
                           0;
                        
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t7067270986) {
                            
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t7067370987 = b7041770985;
                        } else {
                            
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t7067370987 = a7041670984;
                        }
                        
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int t7067770988 =
                          t7067370987;
                        
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int a7041870989 =
                          end;
                        
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int b7041970990 =
                          thatMS.
                            end;
                        
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t7067470991 =
                          ((a7041870989) < (((int)(b7041970990))));
                        
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
int t7067570992 =
                           0;
                        
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t7067470991) {
                            
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t7067570992 = a7041870989;
                        } else {
                            
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t7067570992 = b7041970990;
                        }
                        
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int t7067870993 =
                          t7067570992;
                        
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.util.concurrent.OrderedLock t7067970994 =
                          x10.util.concurrent.OrderedLock.createNewLock();
                        
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
alloc47913.$init(t7067670983,
                                                                                                                                       t7067770988,
                                                                                                                                       t7067870993,
                                                                                                                                       t7067970994);
                        
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Region __desugarer__var__37__70420 =
                          ((x10.array.Region)(((x10.array.Region)
                                                alloc47913)));
                        
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
x10.array.Region ret70421 =
                           null;
                        
//#line 79 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7068070995 =
                          __desugarer__var__37__70420.
                            rank;
                        
//#line 79 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7068170996 =
                          au.edu.anu.mm.MortonDist.MortonSubregion.this.
                            rank;
                        
//#line 79 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t7068270997 =
                          ((int) t7068070995) ==
                        ((int) t7068170996);
                        
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t7068570998 =
                          !(t7068270997);
                        
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (t7068570998) {
                            
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t7068470999 =
                              true;
                            
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (t7068470999) {
                                
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.lang.FailedDynamicCheckException t7068371000 =
                                  new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==this(:au.edu.anu.mm.MortonDist.MortonSubregion).rank}");
                                
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
throw t7068371000;
                            }
                        }
                        
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
ret70421 = ((x10.array.Region)(__desugarer__var__37__70420));
                        
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Region t70686 =
                          ((x10.array.Region)(ret70421));
                        
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70686;
                    } else {
                        
//#line 81 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.lang.UnsupportedOperationException t70687 =
                          ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(((java.lang.String)("intersection(Region(!MortonSubregion))")))));
                        
//#line 81 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
throw t70687;
                    }
                }
                
                
//#line 85 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public x10.array.Region
                                                                                                                complement(
                                                                                                                ){
                    
//#line 85 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.lang.UnsupportedOperationException t70689 =
                      ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(((java.lang.String)("complement()")))));
                    
//#line 85 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
throw t70689;
                }
                
                
//#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public x10.array.Region
                                                                                                                product(
                                                                                                                final x10.array.Region that){
                    
//#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.lang.UnsupportedOperationException t70690 =
                      ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(((java.lang.String)("product(Region)")))));
                    
//#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
throw t70690;
                }
                
                
//#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public x10.array.Region
                                                                                                                projection(
                                                                                                                final int axis){
                    
//#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.lang.UnsupportedOperationException t70691 =
                      ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(((java.lang.String)("projection(axis : Int)")))));
                    
//#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
throw t70691;
                }
                
                
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public x10.array.Region
                                                                                                                translate(
                                                                                                                final x10.array.Point v){
                    
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.lang.UnsupportedOperationException t70692 =
                      ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(((java.lang.String)("translate(Point)")))));
                    
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
throw t70692;
                }
                
                
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public x10.array.Region
                                                                                                                eliminate(
                                                                                                                final int axis){
                    
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.lang.UnsupportedOperationException t70693 =
                      ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(((java.lang.String)("eliminate(axis : Int)")))));
                    
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
throw t70693;
                }
                
                
//#line 91 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public boolean
                                                                                                                contains$O(
                                                                                                                final x10.array.Point p){
                    
//#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70694 =
                      p.
                        rank;
                    
//#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t70695 =
                      ((int) t70694) !=
                    ((int) 3);
                    
//#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (t70695) {
                        
//#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return false;
                    }
                    
//#line 93 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist t70696 =
                      this.
                        out$;
                    
//#line 93 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int index =
                      t70696.getMortonIndex$O(((x10.array.Point)(p)));
                    
//#line 94 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70697 =
                      start;
                    
//#line 94 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
boolean t70699 =
                      ((index) >= (((int)(t70697))));
                    
//#line 94 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (t70699) {
                        
//#line 94 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70698 =
                          end;
                        
//#line 94 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
t70699 = ((index) <= (((int)(t70698))));
                    }
                    
//#line 94 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t70700 =
                      t70699;
                    
//#line 94 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70700;
                }
                
                
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public x10.lang.Iterator<x10.array.Point>
                                                                                                                iterator(
                                                                                                                ){
                    
//#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist.MortonSubregion.MortonSubregionIterator alloc47914 =
                      ((au.edu.anu.mm.MortonDist.MortonSubregion.MortonSubregionIterator)(new au.edu.anu.mm.MortonDist.MortonSubregion.MortonSubregionIterator((java.lang.System[]) null)));
                    
//#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.util.concurrent.OrderedLock t7070171001 =
                      x10.util.concurrent.OrderedLock.createNewLock();
                    
//#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
alloc47914.$init(this,
                                                                                                                                   ((au.edu.anu.mm.MortonDist.MortonSubregion)(this)),
                                                                                                                                   t7070171001);
                    
//#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.lang.Iterator<x10.array.Point> t70702 =
                      x10.rtt.Types.<x10.lang.Iterator<x10.array.Point>> cast(alloc47914,new x10.rtt.ParameterizedType(x10.lang.Iterator.$RTT, x10.array.Point.$RTT));
                    
//#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70702;
                }
                
                
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public static class MortonSubregionIterator
                                                                                                               extends x10.core.Ref
                                                                                                                 implements x10.lang.Iterator,
                                                                                                                            x10.util.concurrent.Atomic,
                                                                                                                             x10.x10rt.X10JavaSerializable 
                                                                                                               {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, MortonSubregionIterator.class);
                    
                    public static final x10.rtt.RuntimeType<MortonSubregionIterator> $RTT = new x10.rtt.NamedType<MortonSubregionIterator>(
                    "au.edu.anu.mm.MortonDist.MortonSubregion.MortonSubregionIterator", /* base class */MortonSubregionIterator.class
                    , /* parents */ new x10.rtt.Type[] {new x10.rtt.ParameterizedType(x10.lang.Iterator.$RTT, x10.array.Point.$RTT), x10.rtt.Types.OBJECT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body(MortonSubregionIterator $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        $_obj.index = $deserializer.readInt();
                        au.edu.anu.mm.MortonDist.MortonSubregion out$ = (au.edu.anu.mm.MortonDist.MortonSubregion) $deserializer.readRef();
                        $_obj.out$ = out$;
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        MortonSubregionIterator $_obj = new MortonSubregionIterator((java.lang.System[]) null);
                        $deserializer.record_reference($_obj);
                        return $_deserialize_body($_obj, $deserializer);
                        
                    }
                    
                    public short $_get_serialization_id() {
                    
                         return $_serialization_id;
                        
                    }
                    
                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                    
                        $serializer.write(this.index);
                        if (out$ instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$);
                        } else {
                        $serializer.write(this.out$);
                        }
                        
                    }
                    
                    // constructor just for allocation
                    public MortonSubregionIterator(final java.lang.System[] $dummy) { 
                    super($dummy);
                    }
                    // bridge for method abstract public x10.lang.Iterator.next():T
                    final public x10.array.Point
                      next$G(){return next();}
                    
                        
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public au.edu.anu.mm.MortonDist.MortonSubregion
                          out$;
                        
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public int
                          X10$object_lock_id0;
                        
                        
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public x10.util.concurrent.OrderedLock
                                                                                                                         getOrderedLock(
                                                                                                                         ){
                            
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70703 =
                              this.
                                X10$object_lock_id0;
                            
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.util.concurrent.OrderedLock t70704 =
                              x10.util.concurrent.OrderedLock.getLock((int)(t70703));
                            
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70704;
                        }
                        
                        
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public static int
                          X10$class_lock_id1 =
                          x10.util.concurrent.OrderedLock.createNewLockID();
                        
                        
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public static x10.util.concurrent.OrderedLock
                                                                                                                         getStaticOrderedLock(
                                                                                                                         ){
                            
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70705 =
                              au.edu.anu.mm.MortonDist.MortonSubregion.MortonSubregionIterator.X10$class_lock_id1;
                            
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.util.concurrent.OrderedLock t70706 =
                              x10.util.concurrent.OrderedLock.getLock((int)(t70705));
                            
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70706;
                        }
                        
                        
//#line 102 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public int
                          index;
                        
                        
//#line 104 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
// creation method for java code
                        public static au.edu.anu.mm.MortonDist.MortonSubregion.MortonSubregionIterator $make(final au.edu.anu.mm.MortonDist.MortonSubregion out$,
                                                                                                             final au.edu.anu.mm.MortonDist.MortonSubregion r){return new au.edu.anu.mm.MortonDist.MortonSubregion.MortonSubregionIterator((java.lang.System[]) null).$init(out$,r);}
                        
                        // constructor for non-virtual call
                        final public au.edu.anu.mm.MortonDist.MortonSubregion.MortonSubregionIterator au$edu$anu$mm$MortonDist$MortonSubregion$MortonSubregionIterator$$init$S(final au.edu.anu.mm.MortonDist.MortonSubregion out$,
                                                                                                                                                                               final au.edu.anu.mm.MortonDist.MortonSubregion r) { {
                                                                                                                                                                                                                                          
//#line 104 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"

                                                                                                                                                                                                                                          
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this.out$ = out$;
                                                                                                                                                                                                                                          
//#line 104 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"

                                                                                                                                                                                                                                          
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist.MortonSubregion.MortonSubregionIterator this7042371002 =
                                                                                                                                                                                                                                            this;
                                                                                                                                                                                                                                          
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this7042371002.X10$object_lock_id0 = -1;
                                                                                                                                                                                                                                          
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this7042371002.index = 0;
                                                                                                                                                                                                                                          
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70707 =
                                                                                                                                                                                                                                            r.
                                                                                                                                                                                                                                              start;
                                                                                                                                                                                                                                          
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70708 =
                                                                                                                                                                                                                                            ((t70707) - (((int)(1))));
                                                                                                                                                                                                                                          
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this.index = t70708;
                                                                                                                                                                                                                                      }
                                                                                                                                                                                                                                      return this;
                                                                                                                                                                                                                                      }
                        
                        // constructor
                        public au.edu.anu.mm.MortonDist.MortonSubregion.MortonSubregionIterator $init(final au.edu.anu.mm.MortonDist.MortonSubregion out$,
                                                                                                      final au.edu.anu.mm.MortonDist.MortonSubregion r){return au$edu$anu$mm$MortonDist$MortonSubregion$MortonSubregionIterator$$init$S(out$,r);}
                        
                        
                        
//#line 104 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
// creation method for java code
                        public static au.edu.anu.mm.MortonDist.MortonSubregion.MortonSubregionIterator $make(final au.edu.anu.mm.MortonDist.MortonSubregion out$,
                                                                                                             final au.edu.anu.mm.MortonDist.MortonSubregion r,
                                                                                                             final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.mm.MortonDist.MortonSubregion.MortonSubregionIterator((java.lang.System[]) null).$init(out$,r,paramLock);}
                        
                        // constructor for non-virtual call
                        final public au.edu.anu.mm.MortonDist.MortonSubregion.MortonSubregionIterator au$edu$anu$mm$MortonDist$MortonSubregion$MortonSubregionIterator$$init$S(final au.edu.anu.mm.MortonDist.MortonSubregion out$,
                                                                                                                                                                               final au.edu.anu.mm.MortonDist.MortonSubregion r,
                                                                                                                                                                               final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                                                                                                                         
//#line 104 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"

                                                                                                                                                                                                                                         
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this.out$ = out$;
                                                                                                                                                                                                                                         
//#line 104 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"

                                                                                                                                                                                                                                         
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist.MortonSubregion.MortonSubregionIterator this7042671003 =
                                                                                                                                                                                                                                           this;
                                                                                                                                                                                                                                         
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this7042671003.X10$object_lock_id0 = -1;
                                                                                                                                                                                                                                         
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this7042671003.index = 0;
                                                                                                                                                                                                                                         
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70709 =
                                                                                                                                                                                                                                           r.
                                                                                                                                                                                                                                             start;
                                                                                                                                                                                                                                         
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70710 =
                                                                                                                                                                                                                                           ((t70709) - (((int)(1))));
                                                                                                                                                                                                                                         
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this.index = t70710;
                                                                                                                                                                                                                                         
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70711 =
                                                                                                                                                                                                                                           paramLock.getIndex();
                                                                                                                                                                                                                                         
//#line 104 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this.X10$object_lock_id0 = ((int)(t70711));
                                                                                                                                                                                                                                     }
                                                                                                                                                                                                                                     return this;
                                                                                                                                                                                                                                     }
                        
                        // constructor
                        public au.edu.anu.mm.MortonDist.MortonSubregion.MortonSubregionIterator $init(final au.edu.anu.mm.MortonDist.MortonSubregion out$,
                                                                                                      final au.edu.anu.mm.MortonDist.MortonSubregion r,
                                                                                                      final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$mm$MortonDist$MortonSubregion$MortonSubregionIterator$$init$S(out$,r,paramLock);}
                        
                        
                        
//#line 108 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final public boolean
                                                                                                                         hasNext$O(
                                                                                                                         ){
                            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70713 =
                              index;
                            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist.MortonSubregion t70712 =
                              this.
                                out$;
                            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70714 =
                              t70712.
                                end;
                            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t70715 =
                              ((t70713) < (((int)(t70714))));
                            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (t70715) {
                                
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return true;
                            } else {
                                
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return false;
                            }
                        }
                        
                        
//#line 113 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final public x10.array.Point
                                                                                                                         next(
                                                                                                                         ){
                            
//#line 114 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist.MortonSubregion t70716 =
                              this.
                                out$;
                            
//#line 114 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist t70719 =
                              t70716.
                                out$;
                            
//#line 114 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist.MortonSubregion.MortonSubregionIterator x70429 =
                              ((au.edu.anu.mm.MortonDist.MortonSubregion.MortonSubregionIterator)(this));
                            
//#line 114 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70717 =
                              x70429.
                                index;
                            
//#line 114 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70718 =
                              ((t70717) + (((int)(1))));
                            
//#line 114 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70720 =
                              x70429.index = t70718;
                            
//#line 114 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Point t70721 =
                              ((x10.array.Point)(t70719.getPoint((int)(t70720))));
                            
//#line 114 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70721;
                        }
                        
                        
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final public au.edu.anu.mm.MortonDist.MortonSubregion.MortonSubregionIterator
                                                                                                                         au$edu$anu$mm$MortonDist$MortonSubregion$MortonSubregionIterator$$au$edu$anu$mm$MortonDist$MortonSubregion$MortonSubregionIterator$this(
                                                                                                                         ){
                            
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return au.edu.anu.mm.MortonDist.MortonSubregion.MortonSubregionIterator.this;
                        }
                        
                        
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final public au.edu.anu.mm.MortonDist.MortonSubregion
                                                                                                                         au$edu$anu$mm$MortonDist$MortonSubregion$MortonSubregionIterator$$au$edu$anu$mm$MortonDist$MortonSubregion$this(
                                                                                                                         ){
                            
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist.MortonSubregion t70722 =
                              this.
                                out$;
                            
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70722;
                        }
                        
                        
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final public au.edu.anu.mm.MortonDist
                                                                                                                         au$edu$anu$mm$MortonDist$MortonSubregion$MortonSubregionIterator$$au$edu$anu$mm$MortonDist$this(
                                                                                                                         ){
                            
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist.MortonSubregion t70723 =
                              this.
                                out$;
                            
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist t70724 =
                              t70723.
                                out$;
                            
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70724;
                        }
                        
                        
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final private void
                                                                                                                         __fieldInitializers47117(
                                                                                                                         ){
                            
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this.X10$object_lock_id0 = -1;
                            
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this.index = 0;
                        }
                        
                        final public static void
                          __fieldInitializers47117$P(
                          final au.edu.anu.mm.MortonDist.MortonSubregion.MortonSubregionIterator MortonSubregionIterator){
                            MortonSubregionIterator.__fieldInitializers47117();
                        }
                    
                }
                
                
                
//#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public java.lang.String
                                                                                                                 toString(
                                                                                                                 ){
                    
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70725 =
                      start;
                    
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final java.lang.String t70726 =
                      (("Z[") + ((x10.core.Int.$box(t70725))));
                    
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final java.lang.String t70727 =
                      ((t70726) + (".."));
                    
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70728 =
                      end;
                    
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final java.lang.String t70729 =
                      ((t70727) + ((x10.core.Int.$box(t70728))));
                    
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final java.lang.String t70730 =
                      ((t70729) + ("]"));
                    
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70730;
                }
                
                
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final public au.edu.anu.mm.MortonDist.MortonSubregion
                                                                                                                au$edu$anu$mm$MortonDist$MortonSubregion$$au$edu$anu$mm$MortonDist$MortonSubregion$this(
                                                                                                                ){
                    
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return au.edu.anu.mm.MortonDist.MortonSubregion.this;
                }
                
                
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final public au.edu.anu.mm.MortonDist
                                                                                                                au$edu$anu$mm$MortonDist$MortonSubregion$$au$edu$anu$mm$MortonDist$this(
                                                                                                                ){
                    
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist t70731 =
                      this.
                        out$;
                    
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70731;
                }
                
                
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final private void
                                                                                                                __fieldInitializers47118(
                                                                                                                ){
                    
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this.X10$object_lock_id0 = -1;
                }
                
                final public static void
                  __fieldInitializers47118$P(
                  final au.edu.anu.mm.MortonDist.MortonSubregion MortonSubregion){
                    MortonSubregion.__fieldInitializers47118();
                }
                
                public static class $Closure$72
                extends x10.core.Ref
                  implements x10.core.fun.Fun_0_1,
                              x10.x10rt.X10JavaSerializable 
                {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$72.class);
                    
                    public static final x10.rtt.RuntimeType<$Closure$72> $RTT = new x10.rtt.StaticFunType<$Closure$72>(
                    /* base class */$Closure$72.class
                    , /* parents */ new x10.rtt.Type[] {new x10.rtt.ParameterizedType(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$72 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        $Closure$72 $_obj = new $Closure$72((java.lang.System[]) null);
                        $deserializer.record_reference($_obj);
                        return $_deserialize_body($_obj, $deserializer);
                        
                    }
                    
                    public short $_get_serialization_id() {
                    
                         return $_serialization_id;
                        
                    }
                    
                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                    
                        
                    }
                    
                    // constructor just for allocation
                    public $Closure$72(final java.lang.System[] $dummy) { 
                    super($dummy);
                    }
                    // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
                    public java.lang.Object
                      $apply(final java.lang.Object a1,final x10.rtt.Type t1){return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));}
                    
                        
                        public int
                          $apply$O(
                          final int i){
                            
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return 0;
                        }
                        
                        // creation method for java code
                        public static au.edu.anu.mm.MortonDist.MortonSubregion.$Closure$72 $make(){return new $Closure$72();}
                        public $Closure$72() { {
                                                      
                                                  }}
                        
                    }
                    
                public static class $Closure$73
                extends x10.core.Ref
                  implements x10.core.fun.Fun_0_1,
                              x10.x10rt.X10JavaSerializable 
                {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$73.class);
                    
                    public static final x10.rtt.RuntimeType<$Closure$73> $RTT = new x10.rtt.StaticFunType<$Closure$73>(
                    /* base class */$Closure$73.class
                    , /* parents */ new x10.rtt.Type[] {new x10.rtt.ParameterizedType(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$73 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        au.edu.anu.mm.MortonDist.MortonSubregion out$$ = (au.edu.anu.mm.MortonDist.MortonSubregion) $deserializer.readRef();
                        $_obj.out$$ = out$$;
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        $Closure$73 $_obj = new $Closure$73((java.lang.System[]) null);
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
                    public $Closure$73(final java.lang.System[] $dummy) { 
                    super($dummy);
                    }
                    // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
                    public java.lang.Object
                      $apply(final java.lang.Object a1,final x10.rtt.Type t1){return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));}
                    
                        
                        public int
                          $apply$O(
                          final int i){
                            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist t70655 =
                              this.
                                out$$.
                                out$;
                            
//#line 376 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int i70160 =
                              t70655.
                                dimDigits;
                            
//#line 377 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int t70656 =
                              ((1) << (((int)(i70160))));
                            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70656;
                        }
                        
                        public au.edu.anu.mm.MortonDist.MortonSubregion
                          out$$;
                        
                        // creation method for java code
                        public static au.edu.anu.mm.MortonDist.MortonSubregion.$Closure$73 $make(final au.edu.anu.mm.MortonDist.MortonSubregion out$$){return new $Closure$73(out$$);}
                        public $Closure$73(final au.edu.anu.mm.MortonDist.MortonSubregion out$$) { {
                                                                                                          this.out$$ = out$$;
                                                                                                      }}
                        
                    }
                    
                
                }
                
            
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public static au.edu.anu.mm.MortonDist
                                                                                                             makeMorton(
                                                                                                             final x10.array.Region r){
                
//#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist alloc47915 =
                  ((au.edu.anu.mm.MortonDist)(new au.edu.anu.mm.MortonDist((java.lang.System[]) null)));
                
//#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.PlaceGroup.WorldPlaceGroup t7073271004 =
                  ((x10.array.PlaceGroup.WorldPlaceGroup)(x10.array.PlaceGroup.getInitialized$WORLD()));
                
//#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.util.concurrent.OrderedLock t7073371005 =
                  x10.util.concurrent.OrderedLock.createNewLock();
                
//#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
alloc47915.$init(((x10.array.Region)(r)),
                                                                                                                                ((x10.array.PlaceGroup)(t7073271004)),
                                                                                                                                t7073371005);
                
//#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return alloc47915;
            }
            
            
//#line 127 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public static au.edu.anu.mm.MortonDist
                                                                                                             makeMorton(
                                                                                                             final x10.array.Region r,
                                                                                                             final x10.array.PlaceGroup pg){
                
//#line 128 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist alloc47916 =
                  ((au.edu.anu.mm.MortonDist)(new au.edu.anu.mm.MortonDist((java.lang.System[]) null)));
                
//#line 128 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.util.concurrent.OrderedLock t7073471006 =
                  x10.util.concurrent.OrderedLock.createNewLock();
                
//#line 128 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
alloc47916.$init(((x10.array.Region)(r)),
                                                                                                                                ((x10.array.PlaceGroup)(pg)),
                                                                                                                                t7073471006);
                
//#line 128 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return alloc47916;
            }
            
            
//#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
// creation method for java code
            public static au.edu.anu.mm.MortonDist $make(final x10.array.Region r,
                                                         final x10.array.PlaceGroup pg){return new au.edu.anu.mm.MortonDist((java.lang.System[]) null).$init(r,pg);}
            
            // constructor for non-virtual call
            final public au.edu.anu.mm.MortonDist au$edu$anu$mm$MortonDist$$init$S(final x10.array.Region r,
                                                                                   final x10.array.PlaceGroup pg) { {
                                                                                                                           
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Dist this70432 =
                                                                                                                             ((x10.array.Dist)(this));
                                                                                                                           
//#line 668 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
final x10.array.Region region70431 =
                                                                                                                             ((x10.array.Region)(r));
                                                                                                                           
//#line 668 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
this70432.x10$lang$Object$$init$S();
                                                                                                                           
//#line 669 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
this70432.region = region70431;
                                                                                                                           
//#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"

                                                                                                                           
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist this7043471007 =
                                                                                                                             this;
                                                                                                                           
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this7043471007.X10$object_lock_id0 = -1;
                                                                                                                           
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this7043471007.regionForHere = null;
                                                                                                                           
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70735 =
                                                                                                                             r.size$O();
                                                                                                                           
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final double t70736 =
                                                                                                                             ((double)(int)(((int)(t70735))));
                                                                                                                           
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final double t70737 =
                                                                                                                             java.lang.Math.cbrt(((double)(t70736)));
                                                                                                                           
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70738 =
                                                                                                                             ((int)(double)(((double)(t70737))));
                                                                                                                           
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70739 =
                                                                                                                             x10.lang.Math.log2$O((int)(t70738));
                                                                                                                           
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70740 =
                                                                                                                             t70739;
                                                                                                                           
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this.dimDigits = t70740;
                                                                                                                           
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this.pg = ((x10.array.PlaceGroup)(pg));
                                                                                                                       }
                                                                                                                       return this;
                                                                                                                       }
            
            // constructor
            public au.edu.anu.mm.MortonDist $init(final x10.array.Region r,
                                                  final x10.array.PlaceGroup pg){return au$edu$anu$mm$MortonDist$$init$S(r,pg);}
            
            
            
//#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
// creation method for java code
            public static au.edu.anu.mm.MortonDist $make(final x10.array.Region r,
                                                         final x10.array.PlaceGroup pg,
                                                         final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.mm.MortonDist((java.lang.System[]) null).$init(r,pg,paramLock);}
            
            // constructor for non-virtual call
            final public au.edu.anu.mm.MortonDist au$edu$anu$mm$MortonDist$$init$S(final x10.array.Region r,
                                                                                   final x10.array.PlaceGroup pg,
                                                                                   final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                             
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Dist this70438 =
                                                                                                                                               ((x10.array.Dist)(this));
                                                                                                                                             
//#line 668 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
final x10.array.Region region70437 =
                                                                                                                                               ((x10.array.Region)(r));
                                                                                                                                             
//#line 668 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
this70438.x10$lang$Object$$init$S();
                                                                                                                                             
//#line 669 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
this70438.region = region70437;
                                                                                                                                             
//#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"

                                                                                                                                             
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist this7044071008 =
                                                                                                                                               this;
                                                                                                                                             
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this7044071008.X10$object_lock_id0 = -1;
                                                                                                                                             
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this7044071008.regionForHere = null;
                                                                                                                                             
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70741 =
                                                                                                                                               r.size$O();
                                                                                                                                             
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final double t70742 =
                                                                                                                                               ((double)(int)(((int)(t70741))));
                                                                                                                                             
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final double t70743 =
                                                                                                                                               java.lang.Math.cbrt(((double)(t70742)));
                                                                                                                                             
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70744 =
                                                                                                                                               ((int)(double)(((double)(t70743))));
                                                                                                                                             
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70745 =
                                                                                                                                               x10.lang.Math.log2$O((int)(t70744));
                                                                                                                                             
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70746 =
                                                                                                                                               t70745;
                                                                                                                                             
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this.dimDigits = t70746;
                                                                                                                                             
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this.pg = ((x10.array.PlaceGroup)(pg));
                                                                                                                                             
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70747 =
                                                                                                                                               paramLock.getIndex();
                                                                                                                                             
//#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this.X10$object_lock_id0 = ((int)(t70747));
                                                                                                                                         }
                                                                                                                                         return this;
                                                                                                                                         }
            
            // constructor
            public au.edu.anu.mm.MortonDist $init(final x10.array.Region r,
                                                  final x10.array.PlaceGroup pg,
                                                  final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$mm$MortonDist$$init$S(r,pg,paramLock);}
            
            
            
//#line 137 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public x10.array.PlaceGroup
                                                                                                             places(
                                                                                                             ){
                
//#line 137 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.PlaceGroup t70748 =
                  ((x10.array.PlaceGroup)(pg));
                
//#line 137 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70748;
            }
            
            
//#line 139 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public int
                                                                                                             numPlaces$O(
                                                                                                             ){
                
//#line 139 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.PlaceGroup t70749 =
                  ((x10.array.PlaceGroup)(pg));
                
//#line 139 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70750 =
                  t70749.numPlaces$O();
                
//#line 139 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70750;
            }
            
            
//#line 141 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public x10.array.Region
                                                                                                             get(
                                                                                                             final x10.lang.Place p){
                
//#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t70756 =
                  x10.rtt.Equality.equalsequals((p),(x10.lang.Runtime.home()));
                
//#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (t70756) {
                    
//#line 143 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Region t70751 =
                      ((x10.array.Region)(regionForHere));
                    
//#line 143 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t70753 =
                      ((t70751) == (null));
                    
//#line 143 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (t70753) {
                        
//#line 144 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Region t70752 =
                          ((x10.array.Region)(this.mortonRegionForPlace(((x10.lang.Place)(x10.lang.Runtime.home())))));
                        
//#line 144 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this.regionForHere = ((x10.array.Region)(t70752));
                    }
                    
//#line 146 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Region t70754 =
                      ((x10.array.Region)(regionForHere));
                    
//#line 146 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70754;
                } else {
                    
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Region t70755 =
                      ((x10.array.Region)(this.mortonRegionForPlace(((x10.lang.Place)(p)))));
                    
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70755;
                }
            }
            
            
//#line 152 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
private x10.array.Region
                                                                                                             mortonRegionForPlace(
                                                                                                             final x10.lang.Place p){
                
//#line 153 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist.MortonSubregion alloc47917 =
                  ((au.edu.anu.mm.MortonDist.MortonSubregion)(new au.edu.anu.mm.MortonDist.MortonSubregion((java.lang.System[]) null)));
                
//#line 153 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7075771009 =
                  p.
                    id;
                
//#line 153 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7075971010 =
                  this.getPlaceStart$O((int)(t7075771009));
                
//#line 154 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7075871011 =
                  p.
                    id;
                
//#line 154 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7076071012 =
                  this.getPlaceEnd$O((int)(t7075871011));
                
//#line 153 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.util.concurrent.OrderedLock t7076171013 =
                  x10.util.concurrent.OrderedLock.createNewLock();
                
//#line 153 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
alloc47917.$init(this,
                                                                                                                                t7075971010,
                                                                                                                                t7076071012,
                                                                                                                                t7076171013);
                
//#line 153 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return alloc47917;
            }
            
            public static x10.array.Region
              mortonRegionForPlace$P(
              final x10.lang.Place p,
              final au.edu.anu.mm.MortonDist MortonDist){
                return MortonDist.mortonRegionForPlace(((x10.lang.Place)(p)));
            }
            
            
//#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public x10.lang.Sequence<x10.array.Region>
                                                                                                             regions(
                                                                                                             ){
                
//#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Array<x10.array.Region> alloc47918 =
                  ((x10.array.Array)(new x10.array.Array<x10.array.Region>((java.lang.System[]) null, x10.array.Region.$RTT)));
                
//#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.PlaceGroup t70762 =
                  ((x10.array.PlaceGroup)(pg));
                
//#line 271 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int size70557 =
                  t70762.numPlaces$O();
                
//#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.array.Region> t70766 =
                  ((x10.core.fun.Fun_0_1)(new au.edu.anu.mm.MortonDist.$Closure$74(this,
                                                                                   pg)));
                
//#line 271 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.array.Region> init70558 =
                  ((x10.core.fun.Fun_0_1)(((x10.core.fun.Fun_0_1<x10.core.Int,x10.array.Region>)(x10.core.fun.Fun_0_1)
                                            t70766)));
                
//#line 271 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc47918.x10$lang$Object$$init$S();
                
//#line 273 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectRegion1D alloc199727056171041 =
                  ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null)));
                
//#line 273 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t7076771021 =
                  ((size70557) - (((int)(1))));
                
//#line 273 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc199727056171041.$init(((int)(0)),
                                                                                                                                           t7076771021);
                
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region __desugarer__var__6__705607057071042 =
                  ((x10.array.Region)(((x10.array.Region)
                                        alloc199727056171041)));
                
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Region ret7057171043 =
                   null;
                
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t7076871022 =
                  __desugarer__var__6__705607057071042.
                    zeroBased;
                
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t7077071023 =
                  ((boolean) t7076871022) ==
                ((boolean) true);
                
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t7077071023) {
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t7076971024 =
                      __desugarer__var__6__705607057071042.
                        rail;
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t7077071023 = ((boolean) t7076971024) ==
                    ((boolean) true);
                }
                
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t7077271025 =
                  t7077071023;
                
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t7077271025) {
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t7077171026 =
                      __desugarer__var__6__705607057071042.
                        rank;
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t7077271025 = ((int) t7077171026) ==
                    ((int) 1);
                }
                
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t7077471027 =
                  t7077271025;
                
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t7077471027) {
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t7077371028 =
                      __desugarer__var__6__705607057071042.
                        rect;
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t7077471027 = ((boolean) t7077371028) ==
                    ((boolean) true);
                }
                
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t7077571029 =
                  t7077471027;
                
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t7077571029) {
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t7077571029 = ((__desugarer__var__6__705607057071042) != (null));
                }
                
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t7077671030 =
                  t7077571029;
                
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t7077971031 =
                  !(t7077671030);
                
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t7077971031) {
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t7077871032 =
                      true;
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t7077871032) {
                        
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t7077771033 =
                          new x10.lang.FailedDynamicCheckException("x10.array.Region{self.zeroBased==true, self.rail==true, self.rank==1, self.rect==true, self!=null}");
                        
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
throw t7077771033;
                    }
                }
                
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret7057171043 = ((x10.array.Region)(__desugarer__var__6__705607057071042));
                
//#line 273 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region myReg7055971044 =
                  ((x10.array.Region)(ret7057171043));
                
//#line 274 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc47918.region = ((x10.array.Region)(myReg7055971044));
                
//#line 274 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc47918.rank = 1;
                
//#line 274 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc47918.rect = true;
                
//#line 274 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc47918.zeroBased = true;
                
//#line 274 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc47918.rail = true;
                
//#line 274 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc47918.size = size70557;
                
//#line 276 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout alloc199737056271045 =
                  new x10.array.RectLayout((java.lang.System[]) null);
                
//#line 97 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int _max07057471046 =
                  ((size70557) - (((int)(1))));
                
//#line 98 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199737056271045.rank = 1;
                
//#line 99 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199737056271045.min0 = 0;
                
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t7078071034 =
                  ((_max07057471046) - (((int)(0))));
                
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t7078171035 =
                  ((t7078071034) + (((int)(1))));
                
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199737056271045.delta0 = t7078171035;
                
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t7078271036 =
                  alloc199737056271045.
                    delta0;
                
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final boolean t7078371037 =
                  ((t7078271036) > (((int)(0))));
                
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int t7078471038 =
                   0;
                
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
if (t7078371037) {
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t7078471038 = alloc199737056271045.
                                                                                                                                          delta0;
                } else {
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t7078471038 = 0;
                }
                
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t7078571039 =
                  t7078471038;
                
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199737056271045.size = t7078571039;
                
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199737056271045.min1 = 0;
                
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199737056271045.delta1 = 0;
                
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199737056271045.min2 = 0;
                
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199737056271045.delta2 = 0;
                
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199737056271045.min3 = 0;
                
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199737056271045.delta3 = 0;
                
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199737056271045.min = null;
                
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199737056271045.delta = null;
                
//#line 276 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc47918.layout = ((x10.array.RectLayout)(alloc199737056271045));
                
//#line 277 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this7057671047 =
                  ((x10.array.RectLayout)(((x10.array.Array<x10.array.Region>)alloc47918).
                                            layout));
                
//#line 277 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int n7056371048 =
                  this7057671047.
                    size;
                
//#line 278 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Region> r7056471049 =
                  x10.core.IndexedMemoryChunk.<x10.array.Region>allocate(x10.array.Region.$RTT, ((int)(n7056371048)), false);
                
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i19990max199927056671040 =
                  ((size70557) - (((int)(1))));
                
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int i199907056771018 =
                  0;
                {
                    
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region[] r7056471049$value71177 =
                      ((x10.array.Region[])r7056471049.value);
                    
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
for (;
                                                                                                                         true;
                                                                                                                         ) {
                        
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t7078771019 =
                          i199907056771018;
                        
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t7079171020 =
                          ((t7078771019) <= (((int)(i19990max199927056671040))));
                        
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (!(t7079171020)) {
                            
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break;
                        }
                        
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i7056871015 =
                          i199907056771018;
                        
//#line 280 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t7079071014 =
                          ((x10.array.Region)(((x10.array.Region)
                                                ((x10.core.fun.Fun_0_1<x10.core.Int,x10.array.Region>)init70558).$apply(x10.core.Int.$box(i7056871015),x10.rtt.Types.INT))));
                        
//#line 280 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
r7056471049$value71177[i7056871015]=t7079071014;
                        
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t7078871016 =
                          i199907056771018;
                        
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t7078971017 =
                          ((t7078871016) + (((int)(1))));
                        
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
i199907056771018 = t7078971017;
                    }
                }
                
//#line 282 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc47918.raw = ((x10.core.IndexedMemoryChunk)(r7056471049));
                
//#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.lang.Sequence<x10.array.Region> t70792 =
                  ((x10.lang.Sequence<x10.array.Region>)
                    ((x10.array.Array<x10.array.Region>)alloc47918).sequence());
                
//#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70792;
            }
            
            
//#line 161 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public x10.array.Dist
                                                                                                             restriction(
                                                                                                             final x10.array.Region r){
                
//#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.lang.UnsupportedOperationException t70793 =
                  ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(((java.lang.String)("restriction(r:Region(rank))")))));
                
//#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
throw t70793;
            }
            
            
//#line 165 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public x10.array.Dist
                                                                                                             restriction(
                                                                                                             final x10.lang.Place p){
                
//#line 64 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
final x10.array.Region r70577 =
                  ((x10.array.Region)(this.get(((x10.lang.Place)(p)))));
                
//#line 65 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
final x10.array.ConstantDist alloc2891970578 =
                  ((x10.array.ConstantDist)(new x10.array.ConstantDist((java.lang.System[]) null)));
                
//#line 27 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10"
final x10.array.Region r70579 =
                  ((x10.array.Region)(r70577));
                
//#line 27 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10"
final x10.lang.Place p70580 =
                  ((x10.lang.Place)(x10.lang.Runtime.home()));
                
//#line 668 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
final x10.array.Region region7058271050 =
                  ((x10.array.Region)(r70579));
                
//#line 668 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
alloc2891970578.x10$lang$Object$$init$S();
                
//#line 669 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
alloc2891970578.region = region7058271050;
                
//#line 29 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10"
alloc2891970578.onePlace = p70580;
                
//#line 65 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
final x10.array.Dist t70794 =
                  ((x10.array.Dist)(((x10.array.Dist)
                                      alloc2891970578)));
                
//#line 166 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70794;
            }
            
            
//#line 169 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public boolean
                                                                                                             equals(
                                                                                                             final java.lang.Object thatObj){
                
//#line 170 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t70795 =
                  au.edu.anu.mm.MortonDist.$RTT.instanceOf(thatObj);
                
//#line 170 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t70796 =
                  !(t70795);
                
//#line 170 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (t70796) {
                    
//#line 170 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return false;
                }
                
//#line 171 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist that =
                  ((au.edu.anu.mm.MortonDist)(x10.rtt.Types.<au.edu.anu.mm.MortonDist> cast(thatObj,au.edu.anu.mm.MortonDist.$RTT)));
                
//#line 172 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Region t70797 =
                  ((x10.array.Region)(this.
                                        region));
                
//#line 172 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70799 =
                  t70797.size$O();
                
//#line 172 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Region t70798 =
                  ((x10.array.Region)(that.
                                        region));
                
//#line 172 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70800 =
                  t70798.size$O();
                
//#line 172 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t70801 =
                  ((int) t70799) ==
                ((int) t70800);
                
//#line 172 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70801;
            }
            
            
//#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public int
                                                                                                             getMortonIndex$O(
                                                                                                             final x10.array.Point p){
                
//#line 184 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70802 =
                  p.
                    rank;
                
//#line 184 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t70804 =
                  ((int) t70802) !=
                ((int) 3);
                
//#line 184 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (t70804) {
                    
//#line 184 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.lang.UnsupportedOperationException t70803 =
                      ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(((java.lang.String)("getMortonIndex(p{self.rank!=3})")))));
                    
//#line 184 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
throw t70803;
                }
                
//#line 186 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
int index =
                  0;
                
//#line 187 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70805 =
                  dimDigits;
                
//#line 376 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int i70584 =
                  ((t70805) - (((int)(1))));
                
//#line 187 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
int digitMask =
                  ((1) << (((int)(i70584))));
                
//#line 189 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
int digit71071 =
                  dimDigits;
                
//#line 189 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
for (;
                                                                                                                    true;
                                                                                                                    ) {
                    
//#line 189 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7080771072 =
                      digit71071;
                    
//#line 189 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t7082771073 =
                      ((t7080771072) > (((int)(0))));
                    
//#line 189 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (!(t7082771073)) {
                        
//#line 189 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
break;
                    }
                    
//#line 190 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
int dim71064 =
                      0;
                    
//#line 190 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
for (;
                                                                                                                        true;
                                                                                                                        ) {
                        
//#line 190 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7081171065 =
                          dim71064;
                        
//#line 190 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t7082471066 =
                          ((t7081171065) < (((int)(3))));
                        
//#line 190 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (!(t7082471066)) {
                            
//#line 190 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
break;
                        }
                        
//#line 191 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7081571051 =
                          digitMask;
                        
//#line 191 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7081471052 =
                          dim71064;
                        
//#line 191 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7081671053 =
                          p.$apply$O((int)(t7081471052));
                        
//#line 191 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int thisDim71054 =
                          ((t7081571051) & (((int)(t7081671053))));
                        
//#line 192 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7082171055 =
                          index;
                        
//#line 192 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7081771056 =
                          digit71071;
                        
//#line 192 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7081871057 =
                          ((t7081771056) * (((int)(2))));
                        
//#line 192 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7081971058 =
                          dim71064;
                        
//#line 192 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7082071059 =
                          ((t7081871057) - (((int)(t7081971058))));
                        
//#line 192 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7082271060 =
                          ((thisDim71054) << (((int)(t7082071059))));
                        
//#line 192 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7082371061 =
                          ((t7082171055) | (((int)(t7082271060))));
                        
//#line 192 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
index = t7082371061;
                        
//#line 190 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7081271062 =
                          dim71064;
                        
//#line 190 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7081371063 =
                          ((t7081271062) + (((int)(1))));
                        
//#line 190 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
dim71064 = t7081371063;
                    }
                    
//#line 194 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7082571067 =
                      digitMask;
                    
//#line 194 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7082671068 =
                      ((t7082571067) >> (((int)(1))));
                    
//#line 194 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
digitMask = t7082671068;
                    
//#line 189 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7080871069 =
                      digit71071;
                    
//#line 189 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7080971070 =
                      ((t7080871069) - (((int)(1))));
                    
//#line 189 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
digit71071 = t7080971070;
                }
                
//#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70828 =
                  index;
                
//#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70828;
            }
            
            
//#line 209 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public int
                                                                                                             getMortonIndex$O(
                                                                                                             final int i0,
                                                                                                             final int i1,
                                                                                                             final int i2){
                
//#line 210 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
int index =
                  0;
                
//#line 211 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70829 =
                  dimDigits;
                
//#line 376 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int i70585 =
                  ((t70829) - (((int)(1))));
                
//#line 211 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
int digitMask =
                  ((1) << (((int)(i70585))));
                
//#line 212 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
int digit71101 =
                  dimDigits;
                
//#line 212 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
for (;
                                                                                                                    true;
                                                                                                                    ) {
                    
//#line 212 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7083171102 =
                      digit71101;
                    
//#line 212 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t7085671103 =
                      ((t7083171102) > (((int)(0))));
                    
//#line 212 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (!(t7085671103)) {
                        
//#line 212 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
break;
                    }
                    
//#line 213 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7083471074 =
                      digitMask;
                    
//#line 213 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int dim071075 =
                      ((t7083471074) & (((int)(i0))));
                    
//#line 214 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7083771076 =
                      index;
                    
//#line 214 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7083571077 =
                      digit71101;
                    
//#line 214 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7083671078 =
                      ((t7083571077) * (((int)(2))));
                    
//#line 214 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7083871079 =
                      ((dim071075) << (((int)(t7083671078))));
                    
//#line 214 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7083971080 =
                      ((t7083771076) | (((int)(t7083871079))));
                    
//#line 214 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
index = t7083971080;
                    
//#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7084071081 =
                      digitMask;
                    
//#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int dim171082 =
                      ((t7084071081) & (((int)(i1))));
                    
//#line 216 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7084471083 =
                      index;
                    
//#line 216 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7084171084 =
                      digit71101;
                    
//#line 216 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7084271085 =
                      ((t7084171084) * (((int)(2))));
                    
//#line 216 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7084371086 =
                      ((t7084271085) - (((int)(1))));
                    
//#line 216 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7084571087 =
                      ((dim171082) << (((int)(t7084371086))));
                    
//#line 216 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7084671088 =
                      ((t7084471083) | (((int)(t7084571087))));
                    
//#line 216 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
index = t7084671088;
                    
//#line 217 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7084771089 =
                      digitMask;
                    
//#line 217 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int dim271090 =
                      ((t7084771089) & (((int)(i2))));
                    
//#line 218 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7085171091 =
                      index;
                    
//#line 218 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7084871092 =
                      digit71101;
                    
//#line 218 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7084971093 =
                      ((t7084871092) * (((int)(2))));
                    
//#line 218 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7085071094 =
                      ((t7084971093) - (((int)(2))));
                    
//#line 218 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7085271095 =
                      ((dim271090) << (((int)(t7085071094))));
                    
//#line 218 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7085371096 =
                      ((t7085171091) | (((int)(t7085271095))));
                    
//#line 218 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
index = t7085371096;
                    
//#line 219 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7085471097 =
                      digitMask;
                    
//#line 219 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7085571098 =
                      ((t7085471097) >> (((int)(1))));
                    
//#line 219 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
digitMask = t7085571098;
                    
//#line 212 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7083271099 =
                      digit71101;
                    
//#line 212 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7083371100 =
                      ((t7083271099) - (((int)(1))));
                    
//#line 212 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
digit71101 = t7083371100;
                }
                
//#line 221 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70857 =
                  index;
                
//#line 221 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70857;
            }
            
            
//#line 228 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public x10.array.Point
                                                                                                             getPoint(
                                                                                                             final int index){
                
//#line 230 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Array<x10.core.Int> p =
                  ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT)));
                
//#line 243 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
p.x10$lang$Object$$init$S();
                
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectRegion1D alloc199707058971149 =
                  ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null)));
                
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t7085871104 =
                  ((3) - (((int)(1))));
                
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc199707058971149.$init(((int)(0)),
                                                                                                                                           t7085871104);
                
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region __desugarer__var__5__705887059371150 =
                  ((x10.array.Region)(((x10.array.Region)
                                        alloc199707058971149)));
                
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Region ret7059471151 =
                   null;
                
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t7085971105 =
                  __desugarer__var__5__705887059371150.
                    rank;
                
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t7086171106 =
                  ((int) t7085971105) ==
                ((int) 1);
                
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t7086171106) {
                    
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t7086071107 =
                      __desugarer__var__5__705887059371150.
                        zeroBased;
                    
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t7086171106 = ((boolean) t7086071107) ==
                    ((boolean) true);
                }
                
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t7086371108 =
                  t7086171106;
                
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t7086371108) {
                    
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t7086271109 =
                      __desugarer__var__5__705887059371150.
                        rect;
                    
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t7086371108 = ((boolean) t7086271109) ==
                    ((boolean) true);
                }
                
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t7086571110 =
                  t7086371108;
                
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t7086571110) {
                    
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t7086471111 =
                      __desugarer__var__5__705887059371150.
                        rail;
                    
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t7086571110 = ((boolean) t7086471111) ==
                    ((boolean) true);
                }
                
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t7086671112 =
                  t7086571110;
                
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t7086671112) {
                    
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t7086671112 = ((__desugarer__var__5__705887059371150) != (null));
                }
                
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t7086771113 =
                  t7086671112;
                
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t7087071114 =
                  !(t7086771113);
                
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t7087071114) {
                    
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t7086971115 =
                      true;
                    
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t7086971115) {
                        
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t7086871116 =
                          new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==1, self.zeroBased==true, self.rect==true, self.rail==true, self!=null}");
                        
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
throw t7086871116;
                    }
                }
                
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret7059471151 = ((x10.array.Region)(__desugarer__var__5__705887059371150));
                
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region myReg7058771152 =
                  ((x10.array.Region)(ret7059471151));
                
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
p.region = ((x10.array.Region)(myReg7058771152));
                
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
p.rank = 1;
                
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
p.rect = true;
                
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
p.zeroBased = true;
                
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
p.rail = true;
                
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
p.size = 3;
                
//#line 249 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout alloc199717059071153 =
                  new x10.array.RectLayout((java.lang.System[]) null);
                
//#line 97 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int _max07059771154 =
                  ((3) - (((int)(1))));
                
//#line 98 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199717059071153.rank = 1;
                
//#line 99 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199717059071153.min0 = 0;
                
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t7087171117 =
                  ((_max07059771154) - (((int)(0))));
                
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t7087271118 =
                  ((t7087171117) + (((int)(1))));
                
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199717059071153.delta0 = t7087271118;
                
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t7087371119 =
                  alloc199717059071153.
                    delta0;
                
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final boolean t7087471120 =
                  ((t7087371119) > (((int)(0))));
                
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int t7087571121 =
                   0;
                
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
if (t7087471120) {
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t7087571121 = alloc199717059071153.
                                                                                                                                          delta0;
                } else {
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t7087571121 = 0;
                }
                
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t7087671122 =
                  t7087571121;
                
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199717059071153.size = t7087671122;
                
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199717059071153.min1 = 0;
                
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199717059071153.delta1 = 0;
                
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199717059071153.min2 = 0;
                
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199717059071153.delta2 = 0;
                
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199717059071153.min3 = 0;
                
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199717059071153.delta3 = 0;
                
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199717059071153.min = null;
                
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199717059071153.delta = null;
                
//#line 249 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
p.layout = ((x10.array.RectLayout)(alloc199717059071153));
                
//#line 250 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this7059971155 =
                  ((x10.array.RectLayout)(((x10.array.Array<x10.core.Int>)p).
                                            layout));
                
//#line 250 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int n7059171156 =
                  this7059971155.
                    size;
                
//#line 251 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t7087771157 =
                  ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<x10.core.Int>allocate(x10.rtt.Types.INT, ((int)(n7059171156)), true)));
                
//#line 251 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
p.raw = ((x10.core.IndexedMemoryChunk)(t7087771157));
                
//#line 231 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Region t70878 =
                  ((x10.array.Region)(region));
                
//#line 231 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70879 =
                  t70878.size$O();
                
//#line 231 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
int digitMask =
                  ((t70879) / (((int)(2))));
                
//#line 232 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
int digit71158 =
                  dimDigits;
                
//#line 232 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
for (;
                                                                                                                    true;
                                                                                                                    ) {
                    
//#line 232 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7088171159 =
                      digit71158;
                    
//#line 232 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t7090171160 =
                      ((t7088171159) > (((int)(0))));
                    
//#line 232 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (!(t7090171160)) {
                        
//#line 232 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
break;
                    }
                    
//#line 233 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
int dim71144 =
                      0;
                    
//#line 233 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
for (;
                                                                                                                        true;
                                                                                                                        ) {
                        
//#line 233 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7088571145 =
                          dim71144;
                        
//#line 233 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t7090071146 =
                          ((t7088571145) < (((int)(3))));
                        
//#line 233 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (!(t7090071146)) {
                            
//#line 233 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
break;
                        }
                        
//#line 234 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7088871124 =
                          digitMask;
                        
//#line 234 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int thisDim71125 =
                          ((t7088871124) & (((int)(index))));
                        
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i07060871126 =
                          dim71144;
                        
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i07060071127 =
                          dim71144;
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int ret7060171128 =
                           0;
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret7060271129: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t7088971130 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Int>)p).
                                                           raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t7089071131 =
                          ((int[])t7088971130.value)[i07060071127];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret7060171128 = t7089071131;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret7060271129;}
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t7089571132 =
                          ret7060171128;
                        
//#line 235 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7089171133 =
                          digit71158;
                        
//#line 235 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7089271134 =
                          ((t7089171133) * (((int)(2))));
                        
//#line 235 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7089371135 =
                          dim71144;
                        
//#line 235 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7089471136 =
                          ((t7089271134) - (((int)(t7089371135))));
                        
//#line 235 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7089671137 =
                          ((thisDim71125) >> (((int)(t7089471136))));
                        
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int v7060971138 =
                          ((t7089571132) | (((int)(t7089671137))));
                        
//#line 508 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int ret7061071139 =
                           0;
                        
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t7089771123 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Int>)p).
                                                           raw));
                        
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((int[])t7089771123.value)[i07060871126] = v7060971138;
                        
//#line 519 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret7061071139 = v7060971138;
                        
//#line 236 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7089871140 =
                          digitMask;
                        
//#line 236 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7089971141 =
                          ((t7089871140) >> (((int)(1))));
                        
//#line 236 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
digitMask = t7089971141;
                        
//#line 233 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7088671142 =
                          dim71144;
                        
//#line 233 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7088771143 =
                          ((t7088671142) + (((int)(1))));
                        
//#line 233 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
dim71144 = t7088771143;
                    }
                    
//#line 232 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7088271147 =
                      digit71158;
                    
//#line 232 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7088371148 =
                      ((t7088271147) - (((int)(1))));
                    
//#line 232 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
digit71158 = t7088371148;
                }
                
//#line 240 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Point t70902 =
                  ((x10.array.Point)(x10.array.Point.<x10.core.Int>make_0_$_x10$array$Point_T_$(x10.rtt.Types.INT, ((x10.array.Array)(p)))));
                
//#line 240 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70902;
            }
            
            
//#line 243 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public int
                                                                                                             getPlaceStart$O(
                                                                                                             final int placeId){
                
//#line 244 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Region t70903 =
                  ((x10.array.Region)(region));
                
//#line 244 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70904 =
                  t70903.size$O();
                
//#line 244 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70905 =
                  x10.lang.Place.getInitialized$MAX_PLACES();
                
//#line 244 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int blockSize =
                  ((t70904) / (((int)(t70905))));
                
//#line 245 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Region t70906 =
                  ((x10.array.Region)(region));
                
//#line 245 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70907 =
                  t70906.size$O();
                
//#line 245 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70908 =
                  x10.lang.Place.getInitialized$MAX_PLACES();
                
//#line 245 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int numLargerBlocks =
                  ((t70907) % (((int)(t70908))));
                
//#line 246 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t70915 =
                  ((placeId) < (((int)(numLargerBlocks))));
                
//#line 246 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (t70915) {
                    
//#line 247 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70909 =
                      ((blockSize) + (((int)(1))));
                    
//#line 247 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70910 =
                      ((placeId) * (((int)(t70909))));
                    
//#line 247 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70910;
                } else {
                    
//#line 249 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70911 =
                      ((blockSize) + (((int)(1))));
                    
//#line 249 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int firstPortion =
                      ((numLargerBlocks) * (((int)(t70911))));
                    
//#line 250 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70912 =
                      ((placeId) - (((int)(numLargerBlocks))));
                    
//#line 250 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70913 =
                      ((t70912) * (((int)(blockSize))));
                    
//#line 250 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70914 =
                      ((firstPortion) + (((int)(t70913))));
                    
//#line 250 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70914;
                }
            }
            
            
//#line 254 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public int
                                                                                                             getPlaceEnd$O(
                                                                                                             final int placeId){
                
//#line 255 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Region t70916 =
                  ((x10.array.Region)(region));
                
//#line 255 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70917 =
                  t70916.size$O();
                
//#line 255 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70918 =
                  x10.lang.Place.getInitialized$MAX_PLACES();
                
//#line 255 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int blockSize =
                  ((t70917) / (((int)(t70918))));
                
//#line 256 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Region t70919 =
                  ((x10.array.Region)(region));
                
//#line 256 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70920 =
                  t70919.size$O();
                
//#line 256 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70921 =
                  x10.lang.Place.getInitialized$MAX_PLACES();
                
//#line 256 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int numLargerBlocks =
                  ((t70920) % (((int)(t70921))));
                
//#line 257 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t70932 =
                  ((placeId) < (((int)(numLargerBlocks))));
                
//#line 257 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (t70932) {
                    
//#line 258 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70922 =
                      ((placeId) + (((int)(1))));
                    
//#line 258 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70923 =
                      ((blockSize) + (((int)(1))));
                    
//#line 258 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70924 =
                      ((t70922) * (((int)(t70923))));
                    
//#line 258 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70925 =
                      ((t70924) - (((int)(1))));
                    
//#line 258 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70925;
                } else {
                    
//#line 260 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70926 =
                      ((blockSize) + (((int)(1))));
                    
//#line 260 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int firstPortion =
                      ((numLargerBlocks) * (((int)(t70926))));
                    
//#line 261 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70927 =
                      ((placeId) - (((int)(numLargerBlocks))));
                    
//#line 261 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70928 =
                      ((t70927) + (((int)(1))));
                    
//#line 261 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70929 =
                      ((t70928) * (((int)(blockSize))));
                    
//#line 261 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70930 =
                      ((firstPortion) + (((int)(t70929))));
                    
//#line 261 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70931 =
                      ((t70930) - (((int)(1))));
                    
//#line 261 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70931;
                }
            }
            
            
//#line 265 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final private x10.lang.Place
                                                                                                             getPlaceForIndex(
                                                                                                             final int index){
                
//#line 266 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Region t70933 =
                  ((x10.array.Region)(region));
                
//#line 266 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70934 =
                  t70933.size$O();
                
//#line 266 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70935 =
                  x10.lang.Place.getInitialized$MAX_PLACES();
                
//#line 266 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int blockSize =
                  ((t70934) / (((int)(t70935))));
                
//#line 267 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Region t70936 =
                  ((x10.array.Region)(region));
                
//#line 267 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70937 =
                  t70936.size$O();
                
//#line 267 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70938 =
                  x10.lang.Place.getInitialized$MAX_PLACES();
                
//#line 267 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int numLargerBlocks =
                  ((t70937) % (((int)(t70938))));
                
//#line 268 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70939 =
                  ((blockSize) + (((int)(1))));
                
//#line 268 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int firstPart =
                  ((numLargerBlocks) * (((int)(t70939))));
                
//#line 269 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t70945 =
                  ((index) > (((int)(firstPart))));
                
//#line 269 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (t70945) {
                    
//#line 270 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70940 =
                      ((index) - (((int)(firstPart))));
                    
//#line 270 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70941 =
                      ((t70940) / (((int)(blockSize))));
                    
//#line 270 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70942 =
                      t70941;
                    
//#line 127 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Place.x10"
final int id70617 =
                      ((numLargerBlocks) + (((int)(t70942))));
                    
//#line 127 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Place.x10"
final x10.lang.Place alloc3235970618 =
                      new x10.lang.Place((java.lang.System[]) null);
                    
//#line 127 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Place.x10"
alloc3235970618.$init(((int)(id70617)));
                    
//#line 270 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return alloc3235970618;
                } else {
                    
//#line 272 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70943 =
                      ((blockSize) + (((int)(1))));
                    
//#line 272 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70944 =
                      ((index) / (((int)(t70943))));
                    
//#line 127 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Place.x10"
final int id70619 =
                      t70944;
                    
//#line 127 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Place.x10"
final x10.lang.Place alloc3235970620 =
                      new x10.lang.Place((java.lang.System[]) null);
                    
//#line 127 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Place.x10"
alloc3235970620.$init(((int)(id70619)));
                    
//#line 272 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return alloc3235970620;
                }
            }
            
            final public static x10.lang.Place
              getPlaceForIndex$P(
              final int index,
              final au.edu.anu.mm.MortonDist MortonDist){
                return MortonDist.getPlaceForIndex((int)(index));
            }
            
            
//#line 276 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public x10.lang.Place
                                                                                                             $apply(
                                                                                                             final x10.array.Point pt){
                
//#line 277 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Region t70946 =
                  ((x10.array.Region)(region));
                
//#line 277 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t70947 =
                  t70946.contains$O(((x10.array.Point)(pt)));
                
//#line 277 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t70948 =
                  !(t70947);
                
//#line 277 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (t70948) {
                    
//#line 277 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
x10.array.Dist.raiseBoundsError(((x10.array.Point)(pt)));
                }
                
//#line 278 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int index =
                  this.getMortonIndex$O(((x10.array.Point)(pt)));
                
//#line 279 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.lang.Place t70949 =
                  this.getPlaceForIndex((int)(index));
                
//#line 279 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70949;
            }
            
            
//#line 282 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public int
                                                                                                             offset$O(
                                                                                                             final x10.array.Point pt){
                
//#line 284 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Region t70950 =
                  ((x10.array.Region)(this.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
                
//#line 284 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int offset =
                  t70950.indexOf$O(((x10.array.Point)(pt)));
                
//#line 285 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t70954 =
                  ((int) offset) ==
                ((int) -1);
                
//#line 285 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (t70954) {
                    
//#line 286 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Region t70951 =
                      ((x10.array.Region)(region));
                    
//#line 286 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t70952 =
                      t70951.contains$O(((x10.array.Point)(pt)));
                    
//#line 286 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t70953 =
                      !(t70952);
                    
//#line 286 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (t70953) {
                        
//#line 286 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
x10.array.Dist.raiseBoundsError(((x10.array.Point)(pt)));
                    }
                    {
                        
//#line 287 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
x10.array.Dist.raisePlaceError(((x10.array.Point)(pt)));
                    }
                }
                
//#line 289 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return offset;
            }
            
            
//#line 292 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public int
                                                                                                             offset$O(
                                                                                                             final int i0,
                                                                                                             final int i1,
                                                                                                             final int i2){
                
//#line 294 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Region t70957 =
                  ((x10.array.Region)(this.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
                
//#line 294 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int offset =
                  t70957.indexOf$O((int)(i0),
                                   (int)(i1),
                                   (int)(i2));
                
//#line 295 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t70961 =
                  ((int) offset) ==
                ((int) -1);
                
//#line 295 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (t70961) {
                    
//#line 296 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Region t70958 =
                      ((x10.array.Region)(region));
                    
//#line 296 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t70959 =
                      t70958.contains$O((int)(i0),
                                        (int)(i1),
                                        (int)(i2));
                    
//#line 296 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t70960 =
                      !(t70959);
                    
//#line 296 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (t70960) {
                        
//#line 296 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
x10.array.Dist.raiseBoundsError((int)(i0),
                                                                                                                                                       (int)(i1),
                                                                                                                                                       (int)(i2));
                    }
                    {
                        
//#line 297 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
x10.array.Dist.raisePlaceError((int)(i0),
                                                                                                                                                      (int)(i1),
                                                                                                                                                      (int)(i2));
                    }
                }
                
//#line 299 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return offset;
            }
            
            
//#line 302 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public int
                                                                                                             maxOffset$O(
                                                                                                             ){
                
//#line 303 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Region r =
                  ((x10.array.Region)(this.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
                
//#line 304 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70962 =
                  r.size$O();
                
//#line 304 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t70963 =
                  ((t70962) - (((int)(1))));
                
//#line 304 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70963;
            }
            
            
//#line 307 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
public java.lang.String
                                                                                                             toString(
                                                                                                             ){
                
//#line 308 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
java.lang.String s =
                  "MortonDist(";
                
//#line 309 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
boolean first =
                  true;
                
//#line 310 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final au.edu.anu.mm.MortonDist this7062271173 =
                  ((au.edu.anu.mm.MortonDist)(this));
                
//#line 137 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.PlaceGroup t7096571174 =
                  ((x10.array.PlaceGroup)(this7062271173.
                                            pg));
                
//#line 310 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.lang.Iterator<x10.lang.Place> p4792071175 =
                  ((x10.lang.Iterable<x10.lang.Place>)t7096571174).iterator();
                
//#line 310 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
for (;
                                                                                                                    true;
                                                                                                                    ) {
                    
//#line 310 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t7097771176 =
                      ((x10.lang.Iterator<x10.lang.Place>)p4792071175).hasNext$O();
                    
//#line 310 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (!(t7097771176)) {
                        
//#line 310 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
break;
                    }
                    
//#line 310 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.lang.Place p71161 =
                      ((x10.lang.Iterator<x10.lang.Place>)p4792071175).next$G();
                    
//#line 311 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t7096671162 =
                      first;
                    
//#line 311 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final boolean t7096971163 =
                      !(t7096671162);
                    
//#line 311 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
if (t7096971163) {
                        
//#line 311 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final java.lang.String t7096771164 =
                          s;
                        
//#line 311 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final java.lang.String t7096871165 =
                          ((t7096771164) + (","));
                        
//#line 311 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
s = t7096871165;
                    }
                    
//#line 312 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final java.lang.String t7097471166 =
                      s;
                    
//#line 312 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Region t7097071167 =
                      ((x10.array.Region)(this.get(((x10.lang.Place)(p71161)))));
                    
//#line 312 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final java.lang.String t7097171168 =
                      (("") + (t7097071167));
                    
//#line 312 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final java.lang.String t7097271169 =
                      ((t7097171168) + ("->"));
                    
//#line 312 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final int t7097371170 =
                      p71161.
                        id;
                    
//#line 312 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final java.lang.String t7097571171 =
                      ((t7097271169) + ((x10.core.Int.$box(t7097371170))));
                    
//#line 312 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final java.lang.String t7097671172 =
                      ((t7097471166) + (t7097571171));
                    
//#line 312 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
s = t7097671172;
                    
//#line 313 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
first = false;
                }
                
//#line 315 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final java.lang.String t70978 =
                  s;
                
//#line 315 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final java.lang.String t70979 =
                  ((t70978) + (")"));
                
//#line 315 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
s = t70979;
                
//#line 316 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final java.lang.String t70980 =
                  s;
                
//#line 316 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70980;
            }
            
            
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final public au.edu.anu.mm.MortonDist
                                                                                                            au$edu$anu$mm$MortonDist$$au$edu$anu$mm$MortonDist$this(
                                                                                                            ){
                
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return au.edu.anu.mm.MortonDist.this;
            }
            
            
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final private void
                                                                                                            __fieldInitializers47119(
                                                                                                            ){
                
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this.X10$object_lock_id0 = -1;
                
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
this.regionForHere = null;
            }
            
            final public static void
              __fieldInitializers47119$P(
              final au.edu.anu.mm.MortonDist MortonDist){
                MortonDist.__fieldInitializers47119();
            }
            
            public static class $Closure$74
            extends x10.core.Ref
              implements x10.core.fun.Fun_0_1,
                          x10.x10rt.X10JavaSerializable 
            {
                private static final long serialVersionUID = 1L;
                private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$74.class);
                
                public static final x10.rtt.RuntimeType<$Closure$74> $RTT = new x10.rtt.StaticFunType<$Closure$74>(
                /* base class */$Closure$74.class
                , /* parents */ new x10.rtt.Type[] {new x10.rtt.ParameterizedType(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.array.Region.$RTT), x10.rtt.Types.OBJECT}
                );
                public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                
                
                public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$74 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    au.edu.anu.mm.MortonDist out$$ = (au.edu.anu.mm.MortonDist) $deserializer.readRef();
                    $_obj.out$$ = out$$;
                    x10.array.PlaceGroup pg = (x10.array.PlaceGroup) $deserializer.readRef();
                    $_obj.pg = pg;
                    return $_obj;
                    
                }
                
                public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    $Closure$74 $_obj = new $Closure$74((java.lang.System[]) null);
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
                    if (pg instanceof x10.x10rt.X10JavaSerializable) {
                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.pg);
                    } else {
                    $serializer.write(this.pg);
                    }
                    
                }
                
                // constructor just for allocation
                public $Closure$74(final java.lang.System[] $dummy) { 
                super($dummy);
                }
                // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
                public java.lang.Object
                  $apply(final java.lang.Object a1,final x10.rtt.Type t1){return $apply(x10.core.Int.$unbox(a1));}
                
                    
                    public x10.array.Region
                      $apply(
                      final int i){
                        
//#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.PlaceGroup t70763 =
                          ((x10.array.PlaceGroup)(this.
                                                    pg));
                        
//#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.lang.Place t70764 =
                          t70763.$apply((int)(i));
                        
//#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
final x10.array.Region t70765 =
                          ((x10.array.Region)(this.
                                                out$$.mortonRegionForPlace(((x10.lang.Place)(t70764)))));
                        
//#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10"
return t70765;
                    }
                    
                    public au.edu.anu.mm.MortonDist
                      out$$;
                    public x10.array.PlaceGroup
                      pg;
                    
                    // creation method for java code
                    public static au.edu.anu.mm.MortonDist.$Closure$74 $make(final au.edu.anu.mm.MortonDist out$$,
                                                                             final x10.array.PlaceGroup pg){return new $Closure$74(out$$,pg);}
                    public $Closure$74(final au.edu.anu.mm.MortonDist out$$,
                                       final x10.array.PlaceGroup pg) { {
                                                                               this.out$$ = out$$;
                                                                               this.pg = ((x10.array.PlaceGroup)(pg));
                                                                           }}
                    
                }
                
            
        }
        