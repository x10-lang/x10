
public class AsyncReturn
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<AsyncReturn>_RTT = new x10.rtt.RuntimeType<AsyncReturn>(
/* base class */AsyncReturn.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 21
public boolean
                  run(
                  ){
        
//#line 25
final AsyncReturn.
          T$25 f =
          ((AsyncReturn.
          T$25)(new AsyncReturn.
          T$25(this)));
        
//#line 26
f.t = 1;
        
//#line 27
final int v =
          f.
            t;
        
//#line 28
final x10.core.fun.VoidFun_0_0 body =
          ((x10.core.fun.VoidFun_0_0)(new x10.core.fun.VoidFun_0_0() {public final void apply() { {
            
//#line 29
if (((int) v) ==
                            ((int) 1)) {
                
//#line 30
return;
            }
            
//#line 31
x10.
              lang.
              Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                   
//#line 31
x10.
                                     lang.
                                     Runtime.runAt(x10.lang.Place.place(x10.core.Ref.home(f)),
                                                   new x10.core.fun.VoidFun_0_0() {public final void apply() { {
                                                       
//#line 32
try {{
                                                           
//#line 32
x10.
                                                             lang.
                                                             Runtime.lock();
                                                           {
                                                               
//#line 33
f.t = 2;
                                                           }
                                                       }}finally {{
                                                             
//#line 32
x10.
                                                               lang.
                                                               Runtime.release();
                                                         }}
                                                       }}
                                                       });
                                   }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                   });
            }}
            }));
        
//#line 37
try {{
            
//#line 37
x10.
              lang.
              Runtime.startFinish();
            {
                
//#line 37
x10.
                  lang.
                  Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                       
//#line 37
body.apply();
                                   }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                   });
            }
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
        java.lang.Throwable __desugarer__var__292__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 37
x10.
              lang.
              Runtime.pushException(__desugarer__var__292__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__292__) {
            
//#line 37
x10.
              lang.
              Runtime.pushException(__desugarer__var__292__);
        }finally {{
             
//#line 37
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 38
return (((int) f.
                                     t) ==
                            ((int) 1));
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
        							AsyncReturn.main(args);
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
new AsyncReturn().execute();
        }/* } */
        
        public AsyncReturn() {
            super();
        }
        
        
//#line 22
private static class T$25
                    extends x10.core.Ref
                    {public static final x10.rtt.RuntimeType<AsyncReturn.
          T$25>_RTT = new x10.rtt.RuntimeType<AsyncReturn.
          T$25>(
        /* base class */AsyncReturn.
          T$25.class
        , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
        );
        public x10.rtt.RuntimeType getRTT() {return _RTT;}
        
        
        
            
//#line 19
final private AsyncReturn
              out$;
            
//#line 23
int
              t;
            
            public T$25(final AsyncReturn out$) {
                super();
                
//#line 19
this.out$ = out$;
                
//#line 23
this.t = 0;
            }
        
        }
        
    
    }
    