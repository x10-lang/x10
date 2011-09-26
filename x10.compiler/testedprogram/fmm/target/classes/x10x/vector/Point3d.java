package x10x.vector;


public class Point3d
extends x10.core.Struct
  implements x10x.vector.Tuple3d,
             x10.util.concurrent.Atomic,
              x10.x10rt.X10JavaSerializable 
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Point3d.class);
    
    public static final x10.rtt.RuntimeType<Point3d> $RTT = new x10.rtt.NamedType<Point3d>(
    "x10x.vector.Point3d", /* base class */Point3d.class
    , /* parents */ new x10.rtt.Type[] {x10x.vector.Tuple3d.$RTT, x10.rtt.Types.STRUCT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Point3d $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        $_obj.i = $deserializer.readDouble();
        $_obj.j = $deserializer.readDouble();
        $_obj.k = $deserializer.readDouble();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Point3d $_obj = new Point3d((java.lang.System[]) null);
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
    public Point3d(final java.lang.System $dummy) { this.i = 0.0; this.j = 0.0; this.k = 0.0; }
    // constructor just for allocation
    public Point3d(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
public double
          i;
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
public double
          j;
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
public double
          k;
        
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
public int
          X10$object_lock_id0;
        
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
public x10.util.concurrent.OrderedLock
                                                                                                      getOrderedLock(
                                                                                                      ){
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final int t25529 =
              this.
                X10$object_lock_id0;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10.util.concurrent.OrderedLock t25530 =
              x10.util.concurrent.OrderedLock.getLock((int)(t25529));
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
return t25530;
        }
        
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
public static int
          X10$class_lock_id1 =
          x10.util.concurrent.OrderedLock.createNewLockID();
        
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
public static x10.util.concurrent.OrderedLock
                                                                                                      getStaticOrderedLock(
                                                                                                      ){
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final int t25531 =
              x10x.vector.Point3d.X10$class_lock_id1;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10.util.concurrent.OrderedLock t25532 =
              x10.util.concurrent.OrderedLock.getLock((int)(t25531));
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
return t25532;
        }
        
        
//#line 10 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
// creation method for java code
        public static x10x.vector.Point3d $make(final double i,
                                                final double j,
                                                final double k){return new x10x.vector.Point3d((java.lang.System[]) null).$init(i,j,k);}
        
        // constructor for non-virtual call
        final public x10x.vector.Point3d x10x$vector$Point3d$$init$S(final double i,
                                                                     final double j,
                                                                     final double k) { {
                                                                                              
//#line 11 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
this.i = i;
                                                                                              this.j = j;
                                                                                              this.k = k;
                                                                                              
                                                                                              
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d this2501125683 =
                                                                                                this;
                                                                                              
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
this2501125683.X10$object_lock_id0 = -1;
                                                                                          }
                                                                                          return this;
                                                                                          }
        
        // constructor
        public x10x.vector.Point3d $init(final double i,
                                         final double j,
                                         final double k){return x10x$vector$Point3d$$init$S(i,j,k);}
        
        
        
//#line 10 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
// creation method for java code
        public static x10x.vector.Point3d $make(final double i,
                                                final double j,
                                                final double k,
                                                final x10.util.concurrent.OrderedLock paramLock){return new x10x.vector.Point3d((java.lang.System[]) null).$init(i,j,k,paramLock);}
        
        // constructor for non-virtual call
        final public x10x.vector.Point3d x10x$vector$Point3d$$init$S(final double i,
                                                                     final double j,
                                                                     final double k,
                                                                     final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                               
//#line 11 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
this.i = i;
                                                                                                                               this.j = j;
                                                                                                                               this.k = k;
                                                                                                                               
                                                                                                                               
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d this2501425684 =
                                                                                                                                 this;
                                                                                                                               
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
this2501425684.X10$object_lock_id0 = -1;
                                                                                                                               
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final int t25533 =
                                                                                                                                 paramLock.getIndex();
                                                                                                                               
//#line 10 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
this.X10$object_lock_id0 = ((int)(t25533));
                                                                                                                           }
                                                                                                                           return this;
                                                                                                                           }
        
        // constructor
        public x10x.vector.Point3d $init(final double i,
                                         final double j,
                                         final double k,
                                         final x10.util.concurrent.OrderedLock paramLock){return x10x$vector$Point3d$$init$S(i,j,k,paramLock);}
        
        
        
//#line 14 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
// creation method for java code
        public static x10x.vector.Point3d $make(final x10x.vector.Tuple3d t){return new x10x.vector.Point3d((java.lang.System[]) null).$init(t);}
        
        // constructor for non-virtual call
        final public x10x.vector.Point3d x10x$vector$Point3d$$init$S(final x10x.vector.Tuple3d t) { {
                                                                                                           
//#line 15 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25534 =
                                                                                                             t.i$O();
                                                                                                           
//#line 15 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25535 =
                                                                                                             t.j$O();
                                                                                                           
//#line 15 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25536 =
                                                                                                             t.k$O();
                                                                                                           
//#line 15 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
this.$init(((double)(t25534)),
                                                                                                                                                                                                                   ((double)(t25535)),
                                                                                                                                                                                                                   ((double)(t25536)));
                                                                                                       }
                                                                                                       return this;
                                                                                                       }
        
        // constructor
        public x10x.vector.Point3d $init(final x10x.vector.Tuple3d t){return x10x$vector$Point3d$$init$S(t);}
        
        
        
//#line 14 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
// creation method for java code
        public static x10x.vector.Point3d $make(final x10x.vector.Tuple3d t,
                                                final x10.util.concurrent.OrderedLock paramLock){return new x10x.vector.Point3d((java.lang.System[]) null).$init(t,paramLock);}
        
        // constructor for non-virtual call
        final public x10x.vector.Point3d x10x$vector$Point3d$$init$S(final x10x.vector.Tuple3d t,
                                                                     final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                               
//#line 15 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2553725685 =
                                                                                                                                 t.i$O();
                                                                                                                               
//#line 15 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2553825686 =
                                                                                                                                 t.j$O();
                                                                                                                               
//#line 15 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2553925687 =
                                                                                                                                 t.k$O();
                                                                                                                               
//#line 15 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
this.$init(((double)(t2553725685)),
                                                                                                                                                                                                                                       ((double)(t2553825686)),
                                                                                                                                                                                                                                       ((double)(t2553925687)));
                                                                                                                               
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final int t25540 =
                                                                                                                                 paramLock.getIndex();
                                                                                                                               
//#line 14 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
this.X10$object_lock_id0 = ((int)(t25540));
                                                                                                                           }
                                                                                                                           return this;
                                                                                                                           }
        
        // constructor
        public x10x.vector.Point3d $init(final x10x.vector.Tuple3d t,
                                         final x10.util.concurrent.OrderedLock paramLock){return x10x$vector$Point3d$$init$S(t,paramLock);}
        
        
        
//#line 18 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final public double
                                                                                                       i$O(
                                                                                                       ){
            
//#line 18 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25541 =
              i;
            
//#line 18 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
return t25541;
        }
        
        
//#line 19 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final public double
                                                                                                       j$O(
                                                                                                       ){
            
//#line 19 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25542 =
              j;
            
//#line 19 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
return t25542;
        }
        
        
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final public double
                                                                                                       k$O(
                                                                                                       ){
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25543 =
              k;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
return t25543;
        }
        
        
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final public java.lang.String
                                                                                                       toString(
                                                                                                       ){
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25544 =
              i;
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final java.lang.String t25545 =
              (("(") + ((x10.core.Double.$box(t25544))));
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final java.lang.String t25546 =
              ((t25545) + ("i + "));
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25547 =
              j;
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final java.lang.String t25548 =
              ((t25546) + ((x10.core.Double.$box(t25547))));
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final java.lang.String t25549 =
              ((t25548) + ("j + "));
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25550 =
              k;
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final java.lang.String t25551 =
              ((t25549) + ((x10.core.Double.$box(t25550))));
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final java.lang.String t25552 =
              ((t25551) + ("k)"));
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
return t25552;
        }
        
        
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final public x10x.vector.Point3d
                                                                                                       add(
                                                                                                       final x10x.vector.Vector3d b){
            
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d alloc24252 =
              new x10x.vector.Point3d((java.lang.System[]) null);
            
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2555325688 =
              i;
            
//#line 24 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t2555425689 =
              b.
                i;
            
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2555925690 =
              ((t2555325688) + (((double)(t2555425689))));
            
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2555525691 =
              j;
            
//#line 25 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t2555625692 =
              b.
                j;
            
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2556025693 =
              ((t2555525691) + (((double)(t2555625692))));
            
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2555725694 =
              k;
            
//#line 26 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t2555825695 =
              b.
                k;
            
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2556125696 =
              ((t2555725694) + (((double)(t2555825695))));
            
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10.util.concurrent.OrderedLock t2556225697 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
alloc24252.$init(t2555925690,
                                                                                                                          t2556025693,
                                                                                                                          t2556125696,
                                                                                                                          t2556225697);
            
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
return alloc24252;
        }
        
        
//#line 31 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final public x10x.vector.Point3d
                                                                                                       $plus(
                                                                                                       final x10x.vector.Vector3d that){
            
//#line 32 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d this25515 =
              ((x10x.vector.Point3d)(this));
            
//#line 27 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Vector3d b25513 =
              ((x10x.vector.Vector3d)(that));
            
//#line 28 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d alloc2425225514 =
              new x10x.vector.Point3d((java.lang.System[]) null);
            
//#line 28 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2556325698 =
              this25515.
                i;
            
//#line 24 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t2556425699 =
              b25513.
                i;
            
//#line 28 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2556925700 =
              ((t2556325698) + (((double)(t2556425699))));
            
//#line 28 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2556525701 =
              this25515.
                j;
            
//#line 25 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t2556625702 =
              b25513.
                j;
            
//#line 28 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2557025703 =
              ((t2556525701) + (((double)(t2556625702))));
            
//#line 28 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2556725704 =
              this25515.
                k;
            
//#line 26 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t2556825705 =
              b25513.
                k;
            
//#line 28 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2557125706 =
              ((t2556725704) + (((double)(t2556825705))));
            
//#line 28 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10.util.concurrent.OrderedLock t2557225707 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 28 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
alloc2425225514.$init(t2556925700,
                                                                                                                                 t2557025703,
                                                                                                                                 t2557125706,
                                                                                                                                 t2557225707);
            
//#line 32 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
return alloc2425225514;
        }
        
        
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final public x10x.vector.Point3d
                                                                                                       $times(
                                                                                                       final double that){
            
//#line 39 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d this25518 =
              ((x10x.vector.Point3d)(this));
            
//#line 42 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double c25516 =
              that;
            
//#line 43 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d alloc2425325517 =
              new x10x.vector.Point3d((java.lang.System[]) null);
            
//#line 43 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2557325708 =
              this25518.
                i;
            
//#line 43 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2557625709 =
              ((t2557325708) * (((double)(c25516))));
            
//#line 43 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2557425710 =
              this25518.
                j;
            
//#line 43 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2557725711 =
              ((t2557425710) * (((double)(c25516))));
            
//#line 43 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2557525712 =
              this25518.
                k;
            
//#line 43 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2557825713 =
              ((t2557525712) * (((double)(c25516))));
            
//#line 43 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10.util.concurrent.OrderedLock t2557925714 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 43 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
alloc2425325517.$init(t2557625709,
                                                                                                                                 t2557725711,
                                                                                                                                 t2557825713,
                                                                                                                                 t2557925714);
            
//#line 39 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
return alloc2425325517;
        }
        
        
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final public x10x.vector.Point3d
                                                                                                       scale(
                                                                                                       final double c){
            
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d alloc24253 =
              new x10x.vector.Point3d((java.lang.System[]) null);
            
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2558025715 =
              this.
                i;
            
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2558325716 =
              ((t2558025715) * (((double)(c))));
            
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2558125717 =
              this.
                j;
            
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2558425718 =
              ((t2558125717) * (((double)(c))));
            
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2558225719 =
              this.
                k;
            
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2558525720 =
              ((t2558225719) * (((double)(c))));
            
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10.util.concurrent.OrderedLock t2558625721 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
alloc24253.$init(t2558325716,
                                                                                                                          t2558425718,
                                                                                                                          t2558525720,
                                                                                                                          t2558625721);
            
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
return alloc24253;
        }
        
        
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final public x10x.vector.Vector3d
                                                                                                       vector(
                                                                                                       final x10x.vector.Point3d b){
            
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Vector3d alloc24254 =
              new x10x.vector.Vector3d((java.lang.System[]) null);
            
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2558725722 =
              i;
            
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2558825723 =
              b.
                i;
            
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2559325724 =
              ((t2558725722) - (((double)(t2558825723))));
            
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2558925725 =
              j;
            
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2559025726 =
              b.
                j;
            
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2559425727 =
              ((t2558925725) - (((double)(t2559025726))));
            
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2559125728 =
              k;
            
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2559225729 =
              b.
                k;
            
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2559525730 =
              ((t2559125728) - (((double)(t2559225729))));
            
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10.util.concurrent.OrderedLock t2559625731 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
alloc24254.$init(t2559325724,
                                                                                                                          t2559425727,
                                                                                                                          t2559525730,
                                                                                                                          t2559625731);
            
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
return alloc24254;
        }
        
        
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final public x10x.vector.Vector3d
                                                                                                       $minus(
                                                                                                       final x10x.vector.Point3d that){
            
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d this25521 =
              ((x10x.vector.Point3d)(this));
            
//#line 49 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d b25519 =
              ((x10x.vector.Point3d)(that));
            
//#line 50 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Vector3d alloc2425425520 =
              new x10x.vector.Vector3d((java.lang.System[]) null);
            
//#line 50 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2559725732 =
              this25521.
                i;
            
//#line 50 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2559825733 =
              b25519.
                i;
            
//#line 50 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2560325734 =
              ((t2559725732) - (((double)(t2559825733))));
            
//#line 50 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2559925735 =
              this25521.
                j;
            
//#line 50 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2560025736 =
              b25519.
                j;
            
//#line 50 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2560425737 =
              ((t2559925735) - (((double)(t2560025736))));
            
//#line 50 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2560125738 =
              this25521.
                k;
            
//#line 50 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2560225739 =
              b25519.
                k;
            
//#line 50 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2560525740 =
              ((t2560125738) - (((double)(t2560225739))));
            
//#line 50 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10.util.concurrent.OrderedLock t2560625741 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 50 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
alloc2425425520.$init(t2560325734,
                                                                                                                                 t2560425737,
                                                                                                                                 t2560525740,
                                                                                                                                 t2560625741);
            
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
return alloc2425425520;
        }
        
        
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final public double
                                                                                                       distanceSquared$O(
                                                                                                       final x10x.vector.Point3d b){
            
//#line 58 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25607 =
              i;
            
//#line 58 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25608 =
              b.
                i;
            
//#line 58 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double di =
              ((t25607) - (((double)(t25608))));
            
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25609 =
              j;
            
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25610 =
              b.
                j;
            
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double dj =
              ((t25609) - (((double)(t25610))));
            
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25611 =
              k;
            
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25612 =
              b.
                k;
            
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double dk =
              ((t25611) - (((double)(t25612))));
            
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25613 =
              ((di) * (((double)(di))));
            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25614 =
              ((dj) * (((double)(dj))));
            
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25615 =
              ((t25613) + (((double)(t25614))));
            
//#line 63 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25616 =
              ((dk) * (((double)(dk))));
            
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25617 =
              ((t25615) + (((double)(t25616))));
            
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
return t25617;
        }
        
        
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final public double
                                                                                                       distance$O(
                                                                                                       final x10x.vector.Point3d b){
            
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d this25526 =
              ((x10x.vector.Point3d)(this));
            
//#line 57 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d b25522 =
              ((x10x.vector.Point3d)(b));
            
//#line 57 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
double ret25527 =
               0;
            
//#line 58 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2561825742 =
              this25526.
                i;
            
//#line 58 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2561925743 =
              b25522.
                i;
            
//#line 58 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double di2552325744 =
              ((t2561825742) - (((double)(t2561925743))));
            
//#line 59 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2562025745 =
              this25526.
                j;
            
//#line 59 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2562125746 =
              b25522.
                j;
            
//#line 59 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double dj2552425747 =
              ((t2562025745) - (((double)(t2562125746))));
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2562225748 =
              this25526.
                k;
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2562325749 =
              b25522.
                k;
            
//#line 60 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double dk2552525750 =
              ((t2562225748) - (((double)(t2562325749))));
            
//#line 61 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2562425751 =
              ((di2552325744) * (((double)(di2552325744))));
            
//#line 62 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2562525752 =
              ((dj2552425747) * (((double)(dj2552425747))));
            
//#line 61 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2562625753 =
              ((t2562425751) + (((double)(t2562525752))));
            
//#line 63 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2562725754 =
              ((dk2552525750) * (((double)(dk2552525750))));
            
//#line 61 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2562825755 =
              ((t2562625753) + (((double)(t2562725754))));
            
//#line 61 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
ret25527 = t2562825755;
            
//#line 57 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25629 =
              ret25527;
            
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25630 =
              java.lang.Math.sqrt(((double)(t25629)));
            
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
return t25630;
        }
        
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final public java.lang.String
                                                                                                      typeName$O(
                                                                                                      ){try {return x10.rtt.Types.typeName(this);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final public int
                                                                                                      hashCode(
                                                                                                      ){
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
int result =
              1;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final int t25631 =
              result;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final int t25633 =
              ((8191) * (((int)(t25631))));
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25632 =
              this.
                i;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final int t25634 =
              x10.rtt.Types.hashCode(t25632);
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final int t25635 =
              ((t25633) + (((int)(t25634))));
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
result = t25635;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final int t25636 =
              result;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final int t25638 =
              ((8191) * (((int)(t25636))));
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25637 =
              this.
                j;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final int t25639 =
              x10.rtt.Types.hashCode(t25637);
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final int t25640 =
              ((t25638) + (((int)(t25639))));
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
result = t25640;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final int t25641 =
              result;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final int t25643 =
              ((8191) * (((int)(t25641))));
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25642 =
              this.
                k;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final int t25644 =
              x10.rtt.Types.hashCode(t25642);
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final int t25645 =
              ((t25643) + (((int)(t25644))));
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
result = t25645;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final int t25646 =
              result;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
return t25646;
        }
        
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final public boolean
                                                                                                      equals(
                                                                                                      java.lang.Object other){
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final java.lang.Object t25647 =
              other;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final boolean t25648 =
              x10x.vector.Point3d.$RTT.instanceOf(t25647);
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final boolean t25649 =
              !(t25648);
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
if (t25649) {
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
return false;
            }
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final java.lang.Object t25650 =
              other;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d t25651 =
              ((x10x.vector.Point3d)x10.rtt.Types.asStruct(x10x.vector.Point3d.$RTT,t25650));
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final boolean t25652 =
              this.equals(((x10x.vector.Point3d)(t25651)));
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
return t25652;
        }
        
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final public boolean
                                                                                                      equals(
                                                                                                      x10x.vector.Point3d other){
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25654 =
              this.
                i;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d t25653 =
              other;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25655 =
              t25653.
                i;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
boolean t25659 =
              ((double) t25654) ==
            ((double) t25655);
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
if (t25659) {
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25657 =
                  this.
                    j;
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d t25656 =
                  other;
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25658 =
                  t25656.
                    j;
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
t25659 = ((double) t25657) ==
                ((double) t25658);
            }
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
boolean t25663 =
              t25659;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
if (t25663) {
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25661 =
                  this.
                    k;
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d t25660 =
                  other;
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25662 =
                  t25660.
                    k;
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
t25663 = ((double) t25661) ==
                ((double) t25662);
            }
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final boolean t25664 =
              t25663;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
return t25664;
        }
        
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final public boolean
                                                                                                      _struct_equals$O(
                                                                                                      java.lang.Object other){
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final java.lang.Object t25665 =
              other;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final boolean t25666 =
              x10x.vector.Point3d.$RTT.instanceOf(t25665);
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final boolean t25667 =
              !(t25666);
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
if (t25667) {
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
return false;
            }
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final java.lang.Object t25668 =
              other;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d t25669 =
              ((x10x.vector.Point3d)x10.rtt.Types.asStruct(x10x.vector.Point3d.$RTT,t25668));
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final boolean t25670 =
              this._struct_equals$O(((x10x.vector.Point3d)(t25669)));
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
return t25670;
        }
        
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final public boolean
                                                                                                      _struct_equals$O(
                                                                                                      x10x.vector.Point3d other){
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25672 =
              this.
                i;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d t25671 =
              other;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25673 =
              t25671.
                i;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
boolean t25677 =
              ((double) t25672) ==
            ((double) t25673);
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
if (t25677) {
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25675 =
                  this.
                    j;
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d t25674 =
                  other;
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25676 =
                  t25674.
                    j;
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
t25677 = ((double) t25675) ==
                ((double) t25676);
            }
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
boolean t25681 =
              t25677;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
if (t25681) {
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25679 =
                  this.
                    k;
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d t25678 =
                  other;
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t25680 =
                  t25678.
                    k;
                
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
t25681 = ((double) t25679) ==
                ((double) t25680);
            }
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final boolean t25682 =
              t25681;
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
return t25682;
        }
        
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final public x10x.vector.Point3d
                                                                                                      x10x$vector$Point3d$$x10x$vector$Point3d$this(
                                                                                                      ){
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
return x10x.vector.Point3d.this;
        }
        
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final private void
                                                                                                      __fieldInitializers23972(
                                                                                                      ){
            
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
this.X10$object_lock_id0 = -1;
        }
        
        final public static void
          __fieldInitializers23972$P(
          final x10x.vector.Point3d Point3d){
            Point3d.__fieldInitializers23972();
        }
    
}
