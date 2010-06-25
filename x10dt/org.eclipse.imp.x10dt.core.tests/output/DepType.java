
public class DepType
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<DepType>_RTT = new x10.rtt.RuntimeType<DepType>(
/* base class */DepType.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 19
final public int
      i;
    
//#line 19
final public int
      j;
    
    
//#line 22
static class Test
                extends DepType
                {public static final x10.rtt.RuntimeType<DepType.
      Test>_RTT = new x10.rtt.RuntimeType<DepType.
      Test>(
    /* base class */DepType.
      Test.class
    , /* parents */ new x10.rtt.Type[] {DepType._RTT}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 22
final public int
          k;
        
        
        
//#line 23
Test(final int kk) {
            
//#line 24
super(3,
                              4);
            
//#line 25
this.k = kk;
        }
        
        final public int
          k(
          ){
            return this.
                     k;
        }
    
    }
    
    
//#line 29
static class Test2
                extends DepType
                {public static final x10.rtt.RuntimeType<DepType.
      Test2>_RTT = new x10.rtt.RuntimeType<DepType.
      Test2>(
    /* base class */DepType.
      Test2.class
    , /* parents */ new x10.rtt.Type[] {DepType._RTT}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
        
//#line 30
Test2() {
            
//#line 31
super(3,
                              5);
        }
    
    }
    
    
    
//#line 47
public DepType(final int i,
                               final int j) {
        
//#line 47
super();
        
//#line 48
this.i = i;
        
//#line 48
this.j = j;
    }
    
    
//#line 52
public static DepType
                  make(
                  final int i){
        
//#line 52
return new DepType(i,
                                       i);
    }
    
    
//#line 55
public boolean
                  run(
                  ){
        
//#line 56
final DepType d =
          ((DepType)(new DepType(3,
                                 6)));
        
//#line 57
return true;
    }
    
    
//#line 61
public boolean
                  run3(
                  ){
        
//#line 62
x10.
          io.
          Console.OUT.println((("i (=3?) = ") + (i)));
        
//#line 63
return true;
    }
    
    
//#line 65
public boolean
                  run4(
                  final int j){
        
//#line 66
x10.
          io.
          Console.OUT.println((("i (=3?) = ") + (i)));
        
//#line 67
return true;
    }
    
    
//#line 69
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
    							DepType.main(args);
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
        
//#line 70
new DepType(3,
                                9).execute();
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
