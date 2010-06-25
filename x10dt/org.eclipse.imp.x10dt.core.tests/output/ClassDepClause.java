
public class ClassDepClause
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClassDepClause>_RTT = new x10.rtt.RuntimeType<ClassDepClause>(
/* base class */ClassDepClause.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 19
final public int
      i;
    
//#line 19
final public int
      j;
    
    
    
    
//#line 21
public ClassDepClause(final int i,
                                      final int j) {
        
//#line 21
super();
        
//#line 21
this.i = i;
        
//#line 21
this.j = j;
    }
    
    
//#line 22
public boolean
                  run(
                  ){
        
//#line 23
ClassDepClause x =
          ((ClassDepClause)(new ClassDepClause(2,
                                               2)));
        
//#line 24
return true;
    }
    
    
//#line 27
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
    							ClassDepClause.main(args);
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
        
//#line 28
new ClassDepClause(1,
                                       1).execute();
    }/* } */
    
    final public int
      i(
      ){
        return this.
                 i;
    }
    
    final public int
      j(
      ){
        return this.
                 j;
    }

}
