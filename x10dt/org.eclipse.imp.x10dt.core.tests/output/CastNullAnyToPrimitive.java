
public class CastNullAnyToPrimitive
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<CastNullAnyToPrimitive>_RTT = new x10.rtt.RuntimeType<CastNullAnyToPrimitive>(
/* base class */CastNullAnyToPrimitive.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 22
public boolean
                  run(
                  ){
        
//#line 23
try {{
            
//#line 24
java.lang.Object k =
              null;
            
//#line 25
int p =
              x10.rtt.Types.asint(k);
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.ClassCastException) {
        final java.lang.ClassCastException e = (java.lang.ClassCastException) __$generated_wrappedex$__.getCause();
        {
            
//#line 27
return true;
        }
        }
        throw __$generated_wrappedex$__;
        }catch (final java.lang.ClassCastException e) {
            
//#line 27
return true;
        }
        
//#line 29
return false;
    }
    
    
//#line 32
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
    							CastNullAnyToPrimitive.main(args);
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
        
//#line 33
new CastNullAnyToPrimitive().execute();
    }/* } */
    
    public CastNullAnyToPrimitive() {
        super();
    }

}
