
public class DimTest2
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<DimTest2>_RTT = new x10.rtt.RuntimeType<DimTest2>(
/* base class */DimTest2.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 19
public boolean
                  run(
                  ){
        
//#line 20
double meltC =
          ((double)(int)(((int)(0))));
        
//#line 21
double boilC =
          ((double)(int)(((int)(100))));
        
//#line 23
double meltF =
          ((double)(int)(((int)(32))));
        
//#line 24
double boilF =
          ((double)(int)(((int)(212))));
        
//#line 26
boolean a =
          ((int) ((((int)(double)(((double)((((double) (double) 
                                               meltC))))))))) ==
        ((int) ((((int)(double)(((double)(meltF)))))));
        
//#line 27
boolean b =
          ((int) ((((int)(double)(((double)((((double) (double) 
                                               boilC))))))))) ==
        ((int) ((((int)(double)(((double)(boilF)))))));
        
//#line 28
boolean c =
          ((int) ((((int)(double)(((double)((((double) (double) 
                                               meltF))))))))) ==
        ((int) ((((int)(double)(((double)(meltC)))))));
        
//#line 29
boolean d =
          ((int) ((((int)(double)(((double)((((double) (double) 
                                               boilF))))))))) ==
        ((int) ((((int)(double)(((double)(boilC)))))));
        
//#line 31
return a &&
        b &&
        c &&
        d;
    }
    
    
//#line 34
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
    							DimTest2.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$6591)  {
        
//#line 35
new DimTest2().execute();
    }/* } */
    
    public DimTest2() {
        super();
    }

}
