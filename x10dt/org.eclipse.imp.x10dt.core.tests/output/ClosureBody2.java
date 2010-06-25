
public class ClosureBody2
extends ClosureTest
{public static final x10.rtt.RuntimeType<ClosureBody2>_RTT = new x10.rtt.RuntimeType<ClosureBody2>(
/* base class */ClosureBody2.class
, /* parents */ new x10.rtt.Type[] {ClosureTest._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 25
int
      x;
    
    
//#line 27
void
                  x(
                  final int x){
        
//#line 28
this.x = x;
    }
    
    
//#line 32
public boolean
                  run(
                  ){
        
//#line 35
final x10.core.fun.VoidFun_0_0 f =
          ((x10.core.fun.VoidFun_0_0)(new x10.core.fun.VoidFun_0_0() {public final void apply() { {
            {
                
//#line 35
ClosureBody2.this.x((int)(1));
                
//#line 35
return;
            }
        }}
        }));
        
//#line 36
this.check("x after defn",
                               (int)(x),
                               (int)(0));
        
//#line 39
f.apply();
        
//#line 40
this.check("x after f()",
                               (int)(x),
                               (int)(1));
        
//#line 42
return result;
    }
    
    
//#line 45
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
    							ClosureBody2.main(args);
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
        
//#line 46
new ClosureBody2().execute();
    }/* } */
    
    public ClosureBody2() {
        super();
        
//#line 25
this.x = 0;
    }

}
