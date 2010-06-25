
public class WhenReturn
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<WhenReturn>_RTT = new x10.rtt.RuntimeType<WhenReturn>(
/* base class */WhenReturn.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 22
int
                  test(
                  ){
        
//#line 23
int ret =
          0;
        
//#line 24
try {{
            
//#line 24
x10.
              lang.
              Runtime.lock();
            
//#line 24
while (true) {
                
//#line 24
if (WhenReturn.
                                  X.t()) {
                    
//#line 25
return 1;
                }
                
//#line 24
if (WhenReturn.
                                  X.t()) {
                    {
                        
//#line 27
ret = 2;
                    }
                    
//#line 24
break;
                }
                
//#line 24
x10.
                  lang.
                  Runtime.await();
            }
        }}finally {{
              
//#line 24
x10.
                lang.
                Runtime.release();
          }}
        
//#line 29
return ret;
        }
    
    
//#line 32
public boolean
                  run(
                  ){
        
//#line 33
int x =
          this.test();
        
//#line 34
return true;
    }
    
    
//#line 37
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
    							WhenReturn.main(args);
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
        
//#line 38
new WhenReturn().execute();
    }/* } */
    
    
//#line 41
static class X
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<WhenReturn.
      X>_RTT = new x10.rtt.RuntimeType<WhenReturn.
      X>(
    /* base class */WhenReturn.
      X.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
        
//#line 42
static boolean
                      t(
                      ){
            
//#line 42
return true;
        }
        
        public X() {
            super();
        }
    
    }
    
    
    public WhenReturn() {
        super();
    }
    
    }
    