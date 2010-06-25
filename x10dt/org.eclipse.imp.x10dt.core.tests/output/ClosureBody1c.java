
public class ClosureBody1c
extends ClosureTest
{public static final x10.rtt.RuntimeType<ClosureBody1c>_RTT = new x10.rtt.RuntimeType<ClosureBody1c>(
/* base class */ClosureBody1c.class
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
final x10.core.fun.Fun_0_0<java.lang.Integer> h =
          ((x10.core.fun.Fun_0_0)(new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
        public final int apply() { {
            
//#line 36
ClosureBody1c.this.x((int)(2));
            
//#line 36
return ((((int)(ClosureBody1c.this.x()))) + (((int)(1))));
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
        }
        }));
        
//#line 37
this.check("h()",
                               (int)(java.lang.Integer)(h.apply$G()),
                               (int)(3));
        
//#line 38
this.check("x after h()",
                               (int)(this.x()),
                               (int)(2));
        
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
    							ClosureBody1c.main(args);
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
new ClosureBody1c().execute();
    }/* } */
    
    public ClosureBody1c() {
        super();
        
//#line 25
this.x = 0;
    }

}
