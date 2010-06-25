
public class ArrayAccessEqualRank5
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ArrayAccessEqualRank5>_RTT = new x10.rtt.RuntimeType<ArrayAccessEqualRank5>(
/* base class */ArrayAccessEqualRank5.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 27
public boolean
                  run(
                  ){
        
//#line 28
final x10.
          array.
          DistArray<java.lang.Integer> b =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<java.lang.Integer>make(x10.rtt.Types.INT,
                                            x10.
                                              array.
                                              Dist.makeConstant(x10.
                                                                  array.
                                                                  Region.makeRectangular((int)(0),
                                                                                         (int)(9)),
                                                                x10.
                                                                  lang.
                                                                  Runtime.here()),
                                            new x10.core.fun.Fun_0_1<x10.
                                              array.
                                              Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                              array.
                                              Point id$11145) { return apply(id$11145);}
                                            public final int apply(final x10.
                                              array.
                                              Point id$11145) { {
                                                
//#line 28
return 0;
                                            }}
                                            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                            }
                                            })));
        
//#line 29
try {{
            
//#line 29
x10.
              lang.
              Runtime.startFinish();
            {
                {
                    
//#line 29
final x10.
                      array.
                      Dist __desugarer__var__104__ =
                      ((x10.
                      array.
                      Dist)(b.
                              dist));
                    
//#line 29
/* template:forloop { */for (x10.core.Iterator __desugarer__var__105____ = (__desugarer__var__104__.places()).iterator(); __desugarer__var__105____.hasNext(); ) {
                    	final  x10.
                      lang.
                      Place __desugarer__var__105__ = (x10.
                      lang.
                      Place) __desugarer__var__105____.next$G();
                    	
{
                        
//#line 30
x10.
                          lang.
                          Runtime.runAsync(__desugarer__var__105__,
                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                               
//#line 29
/* template:forloop { */for (x10.core.Iterator x__ = (__desugarer__var__104__.restriction(x10.
                                                                                                                                                       lang.
                                                                                                                                                       Runtime.here())).iterator(); x__.hasNext(); ) {
                                               	final  x10.
                                                 array.
                                                 Point x = (x10.
                                                 array.
                                                 Point) x__.next$G();
                                               	final int i =
                                                 x.apply((int)(0));
{
                                                   
//#line 30
x10.
                                                     lang.
                                                     Runtime.runAsync(x10.
                                                                        lang.
                                                                        Runtime.here(),
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          
//#line 30
b.set$G((int)(i),
                                                                                              x);
                                                                      }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                      });
                                               }
                                               }/* } */
                                           }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                           });
                    }
                    }/* } */
                }
            }
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
        java.lang.Throwable __desugarer__var__106__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 29
x10.
              lang.
              Runtime.pushException(__desugarer__var__106__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__106__) {
            
//#line 29
x10.
              lang.
              Runtime.pushException(__desugarer__var__106__);
        }finally {{
             
//#line 29
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 31
return true;
        }
    
    
//#line 34
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
    							ArrayAccessEqualRank5.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$11146)  {
        
//#line 35
new ArrayAccessEqualRank5().execute();
    }/* } */
    
    public ArrayAccessEqualRank5() {
        super();
    }
    
    }
    