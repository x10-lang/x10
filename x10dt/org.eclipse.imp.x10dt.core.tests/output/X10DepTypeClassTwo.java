
public class X10DepTypeClassTwo
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<X10DepTypeClassTwo>_RTT = new x10.rtt.RuntimeType<X10DepTypeClassTwo>(
/* base class */X10DepTypeClassTwo.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 18
final public int
      p;
    
//#line 18
final public int
      q;
    
    
    
//#line 20
public X10DepTypeClassTwo(final int a,
                                          final int b) {
        
//#line 20
super();
        
//#line 21
this.p = a;
        
//#line 21
this.q = b;
    }
    
    
//#line 24
public boolean
                  run(
                  ){
        
//#line 25
X10DepTypeClassTwo one =
          ((X10DepTypeClassTwo)(new X10DepTypeClassTwo(this.
                                                         p,
                                                       0)));
        
//#line 26
return ((int) one.p()) ==
        ((int) 0);
    }
    
    
//#line 29
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
    							X10DepTypeClassTwo.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> args)  {
        
//#line 30
new X10DepTypeClassTwo(0,
                                           0).execute();
    }/* } */
    
    final public int
      p(
      ){
        return this.
                 p;
    }
    
    final public int
      q(
      ){
        return this.
                 q;
    }

}
