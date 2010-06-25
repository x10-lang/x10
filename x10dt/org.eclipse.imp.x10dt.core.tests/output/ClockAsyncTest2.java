
public class ClockAsyncTest2
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClockAsyncTest2>_RTT = new x10.rtt.RuntimeType<ClockAsyncTest2>(
/* base class */ClockAsyncTest2.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 24
public boolean
                  run(
                  ){
        
//#line 25
try {{
            
//#line 25
x10.
              lang.
              Runtime.startFinish();
            {
                
//#line 25
x10.
                  lang.
                  Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                       
//#line 26
final x10.core.Rail<x10.
                                         lang.
                                         Clock> clocks =
                                         ((x10.core.Rail)(x10.core.RailFactory.<x10.
                                         lang.
                                         Clock>makeRailFromValRail(x10.lang.Clock._RTT, /* template:tuple { */x10.core.RailFactory.<x10.
                                         lang.
                                         Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                         lang.
                                         Clock[] { x10.
                                         lang.
                                         Clock.make() })/* } */)));
                                       
//#line 27
x10.
                                         lang.
                                         Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                            lang.
                                                            Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                            lang.
                                                            Clock[] { ((x10.
                                                            lang.
                                                            Clock)((Object[])clocks.value)[0]) })/* } */,
                                                          new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                              
//#line 28
x10.
                                                                lang.
                                                                Runtime.next();
                                                          }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                          });
                                   }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                   });
            }
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
        java.lang.Throwable __desugarer__var__330__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 25
x10.
              lang.
              Runtime.pushException(__desugarer__var__330__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__330__) {
            
//#line 25
x10.
              lang.
              Runtime.pushException(__desugarer__var__330__);
        }finally {{
             
//#line 25
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 31
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
    							ClockAsyncTest2.main(args);
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
new ClockAsyncTest2().execute();
    }/* } */
    
    public ClockAsyncTest2() {
        super();
    }
    
    }
    