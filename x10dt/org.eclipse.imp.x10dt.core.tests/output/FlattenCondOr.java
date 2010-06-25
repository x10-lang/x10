
public class FlattenCondOr
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<FlattenCondOr>_RTT = new x10.rtt.RuntimeType<FlattenCondOr>(
/* base class */FlattenCondOr.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 33
final x10.
      array.
      Array<java.lang.Boolean>
      a;
    
    
//#line 35
public FlattenCondOr() {
        
//#line 35
super();
        
//#line 36
this.a = ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Boolean>(x10.rtt.Types.BOOLEAN,
                                   x10.
                                     array.
                                     Region.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                array.
                                                                Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                                                                array.
                                                                Region[] { x10.
                                                                array.
                                                                Region.makeRectangular((int)(1),
                                                                                       (int)(10)),x10.
                                                                array.
                                                                Region.makeRectangular((int)(1),
                                                                                       (int)(10)) })/* } */),
                                   new x10.core.fun.Fun_0_1<x10.
                                     array.
                                     Point, java.lang.Boolean>() {public final java.lang.Boolean apply$G(final x10.
                                     array.
                                     Point id22678) { return apply(id22678);}
                                   public final boolean apply(final x10.
                                     array.
                                     Point id22678) { {
                                       
//#line 36
final int i =
                                         id22678.apply((int)(0));
                                       
//#line 36
final int j =
                                         id22678.apply((int)(1));
                                       
//#line 36
return true;
                                   }}
                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.BOOLEAN;return null;
                                   }
                                   })));
    }
    
    
//#line 39
boolean
                  m(
                  final boolean x){
        
//#line 39
return x;
    }
    
    
//#line 41
public boolean
                  run(
                  ){
        
//#line 42
final boolean x =
          this.m((boolean)(java.lang.Boolean)(a.apply$G((int)(1),
                                                        (int)(1)))) ||
        a.apply$G((int)(0),
                  (int)(0));
        
//#line 43
return x;
    }
    
    
//#line 46
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
    							FlattenCondOr.main(args);
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
        
//#line 47
new FlattenCondOr().execute();
    }/* } */

}
