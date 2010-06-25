
public class ArrayIndexWithPoint
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ArrayIndexWithPoint>_RTT = new x10.rtt.RuntimeType<ArrayIndexWithPoint>(
/* base class */ArrayIndexWithPoint.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 21
public boolean
                  run(
                  ){
        
//#line 22
final x10.
          array.
          Region e =
          ((x10.
          array.
          Region)(x10.
          array.
          Region.makeRectangular((int)(1),
                                 (int)(10))));
        
//#line 23
final x10.
          array.
          Array<java.lang.Integer> ia =
          ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Integer>(x10.rtt.Types.INT,
                                   e,
                                   new x10.core.fun.Fun_0_1<x10.
                                     array.
                                     Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                     array.
                                     Point id$14090) { return apply(id$14090);}
                                   public final int apply(final x10.
                                     array.
                                     Point id$14090) { {
                                       
//#line 23
return 0;
                                   }}
                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                   }
                                   })));
        
//#line 24
for (
//#line 24
final x10.core.Iterator<x10.
                           array.
                           Point> p14162 =
                           ia.
                             region.iterator();
                         p14162.hasNext();
                         ) {
            
//#line 24
final x10.
              array.
              Point p =
              ((x10.
              array.
              Point)(p14162.next$G()));
            
//#line 25
harness.
              x10Test.chk((boolean)(((int) ia.apply$G(p)) ==
                          ((int) 0)));
        }
        
//#line 26
return true;
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
    							ArrayIndexWithPoint.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$14091)  {
        
//#line 30
new ArrayIndexWithPoint().execute();
    }/* } */
    
    public ArrayIndexWithPoint() {
        super();
    }

}
