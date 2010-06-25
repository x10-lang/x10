
public class EncapsulatedArray1D_Dep
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<EncapsulatedArray1D_Dep>_RTT = new x10.rtt.RuntimeType<EncapsulatedArray1D_Dep>(
/* base class */EncapsulatedArray1D_Dep.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 29
static class Wrapper
                extends x10.core.Struct
                {public static final x10.rtt.RuntimeType<EncapsulatedArray1D_Dep.
      Wrapper>_RTT = new x10.rtt.RuntimeType<EncapsulatedArray1D_Dep.
      Wrapper>(
    /* base class */EncapsulatedArray1D_Dep.
      Wrapper.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class), x10.rtt.Types.runtimeType(x10.core.Struct.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 30
final x10.
          array.
          DistArray<java.lang.Double>
          m_array;
        
        
//#line 31
Wrapper(x10.
                              array.
                              DistArray<java.lang.Double> a_array) {
            
//#line 31
super();
            
//#line 32
this.m_array = ((x10.
              array.
              DistArray)(a_array));
        }
    
    final public boolean structEquals(final java.lang.Object o) {
        if (!(o instanceof EncapsulatedArray1D_Dep.Wrapper)) return false;
        if (!x10.rtt.Equality.equalsequals(this.m_array, ((EncapsulatedArray1D_Dep.Wrapper) o).m_array)) return false;
        return true;
        }
    
    }
    
    
    
//#line 36
public boolean
                  run(
                  ){
        
//#line 38
final int size =
          5;
        
//#line 39
final x10.
          array.
          Region R =
          ((x10.
          array.
          Region)(x10.
          array.
          Region.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                     array.
                                     Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                                     array.
                                     Region[] { x10.
                                     array.
                                     Region.makeRectangular((int)(0),
                                                            (int)(((((int)(size))) - (((int)(1)))))),x10.
                                     array.
                                     Region.makeRectangular((int)(0),
                                                            (int)(((((int)(size))) - (((int)(1)))))) })/* } */)));
        
//#line 40
final x10.
          array.
          Dist D =
          ((x10.
          array.
          Dist)(x10.
          array.
          Dist.makeUnique()));
        
//#line 41
final int numOfPlaces =
          x10.runtime.impl.java.Runtime.MAX_PLACES;
        
//#line 43
final x10.
          array.
          DistArray<EncapsulatedArray1D_Dep.
          Wrapper> A =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<EncapsulatedArray1D_Dep.
          Wrapper>make(EncapsulatedArray1D_Dep.Wrapper._RTT,
                       D)));
        
//#line 44
try {{
            
//#line 44
x10.
              lang.
              Runtime.startFinish();
            {
                {
                    
//#line 44
final x10.
                      array.
                      Dist __desugarer__var__225__ =
                      ((x10.
                      array.
                      Dist)(D));
                    
//#line 44
/* template:forloop { */for (x10.core.Iterator __desugarer__var__226____ = (__desugarer__var__225__.places()).iterator(); __desugarer__var__226____.hasNext(); ) {
                    	final  x10.
                      lang.
                      Place __desugarer__var__226__ = (x10.
                      lang.
                      Place) __desugarer__var__226____.next$G();
                    	
{
                        
//#line 45
x10.
                          lang.
                          Runtime.runAsync(__desugarer__var__226__,
                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                               
//#line 44
/* template:forloop { */for (x10.core.Iterator id19923__ = (__desugarer__var__225__.restriction(x10.
                                                                                                                                                             lang.
                                                                                                                                                             Runtime.here())).iterator(); id19923__.hasNext(); ) {
                                               	final  x10.
                                                 array.
                                                 Point id19923 = (x10.
                                                 array.
                                                 Point) id19923__.next$G();
                                               	final int i =
                                                 id19923.apply((int)(0));
{
                                                   
//#line 45
x10.
                                                     lang.
                                                     Runtime.runAsync(x10.
                                                                        lang.
                                                                        Runtime.here(),
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          
//#line 45
A.set$G(new EncapsulatedArray1D_Dep.
                                                                                                Wrapper(x10.
                                                                                                          array.
                                                                                                          DistArray.<java.lang.Double>make(x10.rtt.Types.DOUBLE,
                                                                                                                                           x10.
                                                                                                                                             array.
                                                                                                                                             Dist.makeConstant(R,
                                                                                                                                                               x10.
                                                                                                                                                                 lang.
                                                                                                                                                                 Runtime.here()),
                                                                                                                                           new x10.core.fun.Fun_0_1<x10.
                                                                                                                                             array.
                                                                                                                                             Point, java.lang.Double>() {public final java.lang.Double apply$G(final x10.
                                                                                                                                             array.
                                                                                                                                             Point id$19924) { return apply(id$19924);}
                                                                                                                                           public final double apply(final x10.
                                                                                                                                             array.
                                                                                                                                             Point id$19924) { {
                                                                                                                                               
//#line 45
return 0.0;
                                                                                                                                           }}
                                                                                                                                           public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.DOUBLE;return null;
                                                                                                                                           }
                                                                                                                                           })),
                                                                                              (int)(i));
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
        java.lang.Throwable __desugarer__var__227__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 44
x10.
              lang.
              Runtime.pushException(__desugarer__var__227__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__227__) {
            
//#line 44
x10.
              lang.
              Runtime.pushException(__desugarer__var__227__);
        }finally {{
             
//#line 44
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 48
try {{
            
//#line 48
x10.
              lang.
              Runtime.startFinish();
            {
                {
                    
//#line 48
final x10.
                      array.
                      Dist __desugarer__var__228__ =
                      ((x10.
                      array.
                      Dist)(D));
                    
//#line 48
/* template:forloop { */for (x10.core.Iterator __desugarer__var__229____ = (__desugarer__var__228__.places()).iterator(); __desugarer__var__229____.hasNext(); ) {
                    	final  x10.
                      lang.
                      Place __desugarer__var__229__ = (x10.
                      lang.
                      Place) __desugarer__var__229____.next$G();
                    	
{
                        
//#line 48
x10.
                          lang.
                          Runtime.runAsync(__desugarer__var__229__,
                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                               
//#line 48
/* template:forloop { */for (x10.core.Iterator id19925__ = (__desugarer__var__228__.restriction(x10.
                                                                                                                                                             lang.
                                                                                                                                                             Runtime.here())).iterator(); id19925__.hasNext(); ) {
                                               	final  x10.
                                                 array.
                                                 Point id19925 = (x10.
                                                 array.
                                                 Point) id19925__.next$G();
                                               	final int i =
                                                 id19925.apply((int)(0));
{
                                                   
//#line 48
x10.
                                                     lang.
                                                     Runtime.runAsync(x10.
                                                                        lang.
                                                                        Runtime.here(),
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          
//#line 49
final x10.
                                                                            array.
                                                                            DistArray<java.lang.Double> temp =
                                                                            ((x10.
                                                                            array.
                                                                            DistArray)(A.apply$G((int)(i)).
                                                                                         m_array));
                                                                          
//#line 50
for (
//#line 50
final x10.core.Iterator<x10.
                                                                                             array.
                                                                                             Point> p19997 =
                                                                                             temp.region().iterator();
                                                                                           p19997.hasNext();
                                                                                           ) {
                                                                              
//#line 50
final x10.
                                                                                array.
                                                                                Point p =
                                                                                ((x10.
                                                                                array.
                                                                                Point)(p19997.next$G()));
                                                                              
//#line 51
temp.set$G((double)((((double)(int)(((int)(i)))))),
                                                                                                     p);
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
        java.lang.Throwable __desugarer__var__230__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 48
x10.
              lang.
              Runtime.pushException(__desugarer__var__230__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__230__) {
            
//#line 48
x10.
              lang.
              Runtime.pushException(__desugarer__var__230__);
        }finally {{
             
//#line 48
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 54
return true;
        }
        
        
//#line 57
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
        							EncapsulatedArray1D_Dep.main(args);
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
        public static void main(final x10.core.Rail<java.lang.String> id$19926)  {
            
//#line 58
new EncapsulatedArray1D_Dep().execute();
        }/* } */
        
        public EncapsulatedArray1D_Dep() {
            super();
        }
    
    }
    