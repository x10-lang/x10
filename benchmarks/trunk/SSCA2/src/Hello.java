
public class Hello
extends x10.core.Ref
{
public static class /* Join: { */RTT/* } */ extends x10.types.RuntimeType<Hello> {
public static final /* Join: { */RTT/* } */ it = new /* Join: { */RTT/* } */();
    
    
    public RTT() {super(Hello.class);
                      }
    public boolean instanceof$(java.lang.Object o) {
    if (! (o instanceof Hello)) return false;
        return true;
    }
    public java.util.List<x10.types.Type<?>> getTypeParameters() {
    return null;
    }
}

    
    
//#line 3
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
    							Hello.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> args)  {
        
//#line 4
if (((args.
                           length) > (0))) {
            
//#line 5
(x10.
              io.
              Console.OUT).println("The first arg is: " +
                                   args.apply(0));
        }
        
//#line 7
(x10.
          io.
          Console.OUT).println("Hello X10 world ");
        
//#line 8
final Hello h =
          new Hello();
        
//#line 9
boolean result =
          (h).myMethod();
        
//#line 10
(x10.
          io.
          Console.OUT).println("The answer is: " +
                               java.lang.String.valueOf(result));
    }/* } */
    
    
//#line 13
public java.lang.Boolean
                  myMethod(
                  ){
        
//#line 13
return true;
    }
    
    
//#line 2
public Hello() {
        
//#line 2
super();
    }
}
