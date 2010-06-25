
public class NumericExpressionToPrimitiveDepType_8
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<NumericExpressionToPrimitiveDepType_8>_RTT = new x10.rtt.RuntimeType<NumericExpressionToPrimitiveDepType_8>(
/* base class */NumericExpressionToPrimitiveDepType_8.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 22
public boolean
                  run(
                  ){
        
//#line 23
double j =
          0.01;
        
//#line 24
double i =
          0.02;
        
//#line 25
i = new x10.core.fun.Fun_0_1<java.lang.Double, java.lang.Double>() {public final java.lang.Double apply$G(final java.lang.Double __desugarer__var__430__) { return apply((double)__desugarer__var__430__);}
        public final double apply(final double __desugarer__var__430__) { {
            
//#line 25
if (!(((double) __desugarer__var__430__) ==
                              ((double) 0.02))) {
                
//#line 25
throw new java.lang.ClassCastException("x10.lang.Double{self==0.02}");
            }
            
//#line 25
return __desugarer__var__430__;
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.DOUBLE;if (i ==1) return x10.rtt.Types.DOUBLE;return null;
        }
        }.apply(((double) (double) 
                  (j *= 2)));
        
//#line 26
return (((((double) j) ==
                              ((double) 0.02))) &&
                            ((((double) i) ==
                              ((double) 0.02))));
    }
    
    
//#line 29
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
    							NumericExpressionToPrimitiveDepType_8.main(args);
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
    public static void main(x10.core.Rail<java.lang.String> args)  {
        
//#line 30
new NumericExpressionToPrimitiveDepType_8().execute();
    }/* } */
    
    public NumericExpressionToPrimitiveDepType_8() {
        super();
    }

}
