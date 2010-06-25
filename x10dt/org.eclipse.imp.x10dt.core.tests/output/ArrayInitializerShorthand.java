
public class ArrayInitializerShorthand
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ArrayInitializerShorthand>_RTT = new x10.rtt.RuntimeType<ArrayInitializerShorthand>(
/* base class */ArrayInitializerShorthand.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 20
public boolean
                  run(
                  ){
        
//#line 21
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
        
//#line 22
final x10.
          array.
          Array<java.lang.Double> ia =
          ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Double>(x10.rtt.Types.DOUBLE,
                                  r,
                                  new x10.core.fun.Fun_0_1<x10.
                                    array.
                                    Point, java.lang.Double>() {public final java.lang.Double apply$G(final x10.
                                    array.
                                    Point id14988) { return apply(id14988);}
                                  public final double apply(final x10.
                                    array.
                                    Point id14988) { {
                                      
//#line 22
final int i =
                                        id14988.apply((int)(0));
                                      
//#line 22
final int j =
                                        id14988.apply((int)(1));
                                      
//#line 22
return ((double)(int)(((int)(((((int)(i))) + (((int)(j))))))));
                                  }}
                                  public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.DOUBLE;return null;
                                  }
                                  })));
        
//#line 24
for (
//#line 24
final x10.core.Iterator<x10.
                           array.
                           Point> p15061 =
                           r.iterator();
                         p15061.hasNext();
                         ) {
            
//#line 24
final x10.
              array.
              Point p =
              ((x10.
              array.
              Point)(p15061.next$G()));
            
//#line 24
final int i =
              p.apply((int)(0));
            
//#line 24
final int j =
              p.apply((int)(1));
            
//#line 24
harness.
              x10Test.chk((boolean)(((double) ia.apply$G(p)) ==
                          ((double) ((double)(int)(((int)(((((int)(i))) + (((int)(j)))))))))));
        }
        
//#line 26
return true;
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
    							ArrayInitializerShorthand.main(args);
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
        
//#line 30
new ArrayInitializerShorthand().execute();
    }/* } */
    
    public ArrayInitializerShorthand() {
        super();
    }

}
