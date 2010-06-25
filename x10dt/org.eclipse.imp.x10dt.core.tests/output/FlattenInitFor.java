
public class FlattenInitFor
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<FlattenInitFor>_RTT = new x10.rtt.RuntimeType<FlattenInitFor>(
/* base class */FlattenInitFor.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 21
final x10.
      array.
      DistArray<java.lang.Integer>
      a;
    
    
//#line 23
public FlattenInitFor() {
        
//#line 23
super();
        
//#line 24
this.a = ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<java.lang.Integer>make(x10.rtt.Types.INT,
                                            x10.
                                              array.
                                              Dist.makeConstant((x10.
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
                                                                                                                     (int)(10)) })/* } */)),
                                                                x10.
                                                                  lang.
                                                                  Runtime.here()),
                                            new x10.core.fun.Fun_0_1<x10.
                                              array.
                                              Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                              array.
                                              Point id23025) { return apply(id23025);}
                                            public final int apply(final x10.
                                              array.
                                              Point id23025) { {
                                                
//#line 24
final int i =
                                                  id23025.apply((int)(0));
                                                
//#line 24
final int j =
                                                  id23025.apply((int)(1));
                                                
//#line 24
return i;
                                            }}
                                            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                            }
                                            })));
    }
    
    
//#line 27
public boolean
                  run(
                  ){
        
//#line 28
for (
//#line 28
int e =
                           ((java.lang.Integer)((x10.
                                                   lang.
                                                   Runtime.<java.lang.Integer>evalFuture(x10.rtt.Types.INT,
                                                                                         a.
                                                                                           dist.apply((int)(1),
                                                                                                      (int)(1)),
                                                                                         new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                                                                         public final int apply() { {
                                                                                             
//#line 28
return a.apply$G((int)(1),
                                                                                                                          (int)(1));
                                                                                         }}
                                                                                         public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                                                                         }
                                                                                         })).force$G()));
                         ((((int)(e))) < (((int)(3))));
                         
//#line 28
e += 1) {
            
//#line 29
x10.
              io.
              Console.OUT.println("done.");
        }
        
//#line 30
return true;
    }
    
    
//#line 33
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
    							FlattenInitFor.main(args);
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
        
//#line 34
new FlattenInitFor().execute();
    }/* } */

}
