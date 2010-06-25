
public class ClockTest1
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClockTest1>_RTT = new x10.rtt.RuntimeType<ClockTest1>(
/* base class */ClockTest1.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 22
boolean
      flag;
    
    
//#line 24
public boolean
                  run(
                  ){
        
//#line 26
final x10.
          lang.
          Clock c =
          x10.
          lang.
          Clock.make();
        
//#line 30
x10.
          lang.
          Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                             lang.
                             Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                             lang.
                             Clock[] { c })/* } */,
                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                               
//#line 31
try {{
                                   
//#line 31
x10.
                                     lang.
                                     Runtime.startFinish();
                                   {
                                       
//#line 31
x10.
                                         lang.
                                         Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                              
//#line 32
try {{
                                                                  
//#line 32
x10.
                                                                    lang.
                                                                    Runtime.lock();
                                                                  {
                                                                      
//#line 32
ClockTest1.this.flag = true;
                                                                  }
                                                              }}finally {{
                                                                    
//#line 32
x10.
                                                                      lang.
                                                                      Runtime.release();
                                                                }}
                                                              }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                              });
                                       }
                                   }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
                                   if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
                                   java.lang.Throwable __desugarer__var__442__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                                   {
                                       
//#line 31
x10.
                                         lang.
                                         Runtime.pushException(__desugarer__var__442__);
                                   }
                                   }
                                   throw __$generated_wrappedex$__;
                                   }catch (java.lang.Throwable __desugarer__var__442__) {
                                       
//#line 31
x10.
                                         lang.
                                         Runtime.pushException(__desugarer__var__442__);
                                   }finally {{
                                        
//#line 31
x10.
                                          lang.
                                          Runtime.stopFinish();
                                    }}
                               }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                               });
                           
//#line 40
x10.
                             lang.
                             Runtime.next();
                           
//#line 41
boolean b;
                           
//#line 42
try {{
                               
//#line 42
x10.
                                 lang.
                                 Runtime.lock();
                               {
                                   
//#line 42
b = flag;
                               }
                           }}finally {{
                                 
//#line 42
x10.
                                   lang.
                                   Runtime.release();
                             }}
                           
//#line 43
return b;
        }
        
        
//#line 46
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
        							ClockTest1.main(args);
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
            
//#line 47
new ClockTest1().executeAsync();
        }/* } */
        
        public ClockTest1() {
            super();
            
//#line 22
this.flag = false;
        }
        
        }
        