public class DistStream1
extends Benchmark
{public static final x10.rtt.RuntimeType<DistStream1>_RTT = new x10.rtt.RuntimeType<DistStream1>(
/* base class */DistStream1.class
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
return ((((double)(((((double)(((((double)(1.0))) * (((double)(((double)(int)(((int)(DistStream1.localSize))))))))))) * (((double)(((double)(int)(((int)(DistStream1.PARALLELISM))))))))))) * (((double)(((double)(int)(((int)(DistStream1.NUM_TIMES))))))));
    }
    
    
//#line 28
public double
                  expected(
                  ){
        
//#line 28
return ((((double)(((double)(int)(((int)((((((int)(DistStream1.localSize))) + (((int)(1)))))))))))) * (((double)((((((double)(DistStream1.alpha))) + (((double)(((((double)(DistStream1.gamma))) * (((double)(DistStream1.beta)))))))))))));
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
                                 ((((int)(p))) < (((int)(DistStream1.PARALLELISM))));
                                 
//#line 47
p += 1) {
                    
//#line 48
final x10.
                      lang.
                      Place pl =
                      ((x10.
                      lang.
                      Place)(x10.
                      lang.
                      Place.place((int)(p))));
                    
//#line 49
final x10.core.Rail<java.lang.Double> a =
                      ((x10.core.Rail)(new x10.core.fun.Fun_0_1<x10.core.Rail<java.lang.Double>, x10.core.Rail<java.lang.Double>>() {public final x10.core.Rail<java.lang.Double> apply$G(final x10.core.Rail<java.lang.Double> __desugarer__var__42__) { return apply(__desugarer__var__42__);}
                    public final x10.core.Rail<java.lang.Double> apply(final x10.core.Rail<java.lang.Double> __desugarer__var__42__) { {
                        
//#line 49
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__42__,null))/* } */ &&
                                          !x10.core.Ref.at(__desugarer__var__42__, pl.id)) {
                            
//#line 49
throw new java.lang.ClassCastException("x10.lang.Rail[x10.lang.Double]{self.home==pl}");
                        }
                        
//#line 49
return __desugarer__var__42__;
                    }}
                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.core.Rail._RTT, x10.rtt.Types.DOUBLE);if (i ==1) return new x10.rtt.ParameterizedType(x10.core.Rail._RTT, x10.rtt.Types.DOUBLE);return null;
                    }
                    }.apply(((x10.core.Rail)
                              ((x10.core.Rail<java.lang.Double>)((Object[])as.value)[p])))));
                    
//#line 50
final x10.core.ValRail<java.lang.Double> b =
                      ((x10.core.ValRail<java.lang.Double>)((Object[])bs.value)[p]);
                    
//#line 51
final x10.core.ValRail<java.lang.Double> c =
                      ((x10.core.ValRail<java.lang.Double>)((Object[])cs.value)[p]);
                    
//#line 52
x10.
                      lang.
                      Runtime.runAsync(pl,
                                       new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                           
//#line 53
for (
//#line 53
int tt =
                                                              0;
                                                            ((((int)(tt))) < (((int)(DistStream1.NUM_TIMES))));
                                                            
//#line 53
tt += 1) {
                                               
//#line 54
for (
//#line 54
int i =
                                                                  0;
                                                                ((((int)(i))) < (((int)(DistStream1.localSize))));
                                                                
//#line 54
i += 1) {
                                                   
//#line 55
((double[])a.value)[i] = ((((double)(((double[])b.value)[i]))) + (((double)(((((double)(DistStream1.gamma))) * (((double)(((double[])c.value)[i]))))))));
                                               }
                                           }
                                       }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                       });
                }
            }
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
        java.lang.Throwable __desugarer__var__43__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 47
x10.
              lang.
              Runtime.pushException(__desugarer__var__43__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__43__) {
            
//#line 47
x10.
              lang.
              Runtime.pushException(__desugarer__var__43__);
        }finally {{
             
//#line 47
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 58
final x10.
          lang.
          Place p1 =
          ((x10.
          lang.
          Place)(x10.
          lang.
          Place.place((int)(1))));
        
//#line 59
final x10.core.Rail<java.lang.Double> a =
          ((x10.core.Rail)(new x10.core.fun.Fun_0_1<x10.core.Rail<java.lang.Double>, x10.core.Rail<java.lang.Double>>() {public final x10.core.Rail<java.lang.Double> apply$G(final x10.core.Rail<java.lang.Double> __desugarer__var__44__) { return apply(__desugarer__var__44__);}
        public final x10.core.Rail<java.lang.Double> apply(final x10.core.Rail<java.lang.Double> __desugarer__var__44__) { {
            
//#line 59
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__44__,null))/* } */ &&
                              !x10.core.Ref.at(__desugarer__var__44__, p1.id)) {
                
//#line 59
throw new java.lang.ClassCastException("x10.lang.Rail[x10.lang.Double]{self.home==p1}");
            }
            
//#line 59
return __desugarer__var__44__;
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.core.Rail._RTT, x10.rtt.Types.DOUBLE);if (i ==1) return new x10.rtt.ParameterizedType(x10.core.Rail._RTT, x10.rtt.Types.DOUBLE);return null;
        }
        }.apply(((x10.core.Rail)
                  ((x10.core.Rail<java.lang.Double>)((Object[])as.value)[1])))));
        
//#line 60
return x10.
          lang.
          Runtime.<java.lang.Double>evalAt$G(x10.rtt.Types.DOUBLE,
                                             p1,
                                             new x10.core.fun.Fun_0_0<java.lang.Double>() {public final java.lang.Double apply$G() { return apply();}
                                             public final double apply() { {
                                                 
//#line 60
return ((double[])a.value)[1];
                                             }}
                                             public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.DOUBLE;return null;
                                             }
                                             });
        }
    
    
//#line 67
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
    							DistStream1.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$2165)  {
        
//#line 68
new DistStream1().execute();
    }/* } */
    
    public DistStream1() {
        super();
        
//#line 34
this.as = ((x10.core.ValRail)((new java.lang.Object() {final x10.core.ValRail<x10.core.Rail<java.lang.Double>> apply(int length) {Object[] array = new Object[length];for (int p$ = 0; p$ < length; p$++) {final int p = p$;array[p] = x10.
          lang.
          Runtime.<x10.core.Rail<java.lang.Double>>evalAt$G(new x10.rtt.ParameterizedType(x10.core.Rail._RTT, x10.rtt.Types.DOUBLE),
                                                            x10.
                                                              lang.
                                                              Place.place((int)(p)),
                                                            new x10.core.fun.Fun_0_0<x10.core.Rail<java.lang.Double>>() {public final x10.core.Rail<java.lang.Double> apply$G() { return apply();}
                                                            public final x10.core.Rail<java.lang.Double> apply() { {
                                                                
//#line 35
return x10.core.RailFactory.<java.lang.Double>makeVarRail(x10.rtt.Types.DOUBLE, ((int)(DistStream1.localSize)));
                                                            }}
                                                            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.core.Rail._RTT, x10.rtt.Types.DOUBLE);return null;
                                                            }
                                                            });}return new x10.core.ValRail<x10.core.Rail<java.lang.Double>>(new x10.rtt.ParameterizedType(x10.core.Rail._RTT, x10.rtt.Types.DOUBLE), DistStream1.PARALLELISM, array);}}.apply(DistStream1.PARALLELISM))));
        
//#line 38
this.bs = ((x10.core.ValRail)((new java.lang.Object() {final x10.core.ValRail<x10.core.ValRail<java.lang.Double>> apply(int length) {Object[] array = new Object[length];for (int p$ = 0; p$ < length; p$++) {final int p = p$;array[p] = x10.
          lang.
          Runtime.<x10.core.ValRail<java.lang.Double>>evalAt$G(new x10.rtt.ParameterizedType(x10.core.ValRail._RTT, x10.rtt.Types.DOUBLE),
                                                               x10.
                                                                 lang.
                                                                 Place.place((int)(p)),
                                                               new x10.core.fun.Fun_0_0<x10.core.ValRail<java.lang.Double>>() {public final x10.core.ValRail<java.lang.Double> apply$G() { return apply();}
                                                               public final x10.core.ValRail<java.lang.Double> apply() { {
                                                                   
//#line 39
return (new java.lang.Object() {final x10.core.ValRail<java.lang.Double> apply(int length) {double[] array = new double[length];for (int i$ = 0; i$ < length; i$++) {final int i = i$;array[i] = ((((double)(DistStream1.alpha))) * (((double)(((double)(int)(((int)((((((int)(((((int)(p))) * (((int)(DistStream1.localSize))))))) + (((int)(i)))))))))))));}return new x10.core.ValRail<java.lang.Double>(x10.rtt.Types.DOUBLE, DistStream1.localSize, array);}}.apply(DistStream1.localSize));
                                                               }}
                                                               public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.core.ValRail._RTT, x10.rtt.Types.DOUBLE);return null;
                                                               }
                                                               });}return new x10.core.ValRail<x10.core.ValRail<java.lang.Double>>(new x10.rtt.ParameterizedType(x10.core.ValRail._RTT, x10.rtt.Types.DOUBLE), DistStream1.PARALLELISM, array);}}.apply(DistStream1.PARALLELISM))));
        
//#line 42
this.cs = ((x10.core.ValRail)((new java.lang.Object() {final x10.core.ValRail<x10.core.ValRail<java.lang.Double>> apply(int length) {Object[] array = new Object[length];for (int p$ = 0; p$ < length; p$++) {final int p = p$;array[p] = x10.
          lang.
          Runtime.<x10.core.ValRail<java.lang.Double>>evalAt$G(new x10.rtt.ParameterizedType(x10.core.ValRail._RTT, x10.rtt.Types.DOUBLE),
                                                               x10.
                                                                 lang.
                                                                 Place.place((int)(p)),
                                                               new x10.core.fun.Fun_0_0<x10.core.ValRail<java.lang.Double>>() {public final x10.core.ValRail<java.lang.Double> apply$G() { return apply();}
                                                               public final x10.core.ValRail<java.lang.Double> apply() { {
                                                                   
//#line 43
return (new java.lang.Object() {final x10.core.ValRail<java.lang.Double> apply(int length) {double[] array = new double[length];for (int i$ = 0; i$ < length; i$++) {final int i = i$;array[i] = ((((double)(DistStream1.beta))) * (((double)(((double)(int)(((int)((((((int)(((((int)(p))) * (((int)(DistStream1.localSize))))))) + (((int)(i)))))))))))));}return new x10.core.ValRail<java.lang.Double>(x10.rtt.Types.DOUBLE, DistStream1.localSize, array);}}.apply(DistStream1.localSize));
                                                               }}
                                                               public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.core.ValRail._RTT, x10.rtt.Types.DOUBLE);return null;
                                                               }
                                                               });}return new x10.core.ValRail<x10.core.ValRail<java.lang.Double>>(new x10.rtt.ParameterizedType(x10.core.ValRail._RTT, x10.rtt.Types.DOUBLE), DistStream1.PARALLELISM, array);}}.apply(DistStream1.PARALLELISM))));
    }
    
    }
    