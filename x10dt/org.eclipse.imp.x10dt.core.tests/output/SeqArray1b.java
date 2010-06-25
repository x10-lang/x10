public class SeqArray1b
extends Benchmark
{public static final x10.rtt.RuntimeType<SeqArray1b>_RTT = new x10.rtt.RuntimeType<SeqArray1b>(
/* base class */SeqArray1b.class
, /* parents */ new x10.rtt.Type[] {Benchmark._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 23
final int
      N =
      1000000;
    
//#line 24
final int
      M =
      20;
    
    
//#line 25
double
                  expected(
                  ){
        
//#line 25
return ((double)(int)(((int)(((((int)(N))) * (((int)(M))))))));
    }
    
    
//#line 26
double
                  operations(
                  ){
        
//#line 26
return ((double)(int)(((int)(((((int)(N))) * (((int)(M))))))));
    }
    
    
//#line 33
final x10.
      array.
      Array<java.lang.Integer>
      a;
    
    
//#line 35
double
                  once(
                  ){
        
//#line 36
int sum =
          0;
        
//#line 37
for (
//#line 37
int k =
                           0;
                         ((((int)(k))) < (((int)(M))));
                         
//#line 37
k += 1) {
            
//#line 38
for (
//#line 38
int i =
                               0;
                             ((((int)(i))) < (((int)(N))));
                             
//#line 38
i += 1) {
                
//#line 39
sum += a.apply$G((int)(((((int)(i))) + (((int)(k))))));
            }
        }
        
//#line 40
return ((double)(int)(((int)(sum))));
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
    							SeqArray1b.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$2903)  {
        
//#line 48
new SeqArray1b().execute();
    }/* } */
    
    public SeqArray1b() {
        super();
        
//#line 33
this.a = ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Integer>(x10.rtt.Types.INT,
                                   x10.
                                     array.
                                     Region.makeRectangular((int)(0),
                                                            (int)(((((int)(((((int)(N))) + (((int)(M))))))) - (((int)(1)))))),
                                   new x10.core.fun.Fun_0_1<x10.
                                     array.
                                     Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                     array.
                                     Point id$2902) { return apply(id$2902);}
                                   public final int apply(final x10.
                                     array.
                                     Point id$2902) { {
                                       
//#line 33
return 1;
                                   }}
                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                   }
                                   })));
    }

}
