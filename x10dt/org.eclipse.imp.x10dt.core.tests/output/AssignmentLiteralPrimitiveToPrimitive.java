
public class AssignmentLiteralPrimitiveToPrimitive
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<AssignmentLiteralPrimitiveToPrimitive>_RTT = new x10.rtt.RuntimeType<AssignmentLiteralPrimitiveToPrimitive>(
/* base class */AssignmentLiteralPrimitiveToPrimitive.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 20
public boolean
                  run(
                  ){
        
//#line 26
int ii =
          20;
        
//#line 27
int iii =
          -2;
        
//#line 28
long ll =
          30L;
        
//#line 29
float ff =
          0.0010F;
        
//#line 30
double i =
          0.0010;
        
//#line 31
char cc =
          'c';
        
//#line 33
return true;
    }
    
    
//#line 36
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
    							AssignmentLiteralPrimitiveToPrimitive.main(args);
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
        
//#line 37
new AssignmentLiteralPrimitiveToPrimitive().execute();
    }/* } */
    
    public AssignmentLiteralPrimitiveToPrimitive() {
        super();
    }

}
