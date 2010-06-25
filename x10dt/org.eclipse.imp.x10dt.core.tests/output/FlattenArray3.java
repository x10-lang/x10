
public class FlattenArray3
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<FlattenArray3>_RTT = new x10.rtt.RuntimeType<FlattenArray3>(
/* base class */FlattenArray3.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 26
x10.
      array.
      Array<java.lang.Integer>
      a;
    
    
//#line 28
public FlattenArray3() {
        
//#line 28
super();
        
//#line 26
this.a = null;
        
//#line 29
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
                                     Point id20900) { return apply(id20900);}
                                   public final int apply(final x10.
                                     array.
                                     Point id20900) { {
                                       
//#line 29
final int i =
                                         id20900.apply((int)(0));
                                       
//#line 29
final int j =
                                         id20900.apply((int)(1));
                                       
//#line 29
return ((((int)(i))) + (((int)(j))));
                                   }}
                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                   }
                                   })));
    }
    
    
//#line 32
int
                  m(
                  int x){
        
//#line 33
return x;
    }
    
    
//#line 36
public boolean
                  run(
                  ){
        
//#line 37
int x =
          this.m((int)(java.lang.Integer)(a.apply$G((int)(1),
                                                    (int)(1))));
        
//#line 38
int y =
          this.m((int)(java.lang.Integer)(a.apply$G((int)(2),
                                                    (int)(2))));
        
//#line 39
return true;
    }
    
    
//#line 42
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
    							FlattenArray3.main(args);
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
        
//#line 43
new FlattenArray3().execute();
    }/* } */

}
