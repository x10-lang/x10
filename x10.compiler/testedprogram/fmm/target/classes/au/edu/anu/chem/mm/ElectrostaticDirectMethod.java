package au.edu.anu.chem.mm;


public class ElectrostaticDirectMethod
extends x10.core.Ref
  implements x10.util.concurrent.Atomic,
              x10.x10rt.X10JavaSerializable 
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, ElectrostaticDirectMethod.class);
    
    public static final x10.rtt.RuntimeType<ElectrostaticDirectMethod> $RTT = new x10.rtt.NamedType<ElectrostaticDirectMethod>(
    "au.edu.anu.chem.mm.ElectrostaticDirectMethod", /* base class */ElectrostaticDirectMethod.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(ElectrostaticDirectMethod $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        au.edu.anu.util.Timer timer = (au.edu.anu.util.Timer) $deserializer.readRef();
        $_obj.timer = timer;
        $_obj.asyncComms = $deserializer.readBoolean();
        x10.array.DistArray atoms = (x10.array.DistArray) $deserializer.readRef();
        $_obj.atoms = atoms;
        x10.array.DistArray otherAtoms = (x10.array.DistArray) $deserializer.readRef();
        $_obj.otherAtoms = otherAtoms;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        ElectrostaticDirectMethod $_obj = new ElectrostaticDirectMethod((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        if (timer instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.timer);
        } else {
        $serializer.write(this.timer);
        }
        $serializer.write(this.asyncComms);
        if (atoms instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.atoms);
        } else {
        $serializer.write(this.atoms);
        }
        if (otherAtoms instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.otherAtoms);
        } else {
        $serializer.write(this.otherAtoms);
        }
        
    }
    
    // constructor just for allocation
    public ElectrostaticDirectMethod(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
public int
          X10$object_lock_id0;
        
        
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
public x10.util.concurrent.OrderedLock
                                                                                                                            getOrderedLock(
                                                                                                                            ){
            
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int t32499 =
              this.
                X10$object_lock_id0;
            
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.util.concurrent.OrderedLock t32500 =
              x10.util.concurrent.OrderedLock.getLock((int)(t32499));
            
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
return t32500;
        }
        
        
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
public static int
          X10$class_lock_id1 =
          x10.util.concurrent.OrderedLock.createNewLockID();
        
        
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
public static x10.util.concurrent.OrderedLock
                                                                                                                            getStaticOrderedLock(
                                                                                                                            ){
            
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int t32501 =
              au.edu.anu.chem.mm.ElectrostaticDirectMethod.X10$class_lock_id1;
            
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.util.concurrent.OrderedLock t32502 =
              x10.util.concurrent.OrderedLock.getLock((int)(t32501));
            
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
return t32502;
        }
        
        
//#line 29 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final public static int
          TIMER_INDEX_TOTAL =
          0;
        
//#line 31 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
/** A multi-timer for the several segments of a single getEnergy invocation, indexed by the constants above. */public au.edu.anu.util.Timer
          timer;
        
//#line 33 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
public boolean
          asyncComms;
        
//#line 36 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
/** The charges in the simulation, divided up into an array of ValRails, one for each place. */public x10.array.DistArray<x10.array.Array<au.edu.anu.chem.PointCharge>>
          atoms;
        
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
public x10.array.DistArray<x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>>
          otherAtoms;
        
        
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
// creation method for java code
        public static au.edu.anu.chem.mm.ElectrostaticDirectMethod $make(final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> atoms,java.lang.Class<?> $dummy0){return new au.edu.anu.chem.mm.ElectrostaticDirectMethod((java.lang.System[]) null).$init(atoms,(java.lang.Class<?>) null);}
        
        // constructor for non-virtual call
        final public au.edu.anu.chem.mm.ElectrostaticDirectMethod au$edu$anu$chem$mm$ElectrostaticDirectMethod$$init$S(final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> atoms,java.lang.Class<?> $dummy0) { {
                                                                                                                                                                                                                                        
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"

                                                                                                                                                                                                                                        
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"

                                                                                                                                                                                                                                        
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
this.__fieldInitializers26285();
                                                                                                                                                                                                                                        
//#line 140 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
final x10.array.Dist dist31994 =
                                                                                                                                                                                                                                          ((x10.array.Dist)(x10.array.Dist.makeUnique()));
                                                                                                                                                                                                                                        
//#line 45 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.core.fun.Fun_0_1<x10.array.Point,x10.array.Array<au.edu.anu.chem.PointCharge>> t32539 =
                                                                                                                                                                                                                                          ((x10.core.fun.Fun_0_1)(new au.edu.anu.chem.mm.ElectrostaticDirectMethod.$Closure$1(atoms,(java.lang.Class<?>) null)));
                                                                                                                                                                                                                                        
//#line 140 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
final x10.core.fun.Fun_0_1<x10.array.Point,x10.array.Array<au.edu.anu.chem.PointCharge>> init31995 =
                                                                                                                                                                                                                                          ((x10.core.fun.Fun_0_1)(((x10.core.fun.Fun_0_1<x10.array.Point,x10.array.Array<au.edu.anu.chem.PointCharge>>)(x10.core.fun.Fun_0_1)
                                                                                                                                                                                                                                                                    t32539)));
                                                                                                                                                                                                                                        
//#line 140 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.PointCharge>> alloc3102331996 =
                                                                                                                                                                                                                                          ((x10.array.DistArray)(new x10.array.DistArray<x10.array.Array<au.edu.anu.chem.PointCharge>>((java.lang.System[]) null, new x10.rtt.ParameterizedType(x10.array.Array.$RTT, au.edu.anu.chem.PointCharge.$RTT))));
                                                                                                                                                                                                                                        
//#line 140 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
alloc3102331996.$init(((x10.array.Dist)(dist31994)),
                                                                                                                                                                                                                                                                                                                                                                  ((x10.core.fun.Fun_0_1<x10.array.Point,x10.array.Array<au.edu.anu.chem.PointCharge>>)(init31995)),(java.lang.Class<?>) null);
                                                                                                                                                                                                                                        
//#line 44 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
this.atoms = ((x10.array.DistArray)(alloc3102331996));
                                                                                                                                                                                                                                        
//#line 140 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
final x10.array.Dist dist32011 =
                                                                                                                                                                                                                                          ((x10.array.Dist)(x10.array.Dist.makeUnique()));
                                                                                                                                                                                                                                        
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.core.fun.Fun_0_1<x10.array.Point,x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>> t32560 =
                                                                                                                                                                                                                                          ((x10.core.fun.Fun_0_1)(new au.edu.anu.chem.mm.ElectrostaticDirectMethod.$Closure$2()));
                                                                                                                                                                                                                                        
//#line 140 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
final x10.core.fun.Fun_0_1<x10.array.Point,x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>> init32012 =
                                                                                                                                                                                                                                          ((x10.core.fun.Fun_0_1)(((x10.core.fun.Fun_0_1<x10.array.Point,x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>>)(x10.core.fun.Fun_0_1)
                                                                                                                                                                                                                                                                    t32560)));
                                                                                                                                                                                                                                        
//#line 140 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
final x10.array.DistArray<x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>> alloc3102332013 =
                                                                                                                                                                                                                                          ((x10.array.DistArray)(new x10.array.DistArray<x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>>((java.lang.System[]) null, new x10.rtt.ParameterizedType(x10.array.Array.$RTT, new x10.rtt.ParameterizedType(x10.array.Array.$RTT, au.edu.anu.chem.PointCharge.$RTT)))));
                                                                                                                                                                                                                                        
//#line 140 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
alloc3102332013.$init(((x10.array.Dist)(dist32011)),
                                                                                                                                                                                                                                                                                                                                                                  ((x10.core.fun.Fun_0_1<x10.array.Point,x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>>)(init32012)),(java.lang.Class<?>) null);
                                                                                                                                                                                                                                        
//#line 47 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
this.otherAtoms = ((x10.array.DistArray)(alloc3102332013));
                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                    return this;
                                                                                                                                                                                                                                    }
        
        // constructor
        public au.edu.anu.chem.mm.ElectrostaticDirectMethod $init(final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> atoms,java.lang.Class<?> $dummy0){return au$edu$anu$chem$mm$ElectrostaticDirectMethod$$init$S(atoms, $dummy0);}
        
        
        
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
// creation method for java code
        public static au.edu.anu.chem.mm.ElectrostaticDirectMethod $make(final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> atoms,
                                                                         final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.chem.mm.ElectrostaticDirectMethod((java.lang.System[]) null).$init(atoms,paramLock);}
        
        // constructor for non-virtual call
        final public au.edu.anu.chem.mm.ElectrostaticDirectMethod au$edu$anu$chem$mm$ElectrostaticDirectMethod$$init$S(final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> atoms,
                                                                                                                       final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                                                                 
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"

                                                                                                                                                                                 
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"

                                                                                                                                                                                 
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
this.__fieldInitializers26285();
                                                                                                                                                                                 
//#line 140 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
final x10.array.Dist dist32052 =
                                                                                                                                                                                   ((x10.array.Dist)(x10.array.Dist.makeUnique()));
                                                                                                                                                                                 
//#line 45 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.core.fun.Fun_0_1<x10.array.Point,x10.array.Array<au.edu.anu.chem.PointCharge>> t32597 =
                                                                                                                                                                                   ((x10.core.fun.Fun_0_1)(new au.edu.anu.chem.mm.ElectrostaticDirectMethod.$Closure$4(atoms,(java.lang.Class<?>) null)));
                                                                                                                                                                                 
//#line 140 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
final x10.core.fun.Fun_0_1<x10.array.Point,x10.array.Array<au.edu.anu.chem.PointCharge>> init32053 =
                                                                                                                                                                                   ((x10.core.fun.Fun_0_1)(((x10.core.fun.Fun_0_1<x10.array.Point,x10.array.Array<au.edu.anu.chem.PointCharge>>)(x10.core.fun.Fun_0_1)
                                                                                                                                                                                                             t32597)));
                                                                                                                                                                                 
//#line 140 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.PointCharge>> alloc3102332054 =
                                                                                                                                                                                   ((x10.array.DistArray)(new x10.array.DistArray<x10.array.Array<au.edu.anu.chem.PointCharge>>((java.lang.System[]) null, new x10.rtt.ParameterizedType(x10.array.Array.$RTT, au.edu.anu.chem.PointCharge.$RTT))));
                                                                                                                                                                                 
//#line 140 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
alloc3102332054.$init(((x10.array.Dist)(dist32052)),
                                                                                                                                                                                                                                                                                                           ((x10.core.fun.Fun_0_1<x10.array.Point,x10.array.Array<au.edu.anu.chem.PointCharge>>)(init32053)),(java.lang.Class<?>) null);
                                                                                                                                                                                 
//#line 44 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
this.atoms = ((x10.array.DistArray)(alloc3102332054));
                                                                                                                                                                                 
//#line 140 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
final x10.array.Dist dist32069 =
                                                                                                                                                                                   ((x10.array.Dist)(x10.array.Dist.makeUnique()));
                                                                                                                                                                                 
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.core.fun.Fun_0_1<x10.array.Point,x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>> t32618 =
                                                                                                                                                                                   ((x10.core.fun.Fun_0_1)(new au.edu.anu.chem.mm.ElectrostaticDirectMethod.$Closure$5()));
                                                                                                                                                                                 
//#line 140 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
final x10.core.fun.Fun_0_1<x10.array.Point,x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>> init32070 =
                                                                                                                                                                                   ((x10.core.fun.Fun_0_1)(((x10.core.fun.Fun_0_1<x10.array.Point,x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>>)(x10.core.fun.Fun_0_1)
                                                                                                                                                                                                             t32618)));
                                                                                                                                                                                 
//#line 140 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
final x10.array.DistArray<x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>> alloc3102332071 =
                                                                                                                                                                                   ((x10.array.DistArray)(new x10.array.DistArray<x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>>((java.lang.System[]) null, new x10.rtt.ParameterizedType(x10.array.Array.$RTT, new x10.rtt.ParameterizedType(x10.array.Array.$RTT, au.edu.anu.chem.PointCharge.$RTT)))));
                                                                                                                                                                                 
//#line 140 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
alloc3102332071.$init(((x10.array.Dist)(dist32069)),
                                                                                                                                                                                                                                                                                                           ((x10.core.fun.Fun_0_1<x10.array.Point,x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>>)(init32070)),(java.lang.Class<?>) null);
                                                                                                                                                                                 
//#line 47 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
this.otherAtoms = ((x10.array.DistArray)(alloc3102332071));
                                                                                                                                                                                 
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int t32619 =
                                                                                                                                                                                   paramLock.getIndex();
                                                                                                                                                                                 
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
this.X10$object_lock_id0 = ((int)(t32619));
                                                                                                                                                                             }
                                                                                                                                                                             return this;
                                                                                                                                                                             }
        
        // constructor
        public au.edu.anu.chem.mm.ElectrostaticDirectMethod $init(final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> atoms,
                                                                  final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$chem$mm$ElectrostaticDirectMethod$$init$S(atoms,paramLock);}
        
        
        
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
public double
                                                                                                                            getEnergy$O(
                                                                                                                            ){
            
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final au.edu.anu.util.Timer t32620 =
              ((au.edu.anu.util.Timer)(timer));
            
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
t32620.start((int)(0));
            
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final double directEnergy;
            {
                
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.lang.FinishState x10$__var0 =
                  ((x10.lang.FinishState)(x10.lang.Runtime.<x10.core.Double>startCollectingFinish_0_$_x10$lang$Runtime_T_$(x10.rtt.Types.DOUBLE, ((x10.lang.Reducible)((new java.io.Serializable() { au.edu.anu.chem.mm.ElectrostaticDirectMethod.SumReducer eval() {
                                                                                                                               
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final au.edu.anu.chem.mm.ElectrostaticDirectMethod.SumReducer alloc26497 =
                                                                                                                                 new au.edu.anu.chem.mm.ElectrostaticDirectMethod.SumReducer((java.lang.System[]) null);
                                                                                                                               {
                                                                                                                                   
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.util.concurrent.OrderedLock t32622 =
                                                                                                                                     x10.util.concurrent.OrderedLock.createNewLock();
                                                                                                                                   
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
alloc26497.$init(t32622);
                                                                                                                               }
                                                                                                                               return alloc26497;
                                                                                                                           } }.eval()))))));
                
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
try {try {{
                    {
                        
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.PointCharge>> atoms =
                          ((x10.array.DistArray)(this.
                                                   atoms));
                        
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.array.DistArray<x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>> otherAtoms =
                          ((x10.array.DistArray)(this.
                                                   otherAtoms));
                        {
                            {
                                
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
x10.lang.Runtime.ensureNotInAtomic();
                                
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.array.Dist __lowerer__var__0__ =
                                  ((x10.array.Dist)(((x10.array.DistArray<x10.array.Array<au.edu.anu.chem.PointCharge>>)atoms).
                                                      dist));
                                
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
for (
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.lang.Iterator<x10.lang.Place> __lowerer__var__1__33258 =
                                                                                                                                                         ((x10.lang.Iterable<x10.lang.Place>)__lowerer__var__0__.places()).iterator();
                                                                                                                                                       ((x10.lang.Iterator<x10.lang.Place>)__lowerer__var__1__33258).hasNext$O();
                                                                                                                                                       ) {
                                    
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.lang.Place __lowerer__var__1__ =
                                      ((x10.lang.Iterator<x10.lang.Place>)__lowerer__var__1__33258).next$G();
                                    
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
x10.lang.Runtime.runAsync(((x10.lang.Place)(__lowerer__var__1__)),
                                                                                                                                                                                ((x10.core.fun.VoidFun_0_0)(new au.edu.anu.chem.mm.ElectrostaticDirectMethod.$Closure$9(__lowerer__var__0__,
                                                                                                                                                                                                                                                                        atoms,
                                                                                                                                                                                                                                                                        otherAtoms,(java.lang.Class<?>) null))));
                                }
                            }
                        }
                    }
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__2__) {
                    
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__2__)));
                    
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
throw new x10.lang.RuntimeException();
                }finally {{
                     
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
directEnergy = x10.core.Double.$unbox(x10.lang.Runtime.<x10.core.Double>stopCollectingFinish$G(x10.rtt.Types.DOUBLE, ((x10.lang.FinishState)(x10$__var0))));
                 }}
                }
            
//#line 147 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final au.edu.anu.util.Timer t32789 =
              ((au.edu.anu.util.Timer)(timer));
            
//#line 147 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
t32789.stop((int)(0));
            
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final double t32791 =
              ((directEnergy) / (((double)(2.0))));
            
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
return t32791;
            }
        
        
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
public static class SumReducer
                                                                                                                           extends x10.core.Struct
                                                                                                                             implements x10.lang.Reducible,
                                                                                                                                        x10.util.concurrent.Atomic,
                                                                                                                                         x10.x10rt.X10JavaSerializable 
                                                                                                                           {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, SumReducer.class);
            
            public static final x10.rtt.RuntimeType<SumReducer> $RTT = new x10.rtt.NamedType<SumReducer>(
            "au.edu.anu.chem.mm.ElectrostaticDirectMethod.SumReducer", /* base class */SumReducer.class
            , /* parents */ new x10.rtt.Type[] {new x10.rtt.ParameterizedType(x10.lang.Reducible.$RTT, x10.rtt.Types.DOUBLE), x10.rtt.Types.STRUCT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(SumReducer $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                SumReducer $_obj = new SumReducer((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // zero value constructor
            public SumReducer(final java.lang.System $dummy) { }
            // constructor just for allocation
            public SumReducer(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public x10.lang.Reducible.operator()(T,T):T
            public java.lang.Object
              $apply(final java.lang.Object a1,final x10.rtt.Type t1,
            final java.lang.Object a2,final x10.rtt.Type t2){return x10.core.Double.$box($apply$O(x10.core.Double.$unbox(a1),
            x10.core.Double.$unbox(a2)));}
            // bridge for method abstract public x10.lang.Reducible.zero():T
            final public x10.core.Double
              zero$G(){return x10.core.Double.$box(zero$O());}
            
                
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
public int
                  X10$object_lock_id0;
                
                
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
public x10.util.concurrent.OrderedLock
                                                                                                                                     getOrderedLock(
                                                                                                                                     ){
                    
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int t32792 =
                      this.
                        X10$object_lock_id0;
                    
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.util.concurrent.OrderedLock t32793 =
                      x10.util.concurrent.OrderedLock.getLock((int)(t32792));
                    
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
return t32793;
                }
                
                
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
public static int
                  X10$class_lock_id1 =
                  x10.util.concurrent.OrderedLock.createNewLockID();
                
                
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
public static x10.util.concurrent.OrderedLock
                                                                                                                                     getStaticOrderedLock(
                                                                                                                                     ){
                    
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int t32794 =
                      au.edu.anu.chem.mm.ElectrostaticDirectMethod.SumReducer.X10$class_lock_id1;
                    
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.util.concurrent.OrderedLock t32795 =
                      x10.util.concurrent.OrderedLock.getLock((int)(t32794));
                    
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
return t32795;
                }
                
                
//#line 152 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final public double
                                                                                                                                     zero$O(
                                                                                                                                     ){
                    
//#line 152 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
return 0.0;
                }
                
                
//#line 153 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final public double
                                                                                                                                     $apply$O(
                                                                                                                                     final double a,
                                                                                                                                     final double b){
                    
//#line 153 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final double t32796 =
                      ((a) + (((double)(b))));
                    
//#line 153 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
return t32796;
                }
                
                
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final public java.lang.String
                                                                                                                                     typeName$O(
                                                                                                                                     ){try {return x10.rtt.Types.typeName(this);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
                
                
                
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final public java.lang.String
                                                                                                                                     toString(
                                                                                                                                     ){
                    
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
return "struct au.edu.anu.chem.mm.ElectrostaticDirectMethod.SumReducer";
                }
                
                
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final public int
                                                                                                                                     hashCode(
                                                                                                                                     ){
                    
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
int result =
                      1;
                    
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int t32797 =
                      result;
                    
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
return t32797;
                }
                
                
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final public boolean
                                                                                                                                     equals(
                                                                                                                                     java.lang.Object other){
                    
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final java.lang.Object t32798 =
                      other;
                    
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final boolean t32799 =
                      au.edu.anu.chem.mm.ElectrostaticDirectMethod.SumReducer.$RTT.instanceOf(t32798);
                    
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final boolean t32800 =
                      !(t32799);
                    
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
if (t32800) {
                        
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
return false;
                    }
                    
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
return true;
                }
                
                
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final public boolean
                                                                                                                                     equals(
                                                                                                                                     au.edu.anu.chem.mm.ElectrostaticDirectMethod.SumReducer other){
                    
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
return true;
                }
                
                
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final public boolean
                                                                                                                                     _struct_equals$O(
                                                                                                                                     java.lang.Object other){
                    
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final java.lang.Object t32801 =
                      other;
                    
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final boolean t32802 =
                      au.edu.anu.chem.mm.ElectrostaticDirectMethod.SumReducer.$RTT.instanceOf(t32801);
                    
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final boolean t32803 =
                      !(t32802);
                    
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
if (t32803) {
                        
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
return false;
                    }
                    
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
return true;
                }
                
                
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final public boolean
                                                                                                                                     _struct_equals$O(
                                                                                                                                     au.edu.anu.chem.mm.ElectrostaticDirectMethod.SumReducer other){
                    
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
return true;
                }
                
                
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final public au.edu.anu.chem.mm.ElectrostaticDirectMethod.SumReducer
                                                                                                                                     au$edu$anu$chem$mm$ElectrostaticDirectMethod$SumReducer$$au$edu$anu$chem$mm$ElectrostaticDirectMethod$SumReducer$this(
                                                                                                                                     ){
                    
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
return au.edu.anu.chem.mm.ElectrostaticDirectMethod.SumReducer.this;
                }
                
                
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
// creation method for java code
                public static au.edu.anu.chem.mm.ElectrostaticDirectMethod.SumReducer $make(){return new au.edu.anu.chem.mm.ElectrostaticDirectMethod.SumReducer((java.lang.System[]) null).$init();}
                
                // constructor for non-virtual call
                final public au.edu.anu.chem.mm.ElectrostaticDirectMethod.SumReducer au$edu$anu$chem$mm$ElectrostaticDirectMethod$SumReducer$$init$S() { {
                                                                                                                                                                
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"

                                                                                                                                                                
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final au.edu.anu.chem.mm.ElectrostaticDirectMethod.SumReducer this3249333228 =
                                                                                                                                                                  this;
                                                                                                                                                                
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
this3249333228.X10$object_lock_id0 = -1;
                                                                                                                                                            }
                                                                                                                                                            return this;
                                                                                                                                                            }
                
                // constructor
                public au.edu.anu.chem.mm.ElectrostaticDirectMethod.SumReducer $init(){return au$edu$anu$chem$mm$ElectrostaticDirectMethod$SumReducer$$init$S();}
                
                
                
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
// creation method for java code
                public static au.edu.anu.chem.mm.ElectrostaticDirectMethod.SumReducer $make(final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.chem.mm.ElectrostaticDirectMethod.SumReducer((java.lang.System[]) null).$init(paramLock);}
                
                // constructor for non-virtual call
                final public au.edu.anu.chem.mm.ElectrostaticDirectMethod.SumReducer au$edu$anu$chem$mm$ElectrostaticDirectMethod$SumReducer$$init$S(final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                                                                                               
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"

                                                                                                                                                                                                               
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final au.edu.anu.chem.mm.ElectrostaticDirectMethod.SumReducer this3249633229 =
                                                                                                                                                                                                                 this;
                                                                                                                                                                                                               
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
this3249633229.X10$object_lock_id0 = -1;
                                                                                                                                                                                                               
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int t32804 =
                                                                                                                                                                                                                 paramLock.getIndex();
                                                                                                                                                                                                               
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
this.X10$object_lock_id0 = ((int)(t32804));
                                                                                                                                                                                                           }
                                                                                                                                                                                                           return this;
                                                                                                                                                                                                           }
                
                // constructor
                public au.edu.anu.chem.mm.ElectrostaticDirectMethod.SumReducer $init(final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$chem$mm$ElectrostaticDirectMethod$SumReducer$$init$S(paramLock);}
                
                
                
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final private void
                                                                                                                                     __fieldInitializers26284(
                                                                                                                                     ){
                    
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
this.X10$object_lock_id0 = -1;
                }
                
                final public static void
                  __fieldInitializers26284$P(
                  final au.edu.anu.chem.mm.ElectrostaticDirectMethod.SumReducer SumReducer){
                    SumReducer.__fieldInitializers26284();
                }
            
        }
        
        
        
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final public au.edu.anu.chem.mm.ElectrostaticDirectMethod
                                                                                                                            au$edu$anu$chem$mm$ElectrostaticDirectMethod$$au$edu$anu$chem$mm$ElectrostaticDirectMethod$this(
                                                                                                                            ){
            
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
return au.edu.anu.chem.mm.ElectrostaticDirectMethod.this;
        }
        
        
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final private void
                                                                                                                            __fieldInitializers26285(
                                                                                                                            ){
            
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
this.X10$object_lock_id0 = -1;
            
//#line 31 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final au.edu.anu.util.Timer alloc26498 =
              ((au.edu.anu.util.Timer)(new au.edu.anu.util.Timer((java.lang.System[]) null)));
            
//#line 31 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.util.concurrent.OrderedLock t3280533230 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 31 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
alloc26498.$init(((int)(1)),
                                                                                                                                               t3280533230);
            
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
this.timer = ((au.edu.anu.util.Timer)(alloc26498));
            
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
this.asyncComms = true;
        }
        
        final public static void
          __fieldInitializers26285$P(
          final au.edu.anu.chem.mm.ElectrostaticDirectMethod ElectrostaticDirectMethod){
            ElectrostaticDirectMethod.__fieldInitializers26285();
        }
        
        public static int
          getInitialized$TIMER_INDEX_TOTAL(
          ){
            return au.edu.anu.chem.mm.ElectrostaticDirectMethod.TIMER_INDEX_TOTAL;
        }
        
        public static class $Closure$0
        extends x10.core.Ref
          implements x10.core.fun.Fun_0_1,
                      x10.x10rt.X10JavaSerializable 
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$0.class);
            
            public static final x10.rtt.RuntimeType<$Closure$0> $RTT = new x10.rtt.StaticFunType<$Closure$0>(
            /* base class */$Closure$0.class
            , /* parents */ new x10.rtt.Type[] {new x10.rtt.ParameterizedType(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, au.edu.anu.chem.PointCharge.$RTT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$0 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                x10.array.DistArray atoms = (x10.array.DistArray) $deserializer.readRef();
                $_obj.atoms = atoms;
                $_obj.i = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$0 $_obj = new $Closure$0((java.lang.System[]) null);
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
                $serializer.write(this.i);
                
            }
            
            // constructor just for allocation
            public $Closure$0(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object
              $apply(final java.lang.Object a1,final x10.rtt.Type t1){return $apply(x10.core.Int.$unbox(a1));}
            
                
                public au.edu.anu.chem.PointCharge
                  $apply(
                  final int j){
                    
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final au.edu.anu.chem.PointCharge alloc26491 =
                      ((au.edu.anu.chem.PointCharge)(new au.edu.anu.chem.PointCharge((java.lang.System[]) null)));
                    
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.array.Array<au.edu.anu.chem.mm.MMAtom> this3106532806 =
                      ((x10.array.Array)(((x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>>)this.
                                                                                                             atoms).$apply$G((int)(this.
                                                                                                                                     i))));
                    
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i03106432807 =
                      j;
                    
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
au.edu.anu.chem.mm.MMAtom ret3106632808 =
                       null;
                    
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret3106732809: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.chem.mm.MMAtom> t3250432810 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<au.edu.anu.chem.mm.MMAtom>)this3106532806).
                                                       raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.chem.mm.MMAtom t3250532811 =
                      ((au.edu.anu.chem.mm.MMAtom)(((au.edu.anu.chem.mm.MMAtom[])t3250432810.value)[i03106432807]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret3106632808 = t3250532811;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret3106732809;}
                    
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.chem.mm.MMAtom t3250632812 =
                      ret3106632808;
                    
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10x.vector.Point3d t3251032813 =
                      ((x10x.vector.Point3d)(t3250632812.
                                               centre));
                    
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.array.Array<au.edu.anu.chem.mm.MMAtom> this3107432814 =
                      ((x10.array.Array)(((x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>>)this.
                                                                                                             atoms).$apply$G((int)(this.
                                                                                                                                     i))));
                    
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i03107332815 =
                      j;
                    
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
au.edu.anu.chem.mm.MMAtom ret3107532816 =
                       null;
                    
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret3107632817: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.chem.mm.MMAtom> t3250732818 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<au.edu.anu.chem.mm.MMAtom>)this3107432814).
                                                       raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.chem.mm.MMAtom t3250832819 =
                      ((au.edu.anu.chem.mm.MMAtom)(((au.edu.anu.chem.mm.MMAtom[])t3250732818.value)[i03107332815]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret3107532816 = t3250832819;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret3107632817;}
                    
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.chem.mm.MMAtom t3250932820 =
                      ret3107532816;
                    
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final double t3251132821 =
                      t3250932820.
                        charge;
                    
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.util.concurrent.OrderedLock t3251232822 =
                      x10.util.concurrent.OrderedLock.createNewLock();
                    
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
alloc26491.$init(t3251032813,
                                                                                                                                                       t3251132821,
                                                                                                                                                       t3251232822);
                    
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
return alloc26491;
                }
                
                public x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>>
                  atoms;
                public int
                  i;
                
                // creation method for java code
                public static au.edu.anu.chem.mm.ElectrostaticDirectMethod.$Closure$0 $make(final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> atoms,
                                                                                            final int i,java.lang.Class<?> $dummy0){return new $Closure$0(atoms,i,(java.lang.Class<?>) null);}
                public $Closure$0(final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> atoms,
                                  final int i,java.lang.Class<?> $dummy0) { {
                                                                                   this.atoms = ((x10.array.DistArray)(atoms));
                                                                                   this.i = i;
                                                                               }}
                
            }
            
        public static class $Closure$1
        extends x10.core.Ref
          implements x10.core.fun.Fun_0_1,
                      x10.x10rt.X10JavaSerializable 
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$1.class);
            
            public static final x10.rtt.RuntimeType<$Closure$1> $RTT = new x10.rtt.StaticFunType<$Closure$1>(
            /* base class */$Closure$1.class
            , /* parents */ new x10.rtt.Type[] {new x10.rtt.ParameterizedType(x10.core.fun.Fun_0_1.$RTT, x10.array.Point.$RTT, new x10.rtt.ParameterizedType(x10.array.Array.$RTT, au.edu.anu.chem.PointCharge.$RTT)), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$1 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                x10.array.DistArray atoms = (x10.array.DistArray) $deserializer.readRef();
                $_obj.atoms = atoms;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$1 $_obj = new $Closure$1((java.lang.System[]) null);
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
                
            }
            
            // constructor just for allocation
            public $Closure$1(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object
              $apply(final java.lang.Object a1,final x10.rtt.Type t1){return $apply((x10.array.Point)a1);}
            
                
                public x10.array.Array<au.edu.anu.chem.PointCharge>
                  $apply(
                  final x10.array.Point id2){
                    
//#line 45 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int i =
                      id2.$apply$O((int)(0));
                    
//#line 45 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.array.Array<au.edu.anu.chem.PointCharge> alloc26492 =
                      ((x10.array.Array)(new x10.array.Array<au.edu.anu.chem.PointCharge>((java.lang.System[]) null, au.edu.anu.chem.PointCharge.$RTT)));
                    
//#line 45 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.array.Array<au.edu.anu.chem.mm.MMAtom> t32503 =
                      ((x10.array.Array)(((x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>>)this.
                                                                                                             atoms).$apply$G((int)(i))));
                    
//#line 271 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int size31161 =
                      ((x10.array.Array<au.edu.anu.chem.mm.MMAtom>)t32503).
                        size;
                    
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,au.edu.anu.chem.PointCharge> t32513 =
                      ((x10.core.fun.Fun_0_1)(new au.edu.anu.chem.mm.ElectrostaticDirectMethod.$Closure$0(((x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>>)(this.
                                                                                                                                                                               atoms)),
                                                                                                          i,(java.lang.Class<?>) null)));
                    
//#line 271 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,au.edu.anu.chem.PointCharge> init31162 =
                      ((x10.core.fun.Fun_0_1)(((x10.core.fun.Fun_0_1<x10.core.Int,au.edu.anu.chem.PointCharge>)(x10.core.fun.Fun_0_1)
                                                t32513)));
                    
//#line 271 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26492.x10$lang$Object$$init$S();
                    
//#line 273 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectRegion1D alloc199723116532850 =
                      ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null)));
                    
//#line 273 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t3251432830 =
                      ((size31161) - (((int)(1))));
                    
//#line 273 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc199723116532850.$init(((int)(0)),
                                                                                                                                               t3251432830);
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region __desugarer__var__6__311643198732851 =
                      ((x10.array.Region)(((x10.array.Region)
                                            alloc199723116532850)));
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Region ret3198832852 =
                       null;
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3251532831 =
                      __desugarer__var__6__311643198732851.
                        zeroBased;
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3251732832 =
                      ((boolean) t3251532831) ==
                    ((boolean) true);
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3251732832) {
                        
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3251632833 =
                          __desugarer__var__6__311643198732851.
                            rail;
                        
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3251732832 = ((boolean) t3251632833) ==
                        ((boolean) true);
                    }
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3251932834 =
                      t3251732832;
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3251932834) {
                        
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t3251832835 =
                          __desugarer__var__6__311643198732851.
                            rank;
                        
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3251932834 = ((int) t3251832835) ==
                        ((int) 1);
                    }
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3252132836 =
                      t3251932834;
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3252132836) {
                        
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3252032837 =
                          __desugarer__var__6__311643198732851.
                            rect;
                        
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3252132836 = ((boolean) t3252032837) ==
                        ((boolean) true);
                    }
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3252232838 =
                      t3252132836;
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3252232838) {
                        
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3252232838 = ((__desugarer__var__6__311643198732851) != (null));
                    }
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3252332839 =
                      t3252232838;
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3252632840 =
                      !(t3252332839);
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3252632840) {
                        
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3252532841 =
                          true;
                        
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3252532841) {
                            
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t3252432842 =
                              new x10.lang.FailedDynamicCheckException("x10.array.Region{self.zeroBased==true, self.rail==true, self.rank==1, self.rect==true, self!=null}");
                            
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
throw t3252432842;
                        }
                    }
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret3198832852 = ((x10.array.Region)(__desugarer__var__6__311643198732851));
                    
//#line 273 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region myReg3116332853 =
                      ((x10.array.Region)(ret3198832852));
                    
//#line 274 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26492.region = ((x10.array.Region)(myReg3116332853));
                    
//#line 274 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26492.rank = 1;
                    
//#line 274 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26492.rect = true;
                    
//#line 274 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26492.zeroBased = true;
                    
//#line 274 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26492.rail = true;
                    
//#line 274 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26492.size = size31161;
                    
//#line 276 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout alloc199733116632854 =
                      new x10.array.RectLayout((java.lang.System[]) null);
                    
//#line 97 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int _max03199132855 =
                      ((size31161) - (((int)(1))));
                    
//#line 98 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199733116632854.rank = 1;
                    
//#line 99 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199733116632854.min0 = 0;
                    
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3252732843 =
                      ((_max03199132855) - (((int)(0))));
                    
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3252832844 =
                      ((t3252732843) + (((int)(1))));
                    
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199733116632854.delta0 = t3252832844;
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3252932845 =
                      alloc199733116632854.
                        delta0;
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final boolean t3253032846 =
                      ((t3252932845) > (((int)(0))));
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int t3253132847 =
                       0;
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
if (t3253032846) {
                        
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t3253132847 = alloc199733116632854.
                                                                                                                                              delta0;
                    } else {
                        
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t3253132847 = 0;
                    }
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3253232848 =
                      t3253132847;
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199733116632854.size = t3253232848;
                    
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199733116632854.min1 = 0;
                    
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199733116632854.delta1 = 0;
                    
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199733116632854.min2 = 0;
                    
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199733116632854.delta2 = 0;
                    
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199733116632854.min3 = 0;
                    
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199733116632854.delta3 = 0;
                    
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199733116632854.min = null;
                    
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199733116632854.delta = null;
                    
//#line 276 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26492.layout = ((x10.array.RectLayout)(alloc199733116632854));
                    
//#line 277 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this3199332856 =
                      ((x10.array.RectLayout)(((x10.array.Array<au.edu.anu.chem.PointCharge>)alloc26492).
                                                layout));
                    
//#line 277 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int n3116732857 =
                      this3199332856.
                        size;
                    
//#line 278 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.chem.PointCharge> r3116832858 =
                      x10.core.IndexedMemoryChunk.<au.edu.anu.chem.PointCharge>allocate(au.edu.anu.chem.PointCharge.$RTT, ((int)(n3116732857)), false);
                    
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i19990max199923117032849 =
                      ((size31161) - (((int)(1))));
                    
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int i199903117132827 =
                      0;
                    {
                        
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.chem.PointCharge[] r3116832858$value33262 =
                          ((au.edu.anu.chem.PointCharge[])r3116832858.value);
                        
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
for (;
                                                                                                                             true;
                                                                                                                             ) {
                            
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t3253432828 =
                              i199903117132827;
                            
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3253832829 =
                              ((t3253432828) <= (((int)(i19990max199923117032849))));
                            
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (!(t3253832829)) {
                                
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break;
                            }
                            
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i3117232824 =
                              i199903117132827;
                            
//#line 280 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.chem.PointCharge t3253732823 =
                              ((au.edu.anu.chem.PointCharge)(((au.edu.anu.chem.PointCharge)x10.rtt.Types.asStruct(au.edu.anu.chem.PointCharge.$RTT,((x10.core.fun.Fun_0_1<x10.core.Int,au.edu.anu.chem.PointCharge>)init31162).$apply(x10.core.Int.$box(i3117232824),x10.rtt.Types.INT)))));
                            
//#line 280 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
r3116832858$value33262[i3117232824]=t3253732823;
                            
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t3253532825 =
                              i199903117132827;
                            
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t3253632826 =
                              ((t3253532825) + (((int)(1))));
                            
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
i199903117132827 = t3253632826;
                        }
                    }
                    
//#line 282 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26492.raw = ((x10.core.IndexedMemoryChunk)(r3116832858));
                    
//#line 45 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
return alloc26492;
                }
                
                public x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>>
                  atoms;
                
                // creation method for java code
                public static au.edu.anu.chem.mm.ElectrostaticDirectMethod.$Closure$1 $make(final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> atoms,java.lang.Class<?> $dummy0){return new $Closure$1(atoms,(java.lang.Class<?>) null);}
                public $Closure$1(final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> atoms,java.lang.Class<?> $dummy0) { {
                                                                                                                                                   this.atoms = ((x10.array.DistArray)(atoms));
                                                                                                                                               }}
                
            }
            
        public static class $Closure$2
        extends x10.core.Ref
          implements x10.core.fun.Fun_0_1,
                      x10.x10rt.X10JavaSerializable 
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$2.class);
            
            public static final x10.rtt.RuntimeType<$Closure$2> $RTT = new x10.rtt.StaticFunType<$Closure$2>(
            /* base class */$Closure$2.class
            , /* parents */ new x10.rtt.Type[] {new x10.rtt.ParameterizedType(x10.core.fun.Fun_0_1.$RTT, x10.array.Point.$RTT, new x10.rtt.ParameterizedType(x10.array.Array.$RTT, new x10.rtt.ParameterizedType(x10.array.Array.$RTT, au.edu.anu.chem.PointCharge.$RTT))), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$2 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$2 $_obj = new $Closure$2((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public $Closure$2(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object
              $apply(final java.lang.Object a1,final x10.rtt.Type t1){return $apply((x10.array.Point)a1);}
            
                
                public x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>
                  $apply(
                  final x10.array.Point id3){
                    
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int p =
                      id3.$apply$O((int)(0));
                    
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>> alloc26493 =
                      ((x10.array.Array)(new x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>((java.lang.System[]) null, new x10.rtt.ParameterizedType(x10.array.Array.$RTT, au.edu.anu.chem.PointCharge.$RTT))));
                    
//#line 243 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int size31997 =
                      x10.lang.Place.getInitialized$MAX_PLACES();
                    
//#line 243 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26493.x10$lang$Object$$init$S();
                    
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectRegion1D alloc199703200032878 =
                      ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null)));
                    
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t3254032859 =
                      ((size31997) - (((int)(1))));
                    
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc199703200032878.$init(((int)(0)),
                                                                                                                                               t3254032859);
                    
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region __desugarer__var__5__319993200432879 =
                      ((x10.array.Region)(((x10.array.Region)
                                            alloc199703200032878)));
                    
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Region ret3200532880 =
                       null;
                    
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t3254132860 =
                      __desugarer__var__5__319993200432879.
                        rank;
                    
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3254332861 =
                      ((int) t3254132860) ==
                    ((int) 1);
                    
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3254332861) {
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3254232862 =
                          __desugarer__var__5__319993200432879.
                            zeroBased;
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3254332861 = ((boolean) t3254232862) ==
                        ((boolean) true);
                    }
                    
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3254532863 =
                      t3254332861;
                    
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3254532863) {
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3254432864 =
                          __desugarer__var__5__319993200432879.
                            rect;
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3254532863 = ((boolean) t3254432864) ==
                        ((boolean) true);
                    }
                    
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3254732865 =
                      t3254532863;
                    
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3254732865) {
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3254632866 =
                          __desugarer__var__5__319993200432879.
                            rail;
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3254732865 = ((boolean) t3254632866) ==
                        ((boolean) true);
                    }
                    
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3254832867 =
                      t3254732865;
                    
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3254832867) {
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3254832867 = ((__desugarer__var__5__319993200432879) != (null));
                    }
                    
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3254932868 =
                      t3254832867;
                    
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3255232869 =
                      !(t3254932868);
                    
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3255232869) {
                        
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3255132870 =
                          true;
                        
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3255132870) {
                            
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t3255032871 =
                              new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==1, self.zeroBased==true, self.rect==true, self.rail==true, self!=null}");
                            
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
throw t3255032871;
                        }
                    }
                    
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret3200532880 = ((x10.array.Region)(__desugarer__var__5__319993200432879));
                    
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region myReg3199832881 =
                      ((x10.array.Region)(ret3200532880));
                    
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26493.region = ((x10.array.Region)(myReg3199832881));
                    
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26493.rank = 1;
                    
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26493.rect = true;
                    
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26493.zeroBased = true;
                    
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26493.rail = true;
                    
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26493.size = size31997;
                    
//#line 249 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout alloc199713200132882 =
                      new x10.array.RectLayout((java.lang.System[]) null);
                    
//#line 97 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int _max03200832883 =
                      ((size31997) - (((int)(1))));
                    
//#line 98 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713200132882.rank = 1;
                    
//#line 99 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713200132882.min0 = 0;
                    
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3255332872 =
                      ((_max03200832883) - (((int)(0))));
                    
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3255432873 =
                      ((t3255332872) + (((int)(1))));
                    
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713200132882.delta0 = t3255432873;
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3255532874 =
                      alloc199713200132882.
                        delta0;
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final boolean t3255632875 =
                      ((t3255532874) > (((int)(0))));
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int t3255732876 =
                       0;
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
if (t3255632875) {
                        
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t3255732876 = alloc199713200132882.
                                                                                                                                              delta0;
                    } else {
                        
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t3255732876 = 0;
                    }
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3255832877 =
                      t3255732876;
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713200132882.size = t3255832877;
                    
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713200132882.min1 = 0;
                    
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713200132882.delta1 = 0;
                    
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713200132882.min2 = 0;
                    
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713200132882.delta2 = 0;
                    
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713200132882.min3 = 0;
                    
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713200132882.delta3 = 0;
                    
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713200132882.min = null;
                    
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713200132882.delta = null;
                    
//#line 249 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26493.layout = ((x10.array.RectLayout)(alloc199713200132882));
                    
//#line 250 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this3201032884 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>)alloc26493).
                                                layout));
                    
//#line 250 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int n3200232885 =
                      this3201032884.
                        size;
                    
//#line 251 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<au.edu.anu.chem.PointCharge>> t3255932886 =
                      ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<x10.array.Array<au.edu.anu.chem.PointCharge>>allocate(new x10.rtt.ParameterizedType(x10.array.Array.$RTT, au.edu.anu.chem.PointCharge.$RTT), ((int)(n3200232885)), true)));
                    
//#line 251 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26493.raw = ((x10.core.IndexedMemoryChunk)(t3255932886));
                    
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
return alloc26493;
                }
                
                // creation method for java code
                public static au.edu.anu.chem.mm.ElectrostaticDirectMethod.$Closure$2 $make(){return new $Closure$2();}
                public $Closure$2() { {
                                             
                                         }}
                
            }
            
        public static class $Closure$3
        extends x10.core.Ref
          implements x10.core.fun.Fun_0_1,
                      x10.x10rt.X10JavaSerializable 
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$3.class);
            
            public static final x10.rtt.RuntimeType<$Closure$3> $RTT = new x10.rtt.StaticFunType<$Closure$3>(
            /* base class */$Closure$3.class
            , /* parents */ new x10.rtt.Type[] {new x10.rtt.ParameterizedType(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, au.edu.anu.chem.PointCharge.$RTT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$3 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                x10.array.DistArray atoms = (x10.array.DistArray) $deserializer.readRef();
                $_obj.atoms = atoms;
                $_obj.i = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$3 $_obj = new $Closure$3((java.lang.System[]) null);
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
                $serializer.write(this.i);
                
            }
            
            // constructor just for allocation
            public $Closure$3(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object
              $apply(final java.lang.Object a1,final x10.rtt.Type t1){return $apply(x10.core.Int.$unbox(a1));}
            
                
                public au.edu.anu.chem.PointCharge
                  $apply(
                  final int j){
                    
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final au.edu.anu.chem.PointCharge alloc26494 =
                      ((au.edu.anu.chem.PointCharge)(new au.edu.anu.chem.PointCharge((java.lang.System[]) null)));
                    
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.array.Array<au.edu.anu.chem.mm.MMAtom> this3201532887 =
                      ((x10.array.Array)(((x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>>)this.
                                                                                                             atoms).$apply$G((int)(this.
                                                                                                                                     i))));
                    
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i03201432888 =
                      j;
                    
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
au.edu.anu.chem.mm.MMAtom ret3201632889 =
                       null;
                    
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret3201732890: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.chem.mm.MMAtom> t3256232891 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<au.edu.anu.chem.mm.MMAtom>)this3201532887).
                                                       raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.chem.mm.MMAtom t3256332892 =
                      ((au.edu.anu.chem.mm.MMAtom)(((au.edu.anu.chem.mm.MMAtom[])t3256232891.value)[i03201432888]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret3201632889 = t3256332892;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret3201732890;}
                    
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.chem.mm.MMAtom t3256432893 =
                      ret3201632889;
                    
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10x.vector.Point3d t3256832894 =
                      ((x10x.vector.Point3d)(t3256432893.
                                               centre));
                    
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.array.Array<au.edu.anu.chem.mm.MMAtom> this3202432895 =
                      ((x10.array.Array)(((x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>>)this.
                                                                                                             atoms).$apply$G((int)(this.
                                                                                                                                     i))));
                    
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i03202332896 =
                      j;
                    
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
au.edu.anu.chem.mm.MMAtom ret3202532897 =
                       null;
                    
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret3202632898: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.chem.mm.MMAtom> t3256532899 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<au.edu.anu.chem.mm.MMAtom>)this3202432895).
                                                       raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.chem.mm.MMAtom t3256632900 =
                      ((au.edu.anu.chem.mm.MMAtom)(((au.edu.anu.chem.mm.MMAtom[])t3256532899.value)[i03202332896]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret3202532897 = t3256632900;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret3202632898;}
                    
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.chem.mm.MMAtom t3256732901 =
                      ret3202532897;
                    
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final double t3256932902 =
                      t3256732901.
                        charge;
                    
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.util.concurrent.OrderedLock t3257032903 =
                      x10.util.concurrent.OrderedLock.createNewLock();
                    
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
alloc26494.$init(t3256832894,
                                                                                                                                                       t3256932902,
                                                                                                                                                       t3257032903);
                    
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
return alloc26494;
                }
                
                public x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>>
                  atoms;
                public int
                  i;
                
                // creation method for java code
                public static au.edu.anu.chem.mm.ElectrostaticDirectMethod.$Closure$3 $make(final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> atoms,
                                                                                            final int i,java.lang.Class<?> $dummy0){return new $Closure$3(atoms,i,(java.lang.Class<?>) null);}
                public $Closure$3(final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> atoms,
                                  final int i,java.lang.Class<?> $dummy0) { {
                                                                                   this.atoms = ((x10.array.DistArray)(atoms));
                                                                                   this.i = i;
                                                                               }}
                
            }
            
        public static class $Closure$4
        extends x10.core.Ref
          implements x10.core.fun.Fun_0_1,
                      x10.x10rt.X10JavaSerializable 
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$4.class);
            
            public static final x10.rtt.RuntimeType<$Closure$4> $RTT = new x10.rtt.StaticFunType<$Closure$4>(
            /* base class */$Closure$4.class
            , /* parents */ new x10.rtt.Type[] {new x10.rtt.ParameterizedType(x10.core.fun.Fun_0_1.$RTT, x10.array.Point.$RTT, new x10.rtt.ParameterizedType(x10.array.Array.$RTT, au.edu.anu.chem.PointCharge.$RTT)), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$4 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                x10.array.DistArray atoms = (x10.array.DistArray) $deserializer.readRef();
                $_obj.atoms = atoms;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$4 $_obj = new $Closure$4((java.lang.System[]) null);
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
                
            }
            
            // constructor just for allocation
            public $Closure$4(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object
              $apply(final java.lang.Object a1,final x10.rtt.Type t1){return $apply((x10.array.Point)a1);}
            
                
                public x10.array.Array<au.edu.anu.chem.PointCharge>
                  $apply(
                  final x10.array.Point id2){
                    
//#line 45 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int i =
                      id2.$apply$O((int)(0));
                    
//#line 45 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.array.Array<au.edu.anu.chem.PointCharge> alloc26495 =
                      ((x10.array.Array)(new x10.array.Array<au.edu.anu.chem.PointCharge>((java.lang.System[]) null, au.edu.anu.chem.PointCharge.$RTT)));
                    
//#line 45 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.array.Array<au.edu.anu.chem.mm.MMAtom> t32561 =
                      ((x10.array.Array)(((x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>>)this.
                                                                                                             atoms).$apply$G((int)(i))));
                    
//#line 271 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int size32032 =
                      ((x10.array.Array<au.edu.anu.chem.mm.MMAtom>)t32561).
                        size;
                    
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,au.edu.anu.chem.PointCharge> t32571 =
                      ((x10.core.fun.Fun_0_1)(new au.edu.anu.chem.mm.ElectrostaticDirectMethod.$Closure$3(((x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>>)(this.
                                                                                                                                                                               atoms)),
                                                                                                          i,(java.lang.Class<?>) null)));
                    
//#line 271 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,au.edu.anu.chem.PointCharge> init32033 =
                      ((x10.core.fun.Fun_0_1)(((x10.core.fun.Fun_0_1<x10.core.Int,au.edu.anu.chem.PointCharge>)(x10.core.fun.Fun_0_1)
                                                t32571)));
                    
//#line 271 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26495.x10$lang$Object$$init$S();
                    
//#line 273 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectRegion1D alloc199723203632931 =
                      ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null)));
                    
//#line 273 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t3257232911 =
                      ((size32032) - (((int)(1))));
                    
//#line 273 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc199723203632931.$init(((int)(0)),
                                                                                                                                               t3257232911);
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region __desugarer__var__6__320353204532932 =
                      ((x10.array.Region)(((x10.array.Region)
                                            alloc199723203632931)));
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Region ret3204632933 =
                       null;
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3257332912 =
                      __desugarer__var__6__320353204532932.
                        zeroBased;
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3257532913 =
                      ((boolean) t3257332912) ==
                    ((boolean) true);
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3257532913) {
                        
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3257432914 =
                          __desugarer__var__6__320353204532932.
                            rail;
                        
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3257532913 = ((boolean) t3257432914) ==
                        ((boolean) true);
                    }
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3257732915 =
                      t3257532913;
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3257732915) {
                        
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t3257632916 =
                          __desugarer__var__6__320353204532932.
                            rank;
                        
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3257732915 = ((int) t3257632916) ==
                        ((int) 1);
                    }
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3257932917 =
                      t3257732915;
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3257932917) {
                        
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3257832918 =
                          __desugarer__var__6__320353204532932.
                            rect;
                        
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3257932917 = ((boolean) t3257832918) ==
                        ((boolean) true);
                    }
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3258032919 =
                      t3257932917;
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3258032919) {
                        
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3258032919 = ((__desugarer__var__6__320353204532932) != (null));
                    }
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3258132920 =
                      t3258032919;
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3258432921 =
                      !(t3258132920);
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3258432921) {
                        
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3258332922 =
                          true;
                        
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3258332922) {
                            
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t3258232923 =
                              new x10.lang.FailedDynamicCheckException("x10.array.Region{self.zeroBased==true, self.rail==true, self.rank==1, self.rect==true, self!=null}");
                            
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
throw t3258232923;
                        }
                    }
                    
//#line 273 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret3204632933 = ((x10.array.Region)(__desugarer__var__6__320353204532932));
                    
//#line 273 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region myReg3203432934 =
                      ((x10.array.Region)(ret3204632933));
                    
//#line 274 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26495.region = ((x10.array.Region)(myReg3203432934));
                    
//#line 274 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26495.rank = 1;
                    
//#line 274 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26495.rect = true;
                    
//#line 274 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26495.zeroBased = true;
                    
//#line 274 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26495.rail = true;
                    
//#line 274 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26495.size = size32032;
                    
//#line 276 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout alloc199733203732935 =
                      new x10.array.RectLayout((java.lang.System[]) null);
                    
//#line 97 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int _max03204932936 =
                      ((size32032) - (((int)(1))));
                    
//#line 98 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199733203732935.rank = 1;
                    
//#line 99 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199733203732935.min0 = 0;
                    
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3258532924 =
                      ((_max03204932936) - (((int)(0))));
                    
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3258632925 =
                      ((t3258532924) + (((int)(1))));
                    
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199733203732935.delta0 = t3258632925;
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3258732926 =
                      alloc199733203732935.
                        delta0;
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final boolean t3258832927 =
                      ((t3258732926) > (((int)(0))));
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int t3258932928 =
                       0;
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
if (t3258832927) {
                        
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t3258932928 = alloc199733203732935.
                                                                                                                                              delta0;
                    } else {
                        
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t3258932928 = 0;
                    }
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3259032929 =
                      t3258932928;
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199733203732935.size = t3259032929;
                    
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199733203732935.min1 = 0;
                    
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199733203732935.delta1 = 0;
                    
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199733203732935.min2 = 0;
                    
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199733203732935.delta2 = 0;
                    
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199733203732935.min3 = 0;
                    
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199733203732935.delta3 = 0;
                    
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199733203732935.min = null;
                    
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199733203732935.delta = null;
                    
//#line 276 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26495.layout = ((x10.array.RectLayout)(alloc199733203732935));
                    
//#line 277 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this3205132937 =
                      ((x10.array.RectLayout)(((x10.array.Array<au.edu.anu.chem.PointCharge>)alloc26495).
                                                layout));
                    
//#line 277 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int n3203832938 =
                      this3205132937.
                        size;
                    
//#line 278 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.chem.PointCharge> r3203932939 =
                      x10.core.IndexedMemoryChunk.<au.edu.anu.chem.PointCharge>allocate(au.edu.anu.chem.PointCharge.$RTT, ((int)(n3203832938)), false);
                    
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i19990max199923204132930 =
                      ((size32032) - (((int)(1))));
                    
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int i199903204232908 =
                      0;
                    {
                        
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.chem.PointCharge[] r3203932939$value33263 =
                          ((au.edu.anu.chem.PointCharge[])r3203932939.value);
                        
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
for (;
                                                                                                                             true;
                                                                                                                             ) {
                            
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t3259232909 =
                              i199903204232908;
                            
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3259632910 =
                              ((t3259232909) <= (((int)(i19990max199923204132930))));
                            
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (!(t3259632910)) {
                                
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break;
                            }
                            
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i3204332905 =
                              i199903204232908;
                            
//#line 280 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.chem.PointCharge t3259532904 =
                              ((au.edu.anu.chem.PointCharge)(((au.edu.anu.chem.PointCharge)x10.rtt.Types.asStruct(au.edu.anu.chem.PointCharge.$RTT,((x10.core.fun.Fun_0_1<x10.core.Int,au.edu.anu.chem.PointCharge>)init32033).$apply(x10.core.Int.$box(i3204332905),x10.rtt.Types.INT)))));
                            
//#line 280 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
r3203932939$value33263[i3204332905]=t3259532904;
                            
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t3259332906 =
                              i199903204232908;
                            
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t3259432907 =
                              ((t3259332906) + (((int)(1))));
                            
//#line 279 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
i199903204232908 = t3259432907;
                        }
                    }
                    
//#line 282 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26495.raw = ((x10.core.IndexedMemoryChunk)(r3203932939));
                    
//#line 45 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
return alloc26495;
                }
                
                public x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>>
                  atoms;
                
                // creation method for java code
                public static au.edu.anu.chem.mm.ElectrostaticDirectMethod.$Closure$4 $make(final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> atoms,java.lang.Class<?> $dummy0){return new $Closure$4(atoms,(java.lang.Class<?>) null);}
                public $Closure$4(final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> atoms,java.lang.Class<?> $dummy0) { {
                                                                                                                                                   this.atoms = ((x10.array.DistArray)(atoms));
                                                                                                                                               }}
                
            }
            
        public static class $Closure$5
        extends x10.core.Ref
          implements x10.core.fun.Fun_0_1,
                      x10.x10rt.X10JavaSerializable 
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$5.class);
            
            public static final x10.rtt.RuntimeType<$Closure$5> $RTT = new x10.rtt.StaticFunType<$Closure$5>(
            /* base class */$Closure$5.class
            , /* parents */ new x10.rtt.Type[] {new x10.rtt.ParameterizedType(x10.core.fun.Fun_0_1.$RTT, x10.array.Point.$RTT, new x10.rtt.ParameterizedType(x10.array.Array.$RTT, new x10.rtt.ParameterizedType(x10.array.Array.$RTT, au.edu.anu.chem.PointCharge.$RTT))), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$5 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$5 $_obj = new $Closure$5((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public $Closure$5(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object
              $apply(final java.lang.Object a1,final x10.rtt.Type t1){return $apply((x10.array.Point)a1);}
            
                
                public x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>
                  $apply(
                  final x10.array.Point id3){
                    
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int p =
                      id3.$apply$O((int)(0));
                    
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>> alloc26496 =
                      ((x10.array.Array)(new x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>((java.lang.System[]) null, new x10.rtt.ParameterizedType(x10.array.Array.$RTT, au.edu.anu.chem.PointCharge.$RTT))));
                    
//#line 243 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int size32055 =
                      x10.lang.Place.getInitialized$MAX_PLACES();
                    
//#line 243 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26496.x10$lang$Object$$init$S();
                    
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectRegion1D alloc199703205832959 =
                      ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null)));
                    
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t3259832940 =
                      ((size32055) - (((int)(1))));
                    
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc199703205832959.$init(((int)(0)),
                                                                                                                                               t3259832940);
                    
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region __desugarer__var__5__320573206232960 =
                      ((x10.array.Region)(((x10.array.Region)
                                            alloc199703205832959)));
                    
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Region ret3206332961 =
                       null;
                    
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t3259932941 =
                      __desugarer__var__5__320573206232960.
                        rank;
                    
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3260132942 =
                      ((int) t3259932941) ==
                    ((int) 1);
                    
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3260132942) {
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3260032943 =
                          __desugarer__var__5__320573206232960.
                            zeroBased;
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3260132942 = ((boolean) t3260032943) ==
                        ((boolean) true);
                    }
                    
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3260332944 =
                      t3260132942;
                    
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3260332944) {
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3260232945 =
                          __desugarer__var__5__320573206232960.
                            rect;
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3260332944 = ((boolean) t3260232945) ==
                        ((boolean) true);
                    }
                    
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3260532946 =
                      t3260332944;
                    
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3260532946) {
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3260432947 =
                          __desugarer__var__5__320573206232960.
                            rail;
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3260532946 = ((boolean) t3260432947) ==
                        ((boolean) true);
                    }
                    
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t3260632948 =
                      t3260532946;
                    
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3260632948) {
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t3260632948 = ((__desugarer__var__5__320573206232960) != (null));
                    }
                    
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3260732949 =
                      t3260632948;
                    
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3261032950 =
                      !(t3260732949);
                    
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3261032950) {
                        
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t3260932951 =
                          true;
                        
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t3260932951) {
                            
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t3260832952 =
                              new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==1, self.zeroBased==true, self.rect==true, self.rail==true, self!=null}");
                            
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
throw t3260832952;
                        }
                    }
                    
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret3206332961 = ((x10.array.Region)(__desugarer__var__5__320573206232960));
                    
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region myReg3205632962 =
                      ((x10.array.Region)(ret3206332961));
                    
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26496.region = ((x10.array.Region)(myReg3205632962));
                    
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26496.rank = 1;
                    
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26496.rect = true;
                    
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26496.zeroBased = true;
                    
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26496.rail = true;
                    
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26496.size = size32055;
                    
//#line 249 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout alloc199713205932963 =
                      new x10.array.RectLayout((java.lang.System[]) null);
                    
//#line 97 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int _max03206632964 =
                      ((size32055) - (((int)(1))));
                    
//#line 98 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713205932963.rank = 1;
                    
//#line 99 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713205932963.min0 = 0;
                    
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3261132953 =
                      ((_max03206632964) - (((int)(0))));
                    
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3261232954 =
                      ((t3261132953) + (((int)(1))));
                    
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713205932963.delta0 = t3261232954;
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3261332955 =
                      alloc199713205932963.
                        delta0;
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final boolean t3261432956 =
                      ((t3261332955) > (((int)(0))));
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int t3261532957 =
                       0;
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
if (t3261432956) {
                        
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t3261532957 = alloc199713205932963.
                                                                                                                                              delta0;
                    } else {
                        
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t3261532957 = 0;
                    }
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t3261632958 =
                      t3261532957;
                    
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713205932963.size = t3261632958;
                    
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713205932963.min1 = 0;
                    
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713205932963.delta1 = 0;
                    
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713205932963.min2 = 0;
                    
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713205932963.delta2 = 0;
                    
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713205932963.min3 = 0;
                    
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713205932963.delta3 = 0;
                    
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713205932963.min = null;
                    
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713205932963.delta = null;
                    
//#line 249 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26496.layout = ((x10.array.RectLayout)(alloc199713205932963));
                    
//#line 250 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this3206832965 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>)alloc26496).
                                                layout));
                    
//#line 250 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int n3206032966 =
                      this3206832965.
                        size;
                    
//#line 251 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<au.edu.anu.chem.PointCharge>> t3261732967 =
                      ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<x10.array.Array<au.edu.anu.chem.PointCharge>>allocate(new x10.rtt.ParameterizedType(x10.array.Array.$RTT, au.edu.anu.chem.PointCharge.$RTT), ((int)(n3206032966)), true)));
                    
//#line 251 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc26496.raw = ((x10.core.IndexedMemoryChunk)(t3261732967));
                    
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
return alloc26496;
                }
                
                // creation method for java code
                public static au.edu.anu.chem.mm.ElectrostaticDirectMethod.$Closure$5 $make(){return new $Closure$5();}
                public $Closure$5() { {
                                             
                                         }}
                
            }
            
        public static class $Closure$6
        extends x10.core.Ref
          implements x10.core.fun.VoidFun_0_0,
                      x10.x10rt.X10JavaSerializable 
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$6.class);
            
            public static final x10.rtt.RuntimeType<$Closure$6> $RTT = new x10.rtt.StaticVoidFunType<$Closure$6>(
            /* base class */$Closure$6.class
            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$6 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                x10.lang.Place nextPlace = (x10.lang.Place) $deserializer.readRef();
                $_obj.nextPlace = nextPlace;
                x10.array.DistArray otherAtoms = (x10.array.DistArray) $deserializer.readRef();
                $_obj.otherAtoms = otherAtoms;
                $_obj.p1 = $deserializer.readInt();
                x10.array.Array myAtoms = (x10.array.Array) $deserializer.readRef();
                $_obj.myAtoms = myAtoms;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$6 $_obj = new $Closure$6((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (nextPlace instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.nextPlace);
                } else {
                $serializer.write(this.nextPlace);
                }
                if (otherAtoms instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.otherAtoms);
                } else {
                $serializer.write(this.otherAtoms);
                }
                $serializer.write(this.p1);
                if (myAtoms instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.myAtoms);
                } else {
                $serializer.write(this.myAtoms);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$6(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            
                
                public void
                  $apply(
                  ){
                    
//#line 68 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
try {{
                        
//#line 68 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
x10.lang.Runtime.enterAtomic();
                        {
                            
//#line 69 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int t32623 =
                              this.
                                nextPlace.
                                id;
                            
//#line 69 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>> this32365 =
                              ((x10.array.Array)(((x10.array.DistArray<x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>>)this.
                                                                                                                                        otherAtoms).$apply$G((int)(t32623))));
                            
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i032363 =
                              this.
                                p1;
                            
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<au.edu.anu.chem.PointCharge> v32364 =
                              ((x10.array.Array)(this.
                                                   myAtoms));
                            
//#line 508 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<au.edu.anu.chem.PointCharge> ret32366 =
                               null;
                            
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<au.edu.anu.chem.PointCharge>> t3262432968 =
                              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>)this32365).
                                                               raw));
                            
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.array.Array[])t3262432968.value)[i032363] = v32364;
                            
//#line 519 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret32366 = ((x10.array.Array)(v32364));
                        }
                    }}finally {{
                          
//#line 68 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
x10.lang.Runtime.exitAtomic();
                      }}
                    }
                
                public x10.lang.Place
                  nextPlace;
                public x10.array.DistArray<x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>>
                  otherAtoms;
                public int
                  p1;
                public x10.array.Array<au.edu.anu.chem.PointCharge>
                  myAtoms;
                
                // creation method for java code
                public static au.edu.anu.chem.mm.ElectrostaticDirectMethod.$Closure$6 $make(final x10.lang.Place nextPlace,
                                                                                            final x10.array.DistArray<x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>> otherAtoms,
                                                                                            final int p1,
                                                                                            final x10.array.Array<au.edu.anu.chem.PointCharge> myAtoms,java.lang.Class<?> $dummy0){return new $Closure$6(nextPlace,otherAtoms,p1,myAtoms,(java.lang.Class<?>) null);}
                public $Closure$6(final x10.lang.Place nextPlace,
                                  final x10.array.DistArray<x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>> otherAtoms,
                                  final int p1,
                                  final x10.array.Array<au.edu.anu.chem.PointCharge> myAtoms,java.lang.Class<?> $dummy0) { {
                                                                                                                                  this.nextPlace = ((x10.lang.Place)(nextPlace));
                                                                                                                                  this.otherAtoms = ((x10.array.DistArray)(otherAtoms));
                                                                                                                                  this.p1 = p1;
                                                                                                                                  this.myAtoms = ((x10.array.Array)(myAtoms));
                                                                                                                              }}
                
                }
                
            public static class $Closure$7
            extends x10.core.Ref
              implements x10.core.fun.VoidFun_0_0,
                          x10.x10rt.X10JavaSerializable 
            {
                private static final long serialVersionUID = 1L;
                private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$7.class);
                
                public static final x10.rtt.RuntimeType<$Closure$7> $RTT = new x10.rtt.StaticVoidFunType<$Closure$7>(
                /* base class */$Closure$7.class
                , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                );
                public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                
                
                public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$7 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    x10.lang.Place targetPlace33081 = (x10.lang.Place) $deserializer.readRef();
                    $_obj.targetPlace33081 = targetPlace33081;
                    x10.array.DistArray otherAtoms = (x10.array.DistArray) $deserializer.readRef();
                    $_obj.otherAtoms = otherAtoms;
                    $_obj.p1 = $deserializer.readInt();
                    x10.array.Array myAtoms = (x10.array.Array) $deserializer.readRef();
                    $_obj.myAtoms = myAtoms;
                    return $_obj;
                    
                }
                
                public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    $Closure$7 $_obj = new $Closure$7((java.lang.System[]) null);
                    $deserializer.record_reference($_obj);
                    return $_deserialize_body($_obj, $deserializer);
                    
                }
                
                public short $_get_serialization_id() {
                
                     return $_serialization_id;
                    
                }
                
                public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                
                    if (targetPlace33081 instanceof x10.x10rt.X10JavaSerializable) {
                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.targetPlace33081);
                    } else {
                    $serializer.write(this.targetPlace33081);
                    }
                    if (otherAtoms instanceof x10.x10rt.X10JavaSerializable) {
                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.otherAtoms);
                    } else {
                    $serializer.write(this.otherAtoms);
                    }
                    $serializer.write(this.p1);
                    if (myAtoms instanceof x10.x10rt.X10JavaSerializable) {
                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.myAtoms);
                    } else {
                    $serializer.write(this.myAtoms);
                    }
                    
                }
                
                // constructor just for allocation
                public $Closure$7(final java.lang.System[] $dummy) { 
                super($dummy);
                }
                
                    
                    public void
                      $apply(
                      ){
                        
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
try {{
                            
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
x10.lang.Runtime.enterAtomic();
                            {
                                
//#line 91 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int t3266333082 =
                                  this.
                                    targetPlace33081.
                                    id;
                                
//#line 91 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>> this3240033083 =
                                  ((x10.array.Array)(((x10.array.DistArray<x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>>)this.
                                                                                                                                            otherAtoms).$apply$G((int)(t3266333082))));
                                
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i03239833084 =
                                  this.
                                    p1;
                                
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<au.edu.anu.chem.PointCharge> v3239933085 =
                                  ((x10.array.Array)(this.
                                                       myAtoms));
                                
//#line 508 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<au.edu.anu.chem.PointCharge> ret3240133086 =
                                   null;
                                
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<au.edu.anu.chem.PointCharge>> t3266433022 =
                                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>)this3240033083).
                                                                   raw));
                                
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.array.Array[])t3266433022.value)[i03239833084] = v3239933085;
                                
//#line 519 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret3240133086 = ((x10.array.Array)(v3239933085));
                            }
                        }}finally {{
                              
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
x10.lang.Runtime.exitAtomic();
                          }}
                        }
                    
                    public x10.lang.Place
                      targetPlace33081;
                    public x10.array.DistArray<x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>>
                      otherAtoms;
                    public int
                      p1;
                    public x10.array.Array<au.edu.anu.chem.PointCharge>
                      myAtoms;
                    
                    // creation method for java code
                    public static au.edu.anu.chem.mm.ElectrostaticDirectMethod.$Closure$7 $make(final x10.lang.Place targetPlace33081,
                                                                                                final x10.array.DistArray<x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>> otherAtoms,
                                                                                                final int p1,
                                                                                                final x10.array.Array<au.edu.anu.chem.PointCharge> myAtoms,java.lang.Class<?> $dummy0){return new $Closure$7(targetPlace33081,otherAtoms,p1,myAtoms,(java.lang.Class<?>) null);}
                    public $Closure$7(final x10.lang.Place targetPlace33081,
                                      final x10.array.DistArray<x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>> otherAtoms,
                                      final int p1,
                                      final x10.array.Array<au.edu.anu.chem.PointCharge> myAtoms,java.lang.Class<?> $dummy0) { {
                                                                                                                                      this.targetPlace33081 = ((x10.lang.Place)(targetPlace33081));
                                                                                                                                      this.otherAtoms = ((x10.array.DistArray)(otherAtoms));
                                                                                                                                      this.p1 = p1;
                                                                                                                                      this.myAtoms = ((x10.array.Array)(myAtoms));
                                                                                                                                  }}
                    
                    }
                    
                public static class $Closure$8
                extends x10.core.Ref
                  implements x10.core.fun.VoidFun_0_0,
                              x10.x10rt.X10JavaSerializable 
                {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, $Closure$8.class);
                    
                    public static final x10.rtt.RuntimeType<$Closure$8> $RTT = new x10.rtt.StaticVoidFunType<$Closure$8>(
                    /* base class */$Closure$8.class
                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$8 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        x10.array.DistArray atoms = (x10.array.DistArray) $deserializer.readRef();
                        $_obj.atoms = atoms;
                        $_obj.p1 = $deserializer.readInt();
                        x10.array.DistArray otherAtoms = (x10.array.DistArray) $deserializer.readRef();
                        $_obj.otherAtoms = otherAtoms;
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        $Closure$8 $_obj = new $Closure$8((java.lang.System[]) null);
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
                        $serializer.write(this.p1);
                        if (otherAtoms instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.otherAtoms);
                        } else {
                        $serializer.write(this.otherAtoms);
                        }
                        
                    }
                    
                    // constructor just for allocation
                    public $Closure$8(final java.lang.System[] $dummy) { 
                    super($dummy);
                    }
                    
                        
                        public void
                          $apply(
                          ){
                            
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.array.Array<au.edu.anu.chem.PointCharge> myAtoms =
                              ((x10.array.Array)(((x10.array.DistArray<x10.array.Array<au.edu.anu.chem.PointCharge>>)this.
                                                                                                                       atoms).$apply$G((int)(this.
                                                                                                                                               p1))));
                            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
double energyThisPlace =
                              0.0;
                            
//#line 65 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.lang.Place this32362 =
                              ((x10.lang.Place)(x10.lang.Runtime.home()));
                            
//#line 65 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.lang.Place nextPlace =
                              this32362.next((int)(1));
                            
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final boolean t32625 =
                              (!x10.rtt.Equality.equalsequals((nextPlace),(x10.lang.Runtime.home())));
                            
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
if (t32625) {
                                
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
x10.lang.Runtime.runUncountedAsync(((x10.lang.Place)(nextPlace)),
                                                                                                                                                                                     ((x10.core.fun.VoidFun_0_0)(new au.edu.anu.chem.mm.ElectrostaticDirectMethod.$Closure$6(nextPlace,
                                                                                                                                                                                                                                                                             this.
                                                                                                                                                                                                                                                                               otherAtoms,
                                                                                                                                                                                                                                                                             ((int)(this.
                                                                                                                                                                                                                                                                                      p1)),
                                                                                                                                                                                                                                                                             myAtoms,(java.lang.Class<?>) null))));
                            }
                            
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int t3262633101 =
                              ((x10.array.Array<au.edu.anu.chem.PointCharge>)myAtoms).
                                size;
                            
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int i26516max2651833102 =
                              ((t3262633101) - (((int)(1))));
                            
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
int i2651633019 =
                              0;
                            
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
for (;
                                                                                                                                                   true;
                                                                                                                                                   ) {
                                
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int t3262833020 =
                                  i2651633019;
                                
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final boolean t3266033021 =
                                  ((t3262833020) <= (((int)(i26516max2651833102))));
                                
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
if (!(t3266033021)) {
                                    
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
break;
                                }
                                
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int i33016 =
                                  i2651633019;
                                
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i03237333010 =
                                  i33016;
                                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
au.edu.anu.chem.PointCharge ret3237433011 =
                                   null;
                                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret3237533012: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.chem.PointCharge> t3263133013 =
                                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<au.edu.anu.chem.PointCharge>)myAtoms).
                                                                   raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.chem.PointCharge t3263233014 =
                                  ((au.edu.anu.chem.PointCharge)(((au.edu.anu.chem.PointCharge[])t3263133013.value)[i03237333010]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret3237433011 = t3263233014;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret3237533012;}
                                
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final au.edu.anu.chem.PointCharge atomI33015 =
                                  ret3237433011;
                                
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int i26500max2650233009 =
                                  ((i33016) - (((int)(1))));
                                
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
int i2650033005 =
                                  0;
                                
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
for (;
                                                                                                                                                       true;
                                                                                                                                                       ) {
                                    
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int t3263433006 =
                                      i2650033005;
                                    
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final boolean t3265933007 =
                                      ((t3263433006) <= (((int)(i26500max2650233009))));
                                    
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
if (!(t3265933007)) {
                                        
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
break;
                                    }
                                    
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int j33002 =
                                      i2650033005;
                                    
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i03238132983 =
                                      j33002;
                                    
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
au.edu.anu.chem.PointCharge ret3238232984 =
                                       null;
                                    
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret3238332985: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.chem.PointCharge> t3263732986 =
                                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<au.edu.anu.chem.PointCharge>)myAtoms).
                                                                       raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.chem.PointCharge t3263832987 =
                                      ((au.edu.anu.chem.PointCharge)(((au.edu.anu.chem.PointCharge[])t3263732986.value)[i03238132983]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret3238232984 = t3263832987;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret3238332985;}
                                    
//#line 78 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final au.edu.anu.chem.PointCharge atomJ32988 =
                                      ret3238232984;
                                    
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final double t3265632989 =
                                      energyThisPlace;
                                    
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final double t3263932990 =
                                      atomI33015.
                                        charge;
                                    
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final double t3264032991 =
                                      ((2.0) * (((double)(t3263932990))));
                                    
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final double t3264132992 =
                                      atomJ32988.
                                        charge;
                                    
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final double t3265432993 =
                                      ((t3264032991) * (((double)(t3264132992))));
                                    
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10x.vector.Point3d this3239032994 =
                                      ((x10x.vector.Point3d)(atomJ32988.
                                                               centre));
                                    
//#line 66 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d b3238932995 =
                                      ((x10x.vector.Point3d)(atomI33015.
                                                               centre));
                                    
//#line 57 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d b3239132996 =
                                      ((x10x.vector.Point3d)(b3238932995));
                                    
//#line 57 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
double ret3239532997 =
                                       0;
                                    
//#line 58 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t3264232969 =
                                      this3239032994.
                                        i;
                                    
//#line 58 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t3264332970 =
                                      b3239132996.
                                        i;
                                    
//#line 58 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double di3239232971 =
                                      ((t3264232969) - (((double)(t3264332970))));
                                    
//#line 59 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t3264432972 =
                                      this3239032994.
                                        j;
                                    
//#line 59 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t3264532973 =
                                      b3239132996.
                                        j;
                                    
//#line 59 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double dj3239332974 =
                                      ((t3264432972) - (((double)(t3264532973))));
                                    
//#line 60 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t3264632975 =
                                      this3239032994.
                                        k;
                                    
//#line 60 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t3264732976 =
                                      b3239132996.
                                        k;
                                    
//#line 60 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double dk3239432977 =
                                      ((t3264632975) - (((double)(t3264732976))));
                                    
//#line 61 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t3264832978 =
                                      ((di3239232971) * (((double)(di3239232971))));
                                    
//#line 62 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t3264932979 =
                                      ((dj3239332974) * (((double)(dj3239332974))));
                                    
//#line 61 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t3265032980 =
                                      ((t3264832978) + (((double)(t3264932979))));
                                    
//#line 63 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t3265132981 =
                                      ((dk3239432977) * (((double)(dk3239432977))));
                                    
//#line 61 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t3265232982 =
                                      ((t3265032980) + (((double)(t3265132981))));
                                    
//#line 61 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
ret3239532997 = t3265232982;
                                    
//#line 57 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t3265332998 =
                                      ret3239532997;
                                    
//#line 67 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t3265532999 =
                                      java.lang.Math.sqrt(((double)(t3265332998)));
                                    
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final double t3265733000 =
                                      ((t3265432993) / (((double)(t3265532999))));
                                    
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final double t3265833001 =
                                      ((t3265632989) + (((double)(t3265733000))));
                                    
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
energyThisPlace = t3265833001;
                                    
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int t3263533003 =
                                      i2650033005;
                                    
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int t3263633004 =
                                      ((t3263533003) + (((int)(1))));
                                    
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
i2650033005 = t3263633004;
                                }
                                
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int t3262933017 =
                                  i2651633019;
                                
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int t3263033018 =
                                  ((t3262933017) + (((int)(1))));
                                
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
i2651633019 = t3263033018;
                            }
                            
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
x10.lang.Place target =
                              nextPlace.next((int)(1));
                            
//#line 84 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.lang.Place this32397 =
                              ((x10.lang.Place)(x10.lang.Runtime.home()));
                            
//#line 84 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
x10.lang.Place source =
                              this32397.next((int)(-1));
                            
//#line 85 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
while (true) {
                                
//#line 85 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.lang.Place t32661 =
                                  source;
                                
//#line 85 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final boolean t32709 =
                                  (!x10.rtt.Equality.equalsequals((t32661),(x10.lang.Runtime.home())));
                                
//#line 85 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
if (!(t32709)) {
                                    
//#line 85 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
break;
                                }
                                
//#line 86 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.lang.Place t3266233079 =
                                  target;
                                
//#line 86 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final boolean t3266533080 =
                                  (!x10.rtt.Equality.equalsequals((t3266233079),(x10.lang.Runtime.home())));
                                
//#line 86 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
if (t3266533080) {
                                    
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.lang.Place targetPlace33081 =
                                      target;
                                    
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
x10.lang.Runtime.runUncountedAsync(((x10.lang.Place)(targetPlace33081)),
                                                                                                                                                                                         ((x10.core.fun.VoidFun_0_0)(new au.edu.anu.chem.mm.ElectrostaticDirectMethod.$Closure$7(targetPlace33081,
                                                                                                                                                                                                                                                                                 this.
                                                                                                                                                                                                                                                                                   otherAtoms,
                                                                                                                                                                                                                                                                                 ((int)(this.
                                                                                                                                                                                                                                                                                          p1)),
                                                                                                                                                                                                                                                                                 myAtoms,(java.lang.Class<?>) null))));
                                }
                                {
                                    
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
x10.lang.Runtime.ensureNotInAtomic();
                                    
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
try {{
                                        
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
x10.lang.Runtime.enterAtomic();
                                        
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
while (true) {
                                            
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
if (((((x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>)((x10.array.DistArray<x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>>)this.
                                                                                                                                                                                                                                                                                                                           otherAtoms).$apply$G((int)(x10.lang.Runtime.home().
                                                                                                                                                                                                                                                                                                                                                        id))).$apply$G((int)(source.
                                                                                                                                                                                                                                                                                                                                                                               id))) != (null))) {
                                                {
                                                    
                                                }
                                                
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
break;
                                            }
                                            
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
x10.lang.Runtime.awaitAtomic();
                                        }
                                    }}finally {{
                                          
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
x10.lang.Runtime.exitAtomic();
                                      }}
                                    }
                                
//#line 100 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int t3266633087 =
                                  x10.lang.Runtime.home().
                                    id;
                                
//#line 100 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>> this3240933088 =
                                  ((x10.array.Array)(((x10.array.DistArray<x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>>)this.
                                                                                                                                            otherAtoms).$apply$G((int)(t3266633087))));
                                
//#line 100 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.lang.Place t3266733089 =
                                  source;
                                
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i03240833090 =
                                  t3266733089.
                                    id;
                                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<au.edu.anu.chem.PointCharge> ret3241033091 =
                                   null;
                                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret3241133092: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<au.edu.anu.chem.PointCharge>> t3266833093 =
                                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>)this3240933088).
                                                                   raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<au.edu.anu.chem.PointCharge> t3266933094 =
                                  ((x10.array.Array)(((x10.array.Array[])t3266833093.value)[i03240833090]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret3241033091 = ((x10.array.Array)(t3266933094));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret3241133092;}
                                
//#line 100 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.array.Array<au.edu.anu.chem.PointCharge> other33095 =
                                  ((x10.array.Array)(ret3241033091));
                                
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int t3267033077 =
                                  ((x10.array.Array<au.edu.anu.chem.PointCharge>)other33095).
                                    size;
                                
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int i26548max2655033078 =
                                  ((t3267033077) - (((int)(1))));
                                
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
int i2654833073 =
                                  0;
                                
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
for (;
                                                                                                                                                        true;
                                                                                                                                                        ) {
                                    
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int t3267233074 =
                                      i2654833073;
                                    
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final boolean t3270433075 =
                                      ((t3267233074) <= (((int)(i26548max2655033078))));
                                    
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
if (!(t3270433075)) {
                                        
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
break;
                                    }
                                    
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int j33070 =
                                      i2654833073;
                                    
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i03241733064 =
                                      j33070;
                                    
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
au.edu.anu.chem.PointCharge ret3241833065 =
                                       null;
                                    
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret3241933066: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.chem.PointCharge> t3267533067 =
                                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<au.edu.anu.chem.PointCharge>)other33095).
                                                                       raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.chem.PointCharge t3267633068 =
                                      ((au.edu.anu.chem.PointCharge)(((au.edu.anu.chem.PointCharge[])t3267533067.value)[i03241733064]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret3241833065 = t3267633068;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret3241933066;}
                                    
//#line 102 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final au.edu.anu.chem.PointCharge atomJ33069 =
                                      ret3241833065;
                                    
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int t3267733062 =
                                      ((x10.array.Array<au.edu.anu.chem.PointCharge>)myAtoms).
                                        size;
                                    
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int i26532max2653433063 =
                                      ((t3267733062) - (((int)(1))));
                                    
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
int i2653233058 =
                                      0;
                                    
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
for (;
                                                                                                                                                            true;
                                                                                                                                                            ) {
                                        
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int t3267933059 =
                                          i2653233058;
                                        
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final boolean t3270333060 =
                                          ((t3267933059) <= (((int)(i26532max2653433063))));
                                        
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
if (!(t3270333060)) {
                                            
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
break;
                                        }
                                        
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int i33055 =
                                          i2653233058;
                                        
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i03242533037 =
                                          i33055;
                                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
au.edu.anu.chem.PointCharge ret3242633038 =
                                           null;
                                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret3242733039: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.chem.PointCharge> t3268233040 =
                                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<au.edu.anu.chem.PointCharge>)myAtoms).
                                                                           raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.chem.PointCharge t3268333041 =
                                          ((au.edu.anu.chem.PointCharge)(((au.edu.anu.chem.PointCharge[])t3268233040.value)[i03242533037]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret3242633038 = t3268333041;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret3242733039;}
                                        
//#line 104 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final au.edu.anu.chem.PointCharge atomI33042 =
                                          ret3242633038;
                                        
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final double t3270033043 =
                                          energyThisPlace;
                                        
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final double t3268433044 =
                                          atomI33042.
                                            charge;
                                        
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final double t3268533045 =
                                          atomJ33069.
                                            charge;
                                        
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final double t3269833046 =
                                          ((t3268433044) * (((double)(t3268533045))));
                                        
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10x.vector.Point3d this3243433047 =
                                          ((x10x.vector.Point3d)(atomJ33069.
                                                                   centre));
                                        
//#line 66 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d b3243333048 =
                                          ((x10x.vector.Point3d)(atomI33042.
                                                                   centre));
                                        
//#line 57 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d b3243533049 =
                                          ((x10x.vector.Point3d)(b3243333048));
                                        
//#line 57 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
double ret3243933050 =
                                           0;
                                        
//#line 58 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t3268633023 =
                                          this3243433047.
                                            i;
                                        
//#line 58 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t3268733024 =
                                          b3243533049.
                                            i;
                                        
//#line 58 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double di3243633025 =
                                          ((t3268633023) - (((double)(t3268733024))));
                                        
//#line 59 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t3268833026 =
                                          this3243433047.
                                            j;
                                        
//#line 59 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t3268933027 =
                                          b3243533049.
                                            j;
                                        
//#line 59 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double dj3243733028 =
                                          ((t3268833026) - (((double)(t3268933027))));
                                        
//#line 60 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t3269033029 =
                                          this3243433047.
                                            k;
                                        
//#line 60 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t3269133030 =
                                          b3243533049.
                                            k;
                                        
//#line 60 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double dk3243833031 =
                                          ((t3269033029) - (((double)(t3269133030))));
                                        
//#line 61 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t3269233032 =
                                          ((di3243633025) * (((double)(di3243633025))));
                                        
//#line 62 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t3269333033 =
                                          ((dj3243733028) * (((double)(dj3243733028))));
                                        
//#line 61 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t3269433034 =
                                          ((t3269233032) + (((double)(t3269333033))));
                                        
//#line 63 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t3269533035 =
                                          ((dk3243833031) * (((double)(dk3243833031))));
                                        
//#line 61 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t3269633036 =
                                          ((t3269433034) + (((double)(t3269533035))));
                                        
//#line 61 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
ret3243933050 = t3269633036;
                                        
//#line 57 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t3269733051 =
                                          ret3243933050;
                                        
//#line 67 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t3269933052 =
                                          java.lang.Math.sqrt(((double)(t3269733051)));
                                        
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final double t3270133053 =
                                          ((t3269833046) / (((double)(t3269933052))));
                                        
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final double t3270233054 =
                                          ((t3270033043) + (((double)(t3270133053))));
                                        
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
energyThisPlace = t3270233054;
                                        
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int t3268033056 =
                                          i2653233058;
                                        
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int t3268133057 =
                                          ((t3268033056) + (((int)(1))));
                                        
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
i2653233058 = t3268133057;
                                    }
                                    
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int t3267333071 =
                                      i2654833073;
                                    
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int t3267433072 =
                                      ((t3267333071) + (((int)(1))));
                                    
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
i2654833073 = t3267433072;
                                }
                                
//#line 132 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Place.x10"
final x10.lang.Place t3270533096 =
                                  target;
                                
//#line 132 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Place.x10"
final x10.lang.Place t3270633097 =
                                  t3270533096.next((int)(1));
                                
//#line 108 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
target = t3270633097;
                                
//#line 137 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Place.x10"
final x10.lang.Place t3270733098 =
                                  source;
                                
//#line 137 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Place.x10"
final x10.lang.Place t3270833099 =
                                  t3270733098.next((int)(-1));
                                
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
source = t3270833099;
                                }
                            
//#line 112 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final double t32710 =
                              energyThisPlace;
                            
//#line 112 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
x10.lang.Runtime.<x10.core.Double>makeOffer_0_$$x10$lang$Runtime_T(x10.rtt.Types.DOUBLE, x10.core.Double.$box(t32710));
                            }
                        
                        public x10.array.DistArray<x10.array.Array<au.edu.anu.chem.PointCharge>>
                          atoms;
                        public int
                          p1;
                        public x10.array.DistArray<x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>>
                          otherAtoms;
                        
                        // creation method for java code
                        public static au.edu.anu.chem.mm.ElectrostaticDirectMethod.$Closure$8 $make(final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.PointCharge>> atoms,
                                                                                                    final int p1,
                                                                                                    final x10.array.DistArray<x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>> otherAtoms,java.lang.Class<?> $dummy0){return new $Closure$8(atoms,p1,otherAtoms,(java.lang.Class<?>) null);}
                        public $Closure$8(final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.PointCharge>> atoms,
                                          final int p1,
                                          final x10.array.DistArray<x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>> otherAtoms,java.lang.Class<?> $dummy0) { {
                                                                                                                                                                                   this.atoms = ((x10.array.DistArray)(atoms));
                                                                                                                                                                                   this.p1 = p1;
                                                                                                                                                                                   this.otherAtoms = ((x10.array.DistArray)(otherAtoms));
                                                                                                                                                                               }}
                        
                        }
                        
                    public static class $Closure$9
                    extends x10.core.Ref
                      implements x10.core.fun.VoidFun_0_0,
                                  x10.x10rt.X10JavaSerializable 
                    {
                        private static final long serialVersionUID = 1L;
                        private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, $Closure$9.class);
                        
                        public static final x10.rtt.RuntimeType<$Closure$9> $RTT = new x10.rtt.StaticVoidFunType<$Closure$9>(
                        /* base class */$Closure$9.class
                        , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                        );
                        public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                        
                        
                        public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$9 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                        
                            x10.array.Dist __lowerer__var__0__ = (x10.array.Dist) $deserializer.readRef();
                            $_obj.__lowerer__var__0__ = __lowerer__var__0__;
                            x10.array.DistArray atoms = (x10.array.DistArray) $deserializer.readRef();
                            $_obj.atoms = atoms;
                            x10.array.DistArray otherAtoms = (x10.array.DistArray) $deserializer.readRef();
                            $_obj.otherAtoms = otherAtoms;
                            return $_obj;
                            
                        }
                        
                        public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                        
                            $Closure$9 $_obj = new $Closure$9((java.lang.System[]) null);
                            $deserializer.record_reference($_obj);
                            return $_deserialize_body($_obj, $deserializer);
                            
                        }
                        
                        public short $_get_serialization_id() {
                        
                             return $_serialization_id;
                            
                        }
                        
                        public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                        
                            if (__lowerer__var__0__ instanceof x10.x10rt.X10JavaSerializable) {
                            $serializer.write( (x10.x10rt.X10JavaSerializable) this.__lowerer__var__0__);
                            } else {
                            $serializer.write(this.__lowerer__var__0__);
                            }
                            if (atoms instanceof x10.x10rt.X10JavaSerializable) {
                            $serializer.write( (x10.x10rt.X10JavaSerializable) this.atoms);
                            } else {
                            $serializer.write(this.atoms);
                            }
                            if (otherAtoms instanceof x10.x10rt.X10JavaSerializable) {
                            $serializer.write( (x10.x10rt.X10JavaSerializable) this.otherAtoms);
                            } else {
                            $serializer.write(this.otherAtoms);
                            }
                            
                        }
                        
                        // constructor just for allocation
                        public $Closure$9(final java.lang.System[] $dummy) { 
                        super($dummy);
                        }
                        
                            
                            public void
                              $apply(
                              ){
                                
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
for (
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.lang.Iterator<x10.array.Point> id33250 =
                                                                                                                                                         this.
                                                                                                                                                           __lowerer__var__0__.restriction(((x10.lang.Place)(x10.lang.Runtime.home()))).
                                                                                                                                                           region.iterator();
                                                                                                                                                       ((x10.lang.Iterator<x10.array.Point>)id33250).hasNext$O();
                                                                                                                                                       ) {
                                    
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final x10.array.Point id4 =
                                      ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)id33250).next$G()));
                                    
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
final int p1 =
                                      id4.$apply$O((int)(0));
                                    
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/ElectrostaticDirectMethod.x10"
x10.lang.Runtime.runAsync(((x10.lang.Place)(x10.lang.Runtime.home())),
                                                                                                                                                                                ((x10.core.fun.VoidFun_0_0)(new au.edu.anu.chem.mm.ElectrostaticDirectMethod.$Closure$8(this.
                                                                                                                                                                                                                                                                          atoms,
                                                                                                                                                                                                                                                                        p1,
                                                                                                                                                                                                                                                                        this.
                                                                                                                                                                                                                                                                          otherAtoms,(java.lang.Class<?>) null))));
                                }
                            }
                            
                            public x10.array.Dist
                              __lowerer__var__0__;
                            public x10.array.DistArray<x10.array.Array<au.edu.anu.chem.PointCharge>>
                              atoms;
                            public x10.array.DistArray<x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>>
                              otherAtoms;
                            
                            // creation method for java code
                            public static au.edu.anu.chem.mm.ElectrostaticDirectMethod.$Closure$9 $make(final x10.array.Dist __lowerer__var__0__,
                                                                                                        final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.PointCharge>> atoms,
                                                                                                        final x10.array.DistArray<x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>> otherAtoms,java.lang.Class<?> $dummy0){return new $Closure$9(__lowerer__var__0__,atoms,otherAtoms,(java.lang.Class<?>) null);}
                            public $Closure$9(final x10.array.Dist __lowerer__var__0__,
                                              final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.PointCharge>> atoms,
                                              final x10.array.DistArray<x10.array.Array<x10.array.Array<au.edu.anu.chem.PointCharge>>> otherAtoms,java.lang.Class<?> $dummy0) { {
                                                                                                                                                                                       this.__lowerer__var__0__ = ((x10.array.Dist)(__lowerer__var__0__));
                                                                                                                                                                                       this.atoms = ((x10.array.DistArray)(atoms));
                                                                                                                                                                                       this.otherAtoms = ((x10.array.DistArray)(otherAtoms));
                                                                                                                                                                                   }}
                            
                        }
                        
                    
                }
                