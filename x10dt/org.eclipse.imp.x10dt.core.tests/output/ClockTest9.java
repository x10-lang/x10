
public class ClockTest9
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClockTest9>_RTT = new x10.rtt.RuntimeType<ClockTest9>(
/* base class */ClockTest9.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 26
final public static int
      N =
      8;
    
//#line 27
final public static int
      M =
      8;
    
//#line 28
final x10.core.Rail<java.lang.Integer>
      v;
    
    
//#line 30
public boolean
                  run(
                  ){
        
//#line 31
try {{
            
//#line 31
x10.
              lang.
              Runtime.startFinish();
            {
                
//#line 31
x10.
                  lang.
                  Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                       
//#line 32
final x10.
                                         lang.
                                         Clock c =
                                         x10.
                                         lang.
                                         Clock.make();
                                       
//#line 35
/* template:forloop-mult { */
                                       {
                                           x10.array.Region __var42__ = (x10.
                                         array.
                                         Region.makeRectangular((int)(0),
                                                                (int)(((((int)(ClockTest9.N))) - (((int)(1))))))).region();
                                           if (__var42__.rect()) {
                                       	/* Loop: { *//* template:forloop-mult-each { */
                                       for (int __var43__ = __var42__.min(0), __var44__ = __var42__.max(0); __var43__ <= __var44__; __var43__++)
                                       /* } */
                                       /* } */ {
                                       		/* Loop: { *//* template:final-var-assign { */
                                       final int i = __var43__;
                                       /* } */
                                       /* } */
{
                                           
//#line 35
x10.
                                             lang.
                                             Runtime.runAsync(x10.
                                                                lang.
                                                                Runtime.here(),
                                                              /* template:tuple { */x10.core.RailFactory.<x10.
                                                                lang.
                                                                Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                lang.
                                                                Clock[] { c })/* } */,
                                                              new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                  
//#line 36
ClockTest9.this.foreachBody((int)(i),
                                                                                                          c);
                                                              }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                              });
                                       }
                                       	}
                                           } else {
                                       	assert false;
                                           }
                                       }
                                       /* } */
                                       
                                   }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                   });
            }
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
        java.lang.Throwable __desugarer__var__519__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
        {
            
//#line 31
x10.
              lang.
              Runtime.pushException(__desugarer__var__519__);
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.Throwable __desugarer__var__519__) {
            
//#line 31
x10.
              lang.
              Runtime.pushException(__desugarer__var__519__);
        }finally {{
             
//#line 31
x10.
               lang.
               Runtime.stopFinish();
         }}
        
//#line 39
return true;
        }
    
    
//#line 42
void
                  foreachBody(
                  final int i,
                  final x10.
                    lang.
                    Clock c){
        
//#line 43
x10.
          lang.
          Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                             lang.
                             Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                             lang.
                             Clock[] { c })/* } */,
                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                               
//#line 43
try {{
                                   
//#line 43
x10.
                                     lang.
                                     Runtime.startFinish();
                                   {
                                       
//#line 43
x10.
                                         lang.
                                         Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                              
//#line 44
final x10.
                                                                lang.
                                                                Clock d =
                                                                x10.
                                                                lang.
                                                                Clock.make();
                                                              
//#line 47
/* template:forloop-mult { */
                                                              {
                                                                  x10.array.Region __var45__ = (x10.
                                                                array.
                                                                Region.makeRectangular((int)(0),
                                                                                       (int)(((((int)(ClockTest9.M))) - (((int)(1))))))).region();
                                                                  if (__var45__.rect()) {
                                                              	/* Loop: { *//* template:forloop-mult-each { */
                                                              for (int __var46__ = __var45__.min(0), __var47__ = __var45__.max(0); __var46__ <= __var47__; __var46__++)
                                                              /* } */
                                                              /* } */ {
                                                              		/* Loop: { *//* template:final-var-assign { */
                                                              final int j = __var46__;
                                                              /* } */
                                                              /* } */
{
                                                                  
//#line 47
x10.
                                                                    lang.
                                                                    Runtime.runAsync(x10.
                                                                                       lang.
                                                                                       Runtime.here(),
                                                                                     /* template:tuple { */x10.core.RailFactory.<x10.
                                                                                       lang.
                                                                                       Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                                       lang.
                                                                                       Clock[] { d })/* } */,
                                                                                     new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                         
//#line 48
ClockTest9.this.foreachBodyInner((int)(i),
                                                                                                                                      (int)(j),
                                                                                                                                      d);
                                                                                     }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                     });
                                                              }
                                                              	}
                                                                  } else {
                                                              	assert false;
                                                                  }
                                                              }
                                                              /* } */
                                                              
                                                          }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                          });
                                   }
                               }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
                               if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
                               java.lang.Throwable __desugarer__var__520__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                               {
                                   
//#line 43
x10.
                                     lang.
                                     Runtime.pushException(__desugarer__var__520__);
                               }
                               }
                               throw __$generated_wrappedex$__;
                               }catch (java.lang.Throwable __desugarer__var__520__) {
                                   
//#line 43
x10.
                                     lang.
                                     Runtime.pushException(__desugarer__var__520__);
                               }finally {{
                                    
//#line 43
x10.
                                      lang.
                                      Runtime.stopFinish();
                                }}
                               }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                               });
        
//#line 51
x10.
          io.
          Console.OUT.println((("#0a i = ") + (i)));
        
//#line 52
x10.
          lang.
          Runtime.next();
        
//#line 54
x10.
          lang.
          Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                             lang.
                             Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                             lang.
                             Clock[] { c })/* } */,
                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                               
//#line 54
try {{
                                   
//#line 54
x10.
                                     lang.
                                     Runtime.startFinish();
                                   {
                                       
//#line 54
x10.
                                         lang.
                                         Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                              {
                                                                  
//#line 54
final int k48832min48833 =
                                                                    0;
                                                                  
//#line 54
final int k48832max48834 =
                                                                    ((((int)(ClockTest9.N))) - (((int)(1))));
                                                                  
//#line 54
for (
//#line 54
int k48832 =
                                                                                     k48832min48833;
                                                                                   ((((int)(k48832))) <= (((int)(k48832max48834))));
                                                                                   
//#line 54
k48832 += 1) {
                                                                      
//#line 54
final int k =
                                                                        k48832;
                                                                      {
                                                                          
//#line 54
harness.
                                                                            x10Test.chk((boolean)(((int) ((int[])v.value)[k]) ==
                                                                                        ((int) 0)));
                                                                      }
                                                                  }
                                                              }
                                                          }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                          });
                                   }
                               }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
                               if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
                               java.lang.Throwable __desugarer__var__521__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                               {
                                   
//#line 54
x10.
                                     lang.
                                     Runtime.pushException(__desugarer__var__521__);
                               }
                               }
                               throw __$generated_wrappedex$__;
                               }catch (java.lang.Throwable __desugarer__var__521__) {
                                   
//#line 54
x10.
                                     lang.
                                     Runtime.pushException(__desugarer__var__521__);
                               }finally {{
                                    
//#line 54
x10.
                                      lang.
                                      Runtime.stopFinish();
                                }}
                               }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                               });
        
//#line 55
x10.
          io.
          Console.OUT.println((("#0b i = ") + (i)));
        
//#line 56
x10.
          lang.
          Runtime.next();
        }
        
        
//#line 59
void
                      foreachBodyInner(
                      final int i,
                      final int j,
                      final x10.
                        lang.
                        Clock d){
            
//#line 61
x10.
              lang.
              Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                 lang.
                                 Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                 lang.
                                 Clock[] { d })/* } */,
                               new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                   
//#line 61
try {{
                                       
//#line 61
x10.
                                         lang.
                                         Runtime.startFinish();
                                       {
                                           
//#line 61
x10.
                                             lang.
                                             Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                  
//#line 61
try {{
                                                                      
//#line 61
x10.
                                                                        lang.
                                                                        Runtime.lock();
                                                                      {
                                                                          
//#line 61
new x10.core.fun.Fun_0_3<x10.core.Rail<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.core.Rail<java.lang.Integer> x,final java.lang.Integer y0,final java.lang.Integer z) { return apply(x,(int)y0,(int)z);}
                                                                          public final int apply(final x10.core.Rail<java.lang.Integer> x, final int y0, final int z) { {
                                                                              
//#line 61
return ((int[])x.value)[y0] = ((((int)(((int[])x.value)[y0]))) + (((int)(z))));
                                                                          }}
                                                                          public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.core.Rail._RTT, x10.rtt.Types.INT);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;return null;
                                                                          }
                                                                          }.apply(v,
                                                                                  i,
                                                                                  j);
                                                                      }
                                                                  }}finally {{
                                                                        
//#line 61
x10.
                                                                          lang.
                                                                          Runtime.release();
                                                                    }}
                                                                  }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                  });
                                           }
                                       }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
                                       if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
                                       java.lang.Throwable __desugarer__var__522__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                                       {
                                           
//#line 61
x10.
                                             lang.
                                             Runtime.pushException(__desugarer__var__522__);
                                       }
                                       }
                                       throw __$generated_wrappedex$__;
                                       }catch (java.lang.Throwable __desugarer__var__522__) {
                                           
//#line 61
x10.
                                             lang.
                                             Runtime.pushException(__desugarer__var__522__);
                                       }finally {{
                                            
//#line 61
x10.
                                              lang.
                                              Runtime.stopFinish();
                                        }}
                                   }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                   });
                               
//#line 62
x10.
                                 io.
                                 Console.OUT.println((((((("#1 i = ") + (i))) + (" j = "))) + (j)));
                               
//#line 63
x10.
                                 lang.
                                 Runtime.next();
                               
//#line 65
x10.
                                 lang.
                                 Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                    lang.
                                                    Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                    lang.
                                                    Clock[] { d })/* } */,
                                                  new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                      
//#line 65
try {{
                                                          
//#line 65
x10.
                                                            lang.
                                                            Runtime.startFinish();
                                                          {
                                                              
//#line 65
x10.
                                                                lang.
                                                                Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                     
//#line 65
int tmp;
                                                                                     
//#line 65
try {{
                                                                                         
//#line 65
x10.
                                                                                           lang.
                                                                                           Runtime.lock();
                                                                                         {
                                                                                             
//#line 65
tmp = ((int[])v.value)[i];
                                                                                         }
                                                                                     }}finally {{
                                                                                           
//#line 65
x10.
                                                                                             lang.
                                                                                             Runtime.release();
                                                                                       }}
                                                                                     
//#line 65
harness.
                                                                                       x10Test.chk((boolean)(((int) tmp) ==
                                                                                                   ((int) ((((int)(((((int)(ClockTest9.M))) * (((int)((((((int)(ClockTest9.M))) - (((int)(1)))))))))))) / (((int)(2)))))));
                                                                                     }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                     });
                                                              }
                                                          }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
                                                          if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
                                                          java.lang.Throwable __desugarer__var__523__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                                                          {
                                                              
//#line 65
x10.
                                                                lang.
                                                                Runtime.pushException(__desugarer__var__523__);
                                                          }
                                                          }
                                                          throw __$generated_wrappedex$__;
                                                          }catch (java.lang.Throwable __desugarer__var__523__) {
                                                              
//#line 65
x10.
                                                                lang.
                                                                Runtime.pushException(__desugarer__var__523__);
                                                          }finally {{
                                                               
//#line 65
x10.
                                                                 lang.
                                                                 Runtime.stopFinish();
                                                           }}
                                                      }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                      });
                                                  
//#line 66
x10.
                                                    io.
                                                    Console.OUT.println((((((("#2 i = ") + (i))) + (" j = "))) + (j)));
                                                  
//#line 67
x10.
                                                    lang.
                                                    Runtime.next();
                                                  
//#line 69
x10.
                                                    lang.
                                                    Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                       lang.
                                                                       Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                       lang.
                                                                       Clock[] { d })/* } */,
                                                                     new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                         
//#line 69
try {{
                                                                             
//#line 69
x10.
                                                                               lang.
                                                                               Runtime.startFinish();
                                                                             {
                                                                                 
//#line 69
x10.
                                                                                   lang.
                                                                                   Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                                        
//#line 69
try {{
                                                                                                            
//#line 69
x10.
                                                                                                              lang.
                                                                                                              Runtime.lock();
                                                                                                            {
                                                                                                                
//#line 69
new x10.core.fun.Fun_0_3<x10.core.Rail<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.core.Rail<java.lang.Integer> x,final java.lang.Integer y0,final java.lang.Integer z) { return apply(x,(int)y0,(int)z);}
                                                                                                                public final int apply(final x10.core.Rail<java.lang.Integer> x, final int y0, final int z) { {
                                                                                                                    
//#line 69
return ((int[])x.value)[y0] = ((((int)(((int[])x.value)[y0]))) - (((int)(z))));
                                                                                                                }}
                                                                                                                public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.core.Rail._RTT, x10.rtt.Types.INT);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;return null;
                                                                                                                }
                                                                                                                }.apply(v,
                                                                                                                        i,
                                                                                                                        j);
                                                                                                            }
                                                                                                        }}finally {{
                                                                                                              
//#line 69
x10.
                                                                                                                lang.
                                                                                                                Runtime.release();
                                                                                                          }}
                                                                                                        }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                                        });
                                                                                 }
                                                                             }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
                                                                             if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
                                                                             java.lang.Throwable __desugarer__var__524__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
                                                                             {
                                                                                 
//#line 69
x10.
                                                                                   lang.
                                                                                   Runtime.pushException(__desugarer__var__524__);
                                                                             }
                                                                             }
                                                                             throw __$generated_wrappedex$__;
                                                                             }catch (java.lang.Throwable __desugarer__var__524__) {
                                                                                 
//#line 69
x10.
                                                                                   lang.
                                                                                   Runtime.pushException(__desugarer__var__524__);
                                                                             }finally {{
                                                                                  
//#line 69
x10.
                                                                                    lang.
                                                                                    Runtime.stopFinish();
                                                                              }}
                                                                         }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                         });
                                                                     
//#line 70
x10.
                                                                       io.
                                                                       Console.OUT.println((((((("#3 i = ") + (i))) + (" j = "))) + (j)));
                                                                     
//#line 71
x10.
                                                                       lang.
                                                                       Runtime.next();
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
                               							ClockTest9.main(args);
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
                                   
//#line 76
new ClockTest9().executeAsync();
                               }/* } */
                               
                               public ClockTest9() {
                                   super();
                                   
//#line 28
this.v = ((x10.core.Rail)((new java.lang.Object() {final x10.core.Rail<java.lang.Integer> apply(int length) {int[] array = new int[length];for (int x$ = 0; x$ < length; x$++) {final int x = x$;array[x] = 0;}return new x10.core.Rail<java.lang.Integer>(x10.rtt.Types.INT, ClockTest9.N, array);}}.apply(ClockTest9.N))));
                               }
                               
                               }
                               