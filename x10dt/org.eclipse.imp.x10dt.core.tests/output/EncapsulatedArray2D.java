
public class EncapsulatedArray2D
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<EncapsulatedArray2D>_RTT = new x10.rtt.RuntimeType<EncapsulatedArray2D>(
/* base class */EncapsulatedArray2D.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 30
static class Wrapper
                extends x10.core.Struct
                {public static final x10.rtt.RuntimeType<EncapsulatedArray2D.
      Wrapper>_RTT = new x10.rtt.RuntimeType<EncapsulatedArray2D.
      Wrapper>(
    /* base class */EncapsulatedArray2D.
      Wrapper.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class), x10.rtt.Types.runtimeType(x10.core.Struct.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 31
final x10.
          array.
          DistArray<java.lang.Double>
          m_array;
        
        
//#line 32
Wrapper(x10.
                              array.
                              DistArray<java.lang.Double> a_array) {
            
//#line 32
super();
            
//#line 33
this.m_array = ((x10.
              array.
              DistArray)(a_array));
        }
    
    final public boolean structEquals(final java.lang.Object o) {
        if (!(o instanceof EncapsulatedArray2D.Wrapper)) return false;
        if (!x10.rtt.Equality.equalsequals(this.m_array, ((EncapsulatedArray2D.Wrapper) o).m_array)) return false;
        return true;
        }
    
    }
    
    
    
//#line 37
public boolean
                  run(
                  ){
        
//#line 39
final int size =
          5;
        
//#line 40
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
        
//#line 41
final x10.
          array.
          Dist D =
          ((x10.
          array.
          Dist)(x10.
          array.
          Dist.makeBlock(R,
                         (int)(0))));
        
//#line 43
final x10.
          array.
          DistArray<EncapsulatedArray2D.
          Wrapper> A =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<EncapsulatedArray2D.
          Wrapper>make(EncapsulatedArray2D.Wrapper._RTT,
                       D,
                       new x10.core.fun.Fun_0_1<x10.
                         array.
                         Point, EncapsulatedArray2D.
                         Wrapper>() {public final EncapsulatedArray2D.
                         Wrapper apply$G(final x10.
                         array.
                         Point id$20135) { return apply(id$20135);}
                       public final EncapsulatedArray2D.
                         Wrapper apply(final x10.
                         array.
                         Point id$20135) { {
                           
//#line 43
return new EncapsulatedArray2D.
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
                                                                          Point id$20136) { return apply(id$20136);}
                                                                        public final double apply(final x10.
                                                                          array.
                                                                          Point id$20136) { {
                                                                            
//#line 43
return 0.0;
                                                                        }}
                                                                        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.DOUBLE;return null;
                                                                        }
                                                                        }));
                       }}
                       public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return EncapsulatedArray2D.Wrapper._RTT;return null;
                       }
                       })));
        
//#line 47
try {{
            
//#line 47
x10.
              lang.
              Runtime.startFinish();
            {
                {
                    
//#line 47
final x10.
                      array.
                      Dist __desugarer__var__234__ =
                      ((x10.
                      array.
                      Dist)(D));
                    
//#line 47
/* template:forloop { */for (x10.core.Iterator __desugarer__var__235____ = (__desugarer__var__234__.places()).iterator(); __desugarer__var__235____.hasNext(); ) {
                    	final  x10.
                      lang.
                      Place __desugarer__var__235__ = (x10.
                      lang.
                      Place) __desugarer__var__235____.next$G();
                    	
{
                        
//#line 47
x10.
                          lang.
                          Runtime.runAsync(__desugarer__var__235__,
                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                               
//#line 47
/* template:forloop { */for (x10.core.Iterator id20137__ = (__desugarer__var__234__.restriction(x10.
                                                                                                                                                             lang.
                                                                                                                                                             Runtime.here())).iterator(); id20137__.hasNext(); ) {
                                               	final  x10.
                                                 array.
                                                 Point id20137 = (x10.
                                                 array.
                                                 Point) id20137__.next$G();
                                               	final int i =
                                                 id20137.apply((int)(0));
final int j =
                                                 id20137.apply((int)(1));
{
                                                   
//#line 47
x10.
                                                     lang.
                                                     Runtime.runAsync(x10.
                                                                        lang.
                                                                        Runtime.here(),
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          
//#line 48
final x10.
                                                                            array.
                                                                            DistArray<java.lang.Double> temp =
                                                                            ((x10.
                                                                            array.
                                                                            DistArray)(A.apply$G((int)(i),
                                                                                                 (int)(j)).
                                                                                         m_array));
                                                                          
//#line 49
for (
//#line 49
final x10.core.Iterator<x10.
                                                                                             array.
                                                                                             Point> p20209 =
                                                                                             temp.
                                                                                               dist.
                                                                                               region.iterator();
                                                                                           p20209.hasNext();
                                                                                           ) {
                                                                              
//#line 49
final x10.
                                                                                array.
                                                                                Point p =
                                                                                ((x10.
                                                                                array.
                                                                                Point)(p20209.next$G()));
                                                                              
//#line 50
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
        java.lang.Throwable __desugarer__var__236__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 47
x10.
              lang.
              Runtime.pushException(__desugarer__var__236__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__236__) {
            
//#line 47
x10.
              lang.
              Runtime.pushException(__desugarer__var__236__);
        }finally {{
             
//#line 47
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 53
return true;
        }
    
    
//#line 56
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
    							EncapsulatedArray2D.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$20138)  {
        
//#line 57
new EncapsulatedArray2D().execute();
    }/* } */
    
    public EncapsulatedArray2D() {
        super();
    }
    
    }
    