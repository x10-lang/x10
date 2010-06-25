
public class Array3Short
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<Array3Short>_RTT = new x10.rtt.RuntimeType<Array3Short>(
/* base class */Array3Short.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 19
public boolean
                  run(
                  ){
        
//#line 20
final x10.
          array.
          Region r =
          ((x10.
          array.
          Region)(x10.
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
                                                            (int)(10)) })/* } */)));
        
//#line 21
final x10.
          array.
          Array<java.lang.Short> ia =
          ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Short>(x10.rtt.Types.SHORT,
                                 r,
                                 new x10.core.fun.Fun_0_1<x10.
                                   array.
                                   Point, java.lang.Short>() {public final java.lang.Short apply$G(final x10.
                                   array.
                                   Point x) { return apply(x);}
                                 public final short apply(final x10.
                                   array.
                                   Point x) { {
                                     
//#line 21
return (((short)(int)(((int)(0)))));
                                 }}
                                 public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.SHORT;return null;
                                 }
                                 })));
        
//#line 22
ia.set$G((short)(((short)(int)(((int)(42))))),
                             (int)(1),
                             (int)(1));
        
//#line 23
return (((int) 42) ==
                            ((int) ((int)(short)(((short)(ia.apply$G((int)(1),
                                                                     (int)(1))))))));
    }
    
    
//#line 26
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
    							Array3Short.main(args);
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
        
//#line 27
new Array3Short().execute();
    }/* } */
    
    public Array3Short() {
        super();
    }

}
