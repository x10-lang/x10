
public class ArrayAccessWithPoint
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ArrayAccessWithPoint>_RTT = new x10.rtt.RuntimeType<ArrayAccessWithPoint>(
/* base class */ArrayAccessWithPoint.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 21
void
                  a(
                  final int b){
        
    }
    
    
//#line 23
public boolean
                  run(
                  ){
        
//#line 25
final x10.
          array.
          Region e =
          ((x10.
          array.
          Region)(x10.
          array.
          Region.makeRectangular((int)(1),
                                 (int)(10))));
        
//#line 26
final x10.
          array.
          Array<java.lang.Integer> ia =
          ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Integer>(x10.rtt.Types.INT,
                                   e,
                                   new x10.core.fun.Fun_0_1<x10.
                                     array.
                                     Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                     array.
                                     Point id$11442) { return apply(id$11442);}
                                   public final int apply(final x10.
                                     array.
                                     Point id$11442) { {
                                       
//#line 26
return 0;
                                   }}
                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                   }
                                   })));
        
//#line 27
final x10.
          array.
          Point p =
          ((x10.
          array.
          Point)(x10.
          array.
          Point.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { 1 })/* } */)));
        
//#line 29
this.a((int)(java.lang.Integer)(ia.apply$G(p)));
        
//#line 31
return true;
    }
    
    
//#line 34
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
    							ArrayAccessWithPoint.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$11443)  {
        
//#line 35
new ArrayAccessWithPoint().execute();
    }/* } */
    
    public ArrayAccessWithPoint() {
        super();
    }

}
