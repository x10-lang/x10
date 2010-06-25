
public class ConstructorsWithInferredTypes
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ConstructorsWithInferredTypes>_RTT = new x10.rtt.RuntimeType<ConstructorsWithInferredTypes>(
/* base class */ConstructorsWithInferredTypes.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 25
public boolean
                  run(
                  ){
        
//#line 26
final Spot s0 =
          ((Spot)(new x10.core.fun.Fun_0_1<Spot, Spot>() {public final Spot apply$G(final Spot __desugarer__var__439__) { return apply(__desugarer__var__439__);}
        public final Spot apply(final Spot __desugarer__var__439__) { {
            
//#line 26
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__439__,null))/* } */ &&
                              !(((int) __desugarer__var__439__.
                                         x) ==
                                ((int) 0))) {
                
//#line 26
throw new java.lang.ClassCastException("Spot{self.x==0}");
            }
            
//#line 26
return __desugarer__var__439__;
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return Spot._RTT;if (i ==1) return Spot._RTT;return null;
        }
        }.apply(((Spot)
                  new Spot()))));
        
//#line 27
final Spot s1 =
          ((Spot)(new x10.core.fun.Fun_0_1<Spot, Spot>() {public final Spot apply$G(final Spot __desugarer__var__440__) { return apply(__desugarer__var__440__);}
        public final Spot apply(final Spot __desugarer__var__440__) { {
            
//#line 27
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__440__,null))/* } */ &&
                              !(((int) __desugarer__var__440__.
                                         x) ==
                                ((int) 1))) {
                
//#line 27
throw new java.lang.ClassCastException("Spot{self.x==1}");
            }
            
//#line 27
return __desugarer__var__440__;
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return Spot._RTT;if (i ==1) return Spot._RTT;return null;
        }
        }.apply(((Spot)
                  new Spot(1)))));
        
//#line 29
return true;
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
    							ConstructorsWithInferredTypes.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$43742)  {
        
//#line 33
new ConstructorsWithInferredTypes().execute();
    }/* } */
    
    public ConstructorsWithInferredTypes() {
        super();
    }

}

class Spot
extends x10.core.Ref
{public static final x10.rtt.RuntimeType<Spot>_RTT = new x10.rtt.RuntimeType<Spot>(
/* base class */Spot.class
, /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 37
final public int
      x;
    
    
    
//#line 38
Spot() {
        
//#line 38
super();
        
//#line 38
this.x = 0;
    }
    
    
//#line 39
Spot(final int xx) {
        
//#line 39
super();
        
//#line 39
this.x = xx;
    }
    
    final public int
      x(
      ){
        return this.
                 x;
    }

}
