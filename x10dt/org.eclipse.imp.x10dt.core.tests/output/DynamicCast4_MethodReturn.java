
public class DynamicCast4_MethodReturn
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<DynamicCast4_MethodReturn>_RTT = new x10.rtt.RuntimeType<DynamicCast4_MethodReturn>(
/* base class */DynamicCast4_MethodReturn.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 21
public boolean
                  run(
                  ){
        
//#line 22
try {{
            
//#line 24
X10DepTypeClassTwo convertedObject =
              ((X10DepTypeClassTwo)(new x10.core.fun.Fun_0_1<X10DepTypeClassTwo, X10DepTypeClassTwo>() {public final X10DepTypeClassTwo apply$G(final X10DepTypeClassTwo __desugarer__var__438__) { return apply(__desugarer__var__438__);}
            public final X10DepTypeClassTwo apply(final X10DepTypeClassTwo __desugarer__var__438__) { {
                
//#line 25
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__438__,null))/* } */ &&
                                  !(((int) __desugarer__var__438__.
                                             p) ==
                                    ((int) 0) &&
                                    ((int) __desugarer__var__438__.
                                             q) ==
                                    ((int) 2))) {
                    
//#line 25
throw new java.lang.ClassCastException("X10DepTypeClassTwo{self.q==2, self.p==0}");
                }
                
//#line 25
return __desugarer__var__438__;
            }}
            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return X10DepTypeClassTwo._RTT;if (i ==1) return X10DepTypeClassTwo._RTT;return null;
            }
            }.apply(/* template:cast_deptype { */(new java.lang.Object() {
                        final X10DepTypeClassTwo cast(X10DepTypeClassTwo self) {
                            if (self==null) return null;
                            x10.rtt.Type rtt = X10DepTypeClassTwo._RTT;
                            if (rtt != null && ! rtt.instanceof$(self)) throw new java.lang.ClassCastException();
                            boolean sat = true;
                            if (! sat) throw new java.lang.ClassCastException();
                            return self;
                        }
                    }.cast((X10DepTypeClassTwo) this.objectReturner()))/* } */)));
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.ClassCastException) {
        java.lang.ClassCastException e = (java.lang.ClassCastException) __$generated_wrappedex$__.getCause();
        {
            
//#line 28
return true;
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.ClassCastException e) {
            
//#line 28
return true;
        }
        
//#line 31
return false;
    }
    
    
//#line 34
public java.lang.Object
                  objectReturner(
                  ){
        
//#line 35
return new X10DepTypeClassTwo(0,
                                                  1);
    }
    
    
//#line 38
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
    							DynamicCast4_MethodReturn.main(args);
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
        
//#line 39
new DynamicCast4_MethodReturn().execute();
    }/* } */
    
    public DynamicCast4_MethodReturn() {
        super();
    }

}
