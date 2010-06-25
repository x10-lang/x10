
public class ClockTest2
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClockTest2>_RTT = new x10.rtt.RuntimeType<ClockTest2>(
/* base class */ClockTest2.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 20
int
      val;
    
//#line 21
final static int
      N =
      10;
    
    
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
        
//#line 25
for (
//#line 25
int i =
                           0;
                         ((((int)(i))) < (((int)(ClockTest2.N))));
                         
//#line 25
i += 1) {
            
//#line 26
x10.
              lang.
              Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                 lang.
                                 Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                 lang.
                                 Clock[] { c })/* } */,
                               new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                   
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
x10.
                                                                    lang.
                                                                    Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                         
//#line 28
try {{
                                                                                             
//#line 28
x10.
                                                                                               lang.
                                                                                               Runtime.lock();
                                                                                             {
                                                                                                 
//#line 29
ClockTest2.this.val += 1;
                                                                                             }
                                                                                         }}finally {{
                                                                                               
//#line 28
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
                                       java.lang.Throwable __desugarer__var__484__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                                       {
                                           
//#line 26
x10.
                                             lang.
                                             Runtime.pushException(__desugarer__var__484__);
                                       }
                                       }
                                       throw __$generated_wrappedex$__;
                                       }catch (java.lang.Throwable __desugarer__var__484__) {
                                           
//#line 26
x10.
                                             lang.
                                             Runtime.pushException(__desugarer__var__484__);
                                       }finally {{
                                            
//#line 26
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
                                               ((int) ((((int)(i))) + (((int)(1)))))) {
                                   
//#line 36
return false;
                               }
            }
            
//#line 38
if (c.dropped()) {
                
//#line 39
return false;
            }
            
//#line 40
c.drop();
            
//#line 41
if ((!(((boolean)(c.dropped()))))) {
                
//#line 42
return false;
            }
            
//#line 44
return true;
        }
        
        
//#line 47
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
        							ClockTest2.main(args);
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
            
//#line 48
new ClockTest2().executeAsync();
        }/* } */
        
        public ClockTest2() {
            super();
            
//#line 20
this.val = 0;
        }
        
        }
        