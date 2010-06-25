
public class EncapsulatedArray2D_Dep
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<EncapsulatedArray2D_Dep>_RTT = new x10.rtt.RuntimeType<EncapsulatedArray2D_Dep>(
/* base class */EncapsulatedArray2D_Dep.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 22
static class Wrapper
                extends x10.core.Struct
                {public static final x10.rtt.RuntimeType<EncapsulatedArray2D_Dep.
      Wrapper>_RTT = new x10.rtt.RuntimeType<EncapsulatedArray2D_Dep.
      Wrapper>(
    /* base class */EncapsulatedArray2D_Dep.
      Wrapper.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class), x10.rtt.Types.runtimeType(x10.core.Struct.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 23
final x10.
          array.
          DistArray<java.lang.Double>
          m_array;
        
        
//#line 24
Wrapper(x10.
                              array.
                              DistArray<java.lang.Double> a_array) {
            
//#line 24
super();
            
//#line 25
this.m_array = ((x10.
              array.
              DistArray)(a_array));
        }
    
    final public boolean structEquals(final java.lang.Object o) {
        if (!(o instanceof EncapsulatedArray2D_Dep.Wrapper)) return false;
        if (!x10.rtt.Equality.equalsequals(this.m_array, ((EncapsulatedArray2D_Dep.Wrapper) o).m_array)) return false;
        return true;
        }
    
    }
    
    
    
//#line 29
public boolean
                  run(
                  ){
        
//#line 31
final int size =
          5;
        
//#line 32
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
        
//#line 33
final x10.
          array.
          Dist D =
          ((x10.
          array.
          Dist)(x10.
          array.
          Dist.makeBlock(R,
                         (int)(0))));
        
//#line 35
final x10.
          array.
          DistArray<EncapsulatedArray2D_Dep.
          Wrapper> A =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<EncapsulatedArray2D_Dep.
          Wrapper>make(EncapsulatedArray2D_Dep.Wrapper._RTT,
                       D,
                       new x10.core.fun.Fun_0_1<x10.
                         array.
                         Point, EncapsulatedArray2D_Dep.
                         Wrapper>() {public final EncapsulatedArray2D_Dep.
                         Wrapper apply$G(final x10.
                         array.
                         Point id$20347) { return apply(id$20347);}
                       public final EncapsulatedArray2D_Dep.
                         Wrapper apply(final x10.
                         array.
                         Point id$20347) { {
                           
//#line 35
return new EncapsulatedArray2D_Dep.
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
                                                                          Point id$20348) { return apply(id$20348);}
                                                                        public final double apply(final x10.
                                                                          array.
                                                                          Point id$20348) { {
                                                                            
//#line 35
return 0.0;
                                                                        }}
                                                                        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.DOUBLE;return null;
                                                                        }
                                                                        }));
                       }}
                       public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return EncapsulatedArray2D_Dep.Wrapper._RTT;return null;
                       }
                       })));
        
//#line 38
try {{
            
//#line 38
x10.
              lang.
              Runtime.startFinish();
            {
                {
                    
//#line 38
final x10.
                      array.
                      Dist __desugarer__var__240__ =
                      ((x10.
                      array.
                      Dist)(D));
                    
//#line 38
/* template:forloop { */for (x10.core.Iterator __desugarer__var__241____ = (__desugarer__var__240__.places()).iterator(); __desugarer__var__241____.hasNext(); ) {
                    	final  x10.
                      lang.
                      Place __desugarer__var__241__ = (x10.
                      lang.
                      Place) __desugarer__var__241____.next$G();
                    	
{
                        
//#line 38
x10.
                          lang.
                          Runtime.runAsync(__desugarer__var__241__,
                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                               
//#line 38
/* template:forloop { */for (x10.core.Iterator id20349__ = (__desugarer__var__240__.restriction(x10.
                                                                                                                                                             lang.
                                                                                                                                                             Runtime.here())).iterator(); id20349__.hasNext(); ) {
                                               	final  x10.
                                                 array.
                                                 Point id20349 = (x10.
                                                 array.
                                                 Point) id20349__.next$G();
                                               	final int i =
                                                 id20349.apply((int)(0));
final int j =
                                                 id20349.apply((int)(1));
{
                                                   
//#line 38
x10.
                                                     lang.
                                                     Runtime.runAsync(x10.
                                                                        lang.
                                                                        Runtime.here(),
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          
//#line 39
final x10.
                                                                            array.
                                                                            DistArray<java.lang.Double> temp =
                                                                            ((x10.
                                                                            array.
                                                                            DistArray)(A.apply$G((int)(i),
                                                                                                 (int)(j)).
                                                                                         m_array));
                                                                          
//#line 40
for (
//#line 40
final x10.core.Iterator<x10.
                                                                                             array.
                                                                                             Point> p20421 =
                                                                                             temp.
                                                                                               dist.
                                                                                               region.iterator();
                                                                                           p20421.hasNext();
                                                                                           ) {
                                                                              
//#line 40
final x10.
                                                                                array.
                                                                                Point p =
                                                                                ((x10.
                                                                                array.
                                                                                Point)(p20421.next$G()));
                                                                              
//#line 41
temp.set$G((double)((((double)(int)(((int)(((((int)(i))) + (((int)(j)))))))))),
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
        java.lang.Throwable __desugarer__var__242__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 38
x10.
              lang.
              Runtime.pushException(__desugarer__var__242__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__242__) {
            
//#line 38
x10.
              lang.
              Runtime.pushException(__desugarer__var__242__);
        }finally {{
             
//#line 38
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 44
return true;
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
    							EncapsulatedArray2D_Dep.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$20350)  {
        
//#line 48
new EncapsulatedArray2D_Dep().execute();
    }/* } */
    
    public EncapsulatedArray2D_Dep() {
        super();
    }
    
    }
    