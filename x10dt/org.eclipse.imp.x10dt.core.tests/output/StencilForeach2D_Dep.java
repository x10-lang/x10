
public class StencilForeach2D_Dep
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<StencilForeach2D_Dep>_RTT = new x10.rtt.RuntimeType<StencilForeach2D_Dep>(
/* base class */StencilForeach2D_Dep.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 23
public boolean
                  run(
                  ){
        
//#line 24
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
        
//#line 25
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
        
//#line 26
final x10.
          array.
          Point north =
          ((x10.
          array.
          Point)(x10.
          array.
          Point.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { 0,1 })/* } */)));
        
//#line 27
final x10.
          array.
          Point south =
          ((x10.
          array.
          Point)(x10.
          array.
          Point.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { 0,-1 })/* } */)));
        
//#line 28
final x10.
          array.
          Point west =
          ((x10.
          array.
          Point)(x10.
          array.
          Point.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { -1,0 })/* } */)));
        
//#line 29
final x10.
          array.
          Point east =
          ((x10.
          array.
          Point)(x10.
          array.
          Point.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { 1,0 })/* } */)));
        
//#line 30
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
                                    Point id$27563) { return apply(id$27563);}
                                  public final double apply(final x10.
                                    array.
                                    Point id$27563) { {
                                      
//#line 30
return 0.0;
                                  }}
                                  public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.DOUBLE;return null;
                                  }
                                  })));
        
//#line 31
final double h =
          0.1;
        
//#line 33
try {{
            
//#line 33
x10.
              lang.
              Runtime.startFinish();
            {
                
//#line 33
/* template:forloop { */for (x10.core.Iterator p__ = (r).iterator(); p__.hasNext(); ) {
                	final  x10.
                  array.
                  Point p = (x10.
                  array.
                  Point) p__.next$G();
                	
{
                    
//#line 34
x10.
                      lang.
                      Runtime.runAsync(x10.
                                         lang.
                                         Runtime.here(),
                                       new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                           
//#line 34
A.set$G((double)(((((double)((((((double)(((((double)(((((double)(((((double)(A.apply$G(p.$plus(north))))) + (((double)(A.apply$G(p.$plus(south))))))))) + (((double)(A.apply$G(p.$plus(west))))))))) + (((double)(A.apply$G(p.$plus(east))))))))) - (((double)(((((double)(((double)(int)(((int)(4))))))) * (((double)(A.apply$G(p))))))))))))) * (((double)(h))))),
                                                               p);
                                       }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                       });
                }
                }/* } */
            }
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
        java.lang.Throwable __desugarer__var__254__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 33
x10.
              lang.
              Runtime.pushException(__desugarer__var__254__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__254__) {
            
//#line 33
x10.
              lang.
              Runtime.pushException(__desugarer__var__254__);
        }finally {{
             
//#line 33
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 36
return true;
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
    							StencilForeach2D_Dep.main(args);
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
new StencilForeach2D_Dep().execute();
    }/* } */
    
    public StencilForeach2D_Dep() {
        super();
    }
    
    }
    