
public class ClockTest8
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ClockTest8>_RTT = new x10.rtt.RuntimeType<ClockTest8>(
/* base class */ClockTest8.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 41
public boolean
                  run(
                  ){
        
//#line 42
try {{
            
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
ClockTest8.
                                             BoxedClock bc =
                                             ((ClockTest8.
                                             BoxedClock)(new ClockTest8.
                                             BoxedClock(x10.
                                                          lang.
                                                          Clock.make())));
                                           
//#line 45
x10.core.Rail<x10.
                                             lang.
                                             Clock> ca =
                                             ((x10.core.Rail)(x10.core.RailFactory.<x10.
                                             lang.
                                             Clock>makeRailFromValRail(x10.lang.Clock._RTT, /* template:tuple { */x10.core.RailFactory.<x10.
                                             lang.
                                             Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                             lang.
                                             Clock[] { x10.
                                             lang.
                                             Clock.make(),bc.
                                                            val })/* } */)));
                                           
//#line 46
final x10.
                                             lang.
                                             Clock c1 =
                                             ((x10.
                                             lang.
                                             Clock)((Object[])ca.value)[1]);
                                           
//#line 47
final x10.
                                             lang.
                                             Clock c2 =
                                             c1;
                                           
//#line 48
final x10.
                                             lang.
                                             Clock c3 =
                                             ((x10.
                                             lang.
                                             Clock)((Object[])ca.value)[0]);
                                           
//#line 49
bc.
                                                         val.drop();
                                           
//#line 53
final x10.
                                             lang.
                                             Clock c4 =
                                             ((x10.
                                             lang.
                                             Clock)((Object[])ca.value)[ClockTest8.
                                             U.zero()]);
                                           
//#line 54
x10.
                                             lang.
                                             Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                lang.
                                                                Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                lang.
                                                                Clock[] { c4 })/* } */,
                                                              new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                  
//#line 55
x10.
                                                                    lang.
                                                                    Runtime.runAsync(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                                       lang.
                                                                                       Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                                                                                       lang.
                                                                                       Clock[] { c2 })/* } */,
                                                                                     new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                                                         
//#line 55
x10.
                                                                                           io.
                                                                                           Console.OUT.println("hello");
                                                                                     }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                                                     });
                                                              }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                                              });
                                       }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                       });
                }
            }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
            if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
            java.lang.Throwable __desugarer__var__512__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 43
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__512__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__512__) {
                
//#line 43
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__512__);
            }finally {{
                 
//#line 43
x10.
                   lang.
                   Runtime.stopFinish();
             }}
            }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
            if (__$generated_wrappedex$__.getCause() instanceof x10.
              lang.
              MultipleExceptions) {
            final x10.
              lang.
              MultipleExceptions e = (x10.
              lang.
              MultipleExceptions) __$generated_wrappedex$__.getCause();
            {
                
//#line 59
x10.
                  io.
                  Console.OUT.println("MultipleExceptions");
                
//#line 60
return ((int) e.
                                            exceptions.
                                            length) ==
                ((int) 1) &&
                x10.lang.ClockUseException._RTT.instanceof$(((java.lang.Throwable)((Object[])e.
                                                                                               exceptions.value)[0]));
            }
            }
            if (__$generated_wrappedex$__.getCause() instanceof x10.
              lang.
              ClockUseException) {
            final x10.
              lang.
              ClockUseException e = (x10.
              lang.
              ClockUseException) __$generated_wrappedex$__.getCause();
            {
                
//#line 62
x10.
                  io.
                  Console.OUT.println("ClockUseException");
                
//#line 63
return true;
            }
            }
            throw __$generated_wrappedex$__;
            }catch (final x10.
                      lang.
                      MultipleExceptions e) {
                
//#line 59
x10.
                  io.
                  Console.OUT.println("MultipleExceptions");
                
//#line 60
return ((int) e.
                                            exceptions.
                                            length) ==
                ((int) 1) &&
                x10.lang.ClockUseException._RTT.instanceof$(((java.lang.Throwable)((Object[])e.
                                                                                               exceptions.value)[0]));
            }catch (final x10.
                      lang.
                      ClockUseException e) {
                
//#line 62
x10.
                  io.
                  Console.OUT.println("ClockUseException");
                
//#line 63
return true;
            }
        
//#line 65
return true;
        }
    
    
//#line 68
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
    							ClockTest8.main(args);
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
        
//#line 69
new ClockTest8().executeAsync();
    }/* } */
    
    
//#line 72
static class BoxedClock
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<ClockTest8.
      BoxedClock>_RTT = new x10.rtt.RuntimeType<ClockTest8.
      BoxedClock>(
    /* base class */ClockTest8.
      BoxedClock.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 73
public x10.
          lang.
          Clock
          val;
        
        
//#line 74
public BoxedClock(final x10.
                                        lang.
                                        Clock x) {
            
//#line 74
super();
            
//#line 73
this.val = null;
            
//#line 75
this.val = x;
        }
    
    }
    
    
//#line 79
static class U
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<ClockTest8.
      U>_RTT = new x10.rtt.RuntimeType<ClockTest8.
      U>(
    /* base class */ClockTest8.
      U.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
        
//#line 80
public static int
                      zero(
                      ){
            
//#line 80
return 0;
        }
        
        public U() {
            super();
        }
    
    }
    
    
    public ClockTest8() {
        super();
    }
    
    }
    