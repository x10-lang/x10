public class SeqPseudoArray2b
extends Benchmark
{public static final x10.rtt.RuntimeType<SeqPseudoArray2b>_RTT = new x10.rtt.RuntimeType<SeqPseudoArray2b>(
/* base class */SeqPseudoArray2b.class
, /* parents */ new x10.rtt.Type[] {Benchmark._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 29
final int
      N =
      2000;
    
    
//#line 30
double
                  expected(
                  ){
        
//#line 30
return ((((double)(((((double)(((((double)(1.0))) * (((double)(((double)(int)(((int)(N))))))))))) * (((double)(((double)(int)(((int)(N))))))))))) * (((double)(((double)(int)(((int)((((((int)(N))) - (((int)(1)))))))))))));
    }
    
    
//#line 31
double
                  operations(
                  ){
        
//#line 31
return ((((double)(((((double)(2.0))) * (((double)(((double)(int)(((int)(N))))))))))) * (((double)(((double)(int)(((int)(N))))))));
    }
    
    
//#line 38
final static class Arr<T>
                extends x10.core.Ref
                  implements x10.core.fun.Fun_0_2<java.lang.Integer,java.lang.Integer,T>
                {public static final x10.rtt.RuntimeType<SeqPseudoArray2b.
      Arr>_RTT = new x10.rtt.RuntimeType<SeqPseudoArray2b.
      Arr>(
    /* base class */SeqPseudoArray2b.
      Arr.class, 
    /* variances */ new x10.rtt.RuntimeType.Variance[] {x10.rtt.RuntimeType.Variance.INVARIANT}
    , /* parents */ new x10.rtt.Type[] {new x10.rtt.ParameterizedType(x10.core.fun.Fun_0_2._RTT, x10.rtt.Types.INT, x10.rtt.Types.INT, new x10.rtt.UnresolvedType(0)), x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    public x10.rtt.Type getParam(int i) {if (i ==0)return T;return null;}
    // bridge for method abstract public (a1:Z1, a2:Z2)=> U.apply(a1:Z1a2:Z2): U
    final public T
      apply$G(java.lang.Integer a1,
    java.lang.Integer a2){return apply$G((int) a1,
    (int) a2);}
    
        private final x10.rtt.Type T;
        
        
//#line 40
final int
          m0;
        
//#line 41
final int
          m1;
        
//#line 42
final x10.core.Rail<T>
          raw;
        
        
//#line 44
Arr(final x10.rtt.Type T,
                        final int m0,
                        final int m1) {
                                               
//#line 44
super();
                                           this.T = T;
                                            {
                                               
//#line 45
this.m0 = m0;
                                               
//#line 46
this.m1 = m1;
                                               
//#line 47
this.raw = ((x10.core.Rail)(x10.core.RailFactory.<T>makeVarRail(T, ((int)(((((int)(m0))) * (((int)(m1)))))))));
                                           }}
        
        
//#line 50
final void
                      set(
                      final T v,
                      final int i0,
                      final int i1){
            
//#line 51
(raw).set$G(v, ((int)(((((int)(((((int)(i0))) * (((int)(m1))))))) + (((int)(i1)))))));
        }
        
        
//#line 54
final public T
                      apply$G(
                      final int i0,
                      final int i1){
            
//#line 55
return (raw).apply$G(((int)(((((int)(((((int)(i0))) * (((int)(m1))))))) + (((int)(i1)))))));
        }
    
    }
    
    
//#line 59
final SeqPseudoArray2b.
      Arr<java.lang.Double>
      a;
    
    
//#line 61
double
                  once(
                  ){
        
//#line 62
for (
//#line 62
int i =
                           0;
                         ((((int)(i))) < (((int)(N))));
                         
//#line 62
i += 1) {
            
//#line 63
for (
//#line 63
int j =
                               0;
                             ((((int)(j))) < (((int)(N))));
                             
//#line 63
j += 1) {
                
//#line 64
a.set((double)(((double)(int)(((int)((((((int)(i))) + (((int)(j)))))))))),
                                  (int)(i),
                                  (int)(j));
            }
        }
        
//#line 65
double sum =
          0.0;
        
//#line 66
for (
//#line 66
int i =
                           0;
                         ((((int)(i))) < (((int)(N))));
                         
//#line 66
i += 1) {
            
//#line 67
for (
//#line 67
int j =
                               0;
                             ((((int)(j))) < (((int)(N))));
                             
//#line 67
j += 1) {
                
//#line 68
sum += a.apply$G((int)(i),
                                             (int)(j));
            }
        }
        
//#line 69
return sum;
    }
    
    
//#line 76
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
    							SeqPseudoArray2b.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$4281)  {
        
//#line 77
new SeqPseudoArray2b().execute();
    }/* } */
    
    public SeqPseudoArray2b() {
        super();
        
//#line 59
this.a = ((SeqPseudoArray2b.
          Arr)(new SeqPseudoArray2b.
          Arr<java.lang.Double>(x10.rtt.Types.DOUBLE,
                                N,
                                N)));
    }

}
