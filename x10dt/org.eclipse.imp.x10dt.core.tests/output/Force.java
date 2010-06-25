
public class Force
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<Force>_RTT = new x10.rtt.RuntimeType<Force>(
/* base class */Force.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 21
static int
                  rd(
                  final x10.
                    lang.
                    Future<java.lang.Integer> e,
                  final int i,
                  final int j){
        
//#line 22
final int x =
          e.apply$G();
        
//#line 23
return (x10.
                              lang.
                              Runtime.<java.lang.Integer>evalFuture(x10.rtt.Types.INT,
                                                                    x10.
                                                                      lang.
                                                                      Runtime.here(),
                                                                    new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                                                    public final int apply() { {
                                                                        
//#line 23
return x;
                                                                    }}
                                                                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                                                    }
                                                                    })).force$G();
    }
    
    
//#line 26
public boolean
                  run(
                  ){
        
//#line 27
return true;
    }
    
    
//#line 30
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
    							Force.main(args);
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
        
//#line 31
new Force().execute();
    }/* } */
    
    public Force() {
        super();
    }

}
