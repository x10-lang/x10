
public class AsyncTest2
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<AsyncTest2>_RTT = new x10.rtt.RuntimeType<AsyncTest2>(
/* base class */AsyncTest2.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 21
public boolean
                  run(
                  ){
        
//#line 22
final int NP =
          x10.runtime.impl.java.Runtime.MAX_PLACES;
        
//#line 23
final x10.
          array.
          DistArray<java.lang.Integer> A =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<java.lang.Integer>make(x10.rtt.Types.INT,
                                            x10.
                                              array.
                                              Dist.makeUnique())));
        
//#line 24
try {{
            
//#line 24
x10.
              lang.
              Runtime.startFinish();
            {
                {
                    
//#line 25
final int k30269min30270 =
                      0;
                    
//#line 25
final int k30269max30271 =
                      ((((int)(NP))) - (((int)(1))));
                    
//#line 25
for (
//#line 25
int k30269 =
                                       k30269min30270;
                                     ((((int)(k30269))) <= (((int)(k30269max30271))));
                                     
//#line 25
k30269 += 1) {
                        
//#line 25
final int k =
                          k30269;
                        {
                            
//#line 26
x10.
                              lang.
                              Runtime.runAsync(A.
                                                 dist.apply((int)(k)),
                                               new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                   {
                                                       
//#line 27
final x10.
                                                         array.
                                                         Dist __desugarer__var__299__ =
                                                         ((x10.
                                                         array.
                                                         Dist)(A.
                                                                 dist));
                                                       
//#line 27
/* template:forloop { */for (x10.core.Iterator __desugarer__var__300____ = (__desugarer__var__299__.places()).iterator(); __desugarer__var__300____.hasNext(); ) {
                                                       	final  x10.
                                                         lang.
                                                         Place __desugarer__var__300__ = (x10.
                                                         lang.
                                                         Place) __desugarer__var__300____.next$G();
                                                       	
{
                                                           
//#line 28
x10.
                                                             lang.
                                                             Runtime.runAsync(__desugarer__var__300__,
                                                                              new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                  
//#line 27
/* template:forloop { */for (x10.core.Iterator id30197__ = (__desugarer__var__299__.restriction(x10.
                                                                                                                                                                                                lang.
                                                                                                                                                                                                Runtime.here())).iterator(); id30197__.hasNext(); ) {
                                                                                  	final  x10.
                                                                                    array.
                                                                                    Point id30197 = (x10.
                                                                                    array.
                                                                                    Point) id30197__.next$G();
                                                                                  	final int i =
                                                                                    id30197.apply((int)(0));
{
                                                                                      
//#line 28
x10.
                                                                                        lang.
                                                                                        Runtime.runAsync(x10.
                                                                                                           lang.
                                                                                                           Runtime.here(),
                                                                                                         new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                                             
//#line 28
try {{
                                                                                                                 
//#line 28
x10.
                                                                                                                   lang.
                                                                                                                   Runtime.lock();
                                                                                                                 {
                                                                                                                     
//#line 28
new x10.core.fun.Fun_0_3<x10.
                                                                                                                       array.
                                                                                                                       DistArray<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                                                                                                       array.
                                                                                                                       DistArray<java.lang.Integer> x,final java.lang.Integer y0,final java.lang.Integer z) { return apply(x,(int)y0,(int)z);}
                                                                                                                     public final int apply(final x10.
                                                                                                                       array.
                                                                                                                       DistArray<java.lang.Integer> x, final int y0, final int z) { {
                                                                                                                         
//#line 28
return x.set$G((int)(((((int)(x.apply$G((int)(y0))))) + (((int)(z))))),
                                                                                                                                                    (int)(y0));
                                                                                                                     }}
                                                                                                                     public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.INT);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;return null;
                                                                                                                     }
                                                                                                                     }.apply(A,
                                                                                                                             i,
                                                                                                                             i);
                                                                                                                 }
                                                                                                             }}finally {{
                                                                                                                   
//#line 28
x10.
                                                                                                                     lang.
                                                                                                                     Runtime.release();
                                                                                                               }}
                                                                                                             }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                                             });
                                                                                      }
                                                                                      }/* } */
                                                                                  }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                  });
                                                           }
                                                           }/* } */
                                                       }
                                                   }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                   });
                            }
                        }
                    }
                }
            }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
            if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
            java.lang.Throwable __desugarer__var__301__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 24
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__301__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__301__) {
                
//#line 24
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__301__);
            }finally {{
                 
//#line 24
x10.
                   lang.
                   Runtime.stopFinish();
             }}
            
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
                          Dist __desugarer__var__302__ =
                          ((x10.
                          array.
                          Dist)(A.
                                  dist));
                        
//#line 29
/* template:forloop { */for (x10.core.Iterator __desugarer__var__303____ = (__desugarer__var__302__.places()).iterator(); __desugarer__var__303____.hasNext(); ) {
                        	final  x10.
                          lang.
                          Place __desugarer__var__303__ = (x10.
                          lang.
                          Place) __desugarer__var__303____.next$G();
                        	
{
                            
//#line 29
x10.
                              lang.
                              Runtime.runAsync(__desugarer__var__303__,
                                               new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                   
//#line 29
/* template:forloop { */for (x10.core.Iterator id30198__ = (__desugarer__var__302__.restriction(x10.
                                                                                                                                                                 lang.
                                                                                                                                                                 Runtime.here())).iterator(); id30198__.hasNext(); ) {
                                                   	final  x10.
                                                     array.
                                                     Point id30198 = (x10.
                                                     array.
                                                     Point) id30198__.next$G();
                                                   	final int i =
                                                     id30198.apply((int)(0));
{
                                                       
//#line 29
x10.
                                                         lang.
                                                         Runtime.runAsync(x10.
                                                                            lang.
                                                                            Runtime.here(),
                                                                          new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                              
//#line 29
harness.
                                                                                x10Test.chk((boolean)(((int) A.apply$G((int)(i))) ==
                                                                                            ((int) ((((int)(i))) * (((int)(NP)))))));
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
            java.lang.Throwable __desugarer__var__304__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 29
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__304__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__304__) {
                
//#line 29
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__304__);
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
        							AsyncTest2.main(args);
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
        public static void main(final x10.core.Rail<java.lang.String> id$30199)  {
            
//#line 35
new AsyncTest2().execute();
        }/* } */
        
        public AsyncTest2() {
            super();
        }
        
        }
        