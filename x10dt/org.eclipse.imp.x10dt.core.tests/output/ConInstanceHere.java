
public class ConInstanceHere
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ConInstanceHere>_RTT = new x10.rtt.RuntimeType<ConInstanceHere>(
/* base class */ConInstanceHere.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 20
void
                  m(
                  ){
        
    }
    
    
//#line 21
void
                  n(
                  ){
        
//#line 22
x10.
          lang.
          Runtime.runAt(x10.
                          lang.
                          Runtime.here().next(),
                        new x10.core.fun.VoidFun_0_0() {public final void apply() { {
                            
//#line 24
new x10.core.fun.Fun_0_1<ConInstanceHere, ConInstanceHere>() {public final ConInstanceHere apply$G(final ConInstanceHere __desugarer__var__372__) { return apply(__desugarer__var__372__);}
                            public final ConInstanceHere apply(final ConInstanceHere __desugarer__var__372__) { {
                                
//#line 24
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__372__,null))/* } */ &&
                                                  !(/* template:equalsequals { */x10.rtt.Equality.equalsequals(x10.lang.Place.place(x10.core.Ref.home(__desugarer__var__372__)),x10.
                                                      lang.
                                                      Runtime.here())/* } */)) {
                                    
//#line 24
throw new java.lang.ClassCastException("ConInstanceHere{self.home==here}");
                                }
                                
//#line 24
return __desugarer__var__372__;
                            }}
                            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return ConInstanceHere._RTT;if (i ==1) return ConInstanceHere._RTT;return null;
                            }
                            }.apply(((ConInstanceHere)
                                      ConInstanceHere.this)).m();
                        }}
                        });
    }
    
    
//#line 28
public boolean
                  run(
                  ){
        
//#line 29
try {{
            
//#line 30
this.n();
            
//#line 31
return false;
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.ClassCastException) {
        final java.lang.ClassCastException id$36026 = (java.lang.ClassCastException) __$generated_wrappedex$__.getCause();
        {
            
//#line 33
return true;
        }
        }
        throw __$generated_wrappedex$__;
        }catch (final java.lang.ClassCastException id$36026) {
            
//#line 33
return true;
        }
    }
    
    
//#line 37
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
    							ConInstanceHere.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$36027)  {
        
//#line 38
new ConInstanceHere().execute();
    }/* } */
    
    public ConInstanceHere() {
        super();
    }

}
