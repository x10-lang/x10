
class GCSpheres
extends x10.core.Ref
{
public static class /* Join: { */RTT/* } */ extends x10.types.RuntimeType<GCSpheres> {
public static final /* Join: { */RTT/* } */ it = new /* Join: { */RTT/* } */();
    
    
    public RTT() {super(GCSpheres.class);
                      }
    public boolean instanceof$(java.lang.Object o) {
    if (! (o instanceof GCSpheres)) return false;
        return true;
    }
    
    public static class /* Join: { */Submission$RTT/* } */ extends x10.types.RuntimeType<GCSpheres.
      Submission> {
    public static final /* Join: { */Submission$RTT/* } */ it = new /* Join: { */Submission$RTT/* } */();
        
        
        public Submission$RTT() {super(GCSpheres.
                                       Submission.class);
                                     }
        public boolean instanceof$(java.lang.Object o) {
        if (! (o instanceof GCSpheres.
              Submission)) return false;
            return true;
        }
        public java.util.List<x10.types.Type<?>> getTypeParameters() {
        return null;
        }
    }
    
    public static class /* Join: { */Original$RTT/* } */ extends x10.types.RuntimeType<GCSpheres.
      Original> {
    public static final /* Join: { */Original$RTT/* } */ it = new /* Join: { */Original$RTT/* } */();
        
        
        public Original$RTT() {super(GCSpheres.
                                     Original.class);
                                   }
        public boolean instanceof$(java.lang.Object o) {
        if (! (o instanceof GCSpheres.
              Original)) return false;
            return true;
        }
        public java.util.List<x10.types.Type<?>> getTypeParameters() {
        return null;
        }
    }
    
    public static class /* Join: { */Vector3$RTT/* } */ extends x10.types.RuntimeType<GCSpheres.
      Vector3> {
    public static final /* Join: { */Vector3$RTT/* } */ it = new /* Join: { */Vector3$RTT/* } */();
        
        
        public Vector3$RTT() {super(GCSpheres.
                                    Vector3.class);
                                  }
        public boolean instanceof$(java.lang.Object o) {
        if (! (o instanceof GCSpheres.
              Vector3)) return false;
            return true;
        }
        public java.util.List<x10.types.Type<?>> getTypeParameters() {
        return null;
        }
    }
    
    public static class /* Join: { */Sphere$RTT/* } */ extends x10.types.RuntimeType<GCSpheres.
      Sphere> {
    public static final /* Join: { */Sphere$RTT/* } */ it = new /* Join: { */Sphere$RTT/* } */();
        
        
        public Sphere$RTT() {super(GCSpheres.
                                   Sphere.class);
                                 }
        public boolean instanceof$(java.lang.Object o) {
        if (! (o instanceof GCSpheres.
              Sphere)) return false;
            return true;
        }
        public java.util.List<x10.types.Type<?>> getTypeParameters() {
        return null;
        }
    }
    
    public static class /* Join: { */VarBox$RTT</* Join: { */T/* } */>/* } */ extends x10.types.RuntimeType<GCSpheres.
      VarBox<T>> {
    public final x10.types.Type T;
        
        public VarBox$RTT(final x10.types.Type T) {super(GCSpheres.
                                                         VarBox.class);
                                                       this.T = T;
                                                       }
        public boolean instanceof$(java.lang.Object o) {
        if (! (o instanceof GCSpheres.
              VarBox)) return false;
            if (! this.T.equals(((GCSpheres.
              VarBox) o).rtt_GCSpheres$VarBox_T())) return false;
            return true;
        }
        public java.util.List<x10.types.Type<?>> getTypeParameters() {
        return java.util.Arrays.asList(new x10.types.Type<?>[] { 
            T
             });
        }
    }
    public java.util.List<x10.types.Type<?>> getTypeParameters() {
    return null;
    }
}

    
    
//#line 11
static interface Submission
                {
        
        
//#line 12
x10.core.Rail<java.lang.Integer>
                      processFrame(
                      final java.lang.Double x,
                      final java.lang.Double y,
                      final java.lang.Double z);
    }
    
    
//#line 15
static class Original
                extends x10.core.Ref
                  implements GCSpheres.
                               Submission
                {
        
//#line 16
protected x10.core.ValRail<GCSpheres.
          Sphere>
          spheres;
        
        
//#line 18
public static <T> void
                      arraycopy(
                      final x10.types.Type T,
                      final x10.core.Rail<T> src,
                      final java.lang.Integer srcPos,
                      final x10.core.Rail<T> dest,
                      final java.lang.Integer destPos,
                      final java.lang.Integer length){
            
//#line 22
for (
//#line 22
int i =
                               0;
                             i <
                             length;
                             
//#line 22
i += ((int) (int) (java.lang.Integer)
                                                (1))) {
                
//#line 23
/* template:place-check { */((x10.core.Rail<T>) x10.runtime.Runtime.placeCheck(x10.runtime.Runtime.here(), dest))/* } */.set(/* template:place-check { */((x10.core.Rail<T>) x10.runtime.Runtime.placeCheck(x10.runtime.Runtime.here(), src))/* } */.apply(i +
                srcPos), i +
                destPos);
            }
        }
        
        
//#line 27
x10.core.Rail<java.lang.Integer>
          result;
        
        
//#line 29
public Original() {
            
//#line 29
super();
            
//#line 30
this.spheres = ((x10.core.ValRail<GCSpheres.
              Sphere>) GCSpheres.getSpheres());
            
//#line 31
this.result = ((x10.core.Rail<java.lang.Integer>) x10.core.RailFactory.<java.lang.Integer>makeVarRail(x10.types.Types.INT, /* template:place-check { */((GCSpheres.
                                                                                                                                                     Original) x10.runtime.Runtime.placeCheck(x10.runtime.Runtime.here(), this))/* } */.spheres.
                                                                                                                                                     length));
        }
        
        
//#line 34
public x10.core.Rail<java.lang.Integer>
                      processFrame(
                      final java.lang.Double x,
                      final java.lang.Double y,
                      final java.lang.Double z){
            
//#line 35
final GCSpheres.
              Vector3 vec =
              new GCSpheres.
              Vector3(x,
                      y,
                      z);
            
//#line 36
/* template:forloop-mult { */
            {
                x10.lang.Region __var6__ = (x10.
              lang.
              Region.$convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                lang.
                                Region>makeValRailFromJavaArray(x10.lang.Region.RTT.it, new x10.
                                lang.
                                Region[] { /* Join: { */x10.
                                lang.
                                Region.makeRectangular(0,
                                                       /* template:place-check { */((GCSpheres.
                                                         Original) x10.runtime.Runtime.placeCheck(x10.runtime.Runtime.here(), this))/* } */.spheres.
                                                         length -
                                                       1)/* } */ })/* } */)).region();
                if (__var6__.rect()) {
            	/* Loop: { *//* template:forloop-mult-each { */
            for (int __var7__ = __var6__.min(0), __var8__ = __var6__.max(0); __var7__ <= __var8__; __var7__++)
            /* } */
            /* } */ {
            		/* Join: { *//* Loop: { *//* template:final-var-assign { */
            final int i = __var7__;
            /* } */
            /* } */
/* Join: { *//* template:point-create { */
            final  x10.
              lang.
              Point p = x10.lang.Point.make(/* Join: { */__var7__/* } */);/* } */
{
                
//#line 37
if ((/* template:place-check { */((GCSpheres.
                                  Original) x10.runtime.Runtime.placeCheck(x10.runtime.Runtime.here(), this))/* } */.spheres.apply(i)).intersects(vec)) {
                    
//#line 38
/* template:place-check { */((x10.core.Rail<java.lang.Integer>) x10.runtime.Runtime.placeCheck(x10.runtime.Runtime.here(), /* template:place-check { */((GCSpheres.
                      Original) x10.runtime.Runtime.placeCheck(x10.runtime.Runtime.here(), this))/* } */.result))/* } */.set(i, i);
                }
            }/* } *//* } */
            	}
                } else {
            	assert false;
                }
            }
            /* } */
            
            
//#line 41
return /* template:place-check { */((GCSpheres.
              Original) x10.runtime.Runtime.placeCheck(x10.runtime.Runtime.here(), this))/* } */.result;
        }
    }
    
    
//#line 45
static class Vector3
                extends x10.core.Value
                {
        
        
//#line 46
public Vector3(final double x,
                                   final double y,
                                   final double z) {
            
//#line 46
super();
            
//#line 47
this.x = ((double) x);
            
//#line 48
this.y = ((double) y);
            
//#line 49
this.z = ((double) z);
        }
        
        
//#line 51
public java.lang.Double
                      getX(
                      ){
            
//#line 51
return x;
        }
        
        
//#line 52
public java.lang.Double
                      getY(
                      ){
            
//#line 52
return y;
        }
        
        
//#line 53
public java.lang.Double
                      getZ(
                      ){
            
//#line 53
return z;
        }
        
        
//#line 54
public GCSpheres.
                      Vector3
                      add(
                      final GCSpheres.
                        Vector3 other){
            
//#line 54
return new GCSpheres.
              Vector3(this.
                        x +
                      other.
                        x,
                      this.
                        y +
                      other.
                        y,
                      this.
                        z +
                      other.
                        z);
        }
        
        
//#line 57
public GCSpheres.
                      Vector3
                      neg(
                      ){
            
//#line 57
return new GCSpheres.
              Vector3(-this.
                         x,
                      -this.
                         y,
                      -this.
                         z);
        }
        
        
//#line 58
public GCSpheres.
                      Vector3
                      sub(
                      final GCSpheres.
                        Vector3 other){
            
//#line 58
return GCSpheres.
              Vector3.this.add((other).neg());
        }
        
        
//#line 59
public java.lang.Double
                      length(
                      ){
            
//#line 59
return java.lang.Math.sqrt(GCSpheres.
              Vector3.this.length2());
        }
        
        
//#line 60
public java.lang.Double
                      length2(
                      ){
            
//#line 60
return x *
            x +
            y *
            y +
            z *
            z;
        }
        
        
//#line 62
final protected double
          x;
        
//#line 63
final protected double
          y;
        
//#line 64
final protected double
          z;
    }
    
    
//#line 67
static class Sphere
                extends x10.core.Value
                {
        
        
//#line 68
Sphere(final double x,
                           final double y,
                           final double z,
                           final double r) {
            
//#line 68
super();
            
//#line 69
this.pos = new GCSpheres.
              Vector3(x,
                      y,
                      z);
            
//#line 70
this.radius = ((double) r);
        }
        
        
//#line 72
public java.lang.Boolean
                      intersects(
                      final GCSpheres.
                        Vector3 home){
            
//#line 73
return ((home).sub(pos)).length2() <
            radius *
            radius;
        }
        
        
//#line 75
final protected GCSpheres.
          Vector3
          pos;
        
//#line 76
final protected double
          radius;
    }
    
    
//#line 79
static class VarBox<T>
                extends x10.core.Ref
                {public x10.types.Type<?> rtt_GCSpheres$VarBox_T() { return this.T; }
    
        private final x10.types.Type T;
        
        
//#line 80
T
          f;
        
        
//#line 81
VarBox(final x10.types.Type T,
                           final T x) {
                                               
//#line 81
super();
                                           this.T = T;
                                            {
                                               
//#line 81
GCSpheres.
                                                 VarBox.this.set(x);
                                           }}
        
        
//#line 82
T
                      get(
                      ){
            
//#line 82
return /* template:place-check { */((GCSpheres.
              VarBox<T>) x10.runtime.Runtime.placeCheck(x10.runtime.Runtime.here(), this))/* } */.f;
        }
        
        
//#line 83
void
                      set(
                      final T x){
            
//#line 83
this.f = ((T) x);
        }
    }
    
    
//#line 86
final private static GCSpheres.
      VarBox<x10.core.ValRail<GCSpheres.
      Sphere>>
      spheres =
      new GCSpheres.
      VarBox<x10.core.ValRail<GCSpheres.
      Sphere>>(new x10.core.ValRail.RTT(GCSpheres.RTT.Sphere$RTT.it),
               x10.core.RailFactory.<GCSpheres.
                 Sphere>makeValRail(GCSpheres.RTT.Sphere$RTT.it, 0));
    
    
//#line 88
public static x10.core.ValRail<GCSpheres.
                  Sphere>
                  getSpheres(
                  ){
        
//#line 88
return (/* template:place-check { */((GCSpheres.
          VarBox<x10.core.ValRail<GCSpheres.
          Sphere>>) x10.runtime.Runtime.placeCheck(x10.runtime.Runtime.here(), GCSpheres.spheres))/* } */).get();
    }
    
    
//#line 90
/* template:Main { */
    public static class Main extends x10.runtime.impl.java.Runtime {
    	public static void main(java.lang.String[] args) {
    		// start native runtime
    		new Main().start(args);
    	}
    	
    	// called by native runtime inside main x10 thread 
    	public void main(final x10.core.Rail<java.lang.String> args) {
    		try {
    	
    			// start xrx
    			x10.runtime.Runtime.start(
    				// body of main activity
    				new x10.core.fun.VoidFun_0_0() { 
    					public void apply() {
    						// preload classes
    						if (Boolean.getBoolean("x10.PRELOAD_CLASSES")) {
    							x10.runtime.impl.java.PreLoader.preLoad(this.getClass().getEnclosingClass(), Boolean.getBoolean("x10.PRELOAD_STRINGS"));
    						}
    
    						// catch and rethrow checked exceptions
    						// (closures cannot throw checked exceptions)
    						try {
    							// call the original app-main method
    							GCSpheres.main(args);
    						} catch (java.lang.RuntimeException e) {
    							throw e; 
    						} catch (java.lang.Error e) {
    							throw e; 
    						} catch (java.lang.Throwable t) {
    			 		   		throw new x10.lang.MultipleExceptions(t);
    			 		   	}
    					}
    				});
    				
    		} catch (java.lang.Throwable t) {
    			t.printStackTrace();
    			if (t instanceof x10.lang.MultipleExceptions) {
    				x10.core.ValRail<Throwable> exceptions = ((x10.lang.MultipleExceptions) t).exceptions;
    				for(int i = 0; i < exceptions.length; i++) {
    					exceptions.get(i).printStackTrace();
    				}
    			}
    		}
    	}
    }
    
    // the original app-main method
    public static void main(final x10.core.Rail<java.lang.String> args) /* Join: { */throws /* Join: { */java.lang.Exception/* } *//* } */ {
        
//#line 100
final int reps =
          75;
        
//#line 101
final x10.
          util.
          Random ran =
          new x10.
          util.
          Random(((long) (int) (java.lang.Integer)
                   (0)));
        
//#line 103
GCSpheres.generateSpheres(ran);
        
//#line 107
final long init_start =
          x10.
          lang.
          System.nanoTime();
        
//#line 108
final GCSpheres.
          Original student =
          new GCSpheres.
          Original();
        
//#line 109
final long init_time =
          x10.
          lang.
          System.nanoTime() -
        init_start;
        
//#line 119
long total_frame_time =
          ((long) (int) (java.lang.Integer)
            (0));
        
//#line 121
/* template:forloop-mult { */
        {
            x10.lang.Region __var9__ = (x10.
          lang.
          Region.$convert(/* template:tuple { */x10.core.RailFactory.<x10.
                            lang.
                            Region>makeValRailFromJavaArray(x10.lang.Region.RTT.it, new x10.
                            lang.
                            Region[] { /* Join: { */x10.
                            lang.
                            Region.makeRectangular(1,
                                                   reps)/* } */ })/* } */)).region();
            if (__var9__.rect()) {
        	/* Loop: { *//* template:forloop-mult-each { */
        for (int __var10__ = __var9__.min(0), __var11__ = __var9__.max(0); __var10__ <= __var11__; __var10__++)
        /* } */
        /* } */ {
        		/* Join: { *//* Loop: { *//* template:final-var-assign { */
        final int frame = __var10__;
        /* } */
        /* } */
/* Join: { *//* template:point-create { */
        final  x10.
          lang.
          Point _ = x10.lang.Point.make(/* Join: { */__var10__/* } */);/* } */
{
            
//#line 122
final double x =
              (/* template:place-check { */((x10.
              util.
              Random) x10.runtime.Runtime.placeCheck(x10.runtime.Runtime.here(), ran))/* } */).nextDouble() *
            ((double) (int) (java.lang.Integer)
              (10000));
            
//#line 123
final double y =
              (/* template:place-check { */((x10.
              util.
              Random) x10.runtime.Runtime.placeCheck(x10.runtime.Runtime.here(), ran))/* } */).nextDouble() *
            ((double) (int) (java.lang.Integer)
              (10000));
            
//#line 124
final double z =
              (/* template:place-check { */((x10.
              util.
              Random) x10.runtime.Runtime.placeCheck(x10.runtime.Runtime.here(), ran))/* } */).nextDouble() *
            ((double) (int) (java.lang.Integer)
              (10000));
            
//#line 126
final long frame_start =
              x10.
              lang.
              System.nanoTime();
            
//#line 127
final x10.core.Rail<java.lang.Integer> student_result =
              (/* template:place-check { */((GCSpheres.
              Original) x10.runtime.Runtime.placeCheck(x10.runtime.Runtime.here(), student))/* } */).processFrame(x,
                                                                                                                  y,
                                                                                                                  z);
            
//#line 129
total_frame_time += x10.
              lang.
              System.nanoTime() -
            frame_start;
        }/* } *//* } */
        	}
            } else {
        	assert false;
            }
        }
        /* } */
        
        
//#line 135
(x10.
          io.
          Console.OUT).println("Total time: " +
                               ((double) (long) (java.lang.Long)
                                 ((total_frame_time +
                                   init_time))) /
                               1.0E9);
    }/* } */
    
    
//#line 138
private static void
                   generateSpheres(
                   final x10.
                     util.
                     Random ran){
        
//#line 139
final x10.core.ValRail<GCSpheres.
          Sphere> tmp =
          x10.core.RailFactory.<GCSpheres.
          Sphere>makeValRail(GCSpheres.RTT.Sphere$RTT.it, 100000, new x10.core.fun.Fun_0_1</* Join: { */java.lang.Integer, GCSpheres.
          Sphere/* } */>() {public final GCSpheres.
          Sphere apply(/* Join: { */final java.lang.Integer i/* } */) { {
            
//#line 140
final double x =
              (/* template:place-check { */((x10.
              util.
              Random) x10.runtime.Runtime.placeCheck(x10.runtime.Runtime.here(), ran))/* } */).nextDouble() *
            ((double) (int) (java.lang.Integer)
              (10000));
            
//#line 141
final double y =
              (/* template:place-check { */((x10.
              util.
              Random) x10.runtime.Runtime.placeCheck(x10.runtime.Runtime.here(), ran))/* } */).nextDouble() *
            ((double) (int) (java.lang.Integer)
              (10000));
            
//#line 142
final double z =
              (/* template:place-check { */((x10.
              util.
              Random) x10.runtime.Runtime.placeCheck(x10.runtime.Runtime.here(), ran))/* } */).nextDouble() *
            ((double) (int) (java.lang.Integer)
              (10000));
            
//#line 143
final double r =
              (/* template:place-check { */((x10.
              util.
              Random) x10.runtime.Runtime.placeCheck(x10.runtime.Runtime.here(), ran))/* } */).nextDouble() *
            ((double) (int) (java.lang.Integer)
              (1000));
            
//#line 145
return new GCSpheres.
              Sphere(x,
                     y,
                     z,
                     r);
        }}
        public x10.types.Type<?> rtt_x10$lang$Fun_0_1_Z1() { return x10.types.Types.INT; }
        public x10.types.Type<?> rtt_x10$lang$Fun_0_1_U() { return GCSpheres.RTT.Sphere$RTT.it; }
        });
        
//#line 147
(/* template:place-check { */((GCSpheres.
          VarBox<x10.core.ValRail<GCSpheres.
          Sphere>>) x10.runtime.Runtime.placeCheck(x10.runtime.Runtime.here(), GCSpheres.spheres))/* } */).set(tmp);
    }
    
    
//#line 7
public GCSpheres() {
        
//#line 7
super();
    }
}
