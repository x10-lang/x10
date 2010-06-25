
public class ClockTest17_MustFailTimeout
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClockTest17_MustFailTimeout>_RTT = new x10.rtt.RuntimeType<ClockTest17_MustFailTimeout>(
/* base class */ClockTest17_MustFailTimeout.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 48
public boolean
                  run(
                  ){
        
//#line 50
final x10.
          lang.
          Clock c0 =
          x10.
          lang.
          Clock.make();
        
//#line 51
ClockTest17_MustFailTimeout.
          X x =
          ((ClockTest17_MustFailTimeout.
          X)(new ClockTest17_MustFailTimeout.
          X()));
        
//#line 53
ClockTest17_MustFailTimeout.
          foo f0 =
          ((ClockTest17_MustFailTimeout.
          foo)(new ClockTest17_MustFailTimeout.
          Anonymous$36(this)));
        
//#line 61
ClockTest17_MustFailTimeout.
          foo f1 =
          ((ClockTest17_MustFailTimeout.
          foo)(new ClockTest17_MustFailTimeout.
          Anonymous$37(this,
                       c0)));
        
//#line 72
final x10.core.Rail<ClockTest17_MustFailTimeout.
          foo> fooArray =
          ((x10.core.Rail)(x10.core.RailFactory.<ClockTest17_MustFailTimeout.
          foo>makeRailFromValRail(ClockTest17_MustFailTimeout.foo._RTT, ((x10.core.ValRail)
                                                                          /* template:tuple { */x10.core.RailFactory.<ClockTest17_MustFailTimeout.
                                                                          foo>makeValRailFromJavaArray(ClockTest17_MustFailTimeout.foo._RTT, new ClockTest17_MustFailTimeout.
                                                                          foo[] { f0,f1 })/* } */))));
        
//#line 75
ClockTest17_MustFailTimeout.
          Y.test(new x10.core.fun.Fun_0_1<ClockTest17_MustFailTimeout.
                   foo, ClockTest17_MustFailTimeout.
                   foo>() {public final ClockTest17_MustFailTimeout.
                   foo apply$G(final ClockTest17_MustFailTimeout.
                   foo __desugarer__var__478__) { return apply(__desugarer__var__478__);}
                 public final ClockTest17_MustFailTimeout.
                   foo apply(final ClockTest17_MustFailTimeout.
                   foo __desugarer__var__478__) { {
                     
//#line 75
if (!x10.core.Ref.at(__desugarer__var__478__, x10.
                                       lang.
                                       Runtime.here().id)) {
                         
//#line 75
throw new java.lang.ClassCastException("ClockTest17_MustFailTimeout.foo{self.home==here}");
                     }
                     
//#line 75
return __desugarer__var__478__;
                 }}
                 public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return ClockTest17_MustFailTimeout.foo._RTT;if (i ==1) return ClockTest17_MustFailTimeout.foo._RTT;return null;
                 }
                 }.apply(((ClockTest17_MustFailTimeout.
                           foo)
                           (((ClockTest17_MustFailTimeout.
                           foo)((Object[])fooArray.value)[x.zero()])))));
        
//#line 78
x10.
          io.
          Console.OUT.println("#0a before next");
        
//#line 79
x10.
          lang.
          Runtime.next();
        
//#line 80
x10.
          io.
          Console.OUT.println("#0a after next");
        
//#line 83
ClockTest17_MustFailTimeout.
          Y.test(new x10.core.fun.Fun_0_1<ClockTest17_MustFailTimeout.
                   foo, ClockTest17_MustFailTimeout.
                   foo>() {public final ClockTest17_MustFailTimeout.
                   foo apply$G(final ClockTest17_MustFailTimeout.
                   foo __desugarer__var__479__) { return apply(__desugarer__var__479__);}
                 public final ClockTest17_MustFailTimeout.
                   foo apply(final ClockTest17_MustFailTimeout.
                   foo __desugarer__var__479__) { {
                     
//#line 83
if (!x10.core.Ref.at(__desugarer__var__479__, x10.
                                       lang.
                                       Runtime.here().id)) {
                         
//#line 83
throw new java.lang.ClassCastException("ClockTest17_MustFailTimeout.foo{self.home==here}");
                     }
                     
//#line 83
return __desugarer__var__479__;
                 }}
                 public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return ClockTest17_MustFailTimeout.foo._RTT;if (i ==1) return ClockTest17_MustFailTimeout.foo._RTT;return null;
                 }
                 }.apply(((ClockTest17_MustFailTimeout.
                           foo)
                           (((ClockTest17_MustFailTimeout.
                           foo)((Object[])fooArray.value)[x.one()])))));
        
//#line 87
x10.
          io.
          Console.OUT.println("#0b before next");
        
//#line 88
x10.
          lang.
          Runtime.next();
        
//#line 89
x10.
          io.
          Console.OUT.println("#0b after next");
        
//#line 91
return true;
    }
    
    
//#line 94
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
    							ClockTest17_MustFailTimeout.main(args);
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
        
//#line 95
new ClockTest17_MustFailTimeout().execute();
    }/* } */
    
    
//#line 101
static class Y
                 extends x10.core.Ref
                 {public static final x10.rtt.RuntimeType<ClockTest17_MustFailTimeout.
      Y>_RTT = new x10.rtt.RuntimeType<ClockTest17_MustFailTimeout.
      Y>(
    /* base class */ClockTest17_MustFailTimeout.
      Y.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
        
//#line 102
static void
                       test(
                       ClockTest17_MustFailTimeout.
                         foo f){
            
//#line 103
try {{
                
//#line 103
x10.
                  lang.
                  Runtime.startFinish();
                {
                    
//#line 104
f.apply();
                }
            }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
            if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
            java.lang.Throwable __desugarer__var__480__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 103
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__480__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__480__) {
                
//#line 103
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__480__);
            }finally {{
                 
//#line 103
x10.
                   lang.
                   Runtime.stopFinish();
             }}
            }
        
        public Y() {
            super();
        }
        
        }
        
    
//#line 120
static interface foo
                 {public static final x10.rtt.RuntimeType<ClockTest17_MustFailTimeout.
      foo>_RTT = new x10.rtt.RuntimeType<ClockTest17_MustFailTimeout.
      foo>(
    /* base class */ClockTest17_MustFailTimeout.
      foo.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    
        
        
//#line 121
void
                       apply(
                       );
    
    }
    
    
//#line 128
static class X
                 extends x10.core.Ref
                 {public static final x10.rtt.RuntimeType<ClockTest17_MustFailTimeout.
      X>_RTT = new x10.rtt.RuntimeType<ClockTest17_MustFailTimeout.
      X>(
    /* base class */ClockTest17_MustFailTimeout.
      X.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 129
final public x10.core.Rail<java.lang.Integer>
          z;
        
        
//#line 130
int
                       zero(
                       ){
            
//#line 130
return ((int[])z.value)[((int[])z.value)[((int[])z.value)[1]]];
        }
        
        
//#line 131
int
                       one(
                       ){
            
//#line 131
return ((int[])z.value)[((int[])z.value)[((int[])z.value)[0]]];
        }
        
        
//#line 132
void
                       modify(
                       ){
            
//#line 132
new x10.core.fun.Fun_0_3<x10.core.Rail<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.core.Rail<java.lang.Integer> x,final java.lang.Integer y0,final java.lang.Integer z) { return apply(x,(int)y0,(int)z);}
            public final int apply(final x10.core.Rail<java.lang.Integer> x, final int y0, final int z) { {
                
//#line 132
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
            
//#line 129
this.z = ((x10.core.Rail)(x10.core.RailFactory.<java.lang.Integer>makeRailFromValRail(x10.rtt.Types.INT, /* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { 1,0 })/* } */)));
        }
    
    }
    
    
    public ClockTest17_MustFailTimeout() {
        super();
    }
    
    
//#line 53
final private static class Anonymous$36
                extends x10.core.Ref
                  implements ClockTest17_MustFailTimeout.
                               foo
                {public static final x10.rtt.RuntimeType<ClockTest17_MustFailTimeout.
      Anonymous$36>_RTT = new x10.rtt.RuntimeType<ClockTest17_MustFailTimeout.
      Anonymous$36>(
    /* base class */ClockTest17_MustFailTimeout.
      Anonymous$36.class
    , /* parents */ new x10.rtt.Type[] {ClockTest17_MustFailTimeout.foo._RTT, x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 46
final private ClockTest17_MustFailTimeout
          out$;
        
        
//#line 54
public void
                      apply(
                      ){
            
//#line 55
x10.
              lang.
              Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                   
//#line 56
x10.
                                     io.
                                     Console.OUT.println("hello from finish async S");
                               }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                               });
        }
        
        
//#line 53
private Anonymous$36(final ClockTest17_MustFailTimeout out$) {
            
//#line 53
super();
            
//#line 46
this.out$ = out$;
        }
    
    }
    
    
//#line 61
final private static class Anonymous$37
                extends x10.core.Ref
                  implements ClockTest17_MustFailTimeout.
                               foo
                {public static final x10.rtt.RuntimeType<ClockTest17_MustFailTimeout.
      Anonymous$37>_RTT = new x10.rtt.RuntimeType<ClockTest17_MustFailTimeout.
      Anonymous$37>(
    /* base class */ClockTest17_MustFailTimeout.
      Anonymous$37.class
    , /* parents */ new x10.rtt.Type[] {ClockTest17_MustFailTimeout.foo._RTT, x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 46
final private ClockTest17_MustFailTimeout
          out$;
        
//#line 50
final private x10.
          lang.
          Clock
          c0;
        
        
//#line 62
public void
                      apply(
                      ){
            
//#line 64
x10.
              lang.
              Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                 lang.
                                 Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                 lang.
                                 Clock[] { this.
                                             c0 })/* } */,
                               new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                   
//#line 65
x10.
                                     io.
                                     Console.OUT.println("#1 before next");
                                   
//#line 66
x10.
                                     lang.
                                     Runtime.next();
                                   
//#line 67
x10.
                                     io.
                                     Console.OUT.println("#1 after next");
                               }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                               });
        }
        
        
//#line 61
private Anonymous$37(final ClockTest17_MustFailTimeout out$,
                                         final x10.
                                           lang.
                                           Clock c0) {
            
//#line 61
super();
            
//#line 46
this.out$ = out$;
            
//#line 50
this.c0 = c0;
        }
    
    }
    
    
    }
    