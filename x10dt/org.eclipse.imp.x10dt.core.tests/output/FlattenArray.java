
public class FlattenArray
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<FlattenArray>_RTT = new x10.rtt.RuntimeType<FlattenArray>(
/* base class */FlattenArray.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 28
x10.
      array.
      Array<java.lang.Integer>
      a;
    
    
//#line 30
public FlattenArray() {
        
//#line 30
super();
        
//#line 28
this.a = null;
        
//#line 31
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
                                     Point id20540) { return apply(id20540);}
                                   public final int apply(final x10.
                                     array.
                                     Point id20540) { {
                                       
//#line 31
final int i =
                                         id20540.apply((int)(0));
                                       
//#line 31
final int j =
                                         id20540.apply((int)(1));
                                       
//#line 31
return ((((int)(i))) + (((int)(j))));
                                   }}
                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                   }
                                   })));
    }
    
    
//#line 34
int
                  m(
                  int x){
        
//#line 35
return x;
    }
    
    
//#line 38
public boolean
                  run(
                  ){
        
//#line 39
int x =
          ((((int)(this.m((int)(3))))) + (((int)(this.m((int)(java.lang.Integer)(a.apply$G((int)(1),
                                                                                           (int)(1))))))));
        
//#line 40
int y =
          ((((int)(this.m((int)(4))))) + (((int)(this.m((int)(java.lang.Integer)(a.apply$G((int)(2),
                                                                                           (int)(2))))))));
        
//#line 41
int z;
        
//#line 42
if (((int) y) ==
                        ((int) 0)) {
            
//#line 43
z = ((((int)(this.m((int)(4))))) + (((int)(this.m((int)(java.lang.Integer)(a.apply$G((int)(java.lang.Integer)(a.apply$G((int)(0),
                                                                                                                                                (int)(0))),
                                                                                                             (int)(2))))))));
        } else {
            
//#line 45
z = ((((int)(this.m((int)(5))))) + (((int)(this.m((int)(4))))));
        }
        
//#line 47
return ((int) z) ==
        ((int) ((((int)(5))) + (((int)(4)))));
    }
    
    
//#line 50
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
    							FlattenArray.main(args);
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
        
//#line 51
new FlattenArray().execute();
    }/* } */

}
