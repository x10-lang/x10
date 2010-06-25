
public class ArrayCopy3
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ArrayCopy3>_RTT = new x10.rtt.RuntimeType<ArrayCopy3>(
/* base class */ArrayCopy3.class
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
boolean gotException =
          false;
        
//#line 29
try {{
            
//#line 30
x10.
              lang.
              Runtime.runAsync(D.apply(x),
                               new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                   
                               }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                               });
            
//#line 30
;
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
        java.lang.Throwable e = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 32
gotException = true;
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable e) {
            
//#line 32
gotException = true;
        }
        
//#line 34
return gotException;
    }
    
    
//#line 40
public void
                  arrayEqual(
                  final x10.
                    array.
                    DistArray<java.lang.Integer> A,
                  final x10.
                    array.
                    DistArray<java.lang.Integer> B){
        
//#line 41
final x10.
          array.
          Dist D =
          ((x10.
          array.
          Dist)(A.
                  dist));
        
//#line 42
final x10.
          array.
          Dist E =
          ((x10.
          array.
          Dist)(B.
                  dist));
        
//#line 46
try {{
            
//#line 46
x10.
              lang.
              Runtime.startFinish();
            {
                {
                    
//#line 47
final x10.
                      array.
                      Dist __desugarer__var__169__ =
                      ((x10.
                      array.
                      Dist)(D));
                    
//#line 47
/* template:forloop { */for (x10.core.Iterator __desugarer__var__170____ = (__desugarer__var__169__.places()).iterator(); __desugarer__var__170____.hasNext(); ) {
                    	final  x10.
                      lang.
                      Place __desugarer__var__170__ = (x10.
                      lang.
                      Place) __desugarer__var__170____.next$G();
                    	
{
                        
//#line 47
x10.
                          lang.
                          Runtime.runAsync(__desugarer__var__170__,
                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                               
//#line 47
/* template:forloop { */for (x10.core.Iterator p__ = (__desugarer__var__169__.restriction(x10.
                                                                                                                                                       lang.
                                                                                                                                                       Runtime.here())).iterator(); p__.hasNext(); ) {
                                               	final  x10.
                                                 array.
                                                 Point p = (x10.
                                                 array.
                                                 Point) p__.next$G();
                                               	
{
                                                   
//#line 47
x10.
                                                     lang.
                                                     Runtime.runAsync(x10.
                                                                        lang.
                                                                        Runtime.here(),
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          
//#line 48
final int f =
                                                                            ((java.lang.Integer)((x10.
                                                                                                    lang.
                                                                                                    Runtime.<java.lang.Integer>evalFuture(x10.rtt.Types.INT,
                                                                                                                                          E.apply(p),
                                                                                                                                          new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                                                                                                                          public final int apply() { {
                                                                                                                                              
//#line 48
return B.apply$G(p);
                                                                                                                                          }}
                                                                                                                                          public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                                                                                                                          }
                                                                                                                                          })).force$G()));
                                                                          
//#line 49
harness.
                                                                            x10Test.chk((boolean)(((int) A.apply$G(new x10.core.fun.Fun_0_1<x10.
                                                                                                                     array.
                                                                                                                     Point, x10.
                                                                                                                     array.
                                                                                                                     Point>() {public final x10.
                                                                                                                     array.
                                                                                                                     Point apply$G(final x10.
                                                                                                                     array.
                                                                                                                     Point __desugarer__var__168__) { return apply(__desugarer__var__168__);}
                                                                                                                   public final x10.
                                                                                                                     array.
                                                                                                                     Point apply(final x10.
                                                                                                                     array.
                                                                                                                     Point __desugarer__var__168__) { {
                                                                                                                       
//#line 49
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__168__,null))/* } */ &&
                                                                                                                                         !(((int) __desugarer__var__168__.
                                                                                                                                                    rank) ==
                                                                                                                                           ((int) A.rank()))) {
                                                                                                                           
//#line 49
throw new java.lang.ClassCastException("x10.array.Point{self.rank==A.dist.region.rank}");
                                                                                                                       }
                                                                                                                       
//#line 49
return __desugarer__var__168__;
                                                                                                                   }}
                                                                                                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.array.Point._RTT;return null;
                                                                                                                   }
                                                                                                                   }.apply(((x10.
                                                                                                                             array.
                                                                                                                             Point)
                                                                                                                             p)))) ==
                                                                                        ((int) f)));
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
        java.lang.Throwable __desugarer__var__171__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 46
x10.
              lang.
              Runtime.pushException(__desugarer__var__171__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__171__) {
            
//#line 46
x10.
              lang.
              Runtime.pushException(__desugarer__var__171__);
        }finally {{
             
//#line 46
x10.
               lang.
               Runtime.stopFinish();
         }}
        }
    
    
//#line 59
public void
                  arrayCopy(
                  final x10.
                    array.
                    DistArray<java.lang.Integer> A,
                  final x10.
                    array.
                    DistArray<java.lang.Integer> B){
        
//#line 61
final x10.
          array.
          Dist D =
          ((x10.
          array.
          Dist)(A.
                  dist));
        
//#line 62
final x10.
          array.
          Dist E =
          ((x10.
          array.
          Dist)(B.
                  dist));
        
//#line 65
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
        
//#line 68
final x10.
          array.
          DistArray<java.lang.Integer> accessed_a =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<java.lang.Integer>make(x10.rtt.Types.INT,
                                            D,
                                            new x10.core.fun.Fun_0_1<x10.
                                              array.
                                              Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                              array.
                                              Point id$13186) { return apply(id$13186);}
                                            public final int apply(final x10.
                                              array.
                                              Point id$13186) { {
                                                
//#line 68
return 0;
                                            }}
                                            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                            }
                                            })));
        
//#line 71
final x10.
          array.
          DistArray<java.lang.Integer> accessed_b =
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
                                              Point id$13187) { return apply(id$13187);}
                                            public final int apply(final x10.
                                              array.
                                              Point id$13187) { {
                                                
//#line 71
return 0;
                                            }}
                                            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                            }
                                            })));
        
//#line 73
try {{
            
//#line 73
x10.
              lang.
              Runtime.startFinish();
            {
                {
                    
//#line 74
final x10.
                      array.
                      Dist __desugarer__var__172__ =
                      ((x10.
                      array.
                      Dist)(D_1));
                    
//#line 74
/* template:forloop { */for (x10.core.Iterator __desugarer__var__173____ = (__desugarer__var__172__.places()).iterator(); __desugarer__var__173____.hasNext(); ) {
                    	final  x10.
                      lang.
                      Place __desugarer__var__173__ = (x10.
                      lang.
                      Place) __desugarer__var__173____.next$G();
                    	
{
                        
//#line 74
x10.
                          lang.
                          Runtime.runAsync(__desugarer__var__173__,
                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                               
//#line 74
/* template:forloop { */for (x10.core.Iterator x__ = (__desugarer__var__172__.restriction(x10.
                                                                                                                                                       lang.
                                                                                                                                                       Runtime.here())).iterator(); x__.hasNext(); ) {
                                               	final  x10.
                                                 array.
                                                 Point x = (x10.
                                                 array.
                                                 Point) x__.next$G();
                                               	
{
                                                   
//#line 74
x10.
                                                     lang.
                                                     Runtime.runAsync(x10.
                                                                        lang.
                                                                        Runtime.here(),
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          
//#line 75
final x10.
                                                                            lang.
                                                                            Place px =
                                                                            D_1.apply(x);
                                                                          
//#line 76
harness.
                                                                            x10Test.chk((boolean)(/* template:equalsequals { */x10.rtt.Equality.equalsequals(x10.
                                                                                          lang.
                                                                                          Runtime.here(),px)/* } */));
                                                                          
//#line 77
final x10.
                                                                            array.
                                                                            Region LocalD =
                                                                            ((x10.
                                                                            array.
                                                                            Region)(((D.$bar(px))).
                                                                                      region));
                                                                          
//#line 78
for (
//#line 78
final x10.core.Iterator<x10.
                                                                                             lang.
                                                                                             Place> py13286 =
                                                                                             ((E.$bar(LocalD)).places()).iterator();
                                                                                           py13286.hasNext();
                                                                                           ) {
                                                                              
//#line 78
final x10.
                                                                                lang.
                                                                                Place py =
                                                                                py13286.next$G();
                                                                              
//#line 79
final x10.
                                                                                array.
                                                                                Region RemoteE =
                                                                                ((x10.
                                                                                array.
                                                                                Region)(((E.$bar(py))).
                                                                                          region));
                                                                              
//#line 80
final x10.
                                                                                array.
                                                                                Region Common =
                                                                                ((x10.
                                                                                array.
                                                                                Region)(LocalD.$and(RemoteE)));
                                                                              
//#line 81
final x10.
                                                                                array.
                                                                                Dist D_common =
                                                                                ((x10.
                                                                                array.
                                                                                Dist)(D.$bar(Common)));
                                                                              
//#line 83
for (
//#line 83
final x10.core.Iterator<x10.
                                                                                                 array.
                                                                                                 Point> i13285 =
                                                                                                 D_common.
                                                                                                   region.iterator();
                                                                                               i13285.hasNext();
                                                                                               ) {
                                                                                  
//#line 83
final x10.
                                                                                    array.
                                                                                    Point i =
                                                                                    ((x10.
                                                                                    array.
                                                                                    Point)(i13285.next$G()));
                                                                                  
//#line 84
x10.
                                                                                    lang.
                                                                                    Runtime.runAsync(py,
                                                                                                     new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                                         
//#line 84
try {{
                                                                                                             
//#line 84
x10.
                                                                                                               lang.
                                                                                                               Runtime.lock();
                                                                                                             {
                                                                                                                 
//#line 84
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
                                                                                                                     
//#line 84
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
                                                                                                               
//#line 84
x10.
                                                                                                                 lang.
                                                                                                                 Runtime.release();
                                                                                                           }}
                                                                                                         }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                                         });
                                                                                  
//#line 85
final int temp =
                                                                                    ((java.lang.Integer)((x10.
                                                                                                            lang.
                                                                                                            Runtime.<java.lang.Integer>evalFuture(x10.rtt.Types.INT,
                                                                                                                                                  py,
                                                                                                                                                  new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                                                                                                                                  public final int apply() { {
                                                                                                                                                      
//#line 85
return B.apply$G(i);
                                                                                                                                                  }}
                                                                                                                                                  public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                                                                                                                                  }
                                                                                                                                                  })).force$G()));
                                                                                  
//#line 89
A.set$G((int)(temp),
                                                                                                      i);
                                                                                  
//#line 90
try {{
                                                                                      
//#line 90
x10.
                                                                                        lang.
                                                                                        Runtime.lock();
                                                                                      {
                                                                                          
//#line 90
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
                                                                                              
//#line 90
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
                                                                                        
//#line 90
x10.
                                                                                          lang.
                                                                                          Runtime.release();
                                                                                    }}
                                                                                  }
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
            java.lang.Throwable __desugarer__var__174__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 73
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__174__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__174__) {
                
//#line 73
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__174__);
            }finally {{
                 
//#line 73
x10.
                   lang.
                   Runtime.stopFinish();
             }}
            
//#line 120
try {{
                
//#line 120
x10.
                  lang.
                  Runtime.startFinish();
                {
                    {
                        
//#line 120
final x10.
                          array.
                          Dist __desugarer__var__175__ =
                          ((x10.
                          array.
                          Dist)(D));
                        
//#line 120
/* template:forloop { */for (x10.core.Iterator __desugarer__var__176____ = (__desugarer__var__175__.places()).iterator(); __desugarer__var__176____.hasNext(); ) {
                        	final  x10.
                          lang.
                          Place __desugarer__var__176__ = (x10.
                          lang.
                          Place) __desugarer__var__176____.next$G();
                        	
{
                            
//#line 120
x10.
                              lang.
                              Runtime.runAsync(__desugarer__var__176__,
                                               new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                   
//#line 120
/* template:forloop { */for (x10.core.Iterator i__ = (__desugarer__var__175__.restriction(x10.
                                                                                                                                                            lang.
                                                                                                                                                            Runtime.here())).iterator(); i__.hasNext(); ) {
                                                   	final  x10.
                                                     array.
                                                     Point i = (x10.
                                                     array.
                                                     Point) i__.next$G();
                                                   	
{
                                                       
//#line 120
x10.
                                                         lang.
                                                         Runtime.runAsync(x10.
                                                                            lang.
                                                                            Runtime.here(),
                                                                          new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                              
//#line 120
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
            java.lang.Throwable __desugarer__var__177__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 120
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__177__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__177__) {
                
//#line 120
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__177__);
            }finally {{
                 
//#line 120
x10.
                   lang.
                   Runtime.stopFinish();
             }}
            
//#line 123
try {{
                
//#line 123
x10.
                  lang.
                  Runtime.startFinish();
                {
                    {
                        
//#line 123
final x10.
                          array.
                          Dist __desugarer__var__178__ =
                          ((x10.
                          array.
                          Dist)(E));
                        
//#line 123
/* template:forloop { */for (x10.core.Iterator __desugarer__var__179____ = (__desugarer__var__178__.places()).iterator(); __desugarer__var__179____.hasNext(); ) {
                        	final  x10.
                          lang.
                          Place __desugarer__var__179__ = (x10.
                          lang.
                          Place) __desugarer__var__179____.next$G();
                        	
{
                            
//#line 123
x10.
                              lang.
                              Runtime.runAsync(__desugarer__var__179__,
                                               new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                   
//#line 123
/* template:forloop { */for (x10.core.Iterator i__ = (__desugarer__var__178__.restriction(x10.
                                                                                                                                                            lang.
                                                                                                                                                            Runtime.here())).iterator(); i__.hasNext(); ) {
                                                   	final  x10.
                                                     array.
                                                     Point i = (x10.
                                                     array.
                                                     Point) i__.next$G();
                                                   	
{
                                                       
//#line 123
x10.
                                                         lang.
                                                         Runtime.runAsync(x10.
                                                                            lang.
                                                                            Runtime.here(),
                                                                          new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                              
//#line 123
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
            java.lang.Throwable __desugarer__var__180__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 123
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__180__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__180__) {
                
//#line 123
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__180__);
            }finally {{
                 
//#line 123
x10.
                   lang.
                   Runtime.stopFinish();
             }}
            }
            
            
//#line 126
final public static int
              N =
              3;
            
            
//#line 132
public boolean
                           run(
                           ){
                
//#line 134
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
                                                                    (int)(((((int)(ArrayCopy3.N))) - (((int)(1)))))),x10.
                                             array.
                                             Region.makeRectangular((int)(0),
                                                                    (int)(((((int)(ArrayCopy3.N))) - (((int)(1)))))),x10.
                                             array.
                                             Region.makeRectangular((int)(0),
                                                                    (int)(((((int)(ArrayCopy3.N))) - (((int)(1)))))),x10.
                                             array.
                                             Region.makeRectangular((int)(0),
                                                                    (int)(((((int)(ArrayCopy3.N))) - (((int)(1)))))) })/* } */)));
                
//#line 135
final x10.
                  array.
                  Region TestDists =
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
                                                                    (int)(((((int)(ArrayCopy3.
                                                                      dist2.N_DIST_TYPES))) - (((int)(1)))))),x10.
                                             array.
                                             Region.makeRectangular((int)(0),
                                                                    (int)(((((int)(ArrayCopy3.
                                                                      dist2.N_DIST_TYPES))) - (((int)(1)))))) })/* } */)));
                
//#line 137
for (
//#line 137
final x10.core.Iterator<x10.
                                    array.
                                    Point> distP13287 =
                                    TestDists.iterator();
                                  distP13287.hasNext();
                                  ) {
                    
//#line 137
final x10.
                      array.
                      Point distP =
                      ((x10.
                      array.
                      Point)(distP13287.next$G()));
                    
//#line 137
final int dX =
                      distP.apply((int)(0));
                    
//#line 137
final int dY =
                      distP.apply((int)(1));
                    
//#line 138
final x10.
                      array.
                      Dist D =
                      ((x10.
                      array.
                      Dist)(ArrayCopy3.
                      dist2.getDist((int)(dX),
                                    R)));
                    
//#line 139
final x10.
                      array.
                      Dist E =
                      ((x10.
                      array.
                      Dist)(ArrayCopy3.
                      dist2.getDist((int)(dY),
                                    R)));
                    
//#line 140
harness.
                      x10Test.chk((boolean)(((Object)D.
                                                       region).equals(E.
                                                                        region) &&
                                  ((Object)D.
                                             region).equals(R)));
                    
//#line 141
final x10.
                      array.
                      DistArray<java.lang.Integer> A =
                      ((x10.
                      array.
                      DistArray)(x10.
                      array.
                      DistArray.<java.lang.Integer>make(x10.rtt.Types.INT,
                                                        D,
                                                        new x10.core.fun.Fun_0_1<x10.
                                                          array.
                                                          Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                                          array.
                                                          Point id$13188) { return apply(id$13188);}
                                                        public final int apply(final x10.
                                                          array.
                                                          Point id$13188) { {
                                                            
//#line 141
return 0;
                                                        }}
                                                        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                                        }
                                                        })));
                    
//#line 142
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
                                                            
//#line 143
final int i =
                                                              p.apply((int)(0));
                                                            
//#line 143
final int j =
                                                              p.apply((int)(1));
                                                            
//#line 143
final int k =
                                                              p.apply((int)(2));
                                                            
//#line 143
final int l =
                                                              p.apply((int)(3));
                                                            
//#line 143
final int x =
                                                              ((((int)(((((int)((((((int)(((((int)((((((int)(((((int)(i))) * (((int)(ArrayCopy3.N))))))) + (((int)(j)))))))) * (((int)(ArrayCopy3.N))))))) + (((int)(k)))))))) * (((int)(ArrayCopy3.N))))))) + (((int)(l))));
                                                            
//#line 143
return ((((int)(((((int)(x))) * (((int)(x))))))) + (((int)(1))));
                                                        }}
                                                        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                                        }
                                                        })));
                    
//#line 144
this.arrayCopy(A,
                                                B);
                    
//#line 145
this.arrayEqual(A,
                                                 B);
                }
                
//#line 147
return true;
            }
            
            
//#line 150
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
            							ArrayCopy3.main(args);
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
                
//#line 151
new ArrayCopy3().execute();
            }/* } */
            
            
//#line 158
static class dist2
                         extends x10.core.Ref
                         {public static final x10.rtt.RuntimeType<ArrayCopy3.
              dist2>_RTT = new x10.rtt.RuntimeType<ArrayCopy3.
              dist2>(
            /* base class */ArrayCopy3.
              dist2.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
            );
            public x10.rtt.RuntimeType getRTT() {return _RTT;}
            
            
            
                
//#line 160
final static int
                  BLOCK =
                  0;
                
//#line 163
final static int
                  CONSTANT =
                  1;
                
//#line 164
final static int
                  N_DIST_TYPES =
                  2;
                
                
//#line 169
public static x10.
                               array.
                               Dist
                               getDist(
                               final int distType,
                               final x10.
                                 array.
                                 Region r){
                    
//#line 170
switch (distType) {
                        
//#line 171
case ArrayCopy3.
                          dist2.BLOCK:
                            
//#line 171
return x10.
                              array.
                              Dist.makeBlock(r);
                        
//#line 174
case ArrayCopy3.
                          dist2.CONSTANT:
                            
//#line 174
return x10.
                              array.
                              Dist.makeConstant(r,
                                                x10.
                                                  lang.
                                                  Runtime.here());
                        
//#line 175
default:
                            
//#line 175
throw new java.lang.Error();
                    }
                }
                
                public dist2() {
                    super();
                }
            
            }
            
            
            public ArrayCopy3() {
                super();
            }
        
        }
        