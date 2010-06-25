
public class ClockTest3
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClockTest3>_RTT = new x10.rtt.RuntimeType<ClockTest3>(
/* base class */ClockTest3.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 20
int
      val;
    
//#line 21
final static int
      N =
      32;
    
    
//#line 23
public boolean
                  run(
                  ){
        
//#line 24
final x10.
          lang.
          Clock c =
          x10.
          lang.
          Clock.make();
        
//#line 26
/* template:forloop-mult { */
        {
            x10.array.Region __var9__ = (x10.
          array.
          Region.makeRectangular((int)(0),
                                 (int)((((((int)(ClockTest3.N))) - (((int)(1)))))))).region();
            if (__var9__.rect()) {
        	/* Loop: { *//* template:forloop-mult-each { */
        for (int __var10__ = __var9__.min(0), __var11__ = __var9__.max(0); __var10__ <= __var11__; __var10__++)
        /* } */
        /* } */ {
        		/* Loop: { *//* template:final-var-assign { */
        final int i = __var10__;
        /* } */
        /* } */
{
            
//#line 26
x10.
              lang.
              Runtime.runAsync(x10.
                                 lang.
                                 Runtime.here(),
                               /* template:tuple { */x10.core.RailFactory.<x10.
                                 lang.
                                 Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                 lang.
                                 Clock[] { c })/* } */,
                               new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                   
//#line 27
x10.
                                     lang.
                                     Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                        lang.
                                                        Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                        lang.
                                                        Clock[] { c })/* } */,
                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                          
//#line 28
try {{
                                                              
//#line 28
x10.
                                                                lang.
                                                                Runtime.startFinish();
                                                              {
                                                                  
//#line 28
x10.
                                                                    lang.
                                                                    Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                         
//#line 29
x10.
                                                                                           lang.
                                                                                           Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                                                
//#line 30
try {{
                                                                                                                    
//#line 30
x10.
                                                                                                                      lang.
                                                                                                                      Runtime.lock();
                                                                                                                    {
                                                                                                                        
//#line 30
ClockTest3.this.val += 1;
                                                                                                                    }
                                                                                                                }}finally {{
                                                                                                                      
//#line 30
x10.
                                                                                                                        lang.
                                                                                                                        Runtime.release();
                                                                                                                  }}
                                                                                                                }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                                                });
                                                                                         }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                         });
                                                                  }
                                                              }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
                                                              if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
                                                              java.lang.Throwable __desugarer__var__487__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                                                              {
                                                                  
//#line 28
x10.
                                                                    lang.
                                                                    Runtime.pushException(__desugarer__var__487__);
                                                              }
                                                              }
                                                              throw __$generated_wrappedex$__;
                                                              }catch (java.lang.Throwable __desugarer__var__487__) {
                                                                  
//#line 28
x10.
                                                                    lang.
                                                                    Runtime.pushException(__desugarer__var__487__);
                                                              }finally {{
                                                                   
//#line 28
x10.
                                                                     lang.
                                                                     Runtime.stopFinish();
                                                               }}
                                                          }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                          });
                                                      
//#line 33
x10.
                                                        lang.
                                                        Runtime.next();
                                                      
//#line 34
int temp;
                                                      
//#line 35
try {{
                                                          
//#line 35
x10.
                                                            lang.
                                                            Runtime.lock();
                                                          {
                                                              
//#line 35
temp = val;
                                                          }
                                                      }}finally {{
                                                            
//#line 35
x10.
                                                              lang.
                                                              Runtime.release();
                                                        }}
                                                      
//#line 36
if (((int) temp) !=
                                                                      ((int) ClockTest3.N)) {
                                                          
//#line 37
throw new java.lang.Error();
                                                      }
                                                      
//#line 39
x10.
                                                        lang.
                                                        Runtime.next();
                                                      
//#line 40
x10.
                                                        lang.
                                                        Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                           lang.
                                                                           Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                           lang.
                                                                           Clock[] { c })/* } */,
                                                                         new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                             
//#line 40
try {{
                                                                                 
//#line 40
x10.
                                                                                   lang.
                                                                                   Runtime.startFinish();
                                                                                 {
                                                                                     
//#line 40
x10.
                                                                                       lang.
                                                                                       Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                                            
//#line 40
x10.
                                                                                                              lang.
                                                                                                              Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                                                                   
//#line 40
try {{
                                                                                                                                       
//#line 40
x10.
                                                                                                                                         lang.
                                                                                                                                         Runtime.lock();
                                                                                                                                       {
                                                                                                                                           
//#line 40
ClockTest3.this.val += 1;
                                                                                                                                       }
                                                                                                                                   }}finally {{
                                                                                                                                         
//#line 40
x10.
                                                                                                                                           lang.
                                                                                                                                           Runtime.release();
                                                                                                                                     }}
                                                                                                                                   }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                                                                   });
                                                                                                            }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                                            });
                                                                                     }
                                                                                 }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
                                                                                 if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
                                                                                 java.lang.Throwable __desugarer__var__488__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                                                                                 {
                                                                                     
//#line 40
x10.
                                                                                       lang.
                                                                                       Runtime.pushException(__desugarer__var__488__);
                                                                                 }
                                                                                 }
                                                                                 throw __$generated_wrappedex$__;
                                                                                 }catch (java.lang.Throwable __desugarer__var__488__) {
                                                                                     
//#line 40
x10.
                                                                                       lang.
                                                                                       Runtime.pushException(__desugarer__var__488__);
                                                                                 }finally {{
                                                                                      
//#line 40
x10.
                                                                                        lang.
                                                                                        Runtime.stopFinish();
                                                                                  }}
                                                                             }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                             });
                                                                         
//#line 41
x10.
                                                                           lang.
                                                                           Runtime.next();
                                                      }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                      });
                                   }
                                   	}
                                       } else {
                                   	assert false;
                                       }
                                   }
                                   /* } */
                                   
                               
//#line 43
x10.
                                 lang.
                                 Runtime.next();
                               
//#line 43
x10.
                                 lang.
                                 Runtime.next();
                               
//#line 43
x10.
                                 lang.
                                 Runtime.next();
                               
//#line 44
int temp2;
                               
//#line 45
try {{
                                   
//#line 45
x10.
                                     lang.
                                     Runtime.lock();
                                   {
                                       
//#line 45
temp2 = val;
                                   }
                               }}finally {{
                                     
//#line 45
x10.
                                       lang.
                                       Runtime.release();
                                 }}
                               
//#line 46
if (((int) temp2) !=
                                               ((int) ((((int)(2))) * (((int)(ClockTest3.N)))))) {
                                   
//#line 47
throw new java.lang.Error();
                               }
                               
//#line 49
return true;
            }
            
            
//#line 52
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
            							ClockTest3.main(args);
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
                
//#line 53
new ClockTest3().executeAsync();
            }/* } */
            
            public ClockTest3() {
                super();
                
//#line 20
this.val = 0;
            }
            
            }
            