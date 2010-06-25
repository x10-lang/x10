
class SeqUTSGeo1
extends Benchmark
{public static final x10.rtt.RuntimeType<SeqUTSGeo1>_RTT = new x10.rtt.RuntimeType<SeqUTSGeo1>(
/* base class */SeqUTSGeo1.class
, /* parents */ new x10.rtt.Type[] {Benchmark._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 45
final static int
      r0 =
      0;
    
//#line 47
final static int
      b0 =
      4;
    
//#line 48
final static int
      d =
      10;
    
    
//#line 49
double
                  expected(
                  ){
        
//#line 49
return 906930.0;
    }
    
    
//#line 51
double
                  operations(
                  ){
        
//#line 51
return ((double)(int)(((int)(size))));
    }
    
    
//#line 59
int
      size;
    
//#line 60
int
      sumb;
    
    
//#line 62
void
                  visit(
                  final long r,
                  final int depth){
        
//#line 63
final double x =
          UTSRand.number((long)(r));
        
//#line 64
final double q =
          ((((double)(((double)(int)(((int)(SeqUTSGeo1.b0))))))) / (((double)((((((double)(1.0))) + (((double)(((double)(int)(((int)(SeqUTSGeo1.b0)))))))))))));
        
//#line 65
final int b =
          ((int)(double)(((double)((((((double)(java.lang.Math.log(((double)(x)))))) / (((double)(java.lang.Math.log(((double)(q))))))))))));
        
//#line 66
this.size += 1;
        
//#line 67
this.sumb += b;
        
//#line 68
if (((((int)(depth))) < (((int)(SeqUTSGeo1.d))))) {
            
//#line 69
for (
//#line 69
int i =
                               0;
                             ((((int)(i))) < (((int)(b))));
                             
//#line 69
i += 1) {
                
//#line 70
this.visit((long)(UTSRand.next((long)(r),
                                                           (int)(i))),
                                       (int)(((((int)(depth))) + (((int)(1))))));
            }
        }
    }
    
    
//#line 77
boolean
      first;
    
    
//#line 79
double
                  once(
                  ){
        
//#line 82
this.size = 0;
        
//#line 83
this.sumb = 0;
        
//#line 84
this.visit((long)(((long)(((int)(SeqUTSGeo1.r0))))),
                               (int)(0));
        
//#line 87
if (first) {
            
//#line 88
final double obsBranch =
              ((((double)((((double)(int)(((int)(sumb)))))))) / (((double)(((double)(int)(((int)(size))))))));
            
//#line 89
final double balancedSize =
              ((((double)((((((double)(java.lang.Math.pow(((double)(((double)(int)(((int)(SeqUTSGeo1.b0)))))), ((double)(((double)(int)(((int)(((((int)(SeqUTSGeo1.d))) + (((int)(1)))))))))))))) - (((double)(((double)(int)(((int)(1)))))))))))) / (((double)((((((double)(obsBranch))) - (((double)(((double)(int)(((int)(1)))))))))))));
            
//#line 90
x10.
              io.
              Console.OUT.printf("balancedSize / size %.2f\n",
                                 (double)(((((double)(balancedSize))) / (((double)(((double)(int)(((int)(size))))))))));
            
//#line 91
x10.
              io.
              Console.OUT.printf("obsBranch / b0 %.2f\n",
                                 (double)(((((double)(obsBranch))) / (((double)(((double)(int)(((int)(SeqUTSGeo1.b0))))))))));
        }
        
//#line 93
this.first = false;
        
//#line 96
return ((double)(int)(((int)(size))));
    }
    
    
//#line 104
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
    							SeqUTSGeo1.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$5249)  {
        
//#line 105
new SeqUTSGeo1().execute();
    }/* } */
    
    public SeqUTSGeo1() {
        super();
        
//#line 59
this.size = 0;
        
//#line 60
this.sumb = 0;
        
//#line 77
this.first = true;
    }

}
