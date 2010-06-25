
public class ClockAsyncTest
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClockAsyncTest>_RTT = new x10.rtt.RuntimeType<ClockAsyncTest>(
/* base class */ClockAsyncTest.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 24
public boolean
                  run(
                  ){
        
//#line 25
try {{
            
//#line 26
final x10.
              array.
              Array<x10.
              lang.
              Clock> clocks =
              ((x10.
              array.
              Array)(new x10.
              array.
              Array<x10.
              lang.
              Clock>(x10.lang.Clock._RTT,
                     x10.
                       array.
                       Region.makeRectangular((int)(0),
                                              (int)(5)),
                     new x10.core.fun.Fun_0_1<x10.
                       array.
                       Point, x10.
                       lang.
                       Clock>() {public final x10.
                       lang.
                       Clock apply$G(final x10.
                       array.
                       Point id$30992) { return apply(id$30992);}
                     public final x10.
                       lang.
                       Clock apply(final x10.
                       array.
                       Point id$30992) { {
                         
//#line 26
return x10.
                           lang.
                           Clock.make();
                     }}
                     public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.lang.Clock._RTT;return null;
                     }
                     })));
            
//#line 27
try {{
                
//#line 27
x10.
                  lang.
                  Runtime.startFinish();
                {
                    
//#line 27
x10.
                      lang.
                      Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                         lang.
                                         Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                         lang.
                                         Clock[] { clocks.apply$G((int)(0)) })/* } */,
                                       new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                           
//#line 28
x10.
                                             lang.
                                             Runtime.next();
                                       }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                       });
                }
            }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
            if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
            java.lang.Throwable __desugarer__var__328__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 27
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__328__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__328__) {
                
//#line 27
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__328__);
            }finally {{
                 
//#line 27
x10.
                   lang.
                   Runtime.stopFinish();
             }}
            }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
            if (__$generated_wrappedex$__.getCause() instanceof x10.
              lang.
              ClockUseException) {
            final x10.
              lang.
              ClockUseException x = (x10.
              lang.
              ClockUseException) __$generated_wrappedex$__.getCause();
            {
                
//#line 31
return true;
            }
            }
            throw __$generated_wrappedex$__;
            }catch (final x10.
                      lang.
                      ClockUseException x) {
                
//#line 31
return true;
            }
        
//#line 33
return false;
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
    							ClockAsyncTest.main(args);
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
new ClockAsyncTest().execute();
    }/* } */
    
    public ClockAsyncTest() {
        super();
    }
    
    }
    