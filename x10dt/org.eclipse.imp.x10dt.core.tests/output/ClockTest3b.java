
public class ClockTest3b
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClockTest3b>_RTT = new x10.rtt.RuntimeType<ClockTest3b>(
/* base class */ClockTest3b.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 22
int
      val;
    
//#line 23
final static int
      N =
      32;
    
    
//#line 25
public boolean
                  run(
                  ){
        
//#line 26
try {{
            
//#line 26
x10.
              lang.
              Runtime.startFinish();
            {
                
//#line 26
x10.
                  lang.
                  Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                       
//#line 27
final x10.
                                         lang.
                                         Clock c =
                                         x10.
                                         lang.
                                         Clock.make();
                                       
//#line 28
/* template:forloop-mult { */
                                       {
                                           x10.array.Region __var21__ = (x10.
                                         array.
                                         Region.makeRectangular((int)(0),
                                                                (int)((((((int)(ClockTest3b.N))) - (((int)(1)))))))).region();
                                           if (__var21__.rect()) {
                                       	/* Loop: { *//* template:forloop-mult-each { */
                                       for (int __var22__ = __var21__.min(0), __var23__ = __var21__.max(0); __var22__ <= __var23__; __var22__++)
                                       /* } */
                                       /* } */ {
                                       		/* Loop: { *//* template:final-var-assign { */
                                       final int i = __var22__;
                                       /* } */
                                       /* } */
{
                                           
//#line 28
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
try {{
                                                                                             
//#line 29
x10.
                                                                                               lang.
                                                                                               Runtime.startFinish();
                                                                                             {
                                                                                                 
//#line 29
x10.
                                                                                                   lang.
                                                                                                   Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                                                        
//#line 29
try {{
                                                                                                                            
//#line 29
x10.
                                                                                                                              lang.
                                                                                                                              Runtime.lock();
                                                                                                                            {
                                                                                                                                
//#line 29
ClockTest3b.this.val += 1;
                                                                                                                            }
                                                                                                                        }}finally {{
                                                                                                                              
//#line 29
x10.
                                                                                                                                lang.
                                                                                                                                Runtime.release();
                                                                                                                          }}
                                                                                                                        }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                                                        });
                                                                                                 }
                                                                                             }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
                                                                                             if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
                                                                                             java.lang.Throwable __desugarer__var__496__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                                                                                             {
                                                                                                 
//#line 29
x10.
                                                                                                   lang.
                                                                                                   Runtime.pushException(__desugarer__var__496__);
                                                                                             }
                                                                                             }
                                                                                             throw __$generated_wrappedex$__;
                                                                                             }catch (java.lang.Throwable __desugarer__var__496__) {
                                                                                                 
//#line 29
x10.
                                                                                                   lang.
                                                                                                   Runtime.pushException(__desugarer__var__496__);
                                                                                             }finally {{
                                                                                                  
//#line 29
x10.
                                                                                                    lang.
                                                                                                    Runtime.stopFinish();
                                                                                              }}
                                                                                         }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                         });
                                                                                     
//#line 30
x10.
                                                                                       lang.
                                                                                       Runtime.next();
                                                                                     
//#line 31
harness.
                                                                                       x10Test.chk((boolean)(((int) val) ==
                                                                                                   ((int) ClockTest3b.N)));
                                                                                     
//#line 32
x10.
                                                                                       lang.
                                                                                       Runtime.next();
                                                                                     
//#line 33
x10.
                                                                                       lang.
                                                                                       Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                                                          lang.
                                                                                                          Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                                                          lang.
                                                                                                          Clock[] { c })/* } */,
                                                                                                        new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                                            
//#line 33
try {{
                                                                                                                
//#line 33
x10.
                                                                                                                  lang.
                                                                                                                  Runtime.startFinish();
                                                                                                                {
                                                                                                                    
//#line 33
x10.
                                                                                                                      lang.
                                                                                                                      Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                                                                           
//#line 33
try {{
                                                                                                                                               
//#line 33
x10.
                                                                                                                                                 lang.
                                                                                                                                                 Runtime.lock();
                                                                                                                                               {
                                                                                                                                                   
//#line 33
ClockTest3b.this.val += 1;
                                                                                                                                               }
                                                                                                                                           }}finally {{
                                                                                                                                                 
//#line 33
x10.
                                                                                                                                                   lang.
                                                                                                                                                   Runtime.release();
                                                                                                                                             }}
                                                                                                                                           }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                                                                           });
                                                                                                                    }
                                                                                                                }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
                                                                                                                if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
                                                                                                                java.lang.Throwable __desugarer__var__497__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                                                                                                                {
                                                                                                                    
//#line 33
x10.
                                                                                                                      lang.
                                                                                                                      Runtime.pushException(__desugarer__var__497__);
                                                                                                                }
                                                                                                                }
                                                                                                                throw __$generated_wrappedex$__;
                                                                                                                }catch (java.lang.Throwable __desugarer__var__497__) {
                                                                                                                    
//#line 33
x10.
                                                                                                                      lang.
                                                                                                                      Runtime.pushException(__desugarer__var__497__);
                                                                                                                }finally {{
                                                                                                                     
//#line 33
x10.
                                                                                                                       lang.
                                                                                                                       Runtime.stopFinish();
                                                                                                                 }}
                                                                                                            }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                                            });
                                                                                                        
//#line 34
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
                                                              
                                           }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                           });
                                   }
                }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
                if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
                java.lang.Throwable __desugarer__var__498__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                {
                    
//#line 26
x10.
                      lang.
                      Runtime.pushException(__desugarer__var__498__);
                }
                }
                throw __$generated_wrappedex$__;
                }catch (java.lang.Throwable __desugarer__var__498__) {
                    
//#line 26
x10.
                      lang.
                      Runtime.pushException(__desugarer__var__498__);
                }finally {{
                     
//#line 26
x10.
                       lang.
                       Runtime.stopFinish();
                 }}
                
//#line 37
harness.
                  x10Test.chk((boolean)(((int) val) ==
                              ((int) ((((int)(2))) * (((int)(ClockTest3b.N)))))));
                
//#line 38
return true;
            }
            
            
//#line 41
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
            							ClockTest3b.main(args);
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
                
//#line 42
new ClockTest3b().executeAsync();
            }/* } */
            
            public ClockTest3b() {
                super();
                
//#line 22
this.val = 0;
            }
        
        }
        