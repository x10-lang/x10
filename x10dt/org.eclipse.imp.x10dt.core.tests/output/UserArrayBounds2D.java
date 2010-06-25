
public class UserArrayBounds2D
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<UserArrayBounds2D>_RTT = new x10.rtt.RuntimeType<UserArrayBounds2D>(
/* base class */UserArrayBounds2D.class
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
int j =
              this.ranInt((int)(((((int)((-(((int)(L))))))) - (((int)(K))))),
                          (int)(((((int)(L))) + (((int)(K))))));
            
//#line 37
int lb1 =
              this.ranInt((int)((-(((int)(L))))),
                          (int)(L));
            
//#line 38
int lb2 =
              this.ranInt((int)((-(((int)(L))))),
                          (int)(L));
            
//#line 39
int ub1 =
              this.ranInt((int)(lb1),
                          (int)(L));
            
//#line 40
int ub2 =
              this.ranInt((int)(lb2),
                          (int)(L));
            
//#line 41
boolean withinBounds =
              UserArrayBounds2D.arrayAccess((int)(lb1),
                                            (int)(ub1),
                                            (int)(lb2),
                                            (int)(ub2),
                                            (int)(i),
                                            (int)(j));
            
//#line 42
harness.
              x10Test.chk((boolean)(UserArrayBounds2D.iff((boolean)(withinBounds),
                                                          (boolean)(((((int)(i))) >= (((int)(lb1)))) &&
                                                          ((((int)(i))) <= (((int)(ub1)))) &&
                                                          ((((int)(j))) >= (((int)(lb2)))) &&
                                                          ((((int)(j))) <= (((int)(ub2))))))));
        }
        
//#line 44
return true;
    }
    
    
//#line 51
private static boolean
                  arrayAccess(
                  final int lb1,
                  final int ub1,
                  final int lb2,
                  final int ub2,
                  final int i,
                  final int j){
        
//#line 52
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
                                     Region.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                                                array.
                                                                Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                                                                array.
                                                                Region[] { x10.
                                                                array.
                                                                Region.makeRectangular((int)(lb1),
                                                                                       (int)(ub1)),x10.
                                                                array.
                                                                Region.makeRectangular((int)(lb2),
                                                                                       (int)(ub2)) })/* } */),
                                   new x10.core.fun.Fun_0_1<x10.
                                     array.
                                     Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                     array.
                                     Point id28878) { return apply(id28878);}
                                   public final int apply(final x10.
                                     array.
                                     Point id28878) { {
                                       
//#line 52
final int i =
                                         id28878.apply((int)(0));
                                       
//#line 52
final int j =
                                         id28878.apply((int)(1));
                                       
//#line 52
return 0;
                                   }}
                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                   }
                                   })));
        
//#line 54
boolean withinBounds =
          true;
        
//#line 55
try {{
            
//#line 56
a.set$G((int)(((int)(long)(((long)(2882400007L))))),
                                (int)(i),
                                (int)(j));
            
//#line 58
harness.
              x10Test.chk((boolean)(x10.rtt.Equality.equalsequals(a.apply$G((int)(i),
                                                                            (int)(j)), ((int)(((int)(long)(((long)(2882400007L)))))))));
        }}catch (x10.runtime.impl.java.WrappedRuntimeException __$generated_wrappedex$__) {
        if (__$generated_wrappedex$__.getCause() instanceof java.lang.ArrayIndexOutOfBoundsException) {
        java.lang.ArrayIndexOutOfBoundsException e = (java.lang.ArrayIndexOutOfBoundsException) __$generated_wrappedex$__.getCause();
        {
            
//#line 60
withinBounds = false;
        }
        }
        throw __$generated_wrappedex$__;
        }catch (java.lang.ArrayIndexOutOfBoundsException e) {
            
//#line 60
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
                  final boolean x,
                  final boolean y){
        
//#line 77
return ((boolean) x) ==
        ((boolean) y);
    }
    
    
//#line 78
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
    							UserArrayBounds2D.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$28879)  {
        
//#line 79
new UserArrayBounds2D().execute();
    }/* } */
    
    public UserArrayBounds2D() {
        super();
    }

}
