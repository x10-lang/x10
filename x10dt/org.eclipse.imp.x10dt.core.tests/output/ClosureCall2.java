
public class ClosureCall2
extends ClosureTest
{public static final x10.rtt.RuntimeType<ClosureCall2>_RTT = new x10.rtt.RuntimeType<ClosureCall2>(
/* base class */ClosureCall2.class
, /* parents */ new x10.rtt.Type[] {ClosureTest._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 25
java.lang.String
                  f(
                  final int x){
        
//#line 25
return "method";
    }
    
    
//#line 26
final x10.core.fun.Fun_0_1<java.lang.String,java.lang.String>
      f;
    
    
//#line 28
public boolean
                  run(
                  ){
        
//#line 30
this.check("f(1)",
                               this.f((int)(1)),
                               "method");
        
//#line 31
this.check("f(\"1\")",
                               f.apply$G("1"),
                               "closure");
        
//#line 33
return result;
    }
    
    
//#line 36
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
    							ClosureCall2.main(args);
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
        
//#line 37
new ClosureCall2().execute();
    }/* } */
    
    public ClosureCall2() {
        super();
        
//#line 26
this.f = ((x10.core.fun.Fun_0_1)(new x10.core.fun.Fun_0_1<java.lang.String, java.lang.String>() {public final java.lang.String apply$G(final java.lang.String x) { return apply(x);}
        public final java.lang.String apply(final java.lang.String x) { {
            
//#line 26
return "closure";
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.runtimeType(java.lang.String.class);if (i ==1) return x10.rtt.Types.runtimeType(java.lang.String.class);return null;
        }
        }));
    }

}
