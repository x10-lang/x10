
public class InlineConstraint
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<InlineConstraint>_RTT = new x10.rtt.RuntimeType<InlineConstraint>(
/* base class */InlineConstraint.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 17
public boolean
                  run(
                  ){
        
//#line 18
final java.lang.Object v =
          ((java.lang.Object)(x10.
          array.
          Region.makeRectangular((int)(0),
                                 (int)(5))));
        
//#line 19
boolean result =
          true;
        
//#line 20
final boolean c =
          new x10.core.fun.Fun_0_1<java.lang.Object, java.lang.Boolean>() {public final java.lang.Boolean apply$G(final java.lang.Object __desugarer__var__386__) { return apply(__desugarer__var__386__);}
        public final boolean apply(final java.lang.Object __desugarer__var__386__) { {
            
//#line 20
return x10.array.Region._RTT.instanceof$(__desugarer__var__386__) &&
            ((int) (/* template:cast_deptype { */(new java.lang.Object() {
                        final x10.
                      array.
                      Region cast(x10.
                      array.
                      Region self) {
                            if (self==null) return null;
                            x10.rtt.Type rtt = x10.array.Region._RTT;
                            if (rtt != null && ! rtt.instanceof$(self)) throw new java.lang.ClassCastException();
                            boolean sat = true;
                            if (! sat) throw new java.lang.ClassCastException();
                            return self;
                        }
                    }.cast((x10.
                      array.
                      Region) __desugarer__var__386__))/* } */).
                     rank) ==
            ((int) 1);
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.runtimeType(java.lang.Object.class);if (i ==1) return x10.rtt.Types.BOOLEAN;return null;
        }
        }.apply(v);
        
//#line 21
if ((!(((boolean)(c))))) {
            
//#line 22
x10.
              io.
              Console.OUT.println("v instanceof Region{self.rank==1} failed.");
            
//#line 23
result = false;
        }
        
//#line 25
final boolean d =
          new x10.core.fun.Fun_0_1<java.lang.Object, java.lang.Boolean>() {public final java.lang.Boolean apply$G(final java.lang.Object __desugarer__var__387__) { return apply(__desugarer__var__387__);}
        public final boolean apply(final java.lang.Object __desugarer__var__387__) { {
            
//#line 25
return x10.array.Region._RTT.instanceof$(__desugarer__var__387__) &&
            ((int) (/* template:cast_deptype { */(new java.lang.Object() {
                        final x10.
                      array.
                      Region cast(x10.
                      array.
                      Region self) {
                            if (self==null) return null;
                            x10.rtt.Type rtt = x10.array.Region._RTT;
                            if (rtt != null && ! rtt.instanceof$(self)) throw new java.lang.ClassCastException();
                            boolean sat = true;
                            if (! sat) throw new java.lang.ClassCastException();
                            return self;
                        }
                    }.cast((x10.
                      array.
                      Region) __desugarer__var__387__))/* } */).
                     rank) ==
            ((int) 2);
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.runtimeType(java.lang.Object.class);if (i ==1) return x10.rtt.Types.BOOLEAN;return null;
        }
        }.apply(v);
        
//#line 26
if (d) {
            
//#line 27
x10.
              io.
              Console.OUT.println("WTF, it\'s a two-dimensional region?!");
            
//#line 28
result = false;
        }
        
//#line 30
try {{
            
//#line 31
final x10.
              array.
              Region e =
              ((x10.
              array.
              Region)(new x10.core.fun.Fun_0_1<x10.
              array.
              Region, x10.
              array.
              Region>() {public final x10.
              array.
              Region apply$G(final x10.
              array.
              Region __desugarer__var__388__) { return apply(__desugarer__var__388__);}
            public final x10.
              array.
              Region apply(final x10.
              array.
              Region __desugarer__var__388__) { {
                
//#line 31
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__388__,null))/* } */ &&
                                  !(((int) __desugarer__var__388__.
                                             rank) ==
                                    ((int) 1))) {
                    
//#line 31
throw new java.lang.ClassCastException("x10.array.Region{self.rank==1}");
                }
                
//#line 31
return __desugarer__var__388__;
            }}
            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Region._RTT;if (i ==1) return x10.array.Region._RTT;return null;
            }
            }.apply(/* template:cast_deptype { */(new java.lang.Object() {
                        final x10.
                      array.
                      Region cast(x10.
                      array.
                      Region self) {
                            if (self==null) return null;
                            x10.rtt.Type rtt = x10.array.Region._RTT;
                            if (rtt != null && ! rtt.instanceof$(self)) throw new java.lang.ClassCastException();
                            boolean sat = true;
                            if (! sat) throw new java.lang.ClassCastException();
                            return self;
                        }
                    }.cast((x10.
                      array.
                      Region) v))/* } */)));
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.ClassCastException) {
        final java.lang.ClassCastException id$37357 = (java.lang.ClassCastException) __$generated_wrappedex$__.getCause();
        {
            
//#line 33
x10.
              io.
              Console.OUT.println("WTF, cast to a single-dimensional region failed?!");
            
//#line 34
result = false;
        }
        }
        throw __$generated_wrappedex$__;
        }catch (final java.lang.ClassCastException id$37357) {
            
//#line 33
x10.
              io.
              Console.OUT.println("WTF, cast to a single-dimensional region failed?!");
            
//#line 34
result = false;
        }
        
//#line 36
try {{
            
//#line 37
final x10.
              array.
              Region f =
              ((x10.
              array.
              Region)(new x10.core.fun.Fun_0_1<x10.
              array.
              Region, x10.
              array.
              Region>() {public final x10.
              array.
              Region apply$G(final x10.
              array.
              Region __desugarer__var__389__) { return apply(__desugarer__var__389__);}
            public final x10.
              array.
              Region apply(final x10.
              array.
              Region __desugarer__var__389__) { {
                
//#line 37
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__389__,null))/* } */ &&
                                  !(((int) __desugarer__var__389__.
                                             rank) ==
                                    ((int) 2))) {
                    
//#line 37
throw new java.lang.ClassCastException("x10.array.Region{self.rank==2}");
                }
                
//#line 37
return __desugarer__var__389__;
            }}
            public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Region._RTT;if (i ==1) return x10.array.Region._RTT;return null;
            }
            }.apply(/* template:cast_deptype { */(new java.lang.Object() {
                        final x10.
                      array.
                      Region cast(x10.
                      array.
                      Region self) {
                            if (self==null) return null;
                            x10.rtt.Type rtt = x10.array.Region._RTT;
                            if (rtt != null && ! rtt.instanceof$(self)) throw new java.lang.ClassCastException();
                            boolean sat = true;
                            if (! sat) throw new java.lang.ClassCastException();
                            return self;
                        }
                    }.cast((x10.
                      array.
                      Region) v))/* } */)));
            
//#line 38
x10.
              io.
              Console.OUT.println("WTF, cast to a two-dimensional region succeeded?!");
            
//#line 39
result = false;
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.ClassCastException) {
        final java.lang.ClassCastException id$37358 = (java.lang.ClassCastException) __$generated_wrappedex$__.getCause();
        {
            
        }
        }
        throw __$generated_wrappedex$__;
        }catch (final java.lang.ClassCastException id$37358) {
            
        }
        
//#line 42
return result;
    }
    
    
//#line 44
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
    							InlineConstraint.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> args)  {
        
//#line 45
new InlineConstraint().execute();
    }/* } */
    
    public InlineConstraint() {
        super();
    }

}
