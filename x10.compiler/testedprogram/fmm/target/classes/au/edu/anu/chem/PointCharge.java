package au.edu.anu.chem;


public class PointCharge
extends x10.core.Struct
  implements x10.util.concurrent.Atomic,
              x10.x10rt.X10JavaSerializable 
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, PointCharge.class);
    
    public static final x10.rtt.RuntimeType<PointCharge> $RTT = new x10.rtt.NamedType<PointCharge>(
    "au.edu.anu.chem.PointCharge", /* base class */PointCharge.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.STRUCT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(PointCharge $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        x10x.vector.Point3d centre = (x10x.vector.Point3d) $deserializer.readRef();
        $_obj.centre = centre;
        $_obj.charge = $deserializer.readDouble();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        PointCharge $_obj = new PointCharge((java.lang.System[]) null);
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
        $serializer.write(this.charge);
        
    }
    
    // zero value constructor
    public PointCharge(final java.lang.System $dummy) { this.centre = new x10x.vector.Point3d($dummy); this.charge = 0.0; }
    // constructor just for allocation
    public PointCharge(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
public int
          X10$object_lock_id0;
        
        
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
public x10.util.concurrent.OrderedLock
                                                                                                           getOrderedLock(
                                                                                                           ){
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final int t55340 =
              this.
                X10$object_lock_id0;
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final x10.util.concurrent.OrderedLock t55341 =
              x10.util.concurrent.OrderedLock.getLock((int)(t55340));
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
return t55341;
        }
        
        
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
public static int
          X10$class_lock_id1 =
          x10.util.concurrent.OrderedLock.createNewLockID();
        
        
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
public static x10.util.concurrent.OrderedLock
                                                                                                           getStaticOrderedLock(
                                                                                                           ){
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final int t55342 =
              au.edu.anu.chem.PointCharge.X10$class_lock_id1;
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final x10.util.concurrent.OrderedLock t55343 =
              x10.util.concurrent.OrderedLock.getLock((int)(t55342));
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
return t55343;
        }
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
public x10x.vector.Point3d
          centre;
        
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
public double
          charge;
        
        
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
// creation method for java code
        public static au.edu.anu.chem.PointCharge $make(final x10x.vector.Point3d centre,
                                                        final double charge){return new au.edu.anu.chem.PointCharge((java.lang.System[]) null).$init(centre,charge);}
        
        // constructor for non-virtual call
        final public au.edu.anu.chem.PointCharge au$edu$anu$chem$PointCharge$$init$S(final x10x.vector.Point3d centre,
                                                                                     final double charge) { {
                                                                                                                   
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"

                                                                                                                   
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final au.edu.anu.chem.PointCharge this5533455389 =
                                                                                                                     this;
                                                                                                                   
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
this5533455389.X10$object_lock_id0 = -1;
                                                                                                                   
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
this.centre = ((x10x.vector.Point3d)(centre));
                                                                                                                   
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
this.charge = charge;
                                                                                                               }
                                                                                                               return this;
                                                                                                               }
        
        // constructor
        public au.edu.anu.chem.PointCharge $init(final x10x.vector.Point3d centre,
                                                 final double charge){return au$edu$anu$chem$PointCharge$$init$S(centre,charge);}
        
        
        
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
// creation method for java code
        public static au.edu.anu.chem.PointCharge $make(final x10x.vector.Point3d centre,
                                                        final double charge,
                                                        final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.chem.PointCharge((java.lang.System[]) null).$init(centre,charge,paramLock);}
        
        // constructor for non-virtual call
        final public au.edu.anu.chem.PointCharge au$edu$anu$chem$PointCharge$$init$S(final x10x.vector.Point3d centre,
                                                                                     final double charge,
                                                                                     final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                               
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"

                                                                                                                                               
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final au.edu.anu.chem.PointCharge this5533755390 =
                                                                                                                                                 this;
                                                                                                                                               
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
this5533755390.X10$object_lock_id0 = -1;
                                                                                                                                               
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
this.centre = ((x10x.vector.Point3d)(centre));
                                                                                                                                               
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
this.charge = charge;
                                                                                                                                               
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final int t55344 =
                                                                                                                                                 paramLock.getIndex();
                                                                                                                                               
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
this.X10$object_lock_id0 = ((int)(t55344));
                                                                                                                                           }
                                                                                                                                           return this;
                                                                                                                                           }
        
        // constructor
        public au.edu.anu.chem.PointCharge $init(final x10x.vector.Point3d centre,
                                                 final double charge,
                                                 final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$chem$PointCharge$$init$S(centre,charge,paramLock);}
        
        
        
//#line 31 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final public java.lang.String
                                                                                                           toString(
                                                                                                           ){
            
//#line 32 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final double t55345 =
              charge;
            
//#line 32 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final java.lang.String t55346 =
              (("Point charge ") + ((x10.core.Double.$box(t55345))));
            
//#line 32 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final java.lang.String t55347 =
              ((t55346) + (" "));
            
//#line 32 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final x10x.vector.Point3d t55348 =
              ((x10x.vector.Point3d)(centre));
            
//#line 32 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final java.lang.String t55349 =
              ((t55347) + (t55348));
            
//#line 32 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
return t55349;
        }
        
        
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final public java.lang.String
                                                                                                           typeName$O(
                                                                                                           ){try {return x10.rtt.Types.typeName(this);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final public int
                                                                                                           hashCode(
                                                                                                           ){
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
int result =
              1;
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final int t55350 =
              result;
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final int t55352 =
              ((8191) * (((int)(t55350))));
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final x10x.vector.Point3d t55351 =
              ((x10x.vector.Point3d)(this.
                                       centre));
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final int t55353 =
              t55351.hashCode();
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final int t55354 =
              ((t55352) + (((int)(t55353))));
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
result = t55354;
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final int t55355 =
              result;
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final int t55357 =
              ((8191) * (((int)(t55355))));
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final double t55356 =
              this.
                charge;
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final int t55358 =
              x10.rtt.Types.hashCode(t55356);
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final int t55359 =
              ((t55357) + (((int)(t55358))));
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
result = t55359;
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final int t55360 =
              result;
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
return t55360;
        }
        
        
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final public boolean
                                                                                                           equals(
                                                                                                           java.lang.Object other){
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final java.lang.Object t55361 =
              other;
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final boolean t55362 =
              au.edu.anu.chem.PointCharge.$RTT.instanceOf(t55361);
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final boolean t55363 =
              !(t55362);
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
if (t55363) {
                
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
return false;
            }
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final java.lang.Object t55364 =
              other;
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final au.edu.anu.chem.PointCharge t55365 =
              ((au.edu.anu.chem.PointCharge)x10.rtt.Types.asStruct(au.edu.anu.chem.PointCharge.$RTT,t55364));
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final boolean t55366 =
              this.equals(((au.edu.anu.chem.PointCharge)(t55365)));
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
return t55366;
        }
        
        
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final public boolean
                                                                                                           equals(
                                                                                                           au.edu.anu.chem.PointCharge other){
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final x10x.vector.Point3d t55368 =
              ((x10x.vector.Point3d)(this.
                                       centre));
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final au.edu.anu.chem.PointCharge t55367 =
              other;
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final x10x.vector.Point3d t55369 =
              ((x10x.vector.Point3d)(t55367.
                                       centre));
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
boolean t55373 =
              x10.rtt.Equality.equalsequals((t55368),(t55369));
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
if (t55373) {
                
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final double t55371 =
                  this.
                    charge;
                
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final au.edu.anu.chem.PointCharge t55370 =
                  other;
                
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final double t55372 =
                  t55370.
                    charge;
                
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
t55373 = ((double) t55371) ==
                ((double) t55372);
            }
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final boolean t55374 =
              t55373;
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
return t55374;
        }
        
        
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final public boolean
                                                                                                           _struct_equals$O(
                                                                                                           java.lang.Object other){
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final java.lang.Object t55375 =
              other;
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final boolean t55376 =
              au.edu.anu.chem.PointCharge.$RTT.instanceOf(t55375);
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final boolean t55377 =
              !(t55376);
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
if (t55377) {
                
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
return false;
            }
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final java.lang.Object t55378 =
              other;
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final au.edu.anu.chem.PointCharge t55379 =
              ((au.edu.anu.chem.PointCharge)x10.rtt.Types.asStruct(au.edu.anu.chem.PointCharge.$RTT,t55378));
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final boolean t55380 =
              this._struct_equals$O(((au.edu.anu.chem.PointCharge)(t55379)));
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
return t55380;
        }
        
        
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final public boolean
                                                                                                           _struct_equals$O(
                                                                                                           au.edu.anu.chem.PointCharge other){
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final x10x.vector.Point3d t55382 =
              ((x10x.vector.Point3d)(this.
                                       centre));
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final au.edu.anu.chem.PointCharge t55381 =
              other;
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final x10x.vector.Point3d t55383 =
              ((x10x.vector.Point3d)(t55381.
                                       centre));
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
boolean t55387 =
              x10.rtt.Equality.equalsequals((t55382),(t55383));
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
if (t55387) {
                
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final double t55385 =
                  this.
                    charge;
                
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final au.edu.anu.chem.PointCharge t55384 =
                  other;
                
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final double t55386 =
                  t55384.
                    charge;
                
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
t55387 = ((double) t55385) ==
                ((double) t55386);
            }
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final boolean t55388 =
              t55387;
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
return t55388;
        }
        
        
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final public au.edu.anu.chem.PointCharge
                                                                                                           au$edu$anu$chem$PointCharge$$au$edu$anu$chem$PointCharge$this(
                                                                                                           ){
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
return au.edu.anu.chem.PointCharge.this;
        }
        
        
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
final private void
                                                                                                           __fieldInitializers31084(
                                                                                                           ){
            
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10"
this.X10$object_lock_id0 = -1;
        }
        
        final public static void
          __fieldInitializers31084$P(
          final au.edu.anu.chem.PointCharge PointCharge){
            PointCharge.__fieldInitializers31084();
        }
    
}
