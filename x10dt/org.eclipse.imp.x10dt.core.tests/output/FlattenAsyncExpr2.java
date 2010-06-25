
public class FlattenAsyncExpr2
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<FlattenAsyncExpr2>_RTT = new x10.rtt.RuntimeType<FlattenAsyncExpr2>(
/* base class */FlattenAsyncExpr2.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 25
final x10.
      array.
      DistArray<java.lang.Integer>
      a;
    
    
//#line 27
public FlattenAsyncExpr2() {
        
//#line 27
super();
        
//#line 28
this.a = ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<java.lang.Integer>make(x10.rtt.Types.INT,
                                            x10.
                                              array.
                                              Dist.makeConstant(x10.
                                                                  array.
                                                                  Region.makeRectangular((int)(1),
                                                                                         (int)(10)),
                                                                x10.
                                                                  lang.
                                                                  Runtime.here()),
                                            new x10.core.fun.Fun_0_1<x10.
                                              array.
                                              Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                              array.
                                              Point id21426) { return apply(id21426);}
                                            public final int apply(final x10.
                                              array.
                                              Point id21426) { {
                                                
//#line 28
final int j =
                                                  id21426.apply((int)(0));
                                                
//#line 28
return ((((int)(2))) * (((int)(j))));
                                            }}
                                            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                            }
                                            })));
    }
    
    
//#line 31
int
                  m(
                  final int x){
        
//#line 31
return x;
    }
    
    
//#line 34
public boolean
                  run(
                  ){
        
//#line 35
try {{
            
//#line 35
x10.
              lang.
              Runtime.startFinish();
            {
                
//#line 35
x10.
                  lang.
                  Runtime.runAsync(a.
                                     dist.apply((int)(1)),
                                   new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                       
//#line 36
FlattenAsyncExpr2.this.m((int)(50000));
                                       
//#line 37
try {{
                                           
//#line 37
x10.
                                             lang.
                                             Runtime.lock();
                                           {
                                               
//#line 37
a.set$G((int)((((((int)(a.apply$G((int)(1))))) ^ (((int)(2)))))),
                                                                   (int)(1));
                                           }
                                       }}finally {{
                                             
//#line 37
x10.
                                               lang.
                                               Runtime.release();
                                         }}
                                       }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                       });
                }
            }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
            if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
            java.lang.Throwable __desugarer__var__244__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 35
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__244__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__244__) {
                
//#line 35
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__244__);
            }finally {{
                 
//#line 35
x10.
                   lang.
                   Runtime.stopFinish();
             }}
            
//#line 39
return ((int) a.apply$G((int)(1))) ==
            ((int) ((((((int)(2))) ^ (((int)(2)))))));
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
        							FlattenAsyncExpr2.main(args);
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
        public static void main(final x10.core.Rail<java.lang.String> id$21428)  {
            
//#line 43
new FlattenAsyncExpr2().execute();
        }/* } */
    
    }
    