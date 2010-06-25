public class JHereEqHere
extends x10.core.Ref
{public static final x10.rtt.RuntimeType<JHereEqHere>_RTT = new x10.rtt.RuntimeType<JHereEqHere>(
/* base class */JHereEqHere.class
, /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 2
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
    							JHereEqHere.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> argv)  {
        
//#line 3
x10.
          io.
          Console.OUT.println((("eek(here,here)=") + (JHereEqHere.<x10.
                                lang.
                                Place>eek(x10.lang.Place._RTT,
                                          x10.
                                            lang.
                                            Runtime.here(),
                                          x10.
                                            lang.
                                            Runtime.here()))));
    }/* } */
    
    
//#line 5
public static <T> boolean
                 eek(
                 final x10.rtt.Type T,
                 final T a,
                 final T b){
        
//#line 6
if (/* template:equalsequals { */x10.rtt.Equality.equalsequals(a,null)/* } */ ||
                       /* template:equalsequals { */x10.rtt.Equality.equalsequals(b,null)/* } */) {
            
//#line 6
return false;
        }
        
//#line 7
return ((Object)((java.lang.Object)(a))).equals(b);
    }
    
    public JHereEqHere() {
        super();
    }

}
