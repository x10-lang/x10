
public class ArrayAccessEqualRank
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ArrayAccessEqualRank>_RTT = new x10.rtt.RuntimeType<ArrayAccessEqualRank>(
/* base class */ArrayAccessEqualRank.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 24
public void
                  arrayEqual(
                  final x10.
                    array.
                    DistArray<java.lang.Integer> A,
                  final x10.
                    array.
                    DistArray<java.lang.Integer> B){
        
//#line 25
try {{
            
//#line 25
x10.
              lang.
              Runtime.startFinish();
            {
                {
                    
//#line 26
final x10.
                      array.
                      Dist __desugarer__var__78__ =
                      ((x10.
                      array.
                      Dist)(A.
                              dist));
                    
//#line 26
/* template:forloop { */for (x10.core.Iterator __desugarer__var__79____ = (__desugarer__var__78__.places()).iterator(); __desugarer__var__79____.hasNext(); ) {
                    	final  x10.
                      lang.
                      Place __desugarer__var__79__ = (x10.
                      lang.
                      Place) __desugarer__var__79____.next$G();
                    	
{
                        
//#line 26
x10.
                          lang.
                          Runtime.runAsync(__desugarer__var__79__,
                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                               
//#line 26
/* template:forloop { */for (x10.core.Iterator p__ = (__desugarer__var__78__.restriction(x10.
                                                                                                                                                      lang.
                                                                                                                                                      Runtime.here())).iterator(); p__.hasNext(); ) {
                                               	final  x10.
                                                 array.
                                                 Point p = (x10.
                                                 array.
                                                 Point) p__.next$G();
                                               	
{
                                                   
//#line 26
x10.
                                                     lang.
                                                     Runtime.runAsync(x10.
                                                                        lang.
                                                                        Runtime.here(),
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          
//#line 27
final int v =
                                                                            x10.
                                                                            lang.
                                                                            Runtime.<java.lang.Integer>evalAt$G(x10.rtt.Types.INT,
                                                                                                                B.
                                                                                                                  dist.apply(p),
                                                                                                                new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                                                                                                public final int apply() { {
                                                                                                                    
//#line 27
return B.apply$G(p);
                                                                                                                }}
                                                                                                                public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                                                                                                }
                                                                                                                });
                                                                          
//#line 28
harness.
                                                                            x10Test.chk((boolean)(((int) A.apply$G(p)) ==
                                                                                        ((int) v)));
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
        java.lang.Throwable __desugarer__var__80__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 25
x10.
              lang.
              Runtime.pushException(__desugarer__var__80__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__80__) {
            
//#line 25
x10.
              lang.
              Runtime.pushException(__desugarer__var__80__);
        }finally {{
             
//#line 25
x10.
               lang.
               Runtime.stopFinish();
         }}
        }
    
    
//#line 32
public boolean
                  run(
                  ){
        
//#line 34
final x10.
          array.
          Dist D =
          ((x10.
          array.
          Dist)(x10.
          array.
          Dist.make(((x10.
                      array.
                      Region)
                      x10.
                      array.
                      Region.makeRectangular((int)(0),
                                             (int)(9))))));
        
//#line 35
final x10.
          array.
          DistArray<java.lang.Integer> a =
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
                                              Point id$10282) { return apply(id$10282);}
                                            public final int apply(final x10.
                                              array.
                                              Point id$10282) { {
                                                
//#line 35
return 0;
                                            }}
                                            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                            }
                                            })));
        
//#line 36
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
                                              Point id$10283) { return apply(id$10283);}
                                            public final int apply(final x10.
                                              array.
                                              Point id$10283) { {
                                                
//#line 36
return 0;
                                            }}
                                            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                            }
                                            })));
        
//#line 37
this.arrayEqual(a,
                                    b);
        
//#line 38
return true;
    }
    
    
//#line 41
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
    							ArrayAccessEqualRank.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$10284)  {
        
//#line 42
new ArrayAccessEqualRank().execute();
    }/* } */
    
    public ArrayAccessEqualRank() {
        super();
    }
    
    }
    