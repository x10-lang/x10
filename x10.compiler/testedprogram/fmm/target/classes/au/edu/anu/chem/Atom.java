package au.edu.anu.chem;


public class Atom
extends x10.core.Ref
  implements x10.util.concurrent.Atomic,
              x10.x10rt.X10JavaSerializable 
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Atom.class);
    
    public static final x10.rtt.RuntimeType<Atom> $RTT = new x10.rtt.NamedType<Atom>(
    "au.edu.anu.chem.Atom", /* base class */Atom.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Atom $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        x10x.vector.Point3d centre = (x10x.vector.Point3d) $deserializer.readRef();
        $_obj.centre = centre;
        java.lang.String symbol = (java.lang.String) $deserializer.readRef();
        $_obj.symbol = symbol;
        x10.util.ArrayList bonds = (x10.util.ArrayList) $deserializer.readRef();
        $_obj.bonds = bonds;
        $_obj.index = $deserializer.readInt();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Atom $_obj = new Atom((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        if (centre instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.centre);
        } else {
        $serializer.write(this.centre);
        }
        $serializer.write(this.symbol);
        if (bonds instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.bonds);
        } else {
        $serializer.write(this.bonds);
        }
        $serializer.write(this.index);
        
    }
    
    // constructor just for allocation
    public Atom(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
public int
          X10$object_lock_id0;
        
        
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
public x10.util.concurrent.OrderedLock
                                                                                                    getOrderedLock(
                                                                                                    ){
            
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final int t55295 =
              this.
                X10$object_lock_id0;
            
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final x10.util.concurrent.OrderedLock t55296 =
              x10.util.concurrent.OrderedLock.getLock((int)(t55295));
            
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
return t55296;
        }
        
        
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
public static int
          X10$class_lock_id1 =
          x10.util.concurrent.OrderedLock.createNewLockID();
        
        
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
public static x10.util.concurrent.OrderedLock
                                                                                                    getStaticOrderedLock(
                                                                                                    ){
            
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final int t55297 =
              au.edu.anu.chem.Atom.X10$class_lock_id1;
            
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final x10.util.concurrent.OrderedLock t55298 =
              x10.util.concurrent.OrderedLock.getLock((int)(t55297));
            
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
return t55298;
        }
        
        
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
/** The location of the atomic nucleus. */public x10x.vector.Point3d
          centre;
        
//#line 30 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
/** The symbol for this atom species. */public java.lang.String
          symbol;
        
//#line 33 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
/** A list of atoms to which this atom is bonded. */public x10.util.ArrayList<x10.util.Pair<au.edu.anu.chem.BondType, au.edu.anu.chem.Atom>>
          bonds;
        
        
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
// creation method for java code
        public static au.edu.anu.chem.Atom $make(final java.lang.String symbol,
                                                 final x10x.vector.Point3d centre){return new au.edu.anu.chem.Atom((java.lang.System[]) null).$init(symbol,centre);}
        
        // constructor for non-virtual call
        final public au.edu.anu.chem.Atom au$edu$anu$chem$Atom$$init$S(final java.lang.String symbol,
                                                                       final x10x.vector.Point3d centre) { {
                                                                                                                  
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"

                                                                                                                  
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"

                                                                                                                  
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.__fieldInitializers25991();
                                                                                                                  
//#line 36 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.symbol = ((java.lang.String)(symbol));
                                                                                                                  
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.centre = ((x10x.vector.Point3d)(centre));
                                                                                                              }
                                                                                                              return this;
                                                                                                              }
        
        // constructor
        public au.edu.anu.chem.Atom $init(final java.lang.String symbol,
                                          final x10x.vector.Point3d centre){return au$edu$anu$chem$Atom$$init$S(symbol,centre);}
        
        
        
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
// creation method for java code
        public static au.edu.anu.chem.Atom $make(final java.lang.String symbol,
                                                 final x10x.vector.Point3d centre,
                                                 final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.chem.Atom((java.lang.System[]) null).$init(symbol,centre,paramLock);}
        
        // constructor for non-virtual call
        final public au.edu.anu.chem.Atom au$edu$anu$chem$Atom$$init$S(final java.lang.String symbol,
                                                                       final x10x.vector.Point3d centre,
                                                                       final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                 
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"

                                                                                                                                 
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"

                                                                                                                                 
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.__fieldInitializers25991();
                                                                                                                                 
//#line 36 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.symbol = ((java.lang.String)(symbol));
                                                                                                                                 
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.centre = ((x10x.vector.Point3d)(centre));
                                                                                                                                 
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final int t55299 =
                                                                                                                                   paramLock.getIndex();
                                                                                                                                 
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.X10$object_lock_id0 = ((int)(t55299));
                                                                                                                             }
                                                                                                                             return this;
                                                                                                                             }
        
        // constructor
        public au.edu.anu.chem.Atom $init(final java.lang.String symbol,
                                          final x10x.vector.Point3d centre,
                                          final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$chem$Atom$$init$S(symbol,centre,paramLock);}
        
        
        
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
// creation method for java code
        public static au.edu.anu.chem.Atom $make(final x10x.vector.Point3d centre){return new au.edu.anu.chem.Atom((java.lang.System[]) null).$init(centre);}
        
        // constructor for non-virtual call
        final public au.edu.anu.chem.Atom au$edu$anu$chem$Atom$$init$S(final x10x.vector.Point3d centre) { {
                                                                                                                  
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"

                                                                                                                  
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"

                                                                                                                  
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.__fieldInitializers25991();
                                                                                                                  
//#line 41 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.symbol = ((java.lang.String)(""));
                                                                                                                  
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.centre = ((x10x.vector.Point3d)(centre));
                                                                                                              }
                                                                                                              return this;
                                                                                                              }
        
        // constructor
        public au.edu.anu.chem.Atom $init(final x10x.vector.Point3d centre){return au$edu$anu$chem$Atom$$init$S(centre);}
        
        
        
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
// creation method for java code
        public static au.edu.anu.chem.Atom $make(final x10x.vector.Point3d centre,
                                                 final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.chem.Atom((java.lang.System[]) null).$init(centre,paramLock);}
        
        // constructor for non-virtual call
        final public au.edu.anu.chem.Atom au$edu$anu$chem$Atom$$init$S(final x10x.vector.Point3d centre,
                                                                       final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                 
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"

                                                                                                                                 
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"

                                                                                                                                 
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.__fieldInitializers25991();
                                                                                                                                 
//#line 41 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.symbol = ((java.lang.String)(""));
                                                                                                                                 
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.centre = ((x10x.vector.Point3d)(centre));
                                                                                                                                 
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final int t55300 =
                                                                                                                                   paramLock.getIndex();
                                                                                                                                 
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.X10$object_lock_id0 = ((int)(t55300));
                                                                                                                             }
                                                                                                                             return this;
                                                                                                                             }
        
        // constructor
        public au.edu.anu.chem.Atom $init(final x10x.vector.Point3d centre,
                                          final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$chem$Atom$$init$S(centre,paramLock);}
        
        
        
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
// creation method for java code
        public static au.edu.anu.chem.Atom $make(final au.edu.anu.chem.Atom atom){return new au.edu.anu.chem.Atom((java.lang.System[]) null).$init(atom);}
        
        // constructor for non-virtual call
        final public au.edu.anu.chem.Atom au$edu$anu$chem$Atom$$init$S(final au.edu.anu.chem.Atom atom) { {
                                                                                                                 
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"

                                                                                                                 
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"

                                                                                                                 
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.__fieldInitializers25991();
                                                                                                                 
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final java.lang.String t55301 =
                                                                                                                   ((java.lang.String)(atom.
                                                                                                                                         symbol));
                                                                                                                 
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.symbol = ((java.lang.String)(t55301));
                                                                                                                 
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final x10x.vector.Point3d t55302 =
                                                                                                                   ((x10x.vector.Point3d)(atom.
                                                                                                                                            centre));
                                                                                                                 
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.centre = ((x10x.vector.Point3d)(t55302));
                                                                                                                 
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final x10.util.ArrayList<x10.util.Pair<au.edu.anu.chem.BondType, au.edu.anu.chem.Atom>> t55303 =
                                                                                                                   ((x10.util.ArrayList)(atom.
                                                                                                                                           bonds));
                                                                                                                 
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final boolean t55306 =
                                                                                                                   ((t55303) != (null));
                                                                                                                 
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
if (t55306) {
                                                                                                                     
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final x10.util.ArrayList<x10.util.Pair<au.edu.anu.chem.BondType, au.edu.anu.chem.Atom>> t55304 =
                                                                                                                       ((x10.util.ArrayList)(atom.
                                                                                                                                               bonds));
                                                                                                                     
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final x10.util.ArrayList<x10.util.Pair<au.edu.anu.chem.BondType, au.edu.anu.chem.Atom>> t55305 =
                                                                                                                       ((x10.util.ArrayList)(x10.util.ArrayList.<x10.util.Pair<au.edu.anu.chem.BondType, au.edu.anu.chem.Atom>>make_0_$_x10$util$ArrayList_T_$(new x10.rtt.ParameterizedType(x10.util.Pair.$RTT, au.edu.anu.chem.BondType.$RTT, au.edu.anu.chem.Atom.$RTT), ((x10.util.Container)(t55304)))));
                                                                                                                     
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.bonds = ((x10.util.ArrayList)(t55305));
                                                                                                                 }
                                                                                                             }
                                                                                                             return this;
                                                                                                             }
        
        // constructor
        public au.edu.anu.chem.Atom $init(final au.edu.anu.chem.Atom atom){return au$edu$anu$chem$Atom$$init$S(atom);}
        
        
        
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
// creation method for java code
        public static au.edu.anu.chem.Atom $make(final au.edu.anu.chem.Atom atom,
                                                 final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.chem.Atom((java.lang.System[]) null).$init(atom,paramLock);}
        
        // constructor for non-virtual call
        final public au.edu.anu.chem.Atom au$edu$anu$chem$Atom$$init$S(final au.edu.anu.chem.Atom atom,
                                                                       final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                 
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"

                                                                                                                                 
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"

                                                                                                                                 
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.__fieldInitializers25991();
                                                                                                                                 
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final java.lang.String t55307 =
                                                                                                                                   ((java.lang.String)(atom.
                                                                                                                                                         symbol));
                                                                                                                                 
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.symbol = ((java.lang.String)(t55307));
                                                                                                                                 
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final x10x.vector.Point3d t55308 =
                                                                                                                                   ((x10x.vector.Point3d)(atom.
                                                                                                                                                            centre));
                                                                                                                                 
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.centre = ((x10x.vector.Point3d)(t55308));
                                                                                                                                 
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final x10.util.ArrayList<x10.util.Pair<au.edu.anu.chem.BondType, au.edu.anu.chem.Atom>> t55309 =
                                                                                                                                   ((x10.util.ArrayList)(atom.
                                                                                                                                                           bonds));
                                                                                                                                 
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final boolean t55312 =
                                                                                                                                   ((t55309) != (null));
                                                                                                                                 
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
if (t55312) {
                                                                                                                                     
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final x10.util.ArrayList<x10.util.Pair<au.edu.anu.chem.BondType, au.edu.anu.chem.Atom>> t55310 =
                                                                                                                                       ((x10.util.ArrayList)(atom.
                                                                                                                                                               bonds));
                                                                                                                                     
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final x10.util.ArrayList<x10.util.Pair<au.edu.anu.chem.BondType, au.edu.anu.chem.Atom>> t55311 =
                                                                                                                                       ((x10.util.ArrayList)(x10.util.ArrayList.<x10.util.Pair<au.edu.anu.chem.BondType, au.edu.anu.chem.Atom>>make_0_$_x10$util$ArrayList_T_$(new x10.rtt.ParameterizedType(x10.util.Pair.$RTT, au.edu.anu.chem.BondType.$RTT, au.edu.anu.chem.Atom.$RTT), ((x10.util.Container)(t55310)))));
                                                                                                                                     
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.bonds = ((x10.util.ArrayList)(t55311));
                                                                                                                                 }
                                                                                                                                 
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final int t55313 =
                                                                                                                                   paramLock.getIndex();
                                                                                                                                 
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.X10$object_lock_id0 = ((int)(t55313));
                                                                                                                             }
                                                                                                                             return this;
                                                                                                                             }
        
        // constructor
        public au.edu.anu.chem.Atom $init(final au.edu.anu.chem.Atom atom,
                                          final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$chem$Atom$$init$S(atom,paramLock);}
        
        
        
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
public x10.util.ArrayList<x10.util.Pair<au.edu.anu.chem.BondType, au.edu.anu.chem.Atom>>
                                                                                                    getBonds(
                                                                                                    ){
            
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final x10.util.ArrayList<x10.util.Pair<au.edu.anu.chem.BondType, au.edu.anu.chem.Atom>> t55314 =
              ((x10.util.ArrayList)(bonds));
            
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
return t55314;
        }
        
        
//#line 65 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
public void
                                                                                                    addBond(
                                                                                                    final au.edu.anu.chem.BondType bondType,
                                                                                                    final au.edu.anu.chem.Atom atom){
            
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.addBondInternal(((au.edu.anu.chem.BondType)(bondType)),
                                                                                                                           ((au.edu.anu.chem.Atom)(atom)));
            
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
atom.addBondInternal(((au.edu.anu.chem.BondType)(bondType)),
                                                                                                                           ((au.edu.anu.chem.Atom)(this)));
        }
        
        
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
public void
                                                                                                    addBondInternal(
                                                                                                    final au.edu.anu.chem.BondType bondType,
                                                                                                    final au.edu.anu.chem.Atom atom){
            
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final x10.util.ArrayList<x10.util.Pair<au.edu.anu.chem.BondType, au.edu.anu.chem.Atom>> t55315 =
              ((x10.util.ArrayList)(bonds));
            
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final boolean t55317 =
              ((t55315) == (null));
            
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
if (t55317) {
                
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final x10.util.ArrayList<x10.util.Pair<au.edu.anu.chem.BondType, au.edu.anu.chem.Atom>> alloc26133 =
                  ((x10.util.ArrayList)(new x10.util.ArrayList<x10.util.Pair<au.edu.anu.chem.BondType, au.edu.anu.chem.Atom>>((java.lang.System[]) null, new x10.rtt.ParameterizedType(x10.util.Pair.$RTT, au.edu.anu.chem.BondType.$RTT, au.edu.anu.chem.Atom.$RTT))));
                
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
alloc26133.$init();
                
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final x10.util.ArrayList<x10.util.Pair<au.edu.anu.chem.BondType, au.edu.anu.chem.Atom>> t55316 =
                  ((x10.util.ArrayList<x10.util.Pair<au.edu.anu.chem.BondType, au.edu.anu.chem.Atom>>)
                    alloc26133);
                
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.bonds = ((x10.util.ArrayList)(t55316));
            }
            
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final x10.util.ArrayList<x10.util.Pair<au.edu.anu.chem.BondType, au.edu.anu.chem.Atom>> t55318 =
              ((x10.util.ArrayList)(bonds));
            
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final x10.util.Pair<au.edu.anu.chem.BondType, au.edu.anu.chem.Atom> alloc26134 =
              new x10.util.Pair<au.edu.anu.chem.BondType, au.edu.anu.chem.Atom>((java.lang.System[]) null, au.edu.anu.chem.BondType.$RTT, au.edu.anu.chem.Atom.$RTT);
            
//#line 21 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/Pair.x10"
final au.edu.anu.chem.BondType first55292 =
              ((au.edu.anu.chem.BondType)(bondType));
            
//#line 21 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/Pair.x10"
final au.edu.anu.chem.Atom second55293 =
              ((au.edu.anu.chem.Atom)(atom));
            
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/Pair.x10"
alloc26134.first = ((au.edu.anu.chem.BondType)(first55292));
            
//#line 23 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/Pair.x10"
alloc26134.second = ((au.edu.anu.chem.Atom)(second55293));
            
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
((x10.util.ArrayList<x10.util.Pair<au.edu.anu.chem.BondType, au.edu.anu.chem.Atom>>)t55318).add_0_$$x10$util$ArrayList_T$O(((x10.util.Pair)(alloc26134)));
        }
        
        
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
/** index of this atom in a molecule */public int
          index;
        
        
//#line 84 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
public void
                                                                                                    setIndex(
                                                                                                    final int i){
            
//#line 84 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.index = i;
        }
        
        
//#line 85 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
public int
                                                                                                    getIndex$O(
                                                                                                    ){
            
//#line 85 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final int t55319 =
              index;
            
//#line 85 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
return t55319;
        }
        
        
//#line 87 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
public java.lang.String
                                                                                                    toString(
                                                                                                    ){
            
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final java.lang.String t55320 =
              ((java.lang.String)(symbol));
            
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final java.lang.String t55322 =
              ((t55320) + (" "));
            
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final x10x.vector.Point3d t55321 =
              ((x10x.vector.Point3d)(centre));
            
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final double t55323 =
              t55321.
                i;
            
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final java.lang.String t55324 =
              ((t55322) + ((x10.core.Double.$box(t55323))));
            
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final java.lang.String t55326 =
              ((t55324) + (" "));
            
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final x10x.vector.Point3d t55325 =
              ((x10x.vector.Point3d)(centre));
            
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final double t55327 =
              t55325.
                j;
            
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final java.lang.String t55328 =
              ((t55326) + ((x10.core.Double.$box(t55327))));
            
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final java.lang.String t55330 =
              ((t55328) + (" "));
            
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final x10x.vector.Point3d t55329 =
              ((x10x.vector.Point3d)(centre));
            
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final double t55331 =
              t55329.
                k;
            
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final java.lang.String t55332 =
              ((t55330) + ((x10.core.Double.$box(t55331))));
            
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
return t55332;
        }
        
        
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final public au.edu.anu.chem.Atom
                                                                                                    au$edu$anu$chem$Atom$$au$edu$anu$chem$Atom$this(
                                                                                                    ){
            
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
return au.edu.anu.chem.Atom.this;
        }
        
        
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final private void
                                                                                                    __fieldInitializers25991(
                                                                                                    ){
            
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.X10$object_lock_id0 = -1;
            
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
final x10x.vector.Point3d t55333 =
              (x10x.vector.Point3d) x10.rtt.Types.zeroValue(x10x.vector.Point3d.$RTT);
            
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.centre = t55333;
            
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.bonds = null;
            
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10"
this.index = 0;
        }
        
        final public static void
          __fieldInitializers25991$P(
          final au.edu.anu.chem.Atom Atom){
            Atom.__fieldInitializers25991();
        }
    
}
