
public class Float_ConstraintDeclaredAsFloat
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<Float_ConstraintDeclaredAsFloat>_RTT = new x10.rtt.RuntimeType<Float_ConstraintDeclaredAsFloat>(
/* base class */Float_ConstraintDeclaredAsFloat.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 21
public boolean
                  run(
                  ){
        
//#line 22
float j =
          1.0E-5F;
        
//#line 24
float i =
          2.0E-5F;
        
//#line 25
i = new x10.core.fun.Fun_0_1<java.lang.Float, java.lang.Float>() {public final java.lang.Float apply$G(final java.lang.Float __desugarer__var__414__) { return apply((float)__desugarer__var__414__);}
        public final float apply(final float __desugarer__var__414__) { {
            
//#line 25
if (!(((float) __desugarer__var__414__) ==
                              ((float) 2.0E-5F))) {
                
//#line 25
throw new java.lang.ClassCastException("x10.lang.Float{self==2.0E-5F}");
            }
            
//#line 25
return __desugarer__var__414__;
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.FLOAT;if (i ==1) return x10.rtt.Types.FLOAT;return null;
        }
        }.apply(((float) (float) 
                  (((((float)(j))) * (((float)(((float)(int)(((int)(2)))))))))));
        
//#line 26
return (((((float) j) ==
                              ((float) 1.0E-5F))) &&
                            ((((float) i) ==
                              ((float) 2.0E-5F))));
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
    							Float_ConstraintDeclaredAsFloat.main(args);
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
new Float_ConstraintDeclaredAsFloat().execute();
    }/* } */
    
    public Float_ConstraintDeclaredAsFloat() {
        super();
    }

}
