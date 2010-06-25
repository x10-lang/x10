public class ArrayLift
extends TestArray
{public static final x10.rtt.RuntimeType<ArrayLift>_RTT = new x10.rtt.RuntimeType<ArrayLift>(
/* base class */ArrayLift.class
, /* parents */ new x10.rtt.Type[] {TestArray._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 18
final public static int
      N =
      9;
    
    
//#line 20
public boolean
                  run(
                  ){
        
//#line 21
harness.
          x10Test.chk((boolean)(((int) x10.
                                         lang.
                                         Place.places.
                                         length) ==
                      ((int) 4)),
                      "This test must be run with 4 places");
        
//#line 23
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
                                                  (int)(ArrayLift.N)))));
        
//#line 24
this.prDist("dist",
                                dist);
        
//#line 26
this.pr("--- original");
        
//#line 27
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
                                               
//#line 27
return ((double)(int)(((int)(p.apply((int)(0))))));
                                           }}
                                           public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.DOUBLE;return null;
                                           }
                                           })));
        
//#line 28
for (
//#line 28
final x10.core.Iterator<x10.
                           array.
                           Point> pt15428 =
                           a.
                             dist.
                             region.iterator();
                         pt15428.hasNext();
                         ) {
            
//#line 28
final x10.
              array.
              Point pt =
              ((x10.
              array.
              Point)(pt15428.next$G()));
            
//#line 29
final double x =
              ((java.lang.Double)((x10.
                                     lang.
                                     Runtime.<java.lang.Double>evalFuture(x10.rtt.Types.DOUBLE,
                                                                          a.
                                                                            dist.apply(pt),
                                                                          new x10.core.fun.Fun_0_0<java.lang.Double>() {public final java.lang.Double apply$G() { return apply();}
                                                                          public final double apply() { {
                                                                              
//#line 29
return a.apply$G(pt);
                                                                          }}
                                                                          public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.DOUBLE;return null;
                                                                          }
                                                                          })).force$G()));
            
//#line 30
out.print(((x) + (" ")));
        }
        
//#line 32
out.println();
        
//#line 34
this.pr("--- lifted");
        
//#line 35
final x10.
          array.
          DistArray<java.lang.Double> b =
          ((x10.
          array.
          DistArray)(new x10.core.fun.Fun_0_1<x10.
          array.
          DistArray<java.lang.Double>, x10.
          array.
          DistArray<java.lang.Double>>() {public final x10.
          array.
          DistArray<java.lang.Double> apply$G(final x10.
          array.
          DistArray<java.lang.Double> __desugarer__var__212__) { return apply(__desugarer__var__212__);}
        public final x10.
          array.
          DistArray<java.lang.Double> apply(final x10.
          array.
          DistArray<java.lang.Double> __desugarer__var__212__) { {
            
//#line 35
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__212__,null))/* } */ &&
                              !(/* template:equalsequals { */x10.rtt.Equality.equalsequals(((x10.
                                  array.
                                  Dist)(__desugarer__var__212__.
                                          dist)),dist)/* } */)) {
                
//#line 35
throw new java.lang.ClassCastException("x10.array.DistArray[x10.lang.Double]{self.dist==dist}");
            }
            
//#line 35
return __desugarer__var__212__;
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.DOUBLE);if (i ==1) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.DOUBLE);return null;
        }
        }.apply(((x10.
                  array.
                  DistArray)
                  a.lift(new x10.core.fun.Fun_0_1<java.lang.Double, java.lang.Double>() {public final java.lang.Double apply$G(final java.lang.Double a) { return apply((double)a);}
                         public final double apply(final double a) { {
                             
//#line 35
return ((((double)(1.5))) * (((double)(a))));
                         }}
                         public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.DOUBLE;if (i ==1) return x10.rtt.Types.DOUBLE;return null;
                         }
                         })))));
        
//#line 36
for (
//#line 36
final x10.core.Iterator<x10.
                           array.
                           Point> pt15429 =
                           b.
                             dist.
                             region.iterator();
                         pt15429.hasNext();
                         ) {
            
//#line 36
final x10.
              array.
              Point pt =
              ((x10.
              array.
              Point)(pt15429.next$G()));
            
//#line 37
final double x =
              ((java.lang.Double)((x10.
                                     lang.
                                     Runtime.<java.lang.Double>evalFuture(x10.rtt.Types.DOUBLE,
                                                                          b.
                                                                            dist.apply(pt),
                                                                          new x10.core.fun.Fun_0_0<java.lang.Double>() {public final java.lang.Double apply$G() { return apply();}
                                                                          public final double apply() { {
                                                                              
//#line 37
return b.apply$G(pt);
                                                                          }}
                                                                          public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.DOUBLE;return null;
                                                                          }
                                                                          })).force$G()));
            
//#line 38
out.print(((x) + (" ")));
        }
        
//#line 40
out.println();
        
//#line 42
return this.status();
    }
    
    
//#line 45
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
    							ArrayLift.main(args);
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
        
//#line 46
new ArrayLift().execute();
    }/* } */
    
    
//#line 49
java.lang.String
                  expected(
                  ){
        
//#line 50
return (((((((((("--- dist: Dist(0->[0..2],1->[3..5],2->[6..7],3->[8..9])\n") + ("0 0 0 1 1 1 2 2 3 3 \n"))) + ("--- original\n"))) + ("0.0 1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0 9.0 \n"))) + ("--- lifted\n"))) + ("0.0 1.5 3.0 4.5 6.0 7.5 9.0 10.5 12.0 13.5 \n"));
    }
    
    public ArrayLift() {
        super();
    }

}
