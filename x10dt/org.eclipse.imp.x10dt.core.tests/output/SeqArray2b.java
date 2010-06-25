public class SeqArray2b
extends Benchmark
{public static final x10.rtt.RuntimeType<SeqArray2b>_RTT = new x10.rtt.RuntimeType<SeqArray2b>(
/* base class */SeqArray2b.class
, /* parents */ new x10.rtt.Type[] {Benchmark._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 24
final int
      N =
      2000;
    
    
//#line 25
double
                  expected(
                  ){
        
//#line 25
return ((((double)(((((double)(((((double)(1.0))) * (((double)(((double)(int)(((int)(N))))))))))) * (((double)(((double)(int)(((int)(N))))))))))) * (((double)(((double)(int)(((int)((((((int)(N))) - (((int)(1)))))))))))));
    }
    
    
//#line 26
double
                  operations(
                  ){
        
//#line 26
return ((((double)(((((double)(2.0))) * (((double)(((double)(int)(((int)(N))))))))))) * (((double)(((double)(int)(((int)(N))))))));
    }
    
    
//#line 32
final x10.
      array.
      Array<java.lang.Double>
      a;
    
    
//#line 34
double
                  once(
                  ){
        {
            
//#line 35
final x10.
              array.
              Region p3372 =
              ((x10.
              array.
              Region)(a.
                        region));
            
//#line 35
final int j3373min3374 =
              p3372.min((int)(1));
            
//#line 35
final int j3373max3375 =
              p3372.max((int)(1));
            
//#line 35
final int i3376min3377 =
              p3372.min((int)(0));
            
//#line 35
final int i3376max3378 =
              p3372.max((int)(0));
            
//#line 35
for (
//#line 35
int i3376 =
                               i3376min3377;
                             ((((int)(i3376))) <= (((int)(i3376max3378))));
                             
//#line 35
i3376 += 1) {
                
//#line 35
final int i =
                  i3376;
                
//#line 35
for (
//#line 35
int j3373 =
                                   j3373min3374;
                                 ((((int)(j3373))) <= (((int)(j3373max3375))));
                                 
//#line 35
j3373 += 1) {
                    
//#line 35
final int j =
                      j3373;
                    {
                        
//#line 36
a.set$G((double)(((double)(int)(((int)((((((int)(i))) + (((int)(j)))))))))),
                                            (int)(i),
                                            (int)(j));
                    }
                }
            }
        }
        
//#line 37
double sum =
          0.0;
        {
            
//#line 38
final x10.
              array.
              Region p3379 =
              ((x10.
              array.
              Region)(a.
                        region));
            
//#line 38
final int j3380min3381 =
              p3379.min((int)(1));
            
//#line 38
final int j3380max3382 =
              p3379.max((int)(1));
            
//#line 38
final int i3383min3384 =
              p3379.min((int)(0));
            
//#line 38
final int i3383max3385 =
              p3379.max((int)(0));
            
//#line 38
for (
//#line 38
int i3383 =
                               i3383min3384;
                             ((((int)(i3383))) <= (((int)(i3383max3385))));
                             
//#line 38
i3383 += 1) {
                
//#line 38
final int i =
                  i3383;
                
//#line 38
for (
//#line 38
int j3380 =
                                   j3380min3381;
                                 ((((int)(j3380))) <= (((int)(j3380max3382))));
                                 
//#line 38
j3380 += 1) {
                    
//#line 38
final int j =
                      j3380;
                    {
                        
//#line 39
sum += a.apply$G((int)(i),
                                                     (int)(j));
                    }
                }
            }
        }
        
//#line 40
return sum;
    }
    
    
//#line 47
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
    							SeqArray2b.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$3295)  {
        
//#line 48
new SeqArray2b().execute();
    }/* } */
    
    public SeqArray2b() {
        super();
        
//#line 32
this.a = ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Double>(x10.rtt.Types.DOUBLE,
                                  x10.
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
                                                                                      (int)(((((int)(N))) - (((int)(1)))))) })/* } */),
                                  new x10.core.fun.Fun_0_1<x10.
                                    array.
                                    Point, java.lang.Double>() {public final java.lang.Double apply$G(final x10.
                                    array.
                                    Point id$3292) { return apply(id$3292);}
                                  public final double apply(final x10.
                                    array.
                                    Point id$3292) { {
                                      
//#line 32
return 0.0;
                                  }}
                                  public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.DOUBLE;return null;
                                  }
                                  })));
    }

}
