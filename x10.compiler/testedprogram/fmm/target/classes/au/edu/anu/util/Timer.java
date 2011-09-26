package au.edu.anu.util;

final public class Timer
extends x10.core.Ref
  implements x10.util.concurrent.Atomic,
              x10.x10rt.X10JavaSerializable 
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Timer.class);
    
    public static final x10.rtt.RuntimeType<Timer> $RTT = new x10.rtt.NamedType<Timer>(
    "au.edu.anu.util.Timer", /* base class */Timer.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Timer $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        x10.array.Array total = (x10.array.Array) $deserializer.readRef();
        $_obj.total = total;
        x10.array.Array count = (x10.array.Array) $deserializer.readRef();
        $_obj.count = count;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Timer $_obj = new Timer((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        if (total instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.total);
        } else {
        $serializer.write(this.total);
        }
        if (count instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.count);
        } else {
        $serializer.write(this.count);
        }
        
    }
    
    // constructor just for allocation
    public Timer(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
public int
          X10$object_lock_id0;
        
        
//#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
public x10.util.concurrent.OrderedLock
                                                                                                    getOrderedLock(
                                                                                                    ){
            
//#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final int t35900 =
              this.
                X10$object_lock_id0;
            
//#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final x10.util.concurrent.OrderedLock t35901 =
              x10.util.concurrent.OrderedLock.getLock((int)(t35900));
            
//#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
return t35901;
        }
        
        
//#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
public static int
          X10$class_lock_id1 =
          x10.util.concurrent.OrderedLock.createNewLockID();
        
        
//#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
public static x10.util.concurrent.OrderedLock
                                                                                                    getStaticOrderedLock(
                                                                                                    ){
            
//#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final int t35902 =
              au.edu.anu.util.Timer.X10$class_lock_id1;
            
//#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final x10.util.concurrent.OrderedLock t35903 =
              x10.util.concurrent.OrderedLock.getLock((int)(t35902));
            
//#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
return t35903;
        }
        
        
//#line 6 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
public x10.array.Array<x10.core.Long>
          total;
        
//#line 7 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
public x10.array.Array<x10.core.Long>
          count;
        
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
// creation method for java code
        public static au.edu.anu.util.Timer $make(final int n){return new au.edu.anu.util.Timer((java.lang.System[]) null).$init(n);}
        
        // constructor for non-virtual call
        final public au.edu.anu.util.Timer au$edu$anu$util$Timer$$init$S(final int n) { {
                                                                                               
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"

                                                                                               
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"

                                                                                               
//#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final au.edu.anu.util.Timer this3570436043 =
                                                                                                 this;
                                                                                               
//#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
this3570436043.X10$object_lock_id0 = -1;
                                                                                               
//#line 10 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final x10.array.Array<x10.core.Long> alloc32101 =
                                                                                                 ((x10.array.Array)(new x10.array.Array<x10.core.Long>((java.lang.System[]) null, x10.rtt.Types.LONG)));
                                                                                               
//#line 243 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int size35707 =
                                                                                                 n;
                                                                                               
//#line 243 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32101.x10$lang$Object$$init$S();
                                                                                               
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectRegion1D alloc199703571036044 =
                                                                                                 ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null)));
                                                                                               
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t3590436005 =
                                                                                                 ((size35707) - (((int)(1))));
                                                                                               
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc199703571036044.$init(((int)(0)),
                                                                                                                                                                                                                          t3590436005);
                                                                                               
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region __desugarer__var__5__357093571436045 =
                                                                                                 ((x10.array.Region)(((x10.array.Region)
                                                                                                                       alloc199703571036044)));
                                                                                               
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Region ret3571536046 =
                                                                                                  null;
                                                                                               
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t3590536006 =
                                                                                                 __desugarer__var__5__357093571436045.
                                                                                                   rank;
                                                                                               
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3590736007 =
                                                                                                 ((int) t3590536006) ==
                                                                                               ((int) 1);
                                                                                               
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3590736007) {
                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3590636008 =
                                                                                                     __desugarer__var__5__357093571436045.
                                                                                                       zeroBased;
                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3590736007 = ((boolean) t3590636008) ==
                                                                                                   ((boolean) true);
                                                                                               }
                                                                                               
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3590936009 =
                                                                                                 t3590736007;
                                                                                               
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3590936009) {
                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3590836010 =
                                                                                                     __desugarer__var__5__357093571436045.
                                                                                                       rect;
                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3590936009 = ((boolean) t3590836010) ==
                                                                                                   ((boolean) true);
                                                                                               }
                                                                                               
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3591136011 =
                                                                                                 t3590936009;
                                                                                               
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3591136011) {
                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3591036012 =
                                                                                                     __desugarer__var__5__357093571436045.
                                                                                                       rail;
                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3591136011 = ((boolean) t3591036012) ==
                                                                                                   ((boolean) true);
                                                                                               }
                                                                                               
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3591236013 =
                                                                                                 t3591136011;
                                                                                               
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3591236013) {
                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3591236013 = ((__desugarer__var__5__357093571436045) != (null));
                                                                                               }
                                                                                               
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3591336014 =
                                                                                                 t3591236013;
                                                                                               
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3591636015 =
                                                                                                 !(t3591336014);
                                                                                               
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3591636015) {
                                                                                                   
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3591536016 =
                                                                                                     true;
                                                                                                   
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3591536016) {
                                                                                                       
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t3591436017 =
                                                                                                         new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==1, self.zeroBased==true, self.rect==true, self.rail==true, self!=null}");
                                                                                                       
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
throw t3591436017;
                                                                                                   }
                                                                                               }
                                                                                               
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret3571536046 = ((x10.array.Region)(__desugarer__var__5__357093571436045));
                                                                                               
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region myReg3570836047 =
                                                                                                 ((x10.array.Region)(ret3571536046));
                                                                                               
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32101.region = ((x10.array.Region)(myReg3570836047));
                                                                                               
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32101.rank = 1;
                                                                                               
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32101.rect = true;
                                                                                               
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32101.zeroBased = true;
                                                                                               
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32101.rail = true;
                                                                                               
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32101.size = size35707;
                                                                                               
//#line 249 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout alloc199713571136048 =
                                                                                                 new x10.array.RectLayout((java.lang.System[]) null);
                                                                                               
//#line 97 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int _max03571836049 =
                                                                                                 ((size35707) - (((int)(1))));
                                                                                               
//#line 98 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713571136048.rank = 1;
                                                                                               
//#line 99 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713571136048.min0 = 0;
                                                                                               
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3591736018 =
                                                                                                 ((_max03571836049) - (((int)(0))));
                                                                                               
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3591836019 =
                                                                                                 ((t3591736018) + (((int)(1))));
                                                                                               
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713571136048.delta0 = t3591836019;
                                                                                               
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3591936020 =
                                                                                                 alloc199713571136048.
                                                                                                   delta0;
                                                                                               
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final boolean t3592036021 =
                                                                                                 ((t3591936020) > (((int)(0))));
                                                                                               
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int t3592136022 =
                                                                                                  0;
                                                                                               
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
if (t3592036021) {
                                                                                                   
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t3592136022 = alloc199713571136048.
                                                                                                                                                                                                                         delta0;
                                                                                               } else {
                                                                                                   
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t3592136022 = 0;
                                                                                               }
                                                                                               
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3592236023 =
                                                                                                 t3592136022;
                                                                                               
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713571136048.size = t3592236023;
                                                                                               
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713571136048.min1 = 0;
                                                                                               
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713571136048.delta1 = 0;
                                                                                               
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713571136048.min2 = 0;
                                                                                               
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713571136048.delta2 = 0;
                                                                                               
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713571136048.min3 = 0;
                                                                                               
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713571136048.delta3 = 0;
                                                                                               
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713571136048.min = null;
                                                                                               
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713571136048.delta = null;
                                                                                               
//#line 249 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32101.layout = ((x10.array.RectLayout)(alloc199713571136048));
                                                                                               
//#line 250 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this3572036050 =
                                                                                                 ((x10.array.RectLayout)(((x10.array.Array<x10.core.Long>)alloc32101).
                                                                                                                           layout));
                                                                                               
//#line 250 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int n3571236051 =
                                                                                                 this3572036050.
                                                                                                   size;
                                                                                               
//#line 251 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Long> t3592336052 =
                                                                                                 ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<x10.core.Long>allocate(x10.rtt.Types.LONG, ((int)(n3571236051)), true)));
                                                                                               
//#line 251 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32101.raw = ((x10.core.IndexedMemoryChunk)(t3592336052));
                                                                                               
//#line 10 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
this.total = ((x10.array.Array)(alloc32101));
                                                                                               
//#line 11 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final x10.array.Array<x10.core.Long> alloc32102 =
                                                                                                 ((x10.array.Array)(new x10.array.Array<x10.core.Long>((java.lang.System[]) null, x10.rtt.Types.LONG)));
                                                                                               
//#line 243 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int size35721 =
                                                                                                 n;
                                                                                               
//#line 243 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32102.x10$lang$Object$$init$S();
                                                                                               
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectRegion1D alloc199703572436053 =
                                                                                                 ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null)));
                                                                                               
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t3592436024 =
                                                                                                 ((size35721) - (((int)(1))));
                                                                                               
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc199703572436053.$init(((int)(0)),
                                                                                                                                                                                                                          t3592436024);
                                                                                               
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region __desugarer__var__5__357233572836054 =
                                                                                                 ((x10.array.Region)(((x10.array.Region)
                                                                                                                       alloc199703572436053)));
                                                                                               
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Region ret3572936055 =
                                                                                                  null;
                                                                                               
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t3592536025 =
                                                                                                 __desugarer__var__5__357233572836054.
                                                                                                   rank;
                                                                                               
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3592736026 =
                                                                                                 ((int) t3592536025) ==
                                                                                               ((int) 1);
                                                                                               
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3592736026) {
                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3592636027 =
                                                                                                     __desugarer__var__5__357233572836054.
                                                                                                       zeroBased;
                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3592736026 = ((boolean) t3592636027) ==
                                                                                                   ((boolean) true);
                                                                                               }
                                                                                               
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3592936028 =
                                                                                                 t3592736026;
                                                                                               
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3592936028) {
                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3592836029 =
                                                                                                     __desugarer__var__5__357233572836054.
                                                                                                       rect;
                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3592936028 = ((boolean) t3592836029) ==
                                                                                                   ((boolean) true);
                                                                                               }
                                                                                               
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3593136030 =
                                                                                                 t3592936028;
                                                                                               
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3593136030) {
                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3593036031 =
                                                                                                     __desugarer__var__5__357233572836054.
                                                                                                       rail;
                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3593136030 = ((boolean) t3593036031) ==
                                                                                                   ((boolean) true);
                                                                                               }
                                                                                               
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3593236032 =
                                                                                                 t3593136030;
                                                                                               
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3593236032) {
                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3593236032 = ((__desugarer__var__5__357233572836054) != (null));
                                                                                               }
                                                                                               
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3593336033 =
                                                                                                 t3593236032;
                                                                                               
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3593636034 =
                                                                                                 !(t3593336033);
                                                                                               
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3593636034) {
                                                                                                   
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3593536035 =
                                                                                                     true;
                                                                                                   
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3593536035) {
                                                                                                       
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t3593436036 =
                                                                                                         new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==1, self.zeroBased==true, self.rect==true, self.rail==true, self!=null}");
                                                                                                       
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
throw t3593436036;
                                                                                                   }
                                                                                               }
                                                                                               
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret3572936055 = ((x10.array.Region)(__desugarer__var__5__357233572836054));
                                                                                               
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region myReg3572236056 =
                                                                                                 ((x10.array.Region)(ret3572936055));
                                                                                               
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32102.region = ((x10.array.Region)(myReg3572236056));
                                                                                               
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32102.rank = 1;
                                                                                               
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32102.rect = true;
                                                                                               
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32102.zeroBased = true;
                                                                                               
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32102.rail = true;
                                                                                               
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32102.size = size35721;
                                                                                               
//#line 249 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout alloc199713572536057 =
                                                                                                 new x10.array.RectLayout((java.lang.System[]) null);
                                                                                               
//#line 97 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int _max03573236058 =
                                                                                                 ((size35721) - (((int)(1))));
                                                                                               
//#line 98 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713572536057.rank = 1;
                                                                                               
//#line 99 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713572536057.min0 = 0;
                                                                                               
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3593736037 =
                                                                                                 ((_max03573236058) - (((int)(0))));
                                                                                               
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3593836038 =
                                                                                                 ((t3593736037) + (((int)(1))));
                                                                                               
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713572536057.delta0 = t3593836038;
                                                                                               
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3593936039 =
                                                                                                 alloc199713572536057.
                                                                                                   delta0;
                                                                                               
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final boolean t3594036040 =
                                                                                                 ((t3593936039) > (((int)(0))));
                                                                                               
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int t3594136041 =
                                                                                                  0;
                                                                                               
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
if (t3594036040) {
                                                                                                   
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t3594136041 = alloc199713572536057.
                                                                                                                                                                                                                         delta0;
                                                                                               } else {
                                                                                                   
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t3594136041 = 0;
                                                                                               }
                                                                                               
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3594236042 =
                                                                                                 t3594136041;
                                                                                               
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713572536057.size = t3594236042;
                                                                                               
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713572536057.min1 = 0;
                                                                                               
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713572536057.delta1 = 0;
                                                                                               
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713572536057.min2 = 0;
                                                                                               
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713572536057.delta2 = 0;
                                                                                               
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713572536057.min3 = 0;
                                                                                               
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713572536057.delta3 = 0;
                                                                                               
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713572536057.min = null;
                                                                                               
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713572536057.delta = null;
                                                                                               
//#line 249 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32102.layout = ((x10.array.RectLayout)(alloc199713572536057));
                                                                                               
//#line 250 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this3573436059 =
                                                                                                 ((x10.array.RectLayout)(((x10.array.Array<x10.core.Long>)alloc32102).
                                                                                                                           layout));
                                                                                               
//#line 250 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int n3572636060 =
                                                                                                 this3573436059.
                                                                                                   size;
                                                                                               
//#line 251 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Long> t3594336061 =
                                                                                                 ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<x10.core.Long>allocate(x10.rtt.Types.LONG, ((int)(n3572636060)), true)));
                                                                                               
//#line 251 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32102.raw = ((x10.core.IndexedMemoryChunk)(t3594336061));
                                                                                               
//#line 11 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
this.count = ((x10.array.Array)(alloc32102));
                                                                                           }
                                                                                           return this;
                                                                                           }
        
        // constructor
        public au.edu.anu.util.Timer $init(final int n){return au$edu$anu$util$Timer$$init$S(n);}
        
        
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
// creation method for java code
        public static au.edu.anu.util.Timer $make(final int n,
                                                  final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.util.Timer((java.lang.System[]) null).$init(n,paramLock);}
        
        // constructor for non-virtual call
        final public au.edu.anu.util.Timer au$edu$anu$util$Timer$$init$S(final int n,
                                                                         final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                   
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"

                                                                                                                                   
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"

                                                                                                                                   
//#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final au.edu.anu.util.Timer this3573536100 =
                                                                                                                                     this;
                                                                                                                                   
//#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
this3573536100.X10$object_lock_id0 = -1;
                                                                                                                                   
//#line 10 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final x10.array.Array<x10.core.Long> alloc32103 =
                                                                                                                                     ((x10.array.Array)(new x10.array.Array<x10.core.Long>((java.lang.System[]) null, x10.rtt.Types.LONG)));
                                                                                                                                   
//#line 243 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int size35738 =
                                                                                                                                     n;
                                                                                                                                   
//#line 243 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32103.x10$lang$Object$$init$S();
                                                                                                                                   
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectRegion1D alloc199703574136101 =
                                                                                                                                     ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null)));
                                                                                                                                   
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t3594436062 =
                                                                                                                                     ((size35738) - (((int)(1))));
                                                                                                                                   
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc199703574136101.$init(((int)(0)),
                                                                                                                                                                                                                                                              t3594436062);
                                                                                                                                   
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region __desugarer__var__5__357403574536102 =
                                                                                                                                     ((x10.array.Region)(((x10.array.Region)
                                                                                                                                                           alloc199703574136101)));
                                                                                                                                   
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Region ret3574636103 =
                                                                                                                                      null;
                                                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t3594536063 =
                                                                                                                                     __desugarer__var__5__357403574536102.
                                                                                                                                       rank;
                                                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3594736064 =
                                                                                                                                     ((int) t3594536063) ==
                                                                                                                                   ((int) 1);
                                                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3594736064) {
                                                                                                                                       
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3594636065 =
                                                                                                                                         __desugarer__var__5__357403574536102.
                                                                                                                                           zeroBased;
                                                                                                                                       
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3594736064 = ((boolean) t3594636065) ==
                                                                                                                                       ((boolean) true);
                                                                                                                                   }
                                                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3594936066 =
                                                                                                                                     t3594736064;
                                                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3594936066) {
                                                                                                                                       
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3594836067 =
                                                                                                                                         __desugarer__var__5__357403574536102.
                                                                                                                                           rect;
                                                                                                                                       
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3594936066 = ((boolean) t3594836067) ==
                                                                                                                                       ((boolean) true);
                                                                                                                                   }
                                                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3595136068 =
                                                                                                                                     t3594936066;
                                                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3595136068) {
                                                                                                                                       
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3595036069 =
                                                                                                                                         __desugarer__var__5__357403574536102.
                                                                                                                                           rail;
                                                                                                                                       
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3595136068 = ((boolean) t3595036069) ==
                                                                                                                                       ((boolean) true);
                                                                                                                                   }
                                                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3595236070 =
                                                                                                                                     t3595136068;
                                                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3595236070) {
                                                                                                                                       
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3595236070 = ((__desugarer__var__5__357403574536102) != (null));
                                                                                                                                   }
                                                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3595336071 =
                                                                                                                                     t3595236070;
                                                                                                                                   
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3595636072 =
                                                                                                                                     !(t3595336071);
                                                                                                                                   
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3595636072) {
                                                                                                                                       
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3595536073 =
                                                                                                                                         true;
                                                                                                                                       
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3595536073) {
                                                                                                                                           
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t3595436074 =
                                                                                                                                             new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==1, self.zeroBased==true, self.rect==true, self.rail==true, self!=null}");
                                                                                                                                           
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
throw t3595436074;
                                                                                                                                       }
                                                                                                                                   }
                                                                                                                                   
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret3574636103 = ((x10.array.Region)(__desugarer__var__5__357403574536102));
                                                                                                                                   
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region myReg3573936104 =
                                                                                                                                     ((x10.array.Region)(ret3574636103));
                                                                                                                                   
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32103.region = ((x10.array.Region)(myReg3573936104));
                                                                                                                                   
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32103.rank = 1;
                                                                                                                                   
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32103.rect = true;
                                                                                                                                   
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32103.zeroBased = true;
                                                                                                                                   
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32103.rail = true;
                                                                                                                                   
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32103.size = size35738;
                                                                                                                                   
//#line 249 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout alloc199713574236105 =
                                                                                                                                     new x10.array.RectLayout((java.lang.System[]) null);
                                                                                                                                   
//#line 97 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int _max03574936106 =
                                                                                                                                     ((size35738) - (((int)(1))));
                                                                                                                                   
//#line 98 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713574236105.rank = 1;
                                                                                                                                   
//#line 99 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713574236105.min0 = 0;
                                                                                                                                   
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3595736075 =
                                                                                                                                     ((_max03574936106) - (((int)(0))));
                                                                                                                                   
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3595836076 =
                                                                                                                                     ((t3595736075) + (((int)(1))));
                                                                                                                                   
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713574236105.delta0 = t3595836076;
                                                                                                                                   
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3595936077 =
                                                                                                                                     alloc199713574236105.
                                                                                                                                       delta0;
                                                                                                                                   
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final boolean t3596036078 =
                                                                                                                                     ((t3595936077) > (((int)(0))));
                                                                                                                                   
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int t3596136079 =
                                                                                                                                      0;
                                                                                                                                   
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
if (t3596036078) {
                                                                                                                                       
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t3596136079 = alloc199713574236105.
                                                                                                                                                                                                                                                             delta0;
                                                                                                                                   } else {
                                                                                                                                       
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t3596136079 = 0;
                                                                                                                                   }
                                                                                                                                   
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3596236080 =
                                                                                                                                     t3596136079;
                                                                                                                                   
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713574236105.size = t3596236080;
                                                                                                                                   
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713574236105.min1 = 0;
                                                                                                                                   
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713574236105.delta1 = 0;
                                                                                                                                   
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713574236105.min2 = 0;
                                                                                                                                   
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713574236105.delta2 = 0;
                                                                                                                                   
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713574236105.min3 = 0;
                                                                                                                                   
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713574236105.delta3 = 0;
                                                                                                                                   
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713574236105.min = null;
                                                                                                                                   
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713574236105.delta = null;
                                                                                                                                   
//#line 249 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32103.layout = ((x10.array.RectLayout)(alloc199713574236105));
                                                                                                                                   
//#line 250 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this3575136107 =
                                                                                                                                     ((x10.array.RectLayout)(((x10.array.Array<x10.core.Long>)alloc32103).
                                                                                                                                                               layout));
                                                                                                                                   
//#line 250 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int n3574336108 =
                                                                                                                                     this3575136107.
                                                                                                                                       size;
                                                                                                                                   
//#line 251 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Long> t3596336109 =
                                                                                                                                     ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<x10.core.Long>allocate(x10.rtt.Types.LONG, ((int)(n3574336108)), true)));
                                                                                                                                   
//#line 251 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32103.raw = ((x10.core.IndexedMemoryChunk)(t3596336109));
                                                                                                                                   
//#line 10 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
this.total = ((x10.array.Array)(alloc32103));
                                                                                                                                   
//#line 11 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final x10.array.Array<x10.core.Long> alloc32104 =
                                                                                                                                     ((x10.array.Array)(new x10.array.Array<x10.core.Long>((java.lang.System[]) null, x10.rtt.Types.LONG)));
                                                                                                                                   
//#line 243 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int size35752 =
                                                                                                                                     n;
                                                                                                                                   
//#line 243 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32104.x10$lang$Object$$init$S();
                                                                                                                                   
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectRegion1D alloc199703575536110 =
                                                                                                                                     ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null)));
                                                                                                                                   
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t3596436081 =
                                                                                                                                     ((size35752) - (((int)(1))));
                                                                                                                                   
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc199703575536110.$init(((int)(0)),
                                                                                                                                                                                                                                                              t3596436081);
                                                                                                                                   
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region __desugarer__var__5__357543575936111 =
                                                                                                                                     ((x10.array.Region)(((x10.array.Region)
                                                                                                                                                           alloc199703575536110)));
                                                                                                                                   
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Region ret3576036112 =
                                                                                                                                      null;
                                                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t3596536082 =
                                                                                                                                     __desugarer__var__5__357543575936111.
                                                                                                                                       rank;
                                                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3596736083 =
                                                                                                                                     ((int) t3596536082) ==
                                                                                                                                   ((int) 1);
                                                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3596736083) {
                                                                                                                                       
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3596636084 =
                                                                                                                                         __desugarer__var__5__357543575936111.
                                                                                                                                           zeroBased;
                                                                                                                                       
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3596736083 = ((boolean) t3596636084) ==
                                                                                                                                       ((boolean) true);
                                                                                                                                   }
                                                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3596936085 =
                                                                                                                                     t3596736083;
                                                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3596936085) {
                                                                                                                                       
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3596836086 =
                                                                                                                                         __desugarer__var__5__357543575936111.
                                                                                                                                           rect;
                                                                                                                                       
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3596936085 = ((boolean) t3596836086) ==
                                                                                                                                       ((boolean) true);
                                                                                                                                   }
                                                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3597136087 =
                                                                                                                                     t3596936085;
                                                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3597136087) {
                                                                                                                                       
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3597036088 =
                                                                                                                                         __desugarer__var__5__357543575936111.
                                                                                                                                           rail;
                                                                                                                                       
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3597136087 = ((boolean) t3597036088) ==
                                                                                                                                       ((boolean) true);
                                                                                                                                   }
                                                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3597236089 =
                                                                                                                                     t3597136087;
                                                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3597236089) {
                                                                                                                                       
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3597236089 = ((__desugarer__var__5__357543575936111) != (null));
                                                                                                                                   }
                                                                                                                                   
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3597336090 =
                                                                                                                                     t3597236089;
                                                                                                                                   
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3597636091 =
                                                                                                                                     !(t3597336090);
                                                                                                                                   
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3597636091) {
                                                                                                                                       
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3597536092 =
                                                                                                                                         true;
                                                                                                                                       
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3597536092) {
                                                                                                                                           
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t3597436093 =
                                                                                                                                             new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==1, self.zeroBased==true, self.rect==true, self.rail==true, self!=null}");
                                                                                                                                           
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
throw t3597436093;
                                                                                                                                       }
                                                                                                                                   }
                                                                                                                                   
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret3576036112 = ((x10.array.Region)(__desugarer__var__5__357543575936111));
                                                                                                                                   
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region myReg3575336113 =
                                                                                                                                     ((x10.array.Region)(ret3576036112));
                                                                                                                                   
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32104.region = ((x10.array.Region)(myReg3575336113));
                                                                                                                                   
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32104.rank = 1;
                                                                                                                                   
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32104.rect = true;
                                                                                                                                   
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32104.zeroBased = true;
                                                                                                                                   
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32104.rail = true;
                                                                                                                                   
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32104.size = size35752;
                                                                                                                                   
//#line 249 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout alloc199713575636114 =
                                                                                                                                     new x10.array.RectLayout((java.lang.System[]) null);
                                                                                                                                   
//#line 97 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int _max03576336115 =
                                                                                                                                     ((size35752) - (((int)(1))));
                                                                                                                                   
//#line 98 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713575636114.rank = 1;
                                                                                                                                   
//#line 99 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713575636114.min0 = 0;
                                                                                                                                   
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3597736094 =
                                                                                                                                     ((_max03576336115) - (((int)(0))));
                                                                                                                                   
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3597836095 =
                                                                                                                                     ((t3597736094) + (((int)(1))));
                                                                                                                                   
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713575636114.delta0 = t3597836095;
                                                                                                                                   
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3597936096 =
                                                                                                                                     alloc199713575636114.
                                                                                                                                       delta0;
                                                                                                                                   
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final boolean t3598036097 =
                                                                                                                                     ((t3597936096) > (((int)(0))));
                                                                                                                                   
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int t3598136098 =
                                                                                                                                      0;
                                                                                                                                   
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
if (t3598036097) {
                                                                                                                                       
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t3598136098 = alloc199713575636114.
                                                                                                                                                                                                                                                             delta0;
                                                                                                                                   } else {
                                                                                                                                       
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t3598136098 = 0;
                                                                                                                                   }
                                                                                                                                   
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3598236099 =
                                                                                                                                     t3598136098;
                                                                                                                                   
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713575636114.size = t3598236099;
                                                                                                                                   
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713575636114.min1 = 0;
                                                                                                                                   
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713575636114.delta1 = 0;
                                                                                                                                   
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713575636114.min2 = 0;
                                                                                                                                   
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713575636114.delta2 = 0;
                                                                                                                                   
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713575636114.min3 = 0;
                                                                                                                                   
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713575636114.delta3 = 0;
                                                                                                                                   
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713575636114.min = null;
                                                                                                                                   
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713575636114.delta = null;
                                                                                                                                   
//#line 249 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32104.layout = ((x10.array.RectLayout)(alloc199713575636114));
                                                                                                                                   
//#line 250 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this3576536116 =
                                                                                                                                     ((x10.array.RectLayout)(((x10.array.Array<x10.core.Long>)alloc32104).
                                                                                                                                                               layout));
                                                                                                                                   
//#line 250 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int n3575736117 =
                                                                                                                                     this3576536116.
                                                                                                                                       size;
                                                                                                                                   
//#line 251 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Long> t3598336118 =
                                                                                                                                     ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<x10.core.Long>allocate(x10.rtt.Types.LONG, ((int)(n3575736117)), true)));
                                                                                                                                   
//#line 251 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc32104.raw = ((x10.core.IndexedMemoryChunk)(t3598336118));
                                                                                                                                   
//#line 11 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
this.count = ((x10.array.Array)(alloc32104));
                                                                                                                                   
//#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final int t35984 =
                                                                                                                                     paramLock.getIndex();
                                                                                                                                   
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
this.X10$object_lock_id0 = ((int)(t35984));
                                                                                                                               }
                                                                                                                               return this;
                                                                                                                               }
        
        // constructor
        public au.edu.anu.util.Timer $init(final int n,
                                           final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$util$Timer$$init$S(n,paramLock);}
        
        
        
//#line 14 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
public void
                                                                                                     start(
                                                                                                     final int id){
            
//#line 14 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final x10.array.Array<x10.core.Long> x35799 =
              ((x10.array.Array)(total));
            
//#line 14 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final int y035800 =
              id;
            
//#line 14 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final long z35801 =
              java.lang.System.nanoTime();
            
//#line 14 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
long ret35810 =
               0;
            
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i0357663580336120 =
              y035800;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
long ret357673580436121 =
               0;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret357683580536122: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Long> t3598536123 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Long>)x35799).
                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final long t3598636124 =
              ((long[])t3598536123.value)[i0357663580336120];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret357673580436121 = t3598636124;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret357683580536122;}
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final long t3598736125 =
              ret357673580436121;
            
//#line 14 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final long r3580236126 =
              ((t3598736125) - (((long)(z35801))));
            
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i0357833580636127 =
              y035800;
            
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final long v357843580736128 =
              r3580236126;
            
//#line 508 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
long ret357853580836129 =
               0;
            
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Long> t3598836119 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Long>)x35799).
                                               raw));
            
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((long[])t3598836119.value)[i0357833580636127] = v357843580736128;
            
//#line 519 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret357853580836129 = v357843580736128;
            
//#line 14 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
ret35810 = r3580236126;
        }
        
        
//#line 15 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
public void
                                                                                                     clear(
                                                                                                     final int id){
            
//#line 15 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final x10.array.Array<x10.core.Long> this35814 =
              ((x10.array.Array)(total));
            
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i035812 =
              id;
            
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final long v35813 =
              ((long)(((int)(0))));
            
//#line 508 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
long ret35815 =
               0;
            
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Long> t3598936130 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Long>)this35814).
                                               raw));
            
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((long[])t3598936130.value)[i035812] = v35813;
            
//#line 519 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret35815 = v35813;
        }
        
        
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
public void
                                                                                                     stop(
                                                                                                     final int id){
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final x10.array.Array<x10.core.Long> x35839 =
              ((x10.array.Array)(total));
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final int y035840 =
              id;
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final long z35841 =
              java.lang.System.nanoTime();
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
long ret35850 =
               0;
            
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i0358223584336133 =
              y035840;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
long ret358233584436134 =
               0;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret358243584536135: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Long> t3599036136 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Long>)x35839).
                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final long t3599136137 =
              ((long[])t3599036136.value)[i0358223584336133];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret358233584436134 = t3599136137;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret358243584536135;}
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final long t3599236138 =
              ret358233584436134;
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final long r3584236139 =
              ((t3599236138) + (((long)(z35841))));
            
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i0358303584636140 =
              y035840;
            
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final long v358313584736141 =
              r3584236139;
            
//#line 508 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
long ret358323584836142 =
               0;
            
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Long> t3599336131 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Long>)x35839).
                                               raw));
            
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((long[])t3599336131.value)[i0358303584636140] = v358313584736141;
            
//#line 519 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret358323584836142 = v358313584736141;
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
ret35850 = r3584236139;
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final x10.array.Array<x10.core.Long> x35869 =
              ((x10.array.Array)(count));
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final int y035870 =
              id;
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
long ret35880 =
               0;
            
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i0358523587336143 =
              y035870;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
long ret358533587436144 =
               0;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret358543587536145: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Long> t3599436146 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Long>)x35869).
                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final long t3599536147 =
              ((long[])t3599436146.value)[i0358523587336143];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret358533587436144 = t3599536147;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret358543587536145;}
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final long t3599636148 =
              ret358533587436144;
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final long r3587236149 =
              ((t3599636148) + (((long)(1L))));
            
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i0358603587636150 =
              y035870;
            
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final long v358613587736151 =
              r3587236149;
            
//#line 508 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
long ret358623587836152 =
               0;
            
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Long> t3599736132 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Long>)x35869).
                                               raw));
            
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((long[])t3599736132.value)[i0358603587636150] = v358613587736151;
            
//#line 519 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret358623587836152 = v358613587736151;
            
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
ret35880 = r3587236149;
        }
        
        
//#line 17 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
public long
                                                                                                     mean$O(
                                                                                                     final int id){
            
//#line 17 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final x10.array.Array<x10.core.Long> this35883 =
              ((x10.array.Array)(total));
            
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i035882 =
              id;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
long ret35884 =
               0;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret35885: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Long> t35998 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Long>)this35883).
                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final long t35999 =
              ((long[])t35998.value)[i035882];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret35884 = t35999;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret35885;}
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final long t36002 =
              ret35884;
            
//#line 17 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final x10.array.Array<x10.core.Long> this35892 =
              ((x10.array.Array)(count));
            
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i035891 =
              id;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
long ret35893 =
               0;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret35894: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Long> t36000 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Long>)this35892).
                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final long t36001 =
              ((long[])t36000.value)[i035891];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret35893 = t36001;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret35894;}
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final long t36003 =
              ret35893;
            
//#line 17 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final long t36004 =
              ((t36002) / (((long)(t36003))));
            
//#line 17 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
return t36004;
        }
        
        
//#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final public au.edu.anu.util.Timer
                                                                                                    au$edu$anu$util$Timer$$au$edu$anu$util$Timer$this(
                                                                                                    ){
            
//#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
return au.edu.anu.util.Timer.this;
        }
        
        
//#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
final private void
                                                                                                    __fieldInitializers32074(
                                                                                                    ){
            
//#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10"
this.X10$object_lock_id0 = -1;
        }
        
        final public static void
          __fieldInitializers32074$P(
          final au.edu.anu.util.Timer Timer){
            Timer.__fieldInitializers32074();
        }
    
}
