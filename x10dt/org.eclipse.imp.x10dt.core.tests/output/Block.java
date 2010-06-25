
public class Block
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<Block>_RTT = new x10.rtt.RuntimeType<Block>(
/* base class */Block.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 18
public boolean
                  run(
                  ){
        
//#line 19
x10.
          array.
          Region r =
          ((x10.
          array.
          Region)(new x10.core.fun.Fun_0_1<x10.
          array.
          Region, x10.
          array.
          Region>() {public final x10.
          array.
          Region apply$G(final x10.
          array.
          Region __desugarer__var__366__) { return apply(__desugarer__var__366__);}
        public final x10.
          array.
          Region apply(final x10.
          array.
          Region __desugarer__var__366__) { {
            
//#line 19
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__366__,null))/* } */ &&
                              !(((int) __desugarer__var__366__.
                                         rank) ==
                                ((int) 1) &&
                                ((boolean) __desugarer__var__366__.
                                             zeroBased) ==
                                ((boolean) true) &&
                                ((boolean) __desugarer__var__366__.
                                             rect) ==
                                ((boolean) true))) {
                
//#line 19
throw new java.lang.ClassCastException(("x10.array.Region{self.rank==1, self.zeroBased==true, self.re" +
                                                                    "ct==true}"));
            }
            
//#line 19
return __desugarer__var__366__;
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Region._RTT;if (i ==1) return x10.array.Region._RTT;return null;
        }
        }.apply(((x10.
                  array.
                  Region)
                  x10.
                  array.
                  Region.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                             array.
                                             Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                                             array.
                                             Region[] { x10.
                                             array.
                                             Region.makeRectangular((int)(0),
                                                                    (int)(9)) })/* } */)))));
        
//#line 20
x10.
          array.
          Dist d =
          ((x10.
          array.
          Dist)(x10.
          array.
          Dist.makeBlock(r,
                         (int)(0))));
        
//#line 21
return true;
    }
    
    
//#line 24
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
    							Block.main(args);
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
        
//#line 25
new Block().execute();
    }/* } */
    
    public Block() {
        super();
    }

}
