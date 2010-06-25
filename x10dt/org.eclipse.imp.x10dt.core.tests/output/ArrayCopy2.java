
public class ArrayCopy2
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ArrayCopy2>_RTT = new x10.rtt.RuntimeType<ArrayCopy2>(
/* base class */ArrayCopy2.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 27
static boolean
                  outOfRange(
                  final x10.
                    array.
                    Dist D,
                  final x10.
                    array.
                    Point x){
        
//#line 28
try {{
            
//#line 29
x10.
              lang.
              Runtime.runAsync(D.apply(x),
                               new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                   
                               }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                               });
            
//#line 29
;
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
        final java.lang.Throwable id$12917 = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 31
return true;
        }
        }
        throw __$generated_wrappedex$__;
        }catch (final java.lang.Throwable id$12917) {
            
//#line 31
return true;
        }
        
//#line 33
return false;
    }
    
    
//#line 39
public <T> void
                  arrayEqual(
                  final x10.rtt.Type T,
                  final x10.
                    array.
                    DistArray<T> A,
                  final x10.
                    array.
                    DistArray<T> B){
        
//#line 43
try {{
            
//#line 43
x10.
              lang.
              Runtime.startFinish();
            {
                {
                    
//#line 44
final x10.
                      array.
                      Dist __desugarer__var__141__ =
                      ((x10.
                      array.
                      Dist)(A.
                              dist));
                    
//#line 44
/* template:forloop { */for (x10.core.Iterator __desugarer__var__142____ = (__desugarer__var__141__.places()).iterator(); __desugarer__var__142____.hasNext(); ) {
                    	final  x10.
                      lang.
                      Place __desugarer__var__142__ = (x10.
                      lang.
                      Place) __desugarer__var__142____.next$G();
                    	
{
                        
//#line 44
x10.
                          lang.
                          Runtime.runAsync(__desugarer__var__142__,
                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                               
//#line 44
/* template:forloop { */for (x10.core.Iterator p__ = (__desugarer__var__141__.restriction(x10.
                                                                                                                                                       lang.
                                                                                                                                                       Runtime.here())).iterator(); p__.hasNext(); ) {
                                               	final  x10.
                                                 array.
                                                 Point p = (x10.
                                                 array.
                                                 Point) p__.next$G();
                                               	
{
                                                   
//#line 44
x10.
                                                     lang.
                                                     Runtime.runAsync(x10.
                                                                        lang.
                                                                        Runtime.here(),
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          
//#line 45
final T v =
                                                                            ((T)(x10.
                                                                            lang.
                                                                            Runtime.<T>evalAt$G(T,
                                                                                                B.
                                                                                                  dist.apply(p),
                                                                                                new x10.core.fun.Fun_0_0<T>() {public final T apply$G() { {
                                                                                                    
//#line 45
return B.apply$G(p);
                                                                                                }}
                                                                                                public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return T;return null;
                                                                                                }
                                                                                                })));
                                                                          
//#line 46
harness.
                                                                            x10Test.chk((boolean)(/* template:equalsequals { */x10.rtt.Equality.equalsequals(A.apply$G(p),v)/* } */));
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
        java.lang.Throwable __desugarer__var__143__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 43
x10.
              lang.
              Runtime.pushException(__desugarer__var__143__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__143__) {
            
//#line 43
x10.
              lang.
              Runtime.pushException(__desugarer__var__143__);
        }finally {{
             
//#line 43
x10.
               lang.
               Runtime.stopFinish();
         }}
        }
    
    
//#line 56
public void
                  arrayCopy(
                  final x10.
                    array.
                    DistArray<java.lang.Integer> A,
                  final x10.
                    array.
                    DistArray<java.lang.Integer> B){
        
//#line 58
final x10.
          array.
          Dist D =
          ((x10.
          array.
          Dist)(A.
                  dist));
        
//#line 59
final x10.
          array.
          Dist E =
          ((x10.
          array.
          Dist)(B.
                  dist));
        
//#line 62
final x10.
          array.
          Dist D_1 =
          ((x10.
          array.
          Dist)(x10.
          array.
          Dist.makeUnique(x10.core.RailFactory.<x10.
                            lang.
                            Place>makeRailFromValRail(x10.lang.Place._RTT, D.places()))));
        
//#line 65
final x10.
          array.
          DistArray<java.lang.Integer> accessed_a =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<java.lang.Integer>make(x10.rtt.Types.INT,
                                            D)));
        
//#line 68
final x10.
          array.
          DistArray<java.lang.Integer> accessed_b =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<java.lang.Integer>make(x10.rtt.Types.INT,
                                            E)));
        
//#line 70
try {{
            
//#line 70
x10.
              lang.
              Runtime.startFinish();
            {
                {
                    
//#line 71
final x10.
                      array.
                      Dist __desugarer__var__144__ =
                      ((x10.
                      array.
                      Dist)(D_1));
                    
//#line 71
/* template:forloop { */for (x10.core.Iterator __desugarer__var__145____ = (__desugarer__var__144__.places()).iterator(); __desugarer__var__145____.hasNext(); ) {
                    	final  x10.
                      lang.
                      Place __desugarer__var__145__ = (x10.
                      lang.
                      Place) __desugarer__var__145____.next$G();
                    	
{
                        
//#line 71
x10.
                          lang.
                          Runtime.runAsync(__desugarer__var__145__,
                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                               
//#line 71
/* template:forloop { */for (x10.core.Iterator x__ = (__desugarer__var__144__.restriction(x10.
                                                                                                                                                       lang.
                                                                                                                                                       Runtime.here())).iterator(); x__.hasNext(); ) {
                                               	final  x10.
                                                 array.
                                                 Point x = (x10.
                                                 array.
                                                 Point) x__.next$G();
                                               	
{
                                                   
//#line 71
x10.
                                                     lang.
                                                     Runtime.runAsync(x10.
                                                                        lang.
                                                                        Runtime.here(),
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          
//#line 72
final x10.
                                                                            lang.
                                                                            Place px =
                                                                            D_1.apply(x);
                                                                          
//#line 73
harness.
                                                                            x10Test.chk((boolean)(/* template:equalsequals { */x10.rtt.Equality.equalsequals(px,x10.
                                                                                          lang.
                                                                                          Runtime.here())/* } */));
                                                                          
//#line 74
final x10.
                                                                            array.
                                                                            Dist D_local =
                                                                            ((x10.
                                                                            array.
                                                                            Dist)((D.$bar(px))));
                                                                          
//#line 75
for (
//#line 75
final x10.core.Iterator<x10.
                                                                                             array.
                                                                                             Point> i13017 =
                                                                                             D_local.
                                                                                               region.iterator();
                                                                                           i13017.hasNext();
                                                                                           ) {
                                                                              
//#line 75
final x10.
                                                                                array.
                                                                                Point i =
                                                                                ((x10.
                                                                                array.
                                                                                Point)(i13017.next$G()));
                                                                              
//#line 79
A.set$G((int)(java.lang.Integer)(x10.
                                                                                                    lang.
                                                                                                    Runtime.<java.lang.Integer>evalAt$G(x10.rtt.Types.INT,
                                                                                                                                        E.apply(i),
                                                                                                                                        new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                                                                                                                        public final int apply() { {
                                                                                                                                            
//#line 80
try {{
                                                                                                                                                
//#line 80
x10.
                                                                                                                                                  lang.
                                                                                                                                                  Runtime.lock();
                                                                                                                                                {
                                                                                                                                                    
//#line 80
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
                                                                                                                                                        
//#line 80
return x.set$G((int)(((((int)(x.apply$G(y0)))) + (((int)(z))))),
                                                                                                                                                                                   y0);
                                                                                                                                                    }}
                                                                                                                                                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.INT);if (i ==1) return x10.array.Point._RTT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;return null;
                                                                                                                                                    }
                                                                                                                                                    }.apply(accessed_b,
                                                                                                                                                            i,
                                                                                                                                                            1);
                                                                                                                                                }
                                                                                                                                            }}finally {{
                                                                                                                                                  
//#line 80
x10.
                                                                                                                                                    lang.
                                                                                                                                                    Runtime.release();
                                                                                                                                              }}
                                                                                                                                            
//#line 81
return B.apply$G(i);
                                                                                                                                            }}
                                                                                                                                            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                                                                                                                            }
                                                                                                                                            })),
                                                                                                                                        i);
                                                                              
//#line 83
try {{
                                                                                  
//#line 83
x10.
                                                                                    lang.
                                                                                    Runtime.lock();
                                                                                  {
                                                                                      
//#line 83
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
                                                                                          
//#line 83
return x.set$G((int)(((((int)(x.apply$G(y0)))) + (((int)(z))))),
                                                                                                                     y0);
                                                                                      }}
                                                                                      public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.INT);if (i ==1) return x10.array.Point._RTT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;return null;
                                                                                      }
                                                                                      }.apply(accessed_a,
                                                                                              i,
                                                                                              1);
                                                                                  }
                                                                              }}finally {{
                                                                                    
//#line 83
x10.
                                                                                      lang.
                                                                                      Runtime.release();
                                                                                }}
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
            java.lang.Throwable __desugarer__var__146__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 70
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__146__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__146__) {
                
//#line 70
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__146__);
            }finally {{
                 
//#line 70
x10.
                   lang.
                   Runtime.stopFinish();
             }}
            
//#line 102
try {{
                
//#line 102
x10.
                  lang.
                  Runtime.startFinish();
                {
                    {
                        
//#line 102
final x10.
                          array.
                          Dist __desugarer__var__147__ =
                          ((x10.
                          array.
                          Dist)(D));
                        
//#line 102
/* template:forloop { */for (x10.core.Iterator __desugarer__var__148____ = (__desugarer__var__147__.places()).iterator(); __desugarer__var__148____.hasNext(); ) {
                        	final  x10.
                          lang.
                          Place __desugarer__var__148__ = (x10.
                          lang.
                          Place) __desugarer__var__148____.next$G();
                        	
{
                            
//#line 102
x10.
                              lang.
                              Runtime.runAsync(__desugarer__var__148__,
                                               new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                   
//#line 102
/* template:forloop { */for (x10.core.Iterator i__ = (__desugarer__var__147__.restriction(x10.
                                                                                                                                                            lang.
                                                                                                                                                            Runtime.here())).iterator(); i__.hasNext(); ) {
                                                   	final  x10.
                                                     array.
                                                     Point i = (x10.
                                                     array.
                                                     Point) i__.next$G();
                                                   	
{
                                                       
//#line 102
x10.
                                                         lang.
                                                         Runtime.runAsync(x10.
                                                                            lang.
                                                                            Runtime.here(),
                                                                          new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                              
//#line 102
harness.
                                                                                x10Test.chk((boolean)(((int) accessed_a.apply$G(i)) ==
                                                                                            ((int) 1)));
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
            java.lang.Throwable __desugarer__var__149__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 102
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__149__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__149__) {
                
//#line 102
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__149__);
            }finally {{
                 
//#line 102
x10.
                   lang.
                   Runtime.stopFinish();
             }}
            
//#line 105
try {{
                
//#line 105
x10.
                  lang.
                  Runtime.startFinish();
                {
                    {
                        
//#line 105
final x10.
                          array.
                          Dist __desugarer__var__150__ =
                          ((x10.
                          array.
                          Dist)(E));
                        
//#line 105
/* template:forloop { */for (x10.core.Iterator __desugarer__var__151____ = (__desugarer__var__150__.places()).iterator(); __desugarer__var__151____.hasNext(); ) {
                        	final  x10.
                          lang.
                          Place __desugarer__var__151__ = (x10.
                          lang.
                          Place) __desugarer__var__151____.next$G();
                        	
{
                            
//#line 105
x10.
                              lang.
                              Runtime.runAsync(__desugarer__var__151__,
                                               new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                   
//#line 105
/* template:forloop { */for (x10.core.Iterator i__ = (__desugarer__var__150__.restriction(x10.
                                                                                                                                                            lang.
                                                                                                                                                            Runtime.here())).iterator(); i__.hasNext(); ) {
                                                   	final  x10.
                                                     array.
                                                     Point i = (x10.
                                                     array.
                                                     Point) i__.next$G();
                                                   	
{
                                                       
//#line 105
x10.
                                                         lang.
                                                         Runtime.runAsync(x10.
                                                                            lang.
                                                                            Runtime.here(),
                                                                          new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                              
//#line 105
harness.
                                                                                x10Test.chk((boolean)(((int) accessed_b.apply$G(i)) ==
                                                                                            ((int) 1)));
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
            java.lang.Throwable __desugarer__var__152__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 105
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__152__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__152__) {
                
//#line 105
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__152__);
            }finally {{
                 
//#line 105
x10.
                   lang.
                   Runtime.stopFinish();
             }}
            }
            
            
//#line 108
final public static int
              N =
              3;
            
            
//#line 114
public boolean
                           run(
                           ){
                
//#line 116
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
                                                                    (int)(((((int)(ArrayCopy2.N))) - (((int)(1)))))),x10.
                                             array.
                                             Region.makeRectangular((int)(0),
                                                                    (int)(((((int)(ArrayCopy2.N))) - (((int)(1)))))),x10.
                                             array.
                                             Region.makeRectangular((int)(0),
                                                                    (int)(((((int)(ArrayCopy2.N))) - (((int)(1)))))),x10.
                                             array.
                                             Region.makeRectangular((int)(0),
                                                                    (int)(((((int)(ArrayCopy2.N))) - (((int)(1)))))) })/* } */)));
                
//#line 117
final x10.core.ValRail<x10.
                  array.
                  Region> TestDists =
                  ((x10.core.ValRail)(/* template:tuple { */x10.core.RailFactory.<x10.
                  array.
                  Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                  array.
                  Region[] { x10.
                  array.
                  Region.makeRectangular((int)(0),
                                         (int)(((((int)(ArrayCopy2.
                                           dist2.N_DIST_TYPES))) - (((int)(1)))))),x10.
                  array.
                  Region.makeRectangular((int)(0),
                                         (int)(((((int)(ArrayCopy2.
                                           dist2.N_DIST_TYPES))) - (((int)(1)))))) })/* } */));
                {
                    
//#line 119
final x10.
                      array.
                      Region distP13018 =
                      ((x10.
                      array.
                      Region)(x10.
                      array.
                      Region.$implicit_convert(TestDists)));
                    
//#line 119
final x10.core.Rail<java.lang.Integer> distP13019 =
                      ((x10.core.Rail)(x10.core.RailFactory.<java.lang.Integer>makeVarRail(x10.rtt.Types.INT, ((int)(2)))));
                    
//#line 119
final int dY13020min13021 =
                      distP13018.min((int)(1));
                    
//#line 119
final int dY13020max13022 =
                      distP13018.max((int)(1));
                    
//#line 119
final int dX13023min13024 =
                      distP13018.min((int)(0));
                    
//#line 119
final int dX13023max13025 =
                      distP13018.max((int)(0));
                    
//#line 119
for (
//#line 119
int dX13023 =
                                        dX13023min13024;
                                      ((((int)(dX13023))) <= (((int)(dX13023max13025))));
                                      
//#line 119
dX13023 += 1) {
                        
//#line 119
final int dX =
                          dX13023;
                        
//#line 119
((int[])distP13019.value)[0] = dX13023;
                        
//#line 119
for (
//#line 119
int dY13020 =
                                            dY13020min13021;
                                          ((((int)(dY13020))) <= (((int)(dY13020max13022))));
                                          
//#line 119
dY13020 += 1) {
                            
//#line 119
final int dY =
                              dY13020;
                            
//#line 119
((int[])distP13019.value)[1] = dY13020;
                            
//#line 119
final x10.
                              array.
                              Point distP =
                              ((x10.
                              array.
                              Point)(x10.
                              array.
                              Point.make(distP13019)));
                            {
                                
//#line 120
final x10.
                                  array.
                                  Dist D =
                                  ((x10.
                                  array.
                                  Dist)(ArrayCopy2.
                                  dist2.getDist((int)(dX),
                                                R)));
                                
//#line 121
final x10.
                                  array.
                                  Dist E =
                                  ((x10.
                                  array.
                                  Dist)(ArrayCopy2.
                                  dist2.getDist((int)(dY),
                                                R)));
                                
//#line 132
harness.
                                  x10Test.chk((boolean)(((Object)D.
                                                                   region).equals(E.
                                                                                    region) &&
                                              ((Object)D.
                                                         region).equals(R)));
                                
//#line 133
final x10.
                                  array.
                                  DistArray<java.lang.Integer> A =
                                  ((x10.
                                  array.
                                  DistArray)(x10.
                                  array.
                                  DistArray.<java.lang.Integer>make(x10.rtt.Types.INT,
                                                                    D)));
                                
//#line 134
final x10.
                                  array.
                                  DistArray<java.lang.Integer> B =
                                  ((x10.
                                  array.
                                  DistArray)(x10.
                                  array.
                                  DistArray.<java.lang.Integer>make(x10.rtt.Types.INT,
                                                                    E,
                                                                    new x10.core.fun.Fun_0_1<x10.
                                                                      array.
                                                                      Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                                                      array.
                                                                      Point p) { return apply(p);}
                                                                    public final int apply(final x10.
                                                                      array.
                                                                      Point p) { {
                                                                        
//#line 134
final int i =
                                                                          p.apply((int)(0));
                                                                        
//#line 134
final int j =
                                                                          p.apply((int)(1));
                                                                        
//#line 134
final int k =
                                                                          p.apply((int)(2));
                                                                        
//#line 134
final int l =
                                                                          p.apply((int)(3));
                                                                        
//#line 134
final int x =
                                                                          ((((int)(((((int)((((((int)(((((int)((((((int)(((((int)(i))) * (((int)(ArrayCopy2.N))))))) + (((int)(j)))))))) * (((int)(ArrayCopy2.N))))))) + (((int)(k)))))))) * (((int)(ArrayCopy2.N))))))) + (((int)(l))));
                                                                        
//#line 134
return ((((int)(((((int)(x))) * (((int)(x))))))) + (((int)(1))));
                                                                    }}
                                                                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                                                    }
                                                                    })));
                                
//#line 135
this.arrayCopy(A,
                                                            B);
                                
//#line 136
this.<java.lang.Integer>arrayEqual(x10.rtt.Types.INT,
                                                                                A,
                                                                                B);
                            }
                        }
                    }
                }
                
//#line 138
return true;
            }
            
            
//#line 141
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
            							ArrayCopy2.main(args);
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
                
//#line 142
new ArrayCopy2().execute();
            }/* } */
            
            
//#line 149
static class dist2
                         extends x10.core.Ref
                         {public static final x10.rtt.RuntimeType<ArrayCopy2.
              dist2>_RTT = new x10.rtt.RuntimeType<ArrayCopy2.
              dist2>(
            /* base class */ArrayCopy2.
              dist2.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
            );
            public x10.rtt.RuntimeType getRTT() {return _RTT;}
            
            
            
                
//#line 151
final static int
                  BLOCK =
                  0;
                
//#line 152
final static int
                  CONSTANT =
                  1;
                
//#line 154
final static int
                  N_DIST_TYPES =
                  2;
                
                
//#line 158
public static x10.
                               array.
                               Dist
                               getDist(
                               final int distType,
                               final x10.
                                 array.
                                 Region R){
                    
//#line 159
switch (distType) {
                        
//#line 160
case ArrayCopy2.
                          dist2.BLOCK:
                            
//#line 160
return new x10.core.fun.Fun_0_1<x10.
                              array.
                              Dist, x10.
                              array.
                              Dist>() {public final x10.
                              array.
                              Dist apply$G(final x10.
                              array.
                              Dist __desugarer__var__153__) { return apply(__desugarer__var__153__);}
                            public final x10.
                              array.
                              Dist apply(final x10.
                              array.
                              Dist __desugarer__var__153__) { {
                                
//#line 160
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__153__,null))/* } */ &&
                                                   !(/* template:equalsequals { */x10.rtt.Equality.equalsequals(__desugarer__var__153__.
                                                                                                                  region,R)/* } */)) {
                                    
//#line 160
throw new java.lang.ClassCastException("x10.array.Dist{self.region==R}");
                                }
                                
//#line 160
return __desugarer__var__153__;
                            }}
                            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Dist._RTT;if (i ==1) return x10.array.Dist._RTT;return null;
                            }
                            }.apply(((x10.
                                      array.
                                      Dist)
                                      x10.
                                      array.
                                      Dist.makeBlock(R,
                                                     (int)(0))));
                        
//#line 161
case ArrayCopy2.
                          dist2.CONSTANT:
                            
//#line 161
return new x10.core.fun.Fun_0_1<x10.
                              array.
                              Dist, x10.
                              array.
                              Dist>() {public final x10.
                              array.
                              Dist apply$G(final x10.
                              array.
                              Dist __desugarer__var__154__) { return apply(__desugarer__var__154__);}
                            public final x10.
                              array.
                              Dist apply(final x10.
                              array.
                              Dist __desugarer__var__154__) { {
                                
//#line 161
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__154__,null))/* } */ &&
                                                   !(/* template:equalsequals { */x10.rtt.Equality.equalsequals(__desugarer__var__154__.
                                                                                                                  region,R)/* } */)) {
                                    
//#line 161
throw new java.lang.ClassCastException("x10.array.Dist{self.region==R}");
                                }
                                
//#line 161
return __desugarer__var__154__;
                            }}
                            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Dist._RTT;if (i ==1) return x10.array.Dist._RTT;return null;
                            }
                            }.apply(((x10.
                                      array.
                                      Dist)
                                      (x10.
                                         array.
                                         Dist.makeConstant(R,
                                                           x10.
                                                             lang.
                                                             Runtime.here()))));
                        
//#line 163
default:
                            
//#line 163
throw new java.lang.Error();
                    }
                }
                
                public dist2() {
                    super();
                }
            
            }
            
            
            public ArrayCopy2() {
                super();
            }
        
        }
        