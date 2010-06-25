
public class StencilFor2D
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<StencilFor2D>_RTT = new x10.rtt.RuntimeType<StencilFor2D>(
/* base class */StencilFor2D.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 21
public boolean
                  run(
                  ){
        
//#line 23
final x10.
          array.
          Region R =
          ((x10.
          array.
          Region)(x10.
          array.
          Region.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                     array.
                                     Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                                     array.
                                     Region[] { x10.
                                     array.
                                     Region.makeRectangular((int)(-1),
                                                            (int)(256)),x10.
                                     array.
                                     Region.makeRectangular((int)(-1),
                                                            (int)(256)) })/* } */)));
        
//#line 24
final x10.
          array.
          Region r =
          ((x10.
          array.
          Region)(x10.
          array.
          Region.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                     array.
                                     Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                                     array.
                                     Region[] { x10.
                                     array.
                                     Region.makeRectangular((int)(0),
                                                            (int)(255)),x10.
                                     array.
                                     Region.makeRectangular((int)(0),
                                                            (int)(255)) })/* } */)));
        
//#line 25
final x10.
          array.
          Point north =
          ((x10.
          array.
          Point)(x10.
          array.
          Point.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { 0,1 })/* } */)));
        
//#line 26
final x10.
          array.
          Point south =
          ((x10.
          array.
          Point)(x10.
          array.
          Point.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { 0,-1 })/* } */)));
        
//#line 27
final x10.
          array.
          Point west =
          ((x10.
          array.
          Point)(x10.
          array.
          Point.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { -1,0 })/* } */)));
        
//#line 28
final x10.
          array.
          Point east =
          ((x10.
          array.
          Point)(x10.
          array.
          Point.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { 1,0 })/* } */)));
        
//#line 29
final x10.
          array.
          Array<java.lang.Double> A =
          ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Double>(x10.rtt.Types.DOUBLE,
                                  R,
                                  new x10.core.fun.Fun_0_1<x10.
                                    array.
                                    Point, java.lang.Double>() {public final java.lang.Double apply$G(final x10.
                                    array.
                                    Point id$27195) { return apply(id$27195);}
                                  public final double apply(final x10.
                                    array.
                                    Point id$27195) { {
                                      
//#line 29
return 0.0;
                                  }}
                                  public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.DOUBLE;return null;
                                  }
                                  })));
        
//#line 30
final double h =
          0.1;
        {
            
//#line 32
final x10.
              array.
              Region p27268 =
              ((x10.
              array.
              Region)(r));
            
//#line 32
final x10.core.Rail<java.lang.Integer> p27269 =
              ((x10.core.Rail)(x10.core.RailFactory.<java.lang.Integer>makeVarRail(x10.rtt.Types.INT, ((int)(2)))));
            
//#line 32
final int p27270min27271 =
              p27268.min((int)(1));
            
//#line 32
final int p27270max27272 =
              p27268.max((int)(1));
            
//#line 32
final int p27273min27274 =
              p27268.min((int)(0));
            
//#line 32
final int p27273max27275 =
              p27268.max((int)(0));
            
//#line 32
for (
//#line 32
int p27273 =
                               p27273min27274;
                             ((((int)(p27273))) <= (((int)(p27273max27275))));
                             
//#line 32
p27273 += 1) {
                
//#line 32
((int[])p27269.value)[0] = p27273;
                
//#line 32
for (
//#line 32
int p27270 =
                                   p27270min27271;
                                 ((((int)(p27270))) <= (((int)(p27270max27272))));
                                 
//#line 32
p27270 += 1) {
                    
//#line 32
((int[])p27269.value)[1] = p27270;
                    
//#line 32
final x10.
                      array.
                      Point p =
                      ((x10.
                      array.
                      Point)(x10.
                      array.
                      Point.make(p27269)));
                    {
                        
//#line 33
A.set$G((double)(((((double)((((((double)(((((double)(((((double)(((((double)(A.apply$G(p.$plus(north))))) + (((double)(A.apply$G(p.$plus(south))))))))) + (((double)(A.apply$G(p.$plus(west))))))))) + (((double)(A.apply$G(p.$plus(east))))))))) - (((double)(((((double)(((double)(int)(((int)(4))))))) * (((double)(A.apply$G(p))))))))))))) * (((double)(h))))),
                                            p);
                    }
                }
            }
        }
        
//#line 35
return true;
    }
    
    
//#line 38
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
    							StencilFor2D.main(args);
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
        
//#line 39
new StencilFor2D().execute();
    }/* } */
    
    public StencilFor2D() {
        super();
    }

}
