
class S1
extends x10.core.Struct
{public static final x10.rtt.RuntimeType<S1>_RTT = new x10.rtt.RuntimeType<S1>(
/* base class */S1.class
, /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class), x10.rtt.Types.runtimeType(x10.core.Struct.class)}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 18
final int
      x;
    
//#line 19
final int
      y;
    
    
//#line 21
public S1(final int a,
                          final int b) {
        
//#line 21
super();
        
//#line 21
this.x = a;
        
//#line 21
this.y = b;
    }
    
    
//#line 22
final public int
                  sum(
                  ){
        
//#line 22
return ((((int)(x))) + (((int)(y))));
    }

final public boolean structEquals(final java.lang.Object o) {
    if (!(o instanceof S1)) return false;
    if (!x10.rtt.Equality.equalsequals(this.x, ((S1) o).x)) return false;
    if (!x10.rtt.Equality.equalsequals(this.y, ((S1) o).y)) return false;
    return true;
    }

}

public class StructCall
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<StructCall>_RTT = new x10.rtt.RuntimeType<StructCall>(
/* base class */StructCall.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 27
public boolean
                  run(
                  ){
        
//#line 28
final S1 a =
          new S1(3,
                 4);
        
//#line 29
return ((int) a.sum()) ==
        ((int) 7);
    }
    
    
//#line 32
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
    							StructCall.main(args);
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
        
//#line 33
new StructCall().execute();
    }/* } */
    
    public StructCall() {
        super();
    }

}
