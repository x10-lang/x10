
public class UserDefinedArray
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<UserDefinedArray>_RTT = new x10.rtt.RuntimeType<UserDefinedArray>(
/* base class */UserDefinedArray.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 23
final static x10.
      array.
      Region
      R =
      ((x10.
      array.
      Region)(x10.
      array.
      Region.makeRectangular((int)(0),
                             (int)(1))));
    
//#line 24
final static x10.
      array.
      Dist
      D =
      ((x10.
      array.
      Dist)(x10.
      array.
      Dist.makeBlock(UserDefinedArray.R,
                     (int)(0))));
    
    
//#line 26
public boolean
                  run(
                  ){
        
//#line 28
harness.
          x10Test.chk((boolean)(((((int)(x10.runtime.impl.java.Runtime.MAX_PLACES))) <= (((int)(1)))) ||
                      /* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(UserDefinedArray.D.apply((int)(0)),UserDefinedArray.D.apply((int)(1))))/* } */));
        
//#line 32
final UserDefinedArray.
          E v1 =
          (x10.
             lang.
             Runtime.<UserDefinedArray.
             E>evalFuture(UserDefinedArray.E._RTT,
                          UserDefinedArray.D.apply((int)(1)),
                          new x10.core.fun.Fun_0_0<UserDefinedArray.
                            E>() {public final UserDefinedArray.
                            E apply$G() { return apply();}
                          public final UserDefinedArray.
                            E apply() { {
                              
//#line 32
return new UserDefinedArray.
                                E(1);
                          }}
                          public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return UserDefinedArray.E._RTT;return null;
                          }
                          })).force$G();
        
//#line 33
final UserDefinedArray.
          E v2 =
          (x10.
             lang.
             Runtime.<UserDefinedArray.
             E>evalFuture(UserDefinedArray.E._RTT,
                          UserDefinedArray.D.apply((int)(0)),
                          new x10.core.fun.Fun_0_0<UserDefinedArray.
                            E>() {public final UserDefinedArray.
                            E apply$G() { return apply();}
                          public final UserDefinedArray.
                            E apply() { {
                              
//#line 33
return new UserDefinedArray.
                                E(2);
                          }}
                          public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return UserDefinedArray.E._RTT;return null;
                          }
                          })).force$G();
        
//#line 34
final x10.
          array.
          DistArray<UserDefinedArray.
          E> a =
          ((x10.
          array.
          DistArray)(x10.
          array.
          DistArray.<UserDefinedArray.
          E>make(UserDefinedArray.E._RTT,
                 UserDefinedArray.D,
                 new x10.core.fun.Fun_0_1<x10.
                   array.
                   Point, UserDefinedArray.
                   E>() {public final UserDefinedArray.
                   E apply$G(final x10.
                   array.
                   Point id29225) { return apply(id29225);}
                 public final UserDefinedArray.
                   E apply(final x10.
                   array.
                   Point id29225) { {
                     
//#line 34
final int i =
                       id29225.apply((int)(0));
                     
//#line 34
return ((((int) i) ==
                                          ((int) 0)))
                       ? v1
                       : v2;
                 }}
                 public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return UserDefinedArray.E._RTT;return null;
                 }
                 })));
        
//#line 36
harness.
          x10Test.chk((boolean)(/* template:equalsequals { */x10.rtt.Equality.equalsequals(a.
                                                                                             dist.apply((int)(0)),UserDefinedArray.D.apply((int)(0)))/* } */));
        
//#line 37
harness.
          x10Test.chk((boolean)(/* template:equalsequals { */x10.rtt.Equality.equalsequals(((UserDefinedArray.
                        E)((x10.
                              lang.
                              Runtime.<UserDefinedArray.
                              E>evalFuture(UserDefinedArray.E._RTT,
                                           a.
                                             dist.apply((int)(0)),
                                           new x10.core.fun.Fun_0_0<UserDefinedArray.
                                             E>() {public final UserDefinedArray.
                                             E apply$G() { return apply();}
                                           public final UserDefinedArray.
                                             E apply() { {
                                               
//#line 37
return a.apply$G((int)(0));
                                           }}
                                           public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return UserDefinedArray.E._RTT;return null;
                                           }
                                           })).force$G())),v1)/* } */));
        
//#line 38
x10.
          io.
          Console.OUT.println((((((("v1.home() ") + (x10.lang.Place.place(x10.core.Ref.home(v1))))) + (" D(1) "))) + (UserDefinedArray.D.apply((int)(1)))));
        
//#line 39
harness.
          x10Test.chk((boolean)(/* template:equalsequals { */x10.rtt.Equality.equalsequals(x10.lang.Place.place(x10.core.Ref.home(v1)),UserDefinedArray.D.apply((int)(1)))/* } */));
        
//#line 40
harness.
          x10Test.chk((boolean)(((int) (x10.
                                          lang.
                                          Runtime.<java.lang.Integer>evalFuture(x10.rtt.Types.INT,
                                                                                x10.lang.Place.place(x10.core.Ref.home(v1)),
                                                                                new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                                                                public final int apply() { {
                                                                                    
//#line 40
return v1.
                                                                                                         v;
                                                                                }}
                                                                                public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                                                                }
                                                                                })).force$G()) ==
                      ((int) 1)));
        
//#line 43
harness.
          x10Test.chk((boolean)(/* template:equalsequals { */x10.rtt.Equality.equalsequals(a.
                                                                                             dist.apply((int)(1)),UserDefinedArray.D.apply((int)(1)))/* } */));
        
//#line 44
harness.
          x10Test.chk((boolean)(/* template:equalsequals { */x10.rtt.Equality.equalsequals(((UserDefinedArray.
                        E)((x10.
                              lang.
                              Runtime.<UserDefinedArray.
                              E>evalFuture(UserDefinedArray.E._RTT,
                                           a.
                                             dist.apply((int)(1)),
                                           new x10.core.fun.Fun_0_0<UserDefinedArray.
                                             E>() {public final UserDefinedArray.
                                             E apply$G() { return apply();}
                                           public final UserDefinedArray.
                                             E apply() { {
                                               
//#line 44
return a.apply$G((int)(1));
                                           }}
                                           public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return UserDefinedArray.E._RTT;return null;
                                           }
                                           })).force$G())),v2)/* } */));
        
//#line 45
harness.
          x10Test.chk((boolean)(/* template:equalsequals { */x10.rtt.Equality.equalsequals(x10.lang.Place.place(x10.core.Ref.home(v2)),UserDefinedArray.D.apply((int)(0)))/* } */));
        
//#line 46
harness.
          x10Test.chk((boolean)(((int) (x10.
                                          lang.
                                          Runtime.<java.lang.Integer>evalFuture(x10.rtt.Types.INT,
                                                                                x10.lang.Place.place(x10.core.Ref.home(v2)),
                                                                                new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                                                                public final int apply() { {
                                                                                    
//#line 46
return v2.
                                                                                                         v;
                                                                                }}
                                                                                public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                                                                }
                                                                                })).force$G()) ==
                      ((int) 2)));
        
//#line 49
int i0 =
          ((java.lang.Integer)((x10.
                                  lang.
                                  Runtime.<java.lang.Integer>evalFuture(x10.rtt.Types.INT,
                                                                        x10.lang.Place.place(x10.core.Ref.home(((UserDefinedArray.
                                                                          E)((x10.
                                                                                lang.
                                                                                Runtime.<UserDefinedArray.
                                                                                E>evalFuture(UserDefinedArray.E._RTT,
                                                                                             a.
                                                                                               dist.apply((int)(0)),
                                                                                             new x10.core.fun.Fun_0_0<UserDefinedArray.
                                                                                               E>() {public final UserDefinedArray.
                                                                                               E apply$G() { return apply();}
                                                                                             public final UserDefinedArray.
                                                                                               E apply() { {
                                                                                                 
//#line 49
return a.apply$G((int)(0));
                                                                                             }}
                                                                                             public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return UserDefinedArray.E._RTT;return null;
                                                                                             }
                                                                                             })).force$G())))),
                                                                        new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                                                        public final int apply() { {
                                                                            
//#line 50
return new x10.core.fun.Fun_0_1<UserDefinedArray.
                                                                                                 E, UserDefinedArray.
                                                                                                 E>() {public final UserDefinedArray.
                                                                                                 E apply$G(final UserDefinedArray.
                                                                                                 E __desugarer__var__281__) { return apply(__desugarer__var__281__);}
                                                                                               public final UserDefinedArray.
                                                                                                 E apply(final UserDefinedArray.
                                                                                                 E __desugarer__var__281__) { {
                                                                                                   
//#line 50
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__281__,null))/* } */ &&
                                                                                                                     !(/* template:equalsequals { */x10.rtt.Equality.equalsequals(x10.lang.Place.place(x10.core.Ref.home(__desugarer__var__281__)),x10.
                                                                                                                         lang.
                                                                                                                         Runtime.here())/* } */)) {
                                                                                                       
//#line 50
throw new java.lang.ClassCastException("UserDefinedArray.E{self.home==here}");
                                                                                                   }
                                                                                                   
//#line 50
return __desugarer__var__281__;
                                                                                               }}
                                                                                               public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return UserDefinedArray.E._RTT;if (i ==1) return UserDefinedArray.E._RTT;return null;
                                                                                               }
                                                                                               }.apply(((UserDefinedArray.
                                                                                                         E)
                                                                                                         (x10.
                                                                                                            lang.
                                                                                                            Runtime.<UserDefinedArray.
                                                                                                            E>evalFuture(UserDefinedArray.E._RTT,
                                                                                                                         a.
                                                                                                                           dist.apply((int)(0)),
                                                                                                                         new x10.core.fun.Fun_0_0<UserDefinedArray.
                                                                                                                           E>() {public final UserDefinedArray.
                                                                                                                           E apply$G() { return apply();}
                                                                                                                         public final UserDefinedArray.
                                                                                                                           E apply() { {
                                                                                                                             
//#line 50
return a.apply$G((int)(0));
                                                                                                                         }}
                                                                                                                         public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return UserDefinedArray.E._RTT;return null;
                                                                                                                         }
                                                                                                                         })).force$G())).
                                                                                                 v;
                                                                        }}
                                                                        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                                                        }
                                                                        })).force$G()));
        
//#line 53
int i1 =
          ((java.lang.Integer)((x10.
                                  lang.
                                  Runtime.<java.lang.Integer>evalFuture(x10.rtt.Types.INT,
                                                                        x10.lang.Place.place(x10.core.Ref.home(((UserDefinedArray.
                                                                          E)((x10.
                                                                                lang.
                                                                                Runtime.<UserDefinedArray.
                                                                                E>evalFuture(UserDefinedArray.E._RTT,
                                                                                             a.
                                                                                               dist.apply((int)(1)),
                                                                                             new x10.core.fun.Fun_0_0<UserDefinedArray.
                                                                                               E>() {public final UserDefinedArray.
                                                                                               E apply$G() { return apply();}
                                                                                             public final UserDefinedArray.
                                                                                               E apply() { {
                                                                                                 
//#line 53
return a.apply$G((int)(1));
                                                                                             }}
                                                                                             public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return UserDefinedArray.E._RTT;return null;
                                                                                             }
                                                                                             })).force$G())))),
                                                                        new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                                                        public final int apply() { {
                                                                            
//#line 54
return new x10.core.fun.Fun_0_1<UserDefinedArray.
                                                                                                 E, UserDefinedArray.
                                                                                                 E>() {public final UserDefinedArray.
                                                                                                 E apply$G(final UserDefinedArray.
                                                                                                 E __desugarer__var__282__) { return apply(__desugarer__var__282__);}
                                                                                               public final UserDefinedArray.
                                                                                                 E apply(final UserDefinedArray.
                                                                                                 E __desugarer__var__282__) { {
                                                                                                   
//#line 54
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__282__,null))/* } */ &&
                                                                                                                     !(/* template:equalsequals { */x10.rtt.Equality.equalsequals(x10.lang.Place.place(x10.core.Ref.home(__desugarer__var__282__)),x10.
                                                                                                                         lang.
                                                                                                                         Runtime.here())/* } */)) {
                                                                                                       
//#line 54
throw new java.lang.ClassCastException("UserDefinedArray.E{self.home==here}");
                                                                                                   }
                                                                                                   
//#line 54
return __desugarer__var__282__;
                                                                                               }}
                                                                                               public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return UserDefinedArray.E._RTT;if (i ==1) return UserDefinedArray.E._RTT;return null;
                                                                                               }
                                                                                               }.apply(((UserDefinedArray.
                                                                                                         E)
                                                                                                         (x10.
                                                                                                            lang.
                                                                                                            Runtime.<UserDefinedArray.
                                                                                                            E>evalFuture(UserDefinedArray.E._RTT,
                                                                                                                         a.
                                                                                                                           dist.apply((int)(1)),
                                                                                                                         new x10.core.fun.Fun_0_0<UserDefinedArray.
                                                                                                                           E>() {public final UserDefinedArray.
                                                                                                                           E apply$G() { return apply();}
                                                                                                                         public final UserDefinedArray.
                                                                                                                           E apply() { {
                                                                                                                             
//#line 54
return a.apply$G((int)(1));
                                                                                                                         }}
                                                                                                                         public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return UserDefinedArray.E._RTT;return null;
                                                                                                                         }
                                                                                                                         })).force$G())).
                                                                                                 v;
                                                                        }}
                                                                        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                                                        }
                                                                        })).force$G()));
        
//#line 56
return ((int) ((((int)(i0))) + (((int)(1))))) ==
        ((int) i1);
    }
    
    
//#line 59
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
    							UserDefinedArray.main(args);
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
        
//#line 60
new UserDefinedArray().execute();
    }/* } */
    
    
//#line 63
static class E
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<UserDefinedArray.
      E>_RTT = new x10.rtt.RuntimeType<UserDefinedArray.
      E>(
    /* base class */UserDefinedArray.
      E.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 64
int
          v;
        
        
//#line 65
E(int i) {
            
//#line 65
super();
            
//#line 64
this.v = 0;
            
//#line 65
this.v = i;
        }
    
    }
    
    
    public UserDefinedArray() {
        super();
    }

}
