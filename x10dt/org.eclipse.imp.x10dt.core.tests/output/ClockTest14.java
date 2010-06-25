
public class ClockTest14
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClockTest14>_RTT = new x10.rtt.RuntimeType<ClockTest14>(
/* base class */ClockTest14.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 20
public boolean
                  run(
                  ){
        
//#line 21
final x10.
          lang.
          Clock c =
          x10.
          lang.
          Clock.make();
        
//#line 22
boolean gotException;
        
//#line 23
x10.
          lang.
          Runtime.next();
        
//#line 24
c.resume();
        
//#line 25
c.drop();
        
//#line 26
harness.
          x10Test.chk((boolean)(c.dropped()));
        
//#line 27
x10.
          lang.
          Runtime.next();
        
//#line 28
gotException = false;
        
//#line 29
try {{
            
//#line 30
c.resume();
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof x10.
          lang.
          ClockUseException) {
        x10.
          lang.
          ClockUseException e = (x10.
          lang.
          ClockUseException) __$generated_wrappedex$__.getCause();
        {
            
//#line 32
gotException = true;
        }
        }
        throw __$generated_wrappedex$__;
        }catch (x10.
                  lang.
                  ClockUseException e) {
            
//#line 32
gotException = true;
        }
        
//#line 34
harness.
          x10Test.chk((boolean)(gotException));
        
//#line 35
gotException = false;
        
//#line 36
try {{
            
//#line 37
x10.
              lang.
              Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                 lang.
                                 Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                 lang.
                                 Clock[] { c })/* } */,
                               new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                   
                               }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                               });
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof x10.
          lang.
          ClockUseException) {
        x10.
          lang.
          ClockUseException e = (x10.
          lang.
          ClockUseException) __$generated_wrappedex$__.getCause();
        {
            
//#line 39
gotException = true;
        }
        }
        throw __$generated_wrappedex$__;
        }catch (x10.
                  lang.
                  ClockUseException e) {
            
//#line 39
gotException = true;
        }
        
//#line 41
harness.
          x10Test.chk((boolean)(gotException));
        
//#line 42
return true;
    }
    
    
//#line 45
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
    							ClockTest14.main(args);
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
        
//#line 46
new ClockTest14().execute();
    }/* } */
    
    public ClockTest14() {
        super();
    }

}
