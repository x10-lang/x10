
public class ClockTest16a
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClockTest16a>_RTT = new x10.rtt.RuntimeType<ClockTest16a>(
/* base class */ClockTest16a.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 44
public boolean
                  run(
                  ){
        
//#line 45
try {{
            
//#line 46
final ClockTest16a.
              X x =
              ((ClockTest16a.
              X)(new ClockTest16a.
              X()));
            
//#line 47
try {{
                
//#line 47
x10.
                  lang.
                  Runtime.startFinish();
                {
                    
//#line 47
x10.
                      lang.
                      Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                           
//#line 48
final x10.
                                             lang.
                                             Clock c0 =
                                             x10.
                                             lang.
                                             Clock.make();
                                           
//#line 49
final x10.
                                             lang.
                                             Clock c1 =
                                             x10.
                                             lang.
                                             Clock.make();
                                           
//#line 50
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
                                           
//#line 51
(((x10.
                                                          lang.
                                                          Clock)((Object[])ca.value)[0])).drop();
                                           {
                                               
//#line 60
final x10.
                                                 lang.
                                                 Clock cx =
                                                 ((x10.
                                                 lang.
                                                 Clock)((Object[])ca.value)[1]);
                                               
//#line 61
x10.
                                                 lang.
                                                 Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                    lang.
                                                                    Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                    lang.
                                                                    Clock[] { cx })/* } */,
                                                                  new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                      
//#line 62
x10.
                                                                        lang.
                                                                        Runtime.next();
                                                                  }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                  });
                                           }
                                           {
                                               
//#line 69
final x10.
                                                 lang.
                                                 Clock cx =
                                                 ((x10.
                                                 lang.
                                                 Clock)((Object[])ca.value)[x.one()]);
                                               
//#line 70
x10.
                                                 lang.
                                                 Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                    lang.
                                                                    Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                    lang.
                                                                    Clock[] { cx })/* } */,
                                                                  new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                      
//#line 71
x10.
                                                                        lang.
                                                                        Runtime.next();
                                                                  }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                  });
                                           }
                                           
//#line 75
ClockTest16a.
                                             foo f0 =
                                             ((ClockTest16a.
                                             foo)(new ClockTest16a.
                                             Anonymous$32(ClockTest16a.this,
                                                          ca,
                                                          x)));
                                           
//#line 84
ClockTest16a.
                                             foo f1 =
                                             ((ClockTest16a.
                                             foo)(new ClockTest16a.
                                             Anonymous$33(ClockTest16a.this,
                                                          ca,
                                                          x)));
                                           
//#line 93
final x10.core.Rail<ClockTest16a.
                                             foo> fooArray =
                                             ((x10.core.Rail)(x10.core.RailFactory.<ClockTest16a.
                                             foo>makeRailFromValRail(ClockTest16a.foo._RTT, ((x10.core.ValRail)
                                                                                              /* template:tuple { */x10.core.RailFactory.<ClockTest16a.
                                                                                              foo>makeValRailFromJavaArray(ClockTest16a.foo._RTT, new ClockTest16a.
                                                                                              foo[] { f0,f1 })/* } */))));
                                           
//#line 97
ClockTest16a.
                                             Y.test(new x10.core.fun.Fun_0_1<ClockTest16a.
                                                      foo, ClockTest16a.
                                                      foo>() {public final ClockTest16a.
                                                      foo apply$G(final ClockTest16a.
                                                      foo __desugarer__var__470__) { return apply(__desugarer__var__470__);}
                                                    public final ClockTest16a.
                                                      foo apply(final ClockTest16a.
                                                      foo __desugarer__var__470__) { {
                                                        
//#line 97
if (!x10.core.Ref.at(__desugarer__var__470__, x10.
                                                                          lang.
                                                                          Runtime.here().id)) {
                                                            
//#line 97
throw new java.lang.ClassCastException("ClockTest16a.foo{self.home==here}");
                                                        }
                                                        
//#line 97
return __desugarer__var__470__;
                                                    }}
                                                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return ClockTest16a.foo._RTT;if (i ==1) return ClockTest16a.foo._RTT;return null;
                                                    }
                                                    }.apply(((ClockTest16a.
                                                              foo)
                                                              (((ClockTest16a.
                                                              foo)((Object[])fooArray.value)[x.one()])))));
                                           
//#line 99
x10.
                                             io.
                                             Console.OUT.println("point #1");
                                           
//#line 102
ClockTest16a.
                                             Y.test(new x10.core.fun.Fun_0_1<ClockTest16a.
                                                      foo, ClockTest16a.
                                                      foo>() {public final ClockTest16a.
                                                      foo apply$G(final ClockTest16a.
                                                      foo __desugarer__var__471__) { return apply(__desugarer__var__471__);}
                                                    public final ClockTest16a.
                                                      foo apply(final ClockTest16a.
                                                      foo __desugarer__var__471__) { {
                                                        
//#line 102
if (!x10.core.Ref.at(__desugarer__var__471__, x10.
                                                                           lang.
                                                                           Runtime.here().id)) {
                                                            
//#line 102
throw new java.lang.ClassCastException("ClockTest16a.foo{self.home==here}");
                                                        }
                                                        
//#line 102
return __desugarer__var__471__;
                                                    }}
                                                    public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return ClockTest16a.foo._RTT;if (i ==1) return ClockTest16a.foo._RTT;return null;
                                                    }
                                                    }.apply(((ClockTest16a.
                                                              foo)
                                                              (((ClockTest16a.
                                                              foo)((Object[])fooArray.value)[x.zero()])))));
                                           
//#line 104
x10.
                                             io.
                                             Console.OUT.println("point #2");
                                           {
                                               
//#line 108
final x10.
                                                 lang.
                                                 Clock cx =
                                                 ((x10.
                                                 lang.
                                                 Clock)((Object[])ca.value)[x.zero()]);
                                               
//#line 109
x10.
                                                 lang.
                                                 Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                    lang.
                                                                    Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                    lang.
                                                                    Clock[] { cx })/* } */,
                                                                  new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                      
//#line 110
x10.
                                                                        lang.
                                                                        Runtime.next();
                                                                  }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                  });
                                           }
                                           
//#line 114
x10.
                                             io.
                                             Console.OUT.println("point #3");
                                           {
                                               
//#line 118
final x10.
                                                 lang.
                                                 Clock cx =
                                                 ((x10.
                                                 lang.
                                                 Clock)((Object[])ca.value)[0]);
                                               
//#line 119
x10.
                                                 lang.
                                                 Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                    lang.
                                                                    Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                    lang.
                                                                    Clock[] { cx })/* } */,
                                                                  new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                      
//#line 120
x10.
                                                                        lang.
                                                                        Runtime.next();
                                                                  }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                  });
                                           }
                                       }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                       });
                }
            }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
            if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
            java.lang.Throwable __desugarer__var__472__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 47
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__472__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__472__) {
                
//#line 47
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__472__);
            }finally {{
                 
//#line 47
x10.
                   lang.
                   Runtime.stopFinish();
             }}
            }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
            if (__$generated_wrappedex$__.getCause() instanceof x10.
              lang.
              ClockUseException) {
            final x10.
              lang.
              ClockUseException e = (x10.
              lang.
              ClockUseException) __$generated_wrappedex$__.getCause();
            {
                
//#line 125
return true;
            }
            }
            throw __$generated_wrappedex$__;
            }catch (final x10.
                      lang.
                      ClockUseException e) {
                
//#line 125
return true;
            }
        
//#line 127
return false;
        }
    
    
//#line 130
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
    							ClockTest16a.main(args);
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
        
//#line 131
new ClockTest16a().execute();
    }/* } */
    
    
//#line 137
static class Y
                 extends x10.core.Ref
                 {public static final x10.rtt.RuntimeType<ClockTest16a.
      Y>_RTT = new x10.rtt.RuntimeType<ClockTest16a.
      Y>(
    /* base class */ClockTest16a.
      Y.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
        
//#line 138
static void
                       test(
                       final ClockTest16a.
                         foo f){
            {
                
//#line 140
f.apply();
                
//#line 142
x10.
                  lang.
                  Runtime.next();
            }
        }
        
        public Y() {
            super();
        }
    
    }
    
    
//#line 158
static interface foo
                 {public static final x10.rtt.RuntimeType<ClockTest16a.
      foo>_RTT = new x10.rtt.RuntimeType<ClockTest16a.
      foo>(
    /* base class */ClockTest16a.
      foo.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    
        
        
//#line 159
void
                       apply(
                       );
    
    }
    
    
//#line 166
static class X
                 extends x10.core.Ref
                 {public static final x10.rtt.RuntimeType<ClockTest16a.
      X>_RTT = new x10.rtt.RuntimeType<ClockTest16a.
      X>(
    /* base class */ClockTest16a.
      X.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 167
public x10.core.Rail<java.lang.Integer>
          z;
        
        
//#line 168
int
                       zero(
                       ){
            
//#line 168
return ((int[])z.value)[((int[])z.value)[((int[])z.value)[1]]];
        }
        
        
//#line 169
int
                       one(
                       ){
            
//#line 169
return ((int[])z.value)[((int[])z.value)[((int[])z.value)[0]]];
        }
        
        
//#line 170
void
                       modify(
                       ){
            
//#line 170
new x10.core.fun.Fun_0_3<x10.core.Rail<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.core.Rail<java.lang.Integer> x,final java.lang.Integer y0,final java.lang.Integer z) { return apply(x,(int)y0,(int)z);}
            public final int apply(final x10.core.Rail<java.lang.Integer> x, final int y0, final int z) { {
                
//#line 170
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
            
//#line 167
this.z = ((x10.core.Rail)(x10.core.RailFactory.<java.lang.Integer>makeRailFromValRail(x10.rtt.Types.INT, /* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { 1,0 })/* } */)));
        }
    
    }
    
    
    public ClockTest16a() {
        super();
    }
    
    
//#line 75
final private static class Anonymous$32
                extends x10.core.Ref
                  implements ClockTest16a.
                               foo
                {public static final x10.rtt.RuntimeType<ClockTest16a.
      Anonymous$32>_RTT = new x10.rtt.RuntimeType<ClockTest16a.
      Anonymous$32>(
    /* base class */ClockTest16a.
      Anonymous$32.class
    , /* parents */ new x10.rtt.Type[] {ClockTest16a.foo._RTT, x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 42
final private ClockTest16a
          out$;
        
//#line 50
final private x10.core.Rail<x10.
          lang.
          Clock>
          ca;
        
//#line 46
final private ClockTest16a.
          X
          x;
        
        
//#line 76
public void
                      apply(
                      ){
            
//#line 77
final x10.
              lang.
              Clock cx =
              ((x10.
              lang.
              Clock)((Object[])this.
                                 ca.value)[new x10.core.fun.Fun_0_1<ClockTest16a.
              X, ClockTest16a.
              X>() {public final ClockTest16a.
              X apply$G(final ClockTest16a.
              X __desugarer__var__473__) { return apply(__desugarer__var__473__);}
            public final ClockTest16a.
              X apply(final ClockTest16a.
              X __desugarer__var__473__) { {
                
//#line 77
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__473__,null))/* } */ &&
                                  !(/* template:equalsequals { */x10.rtt.Equality.equalsequals(x10.lang.Place.place(x10.core.Ref.home(__desugarer__var__473__)),x10.
                                      lang.
                                      Runtime.here())/* } */)) {
                    
//#line 77
throw new java.lang.ClassCastException("ClockTest16a.X{self.home==here}");
                }
                
//#line 77
return __desugarer__var__473__;
            }}
            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return ClockTest16a.X._RTT;if (i ==1) return ClockTest16a.X._RTT;return null;
            }
            }.apply(((ClockTest16a.
                      X)
                      this.
                        x)).zero()]);
            
//#line 78
x10.
              lang.
              Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                 lang.
                                 Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                 lang.
                                 Clock[] { cx })/* } */,
                               new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                   
//#line 79
x10.
                                     lang.
                                     Runtime.next();
                               }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                               });
        }
        
        
//#line 75
private Anonymous$32(final ClockTest16a out$,
                                         final x10.core.Rail<x10.
                                           lang.
                                           Clock> ca,
                                         final ClockTest16a.
                                           X x) {
            
//#line 75
super();
            
//#line 42
this.out$ = out$;
            
//#line 50
this.ca = ((x10.core.Rail)(ca));
            
//#line 46
this.x = ((ClockTest16a.
              X)(x));
        }
    
    }
    
    
//#line 84
final private static class Anonymous$33
                extends x10.core.Ref
                  implements ClockTest16a.
                               foo
                {public static final x10.rtt.RuntimeType<ClockTest16a.
      Anonymous$33>_RTT = new x10.rtt.RuntimeType<ClockTest16a.
      Anonymous$33>(
    /* base class */ClockTest16a.
      Anonymous$33.class
    , /* parents */ new x10.rtt.Type[] {ClockTest16a.foo._RTT, x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 42
final private ClockTest16a
          out$;
        
//#line 50
final private x10.core.Rail<x10.
          lang.
          Clock>
          ca;
        
//#line 46
final private ClockTest16a.
          X
          x;
        
        
//#line 85
public void
                      apply(
                      ){
            
//#line 86
final x10.
              lang.
              Clock cx =
              ((x10.
              lang.
              Clock)((Object[])this.
                                 ca.value)[new x10.core.fun.Fun_0_1<ClockTest16a.
              X, ClockTest16a.
              X>() {public final ClockTest16a.
              X apply$G(final ClockTest16a.
              X __desugarer__var__474__) { return apply(__desugarer__var__474__);}
            public final ClockTest16a.
              X apply(final ClockTest16a.
              X __desugarer__var__474__) { {
                
//#line 86
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__474__,null))/* } */ &&
                                  !(/* template:equalsequals { */x10.rtt.Equality.equalsequals(x10.lang.Place.place(x10.core.Ref.home(__desugarer__var__474__)),x10.
                                      lang.
                                      Runtime.here())/* } */)) {
                    
//#line 86
throw new java.lang.ClassCastException("ClockTest16a.X{self.home==here}");
                }
                
//#line 86
return __desugarer__var__474__;
            }}
            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return ClockTest16a.X._RTT;if (i ==1) return ClockTest16a.X._RTT;return null;
            }
            }.apply(((ClockTest16a.
                      X)
                      this.
                        x)).one()]);
            
//#line 87
x10.
              lang.
              Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                 lang.
                                 Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                 lang.
                                 Clock[] { cx })/* } */,
                               new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                   
//#line 88
x10.
                                     lang.
                                     Runtime.next();
                               }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                               });
        }
        
        
//#line 84
private Anonymous$33(final ClockTest16a out$,
                                         final x10.core.Rail<x10.
                                           lang.
                                           Clock> ca,
                                         final ClockTest16a.
                                           X x) {
            
//#line 84
super();
            
//#line 42
this.out$ = out$;
            
//#line 50
this.ca = ((x10.core.Rail)(ca));
            
//#line 46
this.x = ((ClockTest16a.
              X)(x));
        }
    
    }
    
    
    }
    