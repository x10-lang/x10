
public class CastPrimitiveToPrimitiveConstrained1
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<CastPrimitiveToPrimitiveConstrained1>_RTT = new x10.rtt.RuntimeType<CastPrimitiveToPrimitiveConstrained1>(
/* base class */CastPrimitiveToPrimitiveConstrained1.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 21
public boolean
                  run(
                  ){
        
//#line 23
try {{
            
//#line 24
int i =
              0;
            
//#line 25
int j =
              1;
            
//#line 26
i = new x10.core.fun.Fun_0_1<java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final java.lang.Integer __desugarer__var__410__) { return apply((int)__desugarer__var__410__);}
            public final int apply(final int __desugarer__var__410__) { {
                
//#line 26
if (!(((int) __desugarer__var__410__) ==
                                  ((int) 0))) {
                    
//#line 26
throw new java.lang.ClassCastException("x10.lang.Int{self==0}");
                }
                
//#line 26
return __desugarer__var__410__;
            }}
            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;if (i ==1) return x10.rtt.Types.INT;return null;
            }
            }.apply(((int) (int) 
                      j));
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.ClassCastException) {
        final java.lang.ClassCastException e = (java.lang.ClassCastException) __$generated_wrappedex$__.getCause();
        {
            
//#line 28
return true;
        }
        }
        throw __$generated_wrappedex$__;
        }catch (final java.lang.ClassCastException e) {
            
//#line 28
return true;
        }
        
//#line 31
return false;
    }
    
    
//#line 34
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
    							CastPrimitiveToPrimitiveConstrained1.main(args);
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
        
//#line 35
new CastPrimitiveToPrimitiveConstrained1().execute();
    }/* } */
    
    public CastPrimitiveToPrimitiveConstrained1() {
        super();
    }

}
