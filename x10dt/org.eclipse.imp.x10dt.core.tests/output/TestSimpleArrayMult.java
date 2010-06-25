
public class TestSimpleArrayMult
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<TestSimpleArrayMult>_RTT = new x10.rtt.RuntimeType<TestSimpleArrayMult>(
/* base class */TestSimpleArrayMult.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 20
public boolean
                  run(
                  ){
        
//#line 22
final int N =
          99900;
        
//#line 23
long start1 =
          x10.
          lang.
          System.currentTimeMillis();
        
//#line 24
final x10.
          array.
          Region e =
          ((x10.
          array.
          Region)(x10.
          array.
          Region.makeRectangular((int)(1),
                                 (int)(N))));
        
//#line 25
long regionStop =
          x10.
          lang.
          System.currentTimeMillis();
        
//#line 26
final x10.
          array.
          Array<java.lang.Integer> ia =
          ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Integer>(x10.rtt.Types.INT,
                                   e,
                                   new x10.core.fun.Fun_0_1<x10.
                                     array.
                                     Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                     array.
                                     Point id$28489) { return apply(id$28489);}
                                   public final int apply(final x10.
                                     array.
                                     Point id$28489) { {
                                       
//#line 26
return 0;
                                   }}
                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                   }
                                   })));
        
//#line 27
final x10.
          array.
          Array<java.lang.Integer> ib =
          ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Integer>(x10.rtt.Types.INT,
                                   e,
                                   new x10.core.fun.Fun_0_1<x10.
                                     array.
                                     Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                     array.
                                     Point id28490) { return apply(id28490);}
                                   public final int apply(final x10.
                                     array.
                                     Point id28490) { {
                                       
//#line 27
final int i =
                                         id28490.apply((int)(0));
                                       
//#line 27
return i;
                                   }}
                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                   }
                                   })));
        
//#line 28
final x10.
          array.
          Array<java.lang.Integer> ic =
          ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Integer>(x10.rtt.Types.INT,
                                   e,
                                   new x10.core.fun.Fun_0_1<x10.
                                     array.
                                     Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                     array.
                                     Point id$28491) { return apply(id$28491);}
                                   public final int apply(final x10.
                                     array.
                                     Point id$28491) { {
                                       
//#line 28
return 2;
                                   }}
                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                   }
                                   })));
        
//#line 29
long initStop =
          x10.
          lang.
          System.currentTimeMillis();
        {
            
//#line 31
final x10.
              array.
              Region p28579 =
              ((x10.
              array.
              Region)(e));
            
//#line 31
final x10.core.Rail<java.lang.Integer> p28580 =
              ((x10.core.Rail)(x10.core.RailFactory.<java.lang.Integer>makeVarRail(x10.rtt.Types.INT, ((int)(1)))));
            
//#line 31
final int p28581min28582 =
              p28579.min((int)(0));
            
//#line 31
final int p28581max28583 =
              p28579.max((int)(0));
            
//#line 31
for (
//#line 31
int p28581 =
                               p28581min28582;
                             ((((int)(p28581))) <= (((int)(p28581max28583))));
                             
//#line 31
p28581 += 1) {
                
//#line 31
((int[])p28580.value)[0] = p28581;
                
//#line 31
final x10.
                  array.
                  Point p =
                  ((x10.
                  array.
                  Point)(x10.
                  array.
                  Point.make(p28580)));
                {
                    
//#line 32
ia.set$G((int)(((((int)(ib.apply$G(p)))) * (((int)(ic.apply$G(p)))))),
                                         p);
                }
            }
        }
        
//#line 35
long multStop =
          x10.
          lang.
          System.currentTimeMillis();
        
//#line 36
int sum =
          TestSimpleArrayMult.sum(ia);
        
//#line 37
int expectedValue =
          (((((int)(N))) * (((int)((((((int)(N))) + (((int)(1))))))))));
        
//#line 40
x10.
          io.
          Console.OUT.println((("expected vaule:") + (expectedValue)));
        
//#line 41
harness.
          x10Test.chk((boolean)(((int) sum) ==
                      ((int) expectedValue)));
        
//#line 43
long regionTime =
          regionStop = start1;
        
//#line 44
long constructTime =
          ((((long)(initStop))) - (((long)(regionStop))));
        
//#line 45
long multTime =
          ((((long)(multStop))) - (((long)(initStop))));
        
//#line 47
x10.
          io.
          Console.OUT.println((("Region construction time:") + ((((((double)(((double)(long)(((long)(regionTime))))))) / (((double)(1000.0))))))));
        
//#line 48
x10.
          io.
          Console.OUT.println((("Array construction time :") + ((((((double)(((double)(long)(((long)(constructTime))))))) / (((double)(1000.0))))))));
        
//#line 49
x10.
          io.
          Console.OUT.println((("Multiplication time     :") + ((((((double)(((double)(long)(((long)(multTime))))))) / (((double)(1000.0))))))));
        
//#line 51
return true;
    }
    
    
//#line 54
public static int
                  sum(
                  final x10.
                    array.
                    Array<java.lang.Integer> ia){
        
//#line 55
int s =
          0;
        
//#line 56
for (
//#line 56
final x10.core.Iterator<x10.
                           array.
                           Point> i28587 =
                           ia.
                             region.iterator();
                         i28587.hasNext();
                         ) {
            
//#line 56
final x10.
              array.
              Point i =
              ((x10.
              array.
              Point)(i28587.next$G()));
            
//#line 56
s += ia.apply$G(i);
        }
        
//#line 57
return s;
    }
    
    
//#line 61
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
    							TestSimpleArrayMult.main(args);
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
        
//#line 62
new TestSimpleArrayMult().execute();
    }/* } */
    
    public TestSimpleArrayMult() {
        super();
    }

}
