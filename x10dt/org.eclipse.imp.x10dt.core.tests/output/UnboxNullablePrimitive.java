
public class UnboxNullablePrimitive
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<UnboxNullablePrimitive>_RTT = new x10.rtt.RuntimeType<UnboxNullablePrimitive>(
/* base class */UnboxNullablePrimitive.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 21
public boolean
                  run(
                  ){
        
//#line 22
boolean res1 =
          false;
        
//#line 23
boolean res2 =
          false;
        
//#line 24
boolean res4 =
          false;
        
//#line 26
x10.
          util.
          Box<java.lang.Integer> ni =
          new x10.
          util.
          Box<java.lang.Integer>(x10.rtt.Types.INT,
                                 4);
        
//#line 27
x10.
          util.
          Box<java.lang.Integer> nn =
          null;
        
//#line 31
int case1a =
          ((int) (int) 
            ni.
              value);
        
//#line 33
try {{
            
//#line 35
final int case1b =
              ((int) (int) 
                nn.
                  value);
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.ClassCastException) {
        final java.lang.ClassCastException e = (java.lang.ClassCastException) __$generated_wrappedex$__.getCause();
        {
            
//#line 37
res1 = true;
        }
        }
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.NullPointerException) {
        final java.lang.NullPointerException e = (java.lang.NullPointerException) __$generated_wrappedex$__.getCause();
        {
            
//#line 39
res1 = true;
        }
        }
        throw __$generated_wrappedex$__;
        }catch (final java.lang.ClassCastException e) {
            
//#line 37
res1 = true;
        }catch (final java.lang.NullPointerException e) {
            
//#line 39
res1 = true;
        }
        
//#line 44
try {{
            
//#line 47
final int case2a =
              new x10.core.fun.Fun_0_1<java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final java.lang.Integer __desugarer__var__433__) { return apply((int)__desugarer__var__433__);}
            public final int apply(final int __desugarer__var__433__) { {
                
//#line 47
if (!(((int) __desugarer__var__433__) ==
                                  ((int) 3))) {
                    
//#line 47
throw new java.lang.ClassCastException("x10.lang.Int{self==3}");
                }
                
//#line 47
return __desugarer__var__433__;
            }}
            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;if (i ==1) return x10.rtt.Types.INT;return null;
            }
            }.apply(((int) (int) 
                      ni.
                        value));
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.ClassCastException) {
        final java.lang.ClassCastException e = (java.lang.ClassCastException) __$generated_wrappedex$__.getCause();
        {
            
//#line 49
res2 = true;
        }
        }
        throw __$generated_wrappedex$__;
        }catch (final java.lang.ClassCastException e) {
            
//#line 49
res2 = true;
        }
        
//#line 53
try {{
            
//#line 56
final int case2b =
              new x10.core.fun.Fun_0_1<java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final java.lang.Integer __desugarer__var__434__) { return apply((int)__desugarer__var__434__);}
            public final int apply(final int __desugarer__var__434__) { {
                
//#line 56
if (!(((int) __desugarer__var__434__) ==
                                  ((int) 3))) {
                    
//#line 56
throw new java.lang.ClassCastException("x10.lang.Int{self==3}");
                }
                
//#line 56
return __desugarer__var__434__;
            }}
            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;if (i ==1) return x10.rtt.Types.INT;return null;
            }
            }.apply(((int) (int) 
                      nn.
                        value));
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.ClassCastException) {
        final java.lang.ClassCastException e = (java.lang.ClassCastException) __$generated_wrappedex$__.getCause();
        {
            
//#line 58
res2 &= true;
        }
        }
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.NullPointerException) {
        final java.lang.NullPointerException e = (java.lang.NullPointerException) __$generated_wrappedex$__.getCause();
        {
            
//#line 60
res2 &= true;
        }
        }
        throw __$generated_wrappedex$__;
        }catch (final java.lang.ClassCastException e) {
            
//#line 58
res2 &= true;
        }catch (final java.lang.NullPointerException e) {
            
//#line 60
res2 &= true;
        }
        
//#line 64
return res1 &&
        res2;
    }
    
    
//#line 68
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
    							UnboxNullablePrimitive.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$42423)  {
        
//#line 69
new UnboxNullablePrimitive().execute();
    }/* } */
    
    public UnboxNullablePrimitive() {
        super();
    }

}
