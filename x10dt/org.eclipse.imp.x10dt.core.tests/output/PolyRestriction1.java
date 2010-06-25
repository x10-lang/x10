class PolyRestriction1
extends TestArray
{public static final x10.rtt.RuntimeType<PolyRestriction1>_RTT = new x10.rtt.RuntimeType<PolyRestriction1>(
/* base class */PolyRestriction1.class
, /* parents */ new x10.rtt.Type[] {TestArray._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 19
public boolean
                  run(
                  ){
        
//#line 21
final x10.
          array.
          Region r1 =
          ((x10.
          array.
          Region)(x10.
          array.
          Region.makeRectangular(x10.core.RailFactory.<java.lang.Integer>makeRailFromValRail(x10.rtt.Types.INT, ((x10.core.ValRail)
                                                                                                                  /* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { 0,0 })/* } */)),
                                 x10.core.RailFactory.<java.lang.Integer>makeRailFromValRail(x10.rtt.Types.INT, ((x10.core.ValRail)
                                                                                                                  /* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { 5,5 })/* } */)))));
        
//#line 22
final x10.
          array.
          DistArray<java.lang.Double> a1 =
          ((x10.
          array.
          DistArray)(this.prArray("whole array",
                                  r1)));
        
//#line 24
final x10.
          array.
          Region r2 =
          ((x10.
          array.
          Region)(x10.
          array.
          Region.makeRectangular(x10.core.RailFactory.<java.lang.Integer>makeRailFromValRail(x10.rtt.Types.INT, /* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { 1,2 })/* } */),
                                 x10.core.RailFactory.<java.lang.Integer>makeRailFromValRail(x10.rtt.Types.INT, /* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { 3,4 })/* } */))));
        
//#line 25
final x10.
          array.
          DistArray<java.lang.Double> a2 =
          ((x10.
          array.
          DistArray)(a1.restriction(r2)));
        
//#line 26
this.prArray("restricted array",
                                 a2);
        
//#line 28
for (
//#line 28
final x10.core.Iterator<x10.
                           array.
                           Point> x26829 =
                           a2.region().iterator();
                         x26829.hasNext();
                         ) {
            
//#line 28
final x10.
              array.
              Point x =
              ((x10.
              array.
              Point)(x26829.next$G()));
            
//#line 29
a2.set$G((double)(7.0),
                                 (int)(x.apply((int)(0))),
                                 (int)(x.apply((int)(1))));
        }
        
//#line 31
this.prArray("whole array modified",
                                 a1);
        
//#line 33
return this.status();
    }
    
    
//#line 36
java.lang.String
                  expected(
                  ){
        
//#line 37
return (((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("--- PolyRestriction1: whole array\n") + ("rank 2\n"))) + ("rect true\n"))) + ("zeroBased true\n"))) + ("rail false\n"))) + ("isConvex() true\n"))) + ("size() 36\n"))) + ("region: [0..5,0..5]\n"))) + ("  poly\n"))) + ("    0  0 0 0 0 0 0 . . . . \n"))) + ("    1  0 1 2 3 4 5 . . . . \n"))) + ("    2  0 2 4 6 8 0 . . . . \n"))) + ("    3  0 3 6 9 2 5 . . . . \n"))) + ("    4  0 4 8 2 6 0 . . . . \n"))) + ("    5  0 5 0 5 0 5 . . . . \n"))) + ("  iterator\n"))) + ("    0  0 0 0 0 0 0 . . . . \n"))) + ("    1  0 1 2 3 4 5 . . . . \n"))) + ("    2  0 2 4 6 8 0 . . . . \n"))) + ("    3  0 3 6 9 2 5 . . . . \n"))) + ("    4  0 4 8 2 6 0 . . . . \n"))) + ("    5  0 5 0 5 0 5 . . . . \n"))) + ("--- PolyRestriction1: restricted array\n"))) + ("rank 2\n"))) + ("rect true\n"))) + ("zeroBased false\n"))) + ("rail false\n"))) + ("isConvex() true\n"))) + ("size() 9\n"))) + ("region: [1..3,2..4]\n"))) + ("  poly\n"))) + ("    1  . . 2 3 4 . . . . . \n"))) + ("    2  . . 4 6 8 . . . . . \n"))) + ("    3  . . 6 9 2 . . . . . \n"))) + ("  iterator\n"))) + ("    1  . . 2 3 4 . . . . . \n"))) + ("    2  . . 4 6 8 . . . . . \n"))) + ("    3  . . 6 9 2 . . . . . \n"))) + ("--- PolyRestriction1: whole array modified\n"))) + ("rank 2\n"))) + ("rect true\n"))) + ("zeroBased true\n"))) + ("rail false\n"))) + ("isConvex() true\n"))) + ("size() 36\n"))) + ("region: [0..5,0..5]\n"))) + ("  poly\n"))) + ("    0  0 0 0 0 0 0 . . . . \n"))) + ("    1  0 1 7 7 7 5 . . . . \n"))) + ("    2  0 2 7 7 7 0 . . . . \n"))) + ("    3  0 3 7 7 7 5 . . . . \n"))) + ("    4  0 4 8 2 6 0 . . . . \n"))) + ("    5  0 5 0 5 0 5 . . . . \n"))) + ("  iterator\n"))) + ("    0  0 0 0 0 0 0 . . . . \n"))) + ("    1  0 1 7 7 7 5 . . . . \n"))) + ("    2  0 2 7 7 7 0 . . . . \n"))) + ("    3  0 3 7 7 7 5 . . . . \n"))) + ("    4  0 4 8 2 6 0 . . . . \n"))) + ("    5  0 5 0 5 0 5 . . . . \n"));
    }
    
    
//#line 98
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
    							PolyRestriction1.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$26226)  {
        
//#line 99
new PolyRestriction1().execute();
    }/* } */
    
    public PolyRestriction1() {
        super();
    }

}
