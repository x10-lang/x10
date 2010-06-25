
class SeqUTSBin1
extends Benchmark
{public static final x10.rtt.RuntimeType<SeqUTSBin1>_RTT = new x10.rtt.RuntimeType<SeqUTSBin1>(
/* base class */SeqUTSBin1.class
, /* parents */ new x10.rtt.Type[] {Benchmark._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 43
final static int
      r0 =
      0;
    
//#line 45
final static int
      b0 =
      50000;
    
//#line 46
final static double
      q =
      0.12;
    
//#line 47
final static int
      m =
      8;
    
    
//#line 48
double
                  expected(
                  ){
        
//#line 48
return 1234872.0;
    }
    
    
//#line 50
double
                  operations(
                  ){
        
//#line 50
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
                  final long r){
        
//#line 63
final double x =
          UTSRand.number((long)(r));
        
//#line 64
final int b =
          ((((double)(x))) < (((double)(SeqUTSBin1.q))))
          ? SeqUTSBin1.m
          : 0;
        
//#line 65
this.sumb += b;
        
//#line 66
this.size += 1;
        
//#line 67
for (
//#line 67
int i =
                           0;
                         ((((int)(i))) < (((int)(b))));
                         
//#line 67
i += 1) {
            
//#line 68
this.visit((long)(UTSRand.next((long)(r),
                                                       (int)(i))));
        }
    }
    
    
//#line 75
boolean
      first;
    
    
//#line 77
double
                  once(
                  ){
        
//#line 80
this.size = 0;
        
//#line 81
this.sumb = 0;
        
//#line 82
for (
//#line 82
int i =
                           0;
                         ((((int)(i))) < (((int)(SeqUTSBin1.b0))));
                         
//#line 82
i += 1) {
            
//#line 83
this.visit((long)(UTSRand.next((long)(((long)(((int)(SeqUTSBin1.r0))))),
                                                       (int)(i))));
        }
        
//#line 86
if (first) {
            
//#line 87
final double expSize =
              ((((double)(((double)(int)(((int)(SeqUTSBin1.b0))))))) / (((double)((((((double)(1.0))) - (((double)(((((double)(SeqUTSBin1.q))) * (((double)(((double)(int)(((int)(SeqUTSBin1.m)))))))))))))))));
            
//#line 88
final double obsBranch =
              ((((double)((((double)(int)(((int)(sumb)))))))) / (((double)(((double)(int)(((int)(size))))))));
            
//#line 89
final double expBranch =
              ((((double)(SeqUTSBin1.q))) * (((double)(((double)(int)(((int)(SeqUTSBin1.m))))))));
            
//#line 90
x10.
              io.
              Console.OUT.printf("exp size / obs size: %.3f\n",
                                 (double)(((((double)(expSize))) / (((double)(((double)(int)(((int)(size))))))))));
            
//#line 91
x10.
              io.
              Console.OUT.printf("exp branching / obs branching: %.3f\n",
                                 (double)(((((double)(expBranch))) / (((double)(obsBranch))))));
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
    							SeqUTSBin1.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$5113)  {
        
//#line 105
new SeqUTSBin1().execute();
    }/* } */
    
    public SeqUTSBin1() {
        super();
        
//#line 59
this.size = 0;
        
//#line 60
this.sumb = 0;
        
//#line 75
this.first = true;
    }

}
