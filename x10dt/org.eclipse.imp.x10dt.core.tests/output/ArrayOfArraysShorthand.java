
public class ArrayOfArraysShorthand
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ArrayOfArraysShorthand>_RTT = new x10.rtt.RuntimeType<ArrayOfArraysShorthand>(
/* base class */ArrayOfArraysShorthand.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 22
public boolean
                  run(
                  ){
        
//#line 24
final x10.
          array.
          Region r1 =
          ((x10.
          array.
          Region)(x10.
          array.
          Region.makeRectangular((int)(0),
                                 (int)(7))));
        
//#line 25
final x10.
          array.
          Region r2 =
          ((x10.
          array.
          Region)(x10.
          array.
          Region.makeRectangular((int)(0),
                                 (int)(9))));
        
//#line 26
final x10.
          array.
          Region r =
          ((x10.
          array.
          Region)(x10.
          array.
          Region.make(/* template:tuple { */x10.core.RailFactory.<x10.
                        array.
                        Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                        array.
                        Region[] { r1,r2 })/* } */)));
        
//#line 27
final x10.
          array.
          Array<x10.
          array.
          Array<java.lang.Integer>> ia =
          ((x10.
          array.
          Array)(new x10.
          array.
          Array<x10.
          array.
          Array<java.lang.Integer>>(new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.INT),
                                    r1,
                                    new x10.core.fun.Fun_0_1<x10.
                                      array.
                                      Point, x10.
                                      array.
                                      Array<java.lang.Integer>>() {public final x10.
                                      array.
                                      Array<java.lang.Integer> apply$G(final x10.
                                      array.
                                      Point id$15574) { return apply(id$15574);}
                                    public final x10.
                                      array.
                                      Array<java.lang.Integer> apply(final x10.
                                      array.
                                      Point id$15574) { {
                                        
//#line 27
return new x10.
                                          array.
                                          Array<java.lang.Integer>(x10.rtt.Types.INT,
                                                                   r2,
                                                                   new x10.core.fun.Fun_0_1<x10.
                                                                     array.
                                                                     Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                                                     array.
                                                                     Point id15575) { return apply(id15575);}
                                                                   public final int apply(final x10.
                                                                     array.
                                                                     Point id15575) { {
                                                                       
//#line 27
final int j =
                                                                         id15575.apply((int)(0));
                                                                       
//#line 27
return j;
                                                                   }}
                                                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                                                   }
                                                                   });
                                    }}
                                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.INT);return null;
                                    }
                                    })));
        
//#line 29
for (
//#line 29
final x10.core.Iterator<x10.
                           array.
                           Point> id15651 =
                           r.iterator();
                         id15651.hasNext();
                         ) {
            
//#line 29
final x10.
              array.
              Point id15576 =
              ((x10.
                array.
                Point)
                id15651.next$G());
            
//#line 29
final int i =
              id15576.apply((int)(0));
            
//#line 29
final int j =
              id15576.apply((int)(1));
            
//#line 29
harness.
              x10Test.chk((boolean)(((int) ia.apply$G((int)(i)).apply$G((int)(j))) ==
                          ((int) j)));
        }
        
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
    							ArrayOfArraysShorthand.main(args);
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
        
//#line 35
new ArrayOfArraysShorthand().execute();
    }/* } */
    
    public ArrayOfArraysShorthand() {
        super();
    }

}
