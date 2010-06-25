
public class ClockTest15
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClockTest15>_RTT = new x10.rtt.RuntimeType<ClockTest15>(
/* base class */ClockTest15.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 77
int
      x;
    
    
//#line 78
public boolean
                  run(
                  ){
        
//#line 79
try {{
            
//#line 79
x10.
              lang.
              Runtime.startFinish();
            {
                
//#line 79
x10.
                  lang.
                  Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                       
//#line 80
final x10.
                                         lang.
                                         Clock a =
                                         x10.
                                         lang.
                                         Clock.make();
                                       
//#line 81
final x10.
                                         lang.
                                         Clock b =
                                         x10.
                                         lang.
                                         Clock.make();
                                       
//#line 82
x10.
                                         lang.
                                         Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                            lang.
                                                            Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                            lang.
                                                            Clock[] { a })/* } */,
                                                          new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                              
//#line 83
try {{
                                                                  
//#line 83
x10.
                                                                    lang.
                                                                    Runtime.lock();
                                                                  {
                                                                      
//#line 83
ClockTest15.this.x += 1;
                                                                  }
                                                              }}finally {{
                                                                    
//#line 83
x10.
                                                                      lang.
                                                                      Runtime.release();
                                                                }}
                                                              
//#line 84
x10.
                                                                lang.
                                                                Runtime.next();
                                                              
//#line 85
int tmp;
                                                              
//#line 86
try {{
                                                                  
//#line 86
x10.
                                                                    lang.
                                                                    Runtime.lock();
                                                                  {
                                                                      
//#line 86
tmp = x;
                                                                  }
                                                              }}finally {{
                                                                    
//#line 86
x10.
                                                                      lang.
                                                                      Runtime.release();
                                                                }}
                                                              
//#line 87
x10.
                                                                io.
                                                                Console.OUT.println((("A1 advanced, x = ") + (tmp)));
                                                              
//#line 88
harness.
                                                                x10Test.chk((boolean)(((int) tmp) ==
                                                                            ((int) 2)));
                                                              
//#line 89
x10.
                                                                lang.
                                                                Runtime.next();
                                                              }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                              });
                                                          
//#line 91
x10.
                                                            lang.
                                                            Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                               lang.
                                                                               Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                               lang.
                                                                               Clock[] { a,b })/* } */,
                                                                             new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                 
//#line 92
try {{
                                                                                     
//#line 92
x10.
                                                                                       lang.
                                                                                       Runtime.lock();
                                                                                     {
                                                                                         
//#line 92
ClockTest15.this.x += 1;
                                                                                     }
                                                                                 }}finally {{
                                                                                       
//#line 92
x10.
                                                                                         lang.
                                                                                         Runtime.release();
                                                                                   }}
                                                                                 
//#line 93
x10.
                                                                                   lang.
                                                                                   Runtime.next();
                                                                                 
//#line 94
int tmp;
                                                                                 
//#line 95
try {{
                                                                                     
//#line 95
x10.
                                                                                       lang.
                                                                                       Runtime.lock();
                                                                                     {
                                                                                         
//#line 95
tmp = x;
                                                                                     }
                                                                                 }}finally {{
                                                                                       
//#line 95
x10.
                                                                                         lang.
                                                                                         Runtime.release();
                                                                                   }}
                                                                                 
//#line 96
x10.
                                                                                   io.
                                                                                   Console.OUT.println((("A2 advanced, x = ") + (tmp)));
                                                                                 
//#line 97
harness.
                                                                                   x10Test.chk((boolean)(((int) tmp) ==
                                                                                               ((int) 3)));
                                                                                 
//#line 98
x10.
                                                                                   lang.
                                                                                   Runtime.next();
                                                                                 }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                 });
                                                                             
//#line 100
x10.
                                                                               lang.
                                                                               Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                                                  lang.
                                                                                                  Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                                                  lang.
                                                                                                  Clock[] { b })/* } */,
                                                                                                new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                                    
//#line 101
x10.
                                                                                                      lang.
                                                                                                      Activity.sleep((long)(((long)(((int)(5000))))));
                                                                                                    
//#line 102
try {{
                                                                                                        
//#line 102
x10.
                                                                                                          lang.
                                                                                                          Runtime.lock();
                                                                                                        {
                                                                                                            
//#line 102
ClockTest15.this.x += 1;
                                                                                                        }
                                                                                                    }}finally {{
                                                                                                          
//#line 102
x10.
                                                                                                            lang.
                                                                                                            Runtime.release();
                                                                                                      }}
                                                                                                    
//#line 103
x10.
                                                                                                      lang.
                                                                                                      Runtime.next();
                                                                                                    
//#line 104
int tmp;
                                                                                                    
//#line 105
try {{
                                                                                                        
//#line 105
x10.
                                                                                                          lang.
                                                                                                          Runtime.lock();
                                                                                                        {
                                                                                                            
//#line 105
tmp = x;
                                                                                                        }
                                                                                                    }}finally {{
                                                                                                          
//#line 105
x10.
                                                                                                            lang.
                                                                                                            Runtime.release();
                                                                                                      }}
                                                                                                    
//#line 106
x10.
                                                                                                      io.
                                                                                                      Console.OUT.println((("A3 advanced, x = ") + (tmp)));
                                                                                                    
//#line 107
harness.
                                                                                                      x10Test.chk((boolean)(((int) tmp) ==
                                                                                                                  ((int) 3)));
                                                                                                    
//#line 108
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
                java.lang.Throwable __desugarer__var__462__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                {
                    
//#line 79
x10.
                      lang.
                      Runtime.pushException(__desugarer__var__462__);
                }
                }
                throw __$generated_wrappedex$__;
                }catch (java.lang.Throwable __desugarer__var__462__) {
                    
//#line 79
x10.
                      lang.
                      Runtime.pushException(__desugarer__var__462__);
                }finally {{
                     
//#line 79
x10.
                       lang.
                       Runtime.stopFinish();
                 }}
                
//#line 111
return true;
                }
            
            
//#line 114
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
            							ClockTest15.main(args);
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
                
//#line 115
new ClockTest15().execute();
            }/* } */
            
            public ClockTest15() {
                super();
                
//#line 77
this.x = 0;
            }
            
            }
            