
public class ConsistentInterfaceInvariants
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ConsistentInterfaceInvariants>_RTT = new x10.rtt.RuntimeType<ConsistentInterfaceInvariants>(
/* base class */ConsistentInterfaceInvariants.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 22
public static interface Test
                {public static final x10.rtt.RuntimeType<ConsistentInterfaceInvariants.
      Test>_RTT = new x10.rtt.RuntimeType<ConsistentInterfaceInvariants.
      Test>(
    /* base class */ConsistentInterfaceInvariants.
      Test.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    
        
        
//#line 23
int
                      put(
                      );
        
        int
          l(
          );
        
        int
          m(
          );
    
    }
    
    
//#line 26
public static interface Test1
                  extends ConsistentInterfaceInvariants.
                            Test
                {public static final x10.rtt.RuntimeType<ConsistentInterfaceInvariants.
      Test1>_RTT = new x10.rtt.RuntimeType<ConsistentInterfaceInvariants.
      Test1>(
    /* base class */ConsistentInterfaceInvariants.
      Test1.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class), ConsistentInterfaceInvariants.Test._RTT}
    );
    
        
        
//#line 27
int
                      foo(
                      );
        
        int
          n(
          );
    
    }
    
    
    
//#line 30
public boolean
                  run(
                  ){
        
//#line 30
return true;
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
    							ConsistentInterfaceInvariants.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> a)  {
        
//#line 33
new ConsistentInterfaceInvariants().execute();
    }/* } */
    
    public ConsistentInterfaceInvariants() {
        super();
    }

}
