
public class ClockTest15WithResume
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClockTest15WithResume>_RTT = new x10.rtt.RuntimeType<ClockTest15WithResume>(
/* base class */ClockTest15WithResume.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 81
int
      x;
    
//#line 82
boolean
      advanced_A1;
    
    
//#line 83
public boolean
                  run(
                  ){
        
//#line 84
try {{
            
//#line 84
x10.
              lang.
              Runtime.startFinish();
            {
                
//#line 84
x10.
                  lang.
                  Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                       
//#line 85
final x10.
                                         lang.
                                         Clock a =
                                         x10.
                                         lang.
                                         Clock.make();
                                       
//#line 86
final x10.
                                         lang.
                                         Clock b =
                                         x10.
                                         lang.
                                         Clock.make();
                                       
//#line 87
x10.
                                         lang.
                                         Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                            lang.
                                                            Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                            lang.
                                                            Clock[] { a })/* } */,
                                                          new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                              
//#line 88
try {{
                                                                  
//#line 88
x10.
                                                                    lang.
                                                                    Runtime.lock();
                                                                  {
                                                                      
//#line 88
ClockTest15WithResume.this.x += 1;
                                                                  }
                                                              }}finally {{
                                                                    
//#line 88
x10.
                                                                      lang.
                                                                      Runtime.release();
                                                                }}
                                                              
//#line 89
x10.
                                                                lang.
                                                                Runtime.next();
                                                              
//#line 90
int tmp;
                                                              
//#line 91
try {{
                                                                  
//#line 91
x10.
                                                                    lang.
                                                                    Runtime.lock();
                                                                  {
                                                                      
//#line 91
tmp = x;
                                                                  }
                                                              }}finally {{
                                                                    
//#line 91
x10.
                                                                      lang.
                                                                      Runtime.release();
                                                                }}
                                                              
//#line 92
x10.
                                                                io.
                                                                Console.OUT.println((("A1 advanced, x = ") + (tmp)));
                                                              
//#line 93
try {{
                                                                  
//#line 93
x10.
                                                                    lang.
                                                                    Runtime.lock();
                                                                  {
                                                                      
//#line 93
ClockTest15WithResume.this.advanced_A1 = true;
                                                                  }
                                                              }}finally {{
                                                                    
//#line 93
x10.
                                                                      lang.
                                                                      Runtime.release();
                                                                }}
                                                              
//#line 94
harness.
                                                                x10Test.chk((boolean)(((int) tmp) ==
                                                                            ((int) 2)));
                                                              
//#line 95
x10.
                                                                lang.
                                                                Runtime.next();
                                                              }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                              });
                                                              
//#line 97
x10.
                                                                lang.
                                                                Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                                   lang.
                                                                                   Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                                   lang.
                                                                                   Clock[] { a,b })/* } */,
                                                                                 new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                     
//#line 98
try {{
                                                                                         
//#line 98
x10.
                                                                                           lang.
                                                                                           Runtime.lock();
                                                                                         {
                                                                                             
//#line 98
ClockTest15WithResume.this.x += 1;
                                                                                         }
                                                                                     }}finally {{
                                                                                           
//#line 98
x10.
                                                                                             lang.
                                                                                             Runtime.release();
                                                                                       }}
                                                                                     
//#line 99
x10.
                                                                                       lang.
                                                                                       Runtime.next();
                                                                                     
//#line 100
int tmp;
                                                                                     
//#line 101
try {{
                                                                                         
//#line 101
x10.
                                                                                           lang.
                                                                                           Runtime.lock();
                                                                                         {
                                                                                             
//#line 101
tmp = x;
                                                                                         }
                                                                                     }}finally {{
                                                                                           
//#line 101
x10.
                                                                                             lang.
                                                                                             Runtime.release();
                                                                                       }}
                                                                                     
//#line 102
x10.
                                                                                       io.
                                                                                       Console.OUT.println((("A2 advanced, x = ") + (tmp)));
                                                                                     
//#line 103
harness.
                                                                                       x10Test.chk((boolean)(((int) tmp) ==
                                                                                                   ((int) 3)));
                                                                                     
//#line 104
x10.
                                                                                       lang.
                                                                                       Runtime.next();
                                                                                     }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                     });
                                                                                 
//#line 106
x10.
                                                                                   lang.
                                                                                   Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                                                      lang.
                                                                                                      Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                                                      lang.
                                                                                                      Clock[] { b })/* } */,
                                                                                                    new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                                        
//#line 107
try {{
                                                                                                            
//#line 107
x10.
                                                                                                              lang.
                                                                                                              Runtime.lock();
                                                                                                            
//#line 107
while (!((advanced_A1)))
                                                                                                                
//#line 107
x10.
                                                                                                                  lang.
                                                                                                                  Runtime.await();
                                                                                                        }}finally {{
                                                                                                              
//#line 107
x10.
                                                                                                                lang.
                                                                                                                Runtime.release();
                                                                                                          }}
                                                                                                        
//#line 108
try {{
                                                                                                            
//#line 108
x10.
                                                                                                              lang.
                                                                                                              Runtime.lock();
                                                                                                            {
                                                                                                                
//#line 108
ClockTest15WithResume.this.x += 1;
                                                                                                            }
                                                                                                        }}finally {{
                                                                                                              
//#line 108
x10.
                                                                                                                lang.
                                                                                                                Runtime.release();
                                                                                                          }}
                                                                                                        
//#line 109
x10.
                                                                                                          lang.
                                                                                                          Runtime.next();
                                                                                                        
//#line 110
int tmp;
                                                                                                        
//#line 111
try {{
                                                                                                            
//#line 111
x10.
                                                                                                              lang.
                                                                                                              Runtime.lock();
                                                                                                            {
                                                                                                                
//#line 111
tmp = x;
                                                                                                            }
                                                                                                        }}finally {{
                                                                                                              
//#line 111
x10.
                                                                                                                lang.
                                                                                                                Runtime.release();
                                                                                                          }}
                                                                                                        
//#line 112
x10.
                                                                                                          io.
                                                                                                          Console.OUT.println((("A3 advanced, x = ") + (tmp)));
                                                                                                        
//#line 113
harness.
                                                                                                          x10Test.chk((boolean)(((int) tmp) ==
                                                                                                                      ((int) 3)));
                                                                                                        
//#line 114
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
                                       java.lang.Throwable __desugarer__var__464__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                                       {
                                           
//#line 84
x10.
                                             lang.
                                             Runtime.pushException(__desugarer__var__464__);
                                       }
                                       }
                                       throw __$generated_wrappedex$__;
                                       }catch (java.lang.Throwable __desugarer__var__464__) {
                                           
//#line 84
x10.
                                             lang.
                                             Runtime.pushException(__desugarer__var__464__);
                                       }finally {{
                                            
//#line 84
x10.
                                              lang.
                                              Runtime.stopFinish();
                                        }}
                                       
//#line 117
return true;
                }
                
                
//#line 120
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
                							ClockTest15WithResume.main(args);
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
                    
//#line 121
new ClockTest15WithResume().execute();
                }/* } */
                
                public ClockTest15WithResume() {
                    super();
                    
//#line 81
this.x = 0;
                    
//#line 82
this.advanced_A1 = false;
                }
                
                }
                