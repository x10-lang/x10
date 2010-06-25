
public class ClosureBody1b
extends ClosureTest
{public static final x10.rtt.RuntimeType<ClosureBody1b>_RTT = new x10.rtt.RuntimeType<ClosureBody1b>(
/* base class */ClosureBody1b.class
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
    
    
//#line 31
int
                  x(
                  ){
        
//#line 31
return x;
    }
    
    
//#line 33
public boolean
                  run(
                  ){
        
//#line 36
final x10.core.fun.VoidFun_0_0 g =
          ((x10.core.fun.VoidFun_0_0)(new x10.core.fun.VoidFun_0_0() {public final void apply() { {
            
//#line 36
ClosureBody1b.this.x((int)(1));
        }}
        }));
        
//#line 37
g.apply();
        
//#line 38
this.check("x after g()",
                               (int)(this.x()),
                               (int)(1));
        
//#line 40
return result;
    }
    
    
//#line 43
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
    							ClosureBody1b.main(args);
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
        
//#line 44
new ClosureBody1b().execute();
    }/* } */
    
    public ClosureBody1b() {
        super();
        
//#line 25
this.x = 0;
    }

}
