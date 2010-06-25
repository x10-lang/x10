public class ParStream1
extends Benchmark
{public static final x10.rtt.RuntimeType<ParStream1>_RTT = new x10.rtt.RuntimeType<ParStream1>(
/* base class */ParStream1.class
, /* parents */ new x10.rtt.Type[] {Benchmark._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 19
final static double
      alpha =
      1.5;
    
//#line 20
final static double
      beta =
      2.5;
    
//#line 21
final static double
      gamma =
      3.0;
    
//#line 23
final static int
      NUM_TIMES =
      10;
    
//#line 24
final static int
      PARALLELISM =
      2;
    
//#line 25
final static int
      localSize =
      ((((int)(512))) * (((int)(1024))));
    
    
//#line 27
public double
                  operations(
                  ){
        
//#line 27
return ((((double)(((((double)(((((double)(1.0))) * (((double)(((double)(int)(((int)(ParStream1.localSize))))))))))) * (((double)(((double)(int)(((int)(ParStream1.PARALLELISM))))))))))) * (((double)(((double)(int)(((int)(ParStream1.NUM_TIMES))))))));
    }
    
    
//#line 28
public double
                  expected(
                  ){
        
//#line 28
return ((((double)(((double)(int)(((int)((((((int)(ParStream1.localSize))) + (((int)(1)))))))))))) * (((double)((((((double)(ParStream1.alpha))) + (((double)(((((double)(ParStream1.gamma))) * (((double)(ParStream1.beta)))))))))))));
    }
    
    
//#line 34
final x10.core.ValRail<x10.core.Rail<java.lang.Double>>
      as;
    
//#line 38
final x10.core.ValRail<x10.core.ValRail<java.lang.Double>>
      bs;
    
//#line 42
final x10.core.ValRail<x10.core.ValRail<java.lang.Double>>
      cs;
    
    
//#line 46
public double
                  once(
                  ){
        
//#line 47
try {{
            
//#line 47
x10.
              lang.
              Runtime.startFinish();
            {
                
//#line 47
for (
//#line 47
int p =
                                   0;
                                 ((((int)(p))) < (((int)(ParStream1.PARALLELISM))));
                                 
//#line 47
p += 1) {
                    
//#line 48
final x10.core.Rail<java.lang.Double> a =
                      ((x10.core.Rail)(((x10.core.Rail<java.lang.Double>)((Object[])as.value)[p])));
                    
//#line 49
final x10.core.ValRail<java.lang.Double> b =
                      ((x10.core.ValRail<java.lang.Double>)((Object[])bs.value)[p]);
                    
//#line 50
final x10.core.ValRail<java.lang.Double> c =
                      ((x10.core.ValRail<java.lang.Double>)((Object[])cs.value)[p]);
                    
//#line 51
x10.
                      lang.
                      Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                           
//#line 52
for (
//#line 52
int tt =
                                                              0;
                                                            ((((int)(tt))) < (((int)(ParStream1.NUM_TIMES))));
                                                            
//#line 52
tt += 1) {
                                               
//#line 53
for (
//#line 53
int i =
                                                                  0;
                                                                ((((int)(i))) < (((int)(ParStream1.localSize))));
                                                                
//#line 53
i += 1) {
                                                   
//#line 54
((double[])a.value)[i] = ((((double)(((double[])b.value)[i]))) + (((double)(((((double)(ParStream1.gamma))) * (((double)(((double[])c.value)[i]))))))));
                                               }
                                           }
                                       }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                       });
                }
            }
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
        java.lang.Throwable __desugarer__var__52__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 47
x10.
              lang.
              Runtime.pushException(__desugarer__var__52__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__52__) {
            
//#line 47
x10.
              lang.
              Runtime.pushException(__desugarer__var__52__);
        }finally {{
             
//#line 47
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 57
return ((double[])((x10.core.Rail<java.lang.Double>)((Object[])as.value)[1]).value)[1];
        }
    
    
//#line 64
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
    							ParStream1.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$2437)  {
        
//#line 65
new ParStream1().execute();
    }/* } */
    
    public ParStream1() {
        super();
        
//#line 34
this.as = ((x10.core.ValRail)((new java.lang.Object() {final x10.core.ValRail<x10.core.Rail<java.lang.Double>> apply(int length) {Object[] array = new Object[length];for (int p$ = 0; p$ < length; p$++) {final int p = p$;array[p] = x10.core.RailFactory.<java.lang.Double>makeVarRail(x10.rtt.Types.DOUBLE, ((int)(ParStream1.localSize)));}return new x10.core.ValRail<x10.core.Rail<java.lang.Double>>(new x10.rtt.ParameterizedType(x10.core.Rail._RTT, x10.rtt.Types.DOUBLE), ParStream1.PARALLELISM, array);}}.apply(ParStream1.PARALLELISM))));
        
//#line 38
this.bs = ((x10.core.ValRail)((new java.lang.Object() {final x10.core.ValRail<x10.core.ValRail<java.lang.Double>> apply(int length) {Object[] array = new Object[length];for (int p$ = 0; p$ < length; p$++) {final int p = p$;array[p] = (new java.lang.Object() {final x10.core.ValRail<java.lang.Double> apply(int length) {double[] array = new double[length];for (int i$ = 0; i$ < length; i$++) {final int i = i$;array[i] = ((((double)(ParStream1.alpha))) * (((double)(((double)(int)(((int)((((((int)(((((int)(p))) * (((int)(ParStream1.localSize))))))) + (((int)(i)))))))))))));}return new x10.core.ValRail<java.lang.Double>(x10.rtt.Types.DOUBLE, ParStream1.localSize, array);}}.apply(ParStream1.localSize));}return new x10.core.ValRail<x10.core.ValRail<java.lang.Double>>(new x10.rtt.ParameterizedType(x10.core.ValRail._RTT, x10.rtt.Types.DOUBLE), ParStream1.PARALLELISM, array);}}.apply(ParStream1.PARALLELISM))));
        
//#line 42
this.cs = ((x10.core.ValRail)((new java.lang.Object() {final x10.core.ValRail<x10.core.ValRail<java.lang.Double>> apply(int length) {Object[] array = new Object[length];for (int p$ = 0; p$ < length; p$++) {final int p = p$;array[p] = (new java.lang.Object() {final x10.core.ValRail<java.lang.Double> apply(int length) {double[] array = new double[length];for (int i$ = 0; i$ < length; i$++) {final int i = i$;array[i] = ((((double)(ParStream1.beta))) * (((double)(((double)(int)(((int)((((((int)(((((int)(p))) * (((int)(ParStream1.localSize))))))) + (((int)(i)))))))))))));}return new x10.core.ValRail<java.lang.Double>(x10.rtt.Types.DOUBLE, ParStream1.localSize, array);}}.apply(ParStream1.localSize));}return new x10.core.ValRail<x10.core.ValRail<java.lang.Double>>(new x10.rtt.ParameterizedType(x10.core.ValRail._RTT, x10.rtt.Types.DOUBLE), ParStream1.PARALLELISM, array);}}.apply(ParStream1.PARALLELISM))));
    }
    
    }
    