
public class ArrayCopy1
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ArrayCopy1>_RTT = new x10.rtt.RuntimeType<ArrayCopy1>(
/* base class */ArrayCopy1.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 30
public void
                  arrayEqual(
                  final x10.
                    array.
                    DistArray<java.lang.Integer> A,
                  final x10.
                    array.
                    DistArray<java.lang.Integer> B){
        
//#line 32
final x10.
          array.
          Dist D =
          ((x10.
          array.
          Dist)(A.
                  dist));
        
//#line 33
final x10.
          array.
          Dist E =
          ((x10.
          array.
          Dist)(B.
                  dist));
        
//#line 38
try {{
            
//#line 38
x10.
              lang.
              Runtime.startFinish();
            {
                {
                    
//#line 39
final x10.
                      array.
                      Dist __desugarer__var__119__ =
                      ((x10.
                      array.
                      Dist)(D));
                    
//#line 39
/* template:forloop { */for (x10.core.Iterator __desugarer__var__120____ = (__desugarer__var__119__.places()).iterator(); __desugarer__var__120____.hasNext(); ) {
                    	final  x10.
                      lang.
                      Place __desugarer__var__120__ = (x10.
                      lang.
                      Place) __desugarer__var__120____.next$G();
                    	
{
                        
//#line 39
x10.
                          lang.
                          Runtime.runAsync(__desugarer__var__120__,
                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                               
//#line 39
/* template:forloop { */for (x10.core.Iterator p__ = (__desugarer__var__119__.restriction(x10.
                                                                                                                                                       lang.
                                                                                                                                                       Runtime.here())).iterator(); p__.hasNext(); ) {
                                               	final  x10.
                                                 array.
                                                 Point p = (x10.
                                                 array.
                                                 Point) p__.next$G();
                                               	
{
                                                   
//#line 39
x10.
                                                     lang.
                                                     Runtime.runAsync(x10.
                                                                        lang.
                                                                        Runtime.here(),
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          
//#line 40
final x10.
                                                                            array.
                                                                            Point pa =
                                                                            ((x10.
                                                                            array.
                                                                            Point)(new x10.core.fun.Fun_0_1<x10.
                                                                            array.
                                                                            Point, x10.
                                                                            array.
                                                                            Point>() {public final x10.
                                                                            array.
                                                                            Point apply$G(final x10.
                                                                            array.
                                                                            Point __desugarer__var__117__) { return apply(__desugarer__var__117__);}
                                                                          public final x10.
                                                                            array.
                                                                            Point apply(final x10.
                                                                            array.
                                                                            Point __desugarer__var__117__) { {
                                                                              
//#line 40
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__117__,null))/* } */ &&
                                                                                                !(((int) __desugarer__var__117__.
                                                                                                           rank) ==
                                                                                                  ((int) A.
                                                                                                           dist.
                                                                                                           region.
                                                                                                           rank))) {
                                                                                  
//#line 40
throw new java.lang.ClassCastException("x10.array.Point{self.rank==A.dist.region.rank}");
                                                                              }
                                                                              
//#line 40
return __desugarer__var__117__;
                                                                          }}
                                                                          public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.array.Point._RTT;return null;
                                                                          }
                                                                          }.apply(((x10.
                                                                                    array.
                                                                                    Point)
                                                                                    p))));
                                                                          
//#line 41
final x10.
                                                                            array.
                                                                            Point pb =
                                                                            ((x10.
                                                                            array.
                                                                            Point)(new x10.core.fun.Fun_0_1<x10.
                                                                            array.
                                                                            Point, x10.
                                                                            array.
                                                                            Point>() {public final x10.
                                                                            array.
                                                                            Point apply$G(final x10.
                                                                            array.
                                                                            Point __desugarer__var__118__) { return apply(__desugarer__var__118__);}
                                                                          public final x10.
                                                                            array.
                                                                            Point apply(final x10.
                                                                            array.
                                                                            Point __desugarer__var__118__) { {
                                                                              
//#line 41
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__118__,null))/* } */ &&
                                                                                                !(((int) __desugarer__var__118__.
                                                                                                           rank) ==
                                                                                                  ((int) B.
                                                                                                           dist.
                                                                                                           region.
                                                                                                           rank))) {
                                                                                  
//#line 41
throw new java.lang.ClassCastException("x10.array.Point{self.rank==B.dist.region.rank}");
                                                                              }
                                                                              
//#line 41
return __desugarer__var__118__;
                                                                          }}
                                                                          public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.array.Point._RTT;return null;
                                                                          }
                                                                          }.apply(((x10.
                                                                                    array.
                                                                                    Point)
                                                                                    p))));
                                                                          
//#line 42
final int fp =
                                                                            ((java.lang.Integer)((x10.
                                                                                                    lang.
                                                                                                    Runtime.<java.lang.Integer>evalFuture(x10.rtt.Types.INT,
                                                                                                                                          E.apply(p),
                                                                                                                                          new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                                                                                                                          public final int apply() { {
                                                                                                                                              
//#line 42
return B.apply$G(pb);
                                                                                                                                          }}
                                                                                                                                          public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                                                                                                                          }
                                                                                                                                          })).force$G()));
                                                                          
//#line 43
if (((int) A.apply$G(pa)) !=
                                                                                          ((int) fp)) {
                                                                              
//#line 44
throw new java.lang.Error((((((((((((((((((("****Error: A(") + (p))) + (")= "))) + (A.apply$G(pa)))) + (", B("))) + (p))) + (")="))) + (B.apply$G(pb)))) + (" fp= "))) + (fp)));
                                                                          }
                                                                          
//#line 45
harness.
                                                                            x10Test.chk((boolean)(((int) A.apply$G(pa)) ==
                                                                                        ((int) fp)));
                                                                          
//#line 46
harness.
                                                                            x10Test.chk((boolean)(((int) A.apply$G(pa)) ==
                                                                                        ((int) (x10.
                                                                                                  lang.
                                                                                                  Runtime.<java.lang.Integer>evalFuture(x10.rtt.Types.INT,
                                                                                                                                        E.apply(p),
                                                                                                                                        new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                                                                                                                        public final int apply() { {
                                                                                                                                            
//#line 46
return B.apply$G(pb);
                                                                                                                                        }}
                                                                                                                                        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                                                                                                                        }
                                                                                                                                        })).force$G())));
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
        java.lang.Throwable __desugarer__var__121__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 38
x10.
              lang.
              Runtime.pushException(__desugarer__var__121__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__121__) {
            
//#line 38
x10.
              lang.
              Runtime.pushException(__desugarer__var__121__);
        }finally {{
             
//#line 38
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
        
//#line 63
try {{
            
//#line 63
x10.
              lang.
              Runtime.startFinish();
            {
                {
                    
//#line 64
final x10.
                      array.
                      Dist __desugarer__var__124__ =
                      ((x10.
                      array.
                      Dist)(D));
                    
//#line 64
/* template:forloop { */for (x10.core.Iterator __desugarer__var__125____ = (__desugarer__var__124__.places()).iterator(); __desugarer__var__125____.hasNext(); ) {
                    	final  x10.
                      lang.
                      Place __desugarer__var__125__ = (x10.
                      lang.
                      Place) __desugarer__var__125____.next$G();
                    	
{
                        
//#line 64
x10.
                          lang.
                          Runtime.runAsync(__desugarer__var__125__,
                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                               
//#line 64
/* template:forloop { */for (x10.core.Iterator p__ = (__desugarer__var__124__.restriction(x10.
                                                                                                                                                       lang.
                                                                                                                                                       Runtime.here())).iterator(); p__.hasNext(); ) {
                                               	final  x10.
                                                 array.
                                                 Point p = (x10.
                                                 array.
                                                 Point) p__.next$G();
                                               	
{
                                                   
//#line 64
x10.
                                                     lang.
                                                     Runtime.runAsync(x10.
                                                                        lang.
                                                                        Runtime.here(),
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          
//#line 65
final x10.
                                                                            array.
                                                                            Point pa =
                                                                            ((x10.
                                                                            array.
                                                                            Point)(new x10.core.fun.Fun_0_1<x10.
                                                                            array.
                                                                            Point, x10.
                                                                            array.
                                                                            Point>() {public final x10.
                                                                            array.
                                                                            Point apply$G(final x10.
                                                                            array.
                                                                            Point __desugarer__var__122__) { return apply(__desugarer__var__122__);}
                                                                          public final x10.
                                                                            array.
                                                                            Point apply(final x10.
                                                                            array.
                                                                            Point __desugarer__var__122__) { {
                                                                              
//#line 65
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__122__,null))/* } */ &&
                                                                                                !(((int) __desugarer__var__122__.
                                                                                                           rank) ==
                                                                                                  ((int) A.
                                                                                                           dist.
                                                                                                           region.
                                                                                                           rank))) {
                                                                                  
//#line 65
throw new java.lang.ClassCastException("x10.array.Point{self.rank==A.dist.region.rank}");
                                                                              }
                                                                              
//#line 65
return __desugarer__var__122__;
                                                                          }}
                                                                          public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.array.Point._RTT;return null;
                                                                          }
                                                                          }.apply(((x10.
                                                                                    array.
                                                                                    Point)
                                                                                    p))));
                                                                          
//#line 66
final x10.
                                                                            array.
                                                                            Point pb =
                                                                            ((x10.
                                                                            array.
                                                                            Point)(new x10.core.fun.Fun_0_1<x10.
                                                                            array.
                                                                            Point, x10.
                                                                            array.
                                                                            Point>() {public final x10.
                                                                            array.
                                                                            Point apply$G(final x10.
                                                                            array.
                                                                            Point __desugarer__var__123__) { return apply(__desugarer__var__123__);}
                                                                          public final x10.
                                                                            array.
                                                                            Point apply(final x10.
                                                                            array.
                                                                            Point __desugarer__var__123__) { {
                                                                              
//#line 66
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__123__,null))/* } */ &&
                                                                                                !(((int) __desugarer__var__123__.
                                                                                                           rank) ==
                                                                                                  ((int) B.
                                                                                                           dist.
                                                                                                           region.
                                                                                                           rank))) {
                                                                                  
//#line 66
throw new java.lang.ClassCastException("x10.array.Point{self.rank==B.dist.region.rank}");
                                                                              }
                                                                              
//#line 66
return __desugarer__var__123__;
                                                                          }}
                                                                          public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.array.Point._RTT;return null;
                                                                          }
                                                                          }.apply(((x10.
                                                                                    array.
                                                                                    Point)
                                                                                    p))));
                                                                          
//#line 67
harness.
                                                                            x10Test.chk((boolean)(/* template:equalsequals { */x10.rtt.Equality.equalsequals(D.apply(p),x10.
                                                                                          lang.
                                                                                          Runtime.here())/* } */));
                                                                          
//#line 68
x10.
                                                                            lang.
                                                                            Runtime.runAsync(E.apply(p),
                                                                                             new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                                 
//#line 68
harness.
                                                                                                   x10Test.chk((boolean)(/* template:equalsequals { */x10.rtt.Equality.equalsequals(E.apply(p),x10.
                                                                                                                 lang.
                                                                                                                 Runtime.here())/* } */));
                                                                                             }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                             });
                                                                          
//#line 69
A.set$G((int)(java.lang.Integer)((x10.
                                                                                                                          lang.
                                                                                                                          Runtime.<java.lang.Integer>evalFuture(x10.rtt.Types.INT,
                                                                                                                                                                E.apply(p),
                                                                                                                                                                new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                                                                                                                                                public final int apply() { {
                                                                                                                                                                    
//#line 69
return B.apply$G(pb);
                                                                                                                                                                }}
                                                                                                                                                                public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                                                                                                                                                }
                                                                                                                                                                })).force$G()),
                                                                                              pa);
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
        java.lang.Throwable __desugarer__var__126__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 63
x10.
              lang.
              Runtime.pushException(__desugarer__var__126__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__126__) {
            
//#line 63
x10.
              lang.
              Runtime.pushException(__desugarer__var__126__);
        }finally {{
             
//#line 63
x10.
               lang.
               Runtime.stopFinish();
         }}
        }
    
    
//#line 73
final static int
      N =
      3;
    
    
//#line 79
public boolean
                  run(
                  ){
        
//#line 81
try {{
            
//#line 83
final x10.
              array.
              Region R =
              x10.
              array.
              Region.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                         array.
                                         Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                                         array.
                                         Region[] { x10.
                                         array.
                                         Region.makeRectangular((int)(0),
                                                                (int)(((((int)(ArrayCopy1.N))) - (((int)(1)))))),x10.
                                         array.
                                         Region.makeRectangular((int)(0),
                                                                (int)(((((int)(ArrayCopy1.N))) - (((int)(1)))))),x10.
                                         array.
                                         Region.makeRectangular((int)(0),
                                                                (int)(((((int)(ArrayCopy1.N))) - (((int)(1)))))),x10.
                                         array.
                                         Region.makeRectangular((int)(0),
                                                                (int)(((((int)(ArrayCopy1.N))) - (((int)(1)))))) })/* } */);
            
//#line 84
final x10.
              array.
              Region TestDists =
              x10.
              array.
              Region.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                         array.
                                         Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                                         array.
                                         Region[] { x10.
                                         array.
                                         Region.makeRectangular((int)(0),
                                                                (int)(((((int)(ArrayCopy1.
                                                                  dist2.N_DIST_TYPES))) - (((int)(1)))))),x10.
                                         array.
                                         Region.makeRectangular((int)(0),
                                                                (int)(((((int)(ArrayCopy1.
                                                                  dist2.N_DIST_TYPES))) - (((int)(1)))))) })/* } */);
            
//#line 86
for (
//#line 86
final x10.core.Iterator<x10.
                               array.
                               Point> distP12739 =
                               TestDists.iterator();
                             distP12739.hasNext();
                             ) {
                
//#line 86
final x10.
                  array.
                  Point distP =
                  ((x10.
                    array.
                    Point)
                    distP12739.next$G());
                
//#line 86
final int dX =
                  distP.apply((int)(0));
                
//#line 86
final int dY =
                  distP.apply((int)(1));
                
//#line 87
final x10.
                  array.
                  Dist D =
                  ArrayCopy1.
                  dist2.getDist((int)(dX),
                                R);
                
//#line 88
final x10.
                  array.
                  Dist E =
                  ArrayCopy1.
                  dist2.getDist((int)(dY),
                                R);
                
//#line 89
harness.
                  x10Test.chk((boolean)(((Object)D.
                                                   region).equals(E.
                                                                    region) &&
                              ((Object)D.
                                         region).equals(R)));
                
//#line 90
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
                                                      Point id$12620) { return apply(id$12620);}
                                                    public final int apply(final x10.
                                                      array.
                                                      Point id$12620) { {
                                                        
//#line 90
return 0;
                                                    }}
                                                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                                    }
                                                    })));
                
//#line 91
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
                                                        
//#line 92
final int i =
                                                          p.apply((int)(0));
                                                        
//#line 92
final int j =
                                                          p.apply((int)(1));
                                                        
//#line 92
final int k =
                                                          p.apply((int)(2));
                                                        
//#line 92
final int l =
                                                          p.apply((int)(3));
                                                        
//#line 93
int x =
                                                          ((((int)(((((int)((((((int)(((((int)((((((int)(((((int)(i))) * (((int)(ArrayCopy1.N))))))) + (((int)(j)))))))) * (((int)(ArrayCopy1.N))))))) + (((int)(k)))))))) * (((int)(ArrayCopy1.N))))))) + (((int)(l))));
                                                        
//#line 94
return ((((int)(((((int)(x))) * (((int)(x))))))) + (((int)(1))));
                                                    }}
                                                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                                    }
                                                    })));
                
//#line 97
this.arrayCopy(A,
                                           B);
                
//#line 98
this.arrayEqual(A,
                                            B);
            }
            
//#line 101
return true;
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.Error) {
        final java.lang.Error e = (java.lang.Error) __$generated_wrappedex$__.getCause();
        {
            
//#line 105
x10.
              io.
              Console.OUT.println(e.toString());
            
//#line 106
return false;
        }
        }
        throw __$generated_wrappedex$__;
        }catch (final java.lang.Error e) {
            
//#line 105
x10.
              io.
              Console.OUT.println(e.toString());
            
//#line 106
return false;
        }
    }
    
    
//#line 110
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
    							ArrayCopy1.main(args);
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
        
//#line 111
new ArrayCopy1().execute();
    }/* } */
    
    
//#line 118
static class dist2
                 extends x10.core.Ref
                 {public static final x10.rtt.RuntimeType<ArrayCopy1.
      dist2>_RTT = new x10.rtt.RuntimeType<ArrayCopy1.
      dist2>(
    /* base class */ArrayCopy1.
      dist2.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 120
final static int
          BLOCK =
          0;
        
//#line 123
final static int
          CONSTANT =
          1;
        
//#line 126
final static int
          N_DIST_TYPES =
          2;
        
        
//#line 131
public static x10.
                       array.
                       Dist
                       getDist(
                       final int distType,
                       final x10.
                         array.
                         Region r){
            
//#line 132
switch (distType) {
                
//#line 133
case ArrayCopy1.
                  dist2.BLOCK:
                    
//#line 133
return x10.
                      array.
                      Dist.makeBlock(r,
                                     (int)(0));
                
//#line 136
case ArrayCopy1.
                  dist2.CONSTANT:
                    
//#line 136
return x10.
                      array.
                      Dist.makeConstant(r,
                                        x10.
                                          lang.
                                          Runtime.here());
                
//#line 139
default:
                    
//#line 139
throw new java.lang.Error();
            }
        }
        
        public dist2() {
            super();
        }
    
    }
    
    
    public ArrayCopy1() {
        super();
    }
    
    }
    