
public class UnboxNullablePrimitiveConstrained
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<UnboxNullablePrimitiveConstrained>_RTT = new x10.rtt.RuntimeType<UnboxNullablePrimitiveConstrained>(
/* base class */UnboxNullablePrimitiveConstrained.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 20
public boolean
                  run(
                  ){
        
//#line 21
boolean res1 =
          false;
        
//#line 23
x10.
          util.
          Box<java.lang.Integer> ni =
          new x10.
          util.
          Box<java.lang.Integer>(x10.rtt.Types.INT,
                                 4);
        
//#line 24
x10.
          util.
          Box<java.lang.Integer> nn =
          null;
        
//#line 28
int case1a =
          ((int) (int) 
            ni.
              value);
        
//#line 30
try {{
            
//#line 32
int case1b =
              ((int) (int) 
                nn.
                  value);
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.ClassCastException) {
        java.lang.ClassCastException e = (java.lang.ClassCastException) __$generated_wrappedex$__.getCause();
        {
            
//#line 34
res1 = true;
        }
        }
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.NullPointerException) {
        final java.lang.NullPointerException e = (java.lang.NullPointerException) __$generated_wrappedex$__.getCause();
        {
            
//#line 36
res1 = true;
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.ClassCastException e) {
            
//#line 34
res1 = true;
        }catch (final java.lang.NullPointerException e) {
            
//#line 36
res1 = true;
        }
        
//#line 57
x10.
          util.
          Box<java.lang.Integer> case3a =
          ((x10.
            util.
            Box)
            ni);
        
//#line 60
x10.
          util.
          Box<java.lang.Integer> case3b =
          ((x10.
            util.
            Box)
            nn);
        
//#line 75
return res1;
    }
    
    
//#line 79
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
    							UnboxNullablePrimitiveConstrained.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$42559)  {
        
//#line 80
new UnboxNullablePrimitiveConstrained().execute();
    }/* } */
    
    public UnboxNullablePrimitiveConstrained() {
        super();
    }

}
