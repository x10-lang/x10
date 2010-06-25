
public class ArrayReduce
extends TestArray
{public static final x10.rtt.RuntimeType<ArrayReduce>_RTT = new x10.rtt.RuntimeType<ArrayReduce>(
/* base class */ArrayReduce.class
, /* parents */ new x10.rtt.Type[] {TestArray._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 20
final public static int
      N =
      9;
    
    
//#line 22
public boolean
                  run(
                  ){
        
//#line 23
harness.
          x10Test.chk((boolean)(((int) x10.
                                         lang.
                                         Place.places.
                                         length) ==
                      ((int) 4)),
                      "This test must be run with 4 places");
        
//#line 25
final x10.
          array.
          Dist dist =
          ((x10.
          array.
          Dist)(x10.
          array.
          Dist.makeBlock(x10.
                           array.
                           Region.makeRectangular((int)(0),
                                                  (int)(ArrayReduce.N)))));
        
//#line 26
this.prDist("dist",
                                dist);
        
//#line 28
this.pr("--- original");
        
//#line 29
final x10.
          array.
          DistArray<java.lang.Double> a =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<java.lang.Double>make(x10.rtt.Types.DOUBLE,
                                           dist,
                                           new x10.core.fun.Fun_0_1<x10.
                                             array.
                                             Point, java.lang.Double>() {public final java.lang.Double apply$G(final x10.
                                             array.
                                             Point p) { return apply(p);}
                                           public final double apply(final x10.
                                             array.
                                             Point p) { {
                                               
//#line 29
return ((double)(int)(((int)(p.apply((int)(0))))));
                                           }}
                                           public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.DOUBLE;return null;
                                           }
                                           })));
        
//#line 30
for (
//#line 30
final x10.core.Iterator<x10.
                           array.
                           Point> pt16887 =
                           a.
                             dist.
                             region.iterator();
                         pt16887.hasNext();
                         ) {
            
//#line 30
final x10.
              array.
              Point pt =
              ((x10.
              array.
              Point)(pt16887.next$G()));
            
//#line 31
final double x =
              ((java.lang.Double)((x10.
                                     lang.
                                     Runtime.<java.lang.Double>evalFuture(x10.rtt.Types.DOUBLE,
                                                                          a.
                                                                            dist.apply(pt),
                                                                          new x10.core.fun.Fun_0_0<java.lang.Double>() {public final java.lang.Double apply$G() { return apply();}
                                                                          public final double apply() { {
                                                                              
//#line 31
return a.apply$G(pt);
                                                                          }}
                                                                          public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.DOUBLE;return null;
                                                                          }
                                                                          })).force$G()));
            
//#line 32
out.print(((x) + (" ")));
        }
        
//#line 34
out.println();
        
//#line 36
this.pr("--- reduced");
        
//#line 38
final x10.core.fun.Fun_0_2<java.lang.Double,java.lang.Double,java.lang.Double> sum =
          ((x10.core.fun.Fun_0_2)(new x10.core.fun.Fun_0_2<java.lang.Double, java.lang.Double, java.lang.Double>() {public final java.lang.Double apply$G(final java.lang.Double a,final java.lang.Double b) { return apply((double)a,(double)b);}
        public final double apply(final double a, final double b) { {
            
//#line 38
return ((((double)(a))) + (((double)(b))));
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.DOUBLE;if (i ==1) return x10.rtt.Types.DOUBLE;if (i ==2) return x10.rtt.Types.DOUBLE;return null;
        }
        }));
        
//#line 39
out.println((("sum: ") + (a.reduce$G(sum,
                                                         (double)(0.0)))));
        
//#line 41
final x10.core.fun.Fun_0_2<java.lang.Double,java.lang.Double,java.lang.Double> min =
          ((x10.core.fun.Fun_0_2)(new x10.core.fun.Fun_0_2<java.lang.Double, java.lang.Double, java.lang.Double>() {public final java.lang.Double apply$G(final java.lang.Double a,final java.lang.Double b) { return apply((double)a,(double)b);}
        public final double apply(final double a, final double b) { {
            
//#line 41
return x10.
              lang.
              Math.min((double)(a),
                       (double)(b));
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.DOUBLE;if (i ==1) return x10.rtt.Types.DOUBLE;if (i ==2) return x10.rtt.Types.DOUBLE;return null;
        }
        }));
        
//#line 42
out.println((("min: ") + (a.reduce$G(min,
                                                         (double)(java.lang.Double.POSITIVE_INFINITY)))));
        
//#line 44
final x10.core.fun.Fun_0_2<java.lang.Double,java.lang.Double,java.lang.Double> max =
          ((x10.core.fun.Fun_0_2)(new x10.core.fun.Fun_0_2<java.lang.Double, java.lang.Double, java.lang.Double>() {public final java.lang.Double apply$G(final java.lang.Double a,final java.lang.Double b) { return apply((double)a,(double)b);}
        public final double apply(final double a, final double b) { {
            
//#line 44
return x10.
              lang.
              Math.max((double)(a),
                       (double)(b));
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.DOUBLE;if (i ==1) return x10.rtt.Types.DOUBLE;if (i ==2) return x10.rtt.Types.DOUBLE;return null;
        }
        }));
        
//#line 45
out.println((("max: ") + (a.reduce$G(max,
                                                         (double)(java.lang.Double.NEGATIVE_INFINITY)))));
        
//#line 47
return this.status();
    }
    
    
//#line 50
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
    							ArrayReduce.main(args);
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
        
//#line 51
new ArrayReduce().execute();
    }/* } */
    
    
//#line 54
java.lang.String
                  expected(
                  ){
        
//#line 55
return (((((((((((((("--- dist: Dist(0->[0..2],1->[3..5],2->[6..7],3->[8..9])\n") + ("0 0 0 1 1 1 2 2 3 3 \n"))) + ("--- original\n"))) + ("0.0 1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0 9.0 \n"))) + ("--- reduced\n"))) + ("sum: 45.0\n"))) + ("min: 0.0\n"))) + ("max: 9.0\n"));
    }
    
    public ArrayReduce() {
        super();
    }

}
