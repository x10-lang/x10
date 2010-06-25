
public class ClockTest3a
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClockTest3a>_RTT = new x10.rtt.RuntimeType<ClockTest3a>(
/* base class */ClockTest3a.class
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
            x10.array.Region __var15__ = (x10.
          array.
          Region.makeRectangular((int)(0),
                                 (int)((((((int)(ClockTest3a.N))) - (((int)(1)))))))).region();
            if (__var15__.rect()) {
        	/* Loop: { *//* template:forloop-mult-each { */
        for (int __var16__ = __var15__.min(0), __var17__ = __var15__.max(0); __var16__ <= __var17__; __var16__++)
        /* } */
        /* } */ {
        		/* Loop: { *//* template:final-var-assign { */
        final int i = __var16__;
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
                                                                                         
//#line 27
try {{
                                                                                             
//#line 27
x10.
                                                                                               lang.
                                                                                               Runtime.lock();
                                                                                             {
                                                                                                 
//#line 27
ClockTest3a.this.val += 1;
                                                                                             }
                                                                                         }}finally {{
                                                                                               
//#line 27
x10.
                                                                                                 lang.
                                                                                                 Runtime.release();
                                                                                           }}
                                                                                         }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                         });
                                                                  }
                                                              }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
                                                              if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
                                                              java.lang.Throwable __desugarer__var__491__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                                                              {
                                                                  
//#line 27
x10.
                                                                    lang.
                                                                    Runtime.pushException(__desugarer__var__491__);
                                                              }
                                                              }
                                                              throw __$generated_wrappedex$__;
                                                              }catch (java.lang.Throwable __desugarer__var__491__) {
                                                                  
//#line 27
x10.
                                                                    lang.
                                                                    Runtime.pushException(__desugarer__var__491__);
                                                              }finally {{
                                                                   
//#line 27
x10.
                                                                     lang.
                                                                     Runtime.stopFinish();
                                                               }}
                                                          }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                          });
                                                      
//#line 28
x10.
                                                        lang.
                                                        Runtime.next();
                                                      
//#line 29
if (((int) val) !=
                                                                      ((int) ClockTest3a.N)) {
                                                          
//#line 30
throw new java.lang.Error();
                                                      }
                                                      
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
ClockTest3a.this.val += 1;
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
                                                                                 java.lang.Throwable __desugarer__var__492__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                                                                                 {
                                                                                     
//#line 33
x10.
                                                                                       lang.
                                                                                       Runtime.pushException(__desugarer__var__492__);
                                                                                 }
                                                                                 }
                                                                                 throw __$generated_wrappedex$__;
                                                                                 }catch (java.lang.Throwable __desugarer__var__492__) {
                                                                                     
//#line 33
x10.
                                                                                       lang.
                                                                                       Runtime.pushException(__desugarer__var__492__);
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
                               
            
//#line 36
x10.
              lang.
              Runtime.next();
            
//#line 36
x10.
              lang.
              Runtime.next();
            
//#line 36
x10.
              lang.
              Runtime.next();
            
//#line 37
if (((int) val) !=
                            ((int) ((((int)(2))) * (((int)(ClockTest3a.N)))))) {
                
//#line 38
throw new java.lang.Error();
            }
            
//#line 40
return true;
            }
        
        
//#line 43
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
        							ClockTest3a.main(args);
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
            
//#line 44
new ClockTest3a().executeAsync();
        }/* } */
        
        public ClockTest3a() {
            super();
            
//#line 20
this.val = 0;
        }
        
        }
        