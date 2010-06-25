
public class AsyncFieldAccess
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<AsyncFieldAccess>_RTT = new x10.rtt.RuntimeType<AsyncFieldAccess>(
/* base class */AsyncFieldAccess.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 19
AsyncFieldAccess.
      T
      t;
    
    
//#line 20
public boolean
                  run(
                  ){
        
//#line 21
x10.
          lang.
          Place Second =
          x10.
          lang.
          Place.FIRST_PLACE.next();
        
//#line 22
x10.
          array.
          Region r =
          x10.
          array.
          Region.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                     array.
                                     Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                                     array.
                                     Region[] { x10.
                                     array.
                                     Region.makeRectangular((int)(0),
                                                            (int)(0)) })/* } */);
        
//#line 23
final x10.
          array.
          Dist D =
          x10.
          array.
          Dist.makeConstant(r,
                            Second);
        
//#line 24
try {{
            
//#line 24
x10.
              lang.
              Runtime.startFinish();
            {
                {
                    
//#line 24
final x10.
                      array.
                      Dist __desugarer__var__286__ =
                      ((x10.
                      array.
                      Dist)(D));
                    
//#line 24
/* template:forloop { */for (x10.core.Iterator __desugarer__var__287____ = (__desugarer__var__286__.places()).iterator(); __desugarer__var__287____.hasNext(); ) {
                    	final  x10.
                      lang.
                      Place __desugarer__var__287__ = (x10.
                      lang.
                      Place) __desugarer__var__287____.next$G();
                    	
{
                        
//#line 24
x10.
                          lang.
                          Runtime.runAsync(__desugarer__var__287__,
                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                               
//#line 24
/* template:forloop { */for (x10.core.Iterator p__ = (__desugarer__var__286__.restriction(x10.
                                                                                                                                                       lang.
                                                                                                                                                       Runtime.here())).iterator(); p__.hasNext(); ) {
                                               	final  x10.
                                                 array.
                                                 Point p = (x10.
                                                 array.
                                                 Point) p__.next$G();
                                               	
{
                                                   
//#line 24
x10.
                                                     lang.
                                                     Runtime.runAsync(x10.
                                                                        lang.
                                                                        Runtime.here(),
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          
//#line 25
final AsyncFieldAccess.
                                                                            T NewT =
                                                                            new AsyncFieldAccess.
                                                                            T();
                                                                          
//#line 26
x10.
                                                                            lang.
                                                                            Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                                 
//#line 26
x10.
                                                                                                   lang.
                                                                                                   Runtime.runAt(x10.lang.Place.place(x10.core.Ref.home(AsyncFieldAccess.this)),
                                                                                                                 new x10.core.fun.VoidFun_0_0() {public final void apply() { {
                                                                                                                     
//#line 26
AsyncFieldAccess.this.t = NewT;
                                                                                                                 }}
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
        java.lang.Throwable __desugarer__var__288__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 24
x10.
              lang.
              Runtime.pushException(__desugarer__var__288__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__288__) {
            
//#line 24
x10.
              lang.
              Runtime.pushException(__desugarer__var__288__);
        }finally {{
             
//#line 24
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 28
final AsyncFieldAccess.
          T tt =
          this.
            t;
        
//#line 29
x10.
          lang.
          Runtime.runAt(x10.lang.Place.place(x10.core.Ref.home(tt)),
                        new x10.core.fun.VoidFun_0_0() {public final void apply() { {
                            
//#line 29
try {{
                                
//#line 29
x10.
                                  lang.
                                  Runtime.lock();
                                {
                                    
//#line 29
tt.i = 3;
                                }
                            }}finally {{
                                  
//#line 29
x10.
                                    lang.
                                    Runtime.release();
                              }}
                            }}
                            });
        
//#line 30
return ((int) 3) ==
        ((int) ((x10.
                   lang.
                   Runtime.<java.lang.Integer>evalAt$G(x10.rtt.Types.INT,
                                                       x10.lang.Place.place(x10.core.Ref.home(tt)),
                                                       new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                                       public final int apply() { {
                                                           
//#line 30
return tt.
                                                                                i;
                                                       }}
                                                       public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                                       }
                                                       }))));
        }
        
        
//#line 33
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
        							AsyncFieldAccess.main(args);
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
        public static void main(final x10.core.Rail<java.lang.String> id$29411)  {
            
//#line 34
new AsyncFieldAccess().execute();
        }/* } */
        
        
//#line 37
static class T
                    extends x10.core.Ref
                    {public static final x10.rtt.RuntimeType<AsyncFieldAccess.
          T>_RTT = new x10.rtt.RuntimeType<AsyncFieldAccess.
          T>(
        /* base class */AsyncFieldAccess.
          T.class
        , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
        );
        public x10.rtt.RuntimeType getRTT() {return _RTT;}
        
        
        
            
//#line 38
public int
              i;
            
            public T() {
                super();
                
//#line 38
this.i = 0;
            }
        
        }
        
        
        public AsyncFieldAccess() {
            super();
            
//#line 19
this.t = null;
        }
    
    }
    