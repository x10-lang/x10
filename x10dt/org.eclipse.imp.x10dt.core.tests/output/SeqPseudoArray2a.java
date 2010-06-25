public class SeqPseudoArray2a
extends Benchmark
{public static final x10.rtt.RuntimeType<SeqPseudoArray2a>_RTT = new x10.rtt.RuntimeType<SeqPseudoArray2a>(
/* base class */SeqPseudoArray2a.class
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
final static class Arr
                extends x10.core.Ref
                  implements x10.core.fun.Fun_0_2<java.lang.Integer,java.lang.Integer,java.lang.Double>
                {public static final x10.rtt.RuntimeType<SeqPseudoArray2a.
      Arr>_RTT = new x10.rtt.RuntimeType<SeqPseudoArray2a.
      Arr>(
    /* base class */SeqPseudoArray2a.
      Arr.class
    , /* parents */ new x10.rtt.Type[] {new x10.rtt.ParameterizedType(x10.core.fun.Fun_0_2._RTT, x10.rtt.Types.INT, x10.rtt.Types.INT, x10.rtt.Types.DOUBLE), x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    // bridge for method abstract public (a1:Z1, a2:Z2)=> U.apply(a1:Z1a2:Z2): U
    final public java.lang.Double
      apply$G(java.lang.Integer a1,
    java.lang.Integer a2){return apply((int) a1,
    (int) a2);}
    
        
//#line 40
final int
          m0;
        
//#line 41
final int
          m1;
        
//#line 42
final x10.core.Rail<java.lang.Double>
          raw;
        
        
//#line 44
Arr(final int m0,
                        final int m1) {
            
//#line 44
super();
            
//#line 45
this.m0 = m0;
            
//#line 46
this.m1 = m1;
            
//#line 47
this.raw = ((x10.core.Rail)(x10.core.RailFactory.<java.lang.Double>makeVarRail(x10.rtt.Types.DOUBLE, ((int)(((((int)(m0))) * (((int)(m1)))))))));
        }
        
        
//#line 50
final void
                      set(
                      final double v,
                      final int i0,
                      final int i1){
            
//#line 51
((double[])raw.value)[((((int)(((((int)(i0))) * (((int)(m1))))))) + (((int)(i1))))] = v;
        }
        
        
//#line 54
final public double
                      apply(
                      final int i0,
                      final int i1){
            
//#line 55
return ((double[])raw.value)[((((int)(((((int)(i0))) * (((int)(m1))))))) + (((int)(i1))))];
        }
    
    }
    
    
//#line 59
final SeqPseudoArray2a.
      Arr
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
sum += a.apply((int)(i),
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
    							SeqPseudoArray2a.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$4141)  {
        
//#line 77
new SeqPseudoArray2a().execute();
    }/* } */
    
    public SeqPseudoArray2a() {
        super();
        
//#line 59
this.a = ((SeqPseudoArray2a.
          Arr)(new SeqPseudoArray2a.
          Arr(N,
              N)));
    }

}
