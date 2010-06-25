
public class Long_ConstraintDeclaredAsLong
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<Long_ConstraintDeclaredAsLong>_RTT = new x10.rtt.RuntimeType<Long_ConstraintDeclaredAsLong>(
/* base class */Long_ConstraintDeclaredAsLong.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 22
public boolean
                  run(
                  ){
        
//#line 23
long j =
          2147493646L;
        
//#line 25
long i =
          2147493647L;
        
//#line 26
i = new x10.core.fun.Fun_0_1<java.lang.Long, java.lang.Long>() {public final java.lang.Long apply$G(final java.lang.Long __desugarer__var__418__) { return apply((long)__desugarer__var__418__);}
        public final long apply(final long __desugarer__var__418__) { {
            
//#line 26
if (!(((long) __desugarer__var__418__) ==
                              ((long) 2147493647L))) {
                
//#line 26
throw new java.lang.ClassCastException("x10.lang.Long{self==2147493647L}");
            }
            
//#line 26
return __desugarer__var__418__;
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.LONG;if (i ==1) return x10.rtt.Types.LONG;return null;
        }
        }.apply(((long) (long) 
                  (j += 1L)));
        
//#line 27
return (((((long) j) ==
                              ((long) 2147493647L))) &&
                            ((((long) i) ==
                              ((long) 2147493647L))));
    }
    
    
//#line 30
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
    							Long_ConstraintDeclaredAsLong.main(args);
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
        
//#line 31
new Long_ConstraintDeclaredAsLong().execute();
    }/* } */
    
    public Long_ConstraintDeclaredAsLong() {
        super();
    }

}
