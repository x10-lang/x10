
public class AtEach2
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<AtEach2>_RTT = new x10.rtt.RuntimeType<AtEach2>(
/* base class */AtEach2.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 21
int
      nplaces;
    
    
//#line 23
public boolean
                  run(
                  ){
        
//#line 24
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
                      Dist __desugarer__var__342__ =
                      ((x10.
                      array.
                      Dist)(d));
                    
//#line 25
/* template:forloop { */for (x10.core.Iterator __desugarer__var__343____ = (__desugarer__var__342__.places()).iterator(); __desugarer__var__343____.hasNext(); ) {
                    	final  x10.
                      lang.
                      Place __desugarer__var__343__ = (x10.
                      lang.
                      Place) __desugarer__var__343____.next$G();
                    	
{
                        
//#line 25
x10.
                          lang.
                          Runtime.runAsync(__desugarer__var__343__,
                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                               
//#line 25
/* template:forloop { */for (x10.core.Iterator p__ = (__desugarer__var__342__.restriction(x10.
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
harness.
                                                                            x10Test.chk((boolean)(/* template:equalsequals { */x10.rtt.Equality.equalsequals(x10.
                                                                                          lang.
                                                                                          Runtime.here(),d.apply(p))/* } */));
                                                                          
//#line 29
x10.
                                                                            lang.
                                                                            Runtime.runAsync(x10.lang.Place.place(x10.core.Ref.home(AtEach2.this)),
                                                                                             new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                                 
//#line 30
try {{
                                                                                                     
//#line 30
x10.
                                                                                                       lang.
                                                                                                       Runtime.lock();
                                                                                                     {
                                                                                                         
//#line 30
AtEach2.this.nplaces += 1;
                                                                                                     }
                                                                                                 }}finally {{
                                                                                                       
//#line 30
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
            java.lang.Throwable __desugarer__var__344__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 25
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__344__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__344__) {
                
//#line 25
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__344__);
            }finally {{
                 
//#line 25
x10.
                   lang.
                   Runtime.stopFinish();
             }}
            
//#line 33
return ((int) nplaces) ==
            ((int) x10.runtime.impl.java.Runtime.MAX_PLACES);
        }
        
        
//#line 36
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
        							AtEach2.main(args);
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
        public static void main(final x10.core.Rail<java.lang.String> id$32111)  {
            
//#line 37
new AtEach2().execute();
        }/* } */
        
        public AtEach2() {
            super();
            
//#line 21
this.nplaces = 0;
        }
    
    }
    