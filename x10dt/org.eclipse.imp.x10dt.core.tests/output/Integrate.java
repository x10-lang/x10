public class Integrate
extends x10.core.Ref
{public static final x10.rtt.RuntimeType<Integrate>_RTT = new x10.rtt.RuntimeType<Integrate>(
/* base class */Integrate.class
, /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 2
final static double
      epsilon =
      1.0E-12;
    
//#line 3
final x10.core.fun.Fun_0_1<java.lang.Double,java.lang.Double>
      fun;
    
    
//#line 4
public Integrate(final x10.core.fun.Fun_0_1<java.lang.Double,java.lang.Double> f) {
        
//#line 4
super();
        
//#line 4
this.fun = f;
    }
    
    
//#line 5
public double
                 computeArea(
                 final double left,
                 final double right){
        
//#line 6
return this.eval((double)(left),
                                    (double)(java.lang.Double)(fun.apply$G(left)),
                                    (double)(right),
                                    (double)(java.lang.Double)(fun.apply$G(right)),
                                    (double)(((double)(int)(((int)(0))))));
    }
    
    
//#line 8
private double
                 eval(
                 final double l,
                 final double fl,
                 final double r,
                 final double fr,
                 final double a){
        
//#line 10
final double h =
          ((((double)((((((double)(r))) - (((double)(l)))))))) / (((double)(((double)(int)(((int)(2))))))));
        
//#line 11
final double hh =
          ((((double)(h))) / (((double)(((double)(int)(((int)(2))))))));
        
//#line 12
final double c =
          ((((double)(l))) + (((double)(h))));
        
//#line 13
final double fc =
          fun.apply$G(c);
        
//#line 14
final double al =
          ((((double)((((((double)(fl))) + (((double)(fc)))))))) * (((double)(hh))));
        
//#line 15
final double ar =
          ((((double)((((((double)(fr))) + (((double)(fc)))))))) * (((double)(hh))));
        
//#line 16
final double alr =
          ((((double)(al))) + (((double)(ar))));
        
//#line 17
return ((((((double)(x10.
                               lang.
                               Math.abs((double)(((((double)(alr))) - (((double)(a))))))))) < (((double)(Integrate.epsilon))))))
          ? alr
          : ((((double)(this.eval((double)(c),
                                  (double)(fc),
                                  (double)(r),
                                  (double)(fr),
                                  (double)(ar))))) + (((double)(this.eval((double)(l),
                                                                          (double)(fl),
                                                                          (double)(c),
                                                                          (double)(fc),
                                                                          (double)(al))))));
    }
    
    
//#line 22
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
    			x10.lang.Runtime.start(
    				// static init activity
    				new x10.core.fun.VoidFun_0_0() {
    					public void apply() {
    						// preload classes
    						if (Boolean.getBoolean("x10.PRELOAD_CLASSES")) {
    							x10.runtime.impl.java.PreLoader.preLoad(this.getClass().getEnclosingClass(), Boolean.getBoolean("x10.PRELOAD_STRINGS"));
    						}
    					}
    				},
    				// body of main activity
    				new x10.core.fun.VoidFun_0_0() {
    					public void apply() {
    						// catch and rethrow checked exceptions
    						// (closures cannot throw checked exceptions)
    						try {
    							// call the original app-main method
    							Integrate.main(args);
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
    		}
    	}
    }
    
    // the original app-main method
    public static void main(final x10.core.Rail<java.lang.String> args)  {
        
//#line 24
final Integrate obj =
          ((Integrate)(new Integrate(new x10.core.fun.Fun_0_1<java.lang.Double, java.lang.Double>() {public final java.lang.Double apply$G(final java.lang.Double x) { return apply((double)x);}
                                     public final double apply(final double x) { {
                                         
//#line 24
return ((((double)((((((double)(((((double)(x))) * (((double)(x))))))) + (((double)(1.0)))))))) * (((double)(x))));
                                     }}
                                     public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.DOUBLE;if (i ==1) return x10.rtt.Types.DOUBLE;return null;
                                     }
                                     })));
        
//#line 25
final int xMax =
          ((((int)(args.
                     length))) > (((int)(0))))
          ? java.lang.Integer.parseInt(((java.lang.String)((Object[])args.value)[0]))
          : 10;
        
//#line 26
double start =
          ((double)(long)(((long)((-(((long)(x10.
          lang.
          System.nanoTime()))))))));
        
//#line 27
final double area =
          obj.computeArea((double)(((double)(int)(((int)(0))))),
                          (double)(((double)(int)(((int)(xMax))))));
        
//#line 28
start += ((double)(((long)(x10.
          lang.
          System.nanoTime()))));
        
//#line 29
x10.
          io.
          Console.OUT.println((((((((((((("The area of (x*x +1) * x from 0 to ") + (xMax))) + (" is "))) + (area))) + (" (t="))) + ((((float)(double)(((double)(((((double)(start))) / (((double)(((double)(int)(((int)((((((int)(1000))) * (((int)(1000))))))))))))))))))))) + (" ms)")));
    }/* } */

}
