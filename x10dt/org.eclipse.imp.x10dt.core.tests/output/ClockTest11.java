
public class ClockTest11
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClockTest11>_RTT = new x10.rtt.RuntimeType<ClockTest11>(
/* base class */ClockTest11.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 36
public boolean
                  run(
                  ){
        
//#line 37
try {{
            
//#line 38
try {{
                
//#line 38
x10.
                  lang.
                  Runtime.startFinish();
                {
                    
//#line 38
x10.
                      lang.
                      Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                           
//#line 39
final x10.
                                             lang.
                                             Clock c =
                                             x10.
                                             lang.
                                             Clock.make();
                                           
//#line 40
final x10.
                                             lang.
                                             Clock d =
                                             x10.
                                             lang.
                                             Clock.make();
                                           
//#line 41
x10.
                                             lang.
                                             Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                lang.
                                                                Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                lang.
                                                                Clock[] { d })/* } */,
                                                              new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                  
//#line 42
x10.
                                                                    lang.
                                                                    Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                                       lang.
                                                                                       Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                                       lang.
                                                                                       Clock[] { c })/* } */,
                                                                                     new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                         
//#line 42
x10.
                                                                                           io.
                                                                                           Console.OUT.println("hello");
                                                                                     }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                     });
                                                              }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                              });
                                       }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                       });
                }
            }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
            if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
            java.lang.Throwable __desugarer__var__456__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 38
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__456__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__456__) {
                
//#line 38
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__456__);
            }finally {{
                 
//#line 38
x10.
                   lang.
                   Runtime.stopFinish();
             }}
            
//#line 45
return false;
            }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
            if (__$generated_wrappedex$__.getCause() instanceof x10.
              lang.
              MultipleExceptions) {
            x10.
              lang.
              MultipleExceptions e = (x10.
              lang.
              MultipleExceptions) __$generated_wrappedex$__.getCause();
            {
                
//#line 48
return false;
            }
            }
            if (__$generated_wrappedex$__.getCause() instanceof x10.
              lang.
              ClockUseException) {
            x10.
              lang.
              ClockUseException e = (x10.
              lang.
              ClockUseException) __$generated_wrappedex$__.getCause();
            {
                
            }
            }
            throw __$generated_wrappedex$__;
            }catch (x10.
                      lang.
                      MultipleExceptions e) {
                
//#line 48
return false;
            }catch (x10.
                      lang.
                      ClockUseException e) {
                
            }
        
//#line 51
return true;
        }
    
    
//#line 54
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
    							ClockTest11.main(args);
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
        
//#line 55
new ClockTest11().execute();
    }/* } */
    
    public ClockTest11() {
        super();
    }
    
    }
    