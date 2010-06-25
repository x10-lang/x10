public class SeqArray2a
extends Benchmark
{public static final x10.rtt.RuntimeType<SeqArray2a>_RTT = new x10.rtt.RuntimeType<SeqArray2a>(
/* base class */SeqArray2a.class
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
        
//#line 35
for (
//#line 35
int i =
                           0;
                         ((((int)(i))) < (((int)(N))));
                         
//#line 35
i += 1) {
            
//#line 36
for (
//#line 36
int j =
                               0;
                             ((((int)(j))) < (((int)(N))));
                             
//#line 36
j += 1) {
                
//#line 37
a.set$G((double)(((double)(int)(((int)((((((int)(i))) + (((int)(j)))))))))),
                                    (int)(i),
                                    (int)(j));
            }
        }
        
//#line 38
double sum =
          0.0;
        
//#line 39
for (
//#line 39
int i =
                           0;
                         ((((int)(i))) < (((int)(N))));
                         
//#line 39
i += 1) {
            
//#line 40
for (
//#line 40
int j =
                               0;
                             ((((int)(j))) < (((int)(N))));
                             
//#line 40
j += 1) {
                
//#line 41
sum += a.apply$G((int)(i),
                                             (int)(j));
            }
        }
        
//#line 42
return sum;
    }
    
    
//#line 49
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
    							SeqArray2a.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$3081)  {
        
//#line 50
new SeqArray2a().execute();
    }/* } */
    
    public SeqArray2a() {
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
                                    Point id$3078) { return apply(id$3078);}
                                  public final double apply(final x10.
                                    array.
                                    Point id$3078) { {
                                      
//#line 32
return 0.0;
                                  }}
                                  public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.DOUBLE;return null;
                                  }
                                  })));
    }

}
