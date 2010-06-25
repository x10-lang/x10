
public class AsyncTest5
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<AsyncTest5>_RTT = new x10.rtt.RuntimeType<AsyncTest5>(
/* base class */AsyncTest5.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 21
public boolean
                  run(
                  ){
        
//#line 22
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
        
//#line 23
harness.
          x10Test.chk((boolean)(((((int)(x10.runtime.impl.java.Runtime.MAX_PLACES))) >= (((int)(2))))));
        
//#line 24
try {{
            
//#line 24
x10.
              lang.
              Runtime.startFinish();
            {
                
//#line 24
x10.
                  lang.
                  Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                       
//#line 24
harness.
                                         x10Test.chk((boolean)(/* template:equalsequals { */x10.rtt.Equality.equalsequals(A.
                                                                                                                            dist.apply((int)(0)),x10.
                                                       lang.
                                                       Runtime.here())/* } */));
                                   }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                   });
            }
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
        java.lang.Throwable __desugarer__var__320__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 24
x10.
              lang.
              Runtime.pushException(__desugarer__var__320__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__320__) {
            
//#line 24
x10.
              lang.
              Runtime.pushException(__desugarer__var__320__);
        }finally {{
             
//#line 24
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 26
for (
//#line 26
final x10.core.Iterator<x10.
                           array.
                           Point> id30875 =
                           A.
                             dist.
                             region.iterator();
                         id30875.hasNext();
                         ) {
            
//#line 26
final x10.
              array.
              Point id30801 =
              ((x10.
                array.
                Point)
                id30875.next$G());
            
//#line 26
final int i =
              id30801.apply((int)(0));
            
//#line 27
for (
//#line 27
final x10.core.Iterator<x10.
                               array.
                               Point> id30874 =
                               A.
                                 dist.
                                 region.iterator();
                             id30874.hasNext();
                             ) {
                
//#line 27
final x10.
                  array.
                  Point id30802 =
                  ((x10.
                    array.
                    Point)
                    id30874.next$G());
                
//#line 27
final int j =
                  id30802.apply((int)(0));
                
//#line 28
harness.
                  x10Test.chk((boolean)(AsyncTest5.implies((boolean)(/* template:equalsequals { */x10.rtt.Equality.equalsequals(A.
                                                                                                                                  dist.apply((int)(i)),A.
                                                                                                                                                         dist.apply((int)(j)))/* } */),
                                                           (boolean)(((int) i) ==
                                                           ((int) j)))));
            }
        }
        
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
                      Dist __desugarer__var__321__ =
                      A.
                        dist;
                    
//#line 31
/* template:forloop { */for (x10.core.Iterator __desugarer__var__322____ = (__desugarer__var__321__.places()).iterator(); __desugarer__var__322____.hasNext(); ) {
                    	final  x10.
                      lang.
                      Place __desugarer__var__322__ = (x10.
                      lang.
                      Place) __desugarer__var__322____.next$G();
                    	
{
                        
//#line 31
x10.
                          lang.
                          Runtime.runAsync(__desugarer__var__322__,
                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                               
//#line 31
/* template:forloop { */for (x10.core.Iterator id30803__ = (__desugarer__var__321__.restriction(x10.
                                                                                                                                                             lang.
                                                                                                                                                             Runtime.here())).iterator(); id30803__.hasNext(); ) {
                                               	final  x10.
                                                 array.
                                                 Point id30803 = (x10.
                                                 array.
                                                 Point) id30803__.next$G();
                                               	final int i =
                                                 id30803.apply((int)(0));
{
                                                   
//#line 31
x10.
                                                     lang.
                                                     Runtime.runAsync(x10.
                                                                        lang.
                                                                        Runtime.here(),
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          
//#line 32
x10.
                                                                            lang.
                                                                            Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                                 
//#line 32
try {{
                                                                                                     
//#line 32
x10.
                                                                                                       lang.
                                                                                                       Runtime.lock();
                                                                                                     {
                                                                                                         
//#line 32
new x10.core.fun.Fun_0_3<x10.
                                                                                                           array.
                                                                                                           DistArray<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                                                                                           array.
                                                                                                           DistArray<java.lang.Integer> x,final java.lang.Integer y0,final java.lang.Integer z) { return apply(x,(int)y0,(int)z);}
                                                                                                         public final int apply(final x10.
                                                                                                           array.
                                                                                                           DistArray<java.lang.Integer> x, final int y0, final int z) { {
                                                                                                             
//#line 32
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
                                                                                                       
//#line 32
x10.
                                                                                                         lang.
                                                                                                         Runtime.release();
                                                                                                   }}
                                                                                                 
//#line 33
harness.
                                                                                                   x10Test.chk((boolean)(/* template:equalsequals { */x10.rtt.Equality.equalsequals(A.
                                                                                                                                                                                      dist.apply((int)(i)),x10.
                                                                                                                 lang.
                                                                                                                 Runtime.here())/* } */));
                                                                                                 
//#line 34
x10.
                                                                                                   lang.
                                                                                                   Runtime.runAsync(x10.lang.Place.place(x10.core.Ref.home(AsyncTest5.this)),
                                                                                                                    new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                                                        
//#line 34
x10.
                                                                                                                          lang.
                                                                                                                          Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                                                                               
//#line 34
harness.
                                                                                                                                                 x10Test.chk((boolean)(/* template:equalsequals { */x10.rtt.Equality.equalsequals(A.
                                                                                                                                                                                                                                    dist.apply((int)(0)),x10.
                                                                                                                                                               lang.
                                                                                                                                                               Runtime.here())/* } */));
                                                                                                                                           }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                                                                           });
                                                                                                                    }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                                                    });
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
            java.lang.Throwable __desugarer__var__323__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 31
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__323__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__323__) {
                
//#line 31
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__323__);
            }finally {{
                 
//#line 31
x10.
                   lang.
                   Runtime.stopFinish();
             }}
            
//#line 37
try {{
                
//#line 37
x10.
                  lang.
                  Runtime.startFinish();
                {
                    {
                        
//#line 37
final x10.
                          array.
                          Dist __desugarer__var__324__ =
                          A.
                            dist;
                        
//#line 37
/* template:forloop { */for (x10.core.Iterator __desugarer__var__325____ = (__desugarer__var__324__.places()).iterator(); __desugarer__var__325____.hasNext(); ) {
                        	final  x10.
                          lang.
                          Place __desugarer__var__325__ = (x10.
                          lang.
                          Place) __desugarer__var__325____.next$G();
                        	
{
                            
//#line 37
x10.
                              lang.
                              Runtime.runAsync(__desugarer__var__325__,
                                               new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                   
//#line 37
/* template:forloop { */for (x10.core.Iterator id30804__ = (__desugarer__var__324__.restriction(x10.
                                                                                                                                                                 lang.
                                                                                                                                                                 Runtime.here())).iterator(); id30804__.hasNext(); ) {
                                                   	final  x10.
                                                     array.
                                                     Point id30804 = (x10.
                                                     array.
                                                     Point) id30804__.next$G();
                                                   	final int i =
                                                     id30804.apply((int)(0));
{
                                                       
//#line 37
x10.
                                                         lang.
                                                         Runtime.runAsync(x10.
                                                                            lang.
                                                                            Runtime.here(),
                                                                          new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                              
//#line 38
harness.
                                                                                x10Test.chk((boolean)(((int) A.apply$G((int)(i))) ==
                                                                                            ((int) i)));
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
            java.lang.Throwable __desugarer__var__326__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 37
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__326__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__326__) {
                
//#line 37
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__326__);
            }finally {{
                 
//#line 37
x10.
                   lang.
                   Runtime.stopFinish();
             }}
            
//#line 40
return true;
        }
        
        
//#line 43
static boolean
                      implies(
                      boolean x,
                      boolean y){
            
//#line 44
return ((((boolean)(((!(((boolean)(x)))))))) | (((boolean)(y))));
        }
        
        
//#line 47
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
        							AsyncTest5.main(args);
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
            
//#line 48
new AsyncTest5().execute();
        }/* } */
        
        public AsyncTest5() {
            super();
        }
        
        }
        