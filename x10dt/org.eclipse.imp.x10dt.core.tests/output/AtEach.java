
public class AtEach
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<AtEach>_RTT = new x10.rtt.RuntimeType<AtEach>(
/* base class */AtEach.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 20
int
      nplaces;
    
    
//#line 22
public boolean
                  run(
                  ){
        
//#line 23
final x10.
          array.
          Dist d =
          x10.
          array.
          Dist.makeUnique(x10.core.RailFactory.<x10.
                            lang.
                            Place>makeRailFromValRail(x10.lang.Place._RTT, x10.
                            lang.
                            Place.places));
        
//#line 24
final x10.
          array.
          DistArray<java.lang.Integer> disagree =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<java.lang.Integer>make(x10.rtt.Types.INT,
                                            d)));
        
//#line 25
try {{
            
//#line 25
x10.
              lang.
              Runtime.startFinish();
            {
                {
                    
//#line 25
final x10.
                      array.
                      Dist __desugarer__var__336__ =
                      ((x10.
                      array.
                      Dist)(d));
                    
//#line 25
/* template:forloop { */for (x10.core.Iterator __desugarer__var__337____ = (__desugarer__var__336__.places()).iterator(); __desugarer__var__337____.hasNext(); ) {
                    	final  x10.
                      lang.
                      Place __desugarer__var__337__ = (x10.
                      lang.
                      Place) __desugarer__var__337____.next$G();
                    	
{
                        
//#line 25
x10.
                          lang.
                          Runtime.runAsync(__desugarer__var__337__,
                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                               
//#line 25
/* template:forloop { */for (x10.core.Iterator p__ = (__desugarer__var__336__.restriction(x10.
                                                                                                                                                       lang.
                                                                                                                                                       Runtime.here())).iterator(); p__.hasNext(); ) {
                                               	final  x10.
                                                 array.
                                                 Point p = (x10.
                                                 array.
                                                 Point) p__.next$G();
                                               	
{
                                                   
//#line 25
x10.
                                                     lang.
                                                     Runtime.runAsync(x10.
                                                                        lang.
                                                                        Runtime.here(),
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          
//#line 28
new x10.core.fun.Fun_0_3<x10.
                                                                            array.
                                                                            DistArray<java.lang.Integer>, x10.
                                                                            array.
                                                                            Point, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                                                            array.
                                                                            DistArray<java.lang.Integer> x,final x10.
                                                                            array.
                                                                            Point y0,final java.lang.Integer z) { return apply(x,y0,(int)z);}
                                                                          public final int apply(final x10.
                                                                            array.
                                                                            DistArray<java.lang.Integer> x, final x10.
                                                                            array.
                                                                            Point y0, final int z) { {
                                                                              
//#line 28
return x.set$G((int)(((((int)(x.apply$G(y0)))) | (((int)(z))))),
                                                                                                         y0);
                                                                          }}
                                                                          public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.INT);if (i ==1) return x10.array.Point._RTT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;return null;
                                                                          }
                                                                          }.apply(disagree,
                                                                                  p,
                                                                                  (((/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(x10.
                                                                                       lang.
                                                                                       Runtime.here(),d.apply(p)))/* } */))
                                                                                     ? 1
                                                                                     : 0));
                                                                          
//#line 29
x10.
                                                                            lang.
                                                                            Runtime.runAsync(x10.lang.Place.place(x10.core.Ref.home(AtEach.this)),
                                                                                             new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                                 
//#line 29
try {{
                                                                                                     
//#line 29
x10.
                                                                                                       lang.
                                                                                                       Runtime.lock();
                                                                                                     {
                                                                                                         
//#line 29
AtEach.this.nplaces += 1;
                                                                                                     }
                                                                                                 }}finally {{
                                                                                                       
//#line 29
x10.
                                                                                                         lang.
                                                                                                         Runtime.release();
                                                                                                   }}
                                                                                                 }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                                 });
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
            java.lang.Throwable __desugarer__var__338__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 25
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__338__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__338__) {
                
//#line 25
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__338__);
            }finally {{
                 
//#line 25
x10.
                   lang.
                   Runtime.stopFinish();
             }}
            
//#line 34
return ((int) disagree.reduce$G(new x10.core.fun.Fun_0_2<java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final java.lang.Integer x,final java.lang.Integer y) { return apply((int)x,(int)y);}
                                                        public final int apply(final int x, final int y) { {
                                                            
//#line 34
return ((((int)(x))) + (((int)(y))));
                                                        }}
                                                        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;return null;
                                                        }
                                                        },
                                                        (int)(0))) ==
            ((int) 0) &&
            ((int) nplaces) ==
            ((int) x10.runtime.impl.java.Runtime.MAX_PLACES);
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
        							AtEach.main(args);
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
new AtEach().execute();
        }/* } */
        
        public AtEach() {
            super();
            
//#line 20
this.nplaces = 0;
        }
    
    }
    