
public class FlattenConditional2
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<FlattenConditional2>_RTT = new x10.rtt.RuntimeType<FlattenConditional2>(
/* base class */FlattenConditional2.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 21
x10.
      array.
      Array<java.lang.Integer>
      a;
    
    
//#line 23
public FlattenConditional2() {
        
//#line 23
super();
        
//#line 21
this.a = null;
        
//#line 27
this.extra = 4;
        
//#line 24
this.a = ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Integer>(x10.rtt.Types.INT,
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
                                     Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                     array.
                                     Point id22320) { return apply(id22320);}
                                   public final int apply(final x10.
                                     array.
                                     Point id22320) { {
                                       
//#line 24
final int i =
                                         id22320.apply((int)(0));
                                       
//#line 24
final int j =
                                         id22320.apply((int)(1));
                                       
//#line 24
return ((((int)(i))) + (((int)(j))));
                                   }}
                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                   }
                                   })));
    }
    
    
//#line 27
int
      extra;
    
    
//#line 29
int
                  m(
                  int i){
        
//#line 30
if (((int) i) ==
                        ((int) 6)) {
            
//#line 30
throw new java.lang.Error();
        }
        
//#line 31
return i;
    }
    
    
//#line 34
public boolean
                  run(
                  ){
        
//#line 35
int x =
          ((int) a.apply$G((int)(1),
                           (int)(1))) ==
        ((int) 2)
          ? this.m((int)(java.lang.Integer)(a.apply$G((int)(2),
                                                      (int)(2))))
          : this.m((int)(java.lang.Integer)(a.apply$G((int)(3),
                                                      (int)(3))));
        
//#line 36
return ((int) x) ==
        ((int) 4);
    }
    
    
//#line 39
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
    							FlattenConditional2.main(args);
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
        
//#line 40
new FlattenConditional2().execute();
    }/* } */

}
