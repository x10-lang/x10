
public class DimTest
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<DimTest>_RTT = new x10.rtt.RuntimeType<DimTest>(
/* base class */DimTest.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 19
double
                  move(
                  double x0,
                  double v0,
                  double a,
                  double t){
        
//#line 20
return ((((double)(((((double)(x0))) + (((double)((((((double)(v0))) * (((double)(t)))))))))))) + (((double)(((((double)((((((double)(((((double)(a))) * (((double)(t))))))) * (((double)(t)))))))) / (((double)(((double)(int)(((int)(2))))))))))));
    }
    
    
//#line 21
public boolean
                  run(
                  ){
        
//#line 22
double x0 =
          ((double)(int)(((int)(0))));
        
//#line 23
double t =
          ((double)(int)(((int)(1))));
        
//#line 24
double v0 =
          ((double)(int)(((int)(0))));
        
//#line 25
double a =
          dims.
          Acceleration.g;
        
//#line 27
double x1 =
          this.move((double)(x0),
                    (double)(v0),
                    (double)(a),
                    (double)(t));
        
//#line 29
return ((double) x1) ==
        ((double) 4.9);
    }
    
    
//#line 32
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
    							DimTest.main(args);
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
    public static void main(x10.core.Rail<java.lang.String> args)  {
        
//#line 33
new DimTest().execute();
    }/* } */
    
    public DimTest() {
        super();
    }

}
