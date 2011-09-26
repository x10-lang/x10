package x10x.vector;


public class Vector3d
extends x10.core.Struct
  implements x10x.vector.Tuple3d,
             x10.util.concurrent.Atomic,
              x10.x10rt.X10JavaSerializable 
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Vector3d.class);
    
    public static final x10.rtt.RuntimeType<Vector3d> $RTT = new x10.rtt.NamedType<Vector3d>(
    "x10x.vector.Vector3d", /* base class */Vector3d.class
    , /* parents */ new x10.rtt.Type[] {x10x.vector.Tuple3d.$RTT, x10.rtt.Types.STRUCT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Vector3d $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        $_obj.i = $deserializer.readDouble();
        $_obj.j = $deserializer.readDouble();
        $_obj.k = $deserializer.readDouble();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Vector3d $_obj = new Vector3d((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.write(this.i);
        $serializer.write(this.j);
        $serializer.write(this.k);
        
    }
    
    // zero value constructor
    public Vector3d(final java.lang.System $dummy) { this.i = 0.0; this.j = 0.0; this.k = 0.0; }
    // constructor just for allocation
    public Vector3d(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
public double
          i;
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
public double
          j;
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
public double
          k;
        
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
public int
          X10$object_lock_id0;
        
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
public x10.util.concurrent.OrderedLock
                                                                                                       getOrderedLock(
                                                                                                       ){
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final int t54180 =
              this.
                X10$object_lock_id0;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10.util.concurrent.OrderedLock t54181 =
              x10.util.concurrent.OrderedLock.getLock((int)(t54180));
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return t54181;
        }
        
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
public static int
          X10$class_lock_id1 =
          x10.util.concurrent.OrderedLock.createNewLockID();
        
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
public static x10.util.concurrent.OrderedLock
                                                                                                       getStaticOrderedLock(
                                                                                                       ){
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final int t54182 =
              x10x.vector.Vector3d.X10$class_lock_id1;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10.util.concurrent.OrderedLock t54183 =
              x10.util.concurrent.OrderedLock.getLock((int)(t54182));
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return t54183;
        }
        
        
//#line 10 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
public static x10x.vector.Vector3d
          NULL;
        
//#line 12 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
public static x10x.vector.Vector3d
          UX;
        
//#line 13 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
public static x10x.vector.Vector3d
          UY;
        
//#line 14 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
public static x10x.vector.Vector3d
          UZ;
        
        
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
// creation method for java code
        public static x10x.vector.Vector3d $make(final double i,
                                                 final double j,
                                                 final double k){return new x10x.vector.Vector3d((java.lang.System[]) null).$init(i,j,k);}
        
        // constructor for non-virtual call
        final public x10x.vector.Vector3d x10x$vector$Vector3d$$init$S(final double i,
                                                                       final double j,
                                                                       final double k) { {
                                                                                                
//#line 17 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
this.i = i;
                                                                                                this.j = j;
                                                                                                this.k = k;
                                                                                                
                                                                                                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d this5414154523 =
                                                                                                  this;
                                                                                                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
this5414154523.X10$object_lock_id0 = -1;
                                                                                            }
                                                                                            return this;
                                                                                            }
        
        // constructor
        public x10x.vector.Vector3d $init(final double i,
                                          final double j,
                                          final double k){return x10x$vector$Vector3d$$init$S(i,j,k);}
        
        
        
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
// creation method for java code
        public static x10x.vector.Vector3d $make(final double i,
                                                 final double j,
                                                 final double k,
                                                 final x10.util.concurrent.OrderedLock paramLock){return new x10x.vector.Vector3d((java.lang.System[]) null).$init(i,j,k,paramLock);}
        
        // constructor for non-virtual call
        final public x10x.vector.Vector3d x10x$vector$Vector3d$$init$S(final double i,
                                                                       final double j,
                                                                       final double k,
                                                                       final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                 
//#line 17 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
this.i = i;
                                                                                                                                 this.j = j;
                                                                                                                                 this.k = k;
                                                                                                                                 
                                                                                                                                 
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d this5414454524 =
                                                                                                                                   this;
                                                                                                                                 
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
this5414454524.X10$object_lock_id0 = -1;
                                                                                                                                 
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final int t54184 =
                                                                                                                                   paramLock.getIndex();
                                                                                                                                 
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
this.X10$object_lock_id0 = ((int)(t54184));
                                                                                                                             }
                                                                                                                             return this;
                                                                                                                             }
        
        // constructor
        public x10x.vector.Vector3d $init(final double i,
                                          final double j,
                                          final double k,
                                          final x10.util.concurrent.OrderedLock paramLock){return x10x$vector$Vector3d$$init$S(i,j,k,paramLock);}
        
        
        
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
// creation method for java code
        public static x10x.vector.Vector3d $make(final x10x.vector.Tuple3d t){return new x10x.vector.Vector3d((java.lang.System[]) null).$init(t);}
        
        // constructor for non-virtual call
        final public x10x.vector.Vector3d x10x$vector$Vector3d$$init$S(final x10x.vector.Tuple3d t) { {
                                                                                                             
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54185 =
                                                                                                               t.i$O();
                                                                                                             
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54186 =
                                                                                                               t.j$O();
                                                                                                             
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54187 =
                                                                                                               t.k$O();
                                                                                                             
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
this.$init(((double)(t54185)),
                                                                                                                                                                                                                      ((double)(t54186)),
                                                                                                                                                                                                                      ((double)(t54187)));
                                                                                                         }
                                                                                                         return this;
                                                                                                         }
        
        // constructor
        public x10x.vector.Vector3d $init(final x10x.vector.Tuple3d t){return x10x$vector$Vector3d$$init$S(t);}
        
        
        
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
// creation method for java code
        public static x10x.vector.Vector3d $make(final x10x.vector.Tuple3d t,
                                                 final x10.util.concurrent.OrderedLock paramLock){return new x10x.vector.Vector3d((java.lang.System[]) null).$init(t,paramLock);}
        
        // constructor for non-virtual call
        final public x10x.vector.Vector3d x10x$vector$Vector3d$$init$S(final x10x.vector.Tuple3d t,
                                                                       final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                 
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5418854525 =
                                                                                                                                   t.i$O();
                                                                                                                                 
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5418954526 =
                                                                                                                                   t.j$O();
                                                                                                                                 
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5419054527 =
                                                                                                                                   t.k$O();
                                                                                                                                 
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
this.$init(((double)(t5418854525)),
                                                                                                                                                                                                                                          ((double)(t5418954526)),
                                                                                                                                                                                                                                          ((double)(t5419054527)));
                                                                                                                                 
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final int t54191 =
                                                                                                                                   paramLock.getIndex();
                                                                                                                                 
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
this.X10$object_lock_id0 = ((int)(t54191));
                                                                                                                             }
                                                                                                                             return this;
                                                                                                                             }
        
        // constructor
        public x10x.vector.Vector3d $init(final x10x.vector.Tuple3d t,
                                          final x10.util.concurrent.OrderedLock paramLock){return x10x$vector$Vector3d$$init$S(t,paramLock);}
        
        
        
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public double
                                                                                                        i$O(
                                                                                                        ){
            
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54192 =
              i;
            
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return t54192;
        }
        
        
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public double
                                                                                                        j$O(
                                                                                                        ){
            
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54193 =
              j;
            
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return t54193;
        }
        
        
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public double
                                                                                                        k$O(
                                                                                                        ){
            
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54194 =
              k;
            
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return t54194;
        }
        
        
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public java.lang.String
                                                                                                        toString(
                                                                                                        ){
            
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54195 =
              i;
            
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final java.lang.String t54196 =
              (("(") + ((x10.core.Double.$box(t54195))));
            
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final java.lang.String t54197 =
              ((t54196) + ("i + "));
            
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54198 =
              j;
            
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final java.lang.String t54199 =
              ((t54197) + ((x10.core.Double.$box(t54198))));
            
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final java.lang.String t54200 =
              ((t54199) + ("j + "));
            
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54201 =
              k;
            
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final java.lang.String t54202 =
              ((t54200) + ((x10.core.Double.$box(t54201))));
            
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final java.lang.String t54203 =
              ((t54202) + ("k)"));
            
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return t54203;
        }
        
        
//#line 33 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public x10x.vector.Vector3d
                                                                                                        $plus(
                                                                                                        final x10x.vector.Vector3d that){
            
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d this54155 =
              ((x10x.vector.Vector3d)(this));
            
//#line 37 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d b54153 =
              ((x10x.vector.Vector3d)(that));
            
//#line 38 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d alloc2550654154 =
              new x10x.vector.Vector3d((java.lang.System[]) null);
            
//#line 38 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5420454528 =
              this54155.
                i;
            
//#line 24 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5420554529 =
              b54153.
                i;
            
//#line 38 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5421054530 =
              ((t5420454528) + (((double)(t5420554529))));
            
//#line 38 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5420654531 =
              this54155.
                j;
            
//#line 25 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5420754532 =
              b54153.
                j;
            
//#line 38 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5421154533 =
              ((t5420654531) + (((double)(t5420754532))));
            
//#line 38 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5420854534 =
              this54155.
                k;
            
//#line 26 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5420954535 =
              b54153.
                k;
            
//#line 38 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5421254536 =
              ((t5420854534) + (((double)(t5420954535))));
            
//#line 38 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10.util.concurrent.OrderedLock t5421354537 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 38 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
alloc2550654154.$init(t5421054530,
                                                                                                                                  t5421154533,
                                                                                                                                  t5421254536,
                                                                                                                                  t5421354537);
            
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return alloc2550654154;
        }
        
        
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public x10x.vector.Vector3d
                                                                                                        add(
                                                                                                        final x10x.vector.Vector3d b){
            
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d alloc25506 =
              new x10x.vector.Vector3d((java.lang.System[]) null);
            
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5421454538 =
              i;
            
//#line 24 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5421554539 =
              b.
                i;
            
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5422054540 =
              ((t5421454538) + (((double)(t5421554539))));
            
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5421654541 =
              j;
            
//#line 25 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5421754542 =
              b.
                j;
            
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5422154543 =
              ((t5421654541) + (((double)(t5421754542))));
            
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5421854544 =
              k;
            
//#line 26 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5421954545 =
              b.
                k;
            
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5422254546 =
              ((t5421854544) + (((double)(t5421954545))));
            
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10.util.concurrent.OrderedLock t5422354547 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
alloc25506.$init(t5422054540,
                                                                                                                           t5422154543,
                                                                                                                           t5422254546,
                                                                                                                           t5422354547);
            
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return alloc25506;
        }
        
        
//#line 44 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public static x10x.vector.Vector3d
                                                                                                        $minus(
                                                                                                        final x10x.vector.Vector3d x,
                                                                                                        final x10x.vector.Vector3d y){
            
//#line 48 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d b54156 =
              ((x10x.vector.Vector3d)(y));
            
//#line 49 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d alloc2550754157 =
              new x10x.vector.Vector3d((java.lang.System[]) null);
            
//#line 49 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5422454548 =
              x.
                i;
            
//#line 24 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5422554549 =
              b54156.
                i;
            
//#line 49 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5423054550 =
              ((t5422454548) - (((double)(t5422554549))));
            
//#line 49 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5422654551 =
              x.
                j;
            
//#line 25 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5422754552 =
              b54156.
                j;
            
//#line 49 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5423154553 =
              ((t5422654551) - (((double)(t5422754552))));
            
//#line 49 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5422854554 =
              x.
                k;
            
//#line 26 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5422954555 =
              b54156.
                k;
            
//#line 49 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5423254556 =
              ((t5422854554) - (((double)(t5422954555))));
            
//#line 49 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10.util.concurrent.OrderedLock t5423354557 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 49 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
alloc2550754157.$init(t5423054550,
                                                                                                                                  t5423154553,
                                                                                                                                  t5423254556,
                                                                                                                                  t5423354557);
            
//#line 45 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return alloc2550754157;
        }
        
        
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public x10x.vector.Vector3d
                                                                                                        sub(
                                                                                                        final x10x.vector.Vector3d b){
            
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d alloc25507 =
              new x10x.vector.Vector3d((java.lang.System[]) null);
            
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5423454558 =
              i;
            
//#line 24 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5423554559 =
              b.
                i;
            
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5424054560 =
              ((t5423454558) - (((double)(t5423554559))));
            
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5423654561 =
              j;
            
//#line 25 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5423754562 =
              b.
                j;
            
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5424154563 =
              ((t5423654561) - (((double)(t5423754562))));
            
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5423854564 =
              k;
            
//#line 26 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5423954565 =
              b.
                k;
            
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5424254566 =
              ((t5423854564) - (((double)(t5423954565))));
            
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10.util.concurrent.OrderedLock t5424354567 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
alloc25507.$init(t5424054560,
                                                                                                                           t5424154563,
                                                                                                                           t5424254566,
                                                                                                                           t5424354567);
            
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return alloc25507;
        }
        
        
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public double
                                                                                                        $times$O(
                                                                                                        final x10x.vector.Vector3d that){
            
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d this54159 =
              ((x10x.vector.Vector3d)(this));
            
//#line 59 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d vec54158 =
              ((x10x.vector.Vector3d)(that));
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54244 =
              this54159.
                i;
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54245 =
              vec54158.
                i;
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54248 =
              ((t54244) * (((double)(t54245))));
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54246 =
              this54159.
                j;
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54247 =
              vec54158.
                j;
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54249 =
              ((t54246) * (((double)(t54247))));
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54252 =
              ((t54248) + (((double)(t54249))));
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54250 =
              this54159.
                k;
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54251 =
              vec54158.
                k;
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54253 =
              ((t54250) * (((double)(t54251))));
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54254 =
              ((t54252) + (((double)(t54253))));
            
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return t54254;
        }
        
        
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public double
                                                                                                        dot$O(
                                                                                                        final x10x.vector.Vector3d vec){
            
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54255 =
              this.
                i;
            
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54256 =
              vec.
                i;
            
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54259 =
              ((t54255) * (((double)(t54256))));
            
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54257 =
              this.
                j;
            
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54258 =
              vec.
                j;
            
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54260 =
              ((t54257) * (((double)(t54258))));
            
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54263 =
              ((t54259) + (((double)(t54260))));
            
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54261 =
              this.
                k;
            
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54262 =
              vec.
                k;
            
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54264 =
              ((t54261) * (((double)(t54262))));
            
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54265 =
              ((t54263) + (((double)(t54264))));
            
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return t54265;
        }
        
        
//#line 63 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public x10x.vector.Vector3d
                                                                                                        cross(
                                                                                                        final x10x.vector.Vector3d vec){
            
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d alloc25508 =
              new x10x.vector.Vector3d((java.lang.System[]) null);
            
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5426654568 =
              j;
            
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5426754569 =
              vec.
                k;
            
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5427054570 =
              ((t5426654568) * (((double)(t5426754569))));
            
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5426854571 =
              k;
            
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5426954572 =
              vec.
                j;
            
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5427154573 =
              ((t5426854571) * (((double)(t5426954572))));
            
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5428454574 =
              ((t5427054570) - (((double)(t5427154573))));
            
//#line 65 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5427254575 =
              k;
            
//#line 65 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5427354576 =
              vec.
                i;
            
//#line 65 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5427654577 =
              ((t5427254575) * (((double)(t5427354576))));
            
//#line 65 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5427454578 =
              i;
            
//#line 65 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5427554579 =
              vec.
                k;
            
//#line 65 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5427754580 =
              ((t5427454578) * (((double)(t5427554579))));
            
//#line 65 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5428554581 =
              ((t5427654577) - (((double)(t5427754580))));
            
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5427854582 =
              i;
            
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5427954583 =
              vec.
                j;
            
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5428254584 =
              ((t5427854582) * (((double)(t5427954583))));
            
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5428054585 =
              j;
            
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5428154586 =
              vec.
                i;
            
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5428354587 =
              ((t5428054585) * (((double)(t5428154586))));
            
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5428654588 =
              ((t5428254584) - (((double)(t5428354587))));
            
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10.util.concurrent.OrderedLock t5428754589 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
alloc25508.$init(t5428454574,
                                                                                                                           t5428554581,
                                                                                                                           t5428654588,
                                                                                                                           t5428754589);
            
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return alloc25508;
        }
        
        
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public x10x.vector.Vector3d
                                                                                                        $times(
                                                                                                        final double that){
            
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d this54162 =
              ((x10x.vector.Vector3d)(this));
            
//#line 81 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double c54160 =
              that;
            
//#line 82 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d alloc2550954161 =
              new x10x.vector.Vector3d((java.lang.System[]) null);
            
//#line 82 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5428854590 =
              this54162.
                i;
            
//#line 82 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5429154591 =
              ((t5428854590) * (((double)(c54160))));
            
//#line 82 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5428954592 =
              this54162.
                j;
            
//#line 82 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5429254593 =
              ((t5428954592) * (((double)(c54160))));
            
//#line 82 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5429054594 =
              this54162.
                k;
            
//#line 82 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5429354595 =
              ((t5429054594) * (((double)(c54160))));
            
//#line 82 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10.util.concurrent.OrderedLock t5429454596 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 82 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
alloc2550954161.$init(t5429154591,
                                                                                                                                  t5429254593,
                                                                                                                                  t5429354595,
                                                                                                                                  t5429454596);
            
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return alloc2550954161;
        }
        
        
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public static x10x.vector.Vector3d
                                                                                                        $times(
                                                                                                        final double x,
                                                                                                        final x10x.vector.Vector3d y){
            
//#line 72 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double that54163 =
              x;
            
//#line 81 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double c54164 =
              that54163;
            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d alloc2550954165 =
              new x10x.vector.Vector3d((java.lang.System[]) null);
            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5429554597 =
              y.
                i;
            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5429854598 =
              ((t5429554597) * (((double)(c54164))));
            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5429654599 =
              y.
                j;
            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5429954600 =
              ((t5429654599) * (((double)(c54164))));
            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5429754601 =
              y.
                k;
            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5430054602 =
              ((t5429754601) * (((double)(c54164))));
            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10.util.concurrent.OrderedLock t5430154603 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
alloc2550954165.$init(t5429854598,
                                                                                                                                   t5429954600,
                                                                                                                                   t5430054602,
                                                                                                                                   t5430154603);
            
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return alloc2550954165;
        }
        
        
//#line 81 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public x10x.vector.Vector3d
                                                                                                        mul(
                                                                                                        final double c){
            
//#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d alloc25509 =
              new x10x.vector.Vector3d((java.lang.System[]) null);
            
//#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5430254604 =
              this.
                i;
            
//#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5430554605 =
              ((t5430254604) * (((double)(c))));
            
//#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5430354606 =
              this.
                j;
            
//#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5430654607 =
              ((t5430354606) * (((double)(c))));
            
//#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5430454608 =
              this.
                k;
            
//#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5430754609 =
              ((t5430454608) * (((double)(c))));
            
//#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10.util.concurrent.OrderedLock t5430854610 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
alloc25509.$init(t5430554605,
                                                                                                                           t5430654607,
                                                                                                                           t5430754609,
                                                                                                                           t5430854610);
            
//#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return alloc25509;
        }
        
        
//#line 85 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public double
                                                                                                        mixedProduct$O(
                                                                                                        final x10x.vector.Vector3d v2,
                                                                                                        final x10x.vector.Vector3d v3){
            
//#line 86 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d this54169 =
              ((x10x.vector.Vector3d)(this));
            
//#line 63 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d vec54166 =
              ((x10x.vector.Vector3d)(v3));
            
//#line 64 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d alloc2550854167 =
              new x10x.vector.Vector3d((java.lang.System[]) null);
            
//#line 64 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5430954611 =
              v2.
                j;
            
//#line 64 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5431054612 =
              vec54166.
                k;
            
//#line 64 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5431354613 =
              ((t5430954611) * (((double)(t5431054612))));
            
//#line 64 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5431154614 =
              v2.
                k;
            
//#line 64 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5431254615 =
              vec54166.
                j;
            
//#line 64 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5431454616 =
              ((t5431154614) * (((double)(t5431254615))));
            
//#line 64 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5432754617 =
              ((t5431354613) - (((double)(t5431454616))));
            
//#line 65 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5431554618 =
              v2.
                k;
            
//#line 65 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5431654619 =
              vec54166.
                i;
            
//#line 65 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5431954620 =
              ((t5431554618) * (((double)(t5431654619))));
            
//#line 65 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5431754621 =
              v2.
                i;
            
//#line 65 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5431854622 =
              vec54166.
                k;
            
//#line 65 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5432054623 =
              ((t5431754621) * (((double)(t5431854622))));
            
//#line 65 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5432854624 =
              ((t5431954620) - (((double)(t5432054623))));
            
//#line 66 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5432154625 =
              v2.
                i;
            
//#line 66 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5432254626 =
              vec54166.
                j;
            
//#line 66 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5432554627 =
              ((t5432154625) * (((double)(t5432254626))));
            
//#line 66 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5432354628 =
              v2.
                j;
            
//#line 66 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5432454629 =
              vec54166.
                i;
            
//#line 66 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5432654630 =
              ((t5432354628) * (((double)(t5432454629))));
            
//#line 66 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5432954631 =
              ((t5432554627) - (((double)(t5432654630))));
            
//#line 64 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10.util.concurrent.OrderedLock t5433054632 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 64 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
alloc2550854167.$init(t5432754617,
                                                                                                                                  t5432854624,
                                                                                                                                  t5432954631,
                                                                                                                                  t5433054632);
            
//#line 59 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d vec54168 =
              ((x10x.vector.Vector3d)(alloc2550854167));
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54331 =
              this54169.
                i;
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54332 =
              vec54168.
                i;
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54335 =
              ((t54331) * (((double)(t54332))));
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54333 =
              this54169.
                j;
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54334 =
              vec54168.
                j;
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54336 =
              ((t54333) * (((double)(t54334))));
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54339 =
              ((t54335) + (((double)(t54336))));
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54337 =
              this54169.
                k;
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54338 =
              vec54168.
                k;
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54340 =
              ((t54337) * (((double)(t54338))));
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54341 =
              ((t54339) + (((double)(t54340))));
            
//#line 86 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return t54341;
        }
        
        
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public double
                                                                                                        lengthSquared$O(
                                                                                                        ){
            
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54342 =
              i;
            
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54343 =
              i;
            
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54346 =
              ((t54342) * (((double)(t54343))));
            
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54344 =
              j;
            
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54345 =
              j;
            
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54347 =
              ((t54344) * (((double)(t54345))));
            
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54350 =
              ((t54346) + (((double)(t54347))));
            
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54348 =
              k;
            
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54349 =
              k;
            
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54351 =
              ((t54348) * (((double)(t54349))));
            
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54352 =
              ((t54350) + (((double)(t54351))));
            
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return t54352;
        }
        
        
//#line 93 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public double
                                                                                                        length$O(
                                                                                                        ){
            
//#line 94 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54353 =
              i;
            
//#line 94 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54354 =
              i;
            
//#line 94 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54357 =
              ((t54353) * (((double)(t54354))));
            
//#line 94 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54355 =
              j;
            
//#line 94 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54356 =
              j;
            
//#line 94 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54358 =
              ((t54355) * (((double)(t54356))));
            
//#line 94 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54361 =
              ((t54357) + (((double)(t54358))));
            
//#line 94 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54359 =
              k;
            
//#line 94 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54360 =
              k;
            
//#line 94 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54362 =
              ((t54359) * (((double)(t54360))));
            
//#line 94 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54363 =
              ((t54361) + (((double)(t54362))));
            
//#line 94 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54364 =
              java.lang.Math.sqrt(((double)(t54363)));
            
//#line 94 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return t54364;
        }
        
        
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public double
                                                                                                        maxNorm$O(
                                                                                                        ){
            
//#line 98 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54365 =
              i;
            
//#line 343 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final double a54170 =
              x10.lang.Math.abs$O((double)(t54365));
            
//#line 98 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54366 =
              j;
            
//#line 343 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final double b54171 =
              x10.lang.Math.abs$O((double)(t54366));
            
//#line 343 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t54367 =
              ((a54170) < (((double)(b54171))));
            
//#line 343 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
double t54368 =
               0;
            
//#line 343 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t54367) {
                
//#line 343 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t54368 = b54171;
            } else {
                
//#line 343 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t54368 = a54170;
            }
            
//#line 343 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final double a54172 =
              t54368;
            
//#line 98 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54369 =
              k;
            
//#line 343 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final double b54173 =
              x10.lang.Math.abs$O((double)(t54369));
            
//#line 343 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t54370 =
              ((a54172) < (((double)(b54173))));
            
//#line 343 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
double t54371 =
               0;
            
//#line 343 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t54370) {
                
//#line 343 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t54371 = b54173;
            } else {
                
//#line 343 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t54371 = a54172;
            }
            
//#line 343 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final double t54372 =
              t54371;
            
//#line 98 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return t54372;
        }
        
        
//#line 102 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public double
                                                                                                         magnitude$O(
                                                                                                         ){
            
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54373 =
              i;
            
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54374 =
              i;
            
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54377 =
              ((t54373) * (((double)(t54374))));
            
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54375 =
              j;
            
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54376 =
              j;
            
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54378 =
              ((t54375) * (((double)(t54376))));
            
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54381 =
              ((t54377) + (((double)(t54378))));
            
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54379 =
              k;
            
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54380 =
              k;
            
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54382 =
              ((t54379) * (((double)(t54380))));
            
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54383 =
              ((t54381) + (((double)(t54382))));
            
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54384 =
              java.lang.Math.sqrt(((double)(t54383)));
            
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return t54384;
        }
        
        
//#line 106 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public double
                                                                                                         angleWith$O(
                                                                                                         final x10x.vector.Vector3d vec){
            
//#line 107 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d this54175 =
              ((x10x.vector.Vector3d)(this));
            
//#line 59 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d vec54174 =
              ((x10x.vector.Vector3d)(vec));
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54385 =
              this54175.
                i;
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54386 =
              vec54174.
                i;
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54389 =
              ((t54385) * (((double)(t54386))));
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54387 =
              this54175.
                j;
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54388 =
              vec54174.
                j;
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54390 =
              ((t54387) * (((double)(t54388))));
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54393 =
              ((t54389) + (((double)(t54390))));
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54391 =
              this54175.
                k;
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54392 =
              vec54174.
                k;
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54394 =
              ((t54391) * (((double)(t54392))));
            
//#line 107 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double aDotb =
              ((t54393) + (((double)(t54394))));
            
//#line 108 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d this54176 =
              ((x10x.vector.Vector3d)(this));
            
//#line 103 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54395 =
              this54176.
                i;
            
//#line 103 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54396 =
              this54176.
                i;
            
//#line 103 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54399 =
              ((t54395) * (((double)(t54396))));
            
//#line 103 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54397 =
              this54176.
                j;
            
//#line 103 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54398 =
              this54176.
                j;
            
//#line 103 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54400 =
              ((t54397) * (((double)(t54398))));
            
//#line 103 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54403 =
              ((t54399) + (((double)(t54400))));
            
//#line 103 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54401 =
              this54176.
                k;
            
//#line 103 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54402 =
              this54176.
                k;
            
//#line 103 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54404 =
              ((t54401) * (((double)(t54402))));
            
//#line 103 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54405 =
              ((t54403) + (((double)(t54404))));
            
//#line 103 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54417 =
              java.lang.Math.sqrt(((double)(t54405)));
            
//#line 103 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54406 =
              vec.
                i;
            
//#line 103 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54407 =
              vec.
                i;
            
//#line 103 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54410 =
              ((t54406) * (((double)(t54407))));
            
//#line 103 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54408 =
              vec.
                j;
            
//#line 103 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54409 =
              vec.
                j;
            
//#line 103 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54411 =
              ((t54408) * (((double)(t54409))));
            
//#line 103 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54414 =
              ((t54410) + (((double)(t54411))));
            
//#line 103 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54412 =
              vec.
                k;
            
//#line 103 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54413 =
              vec.
                k;
            
//#line 103 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54415 =
              ((t54412) * (((double)(t54413))));
            
//#line 103 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54416 =
              ((t54414) + (((double)(t54415))));
            
//#line 103 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54418 =
              java.lang.Math.sqrt(((double)(t54416)));
            
//#line 108 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double ab =
              ((t54417) * (((double)(t54418))));
            
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54419 =
              ((aDotb) / (((double)(ab))));
            
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54420 =
              java.lang.Math.acos(((double)(t54419)));
            
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return t54420;
        }
        
        
//#line 113 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public x10x.vector.Vector3d
                                                                                                         normalize(
                                                                                                         ){
            
//#line 114 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d this54177 =
              ((x10x.vector.Vector3d)(this));
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54421 =
              this54177.
                i;
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54422 =
              this54177.
                i;
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54425 =
              ((t54421) * (((double)(t54422))));
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54423 =
              this54177.
                j;
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54424 =
              this54177.
                j;
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54426 =
              ((t54423) * (((double)(t54424))));
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54429 =
              ((t54425) + (((double)(t54426))));
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54427 =
              this54177.
                k;
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54428 =
              this54177.
                k;
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54430 =
              ((t54427) * (((double)(t54428))));
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54431 =
              ((t54429) + (((double)(t54430))));
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54432 =
              java.lang.Math.sqrt(((double)(t54431)));
            
//#line 114 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double norm =
              ((1.0) / (((double)(t54432))));
            
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d alloc25510 =
              new x10x.vector.Vector3d((java.lang.System[]) null);
            
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5443354633 =
              i;
            
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5443654634 =
              ((t5443354633) * (((double)(norm))));
            
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5443454635 =
              j;
            
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5443754636 =
              ((t5443454635) * (((double)(norm))));
            
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5443554637 =
              k;
            
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5443854638 =
              ((t5443554637) * (((double)(norm))));
            
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10.util.concurrent.OrderedLock t5443954639 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
alloc25510.$init(t5443654634,
                                                                                                                            t5443754636,
                                                                                                                            t5443854638,
                                                                                                                            t5443954639);
            
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return alloc25510;
        }
        
        
//#line 121 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public x10x.vector.Vector3d
                                                                                                         inverse(
                                                                                                         ){
            
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d this54178 =
              ((x10x.vector.Vector3d)(this));
            
//#line 90 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54440 =
              this54178.
                i;
            
//#line 90 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54441 =
              this54178.
                i;
            
//#line 90 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54444 =
              ((t54440) * (((double)(t54441))));
            
//#line 90 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54442 =
              this54178.
                j;
            
//#line 90 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54443 =
              this54178.
                j;
            
//#line 90 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54445 =
              ((t54442) * (((double)(t54443))));
            
//#line 90 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54448 =
              ((t54444) + (((double)(t54445))));
            
//#line 90 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54446 =
              this54178.
                k;
            
//#line 90 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54447 =
              this54178.
                k;
            
//#line 90 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54449 =
              ((t54446) * (((double)(t54447))));
            
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double l2 =
              ((t54448) + (((double)(t54449))));
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d alloc25511 =
              new x10x.vector.Vector3d((java.lang.System[]) null);
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5445054640 =
              i;
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5445354641 =
              ((t5445054640) / (((double)(l2))));
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5445154642 =
              j;
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5445454643 =
              ((t5445154642) / (((double)(l2))));
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5445254644 =
              k;
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5445554645 =
              ((t5445254644) / (((double)(l2))));
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10.util.concurrent.OrderedLock t5445654646 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
alloc25511.$init(t5445354641,
                                                                                                                            t5445454643,
                                                                                                                            t5445554645,
                                                                                                                            t5445654646);
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return alloc25511;
        }
        
        
//#line 126 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public static x10x.vector.Vector3d
                                                                                                         $minus(
                                                                                                         final x10x.vector.Vector3d x){
            
//#line 129 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d alloc2551254179 =
              new x10x.vector.Vector3d((java.lang.System[]) null);
            
//#line 129 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5445754647 =
              x.
                i;
            
//#line 129 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5446054648 =
              (-(t5445754647));
            
//#line 129 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5445854649 =
              x.
                j;
            
//#line 129 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5446154650 =
              (-(t5445854649));
            
//#line 129 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5445954651 =
              x.
                k;
            
//#line 129 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5446254652 =
              (-(t5445954651));
            
//#line 129 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10.util.concurrent.OrderedLock t5446354653 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 129 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
alloc2551254179.$init(t5446054648,
                                                                                                                                   t5446154650,
                                                                                                                                   t5446254652,
                                                                                                                                   t5446354653);
            
//#line 126 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return alloc2551254179;
        }
        
        
//#line 128 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public x10x.vector.Vector3d
                                                                                                         negate(
                                                                                                         ){
            
//#line 129 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d alloc25512 =
              new x10x.vector.Vector3d((java.lang.System[]) null);
            
//#line 129 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5446454654 =
              i;
            
//#line 129 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5446754655 =
              (-(t5446454654));
            
//#line 129 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5446554656 =
              j;
            
//#line 129 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5446854657 =
              (-(t5446554656));
            
//#line 129 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5446654658 =
              k;
            
//#line 129 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t5446954659 =
              (-(t5446654658));
            
//#line 129 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10.util.concurrent.OrderedLock t5447054660 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 129 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
alloc25512.$init(t5446754655,
                                                                                                                            t5446854657,
                                                                                                                            t5446954659,
                                                                                                                            t5447054660);
            
//#line 129 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return alloc25512;
        }
        
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public java.lang.String
                                                                                                       typeName$O(
                                                                                                       ){try {return x10.rtt.Types.typeName(this);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public int
                                                                                                       hashCode(
                                                                                                       ){
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
int result =
              1;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final int t54471 =
              result;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final int t54473 =
              ((8191) * (((int)(t54471))));
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54472 =
              this.
                i;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final int t54474 =
              x10.rtt.Types.hashCode(t54472);
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final int t54475 =
              ((t54473) + (((int)(t54474))));
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
result = t54475;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final int t54476 =
              result;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final int t54478 =
              ((8191) * (((int)(t54476))));
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54477 =
              this.
                j;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final int t54479 =
              x10.rtt.Types.hashCode(t54477);
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final int t54480 =
              ((t54478) + (((int)(t54479))));
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
result = t54480;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final int t54481 =
              result;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final int t54483 =
              ((8191) * (((int)(t54481))));
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54482 =
              this.
                k;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final int t54484 =
              x10.rtt.Types.hashCode(t54482);
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final int t54485 =
              ((t54483) + (((int)(t54484))));
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
result = t54485;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final int t54486 =
              result;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return t54486;
        }
        
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public boolean
                                                                                                       equals(
                                                                                                       java.lang.Object other){
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final java.lang.Object t54487 =
              other;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final boolean t54488 =
              x10x.vector.Vector3d.$RTT.instanceOf(t54487);
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final boolean t54489 =
              !(t54488);
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
if (t54489) {
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return false;
            }
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final java.lang.Object t54490 =
              other;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d t54491 =
              ((x10x.vector.Vector3d)x10.rtt.Types.asStruct(x10x.vector.Vector3d.$RTT,t54490));
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final boolean t54492 =
              this.equals(((x10x.vector.Vector3d)(t54491)));
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return t54492;
        }
        
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public boolean
                                                                                                       equals(
                                                                                                       x10x.vector.Vector3d other){
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54494 =
              this.
                i;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d t54493 =
              other;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54495 =
              t54493.
                i;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
boolean t54499 =
              ((double) t54494) ==
            ((double) t54495);
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
if (t54499) {
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54497 =
                  this.
                    j;
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d t54496 =
                  other;
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54498 =
                  t54496.
                    j;
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
t54499 = ((double) t54497) ==
                ((double) t54498);
            }
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
boolean t54503 =
              t54499;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
if (t54503) {
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54501 =
                  this.
                    k;
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d t54500 =
                  other;
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54502 =
                  t54500.
                    k;
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
t54503 = ((double) t54501) ==
                ((double) t54502);
            }
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final boolean t54504 =
              t54503;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return t54504;
        }
        
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public boolean
                                                                                                       _struct_equals$O(
                                                                                                       java.lang.Object other){
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final java.lang.Object t54505 =
              other;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final boolean t54506 =
              x10x.vector.Vector3d.$RTT.instanceOf(t54505);
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final boolean t54507 =
              !(t54506);
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
if (t54507) {
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return false;
            }
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final java.lang.Object t54508 =
              other;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d t54509 =
              ((x10x.vector.Vector3d)x10.rtt.Types.asStruct(x10x.vector.Vector3d.$RTT,t54508));
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final boolean t54510 =
              this._struct_equals$O(((x10x.vector.Vector3d)(t54509)));
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return t54510;
        }
        
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public boolean
                                                                                                       _struct_equals$O(
                                                                                                       x10x.vector.Vector3d other){
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54512 =
              this.
                i;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d t54511 =
              other;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54513 =
              t54511.
                i;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
boolean t54517 =
              ((double) t54512) ==
            ((double) t54513);
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
if (t54517) {
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54515 =
                  this.
                    j;
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d t54514 =
                  other;
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54516 =
                  t54514.
                    j;
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
t54517 = ((double) t54515) ==
                ((double) t54516);
            }
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
boolean t54521 =
              t54517;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
if (t54521) {
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54519 =
                  this.
                    k;
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d t54518 =
                  other;
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t54520 =
                  t54518.
                    k;
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
t54521 = ((double) t54519) ==
                ((double) t54520);
            }
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final boolean t54522 =
              t54521;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return t54522;
        }
        
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final public x10x.vector.Vector3d
                                                                                                       x10x$vector$Vector3d$$x10x$vector$Vector3d$this(
                                                                                                       ){
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
return x10x.vector.Vector3d.this;
        }
        
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final private void
                                                                                                       __fieldInitializers25025(
                                                                                                       ){
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
this.X10$object_lock_id0 = -1;
        }
        
        final public static void
          __fieldInitializers25025$P(
          final x10x.vector.Vector3d Vector3d){
            Vector3d.__fieldInitializers25025();
        }
        
        public static int
          fieldId$UZ;
        final public static x10.core.concurrent.AtomicInteger
          initStatus$UZ =
          new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        public static int
          fieldId$UY;
        final public static x10.core.concurrent.AtomicInteger
          initStatus$UY =
          new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        public static int
          fieldId$UX;
        final public static x10.core.concurrent.AtomicInteger
          initStatus$UX =
          new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        public static int
          fieldId$NULL;
        final public static x10.core.concurrent.AtomicInteger
          initStatus$NULL =
          new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        
        public static void
          getDeserialized$NULL(
          byte[] buf){
            if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                  ((boolean) true)) {
                x10x.vector.Vector3d.NULL = ((x10x.vector.Vector3d)(x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))));
            } else {
                x10x.vector.Vector3d.NULL = ((x10x.vector.Vector3d)(((x10x.vector.Vector3d)x10.rtt.Types.asStruct(x10x.vector.Vector3d.$RTT,x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))))));
            }
            x10x.vector.Vector3d.initStatus$NULL.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static x10x.vector.Vector3d
          getInitialized$NULL(
          ){
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0)) {
                if (x10x.vector.Vector3d.initStatus$NULL.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                       (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                    x10x.vector.Vector3d.NULL = ((x10x.vector.Vector3d)(new x10x.vector.Vector3d((java.lang.System[]) null).$init(((double)(int)(((int)(0)))),
                                                                                                                                  ((double)(int)(((int)(0)))),
                                                                                                                                  ((double)(int)(((int)(0)))),
                                                                                                                                  x10.util.concurrent.OrderedLock.createNewLock())));
                    if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                          ((boolean) true)) {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10x.vector.Vector3d.NULL)),
                                                                                  (int)(x10x.vector.Vector3d.fieldId$NULL));
                    } else {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10x.vector.Vector3d.NULL)),
                                                                                  (int)(x10x.vector.Vector3d.fieldId$NULL));
                    }
                    x10x.vector.Vector3d.initStatus$NULL.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
            }
            if (((int) x10x.vector.Vector3d.initStatus$NULL.get()) !=
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                while (((int) x10x.vector.Vector3d.initStatus$NULL.get()) !=
                       ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                }
                x10.runtime.impl.java.InitDispatcher.unlockInitialized();
            }
            return x10x.vector.Vector3d.NULL;
        }
        
        public static void
          getDeserialized$UX(
          byte[] buf){
            if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                  ((boolean) true)) {
                x10x.vector.Vector3d.UX = ((x10x.vector.Vector3d)(x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))));
            } else {
                x10x.vector.Vector3d.UX = ((x10x.vector.Vector3d)(((x10x.vector.Vector3d)x10.rtt.Types.asStruct(x10x.vector.Vector3d.$RTT,x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))))));
            }
            x10x.vector.Vector3d.initStatus$UX.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static x10x.vector.Vector3d
          getInitialized$UX(
          ){
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0)) {
                if (x10x.vector.Vector3d.initStatus$UX.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                     (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                    x10x.vector.Vector3d.UX = ((x10x.vector.Vector3d)(new x10x.vector.Vector3d((java.lang.System[]) null).$init(((double)(1.0)),
                                                                                                                                ((double)(int)(((int)(0)))),
                                                                                                                                ((double)(int)(((int)(0)))),
                                                                                                                                x10.util.concurrent.OrderedLock.createNewLock())));
                    if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                          ((boolean) true)) {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10x.vector.Vector3d.UX)),
                                                                                  (int)(x10x.vector.Vector3d.fieldId$UX));
                    } else {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10x.vector.Vector3d.UX)),
                                                                                  (int)(x10x.vector.Vector3d.fieldId$UX));
                    }
                    x10x.vector.Vector3d.initStatus$UX.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
            }
            if (((int) x10x.vector.Vector3d.initStatus$UX.get()) !=
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                while (((int) x10x.vector.Vector3d.initStatus$UX.get()) !=
                       ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                }
                x10.runtime.impl.java.InitDispatcher.unlockInitialized();
            }
            return x10x.vector.Vector3d.UX;
        }
        
        public static void
          getDeserialized$UY(
          byte[] buf){
            if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                  ((boolean) true)) {
                x10x.vector.Vector3d.UY = ((x10x.vector.Vector3d)(x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))));
            } else {
                x10x.vector.Vector3d.UY = ((x10x.vector.Vector3d)(((x10x.vector.Vector3d)x10.rtt.Types.asStruct(x10x.vector.Vector3d.$RTT,x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))))));
            }
            x10x.vector.Vector3d.initStatus$UY.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static x10x.vector.Vector3d
          getInitialized$UY(
          ){
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0)) {
                if (x10x.vector.Vector3d.initStatus$UY.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                     (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                    x10x.vector.Vector3d.UY = ((x10x.vector.Vector3d)(new x10x.vector.Vector3d((java.lang.System[]) null).$init(((double)(int)(((int)(0)))),
                                                                                                                                ((double)(1.0)),
                                                                                                                                ((double)(int)(((int)(0)))),
                                                                                                                                x10.util.concurrent.OrderedLock.createNewLock())));
                    if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                          ((boolean) true)) {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10x.vector.Vector3d.UY)),
                                                                                  (int)(x10x.vector.Vector3d.fieldId$UY));
                    } else {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10x.vector.Vector3d.UY)),
                                                                                  (int)(x10x.vector.Vector3d.fieldId$UY));
                    }
                    x10x.vector.Vector3d.initStatus$UY.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
            }
            if (((int) x10x.vector.Vector3d.initStatus$UY.get()) !=
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                while (((int) x10x.vector.Vector3d.initStatus$UY.get()) !=
                       ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                }
                x10.runtime.impl.java.InitDispatcher.unlockInitialized();
            }
            return x10x.vector.Vector3d.UY;
        }
        
        public static void
          getDeserialized$UZ(
          byte[] buf){
            if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                  ((boolean) true)) {
                x10x.vector.Vector3d.UZ = ((x10x.vector.Vector3d)(x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))));
            } else {
                x10x.vector.Vector3d.UZ = ((x10x.vector.Vector3d)(((x10x.vector.Vector3d)x10.rtt.Types.asStruct(x10x.vector.Vector3d.$RTT,x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))))));
            }
            x10x.vector.Vector3d.initStatus$UZ.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static x10x.vector.Vector3d
          getInitialized$UZ(
          ){
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0)) {
                if (x10x.vector.Vector3d.initStatus$UZ.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                     (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                    x10x.vector.Vector3d.UZ = ((x10x.vector.Vector3d)(new x10x.vector.Vector3d((java.lang.System[]) null).$init(((double)(int)(((int)(0)))),
                                                                                                                                ((double)(int)(((int)(0)))),
                                                                                                                                ((double)(1.0)),
                                                                                                                                x10.util.concurrent.OrderedLock.createNewLock())));
                    if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                          ((boolean) true)) {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10x.vector.Vector3d.UZ)),
                                                                                  (int)(x10x.vector.Vector3d.fieldId$UZ));
                    } else {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10x.vector.Vector3d.UZ)),
                                                                                  (int)(x10x.vector.Vector3d.fieldId$UZ));
                    }
                    x10x.vector.Vector3d.initStatus$UZ.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
            }
            if (((int) x10x.vector.Vector3d.initStatus$UZ.get()) !=
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                while (((int) x10x.vector.Vector3d.initStatus$UZ.get()) !=
                       ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                }
                x10.runtime.impl.java.InitDispatcher.unlockInitialized();
            }
            return x10x.vector.Vector3d.UZ;
        }
        
        static {
                   x10x.vector.Vector3d.fieldId$NULL = x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10x.vector.Vector3d")),
                                                                                                           ((java.lang.String)("NULL")));
                   x10x.vector.Vector3d.fieldId$UX = x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10x.vector.Vector3d")),
                                                                                                         ((java.lang.String)("UX")));
                   x10x.vector.Vector3d.fieldId$UY = x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10x.vector.Vector3d")),
                                                                                                         ((java.lang.String)("UY")));
                   x10x.vector.Vector3d.fieldId$UZ = x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10x.vector.Vector3d")),
                                                                                                         ((java.lang.String)("UZ")));
               }
    
}
