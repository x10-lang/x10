
public class CastPrimitiveConstrainedToPrimitiveConstrained
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<CastPrimitiveConstrainedToPrimitiveConstrained>_RTT = new x10.rtt.RuntimeType<CastPrimitiveConstrainedToPrimitiveConstrained>(
/* base class */CastPrimitiveConstrainedToPrimitiveConstrained.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 20
public boolean
                  run(
                  ){
        
//#line 22
try {{
            
//#line 23
int i =
              0;
            
//#line 24
int j =
              0;
            
//#line 25
i = new x10.core.fun.Fun_0_1<java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final java.lang.Integer __desugarer__var__406__) { return apply((int)__desugarer__var__406__);}
            public final int apply(final int __desugarer__var__406__) { {
                
//#line 25
if (!(((int) __desugarer__var__406__) ==
                                  ((int) 0))) {
                    
//#line 25
throw new java.lang.ClassCastException("x10.lang.Int{self==0}");
                }
                
//#line 25
return __desugarer__var__406__;
            }}
            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;if (i ==1) return x10.rtt.Types.INT;return null;
            }
            }.apply(((int) (int) 
                      j));
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
        final java.lang.Throwable e = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 27
return false;
        }
        }
        throw __$generated_wrappedex$__;
        }catch (final java.lang.Throwable e) {
            
//#line 27
return false;
        }
        
//#line 30
return true;
    }
    
    
//#line 33
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
    							CastPrimitiveConstrainedToPrimitiveConstrained.main(args);
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
        
//#line 34
new CastPrimitiveConstrainedToPrimitiveConstrained().execute();
    }/* } */
    
    public CastPrimitiveConstrainedToPrimitiveConstrained() {
        super();
    }

}
