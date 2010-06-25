public class SeqMatMultAdd1a
extends Benchmark
{public static final x10.rtt.RuntimeType<SeqMatMultAdd1a>_RTT = new x10.rtt.RuntimeType<SeqMatMultAdd1a>(
/* base class */SeqMatMultAdd1a.class
, /* parents */ new x10.rtt.Type[] {Benchmark._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 18
final int
      N =
      ((((int)(55))) * (((int)(5))));
    
    
//#line 19
double
                  expected(
                  ){
        
//#line 19
return (-(((double)(6866925.0))));
    }
    
    
//#line 20
double
                  operations(
                  ){
        
//#line 20
return ((double)(int)(((int)(((((int)(((((int)(N))) * (((int)(N))))))) * (((int)(N))))))));
    }
    
    
//#line 26
final x10.
      array.
      Region
      r;
    
//#line 27
final x10.
      array.
      Array<java.lang.Double>
      a;
    
//#line 28
final x10.
      array.
      Array<java.lang.Double>
      b;
    
//#line 29
final x10.
      array.
      Array<java.lang.Double>
      c;
    
    
//#line 31
double
                  once(
                  ){
        
//#line 32
for (
//#line 32
int i =
                           0;
                         ((((int)(i))) < (((int)(N))));
                         
//#line 32
i += 1) {
            
//#line 33
for (
//#line 33
int j =
                               0;
                             ((((int)(j))) < (((int)(N))));
                             
//#line 33
j += 1) {
                
//#line 34
for (
//#line 34
int k =
                                   0;
                                 ((((int)(k))) < (((int)(N))));
                                 
//#line 34
k += 1) {
                    
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
                                                                                  (int)(y1))))) + (((double)(z))))),
                                                   (int)(y0),
                                                   (int)(y1));
                    }}
                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.DOUBLE);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.DOUBLE;if (i ==4) return x10.rtt.Types.DOUBLE;return null;
                    }
                    }.apply(a,
                            i,
                            j,
                            ((((double)(b.apply$G((int)(i),
                                                  (int)(k))))) * (((double)(c.apply$G((int)(k),
                                                                                      (int)(j)))))));
                }
            }
        }
        
//#line 36
return a.apply$G((int)(10),
                                     (int)(10));
    }
    
    
//#line 44
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
    							SeqMatMultAdd1a.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$3503)  {
        
//#line 45
new SeqMatMultAdd1a().execute();
    }/* } */
    
    public SeqMatMultAdd1a() {
        super();
        
//#line 26
this.r = ((x10.
          array.
          Region)(x10.
          array.
          Region.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                     array.
                                     Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                                     array.
                                     Region[] { x10.
                                     array.
                                     Region.makeRectangular((int)(0),
                                                            (int)(((((int)(N))) - (((int)(1)))))),x10.
                                     array.
                                     Region.makeRectangular((int)(0),
                                                            (int)(((((int)(N))) - (((int)(1)))))) })/* } */)));
        
//#line 27
this.a = ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Double>(x10.rtt.Types.DOUBLE,
                                  r,
                                  new x10.core.fun.Fun_0_1<x10.
                                    array.
                                    Point, java.lang.Double>() {public final java.lang.Double apply$G(final x10.
                                    array.
                                    Point p) { return apply(p);}
                                  public final double apply(final x10.
                                    array.
                                    Point p) { {
                                      
//#line 27
return ((double)(int)(((int)(((((int)(p.apply((int)(0))))) * (((int)(p.apply((int)(1))))))))));
                                  }}
                                  public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.DOUBLE;return null;
                                  }
                                  })));
        
//#line 28
this.b = ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Double>(x10.rtt.Types.DOUBLE,
                                  r,
                                  new x10.core.fun.Fun_0_1<x10.
                                    array.
                                    Point, java.lang.Double>() {public final java.lang.Double apply$G(final x10.
                                    array.
                                    Point p) { return apply(p);}
                                  public final double apply(final x10.
                                    array.
                                    Point p) { {
                                      
//#line 28
return ((double)(int)(((int)(((((int)(p.apply((int)(0))))) - (((int)(p.apply((int)(1))))))))));
                                  }}
                                  public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.DOUBLE;return null;
                                  }
                                  })));
        
//#line 29
this.c = ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Double>(x10.rtt.Types.DOUBLE,
                                  r,
                                  new x10.core.fun.Fun_0_1<x10.
                                    array.
                                    Point, java.lang.Double>() {public final java.lang.Double apply$G(final x10.
                                    array.
                                    Point p) { return apply(p);}
                                  public final double apply(final x10.
                                    array.
                                    Point p) { {
                                      
//#line 29
return ((double)(int)(((int)(((((int)(p.apply((int)(0))))) + (((int)(p.apply((int)(1))))))))));
                                  }}
                                  public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.DOUBLE;return null;
                                  }
                                  })));
    }

}
