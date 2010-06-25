
public class ArrayDecl
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ArrayDecl>_RTT = new x10.rtt.RuntimeType<ArrayDecl>(
/* base class */ArrayDecl.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 22
final public static int
      N =
      24;
    
    
//#line 24
public boolean
                  run(
                  ){
        
//#line 26
final x10.
          array.
          DistArray<java.lang.Integer> ia0 =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<java.lang.Integer>make(x10.rtt.Types.INT,
                                            x10.
                                              array.
                                              Dist.makeConstant(x10.
                                                                  array.
                                                                  Region.makeRectangular((int)(0),
                                                                                         (int)(((((int)(ArrayDecl.N))) - (((int)(1)))))),
                                                                x10.
                                                                  lang.
                                                                  Runtime.here()))));
        
//#line 27
final x10.
          lang.
          Place p =
          x10.
          lang.
          Runtime.here();
        
//#line 29
harness.
          x10Test.chk((boolean)(ia0.
                                  dist.equals(x10.
                                                array.
                                                Dist.makeConstant(x10.
                                                                    array.
                                                                    Region.makeRectangular((int)(0),
                                                                                           (int)(((((int)(ArrayDecl.N))) - (((int)(1)))))),
                                                                  p))));
        
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
                      Dist __desugarer__var__196__ =
                      ((x10.
                      array.
                      Dist)(ia0.
                              dist));
                    
//#line 31
/* template:forloop { */for (x10.core.Iterator __desugarer__var__197____ = (__desugarer__var__196__.places()).iterator(); __desugarer__var__197____.hasNext(); ) {
                    	final  x10.
                      lang.
                      Place __desugarer__var__197__ = (x10.
                      lang.
                      Place) __desugarer__var__197____.next$G();
                    	
{
                        
//#line 31
x10.
                          lang.
                          Runtime.runAsync(__desugarer__var__197__,
                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                               
//#line 31
/* template:forloop { */for (x10.core.Iterator id13446__ = (__desugarer__var__196__.restriction(x10.
                                                                                                                                                             lang.
                                                                                                                                                             Runtime.here())).iterator(); id13446__.hasNext(); ) {
                                               	final  x10.
                                                 array.
                                                 Point id13446 = (x10.
                                                 array.
                                                 Point) id13446__.next$G();
                                               	final int i =
                                                 id13446.apply((int)(0));
{
                                                   
//#line 31
x10.
                                                     lang.
                                                     Runtime.runAsync(x10.
                                                                        lang.
                                                                        Runtime.here(),
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          
//#line 31
harness.
                                                                            x10Test.chk((boolean)(((int) ia0.apply$G((int)(i))) ==
                                                                                        ((int) 0)));
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
        java.lang.Throwable __desugarer__var__198__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 31
x10.
              lang.
              Runtime.pushException(__desugarer__var__198__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__198__) {
            
//#line 31
x10.
              lang.
              Runtime.pushException(__desugarer__var__198__);
        }finally {{
             
//#line 31
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 33
final x10.
          array.
          DistArray<java.lang.Integer> v_ia2 =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<java.lang.Integer>make(x10.rtt.Types.INT,
                                            x10.
                                              array.
                                              Dist.makeConstant(x10.
                                                                  array.
                                                                  Region.makeRectangular((int)(0),
                                                                                         (int)(((((int)(ArrayDecl.N))) - (((int)(1)))))),
                                                                x10.
                                                                  lang.
                                                                  Runtime.here()),
                                            new x10.core.fun.Fun_0_1<x10.
                                              array.
                                              Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                              array.
                                              Point id13447) { return apply(id13447);}
                                            public final int apply(final x10.
                                              array.
                                              Point id13447) { {
                                                
//#line 33
final int i =
                                                  id13447.apply((int)(0));
                                                
//#line 33
return i;
                                            }}
                                            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                            }
                                            })));
        
//#line 34
harness.
          x10Test.chk((boolean)(v_ia2.
                                  dist.equals(x10.
                                                array.
                                                Dist.makeConstant(x10.
                                                                    array.
                                                                    Region.makeRectangular((int)(0),
                                                                                           (int)(((((int)(ArrayDecl.N))) - (((int)(1)))))),
                                                                  x10.
                                                                    lang.
                                                                    Runtime.here()))));
        
//#line 35
for (
//#line 35
final x10.core.Iterator<x10.
                           array.
                           Point> id13536 =
                           v_ia2.region().iterator();
                         id13536.hasNext();
                         ) {
            
//#line 35
final x10.
              array.
              Point id13448 =
              ((x10.
                array.
                Point)
                id13536.next$G());
            
//#line 35
final int i =
              id13448.apply((int)(0));
            
//#line 35
harness.
              x10Test.chk((boolean)(((int) v_ia2.apply$G((int)(i))) ==
                          ((int) i)));
        }
        
//#line 37
final x10.
          array.
          DistArray<java.lang.Byte> ia2 =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<java.lang.Byte>make(x10.rtt.Types.BYTE,
                                         x10.
                                           array.
                                           Dist.makeConstant(x10.
                                                               array.
                                                               Region.makeRectangular((int)(0),
                                                                                      (int)(((((int)(ArrayDecl.N))) - (((int)(1)))))),
                                                             (x10.
                                                                lang.
                                                                Runtime.here()).prev().prev()),
                                         new x10.core.fun.Fun_0_1<x10.
                                           array.
                                           Point, java.lang.Byte>() {public final java.lang.Byte apply$G(final x10.
                                           array.
                                           Point id$13451) { return apply(id$13451);}
                                         public final byte apply(final x10.
                                           array.
                                           Point id$13451) { {
                                             
//#line 37
return (((byte)(int)(((int)(0)))));
                                         }}
                                         public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.BYTE;return null;
                                         }
                                         })));
        
//#line 38
harness.
          x10Test.chk((boolean)(ia2.
                                  dist.equals(x10.
                                                array.
                                                Dist.makeConstant(x10.
                                                                    array.
                                                                    Region.makeRectangular((int)(0),
                                                                                           (int)(((((int)(ArrayDecl.N))) - (((int)(1)))))),
                                                                  (x10.
                                                                     lang.
                                                                     Runtime.here()).prev().prev()))));
        
//#line 39
try {{
            
//#line 39
x10.
              lang.
              Runtime.startFinish();
            {
                {
                    
//#line 39
final x10.
                      array.
                      Dist __desugarer__var__199__ =
                      ((x10.
                      array.
                      Dist)(ia2.
                              dist));
                    
//#line 39
/* template:forloop { */for (x10.core.Iterator __desugarer__var__200____ = (__desugarer__var__199__.places()).iterator(); __desugarer__var__200____.hasNext(); ) {
                    	final  x10.
                      lang.
                      Place __desugarer__var__200__ = (x10.
                      lang.
                      Place) __desugarer__var__200____.next$G();
                    	
{
                        
//#line 39
x10.
                          lang.
                          Runtime.runAsync(__desugarer__var__200__,
                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                               
//#line 39
/* template:forloop { */for (x10.core.Iterator id13452__ = (__desugarer__var__199__.restriction(x10.
                                                                                                                                                             lang.
                                                                                                                                                             Runtime.here())).iterator(); id13452__.hasNext(); ) {
                                               	final  x10.
                                                 array.
                                                 Point id13452 = (x10.
                                                 array.
                                                 Point) id13452__.next$G();
                                               	final int i =
                                                 id13452.apply((int)(0));
{
                                                   
//#line 39
x10.
                                                     lang.
                                                     Runtime.runAsync(x10.
                                                                        lang.
                                                                        Runtime.here(),
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          
//#line 39
harness.
                                                                            x10Test.chk((boolean)(((byte) ia2.apply$G((int)(i))) ==
                                                                                        ((byte) ((((byte)(int)(((int)(0)))))))));
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
        java.lang.Throwable __desugarer__var__201__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 39
x10.
              lang.
              Runtime.pushException(__desugarer__var__201__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__201__) {
            
//#line 39
x10.
              lang.
              Runtime.pushException(__desugarer__var__201__);
        }finally {{
             
//#line 39
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 43
final x10.
          array.
          DistArray<java.lang.Double> data1 =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<java.lang.Double>make(x10.rtt.Types.DOUBLE,
                                           x10.
                                             array.
                                             Dist.makeConstant(x10.
                                                                 array.
                                                                 Region.makeRectangular((int)(0),
                                                                                        (int)(16)),
                                                               x10.
                                                                 lang.
                                                                 Runtime.here()),
                                           new x10.core.fun.Fun_0_1<x10.
                                             array.
                                             Point, java.lang.Double>() {public final java.lang.Double apply$G(final x10.
                                             array.
                                             Point id13455) { return apply(id13455);}
                                           public final double apply(final x10.
                                             array.
                                             Point id13455) { {
                                               
//#line 43
final int i =
                                                 id13455.apply((int)(0));
                                               
//#line 43
return ((double)(int)(((int)(i))));
                                           }}
                                           public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.DOUBLE;return null;
                                           }
                                           })));
        
//#line 44
harness.
          x10Test.chk((boolean)(data1.
                                  dist.equals(x10.
                                                array.
                                                Dist.makeConstant(x10.
                                                                    array.
                                                                    Region.makeRectangular((int)(0),
                                                                                           (int)(16)),
                                                                  x10.
                                                                    lang.
                                                                    Runtime.here()))));
        
//#line 45
for (
//#line 45
final x10.core.Iterator<x10.
                           array.
                           Point> id13537 =
                           data1.region().iterator();
                         id13537.hasNext();
                         ) {
            
//#line 45
final x10.
              array.
              Point id13456 =
              ((x10.
                array.
                Point)
                id13537.next$G());
            
//#line 45
final int i =
              id13456.apply((int)(0));
            
//#line 45
harness.
              x10Test.chk((boolean)(((double) data1.apply$G((int)(i))) ==
                          ((double) ((((double)(int)(((int)(i)))))))));
        }
        
//#line 47
final java.lang.String myStr =
          "abcdefghijklmnop";
        
//#line 48
final x10.
          array.
          DistArray<java.lang.Character> data2 =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<java.lang.Character>make(x10.rtt.Types.CHAR,
                                              x10.
                                                array.
                                                Dist.makeConstant(x10.
                                                                    array.
                                                                    Region.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                                               array.
                                                                                               Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                                                                                               array.
                                                                                               Region[] { x10.
                                                                                               array.
                                                                                               Region.makeRectangular((int)(1),
                                                                                                                      (int)(2)),x10.
                                                                                               array.
                                                                                               Region.makeRectangular((int)(1),
                                                                                                                      (int)(3)) })/* } */),
                                                                  x10.
                                                                    lang.
                                                                    Runtime.here()),
                                              new x10.core.fun.Fun_0_1<x10.
                                                array.
                                                Point, java.lang.Character>() {public final java.lang.Character apply$G(final x10.
                                                array.
                                                Point id13457) { return apply(id13457);}
                                              public final char apply(final x10.
                                                array.
                                                Point id13457) { {
                                                  
//#line 48
final int i =
                                                    id13457.apply((int)(0));
                                                  
//#line 48
final int j =
                                                    id13457.apply((int)(1));
                                                  
//#line 48
return (myStr).charAt(((int)(((((int)(i))) * (((int)(j)))))));
                                              }}
                                              public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.CHAR;return null;
                                              }
                                              })));
        
//#line 49
harness.
          x10Test.chk((boolean)(data2.
                                  dist.equals(x10.
                                                array.
                                                Dist.makeConstant(x10.
                                                                    array.
                                                                    Region.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                                               array.
                                                                                               Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                                                                                               array.
                                                                                               Region[] { x10.
                                                                                               array.
                                                                                               Region.makeRectangular((int)(1),
                                                                                                                      (int)(2)),x10.
                                                                                               array.
                                                                                               Region.makeRectangular((int)(1),
                                                                                                                      (int)(3)) })/* } */),
                                                                  x10.
                                                                    lang.
                                                                    Runtime.here()))));
        {
            
//#line 50
final x10.
              array.
              Region p13538 =
              ((x10.
              array.
              Region)(data2.region()));
            
//#line 50
final int j13539min13540 =
              p13538.min((int)(1));
            
//#line 50
final int j13539max13541 =
              p13538.max((int)(1));
            
//#line 50
final int i13542min13543 =
              p13538.min((int)(0));
            
//#line 50
final int i13542max13544 =
              p13538.max((int)(0));
            
//#line 50
for (
//#line 50
int i13542 =
                               i13542min13543;
                             ((((int)(i13542))) <= (((int)(i13542max13544))));
                             
//#line 50
i13542 += 1) {
                
//#line 50
final int i =
                  i13542;
                
//#line 50
for (
//#line 50
int j13539 =
                                   j13539min13540;
                                 ((((int)(j13539))) <= (((int)(j13539max13541))));
                                 
//#line 50
j13539 += 1) {
                    
//#line 50
final int j =
                      j13539;
                    {
                        
//#line 50
harness.
                          x10Test.chk((boolean)(((char) data2.apply$G((int)(i),
                                                                      (int)(j))) ==
                                      ((char) (myStr).charAt(((int)(((((int)(i))) * (((int)(j))))))))));
                    }
                }
            }
        }
        
//#line 54
final x10.
          array.
          DistArray<java.lang.Long> data3 =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<java.lang.Long>make(x10.rtt.Types.LONG,
                                         x10.
                                           array.
                                           Dist.makeConstant(x10.
                                                               array.
                                                               Region.makeRectangular((int)(1),
                                                                                      (int)(11)),
                                                             x10.
                                                               lang.
                                                               Runtime.here()),
                                         new x10.core.fun.Fun_0_1<x10.
                                           array.
                                           Point, java.lang.Long>() {public final java.lang.Long apply$G(final x10.
                                           array.
                                           Point id13461) { return apply(id13461);}
                                         public final long apply(final x10.
                                           array.
                                           Point id13461) { {
                                             
//#line 54
final int i =
                                               id13461.apply((int)(0));
                                             
//#line 54
return ((long)(((int)(((((int)(i))) * (((int)(i))))))));
                                         }}
                                         public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.LONG;return null;
                                         }
                                         })));
        
//#line 55
harness.
          x10Test.chk((boolean)(data3.
                                  dist.equals(x10.
                                                array.
                                                Dist.makeConstant(x10.
                                                                    array.
                                                                    Region.makeRectangular((int)(1),
                                                                                           (int)(11)),
                                                                  x10.
                                                                    lang.
                                                                    Runtime.here()))));
        
//#line 56
for (
//#line 56
final x10.core.Iterator<x10.
                           array.
                           Point> id13545 =
                           data3.region().iterator();
                         id13545.hasNext();
                         ) {
            
//#line 56
final x10.
              array.
              Point id13462 =
              ((x10.
                array.
                Point)
                id13545.next$G());
            
//#line 56
final int i =
              id13462.apply((int)(0));
            
//#line 56
harness.
              x10Test.chk((boolean)(((long) data3.apply$G((int)(i))) ==
                          ((long) ((((long)(((int)(((((int)(i))) * (((int)(i)))))))))))));
        }
        
//#line 58
final x10.
          array.
          Dist D =
          ((x10.
          array.
          Dist)(x10.
          array.
          Dist.makeBlock(x10.
                           array.
                           Region.makeRectangular((int)(0),
                                                  (int)(9)),
                         (int)(0))));
        
//#line 59
final x10.
          array.
          DistArray<java.lang.Float> d =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<java.lang.Float>make(x10.rtt.Types.FLOAT,
                                          D,
                                          new x10.core.fun.Fun_0_1<x10.
                                            array.
                                            Point, java.lang.Float>() {public final java.lang.Float apply$G(final x10.
                                            array.
                                            Point id13465) { return apply(id13465);}
                                          public final float apply(final x10.
                                            array.
                                            Point id13465) { {
                                              
//#line 59
final int i =
                                                id13465.apply((int)(0));
                                              
//#line 59
return (((float)(double)(((double)(((((double)(10.0))) * (((double)(((double)(int)(((int)(i)))))))))))));
                                          }}
                                          public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.FLOAT;return null;
                                          }
                                          })));
        
//#line 60
harness.
          x10Test.chk((boolean)(d.
                                  dist.equals(D)));
        
//#line 61
try {{
            
//#line 61
x10.
              lang.
              Runtime.startFinish();
            {
                {
                    
//#line 61
final x10.
                      array.
                      Dist __desugarer__var__202__ =
                      ((x10.
                      array.
                      Dist)(D));
                    
//#line 61
/* template:forloop { */for (x10.core.Iterator __desugarer__var__203____ = (__desugarer__var__202__.places()).iterator(); __desugarer__var__203____.hasNext(); ) {
                    	final  x10.
                      lang.
                      Place __desugarer__var__203__ = (x10.
                      lang.
                      Place) __desugarer__var__203____.next$G();
                    	
{
                        
//#line 61
x10.
                          lang.
                          Runtime.runAsync(__desugarer__var__203__,
                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                               
//#line 61
/* template:forloop { */for (x10.core.Iterator id13466__ = (__desugarer__var__202__.restriction(x10.
                                                                                                                                                             lang.
                                                                                                                                                             Runtime.here())).iterator(); id13466__.hasNext(); ) {
                                               	final  x10.
                                                 array.
                                                 Point id13466 = (x10.
                                                 array.
                                                 Point) id13466__.next$G();
                                               	final int i =
                                                 id13466.apply((int)(0));
{
                                                   
//#line 61
x10.
                                                     lang.
                                                     Runtime.runAsync(x10.
                                                                        lang.
                                                                        Runtime.here(),
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          
//#line 61
harness.
                                                                            x10Test.chk((boolean)(((float) d.apply$G((int)(i))) ==
                                                                                        ((float) ((((float)(double)(((double)(((((double)(10.0))) * (((double)(((double)(int)(((int)(i)))))))))))))))));
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
        java.lang.Throwable __desugarer__var__204__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 61
x10.
              lang.
              Runtime.pushException(__desugarer__var__204__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__204__) {
            
//#line 61
x10.
              lang.
              Runtime.pushException(__desugarer__var__204__);
        }finally {{
             
//#line 61
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 63
final x10.
          array.
          Dist E =
          ((x10.
          array.
          Dist)(x10.
          array.
          Dist.makeBlock(x10.
                           array.
                           Region.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                                      array.
                                                      Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                                                      array.
                                                      Region[] { x10.
                                                      array.
                                                      Region.makeRectangular((int)(1),
                                                                             (int)(7)),x10.
                                                      array.
                                                      Region.makeRectangular((int)(0),
                                                                             (int)(1)) })/* } */),
                         (int)(1))));
        
//#line 64
final x10.
          array.
          DistArray<java.lang.Short> result1 =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<java.lang.Short>make(x10.rtt.Types.SHORT,
                                          E,
                                          new x10.core.fun.Fun_0_1<x10.
                                            array.
                                            Point, java.lang.Short>() {public final java.lang.Short apply$G(final x10.
                                            array.
                                            Point id13467) { return apply(id13467);}
                                          public final short apply(final x10.
                                            array.
                                            Point id13467) { {
                                              
//#line 64
final int i =
                                                id13467.apply((int)(0));
                                              
//#line 64
final int j =
                                                id13467.apply((int)(1));
                                              
//#line 64
return (((short)(int)(((int)(((((int)(i))) + (((int)(j)))))))));
                                          }}
                                          public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.SHORT;return null;
                                          }
                                          })));
        
//#line 65
harness.
          x10Test.chk((boolean)(result1.
                                  dist.equals(E)));
        
//#line 66
try {{
            
//#line 66
x10.
              lang.
              Runtime.startFinish();
            {
                {
                    
//#line 66
final x10.
                      array.
                      Dist __desugarer__var__205__ =
                      ((x10.
                      array.
                      Dist)(E));
                    
//#line 66
/* template:forloop { */for (x10.core.Iterator __desugarer__var__206____ = (__desugarer__var__205__.places()).iterator(); __desugarer__var__206____.hasNext(); ) {
                    	final  x10.
                      lang.
                      Place __desugarer__var__206__ = (x10.
                      lang.
                      Place) __desugarer__var__206____.next$G();
                    	
{
                        
//#line 66
x10.
                          lang.
                          Runtime.runAsync(__desugarer__var__206__,
                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                               
//#line 66
/* template:forloop { */for (x10.core.Iterator id13468__ = (__desugarer__var__205__.restriction(x10.
                                                                                                                                                             lang.
                                                                                                                                                             Runtime.here())).iterator(); id13468__.hasNext(); ) {
                                               	final  x10.
                                                 array.
                                                 Point id13468 = (x10.
                                                 array.
                                                 Point) id13468__.next$G();
                                               	final int i =
                                                 id13468.apply((int)(0));
final int j =
                                                 id13468.apply((int)(1));
{
                                                   
//#line 66
x10.
                                                     lang.
                                                     Runtime.runAsync(x10.
                                                                        lang.
                                                                        Runtime.here(),
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          
//#line 66
harness.
                                                                            x10Test.chk((boolean)(((short) result1.apply$G((int)(i),
                                                                                                                           (int)(j))) ==
                                                                                        ((short) ((((short)(int)(((int)(((((int)(i))) + (((int)(j)))))))))))));
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
        java.lang.Throwable __desugarer__var__207__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 66
x10.
              lang.
              Runtime.pushException(__desugarer__var__207__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__207__) {
            
//#line 66
x10.
              lang.
              Runtime.pushException(__desugarer__var__207__);
        }finally {{
             
//#line 66
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 68
final x10.
          array.
          DistArray<x10.
          lang.
          Complex> result2 =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<x10.
          lang.
          Complex>make(x10.lang.Complex._RTT,
                       x10.
                         array.
                         Dist.makeConstant(x10.
                                             array.
                                             Region.makeRectangular((int)(0),
                                                                    (int)(((((int)(ArrayDecl.N))) - (((int)(1)))))),
                                           x10.
                                             lang.
                                             Runtime.here()),
                       new x10.core.fun.Fun_0_1<x10.
                         array.
                         Point, x10.
                         lang.
                         Complex>() {public final x10.
                         lang.
                         Complex apply$G(final x10.
                         array.
                         Point id13469) { return apply(id13469);}
                       public final x10.
                         lang.
                         Complex apply(final x10.
                         array.
                         Point id13469) { {
                           
//#line 68
final int i =
                             id13469.apply((int)(0));
                           
//#line 68
return new x10.
                             lang.
                             Complex(((double)(int)(((int)(((((int)(i))) * (((int)(ArrayDecl.N)))))))),
                                     ((double)(int)(((int)((-(((int)(i)))))))));
                       }}
                       public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.lang.Complex._RTT;return null;
                       }
                       })));
        
//#line 69
harness.
          x10Test.chk((boolean)(result2.
                                  dist.equals(x10.
                                                array.
                                                Dist.makeConstant(x10.
                                                                    array.
                                                                    Region.makeRectangular((int)(0),
                                                                                           (int)(((((int)(ArrayDecl.N))) - (((int)(1)))))),
                                                                  x10.
                                                                    lang.
                                                                    Runtime.here()))));
        
//#line 70
try {{
            
//#line 70
x10.
              lang.
              Runtime.startFinish();
            {
                {
                    
//#line 70
final x10.
                      array.
                      Dist __desugarer__var__208__ =
                      ((x10.
                      array.
                      Dist)(result2.
                              dist));
                    
//#line 70
/* template:forloop { */for (x10.core.Iterator __desugarer__var__209____ = (__desugarer__var__208__.places()).iterator(); __desugarer__var__209____.hasNext(); ) {
                    	final  x10.
                      lang.
                      Place __desugarer__var__209__ = (x10.
                      lang.
                      Place) __desugarer__var__209____.next$G();
                    	
{
                        
//#line 70
x10.
                          lang.
                          Runtime.runAsync(__desugarer__var__209__,
                                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                               
//#line 70
/* template:forloop { */for (x10.core.Iterator id13470__ = (__desugarer__var__208__.restriction(x10.
                                                                                                                                                             lang.
                                                                                                                                                             Runtime.here())).iterator(); id13470__.hasNext(); ) {
                                               	final  x10.
                                                 array.
                                                 Point id13470 = (x10.
                                                 array.
                                                 Point) id13470__.next$G();
                                               	final int i =
                                                 id13470.apply((int)(0));
{
                                                   
//#line 70
x10.
                                                     lang.
                                                     Runtime.runAsync(x10.
                                                                        lang.
                                                                        Runtime.here(),
                                                                      new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                          
//#line 70
harness.
                                                                            x10Test.chk((boolean)(/* template:equalsequals { */x10.rtt.Equality.equalsequals(result2.apply$G((int)(i)),(new x10.
                                                                                                                                                                                          lang.
                                                                                                                                                                                          Complex(((double)(int)(((int)(((((int)(i))) * (((int)(ArrayDecl.N)))))))),
                                                                                                                                                                                                  ((double)(int)(((int)((-(((int)(i)))))))))))/* } */));
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
        java.lang.Throwable __desugarer__var__210__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 70
x10.
              lang.
              Runtime.pushException(__desugarer__var__210__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__210__) {
            
//#line 70
x10.
              lang.
              Runtime.pushException(__desugarer__var__210__);
        }finally {{
             
//#line 70
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 72
return true;
        }
        
        
//#line 75
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
        							ArrayDecl.main(args);
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
        public static void main(final x10.core.Rail<java.lang.String> id$13471)  {
            
//#line 76
new ArrayDecl().execute();
        }/* } */
        
        public ArrayDecl() {
            super();
        }
        
        }
        