
public class Array4
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<Array4>_RTT = new x10.rtt.RuntimeType<Array4>(
/* base class */Array4.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 19
x10.
      array.
      Array<java.lang.Integer>
      ia;
    
    
//#line 21
public Array4() {
        
//#line 21
super();
        
//#line 19
this.ia = null;
    }
    
    
//#line 23
public Array4(x10.
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
                             (int)(1),
                             (int)(1));
        
//#line 29
return ((int) 42) ==
        ((int) ia.apply$G((int)(1),
                          (int)(1)));
    }
    
    
//#line 35
public boolean
                  run(
                  ){
        
//#line 36
return (new Array4(new x10.
                                         array.
                                         Array<java.lang.Integer>(x10.rtt.Types.INT,
                                                                  x10.
                                                                    array.
                                                                    Region.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                                               array.
                                                                                               Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                                                                                               array.
                                                                                               Region[] { x10.
                                                                                               array.
                                                                                               Region.makeRectangular((int)(1),
                                                                                                                      (int)(10)),x10.
                                                                                               array.
                                                                                               Region.makeRectangular((int)(1),
                                                                                                                      (int)(10)) })/* } */),
                                                                  new x10.core.fun.Fun_0_1<x10.
                                                                    array.
                                                                    Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                                                    array.
                                                                    Point id$9933) { return apply(id$9933);}
                                                                  public final int apply(final x10.
                                                                    array.
                                                                    Point id$9933) { {
                                                                      
//#line 36
return 0;
                                                                  }}
                                                                  public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                                                  }
                                                                  }))).runtest();
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
    							Array4.main(args);
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
new Array4().execute();
    }/* } */

}
