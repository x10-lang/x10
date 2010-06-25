
public class ClockTest18
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClockTest18>_RTT = new x10.rtt.RuntimeType<ClockTest18>(
/* base class */ClockTest18.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 25
public boolean
                  run(
                  ){
        
//#line 26
try {{
            
//#line 27
try {{
                
//#line 27
x10.
                  lang.
                  Runtime.startFinish();
                {
                    
//#line 29
final x10.
                      lang.
                      Clock c0 =
                      x10.
                      lang.
                      Clock.make();
                    
//#line 30
ClockTest18.
                      X x =
                      ((ClockTest18.
                      X)(new ClockTest18.
                      X()));
                    
//#line 32
ClockTest18.
                      foo f0 =
                      ((ClockTest18.
                      foo)(new ClockTest18.
                      Anonymous$40(this)));
                    
//#line 41
ClockTest18.
                      foo f1 =
                      ((ClockTest18.
                      foo)(new ClockTest18.
                      Anonymous$41(this,
                                   c0)));
                    
//#line 54
x10.core.Rail<ClockTest18.
                      foo> fooArray =
                      ((x10.core.Rail)(x10.core.RailFactory.<ClockTest18.
                      foo>makeRailFromValRail(ClockTest18.foo._RTT, /* template:tuple { */x10.core.RailFactory.<ClockTest18.
                      foo>makeValRailFromJavaArray(ClockTest18.foo._RTT, new ClockTest18.
                      foo[] { f0,f1 })/* } */)));
                    
//#line 55
x10.
                      io.
                      Console.OUT.println("#A0 before resume");
                    
//#line 56
c0.resume();
                    
//#line 57
x10.
                      io.
                      Console.OUT.println("#A0 before spawning A3");
                    
//#line 58
ClockTest18.
                      Y.test(((ClockTest18.
                               foo)((Object[])fooArray.value)[x.zero()]));
                    
//#line 59
x10.
                      io.
                      Console.OUT.println("#A0 before spawning A2");
                    
//#line 60
ClockTest18.
                      Y.test(((ClockTest18.
                               foo)((Object[])fooArray.value)[x.one()]));
                    
//#line 61
x10.
                      io.
                      Console.OUT.println("#A0 before spawning A1");
                    
//#line 62
x10.
                      lang.
                      Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                         lang.
                                         Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                         lang.
                                         Clock[] { c0 })/* } */,
                                       new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                           
//#line 62
x10.
                                             io.
                                             Console.OUT.println("#A1: hello from A1");
                                       }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                       });
                    
//#line 63
x10.
                      io.
                      Console.OUT.println("#A0 before next");
                    
//#line 64
x10.
                      lang.
                      Runtime.next();
                    
//#line 65
x10.
                      io.
                      Console.OUT.println("#A0 after next");
                }
            }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
            if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
            java.lang.Throwable __desugarer__var__482__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 27
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__482__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__482__) {
                
//#line 27
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__482__);
            }finally {{
                 
//#line 27
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
                
//#line 68
x10.
                  io.
                  Console.OUT.println("ClockUseException");
                
//#line 69
return true;
            }
            }
            throw __$generated_wrappedex$__;
            }catch (final x10.
                      lang.
                      ClockUseException e) {
                
//#line 68
x10.
                  io.
                  Console.OUT.println("ClockUseException");
                
//#line 69
return true;
            }
        
//#line 71
return false;
        }
    
    
//#line 74
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
    							ClockTest18.main(args);
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
        
//#line 75
new ClockTest18().execute();
    }/* } */
    
    
//#line 81
static class Y
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<ClockTest18.
      Y>_RTT = new x10.rtt.RuntimeType<ClockTest18.
      Y>(
    /* base class */ClockTest18.
      Y.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
        
//#line 82
static void
                      test(
                      ClockTest18.
                        foo f){
            
//#line 83
f.apply();
        }
        
        public Y() {
            super();
        }
    
    }
    
    
//#line 98
static interface foo
                {public static final x10.rtt.RuntimeType<ClockTest18.
      foo>_RTT = new x10.rtt.RuntimeType<ClockTest18.
      foo>(
    /* base class */ClockTest18.
      foo.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    
        
        
//#line 99
void
                      apply(
                      );
    
    }
    
    
//#line 106
static class X
                 extends x10.core.Ref
                 {public static final x10.rtt.RuntimeType<ClockTest18.
      X>_RTT = new x10.rtt.RuntimeType<ClockTest18.
      X>(
    /* base class */ClockTest18.
      X.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 107
public x10.core.Rail<java.lang.Integer>
          z;
        
        
//#line 108
int
                       zero(
                       ){
            
//#line 108
return ((int[])z.value)[((int[])z.value)[((int[])z.value)[1]]];
        }
        
        
//#line 109
int
                       one(
                       ){
            
//#line 109
return ((int[])z.value)[((int[])z.value)[((int[])z.value)[0]]];
        }
        
        
//#line 110
void
                       modify(
                       ){
            
//#line 110
new x10.core.fun.Fun_0_3<x10.core.Rail<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.core.Rail<java.lang.Integer> x,final java.lang.Integer y0,final java.lang.Integer z) { return apply(x,(int)y0,(int)z);}
            public final int apply(final x10.core.Rail<java.lang.Integer> x, final int y0, final int z) { {
                
//#line 110
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
            
//#line 107
this.z = ((x10.core.Rail)(x10.core.RailFactory.<java.lang.Integer>makeRailFromValRail(x10.rtt.Types.INT, /* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { 1,0 })/* } */)));
        }
    
    }
    
    
    public ClockTest18() {
        super();
    }
    
    
//#line 32
final private static class Anonymous$40
                extends x10.core.Ref
                  implements ClockTest18.
                               foo
                {public static final x10.rtt.RuntimeType<ClockTest18.
      Anonymous$40>_RTT = new x10.rtt.RuntimeType<ClockTest18.
      Anonymous$40>(
    /* base class */ClockTest18.
      Anonymous$40.class
    , /* parents */ new x10.rtt.Type[] {ClockTest18.foo._RTT, x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 23
final private ClockTest18
          out$;
        
        
//#line 33
public void
                      apply(
                      ){
            
//#line 35
x10.
              lang.
              Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                   
//#line 36
x10.
                                     io.
                                     Console.OUT.println("#A3: hello from A3");
                               }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                               });
        }
        
        
//#line 32
private Anonymous$40(final ClockTest18 out$) {
            
//#line 32
super();
            
//#line 23
this.out$ = out$;
        }
    
    }
    
    
//#line 41
final private static class Anonymous$41
                extends x10.core.Ref
                  implements ClockTest18.
                               foo
                {public static final x10.rtt.RuntimeType<ClockTest18.
      Anonymous$41>_RTT = new x10.rtt.RuntimeType<ClockTest18.
      Anonymous$41>(
    /* base class */ClockTest18.
      Anonymous$41.class
    , /* parents */ new x10.rtt.Type[] {ClockTest18.foo._RTT, x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 23
final private ClockTest18
          out$;
        
//#line 29
final private x10.
          lang.
          Clock
          c0;
        
        
//#line 42
public void
                      apply(
                      ){
            
//#line 44
x10.
              lang.
              Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                 lang.
                                 Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                 lang.
                                 Clock[] { this.
                                             c0 })/* } */,
                               new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                   
//#line 45
x10.
                                     io.
                                     Console.OUT.println("#A2 before resume");
                                   
//#line 46
ClockTest18.
                                                 Anonymous$41.this.
                                                 c0.resume();
                                   
//#line 47
x10.
                                     io.
                                     Console.OUT.println("#A2 before next");
                                   
//#line 48
x10.
                                     lang.
                                     Runtime.next();
                                   
//#line 49
x10.
                                     io.
                                     Console.OUT.println("#A2 after next");
                               }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                               });
        }
        
        
//#line 41
private Anonymous$41(final ClockTest18 out$,
                                         final x10.
                                           lang.
                                           Clock c0) {
            
//#line 41
super();
            
//#line 23
this.out$ = out$;
            
//#line 29
this.c0 = c0;
        }
    
    }
    
    
    }
    