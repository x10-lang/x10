
public class BitsTest
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<BitsTest>_RTT = new x10.rtt.RuntimeType<BitsTest>(
/* base class */BitsTest.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 18
int
      a;
    
//#line 19
int
      b;
    
    
//#line 21
public boolean
                  run(
                  ){
        
//#line 22
this.b = 65536;
        
//#line 23
this.b = 0;
        
//#line 24
this.a = ((((int)(7))) & (((int)(1))));
        
//#line 25
this.b = ((((int)(19088743))) & (((int)(131071))));
        
//#line 26
return ((int) a) ==
        ((int) 1) &&
        ((int) b) ==
        ((int) 83303);
    }
    
    
//#line 29
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
    							BitsTest.main(args);
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
        
//#line 30
new BitsTest().execute();
    }/* } */
    
    public BitsTest() {
        super();
        
//#line 18
this.a = 0;
        
//#line 19
this.b = 0;
    }

}
