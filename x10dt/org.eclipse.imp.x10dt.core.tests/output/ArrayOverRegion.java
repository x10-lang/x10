
public class ArrayOverRegion
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ArrayOverRegion>_RTT = new x10.rtt.RuntimeType<ArrayOverRegion>(
/* base class */ArrayOverRegion.class
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
        
//#line 23
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
                                    Point, java.lang.Double>() {public final java.lang.Double apply$G(x10.
                                    array.
                                    Point id16169) { return apply(id16169);}
                                  public final double apply(x10.
                                    array.
                                    Point id16169) { {
                                      
//#line 23
final int i =
                                        id16169.apply((int)(0));
                                      
//#line 23
final int j =
                                        id16169.apply((int)(1));
                                      
//#line 23
return ((double)(int)(((int)(((((int)(i))) + (((int)(j))))))));
                                  }}
                                  public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.DOUBLE;return null;
                                  }
                                  })));
        
//#line 25
harness.
          x10Test.chk((boolean)(((Object)ia.
                                           region).equals(r)));
        {
            
//#line 27
final x10.
              array.
              Region p16242 =
              ((x10.
              array.
              Region)(r));
            
//#line 27
final x10.core.Rail<java.lang.Integer> p16243 =
              ((x10.core.Rail)(x10.core.RailFactory.<java.lang.Integer>makeVarRail(x10.rtt.Types.INT, ((int)(2)))));
            
//#line 27
final int j16244min16245 =
              p16242.min((int)(1));
            
//#line 27
final int j16244max16246 =
              p16242.max((int)(1));
            
//#line 27
final int i16247min16248 =
              p16242.min((int)(0));
            
//#line 27
final int i16247max16249 =
              p16242.max((int)(0));
            
//#line 27
for (
//#line 27
int i16247 =
                               i16247min16248;
                             ((((int)(i16247))) <= (((int)(i16247max16249))));
                             
//#line 27
i16247 += 1) {
                
//#line 27
final int i =
                  i16247;
                
//#line 27
((int[])p16243.value)[0] = i16247;
                
//#line 27
for (
//#line 27
int j16244 =
                                   j16244min16245;
                                 ((((int)(j16244))) <= (((int)(j16244max16246))));
                                 
//#line 27
j16244 += 1) {
                    
//#line 27
final int j =
                      j16244;
                    
//#line 27
((int[])p16243.value)[1] = j16244;
                    
//#line 27
final x10.
                      array.
                      Point p =
                      ((x10.
                      array.
                      Point)(x10.
                      array.
                      Point.make(p16243)));
                    {
                        
//#line 27
harness.
                          x10Test.chk((boolean)(((double) ia.apply$G(p)) ==
                                      ((double) ((double)(int)(((int)(((((int)(i))) + (((int)(j)))))))))));
                    }
                }
            }
        }
        
//#line 29
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
    							ArrayOverRegion.main(args);
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
        
//#line 33
new ArrayOverRegion().execute();
    }/* } */
    
    public ArrayOverRegion() {
        super();
    }

}
