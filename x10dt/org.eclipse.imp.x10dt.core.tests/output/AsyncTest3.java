
public class AsyncTest3
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<AsyncTest3>_RTT = new x10.rtt.RuntimeType<AsyncTest3>(
/* base class */AsyncTest3.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 22
public boolean
                  run(
                  ){
        
//#line 23
try {{
            
//#line 24
final x10.
              array.
              DistArray<java.lang.Integer> A =
              ((x10.
              array.
              DistArray)(x10.
              array.
              DistArray.<java.lang.Integer>make(x10.rtt.Types.INT,
                                                x10.
                                                  array.
                                                  Dist.makeUnique())));
            
//#line 25
harness.
              x10Test.chk((boolean)(((((int)(x10.runtime.impl.java.Runtime.MAX_PLACES))) >= (((int)(2))))));
            
//#line 26
harness.
              x10Test.chk((boolean)(/* template:equalsequals { */x10.rtt.Equality.equalsequals(A.
                                                                                                 dist.apply((int)(0)),x10.
                            lang.
                            Runtime.here())/* } */));
            
//#line 27
harness.
              x10Test.chk((boolean)(/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(A.
                                                                                                      dist.apply((int)(1)),x10.
                            lang.
                            Runtime.here()))/* } */));
            
//#line 28
final AsyncTest3.
              X x =
              ((AsyncTest3.
              X)(new AsyncTest3.
              X()));
            
//#line 30
try {{
                
//#line 30
x10.
                  lang.
                  Runtime.startFinish();
                {
                    
//#line 30
x10.
                      lang.
                      Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                           
//#line 30
new x10.core.fun.Fun_0_3<x10.
                                             array.
                                             DistArray<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                             array.
                                             DistArray<java.lang.Integer> x,final java.lang.Integer y0,final java.lang.Integer z) { return apply(x,(int)y0,(int)z);}
                                           public final int apply(final x10.
                                             array.
                                             DistArray<java.lang.Integer> x, final int y0, final int z) { {
                                               
//#line 30
return x.set$G((int)(((((int)(x.apply$G((int)(y0))))) + (((int)(z))))),
                                                                          (int)(y0));
                                           }}
                                           public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.INT);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;return null;
                                           }
                                           }.apply(A,
                                                   0,
                                                   1);
                                       }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                       });
                }
            }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
            if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
            java.lang.Throwable __desugarer__var__309__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 30
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__309__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__309__) {
                
//#line 30
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__309__);
            }finally {{
                 
//#line 30
x10.
                   lang.
                   Runtime.stopFinish();
             }}
            
//#line 31
new x10.core.fun.Fun_0_3<x10.
              array.
              DistArray<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
              array.
              DistArray<java.lang.Integer> x,final java.lang.Integer y0,final java.lang.Integer z) { return apply(x,(int)y0,(int)z);}
            public final int apply(final x10.
              array.
              DistArray<java.lang.Integer> x, final int y0, final int z) { {
                
//#line 31
return x.set$G((int)(((((int)(x.apply$G((int)(y0))))) + (((int)(z))))),
                                           (int)(y0));
            }}
            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.INT);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;return null;
            }
            }.apply(A,
                    0,
                    1);
            
//#line 32
try {{
                
//#line 32
x10.
                  lang.
                  Runtime.startFinish();
                {
                    
//#line 32
x10.
                      lang.
                      Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                           
//#line 32
new x10.core.fun.Fun_0_3<x10.
                                             array.
                                             DistArray<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                             array.
                                             DistArray<java.lang.Integer> x,final java.lang.Integer y0,final java.lang.Integer z) { return apply(x,(int)y0,(int)z);}
                                           public final int apply(final x10.
                                             array.
                                             DistArray<java.lang.Integer> x, final int y0, final int z) { {
                                               
//#line 32
return x.set$G((int)(((((int)(x.apply$G((int)(y0))))) + (((int)(z))))),
                                                                          (int)(y0));
                                           }}
                                           public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.INT);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;return null;
                                           }
                                           }.apply(A,
                                                   1,
                                                   1);
                                       }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                       });
                }
            }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
            if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
            java.lang.Throwable __desugarer__var__310__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 32
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__310__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__310__) {
                
//#line 32
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__310__);
            }finally {{
                 
//#line 32
x10.
                   lang.
                   Runtime.stopFinish();
             }}
            
//#line 33
new x10.core.fun.Fun_0_3<x10.
              array.
              DistArray<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
              array.
              DistArray<java.lang.Integer> x,final java.lang.Integer y0,final java.lang.Integer z) { return apply(x,(int)y0,(int)z);}
            public final int apply(final x10.
              array.
              DistArray<java.lang.Integer> x, final int y0, final int z) { {
                
//#line 33
return x.set$G((int)(((((int)(x.apply$G((int)(y0))))) + (((int)(z))))),
                                           (int)(y0));
            }}
            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.INT);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;return null;
            }
            }.apply(A,
                    1,
                    1);
            
//#line 34
x10.
              io.
              Console.OUT.println("1");
            
//#line 36
try {{
                
//#line 36
x10.
                  lang.
                  Runtime.startFinish();
                {
                    
//#line 36
x10.
                      lang.
                      Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                           
//#line 36
new x10.core.fun.Fun_0_3<x10.
                                             array.
                                             DistArray<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                             array.
                                             DistArray<java.lang.Integer> x,final java.lang.Integer y0,final java.lang.Integer z) { return apply(x,(int)y0,(int)z);}
                                           public final int apply(final x10.
                                             array.
                                             DistArray<java.lang.Integer> x, final int y0, final int z) { {
                                               
//#line 36
return x.set$G((int)(((((int)(x.apply$G((int)(y0))))) + (((int)(z))))),
                                                                          (int)(y0));
                                           }}
                                           public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.INT);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;return null;
                                           }
                                           }.apply(A,
                                                   x.zero(),
                                                   1);
                                       }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                       });
                }
            }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
            if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
            java.lang.Throwable __desugarer__var__311__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 36
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__311__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__311__) {
                
//#line 36
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__311__);
            }finally {{
                 
//#line 36
x10.
                   lang.
                   Runtime.stopFinish();
             }}
            
//#line 37
new x10.core.fun.Fun_0_3<x10.
              array.
              DistArray<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
              array.
              DistArray<java.lang.Integer> x,final java.lang.Integer y0,final java.lang.Integer z) { return apply(x,(int)y0,(int)z);}
            public final int apply(final x10.
              array.
              DistArray<java.lang.Integer> x, final int y0, final int z) { {
                
//#line 37
return x.set$G((int)(((((int)(x.apply$G((int)(y0))))) + (((int)(z))))),
                                           (int)(y0));
            }}
            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.INT);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;return null;
            }
            }.apply(A,
                    x.zero(),
                    1);
            
//#line 39
try {{
                
//#line 39
x10.
                  lang.
                  Runtime.startFinish();
                {
                    
//#line 39
x10.
                      lang.
                      Runtime.runAsync(new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                           
//#line 39
new x10.core.fun.Fun_0_3<x10.
                                             array.
                                             DistArray<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                             array.
                                             DistArray<java.lang.Integer> x,final java.lang.Integer y0,final java.lang.Integer z) { return apply(x,(int)y0,(int)z);}
                                           public final int apply(final x10.
                                             array.
                                             DistArray<java.lang.Integer> x, final int y0, final int z) { {
                                               
//#line 39
return x.set$G((int)(((((int)(x.apply$G((int)(y0))))) + (((int)(z))))),
                                                                          (int)(y0));
                                           }}
                                           public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.INT);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;return null;
                                           }
                                           }.apply(A,
                                                   0,
                                                   A.apply$G((int)(x.one())));
                                       }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                                       });
                }
            }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
            if (__$generated_wrappedex$__.getCause() instanceof java.lang.Throwable) {
            java.lang.Throwable __desugarer__var__312__ = (java.lang.Throwable) __$generated_wrappedex$__.getCause();
            {
                
//#line 39
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__312__);
            }
            }
            throw __$generated_wrappedex$__;
            }catch (java.lang.Throwable __desugarer__var__312__) {
                
//#line 39
x10.
                  lang.
                  Runtime.pushException(__desugarer__var__312__);
            }finally {{
                 
//#line 39
x10.
                   lang.
                   Runtime.stopFinish();
             }}
            
//#line 40
new x10.core.fun.Fun_0_3<x10.
              array.
              DistArray<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
              array.
              DistArray<java.lang.Integer> x,final java.lang.Integer y0,final java.lang.Integer z) { return apply(x,(int)y0,(int)z);}
            public final int apply(final x10.
              array.
              DistArray<java.lang.Integer> x, final int y0, final int z) { {
                
//#line 40
return x.set$G((int)(((((int)(x.apply$G((int)(y0))))) + (((int)(z))))),
                                           (int)(y0));
            }}
            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.DistArray._RTT, x10.rtt.Types.INT);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;return null;
            }
            }.apply(A,
                    0,
                    A.apply$G((int)(x.one())));
            
//#line 41
x10.
              io.
              Console.OUT.println("2");
            
//#line 43
harness.
              x10Test.chk((boolean)(((int) A.apply$G((int)(0))) ==
                          ((int) 8) &&
                          ((int) A.apply$G((int)(1))) ==
                          ((int) 2)));
            
//#line 44
x10.
              io.
              Console.OUT.println("3");
            
//#line 45
return false;
            }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
            if (__$generated_wrappedex$__.getCause() instanceof x10.
              lang.
              BadPlaceException) {
            final x10.
              lang.
              BadPlaceException z = (x10.
              lang.
              BadPlaceException) __$generated_wrappedex$__.getCause();
            {
                
//#line 47
return true;
            }
            }
            throw __$generated_wrappedex$__;
            }catch (final x10.
                      lang.
                      BadPlaceException z) {
                
//#line 47
return true;
            }
            }
        
        
//#line 51
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
        							AsyncTest3.main(args);
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
            
//#line 52
new AsyncTest3().execute();
        }/* } */
        
        
//#line 59
static class X
                    extends x10.core.Ref
                    {public static final x10.rtt.RuntimeType<AsyncTest3.
          X>_RTT = new x10.rtt.RuntimeType<AsyncTest3.
          X>(
        /* base class */AsyncTest3.
          X.class
        , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
        );
        public x10.rtt.RuntimeType getRTT() {return _RTT;}
        
        
        
            
//#line 60
public x10.
              array.
              Array<java.lang.Integer>
              z;
            
            
//#line 61
int
                          zero(
                          ){
                
//#line 61
return z.apply$G((int)(java.lang.Integer)(z.apply$G((int)(java.lang.Integer)(z.apply$G((int)(1))))));
            }
            
            
//#line 62
int
                          one(
                          ){
                
//#line 62
return z.apply$G((int)(java.lang.Integer)(z.apply$G((int)(java.lang.Integer)(z.apply$G((int)(0))))));
            }
            
            
//#line 63
void
                          modify(
                          ){
                
//#line 63
new x10.core.fun.Fun_0_3<x10.
                  array.
                  Array<java.lang.Integer>, java.lang.Integer, java.lang.Integer, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                  array.
                  Array<java.lang.Integer> x,final java.lang.Integer y0,final java.lang.Integer z) { return apply(x,(int)y0,(int)z);}
                public final int apply(final x10.
                  array.
                  Array<java.lang.Integer> x, final int y0, final int z) { {
                    
//#line 63
return x.set$G((int)(((((int)(x.apply$G((int)(y0))))) + (((int)(z))))),
                                               (int)(y0));
                }}
                public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return new x10.rtt.ParameterizedType(x10.array.Array._RTT, x10.rtt.Types.INT);if (i ==1) return x10.rtt.Types.INT;if (i ==2) return x10.rtt.Types.INT;if (i ==3) return x10.rtt.Types.INT;return null;
                }
                }.apply(z,
                        0,
                        1);
            }
            
            public X() {
                super();
                
//#line 60
this.z = ((x10.
                  array.
                  Array)(new x10.
                  array.
                  Array<java.lang.Integer>(x10.rtt.Types.INT,
                                           x10.core.RailFactory.<java.lang.Integer>makeRailFromValRail(x10.rtt.Types.INT, /* template:tuple { */x10.core.RailFactory.<java.lang.Integer>makeValRailFromJavaArray(x10.rtt.Types.INT, new int[] { 1,0 })/* } */))));
            }
        
        }
        
        
        public AsyncTest3() {
            super();
        }
        
        }
        