package au.edu.anu.chem.mm;


public class MMAtom
extends au.edu.anu.chem.Atom
  implements x10.util.concurrent.Atomic,
              x10.x10rt.X10JavaSerializable 
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, MMAtom.class);
    
    public static final x10.rtt.RuntimeType<MMAtom> $RTT = new x10.rtt.NamedType<MMAtom>(
    "au.edu.anu.chem.mm.MMAtom", /* base class */MMAtom.class
    , /* parents */ new x10.rtt.Type[] {au.edu.anu.chem.Atom.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(MMAtom $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        au.edu.anu.chem.Atom.$_deserialize_body($_obj, $deserializer);
        x10x.vector.Vector3d force = (x10x.vector.Vector3d) $deserializer.readRef();
        $_obj.force = force;
        x10x.vector.Vector3d velocity = (x10x.vector.Vector3d) $deserializer.readRef();
        $_obj.velocity = velocity;
        $_obj.charge = $deserializer.readDouble();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        MMAtom $_obj = new MMAtom((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        if (force instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.force);
        } else {
        $serializer.write(this.force);
        }
        if (velocity instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.velocity);
        } else {
        $serializer.write(this.velocity);
        }
        $serializer.write(this.charge);
        
    }
    
    // constructor just for allocation
    public MMAtom(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
public int
          X10$object_lock_id0;
        
        
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
public x10.util.concurrent.OrderedLock
                                                                                                         getOrderedLock(
                                                                                                         ){
            
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final int t26149 =
              this.
                X10$object_lock_id0;
            
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final x10.util.concurrent.OrderedLock t26150 =
              x10.util.concurrent.OrderedLock.getLock((int)(t26149));
            
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
return t26150;
        }
        
        
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
public static int
          X10$class_lock_id1 =
          x10.util.concurrent.OrderedLock.createNewLockID();
        
        
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
public static x10.util.concurrent.OrderedLock
                                                                                                         getStaticOrderedLock(
                                                                                                         ){
            
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final int t26151 =
              au.edu.anu.chem.mm.MMAtom.X10$class_lock_id1;
            
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final x10.util.concurrent.OrderedLock t26152 =
              x10.util.concurrent.OrderedLock.getLock((int)(t26151));
            
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
return t26152;
        }
        
        
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
/** The current force acting upon this atom. */public x10x.vector.Vector3d
          force;
        
//#line 29 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
/** The current velocity of this atom. */public x10x.vector.Vector3d
          velocity;
        
//#line 32 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
/** The effective charge in atomic units. */public double
          charge;
        
        
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
// creation method for java code
        public static au.edu.anu.chem.mm.MMAtom $make(final java.lang.String symbol,
                                                      final x10x.vector.Point3d centre,
                                                      final double charge){return new au.edu.anu.chem.mm.MMAtom((java.lang.System[]) null).$init(symbol,centre,charge);}
        
        // constructor for non-virtual call
        final public au.edu.anu.chem.mm.MMAtom au$edu$anu$chem$mm$MMAtom$$init$S(final java.lang.String symbol,
                                                                                 final x10x.vector.Point3d centre,
                                                                                 final double charge) { {
                                                                                                               
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
super.$init(((java.lang.String)(symbol)),
                                                                                                                                                                                                                          ((x10x.vector.Point3d)(centre)));
                                                                                                               
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"

                                                                                                               
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.__fieldInitializers25761();
                                                                                                               
//#line 36 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.charge = charge;
                                                                                                               
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final x10x.vector.Vector3d t26153 =
                                                                                                                 ((x10x.vector.Vector3d)(x10x.vector.Vector3d.getInitialized$NULL()));
                                                                                                               
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.force = ((x10x.vector.Vector3d)(t26153));
                                                                                                               
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final x10x.vector.Vector3d t26154 =
                                                                                                                 ((x10x.vector.Vector3d)(x10x.vector.Vector3d.getInitialized$NULL()));
                                                                                                               
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.velocity = ((x10x.vector.Vector3d)(t26154));
                                                                                                           }
                                                                                                           return this;
                                                                                                           }
        
        // constructor
        public au.edu.anu.chem.mm.MMAtom $init(final java.lang.String symbol,
                                               final x10x.vector.Point3d centre,
                                               final double charge){return au$edu$anu$chem$mm$MMAtom$$init$S(symbol,centre,charge);}
        
        
        
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
// creation method for java code
        public static au.edu.anu.chem.mm.MMAtom $make(final java.lang.String symbol,
                                                      final x10x.vector.Point3d centre,
                                                      final double charge,
                                                      final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.chem.mm.MMAtom((java.lang.System[]) null).$init(symbol,centre,charge,paramLock);}
        
        // constructor for non-virtual call
        final public au.edu.anu.chem.mm.MMAtom au$edu$anu$chem$mm$MMAtom$$init$S(final java.lang.String symbol,
                                                                                 final x10x.vector.Point3d centre,
                                                                                 final double charge,
                                                                                 final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                           
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
super.$init(((java.lang.String)(symbol)),
                                                                                                                                                                                                                                                      ((x10x.vector.Point3d)(centre)));
                                                                                                                                           
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"

                                                                                                                                           
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.__fieldInitializers25761();
                                                                                                                                           
//#line 36 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.charge = charge;
                                                                                                                                           
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final x10x.vector.Vector3d t26155 =
                                                                                                                                             ((x10x.vector.Vector3d)(x10x.vector.Vector3d.getInitialized$NULL()));
                                                                                                                                           
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.force = ((x10x.vector.Vector3d)(t26155));
                                                                                                                                           
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final x10x.vector.Vector3d t26156 =
                                                                                                                                             ((x10x.vector.Vector3d)(x10x.vector.Vector3d.getInitialized$NULL()));
                                                                                                                                           
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.velocity = ((x10x.vector.Vector3d)(t26156));
                                                                                                                                           
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final int t26157 =
                                                                                                                                             paramLock.getIndex();
                                                                                                                                           
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.X10$object_lock_id0 = ((int)(t26157));
                                                                                                                                       }
                                                                                                                                       return this;
                                                                                                                                       }
        
        // constructor
        public au.edu.anu.chem.mm.MMAtom $init(final java.lang.String symbol,
                                               final x10x.vector.Point3d centre,
                                               final double charge,
                                               final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$chem$mm$MMAtom$$init$S(symbol,centre,charge,paramLock);}
        
        
        
//#line 41 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
// creation method for java code
        public static au.edu.anu.chem.mm.MMAtom $make(final x10x.vector.Point3d centre,
                                                      final double charge){return new au.edu.anu.chem.mm.MMAtom((java.lang.System[]) null).$init(centre,charge);}
        
        // constructor for non-virtual call
        final public au.edu.anu.chem.mm.MMAtom au$edu$anu$chem$mm$MMAtom$$init$S(final x10x.vector.Point3d centre,
                                                                                 final double charge) { {
                                                                                                               
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
super.$init(((x10x.vector.Point3d)(centre)));
                                                                                                               
//#line 41 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"

                                                                                                               
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.__fieldInitializers25761();
                                                                                                               
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.charge = charge;
                                                                                                               
//#line 44 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final x10x.vector.Vector3d t26158 =
                                                                                                                 ((x10x.vector.Vector3d)(x10x.vector.Vector3d.getInitialized$NULL()));
                                                                                                               
//#line 44 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.force = ((x10x.vector.Vector3d)(t26158));
                                                                                                               
//#line 45 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final x10x.vector.Vector3d t26159 =
                                                                                                                 ((x10x.vector.Vector3d)(x10x.vector.Vector3d.getInitialized$NULL()));
                                                                                                               
//#line 45 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.velocity = ((x10x.vector.Vector3d)(t26159));
                                                                                                           }
                                                                                                           return this;
                                                                                                           }
        
        // constructor
        public au.edu.anu.chem.mm.MMAtom $init(final x10x.vector.Point3d centre,
                                               final double charge){return au$edu$anu$chem$mm$MMAtom$$init$S(centre,charge);}
        
        
        
//#line 41 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
// creation method for java code
        public static au.edu.anu.chem.mm.MMAtom $make(final x10x.vector.Point3d centre,
                                                      final double charge,
                                                      final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.chem.mm.MMAtom((java.lang.System[]) null).$init(centre,charge,paramLock);}
        
        // constructor for non-virtual call
        final public au.edu.anu.chem.mm.MMAtom au$edu$anu$chem$mm$MMAtom$$init$S(final x10x.vector.Point3d centre,
                                                                                 final double charge,
                                                                                 final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                           
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
super.$init(((x10x.vector.Point3d)(centre)));
                                                                                                                                           
//#line 41 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"

                                                                                                                                           
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.__fieldInitializers25761();
                                                                                                                                           
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.charge = charge;
                                                                                                                                           
//#line 44 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final x10x.vector.Vector3d t26160 =
                                                                                                                                             ((x10x.vector.Vector3d)(x10x.vector.Vector3d.getInitialized$NULL()));
                                                                                                                                           
//#line 44 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.force = ((x10x.vector.Vector3d)(t26160));
                                                                                                                                           
//#line 45 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final x10x.vector.Vector3d t26161 =
                                                                                                                                             ((x10x.vector.Vector3d)(x10x.vector.Vector3d.getInitialized$NULL()));
                                                                                                                                           
//#line 45 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.velocity = ((x10x.vector.Vector3d)(t26161));
                                                                                                                                           
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final int t26162 =
                                                                                                                                             paramLock.getIndex();
                                                                                                                                           
//#line 41 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.X10$object_lock_id0 = ((int)(t26162));
                                                                                                                                       }
                                                                                                                                       return this;
                                                                                                                                       }
        
        // constructor
        public au.edu.anu.chem.mm.MMAtom $init(final x10x.vector.Point3d centre,
                                               final double charge,
                                               final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$chem$mm$MMAtom$$init$S(centre,charge,paramLock);}
        
        
        
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
// creation method for java code
        public static au.edu.anu.chem.mm.MMAtom $make(final au.edu.anu.chem.mm.MMAtom atom){return new au.edu.anu.chem.mm.MMAtom((java.lang.System[]) null).$init(atom);}
        
        // constructor for non-virtual call
        final public au.edu.anu.chem.mm.MMAtom au$edu$anu$chem$mm$MMAtom$$init$S(final au.edu.anu.chem.mm.MMAtom atom) { {
                                                                                                                                
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
super.$init(((au.edu.anu.chem.Atom)(atom)));
                                                                                                                                
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"

                                                                                                                                
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.__fieldInitializers25761();
                                                                                                                                
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final double t26163 =
                                                                                                                                  atom.
                                                                                                                                    charge;
                                                                                                                                
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.charge = t26163;
                                                                                                                                
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final x10x.vector.Vector3d t26164 =
                                                                                                                                  ((x10x.vector.Vector3d)(atom.
                                                                                                                                                            force));
                                                                                                                                
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.force = ((x10x.vector.Vector3d)(t26164));
                                                                                                                                
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final x10x.vector.Vector3d t26165 =
                                                                                                                                  ((x10x.vector.Vector3d)(atom.
                                                                                                                                                            velocity));
                                                                                                                                
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.velocity = ((x10x.vector.Vector3d)(t26165));
                                                                                                                            }
                                                                                                                            return this;
                                                                                                                            }
        
        // constructor
        public au.edu.anu.chem.mm.MMAtom $init(final au.edu.anu.chem.mm.MMAtom atom){return au$edu$anu$chem$mm$MMAtom$$init$S(atom);}
        
        
        
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
// creation method for java code
        public static au.edu.anu.chem.mm.MMAtom $make(final au.edu.anu.chem.mm.MMAtom atom,
                                                      final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.chem.mm.MMAtom((java.lang.System[]) null).$init(atom,paramLock);}
        
        // constructor for non-virtual call
        final public au.edu.anu.chem.mm.MMAtom au$edu$anu$chem$mm$MMAtom$$init$S(final au.edu.anu.chem.mm.MMAtom atom,
                                                                                 final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                           
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
super.$init(((au.edu.anu.chem.Atom)(atom)));
                                                                                                                                           
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"

                                                                                                                                           
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.__fieldInitializers25761();
                                                                                                                                           
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final double t26166 =
                                                                                                                                             atom.
                                                                                                                                               charge;
                                                                                                                                           
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.charge = t26166;
                                                                                                                                           
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final x10x.vector.Vector3d t26167 =
                                                                                                                                             ((x10x.vector.Vector3d)(atom.
                                                                                                                                                                       force));
                                                                                                                                           
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.force = ((x10x.vector.Vector3d)(t26167));
                                                                                                                                           
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final x10x.vector.Vector3d t26168 =
                                                                                                                                             ((x10x.vector.Vector3d)(atom.
                                                                                                                                                                       velocity));
                                                                                                                                           
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.velocity = ((x10x.vector.Vector3d)(t26168));
                                                                                                                                           
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final int t26169 =
                                                                                                                                             paramLock.getIndex();
                                                                                                                                           
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.X10$object_lock_id0 = ((int)(t26169));
                                                                                                                                       }
                                                                                                                                       return this;
                                                                                                                                       }
        
        // constructor
        public au.edu.anu.chem.mm.MMAtom $init(final au.edu.anu.chem.mm.MMAtom atom,
                                               final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$chem$mm$MMAtom$$init$S(atom,paramLock);}
        
        
        
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
// creation method for java code
        public static au.edu.anu.chem.mm.MMAtom $make(final x10x.vector.Point3d centre){return new au.edu.anu.chem.mm.MMAtom((java.lang.System[]) null).$init(centre);}
        
        // constructor for non-virtual call
        final public au.edu.anu.chem.mm.MMAtom au$edu$anu$chem$mm$MMAtom$$init$S(final x10x.vector.Point3d centre) { {
                                                                                                                            
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.$init(((x10x.vector.Point3d)(centre)),
                                                                                                                                                                                                                                      ((double)(0.0)));
                                                                                                                        }
                                                                                                                        return this;
                                                                                                                        }
        
        // constructor
        public au.edu.anu.chem.mm.MMAtom $init(final x10x.vector.Point3d centre){return au$edu$anu$chem$mm$MMAtom$$init$S(centre);}
        
        
        
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
// creation method for java code
        public static au.edu.anu.chem.mm.MMAtom $make(final x10x.vector.Point3d centre,
                                                      final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.chem.mm.MMAtom((java.lang.System[]) null).$init(centre,paramLock);}
        
        // constructor for non-virtual call
        final public au.edu.anu.chem.mm.MMAtom au$edu$anu$chem$mm$MMAtom$$init$S(final x10x.vector.Point3d centre,
                                                                                 final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                           
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.$init(((x10x.vector.Point3d)(centre)),
                                                                                                                                                                                                                                                     ((double)(0.0)));
                                                                                                                                           
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final int t26170 =
                                                                                                                                             paramLock.getIndex();
                                                                                                                                           
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.X10$object_lock_id0 = ((int)(t26170));
                                                                                                                                       }
                                                                                                                                       return this;
                                                                                                                                       }
        
        // constructor
        public au.edu.anu.chem.mm.MMAtom $init(final x10x.vector.Point3d centre,
                                               final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$chem$mm$MMAtom$$init$S(centre,paramLock);}
        
        
        
//#line 63 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
public double
                                                                                                         pairEnergy$O(
                                                                                                         final au.edu.anu.chem.mm.MMAtom atom2){
            
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final double t26171 =
              charge;
            
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final double t26172 =
              atom2.
                charge;
            
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final double t26185 =
              ((t26171) * (((double)(t26172))));
            
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final x10x.vector.Point3d this26136 =
              ((x10x.vector.Point3d)(centre));
            
//#line 66 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d b26135 =
              ((x10x.vector.Point3d)(atom2.
                                       centre));
            
//#line 57 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d b26137 =
              ((x10x.vector.Point3d)(b26135));
            
//#line 57 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
double ret26141 =
               0;
            
//#line 58 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2617326260 =
              this26136.
                i;
            
//#line 58 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2617426261 =
              b26137.
                i;
            
//#line 58 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double di2613826262 =
              ((t2617326260) - (((double)(t2617426261))));
            
//#line 59 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2617526263 =
              this26136.
                j;
            
//#line 59 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2617626264 =
              b26137.
                j;
            
//#line 59 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double dj2613926265 =
              ((t2617526263) - (((double)(t2617626264))));
            
//#line 60 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2617726266 =
              this26136.
                k;
            
//#line 60 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2617826267 =
              b26137.
                k;
            
//#line 60 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double dk2614026268 =
              ((t2617726266) - (((double)(t2617826267))));
            
//#line 61 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2617926269 =
              ((di2613826262) * (((double)(di2613826262))));
            
//#line 62 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2618026270 =
              ((dj2613926265) * (((double)(dj2613926265))));
            
//#line 61 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2618126271 =
              ((t2617926269) + (((double)(t2618026270))));
            
//#line 63 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2618226272 =
              ((dk2614026268) * (((double)(dk2614026268))));
            
//#line 61 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t2618326273 =
              ((t2618126271) + (((double)(t2618226272))));
            
//#line 61 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
ret26141 = t2618326273;
            
//#line 57 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t26184 =
              ret26141;
            
//#line 67 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t26186 =
              java.lang.Math.sqrt(((double)(t26184)));
            
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final double t26187 =
              ((t26185) / (((double)(t26186))));
            
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
return t26187;
        }
        
        
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
public static class PackedRepresentation
                                                                                                       extends x10.core.Struct
                                                                                                         implements x10.util.concurrent.Atomic,
                                                                                                                     x10.x10rt.X10JavaSerializable 
                                                                                                       {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, PackedRepresentation.class);
            
            public static final x10.rtt.RuntimeType<PackedRepresentation> $RTT = new x10.rtt.NamedType<PackedRepresentation>(
            "au.edu.anu.chem.mm.MMAtom.PackedRepresentation", /* base class */PackedRepresentation.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.STRUCT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(PackedRepresentation $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                java.lang.String symbol = (java.lang.String) $deserializer.readRef();
                $_obj.symbol = symbol;
                $_obj.charge = $deserializer.readDouble();
                x10x.vector.Point3d centre = (x10x.vector.Point3d) $deserializer.readRef();
                $_obj.centre = centre;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                PackedRepresentation $_obj = new PackedRepresentation((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write(this.symbol);
                $serializer.write(this.charge);
                if (centre instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.centre);
                } else {
                $serializer.write(this.centre);
                }
                
            }
            
            // zero value constructor
            public PackedRepresentation(final java.lang.System $dummy) { this.symbol = null; this.charge = 0.0; this.centre = new x10x.vector.Point3d($dummy); }
            // constructor just for allocation
            public PackedRepresentation(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            
                
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
public java.lang.String
                  symbol;
                
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
public double
                  charge;
                
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
public x10x.vector.Point3d
                  centre;
                
                
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
public int
                  X10$object_lock_id0;
                
                
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
public x10.util.concurrent.OrderedLock
                                                                                                                 getOrderedLock(
                                                                                                                 ){
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final int t26188 =
                      this.
                        X10$object_lock_id0;
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final x10.util.concurrent.OrderedLock t26189 =
                      x10.util.concurrent.OrderedLock.getLock((int)(t26188));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
return t26189;
                }
                
                
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
public static int
                  X10$class_lock_id1 =
                  x10.util.concurrent.OrderedLock.createNewLockID();
                
                
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
public static x10.util.concurrent.OrderedLock
                                                                                                                 getStaticOrderedLock(
                                                                                                                 ){
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final int t26190 =
                      au.edu.anu.chem.mm.MMAtom.PackedRepresentation.X10$class_lock_id1;
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final x10.util.concurrent.OrderedLock t26191 =
                      x10.util.concurrent.OrderedLock.getLock((int)(t26190));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
return t26191;
                }
                
                
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
// creation method for java code
                public static au.edu.anu.chem.mm.MMAtom.PackedRepresentation $make(final java.lang.String symbol,
                                                                                   final double charge,
                                                                                   final x10x.vector.Point3d centre){return new au.edu.anu.chem.mm.MMAtom.PackedRepresentation((java.lang.System[]) null).$init(symbol,charge,centre);}
                
                // constructor for non-virtual call
                final public au.edu.anu.chem.mm.MMAtom.PackedRepresentation au$edu$anu$chem$mm$MMAtom$PackedRepresentation$$init$S(final java.lang.String symbol,
                                                                                                                                   final double charge,
                                                                                                                                   final x10x.vector.Point3d centre) { {
                                                                                                                                                                              
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.symbol = symbol;
                                                                                                                                                                              this.charge = charge;
                                                                                                                                                                              this.centre = centre;
                                                                                                                                                                              
                                                                                                                                                                              
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final au.edu.anu.chem.mm.MMAtom.PackedRepresentation this2614326274 =
                                                                                                                                                                                this;
                                                                                                                                                                              
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this2614326274.X10$object_lock_id0 = -1;
                                                                                                                                                                          }
                                                                                                                                                                          return this;
                                                                                                                                                                          }
                
                // constructor
                public au.edu.anu.chem.mm.MMAtom.PackedRepresentation $init(final java.lang.String symbol,
                                                                            final double charge,
                                                                            final x10x.vector.Point3d centre){return au$edu$anu$chem$mm$MMAtom$PackedRepresentation$$init$S(symbol,charge,centre);}
                
                
                
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
// creation method for java code
                public static au.edu.anu.chem.mm.MMAtom.PackedRepresentation $make(final java.lang.String symbol,
                                                                                   final double charge,
                                                                                   final x10x.vector.Point3d centre,
                                                                                   final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.chem.mm.MMAtom.PackedRepresentation((java.lang.System[]) null).$init(symbol,charge,centre,paramLock);}
                
                // constructor for non-virtual call
                final public au.edu.anu.chem.mm.MMAtom.PackedRepresentation au$edu$anu$chem$mm$MMAtom$PackedRepresentation$$init$S(final java.lang.String symbol,
                                                                                                                                   final double charge,
                                                                                                                                   final x10x.vector.Point3d centre,
                                                                                                                                   final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                                                                             
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.symbol = symbol;
                                                                                                                                                                                             this.charge = charge;
                                                                                                                                                                                             this.centre = centre;
                                                                                                                                                                                             
                                                                                                                                                                                             
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final au.edu.anu.chem.mm.MMAtom.PackedRepresentation this2614626275 =
                                                                                                                                                                                               this;
                                                                                                                                                                                             
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this2614626275.X10$object_lock_id0 = -1;
                                                                                                                                                                                             
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final int t26192 =
                                                                                                                                                                                               paramLock.getIndex();
                                                                                                                                                                                             
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.X10$object_lock_id0 = ((int)(t26192));
                                                                                                                                                                                         }
                                                                                                                                                                                         return this;
                                                                                                                                                                                         }
                
                // constructor
                public au.edu.anu.chem.mm.MMAtom.PackedRepresentation $init(final java.lang.String symbol,
                                                                            final double charge,
                                                                            final x10x.vector.Point3d centre,
                                                                            final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$chem$mm$MMAtom$PackedRepresentation$$init$S(symbol,charge,centre,paramLock);}
                
                
                
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final public java.lang.String
                                                                                                                 typeName$O(
                                                                                                                 ){try {return x10.rtt.Types.typeName(this);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
                
                
                
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final public java.lang.String
                                                                                                                 toString(
                                                                                                                 ){
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final java.lang.String t26193 =
                      "struct au.edu.anu.chem.mm.MMAtom.PackedRepresentation: symbol=";
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final java.lang.String t26194 =
                      ((java.lang.String)(this.
                                            symbol));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final java.lang.String t26195 =
                      ((t26193) + (t26194));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final java.lang.String t26196 =
                      ((t26195) + (" charge="));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final double t26197 =
                      this.
                        charge;
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final java.lang.String t26198 =
                      ((t26196) + ((x10.core.Double.$box(t26197))));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final java.lang.String t26199 =
                      ((t26198) + (" centre="));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final x10x.vector.Point3d t26200 =
                      ((x10x.vector.Point3d)(this.
                                               centre));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final java.lang.String t26201 =
                      ((t26199) + (t26200));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
return t26201;
                }
                
                
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final public int
                                                                                                                 hashCode(
                                                                                                                 ){
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
int result =
                      1;
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final int t26202 =
                      result;
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final int t26204 =
                      ((8191) * (((int)(t26202))));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final java.lang.String t26203 =
                      ((java.lang.String)(this.
                                            symbol));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final int t26205 =
                      (t26203).hashCode();
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final int t26206 =
                      ((t26204) + (((int)(t26205))));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
result = t26206;
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final int t26207 =
                      result;
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final int t26209 =
                      ((8191) * (((int)(t26207))));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final double t26208 =
                      this.
                        charge;
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final int t26210 =
                      x10.rtt.Types.hashCode(t26208);
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final int t26211 =
                      ((t26209) + (((int)(t26210))));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
result = t26211;
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final int t26212 =
                      result;
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final int t26214 =
                      ((8191) * (((int)(t26212))));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final x10x.vector.Point3d t26213 =
                      ((x10x.vector.Point3d)(this.
                                               centre));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final int t26215 =
                      t26213.hashCode();
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final int t26216 =
                      ((t26214) + (((int)(t26215))));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
result = t26216;
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final int t26217 =
                      result;
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
return t26217;
                }
                
                
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final public boolean
                                                                                                                 equals(
                                                                                                                 java.lang.Object other){
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final java.lang.Object t26218 =
                      other;
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final boolean t26219 =
                      au.edu.anu.chem.mm.MMAtom.PackedRepresentation.$RTT.instanceOf(t26218);
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final boolean t26220 =
                      !(t26219);
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
if (t26220) {
                        
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
return false;
                    }
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final java.lang.Object t26221 =
                      other;
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final au.edu.anu.chem.mm.MMAtom.PackedRepresentation t26222 =
                      ((au.edu.anu.chem.mm.MMAtom.PackedRepresentation)x10.rtt.Types.asStruct(au.edu.anu.chem.mm.MMAtom.PackedRepresentation.$RTT,t26221));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final boolean t26223 =
                      this.equals(((au.edu.anu.chem.mm.MMAtom.PackedRepresentation)(t26222)));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
return t26223;
                }
                
                
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final public boolean
                                                                                                                 equals(
                                                                                                                 au.edu.anu.chem.mm.MMAtom.PackedRepresentation other){
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final java.lang.String t26225 =
                      ((java.lang.String)(this.
                                            symbol));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final au.edu.anu.chem.mm.MMAtom.PackedRepresentation t26224 =
                      other;
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final java.lang.String t26226 =
                      ((java.lang.String)(t26224.
                                            symbol));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
boolean t26230 =
                      x10.rtt.Equality.equalsequals((t26225),(t26226));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
if (t26230) {
                        
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final double t26228 =
                          this.
                            charge;
                        
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final au.edu.anu.chem.mm.MMAtom.PackedRepresentation t26227 =
                          other;
                        
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final double t26229 =
                          t26227.
                            charge;
                        
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
t26230 = ((double) t26228) ==
                        ((double) t26229);
                    }
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
boolean t26234 =
                      t26230;
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
if (t26234) {
                        
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final x10x.vector.Point3d t26232 =
                          ((x10x.vector.Point3d)(this.
                                                   centre));
                        
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final au.edu.anu.chem.mm.MMAtom.PackedRepresentation t26231 =
                          other;
                        
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final x10x.vector.Point3d t26233 =
                          ((x10x.vector.Point3d)(t26231.
                                                   centre));
                        
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
t26234 = x10.rtt.Equality.equalsequals((t26232),(t26233));
                    }
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final boolean t26235 =
                      t26234;
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
return t26235;
                }
                
                
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final public boolean
                                                                                                                 _struct_equals$O(
                                                                                                                 java.lang.Object other){
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final java.lang.Object t26236 =
                      other;
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final boolean t26237 =
                      au.edu.anu.chem.mm.MMAtom.PackedRepresentation.$RTT.instanceOf(t26236);
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final boolean t26238 =
                      !(t26237);
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
if (t26238) {
                        
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
return false;
                    }
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final java.lang.Object t26239 =
                      other;
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final au.edu.anu.chem.mm.MMAtom.PackedRepresentation t26240 =
                      ((au.edu.anu.chem.mm.MMAtom.PackedRepresentation)x10.rtt.Types.asStruct(au.edu.anu.chem.mm.MMAtom.PackedRepresentation.$RTT,t26239));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final boolean t26241 =
                      this._struct_equals$O(((au.edu.anu.chem.mm.MMAtom.PackedRepresentation)(t26240)));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
return t26241;
                }
                
                
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final public boolean
                                                                                                                 _struct_equals$O(
                                                                                                                 au.edu.anu.chem.mm.MMAtom.PackedRepresentation other){
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final java.lang.String t26243 =
                      ((java.lang.String)(this.
                                            symbol));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final au.edu.anu.chem.mm.MMAtom.PackedRepresentation t26242 =
                      other;
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final java.lang.String t26244 =
                      ((java.lang.String)(t26242.
                                            symbol));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
boolean t26248 =
                      x10.rtt.Equality.equalsequals((t26243),(t26244));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
if (t26248) {
                        
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final double t26246 =
                          this.
                            charge;
                        
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final au.edu.anu.chem.mm.MMAtom.PackedRepresentation t26245 =
                          other;
                        
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final double t26247 =
                          t26245.
                            charge;
                        
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
t26248 = ((double) t26246) ==
                        ((double) t26247);
                    }
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
boolean t26252 =
                      t26248;
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
if (t26252) {
                        
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final x10x.vector.Point3d t26250 =
                          ((x10x.vector.Point3d)(this.
                                                   centre));
                        
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final au.edu.anu.chem.mm.MMAtom.PackedRepresentation t26249 =
                          other;
                        
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final x10x.vector.Point3d t26251 =
                          ((x10x.vector.Point3d)(t26249.
                                                   centre));
                        
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
t26252 = x10.rtt.Equality.equalsequals((t26250),(t26251));
                    }
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final boolean t26253 =
                      t26252;
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
return t26253;
                }
                
                
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final public au.edu.anu.chem.mm.MMAtom.PackedRepresentation
                                                                                                                 au$edu$anu$chem$mm$MMAtom$PackedRepresentation$$au$edu$anu$chem$mm$MMAtom$PackedRepresentation$this(
                                                                                                                 ){
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
return au.edu.anu.chem.mm.MMAtom.PackedRepresentation.this;
                }
                
                
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final private void
                                                                                                                 __fieldInitializers25760(
                                                                                                                 ){
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.X10$object_lock_id0 = -1;
                }
                
                final public static void
                  __fieldInitializers25760$P(
                  final au.edu.anu.chem.mm.MMAtom.PackedRepresentation PackedRepresentation){
                    PackedRepresentation.__fieldInitializers25760();
                }
            
        }
        
        
        
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
public au.edu.anu.chem.mm.MMAtom.PackedRepresentation
                                                                                                         getPackedRepresentation(
                                                                                                         ){
            
//#line 80 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final au.edu.anu.chem.mm.MMAtom.PackedRepresentation alloc25988 =
              new au.edu.anu.chem.mm.MMAtom.PackedRepresentation((java.lang.System[]) null);
            
//#line 80 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final java.lang.String t2625426276 =
              ((java.lang.String)(symbol));
            
//#line 80 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final double t2625526277 =
              charge;
            
//#line 80 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final x10x.vector.Point3d t2625626278 =
              ((x10x.vector.Point3d)(centre));
            
//#line 80 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final x10.util.concurrent.OrderedLock t2625726279 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 80 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
alloc25988.$init(((java.lang.String)(t2625426276)),
                                                                                                                            ((double)(t2625526277)),
                                                                                                                            t2625626278,
                                                                                                                            t2625726279);
            
//#line 80 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
return alloc25988;
        }
        
        
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final public au.edu.anu.chem.mm.MMAtom
                                                                                                         au$edu$anu$chem$mm$MMAtom$$au$edu$anu$chem$mm$MMAtom$this(
                                                                                                         ){
            
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
return au.edu.anu.chem.mm.MMAtom.this;
        }
        
        
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final private void
                                                                                                         __fieldInitializers25761(
                                                                                                         ){
            
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.X10$object_lock_id0 = -1;
            
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final x10x.vector.Vector3d t26258 =
              (x10x.vector.Vector3d) x10.rtt.Types.zeroValue(x10x.vector.Vector3d.$RTT);
            
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.force = t26258;
            
//#line 29 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
final x10x.vector.Vector3d t26259 =
              (x10x.vector.Vector3d) x10.rtt.Types.zeroValue(x10x.vector.Vector3d.$RTT);
            
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10"
this.velocity = t26259;
        }
        
        final public static void
          __fieldInitializers25761$P(
          final au.edu.anu.chem.mm.MMAtom MMAtom){
            MMAtom.__fieldInitializers25761();
        }
    
}
