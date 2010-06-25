
public class AtEachLoopOnArray
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<AtEachLoopOnArray>_RTT = new x10.rtt.RuntimeType<AtEachLoopOnArray>(
/* base class */AtEachLoopOnArray.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 20
boolean
      success;
    
    
//#line 22
public boolean
                  run(
                  ){
        
//#line 23
final x10.
          array.
          DistArray<java.lang.Double> A =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<java.lang.Double>make(x10.rtt.Types.DOUBLE,
                                           x10.
                                             array.
                                             Dist.makeConstant(x10.
                                                                 array.
                                                                 Region.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                                            array.
                                                                                            Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                                                                                            array.
                                                                                            Region[] { x10.
                                                                                            array.
                                                                                            Region.makeRectangular((int)(0),
                                                                                                                   (int)(10)) })/* } */),
                                                               x10.
                                                                 lang.
                                                                 Runtime.here()),
                                           new x10.core.fun.Fun_0_1<x10.
                                             array.
                                             Point, java.lang.Double>() {public final java.lang.Double apply$G(final x10.
                                             array.
                                             Point id32300) { return apply(id32300);}
                                           public final double apply(final x10.
                                             array.
                                             Point id32300) { {
                                               
//#line 24
final int i =
                                                 id32300.apply((int)(0));
                                               
//#line 24
return ((double)(int)(((int)(i))));
                                           }}
                                           public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.DOUBLE;return null;
                                           }
                                           })));
        
//#line 26
try {{
            
//#line 26
x10.
              lang.
              Runtime.startFinish();
            {
                {
                    
//#line 26
final x10.
                      array.
                      Dist __desugarer__var__348__ =
                      A.
                        dist;
                    
//#line 26
/* template:forloop { */for (x10.core.Iterator __desugarer__var__349____ = (__desugarer__var__348__.places()).iterator(); __desugarer__var__349____.hasNext(); ) {
                    	final  x10.
                      lang.
                      Place __desugarer__var__349__ = (x10.
                      lang.
                      Place) __desugarer__var__349____.next$G();
                    	
{
                        
//#line 27
x10.
                          lang.
                          Runtime.runAsync(__desugarer__var__349__,
                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                               
//#line 26
/* template:forloop { */for (x10.core.Iterator id32301__ = (__desugarer__var__348__.restriction(x10.
                                                                                                                                                             lang.
                                                                                                                                                             Runtime.here())).iterator(); id32301__.hasNext(); ) {
                                               	final  x10.
                                                 array.
                                                 Point id32301 = (x10.
                                                 array.
                                                 Point) id32301__.next$G();
                                               	final int i =
                                                 id32301.apply((int)(0));
{
                                                   
//#line 27
x10.
                                                     lang.
                                                     Runtime.runAsync(x10.
                                                                        lang.
                                                                        Runtime.here(),
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          
//#line 27
if (((double) A.apply$G((int)(i))) !=
                                                                                          ((double) ((double)(int)(((int)(i)))))) {
                                                                              
//#line 28
x10.
                                                                                lang.
                                                                                Runtime.runAsync(x10.lang.Place.place(x10.core.Ref.home(AtEachLoopOnArray.this)),
                                                                                                 new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                                     
//#line 28
try {{
                                                                                                         
//#line 28
x10.
                                                                                                           lang.
                                                                                                           Runtime.lock();
                                                                                                         {
                                                                                                             
//#line 28
AtEachLoopOnArray.this.success = false;
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
            java.lang.Throwable __desugarer__var__350__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 26
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__350__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__350__) {
                
//#line 26
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__350__);
            }finally {{
                 
//#line 26
x10.
                   lang.
                   Runtime.stopFinish();
             }}
            
//#line 29
return success;
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
        							AtEachLoopOnArray.main(args);
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
            
//#line 33
new AtEachLoopOnArray().execute();
        }/* } */
        
        public AtEachLoopOnArray() {
            super();
            
//#line 20
this.success = true;
        }
    
    }
    