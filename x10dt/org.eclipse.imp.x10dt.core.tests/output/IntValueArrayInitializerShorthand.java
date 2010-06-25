
public class IntValueArrayInitializerShorthand
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<IntValueArrayInitializerShorthand>_RTT = new x10.rtt.RuntimeType<IntValueArrayInitializerShorthand>(
/* base class */IntValueArrayInitializerShorthand.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 20
public boolean
                  run(
                  ){
        
//#line 22
final x10.
          array.
          Array<java.lang.Integer> ia =
          ((x10.
          array.
          Array)(new x10.
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
                                     Point id24467) { return apply(id24467);}
                                   public final int apply(final x10.
                                     array.
                                     Point id24467) { {
                                       
//#line 22
final int i =
                                         id24467.apply((int)(0));
                                       
//#line 22
final int j =
                                         id24467.apply((int)(1));
                                       
//#line 22
return ((((int)(i))) + (((int)(j))));
                                   }}
                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                   }
                                   })));
        {
            
//#line 24
final x10.
              array.
              Region p24544 =
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
            
//#line 24
final x10.core.Rail<java.lang.Integer> p24545 =
              ((x10.core.Rail)(x10.core.RailFactory.<java.lang.Integer>makeVarRail(x10.rtt.Types.INT, ((int)(2)))));
            
//#line 24
final int j24546min24547 =
              p24544.min((int)(1));
            
//#line 24
final int j24546max24548 =
              p24544.max((int)(1));
            
//#line 24
final int i24549min24550 =
              p24544.min((int)(0));
            
//#line 24
final int i24549max24551 =
              p24544.max((int)(0));
            
//#line 24
for (
//#line 24
int i24549 =
                               i24549min24550;
                             ((((int)(i24549))) <= (((int)(i24549max24551))));
                             
//#line 24
i24549 += 1) {
                
//#line 24
final int i =
                  i24549;
                
//#line 24
((int[])p24545.value)[0] = i24549;
                
//#line 24
for (
//#line 24
int j24546 =
                                   j24546min24547;
                                 ((((int)(j24546))) <= (((int)(j24546max24548))));
                                 
//#line 24
j24546 += 1) {
                    
//#line 24
final int j =
                      j24546;
                    
//#line 24
((int[])p24545.value)[1] = j24546;
                    
//#line 24
final x10.
                      array.
                      Point p =
                      ((x10.
                      array.
                      Point)(x10.
                      array.
                      Point.make(p24545)));
                    {
                        
//#line 24
harness.
                          x10Test.chk((boolean)(((int) ia.apply$G(p)) ==
                                      ((int) ((((int)(i))) + (((int)(j)))))));
                    }
                }
            }
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
    							IntValueArrayInitializerShorthand.main(args);
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
new IntValueArrayInitializerShorthand().execute();
    }/* } */
    
    public IntValueArrayInitializerShorthand() {
        super();
    }

}
