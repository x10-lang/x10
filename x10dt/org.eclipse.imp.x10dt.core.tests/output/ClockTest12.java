
public class ClockTest12
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClockTest12>_RTT = new x10.rtt.RuntimeType<ClockTest12>(
/* base class */ClockTest12.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 24
int
      phase;
    
    
//#line 26
public boolean
                  run(
                  ){
        
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
                  Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                       
//#line 28
final x10.
                                         lang.
                                         Clock c =
                                         x10.
                                         lang.
                                         Clock.make();
                                       
//#line 29
x10.
                                         lang.
                                         Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                            lang.
                                                            Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                            lang.
                                                            Clock[] { c })/* } */,
                                                          new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                              
//#line 29
ClockTest12.this.taskA((int)(1),
                                                                                                 c);
                                                          }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                          });
                                       
//#line 30
x10.
                                         lang.
                                         Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                            lang.
                                                            Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                            lang.
                                                            Clock[] { c })/* } */,
                                                          new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                              
//#line 30
ClockTest12.this.taskA((int)(2),
                                                                                                 c);
                                                          }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                          });
                                       
//#line 31
x10.
                                         lang.
                                         Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                            lang.
                                                            Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                            lang.
                                                            Clock[] { c })/* } */,
                                                          new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                              
//#line 31
ClockTest12.this.taskB(c);
                                                          }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                          });
                                   }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                   });
            }
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
        java.lang.Throwable __desugarer__var__458__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 27
x10.
              lang.
              Runtime.pushException(__desugarer__var__458__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__458__) {
            
//#line 27
x10.
              lang.
              Runtime.pushException(__desugarer__var__458__);
        }finally {{
             
//#line 27
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 33
return true;
        }
    
    
//#line 36
void
                  taskA(
                  int id,
                  final x10.
                    lang.
                    Clock c){
        
//#line 37
int tmp;
        
//#line 38
x10.
          lang.
          Activity.sleep((long)(((long)(((int)(1000))))));
        
//#line 39
try {{
            
//#line 39
x10.
              lang.
              Runtime.lock();
            {
                
//#line 39
tmp = phase;
            }
        }}finally {{
              
//#line 39
x10.
                lang.
                Runtime.release();
          }}
        
//#line 40
x10.
          io.
          Console.OUT.println(((((id) + (" observed current phase = "))) + (tmp)));
        
//#line 41
harness.
          x10Test.chk((boolean)(((int) tmp) ==
                      ((int) 0)));
        
//#line 42
c.resume();
        
//#line 43
x10.
          lang.
          Activity.sleep((long)(((long)(((int)(1000))))));
        
//#line 44
c.resume();
        
//#line 45
try {{
            
//#line 45
x10.
              lang.
              Runtime.lock();
            
//#line 45
while (true) {
                
//#line 45
if (((((int)(phase))) > (((int)(0))))) {
                    {
                        
//#line 46
x10.
                          io.
                          Console.OUT.println(((((id) + (" observed future phase = "))) + (phase)));
                        
//#line 47
harness.
                          x10Test.chk((boolean)(((int) phase) ==
                                      ((int) 1)));
                        
//#line 48
x10.
                          lang.
                          Activity.sleep((long)(((long)(((int)(5000))))));
                        
//#line 49
harness.
                          x10Test.chk((boolean)(((int) phase) ==
                                      ((int) 1)));
                    }
                    
//#line 45
break;
                }
                
//#line 45
x10.
                  lang.
                  Runtime.await();
            }
        }}finally {{
              
//#line 45
x10.
                lang.
                Runtime.release();
          }}
        
//#line 51
x10.
          lang.
          Runtime.next();
        
//#line 52
x10.
          lang.
          Activity.sleep((long)(((long)(((int)(1000))))));
        
//#line 53
try {{
            
//#line 53
x10.
              lang.
              Runtime.lock();
            {
                
//#line 53
tmp = phase;
            }
        }}finally {{
              
//#line 53
x10.
                lang.
                Runtime.release();
          }}
        
//#line 54
x10.
          io.
          Console.OUT.println(((((id) + (" observed current phase = "))) + (tmp)));
        
//#line 55
harness.
          x10Test.chk((boolean)(((int) tmp) ==
                      ((int) 1)));
        
//#line 56
c.resume();
        
//#line 57
c.resume();
        
//#line 58
c.resume();
        
//#line 59
try {{
            
//#line 59
x10.
              lang.
              Runtime.lock();
            
//#line 59
while (true) {
                
//#line 59
if (((((int)(phase))) > (((int)(1))))) {
                    {
                        
//#line 60
x10.
                          io.
                          Console.OUT.println(((((id) + (" observed future phase = "))) + (phase)));
                        
//#line 61
harness.
                          x10Test.chk((boolean)(((int) phase) ==
                                      ((int) 2)));
                        
//#line 62
x10.
                          lang.
                          Activity.sleep((long)(((long)(((int)(5000))))));
                        
//#line 63
harness.
                          x10Test.chk((boolean)(((int) phase) ==
                                      ((int) 2)));
                    }
                    
//#line 59
break;
                }
                
//#line 59
x10.
                  lang.
                  Runtime.await();
            }
        }}finally {{
              
//#line 59
x10.
                lang.
                Runtime.release();
          }}
        
//#line 65
x10.
          lang.
          Runtime.next();
        
//#line 66
x10.
          lang.
          Runtime.next();
        }
        
        
//#line 69
void
                      taskB(
                      final x10.
                        lang.
                        Clock c){
            
//#line 70
int tmp;
            
//#line 71
try {{
                
//#line 71
x10.
                  lang.
                  Runtime.lock();
                {
                    
//#line 71
tmp = phase;
                }
            }}finally {{
                  
//#line 71
x10.
                    lang.
                    Runtime.release();
              }}
            
//#line 72
x10.
              io.
              Console.OUT.println((("now in phase ") + (tmp)));
            
//#line 73
c.resume();
            
//#line 74
x10.
              lang.
              Runtime.next();
            
//#line 75
try {{
                
//#line 75
x10.
                  lang.
                  Runtime.lock();
                {
                    
//#line 75
this.phase += 1;
                }
            }}finally {{
                  
//#line 75
x10.
                    lang.
                    Runtime.release();
              }}
            
//#line 76
try {{
                
//#line 76
x10.
                  lang.
                  Runtime.lock();
                {
                    
//#line 76
tmp = phase;
                }
            }}finally {{
                  
//#line 76
x10.
                    lang.
                    Runtime.release();
              }}
            
//#line 77
x10.
              io.
              Console.OUT.println((("now in phase ") + (tmp)));
            
//#line 78
c.resume();
            
//#line 79
x10.
              lang.
              Runtime.next();
            
//#line 80
try {{
                
//#line 80
x10.
                  lang.
                  Runtime.lock();
                {
                    
//#line 80
this.phase += 1;
                }
            }}finally {{
                  
//#line 80
x10.
                    lang.
                    Runtime.release();
              }}
            
//#line 81
try {{
                
//#line 81
x10.
                  lang.
                  Runtime.lock();
                {
                    
//#line 81
tmp = phase;
                }
            }}finally {{
                  
//#line 81
x10.
                    lang.
                    Runtime.release();
              }}
            
//#line 82
x10.
              io.
              Console.OUT.println((("now in phase ") + (tmp)));
            
//#line 83
c.resume();
            
//#line 84
x10.
              lang.
              Runtime.next();
            }
            
            
//#line 87
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
            							ClockTest12.main(args);
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
                
//#line 88
new ClockTest12().execute();
            }/* } */
            
            public ClockTest12() {
                super();
                
//#line 24
this.phase = 0;
            }
            
            }
            