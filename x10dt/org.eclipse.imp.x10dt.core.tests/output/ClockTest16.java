
public class ClockTest16
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClockTest16>_RTT = new x10.rtt.RuntimeType<ClockTest16>(
/* base class */ClockTest16.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 41
public boolean
                  run(
                  ){
        
//#line 42
final ClockTest16.
          X x =
          ((ClockTest16.
          X)(new ClockTest16.
          X()));
        
//#line 43
try {{
            
//#line 44
try {{
                
//#line 44
x10.
                  lang.
                  Runtime.startFinish();
                {
                    
//#line 44
x10.
                      lang.
                      Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                           
//#line 45
final x10.
                                             lang.
                                             Clock c0 =
                                             x10.
                                             lang.
                                             Clock.make();
                                           
//#line 46
final x10.
                                             lang.
                                             Clock c1 =
                                             x10.
                                             lang.
                                             Clock.make();
                                           
//#line 47
final x10.core.Rail<x10.
                                             lang.
                                             Clock> ca =
                                             ((x10.core.Rail)(x10.core.RailFactory.<x10.
                                             lang.
                                             Clock>makeRailFromValRail(x10.lang.Clock._RTT, /* template:tuple { */x10.core.RailFactory.<x10.
                                             lang.
                                             Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                             lang.
                                             Clock[] { c0,c1 })/* } */)));
                                           
//#line 55
x10.
                                             lang.
                                             Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                lang.
                                                                Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                lang.
                                                                Clock[] { c1 })/* } */,
                                                              new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                  
//#line 56
final x10.
                                                                    lang.
                                                                    Clock cx =
                                                                    ((x10.
                                                                    lang.
                                                                    Clock)((Object[])ca.value)[1]);
                                                                  
//#line 57
x10.
                                                                    lang.
                                                                    Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                                       lang.
                                                                                       Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                                       lang.
                                                                                       Clock[] { cx })/* } */,
                                                                                     new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                         
//#line 58
x10.
                                                                                           lang.
                                                                                           Runtime.next();
                                                                                     }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                     });
                                                                  
//#line 60
x10.
                                                                    lang.
                                                                    Runtime.next();
                                                              }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                              });
                                           
//#line 64
x10.
                                             lang.
                                             Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                lang.
                                                                Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                lang.
                                                                Clock[] { c1 })/* } */,
                                                              new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                  
//#line 65
final x10.
                                                                    lang.
                                                                    Clock cx =
                                                                    ((x10.
                                                                    lang.
                                                                    Clock)((Object[])ca.value)[x.one()]);
                                                                  
//#line 66
x10.
                                                                    lang.
                                                                    Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                                       lang.
                                                                                       Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                                       lang.
                                                                                       Clock[] { cx })/* } */,
                                                                                     new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                         
//#line 67
x10.
                                                                                           lang.
                                                                                           Runtime.next();
                                                                                     }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                     });
                                                                  
//#line 69
x10.
                                                                    lang.
                                                                    Runtime.next();
                                                              }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                              });
                                           
//#line 73
x10.
                                             lang.
                                             Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                lang.
                                                                Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                lang.
                                                                Clock[] { c1 })/* } */,
                                                              new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                  
//#line 74
final x10.
                                                                    lang.
                                                                    Clock cx =
                                                                    ((x10.
                                                                    lang.
                                                                    Clock)((Object[])ca.value)[x.zero()]);
                                                                  
//#line 75
x10.
                                                                    lang.
                                                                    Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                                       lang.
                                                                                       Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                                       lang.
                                                                                       Clock[] { cx })/* } */,
                                                                                     new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                         
//#line 76
x10.
                                                                                           lang.
                                                                                           Runtime.next();
                                                                                     }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                     });
                                                                  
//#line 78
x10.
                                                                    lang.
                                                                    Runtime.next();
                                                              }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                              });
                                           
//#line 81
final ClockTest16.
                                             foo f0 =
                                             ((ClockTest16.
                                             foo)(new ClockTest16.
                                             Anonymous$30(ClockTest16.this,
                                                          ca,
                                                          x)));
                                           
//#line 90
final ClockTest16.
                                             foo f1 =
                                             ((ClockTest16.
                                             foo)(new ClockTest16.
                                             Anonymous$31(ClockTest16.this,
                                                          ca,
                                                          x)));
                                           
//#line 99
final x10.core.Rail<ClockTest16.
                                             foo> fooArray =
                                             ((x10.core.Rail)(x10.core.RailFactory.<ClockTest16.
                                             foo>makeRailFromValRail(ClockTest16.foo._RTT, ((x10.core.ValRail)
                                                                                             /* template:tuple { */x10.core.RailFactory.<ClockTest16.
                                                                                             foo>makeValRailFromJavaArray(ClockTest16.foo._RTT, new ClockTest16.
                                                                                             foo[] { f0,f1 })/* } */))));
                                           
//#line 102
ClockTest16.
                                             Y.test(new x10.core.fun.Fun_0_1<ClockTest16.
                                                      foo, ClockTest16.
                                                      foo>() {public final ClockTest16.
                                                      foo apply$G(final ClockTest16.
                                                      foo __desugarer__var__465__) { return apply(__desugarer__var__465__);}
                                                    public final ClockTest16.
                                                      foo apply(final ClockTest16.
                                                      foo __desugarer__var__465__) { {
                                                        
//#line 102
if (!x10.core.Ref.at(__desugarer__var__465__, x10.
                                                                           lang.
                                                                           Runtime.here().id)) {
                                                            
//#line 102
throw new java.lang.ClassCastException("ClockTest16.foo{self.home==here}");
                                                        }
                                                        
//#line 102
return __desugarer__var__465__;
                                                    }}
                                                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return ClockTest16.foo._RTT;if (i ==1) return ClockTest16.foo._RTT;return null;
                                                    }
                                                    }.apply(((ClockTest16.
                                                              foo)
                                                              (((ClockTest16.
                                                              foo)((Object[])fooArray.value)[x.one()])))),
                                                    c1);
                                           
//#line 105
ClockTest16.
                                             Y.test(new x10.core.fun.Fun_0_1<ClockTest16.
                                                      foo, ClockTest16.
                                                      foo>() {public final ClockTest16.
                                                      foo apply$G(final ClockTest16.
                                                      foo __desugarer__var__466__) { return apply(__desugarer__var__466__);}
                                                    public final ClockTest16.
                                                      foo apply(final ClockTest16.
                                                      foo __desugarer__var__466__) { {
                                                        
//#line 105
if (!x10.core.Ref.at(__desugarer__var__466__, x10.
                                                                           lang.
                                                                           Runtime.here().id)) {
                                                            
//#line 105
throw new java.lang.ClassCastException("ClockTest16.foo{self.home==here}");
                                                        }
                                                        
//#line 105
return __desugarer__var__466__;
                                                    }}
                                                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return ClockTest16.foo._RTT;if (i ==1) return ClockTest16.foo._RTT;return null;
                                                    }
                                                    }.apply(((ClockTest16.
                                                              foo)
                                                              (((ClockTest16.
                                                              foo)((Object[])fooArray.value)[x.zero()])))),
                                                    c1);
                                           
//#line 108
x10.
                                             lang.
                                             Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                lang.
                                                                Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                lang.
                                                                Clock[] { c1 })/* } */,
                                                              new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                  
//#line 109
final x10.
                                                                    lang.
                                                                    Clock cx =
                                                                    ((x10.
                                                                    lang.
                                                                    Clock)((Object[])ca.value)[0]);
                                                                  
//#line 110
x10.
                                                                    lang.
                                                                    Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                                       lang.
                                                                                       Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                                       lang.
                                                                                       Clock[] { cx })/* } */,
                                                                                     new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                         
//#line 111
x10.
                                                                                           lang.
                                                                                           Runtime.next();
                                                                                     }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                     });
                                                                  
//#line 113
x10.
                                                                    lang.
                                                                    Runtime.next();
                                                              }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                              });
                                           
//#line 116
x10.
                                             lang.
                                             Runtime.next();
                                       }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                       });
                }
            }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
            if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
            java.lang.Throwable __desugarer__var__467__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 44
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__467__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__467__) {
                
//#line 44
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__467__);
            }finally {{
                 
//#line 44
x10.
                   lang.
                   Runtime.stopFinish();
             }}
            
//#line 118
return false;
            }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
            if (__$generated_wrappedex$__.getCause() instanceof x10.
              lang.
              ClockUseException) {
            x10.
              lang.
              ClockUseException e = (x10.
              lang.
              ClockUseException) __$generated_wrappedex$__.getCause();
            {
                
            }
            }
            if (__$generated_wrappedex$__.getCause() instanceof x10.
              lang.
              MultipleExceptions) {
            x10.
              lang.
              MultipleExceptions e = (x10.
              lang.
              MultipleExceptions) __$generated_wrappedex$__.getCause();
            {
                
//#line 121
for (
//#line 121
final x10.core.Iterator<java.lang.Throwable> ex46556 =
                                    (e.exceptions()).iterator();
                                  ex46556.hasNext();
                                  ) {
                    
//#line 121
final java.lang.Throwable ex =
                      ex46556.next$G();
                    
//#line 122
if ((!(((boolean)((x10.lang.ClockUseException._RTT.instanceof$(ex))))))) {
                        
//#line 123
return false;
                    }
                }
            }
            }
            throw __$generated_wrappedex$__;
            }catch (x10.
                      lang.
                      ClockUseException e) {
                
            }catch (x10.
                      lang.
                      MultipleExceptions e) {
                
//#line 121
for (
//#line 121
final x10.core.Iterator<java.lang.Throwable> ex46556 =
                                    (e.exceptions()).iterator();
                                  ex46556.hasNext();
                                  ) {
                    
//#line 121
final java.lang.Throwable ex =
                      ex46556.next$G();
                    
//#line 122
if ((!(((boolean)((x10.lang.ClockUseException._RTT.instanceof$(ex))))))) {
                        
//#line 123
return false;
                    }
                }
            }
        
//#line 126
return true;
        }
    
    
//#line 129
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
    							ClockTest16.main(args);
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
        
//#line 130
new ClockTest16().execute();
    }/* } */
    
    
//#line 136
static class Y
                 extends x10.core.Ref
                 {public static final x10.rtt.RuntimeType<ClockTest16.
      Y>_RTT = new x10.rtt.RuntimeType<ClockTest16.
      Y>(
    /* base class */ClockTest16.
      Y.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
        
//#line 137
static void
                       test(
                       final ClockTest16.
                         foo f,
                       final x10.
                         lang.
                         Clock c){
            
//#line 139
x10.
              lang.
              Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                 lang.
                                 Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                 lang.
                                 Clock[] { c })/* } */,
                               new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                   
//#line 140
f.apply();
                                   
//#line 141
x10.
                                     lang.
                                     Runtime.next();
                               }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                               });
        }
        
        public Y() {
            super();
        }
    
    }
    
    
//#line 157
static interface foo
                 {public static final x10.rtt.RuntimeType<ClockTest16.
      foo>_RTT = new x10.rtt.RuntimeType<ClockTest16.
      foo>(
    /* base class */ClockTest16.
      foo.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    
        
        
//#line 158
void
                       apply(
                       );
    
    }
    
    
//#line 165
static class X
                 extends x10.core.Ref
                 {public static final x10.rtt.RuntimeType<ClockTest16.
      X>_RTT = new x10.rtt.RuntimeType<ClockTest16.
      X>(
    /* base class */ClockTest16.
      X.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 166
final public x10.core.Rail<java.lang.Integer>
          z;
        
        
//#line 167
int
                       zero(
                       ){
            
//#line 167
return ((int[])z.value)[((int[])z.value)[1]];
        }
        
        
//#line 168
int
                       one(
                       ){
            
//#line 168
return ((int[])z.value)[((int[])z.value)[((int[])z.value)[0]]];
        }
        
        
//#line 169
void
                       modify(
                       ){
            
//#line 169
new x10.core.fun.Fun_0_3<x10.core.Rail<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.core.Rail<java.lang.Integer> x,final java.lang.Integer y0,final java.lang.Integer z) { return apply(x,(int)y0,(int)z);}
            public final int apply(final x10.core.Rail<java.lang.Integer> x, final int y0, final int z) { {
                
//#line 169
return ((int[])x.value)[y0] = ((((int)(((int[])x.value)[y0]))) + (((int)(z))));
            }}
            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.core.Rail._RTT, x10.rtt.Types.INT);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;return null;
            }
            }.apply(z,
                    0,
                    1);
        }
        
        public X() {
            super();
            
//#line 166
this.z = ((x10.core.Rail)(x10.core.RailFactory.<java.lang.Integer>makeRailFromValRail(x10.rtt.Types.INT, /* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { 1,0 })/* } */)));
        }
    
    }
    
    
    public ClockTest16() {
        super();
    }
    
    
//#line 81
final private static class Anonymous$30
                extends x10.core.Ref
                  implements ClockTest16.
                               foo
                {public static final x10.rtt.RuntimeType<ClockTest16.
      Anonymous$30>_RTT = new x10.rtt.RuntimeType<ClockTest16.
      Anonymous$30>(
    /* base class */ClockTest16.
      Anonymous$30.class
    , /* parents */ new x10.rtt.Type[] {ClockTest16.foo._RTT, x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 39
final private ClockTest16
          out$;
        
//#line 47
final private x10.core.Rail<x10.
          lang.
          Clock>
          ca;
        
//#line 42
final private ClockTest16.
          X
          x;
        
        
//#line 82
public void
                      apply(
                      ){
            
//#line 83
final x10.
              lang.
              Clock cx =
              ((x10.
              lang.
              Clock)((Object[])this.
                                 ca.value)[new x10.core.fun.Fun_0_1<ClockTest16.
              X, ClockTest16.
              X>() {public final ClockTest16.
              X apply$G(final ClockTest16.
              X __desugarer__var__468__) { return apply(__desugarer__var__468__);}
            public final ClockTest16.
              X apply(final ClockTest16.
              X __desugarer__var__468__) { {
                
//#line 83
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__468__,null))/* } */ &&
                                  !(/* template:equalsequals { */x10.rtt.Equality.equalsequals(x10.lang.Place.place(x10.core.Ref.home(__desugarer__var__468__)),x10.
                                      lang.
                                      Runtime.here())/* } */)) {
                    
//#line 83
throw new java.lang.ClassCastException("ClockTest16.X{self.home==here}");
                }
                
//#line 83
return __desugarer__var__468__;
            }}
            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return ClockTest16.X._RTT;if (i ==1) return ClockTest16.X._RTT;return null;
            }
            }.apply(((ClockTest16.
                      X)
                      this.
                        x)).zero()]);
            
//#line 84
x10.
              lang.
              Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                 lang.
                                 Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                 lang.
                                 Clock[] { cx })/* } */,
                               new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                   
//#line 85
x10.
                                     lang.
                                     Runtime.next();
                               }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                               });
        }
        
        
//#line 81
private Anonymous$30(final ClockTest16 out$,
                                         final x10.core.Rail<x10.
                                           lang.
                                           Clock> ca,
                                         final ClockTest16.
                                           X x) {
            
//#line 81
super();
            
//#line 39
this.out$ = out$;
            
//#line 47
this.ca = ((x10.core.Rail)(ca));
            
//#line 42
this.x = ((ClockTest16.
              X)(x));
        }
    
    }
    
    
//#line 90
final private static class Anonymous$31
                extends x10.core.Ref
                  implements ClockTest16.
                               foo
                {public static final x10.rtt.RuntimeType<ClockTest16.
      Anonymous$31>_RTT = new x10.rtt.RuntimeType<ClockTest16.
      Anonymous$31>(
    /* base class */ClockTest16.
      Anonymous$31.class
    , /* parents */ new x10.rtt.Type[] {ClockTest16.foo._RTT, x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 39
final private ClockTest16
          out$;
        
//#line 47
final private x10.core.Rail<x10.
          lang.
          Clock>
          ca;
        
//#line 42
final private ClockTest16.
          X
          x;
        
        
//#line 91
public void
                      apply(
                      ){
            
//#line 92
final x10.
              lang.
              Clock cx =
              ((x10.
              lang.
              Clock)((Object[])this.
                                 ca.value)[new x10.core.fun.Fun_0_1<ClockTest16.
              X, ClockTest16.
              X>() {public final ClockTest16.
              X apply$G(final ClockTest16.
              X __desugarer__var__469__) { return apply(__desugarer__var__469__);}
            public final ClockTest16.
              X apply(final ClockTest16.
              X __desugarer__var__469__) { {
                
//#line 92
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__469__,null))/* } */ &&
                                  !(/* template:equalsequals { */x10.rtt.Equality.equalsequals(x10.lang.Place.place(x10.core.Ref.home(__desugarer__var__469__)),x10.
                                      lang.
                                      Runtime.here())/* } */)) {
                    
//#line 92
throw new java.lang.ClassCastException("ClockTest16.X{self.home==here}");
                }
                
//#line 92
return __desugarer__var__469__;
            }}
            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return ClockTest16.X._RTT;if (i ==1) return ClockTest16.X._RTT;return null;
            }
            }.apply(((ClockTest16.
                      X)
                      this.
                        x)).one()]);
            
//#line 93
x10.
              lang.
              Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                 lang.
                                 Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                 lang.
                                 Clock[] { cx })/* } */,
                               new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                   
//#line 94
x10.
                                     lang.
                                     Runtime.next();
                               }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                               });
        }
        
        
//#line 90
private Anonymous$31(final ClockTest16 out$,
                                         final x10.core.Rail<x10.
                                           lang.
                                           Clock> ca,
                                         final ClockTest16.
                                           X x) {
            
//#line 90
super();
            
//#line 39
this.out$ = out$;
            
//#line 47
this.ca = ((x10.core.Rail)(ca));
            
//#line 42
this.x = ((ClockTest16.
              X)(x));
        }
    
    }
    
    
    }
    