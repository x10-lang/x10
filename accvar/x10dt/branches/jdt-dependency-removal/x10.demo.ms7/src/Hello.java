
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

    
    
//#line 4
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
    			x10.runtime.Runtime.start(
    				// body of main activity
    				new x10.core.fun.VoidFun_0_0() { 
    					public void apply() {
    						// preload classes
    						if (Boolean.getBoolean("x10.PRELOAD_CLASSES")) {
    							x10.runtime.impl.java.PreLoader.preLoad(this.getClass().getEnclosingClass(), Boolean.getBoolean("x10.PRELOAD_STRINGS"));
    						}
    
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
    			if (t instanceof x10.lang.MultipleExceptions) {
    				x10.core.ValRail<Throwable> exceptions = ((x10.lang.MultipleExceptions) t).exceptions;
    				for(int i = 0; i < exceptions.length; i++) {
    					exceptions.get(i).printStackTrace();
    				}
    			}
    		}
    	}
    }
    
    // the original app-main method
    public static void main(x10.core.Rail<java.lang.String> args)  {
        
//#line 5
(x10.
          io.
          Console.OUT).println("Hello X10 world");
        
//#line 6
Hello h =
          new Hello();
        
//#line 7
boolean myBool =
          (/* template:place-check { */((Hello) x10.runtime.Runtime.placeCheck(x10.runtime.Runtime.here(), h))/* } */).myMethod();
        
//#line 8
(x10.
          io.
          Console.OUT).println("The answer is: " +
                               myBool);
    }/* } */
    
    
//#line 12
public java.lang.Boolean
                  myMethod(
                  ){
        
//#line 13
return true;
    }
    
    
//#line 3
public Hello() {
        
//#line 3
super();
    }
}
