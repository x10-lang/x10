
public class WhenReturnAll
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<WhenReturnAll>_RTT = new x10.rtt.RuntimeType<WhenReturnAll>(
/* base class */WhenReturnAll.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 21
int
                  test(
                  ){
        
//#line 22
int ret =
          0;
        
//#line 23
try {{
            
//#line 23
x10.
              lang.
              Runtime.lock();
            
//#line 23
while (true) {
                
//#line 23
if (WhenReturnAll.
                                  X.t()) {
                    
//#line 24
return 1;
                }
                
//#line 23
if (WhenReturnAll.
                                  X.t()) {
                    
//#line 26
return 2;
                }
                
//#line 23
x10.
                  lang.
                  Runtime.await();
            }
        }}finally {{
              
//#line 23
x10.
                lang.
                Runtime.release();
          }}
        }
    
    
//#line 30
public boolean
                  run(
                  ){
        
//#line 31
int x =
          this.test();
        
//#line 32
return true;
    }
    
    
//#line 35
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
    							WhenReturnAll.main(args);
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
        
//#line 36
new WhenReturnAll().execute();
    }/* } */
    
    
//#line 39
static class X
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<WhenReturnAll.
      X>_RTT = new x10.rtt.RuntimeType<WhenReturnAll.
      X>(
    /* base class */WhenReturnAll.
      X.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
        
//#line 40
static boolean
                      t(
                      ){
            
//#line 40
return true;
        }
        
        public X() {
            super();
        }
    
    }
    
    
    public WhenReturnAll() {
        super();
    }
    
    }
    