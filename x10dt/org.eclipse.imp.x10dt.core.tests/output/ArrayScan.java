
public class ArrayScan
extends TestArray
{public static final x10.rtt.RuntimeType<ArrayScan>_RTT = new x10.rtt.RuntimeType<ArrayScan>(
/* base class */ArrayScan.class
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
        
//#line 24
final x10.
          array.
          Array<java.lang.Double> a =
          ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Double>(x10.rtt.Types.DOUBLE,
                                  x10.
                                    array.
                                    Region.makeRectangular((int)(0),
                                                           (int)(ArrayScan.N)),
                                  new x10.core.fun.Fun_0_1<x10.
                                    array.
                                    Point, java.lang.Double>() {public final java.lang.Double apply$G(final x10.
                                    array.
                                    Point p) { return apply(p);}
                                  public final double apply(final x10.
                                    array.
                                    Point p) { {
                                      
//#line 24
return ((double)(int)(((int)(p.apply((int)(0))))));
                                  }}
                                  public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.DOUBLE;return null;
                                  }
                                  })));
        
//#line 25
this.pr("original",
                            a);
        
//#line 27
final x10.core.fun.Fun_0_2<java.lang.Double,java.lang.Double,java.lang.Double> sum =
          ((x10.core.fun.Fun_0_2)(new x10.core.fun.Fun_0_2<java.lang.Double, java.lang.Double, java.lang.Double>() {public final java.lang.Double apply$G(final java.lang.Double a,final java.lang.Double b) { return apply((double)a,(double)b);}
        public final double apply(final double a, final double b) { {
            
//#line 27
return ((((double)(a))) + (((double)(b))));
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.DOUBLE;if (i ==1) return x10.rtt.Types.DOUBLE;if (i ==2) return x10.rtt.Types.DOUBLE;return null;
        }
        }));
        
//#line 28
this.pr("scan sum",
                            a.scan(sum,
                                   (double)(0.0)));
        
//#line 30
final x10.core.fun.Fun_0_2<java.lang.Double,java.lang.Double,java.lang.Double> min =
          ((x10.core.fun.Fun_0_2)(new x10.core.fun.Fun_0_2<java.lang.Double, java.lang.Double, java.lang.Double>() {public final java.lang.Double apply$G(final java.lang.Double a,final java.lang.Double b) { return apply((double)a,(double)b);}
        public final double apply(final double a, final double b) { {
            
//#line 30
return x10.
              lang.
              Math.min((double)(a),
                       (double)(b));
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.DOUBLE;if (i ==1) return x10.rtt.Types.DOUBLE;if (i ==2) return x10.rtt.Types.DOUBLE;return null;
        }
        }));
        
//#line 31
this.pr("scan min",
                            a.scan(min,
                                   (double)(java.lang.Double.POSITIVE_INFINITY)));
        
//#line 33
final x10.core.fun.Fun_0_2<java.lang.Double,java.lang.Double,java.lang.Double> max =
          ((x10.core.fun.Fun_0_2)(new x10.core.fun.Fun_0_2<java.lang.Double, java.lang.Double, java.lang.Double>() {public final java.lang.Double apply$G(final java.lang.Double a,final java.lang.Double b) { return apply((double)a,(double)b);}
        public final double apply(final double a, final double b) { {
            
//#line 33
return x10.
              lang.
              Math.max((double)(a),
                       (double)(b));
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.DOUBLE;if (i ==1) return x10.rtt.Types.DOUBLE;if (i ==2) return x10.rtt.Types.DOUBLE;return null;
        }
        }));
        
//#line 34
this.pr("scan max",
                            a.scan(max,
                                   (double)(java.lang.Double.NEGATIVE_INFINITY)));
        
//#line 36
return this.status();
    }
    
    
//#line 39
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
    							ArrayScan.main(args);
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
        
//#line 40
new ArrayScan().execute();
    }/* } */
    
    
//#line 43
void
                  pr(
                  final java.lang.String msg,
                  final x10.
                    array.
                    Array<java.lang.Double> a){
        
//#line 44
final x10.
          array.
          Array<java.lang.Double> aa =
          ((x10.
          array.
          Array)(new x10.core.fun.Fun_0_1<x10.
          array.
          Array<java.lang.Double>, x10.
          array.
          Array<java.lang.Double>>() {public final x10.
          array.
          Array<java.lang.Double> apply$G(final x10.
          array.
          Array<java.lang.Double> __desugarer__var__214__) { return apply(__desugarer__var__214__);}
        public final x10.
          array.
          Array<java.lang.Double> apply(final x10.
          array.
          Array<java.lang.Double> __desugarer__var__214__) { {
            
//#line 44
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__214__,null))/* } */ &&
                              !x10.core.Ref.at(__desugarer__var__214__, x10.
                              lang.
                              Runtime.here().id)) {
                
//#line 44
throw new java.lang.ClassCastException(("x10.array.Array[x10.lang.Double]{self.home==here, self.regio" +
                                                                    "n.rank==1}"));
            }
            
//#line 44
return __desugarer__var__214__;
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.DOUBLE);if (i ==1) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.DOUBLE);return null;
        }
        }.apply(((x10.
                  array.
                  Array)
                  a))));
        
//#line 45
out.println((("--- ") + (msg)));
        
//#line 46
for (
//#line 46
final x10.core.Iterator<x10.
                           array.
                           Point> pt17335 =
                           aa.
                             region.iterator();
                         pt17335.hasNext();
                         ) {
            
//#line 46
final x10.
              array.
              Point pt =
              ((x10.
              array.
              Point)(pt17335.next$G()));
            
//#line 47
out.print(((aa.apply$G(pt)) + (" ")));
        }
        
//#line 48
out.println();
    }
    
    
//#line 51
java.lang.String
                  expected(
                  ){
        
//#line 52
return (((((((((((((("--- original\n") + ("0.0 1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0 9.0 \n"))) + ("--- scan sum\n"))) + ("0.0 1.0 3.0 6.0 10.0 15.0 21.0 28.0 36.0 45.0 \n"))) + ("--- scan min\n"))) + ("0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 \n"))) + ("--- scan max\n"))) + ("0.0 1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0 9.0 \n"));
    }
    
    public ArrayScan() {
        super();
    }

}
