
public class Array5
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<Array5>_RTT = new x10.rtt.RuntimeType<Array5>(
/* base class */Array5.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 19
x10.
      array.
      Array<java.lang.Integer>
      ia;
    
    
//#line 21
public Array5() {
        
//#line 21
super();
        
//#line 19
this.ia = null;
    }
    
    
//#line 23
public Array5(x10.
                                array.
                                Array<java.lang.Integer> ia) {
        
//#line 23
super();
        
//#line 19
this.ia = null;
        
//#line 24
this.ia = ((x10.
          array.
          Array)(ia));
    }
    
    
//#line 27
private boolean
                  runtest(
                  ){
        
//#line 28
ia.set$G((int)(42),
                             (int)(0));
        
//#line 29
return ((int) 42) ==
        ((int) ia.apply$G((int)(0)));
    }
    
    
//#line 32
public boolean
                  run(
                  ){
        
//#line 33
final x10.
          array.
          Array<java.lang.Integer> temp =
          ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Integer>(x10.rtt.Types.INT,
                                   1,
                                   new x10.core.fun.Fun_0_1<x10.
                                     array.
                                     Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                     array.
                                     Point id$10109) { return apply(id$10109);}
                                   public final int apply(final x10.
                                     array.
                                     Point id$10109) { {
                                       
//#line 33
return 0;
                                   }}
                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                   }
                                   })));
        
//#line 34
temp.set$G((int)(43),
                               (int)(0));
        
//#line 35
return (new Array5(temp)).runtest();
    }
    
    
//#line 38
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
    							Array5.main(args);
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
        
//#line 39
new Array5().execute();
    }/* } */

}
