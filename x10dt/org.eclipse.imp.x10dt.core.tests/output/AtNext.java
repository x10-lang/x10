
public class AtNext
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<AtNext>_RTT = new x10.rtt.RuntimeType<AtNext>(
/* base class */AtNext.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 22
public boolean
                  run(
                  ){
        
//#line 23
final x10.
          lang.
          Place Other =
          x10.
          lang.
          Runtime.here().next();
        
//#line 24
final AtNext.
          T t =
          ((AtNext.
          T)(new AtNext.
          T()));
        
//#line 25
x10.
          lang.
          Runtime.runAt(Other,
                        new x10.core.fun.VoidFun_0_0() {public final void apply() { {
                            
//#line 26
final AtNext.
                              T t1 =
                              new AtNext.
                              T();
                            
//#line 27
x10.
                              lang.
                              Runtime.runAt(x10.lang.Place.place(x10.core.Ref.home(t)),
                                            new x10.core.fun.VoidFun_0_0() {public final void apply() { {
                                                
//#line 27
t.val = ((java.lang.Object)(t1));
                                            }}
                                            });
                        }}
                        });
        
//#line 29
return /* template:equalsequals { */x10.rtt.Equality.equalsequals(x10.lang.Place.place(x10.core.Ref.home(t.
                                                                                                                               val)),Other)/* } */;
    }
    
    
//#line 32
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
    							AtNext.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$31734)  {
        
//#line 33
new AtNext().execute();
    }/* } */
    
    
//#line 36
static class T
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<AtNext.
      T>_RTT = new x10.rtt.RuntimeType<AtNext.
      T>(
    /* base class */AtNext.
      T.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 37
java.lang.Object
          val;
        
        public T() {
            super();
            
//#line 37
this.val = null;
        }
    
    }
    
    
    public AtNext() {
        super();
    }

}
