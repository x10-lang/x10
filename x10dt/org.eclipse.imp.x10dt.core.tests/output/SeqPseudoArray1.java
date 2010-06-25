public class SeqPseudoArray1
extends Benchmark
{public static final x10.rtt.RuntimeType<SeqPseudoArray1>_RTT = new x10.rtt.RuntimeType<SeqPseudoArray1>(
/* base class */SeqPseudoArray1.class
, /* parents */ new x10.rtt.Type[] {Benchmark._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 22
final int
      N =
      2000;
    
    
//#line 23
double
                  expected(
                  ){
        
//#line 23
return 1.0;
    }
    
    
//#line 24
double
                  operations(
                  ){
        
//#line 24
return ((double)(int)(((int)(((((int)(N))) * (((int)(N))))))));
    }
    
    
//#line 31
final static class Arr
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<SeqPseudoArray1.
      Arr>_RTT = new x10.rtt.RuntimeType<SeqPseudoArray1.
      Arr>(
    /* base class */SeqPseudoArray1.
      Arr.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 33
final int
          m0;
        
//#line 34
final int
          m1;
        
//#line 35
final x10.core.Rail<java.lang.Double>
          raw;
        
        
//#line 37
Arr(final int m0,
                        final int m1) {
            
//#line 37
super();
            
//#line 38
this.m0 = m0;
            
//#line 39
this.m1 = m1;
            
//#line 40
this.raw = ((x10.core.Rail)(x10.core.RailFactory.<java.lang.Double>makeVarRail(x10.rtt.Types.DOUBLE, ((int)(((((int)(m0))) * (((int)(m1)))))))));
        }
        
        
//#line 43
final void
                      set(
                      final double v,
                      final int i0,
                      final int i1){
            
//#line 44
((double[])raw.value)[((((int)(((((int)(i0))) * (((int)(m1))))))) + (((int)(i1))))] = v;
        }
        
        
//#line 47
final public double
                      apply(
                      final int i0,
                      final int i1){
            
//#line 48
return ((double[])raw.value)[((((int)(((((int)(i0))) * (((int)(m1))))))) + (((int)(i1))))];
        }
    
    }
    
    
//#line 52
final SeqPseudoArray1.
      Arr
      a;
    
    
//#line 54
double
                  once(
                  ){
        
//#line 55
for (
//#line 55
int i =
                           0;
                         ((((int)(i))) < (((int)(N))));
                         
//#line 55
i += 1) {
            
//#line 56
for (
//#line 56
int j =
                               0;
                             ((((int)(j))) < (((int)(N))));
                             
//#line 56
j += 1) {
                
//#line 57
new x10.core.fun.Fun_0_4<SeqPseudoArray1.
                  Arr, java.lang.Integer, java.lang.Integer, java.lang.Double, java.lang.Double>() {public final java.lang.Double apply$G(final SeqPseudoArray1.
                  Arr x,final java.lang.Integer y0,final java.lang.Integer y1,final java.lang.Double z) { return apply(x,(int)y0,(int)y1,(double)z);}
                public final double apply(final SeqPseudoArray1.
                  Arr x, final int y0, final int y1, final double z) { {
                    
//#line 57
x.set((double)(((((double)(x.apply((int)(y0),
                                                                   (int)(y1))))) + (((double)(z))))),
                                      (int)(y0),
                                      (int)(y1));
                    
//#line 57
return x.apply((int)(y0),
                                               (int)(y1));
                }}
                public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return SeqPseudoArray1.Arr._RTT;if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.DOUBLE;if (i ==4) return x10.rtt.Types.DOUBLE;return null;
                }
                }.apply(a,
                        i,
                        j,
                        ((double)(int)(((int)(1)))));
            }
        }
        
//#line 58
return a.apply((int)(20),
                                   (int)(20));
    }
    
    
//#line 65
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
    							SeqPseudoArray1.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$4001)  {
        
//#line 66
new SeqPseudoArray1().execute();
    }/* } */
    
    public SeqPseudoArray1() {
        super();
        
//#line 52
this.a = ((SeqPseudoArray1.
          Arr)(new SeqPseudoArray1.
          Arr(N,
              N)));
    }

}
