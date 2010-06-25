
public class AssignmentPrimitiveConstrainedIdentity
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<AssignmentPrimitiveConstrainedIdentity>_RTT = new x10.rtt.RuntimeType<AssignmentPrimitiveConstrainedIdentity>(
/* base class */AssignmentPrimitiveConstrainedIdentity.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 21
public boolean
                  run(
                  ){
        
//#line 23
try {{
            
//#line 24
int i =
              0;
            
//#line 25
int j =
              0;
            
//#line 26
return ((int) i) ==
            ((int) j);
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
        final java.lang.Throwable e = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 28
return false;
        }
        }
        throw __$generated_wrappedex$__;
        }catch (final java.lang.Throwable e) {
            
//#line 28
return false;
        }
    }
    
    
//#line 34
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
    							AssignmentPrimitiveConstrainedIdentity.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$38481)  {
        
//#line 35
new AssignmentPrimitiveConstrainedIdentity().execute();
    }/* } */
    
    public AssignmentPrimitiveConstrainedIdentity() {
        super();
    }

}
