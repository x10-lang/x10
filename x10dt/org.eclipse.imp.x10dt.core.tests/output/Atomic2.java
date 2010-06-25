
public class Atomic2
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<Atomic2>_RTT = new x10.rtt.RuntimeType<Atomic2>(
/* base class */Atomic2.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 22
int
      x;
    
    
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
                  Runtime.runAsync(x10.lang.Place.place(x10.core.Ref.home(this)),
                                   new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                       
//#line 26
try {{
                                           
//#line 26
x10.
                                             lang.
                                             Runtime.lock();
                                           {
                                               
//#line 26
Atomic2.this.x += 1;
                                           }
                                       }}finally {{
                                             
//#line 26
x10.
                                               lang.
                                               Runtime.release();
                                         }}
                                       }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                       });
                }
            }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
            if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
            java.lang.Throwable __desugarer__var__352__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 26
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__352__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__352__) {
                
//#line 26
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__352__);
            }finally {{
                 
//#line 26
x10.
                   lang.
                   Runtime.stopFinish();
             }}
            
//#line 27
try {{
                
//#line 27
x10.
                  lang.
                  Runtime.lock();
                {
                    
//#line 27
harness.
                      x10Test.chk((boolean)(((int) x) ==
                                  ((int) 1)));
                }
            }}finally {{
                  
//#line 27
x10.
                    lang.
                    Runtime.release();
              }}
            
//#line 29
boolean gotException =
              false;
            
//#line 30
try {{
                
//#line 31
try {{
                    
//#line 31
x10.
                      lang.
                      Runtime.lock();
                    {
                        
//#line 31
harness.
                          x10Test.chk((boolean)(((int) x) ==
                                      ((int) 0)));
                    }
                }}finally {{
                      
//#line 31
x10.
                        lang.
                        Runtime.release();
                  }}
                }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
                if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
                java.lang.Throwable e = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                {
                    
//#line 33
gotException = true;
                }
                }
                throw __$generated_wrappedex$__;
                }catch (java.lang.Throwable e) {
                    
//#line 33
gotException = true;
                }
            
//#line 35
harness.
              x10Test.chk((boolean)(gotException));
            
//#line 36
return true;
            }
        
        
//#line 39
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
        							Atomic2.main(args);
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
            
//#line 40
new Atomic2().execute();
        }/* } */
        
        public Atomic2() {
            super();
            
//#line 22
this.x = 0;
        }
        
        }
        