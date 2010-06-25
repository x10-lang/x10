
public class AtFieldAccess
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<AtFieldAccess>_RTT = new x10.rtt.RuntimeType<AtFieldAccess>(
/* base class */AtFieldAccess.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 19
AtFieldAccess.
      T
      t;
    
    
//#line 20
public boolean
                  run(
                  ){
        
//#line 21
x10.
          lang.
          Place Second =
          x10.
          lang.
          Place.FIRST_PLACE.next();
        
//#line 22
x10.
          array.
          Region r =
          x10.
          array.
          Region.makeRectangular((int)(0),
                                 (int)(0));
        
//#line 23
final x10.
          array.
          Dist D =
          ((x10.
          array.
          Dist)(x10.
          array.
          Dist.makeConstant(r,
                            Second)));
        
//#line 24
for (
//#line 24
final x10.core.Iterator<x10.
                           array.
                           Point> p31392 =
                           D.
                             region.iterator();
                         p31392.hasNext();
                         ) {
            
//#line 24
final x10.
              array.
              Point p =
              ((x10.
                array.
                Point)
                p31392.next$G());
            
//#line 25
this.t = x10.
              lang.
              Runtime.<AtFieldAccess.
              T>evalAt$G(AtFieldAccess.T._RTT,
                         D.apply(p),
                         new x10.core.fun.Fun_0_0<AtFieldAccess.
                           T>() {public final AtFieldAccess.
                           T apply$G() { return apply();}
                         public final AtFieldAccess.
                           T apply() { {
                             
//#line 25
return new AtFieldAccess.
                               T();
                         }}
                         public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return AtFieldAccess.T._RTT;return null;
                         }
                         });
        }
        
//#line 27
final AtFieldAccess.
          T tt =
          this.
            t;
        
//#line 28
x10.
          lang.
          Runtime.runAt(x10.lang.Place.place(x10.core.Ref.home(tt)),
                        new x10.core.fun.VoidFun_0_0() {public final void apply() { {
                            
//#line 28
tt.i = 3;
                        }}
                        });
        
//#line 29
return ((int) 3) ==
        ((int) ((x10.
                   lang.
                   Runtime.<java.lang.Integer>evalAt$G(x10.rtt.Types.INT,
                                                       x10.lang.Place.place(x10.core.Ref.home(tt)),
                                                       new x10.core.fun.Fun_0_0<java.lang.Integer>() {public final java.lang.Integer apply$G() { return apply();}
                                                       public final int apply() { {
                                                           
//#line 29
return tt.
                                                                                i;
                                                       }}
                                                       public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.rtt.Types.INT;return null;
                                                       }
                                                       }))));
    }
    
    
//#line 32
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
    							AtFieldAccess.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$31316)  {
        
//#line 33
new AtFieldAccess().execute();
    }/* } */
    
    
//#line 36
static class T
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<AtFieldAccess.
      T>_RTT = new x10.rtt.RuntimeType<AtFieldAccess.
      T>(
    /* base class */AtFieldAccess.
      T.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 37
public int
          i;
        
        public T() {
            super();
            
//#line 37
this.i = 0;
        }
    
    }
    
    
    public AtFieldAccess() {
        super();
        
//#line 19
this.t = null;
    }

}
