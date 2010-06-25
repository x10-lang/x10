
public class ClockTest4
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClockTest4>_RTT = new x10.rtt.RuntimeType<ClockTest4>(
/* base class */ClockTest4.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 22
int
      val;
    
//#line 23
final public static int
      N =
      32;
    
    
//#line 25
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
        
//#line 28
/* template:forloop-mult { */
        {
            x10.array.Region __var27__ = (x10.
          array.
          Region.makeRectangular((int)(1),
                                 (int)((((((int)(ClockTest4.N))) - (((int)(1)))))))).region();
            if (__var27__.rect()) {
        	/* Loop: { *//* template:forloop-mult-each { */
        for (int __var28__ = __var27__.min(0), __var29__ = __var27__.max(0); __var28__ <= __var29__; __var28__++)
        /* } */
        /* } */ {
        		/* Loop: { *//* template:final-var-assign { */
        final int i = __var28__;
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
ClockTest4.this.foreachBody((int)(i),
                                                                           c);
                               }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                               });
        }
        	}
            } else {
        	assert false;
            }
        }
        /* } */
        
        
//#line 31
this.foreachBody((int)(0),
                                     c);
        
//#line 32
int temp2;
        
//#line 33
try {{
            
//#line 33
x10.
              lang.
              Runtime.lock();
            {
                
//#line 33
temp2 = val;
            }
        }}finally {{
              
//#line 33
x10.
                lang.
                Runtime.release();
          }}
        
//#line 34
harness.
          x10Test.chk((boolean)(((int) temp2) ==
                      ((int) 0)));
        
//#line 35
return true;
        }
    
    
//#line 38
void
                  foreachBody(
                  final int i,
                  final x10.
                    lang.
                    Clock c){
        
//#line 39
x10.
          lang.
          Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                             lang.
                             Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                             lang.
                             Clock[] { c })/* } */,
                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                               
//#line 39
try {{
                                   
//#line 39
x10.
                                     lang.
                                     Runtime.startFinish();
                                   {
                                       
//#line 39
x10.
                                         lang.
                                         Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                              
//#line 39
x10.
                                                                lang.
                                                                Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                     
//#line 39
try {{
                                                                                         
//#line 39
x10.
                                                                                           lang.
                                                                                           Runtime.lock();
                                                                                         {
                                                                                             
//#line 39
ClockTest4.this.val += i;
                                                                                         }
                                                                                     }}finally {{
                                                                                           
//#line 39
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
                                   java.lang.Throwable __desugarer__var__501__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                                   {
                                       
//#line 39
x10.
                                         lang.
                                         Runtime.pushException(__desugarer__var__501__);
                                   }
                                   }
                                   throw __$generated_wrappedex$__;
                                   }catch (java.lang.Throwable __desugarer__var__501__) {
                                       
//#line 39
x10.
                                         lang.
                                         Runtime.pushException(__desugarer__var__501__);
                                   }finally {{
                                        
//#line 39
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
int temp;
                           
//#line 42
try {{
                               
//#line 42
x10.
                                 lang.
                                 Runtime.lock();
                               {
                                   
//#line 42
temp = val;
                               }
                           }}finally {{
                                 
//#line 42
x10.
                                   lang.
                                   Runtime.release();
                             }}
                           
//#line 43
harness.
                             x10Test.chk((boolean)(((int) temp) ==
                                         ((int) ((((int)(((((int)(ClockTest4.N))) * (((int)((((((int)(ClockTest4.N))) - (((int)(1)))))))))))) / (((int)(2)))))));
                           
//#line 44
x10.
                             lang.
                             Runtime.next();
                           
//#line 45
x10.
                             lang.
                             Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                lang.
                                                Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                lang.
                                                Clock[] { c })/* } */,
                                              new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                  
//#line 45
try {{
                                                      
//#line 45
x10.
                                                        lang.
                                                        Runtime.startFinish();
                                                      {
                                                          
//#line 45
x10.
                                                            lang.
                                                            Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                 
//#line 45
x10.
                                                                                   lang.
                                                                                   Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                                        
//#line 45
try {{
                                                                                                            
//#line 45
x10.
                                                                                                              lang.
                                                                                                              Runtime.lock();
                                                                                                            {
                                                                                                                
//#line 45
ClockTest4.this.val -= i;
                                                                                                            }
                                                                                                        }}finally {{
                                                                                                              
//#line 45
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
                                                      java.lang.Throwable __desugarer__var__502__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                                                      {
                                                          
//#line 45
x10.
                                                            lang.
                                                            Runtime.pushException(__desugarer__var__502__);
                                                      }
                                                      }
                                                      throw __$generated_wrappedex$__;
                                                      }catch (java.lang.Throwable __desugarer__var__502__) {
                                                          
//#line 45
x10.
                                                            lang.
                                                            Runtime.pushException(__desugarer__var__502__);
                                                      }finally {{
                                                           
//#line 45
x10.
                                                             lang.
                                                             Runtime.stopFinish();
                                                       }}
                                                  }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                  });
                                              
//#line 46
x10.
                                                lang.
                                                Runtime.next();
                           }
                           
                           
//#line 49
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
                           							ClockTest4.main(args);
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
                               
//#line 50
new ClockTest4().executeAsync();
                           }/* } */
                           
                           public ClockTest4() {
                               super();
                               
//#line 22
this.val = 0;
                           }
        
        }
        