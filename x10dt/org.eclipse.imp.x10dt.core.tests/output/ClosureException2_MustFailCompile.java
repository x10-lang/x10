
public class ClosureException2_MustFailCompile
extends ClosureTest
{public static final x10.rtt.RuntimeType<ClosureException2_MustFailCompile>_RTT = new x10.rtt.RuntimeType<ClosureException2_MustFailCompile>(
/* base class */ClosureException2_MustFailCompile.class
, /* parents */ new x10.rtt.Type[] {ClosureTest._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 23
public boolean
                  run(
                  ){
        
//#line 25
try {{
            
//#line 27
final x10.core.fun.VoidFun_0_0 f =
              ((x10.core.fun.VoidFun_0_0)(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                
//#line 27
throw new java.lang.Exception();
            }}catch (java.lang.RuntimeException ex) {throw ex;}catch (java.lang.Exception ex) {throw new x10.runtime.impl.java.WrappedRuntimeException(ex);}}
            }));
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.Exception) {
        final java.lang.Exception e = (java.lang.Exception) __$generated_wrappedex$__.getCause();
        {
            
        }
        }
        throw __$generated_wrappedex$__;
        }catch (final java.lang.Exception e) {
            
        }
        
//#line 30
return result;
    }
    
    
//#line 33
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
    							ClosureException2_MustFailCompile.main(args);
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
        
//#line 34
new ClosureException2_MustFailCompile().execute();
    }/* } */
    
    public ClosureException2_MustFailCompile() {
        super();
    }

}
