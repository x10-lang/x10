
public class Array1DCodeGen
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<Array1DCodeGen>_RTT = new x10.rtt.RuntimeType<Array1DCodeGen>(
/* base class */Array1DCodeGen.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 16
final double
                  matgen(
                  final x10.
                    array.
                    DistArray<java.lang.Double> a,
                  final x10.
                    array.
                    DistArray<java.lang.Double> b){
        
//#line 18
int n =
          a.region().max((int)(0));
        
//#line 19
int init =
          1325;
        
//#line 20
double norma =
          0.0;
        
//#line 26
for (
//#line 26
final x10.core.Iterator<x10.
                           array.
                           Point> id7603 =
                           a.
                             dist.
                             region.iterator();
                         id7603.hasNext();
                         ) {
            
//#line 26
final x10.
              array.
              Point id7502 =
              ((x10.
              array.
              Point)(id7603.next$G()));
            
//#line 26
final int i =
              id7502.apply((int)(0));
            
//#line 26
final int j =
              id7502.apply((int)(1));
            
//#line 26
final int k =
              id7502.apply((int)(2));
            
//#line 27
init = ((((int)(((((int)(3125))) * (((int)(init))))))) % (((int)(65536))));
            
//#line 28
double value =
              ((((double)((((((double)(((double)(int)(((int)(init))))))) - (((double)(32768.0)))))))) / (((double)(16384.0))));
            
//#line 29
try {{
                
//#line 29
x10.
                  lang.
                  Runtime.startFinish();
                {
                    
//#line 29
this.write(a,
                                           (int)(i),
                                           (int)(j),
                                           (int)(k),
                                           (double)(value));
                }
            }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
            if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
            java.lang.Throwable __desugarer__var__67__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 29
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__67__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__67__) {
                
//#line 29
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__67__);
            }finally {{
                 
//#line 29
x10.
                   lang.
                   Runtime.stopFinish();
             }}
            
//#line 30
norma = ((((double)(value))) > (((double)(norma))))
              ? value
              : norma;
            }
        
//#line 33
try {{
            
//#line 33
x10.
              lang.
              Runtime.startFinish();
            {
                {
                    
//#line 33
final x10.
                      array.
                      Dist __desugarer__var__68__ =
                      ((x10.
                      array.
                      Dist)(b.
                              dist));
                    
//#line 33
/* template:forloop { */for (x10.core.Iterator __desugarer__var__69____ = (__desugarer__var__68__.places()).iterator(); __desugarer__var__69____.hasNext(); ) {
                    	final  x10.
                      lang.
                      Place __desugarer__var__69__ = (x10.
                      lang.
                      Place) __desugarer__var__69____.next$G();
                    	
{
                        
//#line 33
x10.
                          lang.
                          Runtime.runAsync(__desugarer__var__69__,
                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                               
//#line 33
/* template:forloop { */for (x10.core.Iterator id7503__ = (__desugarer__var__68__.restriction(x10.
                                                                                                                                                           lang.
                                                                                                                                                           Runtime.here())).iterator(); id7503__.hasNext(); ) {
                                               	final  x10.
                                                 array.
                                                 Point id7503 = (x10.
                                                 array.
                                                 Point) id7503__.next$G();
                                               	final int i =
                                                 id7503.apply((int)(0));
final int j =
                                                 id7503.apply((int)(1));
final int k =
                                                 id7503.apply((int)(2));
{
                                                   
//#line 33
x10.
                                                     lang.
                                                     Runtime.runAsync(x10.
                                                                        lang.
                                                                        Runtime.here(),
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          
//#line 33
b.set$G((double)(0.0),
                                                                                              (int)(i),
                                                                                              (int)(j),
                                                                                              (int)(k));
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
        java.lang.Throwable __desugarer__var__70__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 33
x10.
              lang.
              Runtime.pushException(__desugarer__var__70__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__70__) {
            
//#line 33
x10.
              lang.
              Runtime.pushException(__desugarer__var__70__);
        }finally {{
             
//#line 33
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 34
try {{
            
//#line 34
x10.
              lang.
              Runtime.startFinish();
            {
                {
                    
//#line 34
final x10.
                      array.
                      Dist __desugarer__var__71__ =
                      ((x10.
                      array.
                      Dist)(a.
                              dist.$bar((x10.
                                           array.
                                           Region.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                      array.
                                                                      Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                                                                      array.
                                                                      Region[] { x10.
                                                                      array.
                                                                      Region.makeRectangular((int)(0),
                                                                                             (int)(((((int)(n))) - (((int)(1)))))),x10.
                                                                      array.
                                                                      Region.makeRectangular((int)(0),
                                                                                             (int)(((((int)(n))) - (((int)(1)))))),x10.
                                                                      array.
                                                                      Region.makeRectangular((int)(0),
                                                                                             (int)(((((int)(n))) - (((int)(1)))))) })/* } */)))));
                    
//#line 34
/* template:forloop { */for (x10.core.Iterator __desugarer__var__72____ = (__desugarer__var__71__.places()).iterator(); __desugarer__var__72____.hasNext(); ) {
                    	final  x10.
                      lang.
                      Place __desugarer__var__72__ = (x10.
                      lang.
                      Place) __desugarer__var__72____.next$G();
                    	
{
                        
//#line 34
x10.
                          lang.
                          Runtime.runAsync(__desugarer__var__72__,
                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                               
//#line 34
/* template:forloop { */for (x10.core.Iterator id7504__ = (__desugarer__var__71__.restriction(x10.
                                                                                                                                                           lang.
                                                                                                                                                           Runtime.here())).iterator(); id7504__.hasNext(); ) {
                                               	final  x10.
                                                 array.
                                                 Point id7504 = (x10.
                                                 array.
                                                 Point) id7504__.next$G();
                                               	final int i =
                                                 id7504.apply((int)(0));
final int j =
                                                 id7504.apply((int)(1));
final int k =
                                                 id7504.apply((int)(2));
{
                                                   
//#line 34
x10.
                                                     lang.
                                                     Runtime.runAsync(x10.
                                                                        lang.
                                                                        Runtime.here(),
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          
//#line 34
Array1DCodeGen.plusWrite(b,
                                                                                                               (int)(0),
                                                                                                               (int)(j),
                                                                                                               (int)(k),
                                                                                                               (double)(java.lang.Double)(a.apply$G((int)(i),
                                                                                                                                                    (int)(j),
                                                                                                                                                    (int)(k))));
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
        java.lang.Throwable __desugarer__var__73__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 34
x10.
              lang.
              Runtime.pushException(__desugarer__var__73__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__73__) {
            
//#line 34
x10.
              lang.
              Runtime.pushException(__desugarer__var__73__);
        }finally {{
             
//#line 34
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 36
return norma;
        }
        
        
//#line 39
final void
                      write(
                      final x10.
                        array.
                        DistArray<java.lang.Double> a,
                      final int i,
                      final int j,
                      final int k,
                      final double val){
            
//#line 40
x10.
              lang.
              Runtime.runAsync(a.
                                 dist.apply((int)(i),
                                            (int)(j),
                                            (int)(k)),
                               new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                   
//#line 40
try {{
                                       
//#line 40
x10.
                                         lang.
                                         Runtime.lock();
                                       {
                                           
//#line 40
a.set$G((double)(val),
                                                               (int)(i),
                                                               (int)(j),
                                                               (int)(k));
                                       }
                                   }}finally {{
                                         
//#line 40
x10.
                                           lang.
                                           Runtime.release();
                                     }}
                                   }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                   });
            }
        
        
//#line 43
final static void
                      plusWrite(
                      final x10.
                        array.
                        DistArray<java.lang.Double> a,
                      final int i,
                      final int j,
                      final int k,
                      final double val){
            
//#line 47
x10.
              lang.
              Runtime.runAsync(a.
                                 dist.apply((int)(i),
                                            (int)(j),
                                            (int)(k)),
                               new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                   
//#line 47
try {{
                                       
//#line 47
x10.
                                         lang.
                                         Runtime.lock();
                                       {
                                           
//#line 47
new x10.core.fun.Fun_0_5<x10.
                                             array.
                                             DistArray<java.lang.Double>, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Double, java.lang.Double>() {public final java.lang.Double apply$G(final x10.
                                             array.
                                             DistArray<java.lang.Double> x,final java.lang.Integer y0,final java.lang.Integer y1,final java.lang.Integer y2,final java.lang.Double z) { return apply(x,(int)y0,(int)y1,(int)y2,(double)z);}
                                           public final double apply(final x10.
                                             array.
                                             DistArray<java.lang.Double> x, final int y0, final int y1, final int y2, final double z) { {
                                               
//#line 47
return x.set$G((double)(((((double)(x.apply$G((int)(y0),
                                                                                                         (int)(y1),
                                                                                                         (int)(y2))))) + (((double)(z))))),
                                                                          (int)(y0),
                                                                          (int)(y1),
                                                                          (int)(y2));
                                           }}
                                           public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.DOUBLE);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;if (i ==4) return x10.rtt.Types.DOUBLE;if (i ==5) return x10.rtt.Types.DOUBLE;return null;
                                           }
                                           }.apply(a,
                                                   i,
                                                   j,
                                                   k,
                                                   val);
                                       }
                                   }}finally {{
                                         
//#line 47
x10.
                                           lang.
                                           Runtime.release();
                                     }}
                                   }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                   });
            }
        
        
//#line 50
public boolean
                      run(
                      ){
            
//#line 52
final x10.
              array.
              Region R =
              ((x10.
              array.
              Region)(new x10.core.fun.Fun_0_1<x10.
              array.
              Region, x10.
              array.
              Region>() {public final x10.
              array.
              Region apply$G(final x10.
              array.
              Region __desugarer__var__74__) { return apply(__desugarer__var__74__);}
            public final x10.
              array.
              Region apply(final x10.
              array.
              Region __desugarer__var__74__) { {
                
//#line 52
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__74__,null))/* } */ &&
                                  !(((int) __desugarer__var__74__.
                                             rank) ==
                                    ((int) 3) &&
                                    __desugarer__var__74__.
                                      zeroBased &&
                                    __desugarer__var__74__.
                                      rect)) {
                    
//#line 52
throw new java.lang.ClassCastException(("x10.array.Region{self.rect==true, self.zeroBased==true, self" +
                                                                        ".rank==3}"));
                }
                
//#line 52
return __desugarer__var__74__;
            }}
            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Region._RTT;if (i ==1) return x10.array.Region._RTT;return null;
            }
            }.apply(((x10.
                      array.
                      Region)
                      x10.
                      array.
                      Region.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                                 array.
                                                 Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                                                 array.
                                                 Region[] { x10.
                                                 array.
                                                 Region.makeRectangular((int)(0),
                                                                        (int)(9)),x10.
                                                 array.
                                                 Region.makeRectangular((int)(0),
                                                                        (int)(9)),x10.
                                                 array.
                                                 Region.makeRectangular((int)(0),
                                                                        (int)(9)) })/* } */)))));
            
//#line 53
final x10.
              array.
              DistArray<java.lang.Double> a =
              ((x10.
              array.
              DistArray)(x10.
              array.
              DistArray.<java.lang.Double>make(x10.rtt.Types.DOUBLE,
                                               x10.
                                                 array.
                                                 Dist.makeConstant(R,
                                                                   x10.
                                                                     lang.
                                                                     Runtime.here()))));
            
//#line 54
final x10.
              array.
              DistArray<java.lang.Double> b =
              ((x10.
              array.
              DistArray)(x10.
              array.
              DistArray.<java.lang.Double>make(x10.rtt.Types.DOUBLE,
                                               x10.
                                                 array.
                                                 Dist.makeConstant(R,
                                                                   x10.
                                                                     lang.
                                                                     Runtime.here()))));
            
//#line 56
x10.
              io.
              Console.OUT.println((("runtime type of 3dZeroBasedRect array is ") + (x10.core.Ref.typeName(a))));
            
//#line 58
final double result =
              this.matgen(a,
                          b);
            
//#line 59
final x10.
              array.
              Region S =
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
                                                                (int)(9)),x10.
                                         array.
                                         Region.makeRectangular((int)(0),
                                                                (int)(9)),x10.
                                         array.
                                         Region.makeRectangular((int)(0),
                                                                (int)(9)) })/* } */)));
            
//#line 60
final x10.
              array.
              DistArray<java.lang.Double> aa =
              ((x10.
              array.
              DistArray)(x10.
              array.
              DistArray.<java.lang.Double>make(x10.rtt.Types.DOUBLE,
                                               x10.
                                                 array.
                                                 Dist.makeConstant(S,
                                                                   x10.
                                                                     lang.
                                                                     Runtime.here()))));
            
//#line 61
final x10.
              array.
              DistArray<java.lang.Double> bb =
              ((x10.
              array.
              DistArray)(x10.
              array.
              DistArray.<java.lang.Double>make(x10.rtt.Types.DOUBLE,
                                               x10.
                                                 array.
                                                 Dist.makeConstant(S,
                                                                   x10.
                                                                     lang.
                                                                     Runtime.here()))));
            
//#line 62
double result1 =
              this.matgen(aa,
                          bb);
            
//#line 64
x10.
              io.
              Console.OUT.println((("runtime type of unknown array is ") + (x10.core.Ref.typeName(aa))));
            
//#line 65
x10.
              io.
              Console.OUT.println((((((("results are ") + (result))) + (" "))) + (result1)));
            
//#line 67
double diff =
              ((((double)(result))) - (((double)(result1))));
            
//#line 69
return ((((double)(diff))) < (((double)(((double)(int)(((int)(0))))))))
              ? ((((double)(diff))) > (((double)((-(((double)(0.0010))))))))
              : ((((double)(diff))) < (((double)(0.0010))));
        }
        
        
//#line 72
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
        							Array1DCodeGen.main(args);
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
            
//#line 73
new Array1DCodeGen().execute();
        }/* } */
        
        public Array1DCodeGen() {
            super();
        }
        
        }
        