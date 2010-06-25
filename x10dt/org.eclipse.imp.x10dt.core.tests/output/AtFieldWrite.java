
public class AtFieldWrite
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<AtFieldWrite>_RTT = new x10.rtt.RuntimeType<AtFieldWrite>(
/* base class */AtFieldWrite.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 18
AtFieldWrite.
      T
      t;
    
    
//#line 19
public boolean
                  run(
                  ){
        
//#line 20
final x10.
          lang.
          Place Second =
          x10.
          lang.
          Place.FIRST_PLACE.next();
        
//#line 21
final AtFieldWrite.
          T newT =
          ((AtFieldWrite.
          T)(x10.
          lang.
          Runtime.<AtFieldWrite.
          T>evalAt$G(AtFieldWrite.T._RTT,
                     Second,
                     new x10.core.fun.Fun_0_0<AtFieldWrite.
                       T>() {public final AtFieldWrite.
                       T apply$G() { return apply();}
                     public final AtFieldWrite.
                       T apply() { {
                         
//#line 21
return new AtFieldWrite.
                           T();
                     }}
                     public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return AtFieldWrite.T._RTT;return null;
                     }
                     })));
        
//#line 22
x10.
          lang.
          Runtime.runAt(Second,
                        new x10.core.fun.VoidFun_0_0() {public final void apply() { {
                            
//#line 23
newT.i = 3;
                        }}
                        });
        
//#line 25
return true;
    }
    
    
//#line 28
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
    							AtFieldWrite.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$31487)  {
        
//#line 29
new AtFieldWrite().execute();
    }/* } */
    
    
//#line 32
static class T
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<AtFieldWrite.
      T>_RTT = new x10.rtt.RuntimeType<AtFieldWrite.
      T>(
    /* base class */AtFieldWrite.
      T.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 33
public int
          i;
        
        public T() {
            super();
            
//#line 33
this.i = 0;
        }
    
    }
    
    
    public AtFieldWrite() {
        super();
        
//#line 18
this.t = null;
    }

}
