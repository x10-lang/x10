
public class ArrayBounds1D
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<ArrayBounds1D>_RTT = new x10.rtt.RuntimeType<ArrayBounds1D>(
/* base class */ArrayBounds1D.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 28
public boolean
                  run(
                  ){
        
//#line 30
final int COUNT =
          100;
        
//#line 31
final int L =
          10;
        
//#line 32
final int K =
          3;
        
//#line 34
for (
//#line 34
int n =
                           0;
                         ((((int)(n))) < (((int)(COUNT))));
                         
//#line 34
n += 1) {
            
//#line 35
int i =
              this.ranInt((int)(((((int)((-(((int)(L))))))) - (((int)(K))))),
                          (int)(((((int)(L))) + (((int)(K))))));
            
//#line 36
int lb1 =
              this.ranInt((int)((-(((int)(L))))),
                          (int)(L));
            
//#line 37
int ub1 =
              this.ranInt((int)(((((int)(lb1))) - (((int)(1))))),
                          (int)(L));
            
//#line 38
boolean withinBounds =
              ArrayBounds1D.arrayAccess((int)(lb1),
                                        (int)(ub1),
                                        (int)(i));
            
//#line 39
harness.
              x10Test.chk((boolean)(ArrayBounds1D.iff((boolean)(withinBounds),
                                                      (boolean)(((((int)(i))) >= (((int)(lb1)))) &&
                                                      ((((int)(i))) <= (((int)(ub1))))))));
        }
        
//#line 41
return true;
    }
    
    
//#line 48
private static boolean
                  arrayAccess(
                  int lb1,
                  int ub1,
                  int i){
        
//#line 51
x10.
          array.
          Array<java.lang.Integer> a =
          ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Integer>(x10.rtt.Types.INT,
                                   x10.
                                     array.
                                     Region.makeRectangular((int)(lb1),
                                                            (int)(ub1)),
                                   new x10.core.fun.Fun_0_1<x10.
                                     array.
                                     Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                     array.
                                     Point id$11837) { return apply(id$11837);}
                                   public final int apply(final x10.
                                     array.
                                     Point id$11837) { {
                                       
//#line 51
return 0;
                                   }}
                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                   }
                                   })));
        
//#line 52
boolean withinBounds =
          true;
        
//#line 54
try {{
            
//#line 55
a.set$G((int)(((int)(long)(((long)(2882400007L))))),
                                (int)(i));
            
//#line 56
harness.
              x10Test.chk((boolean)(((int) a.apply$G((int)(i))) ==
                          ((int) ((((int)(long)(((long)(2882400007L)))))))));
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.ArrayIndexOutOfBoundsException) {
        java.lang.ArrayIndexOutOfBoundsException e = (java.lang.ArrayIndexOutOfBoundsException) __$generated_wrappedex$__.getCause();
        {
            
//#line 58
withinBounds = false;
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.ArrayIndexOutOfBoundsException e) {
            
//#line 58
withinBounds = false;
        }
        
//#line 62
return withinBounds;
    }
    
    
//#line 70
private static void
                  pr(
                  java.lang.String s){
        
//#line 71
x10.
          io.
          Console.OUT.println(s);
    }
    
    
//#line 77
private static boolean
                  iff(
                  boolean x,
                  boolean y){
        
//#line 78
return ((boolean) x) ==
        ((boolean) y);
    }
    
    
//#line 81
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
    							ArrayBounds1D.main(args);
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
        
//#line 82
new ArrayBounds1D().execute();
    }/* } */
    
    public ArrayBounds1D() {
        super();
    }

}
