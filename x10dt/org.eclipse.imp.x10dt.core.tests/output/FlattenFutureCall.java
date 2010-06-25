
public class FlattenFutureCall
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<FlattenFutureCall>_RTT = new x10.rtt.RuntimeType<FlattenFutureCall>(
/* base class */FlattenFutureCall.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 26
final x10.
      array.
      DistArray<java.lang.Integer>
      a;
    
    
//#line 28
public FlattenFutureCall() {
        
//#line 28
super();
        
//#line 29
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
                                              Point id22855) { return apply(id22855);}
                                            public final int apply(final x10.
                                              array.
                                              Point id22855) { {
                                                
//#line 29
final int i =
                                                  id22855.apply((int)(0));
                                                
//#line 29
final int j =
                                                  id22855.apply((int)(1));
                                                
//#line 29
return ((((int)(i))) + (((int)(j))));
                                            }}
                                            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                            }
                                            })));
    }
    
    
//#line 32
public boolean
                  run(
                  ){
        
//#line 33
boolean x =
          ((java.lang.Boolean)((x10.
                                  lang.
                                  Runtime.<java.lang.Boolean>evalFuture(x10.rtt.Types.BOOLEAN,
                                                                        a.
                                                                          dist.apply((int)(1),
                                                                                     (int)(1)),
                                                                        new x10.core.fun.Fun_0_0<java.lang.Boolean>() {public final java.lang.Boolean apply$G() { return apply();}
                                                                        public final boolean apply() { {
                                                                            
//#line 33
return true;
                                                                        }}
                                                                        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.BOOLEAN;return null;
                                                                        }
                                                                        })).force$G()));
        
//#line 34
return x;
    }
    
    
//#line 37
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
    							FlattenFutureCall.main(args);
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
        
//#line 38
new FlattenFutureCall().execute();
    }/* } */

}
