
class ParUTSBin1
extends Benchmark
{public static final x10.rtt.RuntimeType<ParUTSBin1>_RTT = new x10.rtt.RuntimeType<ParUTSBin1>(
/* base class */ParUTSBin1.class
, /* parents */ new x10.rtt.Type[] {Benchmark._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 49
final static int
      r0 =
      0;
    
//#line 51
final static int
      b0 =
      50000;
    
//#line 52
final static double
      q =
      0.12;
    
//#line 53
final static int
      m =
      8;
    
    
//#line 54
double
                  expected(
                  ){
        
//#line 54
return 1234872.0;
    }
    
    
//#line 56
double
                  operations(
                  ){
        
//#line 56
return ((double)(int)(((int)(asize.intValue()))));
    }
    
    
//#line 99
final java.util.concurrent.atomic.AtomicInteger
      asize;
    
//#line 100
final java.util.concurrent.atomic.AtomicInteger
      asumb;
    
    
//#line 102
void
                   visit(
                   final long r){
        
//#line 103
final double x =
          UTSRand.number((long)(r));
        
//#line 104
final int b =
          ((((double)(x))) < (((double)(ParUTSBin1.q))))
          ? ParUTSBin1.m
          : 0;
        
//#line 105
asumb.addAndGet(((int)(b)));
        
//#line 106
asize.incrementAndGet();
        
//#line 107
for (
//#line 107
int i =
                            0;
                          ((((int)(i))) < (((int)(b))));
                          
//#line 107
i += 1) {
            
//#line 108
final int ii =
              i;
            
//#line 109
x10.
              lang.
              Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                   
//#line 109
ParUTSBin1.this.visit((long)(UTSRand.next((long)(r),
                                                                                          (int)(ii))));
                               }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                               });
        }
    }
    
    
//#line 117
boolean
      first;
    
    
//#line 119
double
                   once(
                   ){
        
//#line 122
asize.set(((int)(0)));
        
//#line 123
asumb.set(((int)(0)));
        
//#line 124
try {{
            
//#line 124
x10.
              lang.
              Runtime.startFinish();
            {
                
//#line 124
for (
//#line 124
int i =
                                    0;
                                  ((((int)(i))) < (((int)(ParUTSBin1.b0))));
                                  
//#line 124
i += 1) {
                    
//#line 125
this.visit((long)(UTSRand.next((long)(((long)(((int)(ParUTSBin1.r0))))),
                                                                (int)(i))));
                }
            }
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
        java.lang.Throwable __desugarer__var__54__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 124
x10.
              lang.
              Runtime.pushException(__desugarer__var__54__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__54__) {
            
//#line 124
x10.
              lang.
              Runtime.pushException(__desugarer__var__54__);
        }finally {{
             
//#line 124
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 127
final int size =
          asize.intValue();
        
//#line 128
final int sumb =
          asumb.intValue();
        
//#line 131
if (first) {
            
//#line 132
final double expSize =
              ((((double)(((double)(int)(((int)(ParUTSBin1.b0))))))) / (((double)((((((double)(1.0))) - (((double)(((((double)(ParUTSBin1.q))) * (((double)(((double)(int)(((int)(ParUTSBin1.m)))))))))))))))));
            
//#line 133
final double obsBranch =
              ((((double)((((double)(int)(((int)(sumb)))))))) / (((double)(((double)(int)(((int)(size))))))));
            
//#line 134
final double expBranch =
              ((((double)(ParUTSBin1.q))) * (((double)(((double)(int)(((int)(ParUTSBin1.m))))))));
            
//#line 135
x10.
              io.
              Console.OUT.printf("exp size / obs size: %.3f\n",
                                 (double)(((((double)(expSize))) / (((double)(((double)(int)(((int)(size))))))))));
            
//#line 136
x10.
              io.
              Console.OUT.printf("exp branching / obs branching: %.3f\n",
                                 (double)(((((double)(expBranch))) / (((double)(obsBranch))))));
        }
        
//#line 138
this.first = false;
        
//#line 141
return ((double)(int)(((int)(size))));
        }
    
    
//#line 149
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
    							ParUTSBin1.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$2573)  {
        
//#line 150
new ParUTSBin1().execute();
    }/* } */
    
    public ParUTSBin1() {
        super();
        
//#line 99
this.asize = ((java.util.concurrent.atomic.AtomicInteger)(new java.util.concurrent.atomic.AtomicInteger(0)));
        
//#line 100
this.asumb = ((java.util.concurrent.atomic.AtomicInteger)(new java.util.concurrent.atomic.AtomicInteger(0)));
        
//#line 117
this.first = true;
    }
    
    }
    