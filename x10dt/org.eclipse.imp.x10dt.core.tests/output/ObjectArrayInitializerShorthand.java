
public class ObjectArrayInitializerShorthand
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ObjectArrayInitializerShorthand>_RTT = new x10.rtt.RuntimeType<ObjectArrayInitializerShorthand>(
/* base class */ObjectArrayInitializerShorthand.class
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
          array.
          Dist> ia =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<x10.
          array.
          Dist>make(x10.array.Dist._RTT,
                    d,
                    new x10.core.fun.Fun_0_1<x10.
                      array.
                      Point, x10.
                      array.
                      Dist>() {public final x10.
                      array.
                      Dist apply$G(final x10.
                      array.
                      Point id25261) { return apply(id25261);}
                    public final x10.
                      array.
                      Dist apply(final x10.
                      array.
                      Point id25261) { {
                        
//#line 24
final int i =
                          id25261.apply((int)(0));
                        
//#line 24
final int j =
                          id25261.apply((int)(1));
                        
//#line 24
return d;
                    }}
                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.array.Dist._RTT;return null;
                    }
                    })));
        {
            
//#line 25
final x10.
              array.
              Region p25337 =
              ((x10.
              array.
              Region)(ia.region()));
            
//#line 25
final int j25338min25339 =
              p25337.min((int)(1));
            
//#line 25
final int j25338max25340 =
              p25337.max((int)(1));
            
//#line 25
final int i25341min25342 =
              p25337.min((int)(0));
            
//#line 25
final int i25341max25343 =
              p25337.max((int)(0));
            
//#line 25
for (
//#line 25
int i25341 =
                               i25341min25342;
                             ((((int)(i25341))) <= (((int)(i25341max25343))));
                             
//#line 25
i25341 += 1) {
                
//#line 25
final int i =
                  i25341;
                
//#line 25
for (
//#line 25
int j25338 =
                                   j25338min25339;
                                 ((((int)(j25338))) <= (((int)(j25338max25340))));
                                 
//#line 25
j25338 += 1) {
                    
//#line 25
final int j =
                      j25338;
                    {
                        
//#line 25
harness.
                          x10Test.chk((boolean)(/* template:equalsequals { */x10.rtt.Equality.equalsequals(ia.apply$G((int)(i),
                                                                                                                      (int)(j)),d)/* } */));
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
    							ObjectArrayInitializerShorthand.main(args);
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
new ObjectArrayInitializerShorthand().execute();
    }/* } */
    
    public ObjectArrayInitializerShorthand() {
        super();
    }

}
