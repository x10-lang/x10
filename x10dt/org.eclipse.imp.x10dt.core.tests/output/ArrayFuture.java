
public class ArrayFuture
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ArrayFuture>_RTT = new x10.rtt.RuntimeType<ArrayFuture>(
/* base class */ArrayFuture.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 22
public boolean
                  run(
                  ){
        
//#line 23
final x10.
          array.
          Dist d =
          ((x10.
          array.
          Dist)(x10.
          array.
          Dist.makeConstant(x10.
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
                            x10.
                              lang.
                              Runtime.here())));
        
//#line 24
final x10.
          array.
          DistArray<x10.
          lang.
          Future<java.lang.Integer>> ia =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<x10.
          lang.
          Future<java.lang.Integer>>make(new x10.rtt.ParameterizedType(x10.lang.Future._RTT, x10.rtt.Types.INT),
                                         d,
                                         new x10.core.fun.Fun_0_1<x10.
                                           array.
                                           Point, x10.
                                           lang.
                                           Future<java.lang.Integer>>() {public final x10.
                                           lang.
                                           Future<java.lang.Integer> apply$G(final x10.
                                           array.
                                           Point id13684) { return apply(id13684);}
                                         public final x10.
                                           lang.
                                           Future<java.lang.Integer> apply(final x10.
                                           array.
                                           Point id13684) { {
                                             
//#line 24
final int i =
                                               id13684.apply((int)(0));
                                             
//#line 24
final int j =
                                               id13684.apply((int)(1));
                                             
//#line 24
return x10.
                                               lang.
                                               Runtime.<java.lang.Integer>evalFuture(x10.rtt.Types.INT,
                                                                                     x10.
                                                                                       lang.
                                                                                       Runtime.here(),
                                                                                     new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                                                                     public final int apply() { {
                                                                                         
//#line 24
return ((((int)(i))) + (((int)(j))));
                                                                                     }}
                                                                                     public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                                                                     }
                                                                                     });
                                         }}
                                         public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return new x10.rtt.ParameterizedType(x10.lang.Future._RTT, x10.rtt.Types.INT);return null;
                                         }
                                         })));
        {
            
//#line 25
final x10.
              array.
              Region p13761 =
              ((x10.
              array.
              Region)(ia.region()));
            
//#line 25
final int j13762min13763 =
              p13761.min((int)(1));
            
//#line 25
final int j13762max13764 =
              p13761.max((int)(1));
            
//#line 25
final int i13765min13766 =
              p13761.min((int)(0));
            
//#line 25
final int i13765max13767 =
              p13761.max((int)(0));
            
//#line 25
for (
//#line 25
int i13765 =
                               i13765min13766;
                             ((((int)(i13765))) <= (((int)(i13765max13767))));
                             
//#line 25
i13765 += 1) {
                
//#line 25
final int i =
                  i13765;
                
//#line 25
for (
//#line 25
int j13762 =
                                   j13762min13763;
                                 ((((int)(j13762))) <= (((int)(j13762max13764))));
                                 
//#line 25
j13762 += 1) {
                    
//#line 25
final int j =
                      j13762;
                    {
                        
//#line 25
harness.
                          x10Test.chk((boolean)(((int) ia.apply$G((int)(i),
                                                                  (int)(j)).apply$G()) ==
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
    							ArrayFuture.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$13686)  {
        
//#line 30
new ArrayFuture().execute();
    }/* } */
    
    public ArrayFuture() {
        super();
    }

}
