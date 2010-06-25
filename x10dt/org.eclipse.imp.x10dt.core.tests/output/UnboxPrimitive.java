
public class UnboxPrimitive
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<UnboxPrimitive>_RTT = new x10.rtt.RuntimeType<UnboxPrimitive>(
/* base class */UnboxPrimitive.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 21
public boolean
                  run(
                  ){
        
//#line 22
boolean res2 =
          false;
        
//#line 23
boolean res4 =
          false;
        
//#line 25
int ni =
          4;
        
//#line 29
int case1a =
          ((int) (int) 
            ni);
        
//#line 32
try {{
            
//#line 35
final int case2a =
              new x10.core.fun.Fun_0_1<java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final java.lang.Integer __desugarer__var__436__) { return apply((int)__desugarer__var__436__);}
            public final int apply(final int __desugarer__var__436__) { {
                
//#line 35
if (!(((int) __desugarer__var__436__) ==
                                  ((int) 3))) {
                    
//#line 35
throw new java.lang.ClassCastException("x10.lang.Int{self==3}");
                }
                
//#line 35
return __desugarer__var__436__;
            }}
            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;if (i ==1) return x10.rtt.Types.INT;return null;
            }
            }.apply(((int) (int) 
                      ni));
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.ClassCastException) {
        final java.lang.ClassCastException e = (java.lang.ClassCastException) __$generated_wrappedex$__.getCause();
        {
            
//#line 37
res2 = true;
        }
        }
        throw __$generated_wrappedex$__;
        }catch (final java.lang.ClassCastException e) {
            
//#line 37
res2 = true;
        }
        
//#line 40
return res2;
    }
    
    
//#line 44
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
    							UnboxPrimitive.main(args);
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
        
//#line 45
new UnboxPrimitive().execute();
    }/* } */
    
    public UnboxPrimitive() {
        super();
    }

}
