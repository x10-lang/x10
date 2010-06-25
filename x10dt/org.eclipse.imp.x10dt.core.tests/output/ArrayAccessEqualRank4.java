
public class ArrayAccessEqualRank4
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ArrayAccessEqualRank4>_RTT = new x10.rtt.RuntimeType<ArrayAccessEqualRank4>(
/* base class */ArrayAccessEqualRank4.class
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
          Region R =
          ((x10.
          array.
          Region)(x10.
          array.
          Region.make((int)(0),
                      (int)(9))));
        
//#line 29
final x10.
          array.
          Dist D =
          ((x10.
          array.
          Dist)(x10.
          array.
          Dist.makeConstant(R)));
        
//#line 30
final x10.
          array.
          DistArray<java.lang.Integer> b =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<java.lang.Integer>make(x10.rtt.Types.INT,
                                            D,
                                            new x10.core.fun.Fun_0_1<x10.
                                              array.
                                              Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                              array.
                                              Point id$10973) { return apply(id$10973);}
                                            public final int apply(final x10.
                                              array.
                                              Point id$10973) { {
                                                
//#line 30
return 0;
                                            }}
                                            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                            }
                                            })));
        
//#line 31
try {{
            
//#line 31
x10.
              lang.
              Runtime.startFinish();
            {
                {
                    
//#line 31
final x10.
                      array.
                      Dist __desugarer__var__98__ =
                      ((x10.
                      array.
                      Dist)(D));
                    
//#line 31
/* template:forloop { */for (x10.core.Iterator __desugarer__var__99____ = (__desugarer__var__98__.places()).iterator(); __desugarer__var__99____.hasNext(); ) {
                    	final  x10.
                      lang.
                      Place __desugarer__var__99__ = (x10.
                      lang.
                      Place) __desugarer__var__99____.next$G();
                    	
{
                        
//#line 31
x10.
                          lang.
                          Runtime.runAsync(__desugarer__var__99__,
                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                               
//#line 31
/* template:forloop { */for (x10.core.Iterator x__ = (__desugarer__var__98__.restriction(x10.
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
                                                   
//#line 31
x10.
                                                     lang.
                                                     Runtime.runAsync(x10.
                                                                        lang.
                                                                        Runtime.here(),
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          
//#line 31
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
        java.lang.Throwable __desugarer__var__100__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 31
x10.
              lang.
              Runtime.pushException(__desugarer__var__100__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__100__) {
            
//#line 31
x10.
              lang.
              Runtime.pushException(__desugarer__var__100__);
        }finally {{
             
//#line 31
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 32
return true;
        }
    
    
//#line 35
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
    							ArrayAccessEqualRank4.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$10974)  {
        
//#line 36
new ArrayAccessEqualRank4().execute();
    }/* } */
    
    public ArrayAccessEqualRank4() {
        super();
    }
    
    }
    