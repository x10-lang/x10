package x10x.polar;


public class Polar3d
extends x10.core.Struct
  implements x10.util.concurrent.Atomic,
              x10.x10rt.X10JavaSerializable 
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Polar3d.class);
    
    public static final x10.rtt.RuntimeType<Polar3d> $RTT = new x10.rtt.NamedType<Polar3d>(
    "x10x.polar.Polar3d", /* base class */Polar3d.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.STRUCT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Polar3d $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        $_obj.r = $deserializer.readDouble();
        $_obj.theta = $deserializer.readDouble();
        $_obj.phi = $deserializer.readDouble();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Polar3d $_obj = new Polar3d((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.write(this.r);
        $serializer.write(this.theta);
        $serializer.write(this.phi);
        
    }
    
    // zero value constructor
    public Polar3d(final java.lang.System $dummy) { this.r = 0.0; this.theta = 0.0; this.phi = 0.0; }
    // constructor just for allocation
    public Polar3d(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
public int
          X10$object_lock_id0;
        
        
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
public x10.util.concurrent.OrderedLock
                                                                                                  getOrderedLock(
                                                                                                  ){
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final int t68058 =
              this.
                X10$object_lock_id0;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final x10.util.concurrent.OrderedLock t68059 =
              x10.util.concurrent.OrderedLock.getLock((int)(t68058));
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
return t68059;
        }
        
        
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
public static int
          X10$class_lock_id1 =
          x10.util.concurrent.OrderedLock.createNewLockID();
        
        
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
public static x10.util.concurrent.OrderedLock
                                                                                                  getStaticOrderedLock(
                                                                                                  ){
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final int t68060 =
              x10x.polar.Polar3d.X10$class_lock_id1;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final x10.util.concurrent.OrderedLock t68061 =
              x10.util.concurrent.OrderedLock.getLock((int)(t68060));
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
return t68061;
        }
        
        
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
public double
          r;
        
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
public double
          theta;
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
public double
          phi;
        
        
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
// creation method for java code
        public static x10x.polar.Polar3d $make(final double r,
                                               final double theta,
                                               final double phi){return new x10x.polar.Polar3d((java.lang.System[]) null).$init(r,theta,phi);}
        
        // constructor for non-virtual call
        final public x10x.polar.Polar3d x10x$polar$Polar3d$$init$S(final double r,
                                                                   final double theta,
                                                                   final double phi) { {
                                                                                              
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"

                                                                                              
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final x10x.polar.Polar3d this6805268217 =
                                                                                                this;
                                                                                              
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
this6805268217.X10$object_lock_id0 = -1;
                                                                                              
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
this.r = r;
                                                                                              
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
this.theta = theta;
                                                                                              
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
this.phi = phi;
                                                                                          }
                                                                                          return this;
                                                                                          }
        
        // constructor
        public x10x.polar.Polar3d $init(final double r,
                                        final double theta,
                                        final double phi){return x10x$polar$Polar3d$$init$S(r,theta,phi);}
        
        
        
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
// creation method for java code
        public static x10x.polar.Polar3d $make(final double r,
                                               final double theta,
                                               final double phi,
                                               final x10.util.concurrent.OrderedLock paramLock){return new x10x.polar.Polar3d((java.lang.System[]) null).$init(r,theta,phi,paramLock);}
        
        // constructor for non-virtual call
        final public x10x.polar.Polar3d x10x$polar$Polar3d$$init$S(final double r,
                                                                   final double theta,
                                                                   final double phi,
                                                                   final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                             
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"

                                                                                                                             
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final x10x.polar.Polar3d this6805568218 =
                                                                                                                               this;
                                                                                                                             
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
this6805568218.X10$object_lock_id0 = -1;
                                                                                                                             
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
this.r = r;
                                                                                                                             
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
this.theta = theta;
                                                                                                                             
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
this.phi = phi;
                                                                                                                             
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final int t68062 =
                                                                                                                               paramLock.getIndex();
                                                                                                                             
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
this.X10$object_lock_id0 = ((int)(t68062));
                                                                                                                         }
                                                                                                                         return this;
                                                                                                                         }
        
        // constructor
        public x10x.polar.Polar3d $init(final double r,
                                        final double theta,
                                        final double phi,
                                        final x10.util.concurrent.OrderedLock paramLock){return x10x$polar$Polar3d$$init$S(r,theta,phi,paramLock);}
        
        
        
//#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final public x10x.vector.Point3d
                                                                                                  toPoint3d(
                                                                                                  ){
            
//#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68063 =
              theta;
            
//#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double sineTheta =
              java.lang.Math.sin(((double)(t68063)));
            
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final x10x.vector.Point3d alloc45893 =
              new x10x.vector.Point3d((java.lang.System[]) null);
            
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t6806468219 =
              r;
            
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t6806668220 =
              ((t6806468219) * (((double)(sineTheta))));
            
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t6806568221 =
              phi;
            
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t6806768222 =
              java.lang.Math.cos(((double)(t6806568221)));
            
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t6807568223 =
              ((t6806668220) * (((double)(t6806768222))));
            
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t6806868224 =
              r;
            
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t6807068225 =
              ((t6806868224) * (((double)(sineTheta))));
            
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t6806968226 =
              phi;
            
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t6807168227 =
              java.lang.Math.sin(((double)(t6806968226)));
            
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t6807668228 =
              ((t6807068225) * (((double)(t6807168227))));
            
//#line 36 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t6807368229 =
              r;
            
//#line 36 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t6807268230 =
              theta;
            
//#line 36 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t6807468231 =
              java.lang.Math.cos(((double)(t6807268230)));
            
//#line 36 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t6807768232 =
              ((t6807368229) * (((double)(t6807468231))));
            
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final x10.util.concurrent.OrderedLock t6807868233 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
alloc45893.$init(t6807568223,
                                                                                                                     t6807668228,
                                                                                                                     t6807768232,
                                                                                                                     t6807868233);
            
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
return alloc45893;
        }
        
        
//#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final public static x10x.polar.Polar3d
                                                                                                  getPolar3d(
                                                                                                  final x10x.vector.Tuple3d point){
            
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68079 =
              point.i$O();
            
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68080 =
              point.i$O();
            
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68083 =
              ((t68079) * (((double)(t68080))));
            
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68081 =
              point.j$O();
            
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68082 =
              point.j$O();
            
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68084 =
              ((t68081) * (((double)(t68082))));
            
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double rxy2 =
              ((t68083) + (((double)(t68084))));
            
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68085 =
              point.k$O();
            
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68086 =
              point.k$O();
            
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68087 =
              ((t68085) * (((double)(t68086))));
            
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double r2 =
              ((rxy2) + (((double)(t68087))));
            
//#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double r =
              java.lang.Math.sqrt(((double)(r2)));
            
//#line 45 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
double phi =
               0;
            
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
double theta =
               0;
            
//#line 47 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final boolean t68103 =
              ((double) rxy2) ==
            ((double) 0.0);
            
//#line 47 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
if (t68103) {
                
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68088 =
                  point.k$O();
                
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final boolean t68090 =
                  ((t68088) >= (((double)(0.0))));
                
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
if (t68090) {
                    
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
theta = 0.0;
                } else {
                    
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
theta = 3.141592653589793;
                }
                
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
phi = 0.0;
            } else {
                
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double rxy =
                  java.lang.Math.sqrt(((double)(rxy2)));
                
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68091 =
                  point.k$O();
                
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68092 =
                  ((t68091) / (((double)(r))));
                
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68093 =
                  java.lang.Math.acos(((double)(t68092)));
                
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
theta = t68093;
                
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68094 =
                  point.i$O();
                
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68095 =
                  ((t68094) / (((double)(rxy))));
                
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68096 =
                  java.lang.Math.acos(((double)(t68095)));
                
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
phi = t68096;
                
//#line 58 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68097 =
                  point.j$O();
                
//#line 58 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final boolean t68102 =
                  ((t68097) < (((double)(0.0))));
                
//#line 58 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
if (t68102) {
                    
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68100 =
                      phi;
                    
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68101 =
                      ((6.283185307179586) - (((double)(t68100))));
                    
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
phi = t68101;
                }
            }
            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final x10x.polar.Polar3d alloc45894 =
              new x10x.polar.Polar3d((java.lang.System[]) null);
            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t6810468234 =
              theta;
            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t6810568235 =
              phi;
            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final x10.util.concurrent.OrderedLock t6810668236 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
alloc45894.$init(((double)(r)),
                                                                                                                     t6810468234,
                                                                                                                     t6810568235,
                                                                                                                     t6810668236);
            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
return alloc45894;
        }
        
        
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final public x10x.polar.Polar3d
                                                                                                  rotate(
                                                                                                  final double alpha,
                                                                                                  final double beta){
            
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68107 =
              phi;
            
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
double newPhi =
              ((t68107) + (((double)(alpha))));
            
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68109 =
              ((3.141592653589793) / (((double)(2.0))));
            
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68110 =
              phi;
            
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
boolean t68115 =
              ((t68109) < (((double)(t68110))));
            
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
if (t68115) {
                
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68113 =
                  phi;
                
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68114 =
                  ((9.42477796076938) / (((double)(2.0))));
                
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
t68115 = ((t68113) < (((double)(t68114))));
            }
            
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final boolean t68117 =
              t68115;
            
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
double t68118 =
               0;
            
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
if (t68117) {
                
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
t68118 = theta;
            } else {
                
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68116 =
                  theta;
                
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
t68118 = (-(t68116));
            }
            
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double tempTheta =
              t68118;
            
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
double newTheta =
              ((tempTheta) + (((double)(beta))));
            
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68120 =
              newTheta;
            
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final boolean t68126 =
              ((t68120) > (((double)(6.283185307179586))));
            
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
if (t68126) {
                
//#line 78 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68123 =
                  newTheta;
                
//#line 78 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68125 =
                  ((t68123) - (((double)(6.283185307179586))));
                
//#line 78 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
newTheta = t68125;
            }
            
//#line 82 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68127 =
              newTheta;
            
//#line 82 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final boolean t68137 =
              ((t68127) < (((double)(0.0))));
            
//#line 82 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
if (t68137) {
                
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68128 =
                  newTheta;
                
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68129 =
                  (-(t68128));
                
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
newTheta = t68129;
            } else {
                
//#line 85 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68130 =
                  newTheta;
                
//#line 85 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final boolean t68136 =
                  ((t68130) > (((double)(3.141592653589793))));
                
//#line 85 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
if (t68136) {
                    
//#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68134 =
                      newTheta;
                    
//#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68135 =
                      ((6.283185307179586) - (((double)(t68134))));
                    
//#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
newTheta = t68135;
                }
            }
            
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68139 =
              newPhi;
            
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final boolean t68151 =
              ((t68139) >= (((double)(6.283185307179586))));
            
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
if (t68151) {
                
//#line 91 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68142 =
                  newPhi;
                
//#line 91 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68144 =
                  ((t68142) - (((double)(6.283185307179586))));
                
//#line 91 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
newPhi = t68144;
            } else {
                
//#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68145 =
                  newPhi;
                
//#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final boolean t68150 =
                  ((t68145) < (((double)(0.0))));
                
//#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
if (t68150) {
                    
//#line 93 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68147 =
                      newPhi;
                    
//#line 93 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68149 =
                      ((t68147) + (((double)(6.283185307179586))));
                    
//#line 93 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
newPhi = t68149;
                }
            }
            
//#line 95 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final x10x.polar.Polar3d alloc45895 =
              new x10x.polar.Polar3d((java.lang.System[]) null);
            
//#line 95 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t6815268237 =
              r;
            
//#line 95 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t6815368238 =
              newTheta;
            
//#line 95 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t6815468239 =
              newPhi;
            
//#line 95 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final x10.util.concurrent.OrderedLock t6815568240 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 95 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
alloc45895.$init(((double)(t6815268237)),
                                                                                                                     t6815368238,
                                                                                                                     t6815468239,
                                                                                                                     t6815568240);
            
//#line 95 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
return alloc45895;
        }
        
        
//#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final public java.lang.String
                                                                                                  toString(
                                                                                                  ){
            
//#line 99 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68156 =
              r;
            
//#line 99 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final java.lang.String t68157 =
              (("(r:") + ((x10.core.Double.$box(t68156))));
            
//#line 99 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final java.lang.String t68158 =
              ((t68157) + (",theta:"));
            
//#line 99 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68159 =
              theta;
            
//#line 99 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final java.lang.String t68160 =
              ((t68158) + ((x10.core.Double.$box(t68159))));
            
//#line 99 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final java.lang.String t68161 =
              ((t68160) + (",phi:"));
            
//#line 99 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68162 =
              phi;
            
//#line 99 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final java.lang.String t68163 =
              ((t68161) + ((x10.core.Double.$box(t68162))));
            
//#line 99 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final java.lang.String t68164 =
              ((t68163) + (")"));
            
//#line 99 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
return t68164;
        }
        
        
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final public java.lang.String
                                                                                                  typeName$O(
                                                                                                  ){try {return x10.rtt.Types.typeName(this);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final public int
                                                                                                  hashCode(
                                                                                                  ){
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
int result =
              1;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final int t68165 =
              result;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final int t68167 =
              ((8191) * (((int)(t68165))));
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68166 =
              this.
                r;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final int t68168 =
              x10.rtt.Types.hashCode(t68166);
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final int t68169 =
              ((t68167) + (((int)(t68168))));
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
result = t68169;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final int t68170 =
              result;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final int t68172 =
              ((8191) * (((int)(t68170))));
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68171 =
              this.
                theta;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final int t68173 =
              x10.rtt.Types.hashCode(t68171);
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final int t68174 =
              ((t68172) + (((int)(t68173))));
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
result = t68174;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final int t68175 =
              result;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final int t68177 =
              ((8191) * (((int)(t68175))));
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68176 =
              this.
                phi;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final int t68178 =
              x10.rtt.Types.hashCode(t68176);
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final int t68179 =
              ((t68177) + (((int)(t68178))));
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
result = t68179;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final int t68180 =
              result;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
return t68180;
        }
        
        
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final public boolean
                                                                                                  equals(
                                                                                                  java.lang.Object other){
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final java.lang.Object t68181 =
              other;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final boolean t68182 =
              x10x.polar.Polar3d.$RTT.instanceOf(t68181);
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final boolean t68183 =
              !(t68182);
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
if (t68183) {
                
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
return false;
            }
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final java.lang.Object t68184 =
              other;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final x10x.polar.Polar3d t68185 =
              ((x10x.polar.Polar3d)x10.rtt.Types.asStruct(x10x.polar.Polar3d.$RTT,t68184));
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final boolean t68186 =
              this.equals(((x10x.polar.Polar3d)(t68185)));
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
return t68186;
        }
        
        
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final public boolean
                                                                                                  equals(
                                                                                                  x10x.polar.Polar3d other){
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68188 =
              this.
                r;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final x10x.polar.Polar3d t68187 =
              other;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68189 =
              t68187.
                r;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
boolean t68193 =
              ((double) t68188) ==
            ((double) t68189);
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
if (t68193) {
                
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68191 =
                  this.
                    theta;
                
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final x10x.polar.Polar3d t68190 =
                  other;
                
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68192 =
                  t68190.
                    theta;
                
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
t68193 = ((double) t68191) ==
                ((double) t68192);
            }
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
boolean t68197 =
              t68193;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
if (t68197) {
                
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68195 =
                  this.
                    phi;
                
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final x10x.polar.Polar3d t68194 =
                  other;
                
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68196 =
                  t68194.
                    phi;
                
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
t68197 = ((double) t68195) ==
                ((double) t68196);
            }
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final boolean t68198 =
              t68197;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
return t68198;
        }
        
        
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final public boolean
                                                                                                  _struct_equals$O(
                                                                                                  java.lang.Object other){
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final java.lang.Object t68199 =
              other;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final boolean t68200 =
              x10x.polar.Polar3d.$RTT.instanceOf(t68199);
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final boolean t68201 =
              !(t68200);
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
if (t68201) {
                
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
return false;
            }
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final java.lang.Object t68202 =
              other;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final x10x.polar.Polar3d t68203 =
              ((x10x.polar.Polar3d)x10.rtt.Types.asStruct(x10x.polar.Polar3d.$RTT,t68202));
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final boolean t68204 =
              this._struct_equals$O(((x10x.polar.Polar3d)(t68203)));
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
return t68204;
        }
        
        
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final public boolean
                                                                                                  _struct_equals$O(
                                                                                                  x10x.polar.Polar3d other){
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68206 =
              this.
                r;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final x10x.polar.Polar3d t68205 =
              other;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68207 =
              t68205.
                r;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
boolean t68211 =
              ((double) t68206) ==
            ((double) t68207);
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
if (t68211) {
                
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68209 =
                  this.
                    theta;
                
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final x10x.polar.Polar3d t68208 =
                  other;
                
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68210 =
                  t68208.
                    theta;
                
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
t68211 = ((double) t68209) ==
                ((double) t68210);
            }
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
boolean t68215 =
              t68211;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
if (t68215) {
                
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68213 =
                  this.
                    phi;
                
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final x10x.polar.Polar3d t68212 =
                  other;
                
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final double t68214 =
                  t68212.
                    phi;
                
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
t68215 = ((double) t68213) ==
                ((double) t68214);
            }
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final boolean t68216 =
              t68215;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
return t68216;
        }
        
        
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final public x10x.polar.Polar3d
                                                                                                  x10x$polar$Polar3d$$x10x$polar$Polar3d$this(
                                                                                                  ){
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
return x10x.polar.Polar3d.this;
        }
        
        
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
final private void
                                                                                                  __fieldInitializers45475(
                                                                                                  ){
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10"
this.X10$object_lock_id0 = -1;
        }
        
        final public static void
          __fieldInitializers45475$P(
          final x10x.polar.Polar3d Polar3d){
            Polar3d.__fieldInitializers45475();
        }
    
}
