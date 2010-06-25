
public class ArrayOpAssign
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ArrayOpAssign>_RTT = new x10.rtt.RuntimeType<ArrayOpAssign>(
/* base class */ArrayOpAssign.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 20
public boolean
                  run(
                  ){
        
//#line 21
boolean result =
          true;
        
//#line 22
final x10.
          array.
          Region R =
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
x10.
          array.
          Array<java.lang.Integer> ia =
          ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Integer>(x10.rtt.Types.INT,
                                   R,
                                   new x10.core.fun.Fun_0_1<x10.
                                     array.
                                     Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                     array.
                                     Point id$15784) { return apply(id$15784);}
                                   public final int apply(final x10.
                                     array.
                                     Point id$15784) { {
                                       
//#line 23
return 0;
                                   }}
                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                   }
                                   })));
        
//#line 24
ia.set$G((int)(1),
                             (int)(1),
                             (int)(1));
        
//#line 25
new x10.core.fun.Fun_0_4<x10.
          array.
          Array<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
          array.
          Array<java.lang.Integer> x,final java.lang.Integer y0,final java.lang.Integer y1,final java.lang.Integer z) { return apply(x,(int)y0,(int)y1,(int)z);}
        public final int apply(final x10.
          array.
          Array<java.lang.Integer> x, final int y0, final int y1, final int z) { {
            
//#line 25
return x.set$G((int)(((((int)(x.apply$G((int)(y0),
                                                                (int)(y1))))) + (((int)(z))))),
                                       (int)(y0),
                                       (int)(y1));
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.INT);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;if (i ==4) return x10.rtt.Types.INT;return null;
        }
        }.apply(ia,
                1,
                1,
                ia.apply$G((int)(1),
                           (int)(1)));
        
//#line 26
result &= (((int) 2) ==
                               ((int) ia.apply$G((int)(1),
                                                 (int)(1))));
        
//#line 27
x10.
          io.
          Console.OUT.println((("ia[1,1])") + (ia.apply$G((int)(1),
                                                          (int)(1)))));
        
//#line 28
new x10.core.fun.Fun_0_4<x10.
          array.
          Array<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
          array.
          Array<java.lang.Integer> x,final java.lang.Integer y0,final java.lang.Integer y1,final java.lang.Integer z) { return apply(x,(int)y0,(int)y1,(int)z);}
        public final int apply(final x10.
          array.
          Array<java.lang.Integer> x, final int y0, final int y1, final int z) { {
            
//#line 28
return x.set$G((int)(((((int)(x.apply$G((int)(y0),
                                                                (int)(y1))))) * (((int)(z))))),
                                       (int)(y0),
                                       (int)(y1));
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.INT);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;if (i ==4) return x10.rtt.Types.INT;return null;
        }
        }.apply(ia,
                1,
                1,
                2);
        
//#line 29
x10.
          io.
          Console.OUT.println((("ia[1,1])") + (ia.apply$G((int)(1),
                                                          (int)(1)))));
        
//#line 30
result &= (((int) 4) ==
                               ((int) ia.apply$G((int)(1),
                                                 (int)(1))));
        
//#line 31
x10.
          array.
          Array<java.lang.Double> id =
          ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Double>(x10.rtt.Types.DOUBLE,
                                  R,
                                  new x10.core.fun.Fun_0_1<x10.
                                    array.
                                    Point, java.lang.Double>() {public final java.lang.Double apply$G(final x10.
                                    array.
                                    Point id$15787) { return apply(id$15787);}
                                  public final double apply(final x10.
                                    array.
                                    Point id$15787) { {
                                      
//#line 31
return 0.0;
                                  }}
                                  public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.DOUBLE;return null;
                                  }
                                  })));
        
//#line 32
new x10.core.fun.Fun_0_4<x10.
          array.
          Array<java.lang.Double>, java.lang.Integer, java.lang.Integer, java.lang.Double, java.lang.Double>() {public final java.lang.Double apply$G(final x10.
          array.
          Array<java.lang.Double> x,final java.lang.Integer y0,final java.lang.Integer y1,final java.lang.Double z) { return apply(x,(int)y0,(int)y1,(double)z);}
        public final double apply(final x10.
          array.
          Array<java.lang.Double> x, final int y0, final int y1, final double z) { {
            
//#line 32
return x.set$G((double)(((((double)(x.apply$G((int)(y0),
                                                                      (int)(y1))))) + (((double)(z))))),
                                       (int)(y0),
                                       (int)(y1));
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.DOUBLE);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.DOUBLE;if (i ==4) return x10.rtt.Types.DOUBLE;return null;
        }
        }.apply(id,
                1,
                1,
                ((double)(int)(((int)(42)))));
        
//#line 33
result &= (((double) ((double)(int)(((int)(42))))) ==
                               ((double) id.apply$G((int)(1),
                                                    (int)(1))));
        
//#line 34
x10.
          io.
          Console.OUT.println((("id[1,1])") + (id.apply$G((int)(1),
                                                          (int)(1)))));
        
//#line 35
new x10.core.fun.Fun_0_4<x10.
          array.
          Array<java.lang.Double>, java.lang.Integer, java.lang.Integer, java.lang.Double, java.lang.Double>() {public final java.lang.Double apply$G(final x10.
          array.
          Array<java.lang.Double> x,final java.lang.Integer y0,final java.lang.Integer y1,final java.lang.Double z) { return apply(x,(int)y0,(int)y1,(double)z);}
        public final double apply(final x10.
          array.
          Array<java.lang.Double> x, final int y0, final int y1, final double z) { {
            
//#line 35
return x.set$G((double)(((((double)(x.apply$G((int)(y0),
                                                                      (int)(y1))))) * (((double)(z))))),
                                       (int)(y0),
                                       (int)(y1));
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.DOUBLE);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.DOUBLE;if (i ==4) return x10.rtt.Types.DOUBLE;return null;
        }
        }.apply(id,
                1,
                1,
                ((double)(int)(((int)(2)))));
        
//#line 36
x10.
          io.
          Console.OUT.println((("id[1,1])") + (id.apply$G((int)(1),
                                                          (int)(1)))));
        
//#line 37
result &= (((double) ((double)(int)(((int)(84))))) ==
                               ((double) id.apply$G((int)(1),
                                                    (int)(1))));
        
//#line 38
return result;
    }
    
    
//#line 41
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
    							ArrayOpAssign.main(args);
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
        
//#line 42
new ArrayOpAssign().execute();
    }/* } */
    
    public ArrayOpAssign() {
        super();
    }

}
