
public class ClockTest7
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClockTest7>_RTT = new x10.rtt.RuntimeType<ClockTest7>(
/* base class */ClockTest7.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 48
int
      val;
    
//#line 49
final static int
      N =
      16;
    
    
//#line 51
public boolean
                  run(
                  ){
        
//#line 52
try {{
            
//#line 53
final x10.
              lang.
              Clock c =
              x10.
              lang.
              Clock.make();
            
//#line 55
try {{
                
//#line 55
x10.
                  lang.
                  Runtime.startFinish();
                {
                    
//#line 55
/* template:forloop-mult { */
                    {
                        x10.array.Region __var33__ = (x10.
                      array.
                      Region.makeRectangular((int)(0),
                                             (int)((((((int)(ClockTest7.N))) - (((int)(1)))))))).region();
                        if (__var33__.rect()) {
                    	/* Loop: { *//* template:forloop-mult-each { */
                    for (int __var34__ = __var33__.min(0), __var35__ = __var33__.max(0); __var34__ <= __var35__; __var34__++)
                    /* } */
                    /* } */ {
                    		/* Loop: { *//* template:final-var-assign { */
                    final int i = __var34__;
                    /* } */
                    /* } */
{
                        
//#line 55
x10.
                          lang.
                          Runtime.runAsync(x10.
                                             lang.
                                             Runtime.here(),
                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                               
//#line 56
try {{
                                                   
//#line 56
x10.
                                                     lang.
                                                     Runtime.lock();
                                                   {
                                                       
//#line 56
ClockTest7.this.val += 1;
                                                   }
                                               }}finally {{
                                                     
//#line 56
x10.
                                                       lang.
                                                       Runtime.release();
                                                 }}
                                               
//#line 57
x10.
                                                 io.
                                                 Console.OUT.println((((("Activity ") + (i))) + (" phase 0")));
                                               
//#line 58
x10.
                                                 lang.
                                                 Runtime.next();
                                               
//#line 59
try {{
                                                   
//#line 59
x10.
                                                     lang.
                                                     Runtime.lock();
                                                   {
                                                       
//#line 59
harness.
                                                         x10Test.chk((boolean)(((int) val) ==
                                                                     ((int) ClockTest7.N)));
                                                   }
                                               }}finally {{
                                                     
//#line 59
x10.
                                                       lang.
                                                       Runtime.release();
                                                 }}
                                               
//#line 60
x10.
                                                 io.
                                                 Console.OUT.println((((("Activity ") + (i))) + (" phase 1")));
                                               
//#line 61
x10.
                                                 lang.
                                                 Runtime.next();
                                               
//#line 62
try {{
                                                   
//#line 62
x10.
                                                     lang.
                                                     Runtime.lock();
                                                   {
                                                       
//#line 62
ClockTest7.this.val += 1;
                                                   }
                                               }}finally {{
                                                     
//#line 62
x10.
                                                       lang.
                                                       Runtime.release();
                                                 }}
                                               
//#line 63
x10.
                                                 io.
                                                 Console.OUT.println((((("Activity ") + (i))) + (" phase 2")));
                                               
//#line 64
c.next();
                                               }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                               });
                        }
                        	}
                            } else {
                        	assert false;
                            }
                        }
                        /* } */
                        
                    }
                }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
                if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
                java.lang.Throwable __desugarer__var__510__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                {
                    
//#line 55
x10.
                      lang.
                      Runtime.pushException(__desugarer__var__510__);
                }
                }
                throw __$generated_wrappedex$__;
                }catch (java.lang.Throwable __desugarer__var__510__) {
                    
//#line 55
x10.
                      lang.
                      Runtime.pushException(__desugarer__var__510__);
                }finally {{
                     
//#line 55
x10.
                       lang.
                       Runtime.stopFinish();
                 }}
                
//#line 67
x10.
                  lang.
                  Runtime.next();
                
//#line 67
x10.
                  lang.
                  Runtime.next();
                
//#line 67
x10.
                  lang.
                  Runtime.next();
                
//#line 69
try {{
                    
//#line 69
x10.
                      lang.
                      Runtime.lock();
                    {
                        
//#line 69
harness.
                          x10Test.chk((boolean)(((int) val) ==
                                      ((int) ((((int)(2))) * (((int)(ClockTest7.N)))))));
                    }
                }}finally {{
                      
//#line 69
x10.
                        lang.
                        Runtime.release();
                  }}
                }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
                if (__$generated_wrappedex$__.getCause() instanceof x10.
                  lang.
                  MultipleExceptions) {
                final x10.
                  lang.
                  MultipleExceptions e = (x10.
                  lang.
                  MultipleExceptions) __$generated_wrappedex$__.getCause();
                {
                    
//#line 72
x10.
                      io.
                      Console.OUT.println("MultipleExceptions");
                    
//#line 73
return x10.rtt.Types.runtimeType(java.lang.Error.class).instanceof$(((java.lang.Throwable)((Object[])e.
                                                                                                                                       exceptions.value)[0]));
                }
                }
                if (__$generated_wrappedex$__.getCause() instanceof java.lang.Error) {
                final java.lang.Error e = (java.lang.Error) __$generated_wrappedex$__.getCause();
                {
                    
//#line 75
x10.
                      io.
                      Console.OUT.println("Error");
                    
//#line 76
return true;
                }
                }
                throw __$generated_wrappedex$__;
                }catch (final x10.
                          lang.
                          MultipleExceptions e) {
                    
//#line 72
x10.
                      io.
                      Console.OUT.println("MultipleExceptions");
                    
//#line 73
return x10.rtt.Types.runtimeType(java.lang.Error.class).instanceof$(((java.lang.Throwable)((Object[])e.
                                                                                                                                       exceptions.value)[0]));
                }catch (final java.lang.Error e) {
                    
//#line 75
x10.
                      io.
                      Console.OUT.println("Error");
                    
//#line 76
return true;
                }
                
//#line 79
return false;
            }
            
            
//#line 82
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
            							ClockTest7.main(args);
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
                
//#line 83
new ClockTest7().executeAsync();
            }/* } */
            
            public ClockTest7() {
                super();
                
//#line 48
this.val = 0;
            }
        
        }
        