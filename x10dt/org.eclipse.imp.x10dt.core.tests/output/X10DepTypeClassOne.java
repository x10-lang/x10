
public class X10DepTypeClassOne
extends harness.
  x10Test
  implements X10InterfaceOne
{public static final x10.rtt.RuntimeType<X10DepTypeClassOne>_RTT = new x10.rtt.RuntimeType<X10DepTypeClassOne>(
/* base class */X10DepTypeClassOne.class
, /* parents */ new x10.rtt.Type[] {X10InterfaceOne._RTT, harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 18
final public int
      p;
    
    
    
//#line 21
public X10DepTypeClassOne(final int p) {
        
//#line 21
super();
        
//#line 22
this.p = p;
    }
    
    
//#line 25
public boolean
                  run(
                  ){
        
//#line 26
final int p =
          1;
        
//#line 27
X10DepTypeClassOne one =
          new X10DepTypeClassOne(p);
        
//#line 28
return ((int) one.p()) ==
        ((int) p);
    }
    
    
//#line 31
public void
                  interfaceMethod(
                  ){
        
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
    							X10DepTypeClassOne.main(args);
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
new X10DepTypeClassOne(0).execute();
    }/* } */
    
    final public int
      p(
      ){
        return this.
                 p;
    }

}
