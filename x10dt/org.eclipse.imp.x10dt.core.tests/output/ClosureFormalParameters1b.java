
public class ClosureFormalParameters1b
extends ClosureTest
{public static final x10.rtt.RuntimeType<ClosureFormalParameters1b>_RTT = new x10.rtt.RuntimeType<ClosureFormalParameters1b>(
/* base class */ClosureFormalParameters1b.class
, /* parents */ new x10.rtt.Type[] {ClosureTest._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 23
public boolean
                  run(
                  ){
        
//#line 25
this.check("((i:int)=>i+1)(1)",
                               (int)(java.lang.Integer)(((new x10.core.fun.Fun_0_1<java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final java.lang.Integer i) { return apply((int)i);}
                                                          public final int apply(final int i) { {
                                                              
//#line 25
return ((((int)(i))) + (((int)(1))));
                                                          }}
                                                          public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;if (i ==1) return x10.rtt.Types.INT;return null;
                                                          }
                                                          })).apply(1)),
                               (int)(2));
        
//#line 27
return result;
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
    							ClosureFormalParameters1b.main(args);
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
new ClosureFormalParameters1b().execute();
    }/* } */
    
    public ClosureFormalParameters1b() {
        super();
    }

}
