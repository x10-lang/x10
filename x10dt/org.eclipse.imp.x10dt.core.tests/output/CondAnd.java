
public class CondAnd
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<CondAnd>_RTT = new x10.rtt.RuntimeType<CondAnd>(
/* base class */CondAnd.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 22
public boolean
                  run(
                  ){
        
//#line 23
final x10.
          array.
          Region r1 =
          ((x10.
          array.
          Region)(x10.
          array.
          Region.makeRectangular((int)(0),
                                 (int)(100))));
        
//#line 24
final x10.
          array.
          Region r2 =
          ((x10.
          array.
          Region)(x10.
          array.
          Region.makeRectangular((int)(2),
                                 (int)(99))));
        
//#line 25
final x10.
          array.
          Region r3 =
          ((x10.
          array.
          Region)(r1.$and(r2)));
        
//#line 26
return true;
    }
    
    
//#line 28
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
    							CondAnd.main(args);
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
        
//#line 29
new CondAnd().execute();
    }/* } */
    
    public CondAnd() {
        super();
    }

}
