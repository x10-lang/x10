
public class MacroConstraint
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<MacroConstraint>_RTT = new x10.rtt.RuntimeType<MacroConstraint>(
/* base class */MacroConstraint.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 18
public boolean
                  run(
                  ){
        
//#line 19
final java.lang.Object v =
          ((java.lang.Object)(x10.
          array.
          Region.makeRectangular((int)(0),
                                 (int)(5))));
        
//#line 20
boolean result =
          true;
        
//#line 21
final boolean c =
          new x10.core.fun.Fun_0_1<java.lang.Object, java.lang.Boolean>() {public final java.lang.Boolean apply$G(final java.lang.Object __desugarer__var__396__) { return apply(__desugarer__var__396__);}
        public final boolean apply(final java.lang.Object __desugarer__var__396__) { {
            
//#line 21
return x10.array.Region._RTT.instanceof$(__desugarer__var__396__) &&
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
                      Region) __desugarer__var__396__))/* } */).
                     rank) ==
            ((int) 1);
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.runtimeType(java.lang.Object.class);if (i ==1) return x10.rtt.Types.BOOLEAN;return null;
        }
        }.apply(v);
        
//#line 22
if ((!(((boolean)(c))))) {
            
//#line 23
x10.
              io.
              Console.OUT.println("Failed v instanceof Region(1)");
            
//#line 24
result = false;
        }
        
//#line 26
final boolean d =
          new x10.core.fun.Fun_0_1<java.lang.Object, java.lang.Boolean>() {public final java.lang.Boolean apply$G(final java.lang.Object __desugarer__var__397__) { return apply(__desugarer__var__397__);}
        public final boolean apply(final java.lang.Object __desugarer__var__397__) { {
            
//#line 26
return x10.array.Region._RTT.instanceof$(__desugarer__var__397__) &&
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
                      Region) __desugarer__var__397__))/* } */).
                     rank) ==
            ((int) 2);
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.runtimeType(java.lang.Object.class);if (i ==1) return x10.rtt.Types.BOOLEAN;return null;
        }
        }.apply(v);
        
//#line 27
if (d) {
            
//#line 28
x10.
              io.
              Console.OUT.println("Failed v instanceof Region(2)");
            
//#line 29
result = false;
        }
        
//#line 31
try {{
            
//#line 32
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
              Region __desugarer__var__398__) { return apply(__desugarer__var__398__);}
            public final x10.
              array.
              Region apply(final x10.
              array.
              Region __desugarer__var__398__) { {
                
//#line 32
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__398__,null))/* } */ &&
                                  !(((int) __desugarer__var__398__.
                                             rank) ==
                                    ((int) 1))) {
                    
//#line 32
throw new java.lang.ClassCastException("x10.array.Region{self.rank==1}");
                }
                
//#line 32
return __desugarer__var__398__;
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
        final java.lang.ClassCastException id$37841 = (java.lang.ClassCastException) __$generated_wrappedex$__.getCause();
        {
            
//#line 34
x10.
              io.
              Console.OUT.println("Failed v as  Region(1)");
            
//#line 35
result = false;
        }
        }
        throw __$generated_wrappedex$__;
        }catch (final java.lang.ClassCastException id$37841) {
            
//#line 34
x10.
              io.
              Console.OUT.println("Failed v as  Region(1)");
            
//#line 35
result = false;
        }
        
//#line 37
try {{
            
//#line 38
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
              Region __desugarer__var__399__) { return apply(__desugarer__var__399__);}
            public final x10.
              array.
              Region apply(final x10.
              array.
              Region __desugarer__var__399__) { {
                
//#line 38
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__399__,null))/* } */ &&
                                  !(((int) __desugarer__var__399__.
                                             rank) ==
                                    ((int) 2))) {
                    
//#line 38
throw new java.lang.ClassCastException("x10.array.Region{self.rank==2}");
                }
                
//#line 38
return __desugarer__var__399__;
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
            
//#line 39
x10.
              io.
              Console.OUT.println("Failed v as  Region(2)");
            
//#line 40
result = false;
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.ClassCastException) {
        final java.lang.ClassCastException id$37842 = (java.lang.ClassCastException) __$generated_wrappedex$__.getCause();
        {
            
        }
        }
        throw __$generated_wrappedex$__;
        }catch (final java.lang.ClassCastException id$37842) {
            
        }
        
//#line 43
return result;
    }
    
    
//#line 46
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
    							MacroConstraint.main(args);
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
        
//#line 47
new MacroConstraint().execute();
    }/* } */
    
    public MacroConstraint() {
        super();
    }

}
