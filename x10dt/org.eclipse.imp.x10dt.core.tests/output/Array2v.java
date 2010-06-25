
public class Array2v
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<Array2v>_RTT = new x10.rtt.RuntimeType<Array2v>(
/* base class */Array2v.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
    
//#line 20
public boolean
                  run(
                  ){
        
//#line 22
final x10.
          array.
          Region e =
          ((x10.
          array.
          Region)(x10.
          array.
          Region.makeRectangular((int)(0),
                                 (int)(9))));
        
//#line 23
final x10.
          array.
          Region r =
          ((x10.
          array.
          Region)(x10.
          array.
          Region.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                     array.
                                     Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                                     array.
                                     Region[] { e,e,e })/* } */)));
        
//#line 25
harness.
          x10Test.chk((boolean)(((Object)r).equals(x10.
                        array.
                        Region.$implicit_convert(/* template:tuple { */x10.core.RailFactory.<x10.
                                                   array.
                                                   Region>makeValRailFromJavaArray(x10.array.Region._RTT, new x10.
                                                   array.
                                                   Region[] { x10.
                                                   array.
                                                   Region.makeRectangular((int)(0),
                                                                          (int)(9)),x10.
                                                   array.
                                                   Region.makeRectangular((int)(0),
                                                                          (int)(9)),x10.
                                                   array.
                                                   Region.makeRectangular((int)(0),
                                                                          (int)(9)) })/* } */))));
        
//#line 27
final x10.
          array.
          Array<java.lang.Integer> ia =
          ((x10.
          array.
          Array)(new x10.
          array.
          Array<java.lang.Integer>(x10.rtt.Types.INT,
                                   r,
                                   new x10.core.fun.Fun_0_1<x10.
                                     array.
                                     Point, java.lang.Integer>() {public final java.lang.Integer apply$G(final x10.
                                     array.
                                     Point id$7997) { return apply(id$7997);}
                                   public final int apply(final x10.
                                     array.
                                     Point id$7997) { {
                                       
//#line 27
return 0;
                                   }}
                                   public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return x10.array.Point._RTT;if (i ==1) return x10.rtt.Types.INT;return null;
                                   }
                                   })));
        {
            
//#line 29
final x10.
              array.
              Region p8072 =
              ((x10.
              array.
              Region)(r));
            
//#line 29
final int k8073min8074 =
              p8072.min((int)(2));
            
//#line 29
final int k8073max8075 =
              p8072.max((int)(2));
            
//#line 29
final int j8076min8077 =
              p8072.min((int)(1));
            
//#line 29
final int j8076max8078 =
              p8072.max((int)(1));
            
//#line 29
final int i8079min8080 =
              p8072.min((int)(0));
            
//#line 29
final int i8079max8081 =
              p8072.max((int)(0));
            
//#line 29
for (
//#line 29
int i8079 =
                               i8079min8080;
                             ((((int)(i8079))) <= (((int)(i8079max8081))));
                             
//#line 29
i8079 += 1) {
                
//#line 29
final int i =
                  i8079;
                
//#line 29
for (
//#line 29
int j8076 =
                                   j8076min8077;
                                 ((((int)(j8076))) <= (((int)(j8076max8078))));
                                 
//#line 29
j8076 += 1) {
                    
//#line 29
final int j =
                      j8076;
                    
//#line 29
for (
//#line 29
int k8073 =
                                       k8073min8074;
                                     ((((int)(k8073))) <= (((int)(k8073max8075))));
                                     
//#line 29
k8073 += 1) {
                        
//#line 29
final int k =
                          k8073;
                        {
                            
//#line 30
harness.
                              x10Test.chk((boolean)(((int) ia.apply$G((int)(i),
                                                                      (int)(j),
                                                                      (int)(k))) ==
                                          ((int) 0)));
                            
//#line 31
ia.set$G((int)(((((int)(((((int)(((((int)(100))) * (((int)(i))))))) + (((int)(((((int)(10))) * (((int)(j))))))))))) + (((int)(k))))),
                                                 (int)(i),
                                                 (int)(j),
                                                 (int)(k));
                        }
                    }
                }
            }
        }
        {
            
//#line 34
final x10.
              array.
              Region p8082 =
              ((x10.
              array.
              Region)(r));
            
//#line 34
final int k8083min8084 =
              p8082.min((int)(2));
            
//#line 34
final int k8083max8085 =
              p8082.max((int)(2));
            
//#line 34
final int j8086min8087 =
              p8082.min((int)(1));
            
//#line 34
final int j8086max8088 =
              p8082.max((int)(1));
            
//#line 34
final int i8089min8090 =
              p8082.min((int)(0));
            
//#line 34
final int i8089max8091 =
              p8082.max((int)(0));
            
//#line 34
for (
//#line 34
int i8089 =
                               i8089min8090;
                             ((((int)(i8089))) <= (((int)(i8089max8091))));
                             
//#line 34
i8089 += 1) {
                
//#line 34
final int i =
                  i8089;
                
//#line 34
for (
//#line 34
int j8086 =
                                   j8086min8087;
                                 ((((int)(j8086))) <= (((int)(j8086max8088))));
                                 
//#line 34
j8086 += 1) {
                    
//#line 34
final int j =
                      j8086;
                    
//#line 34
for (
//#line 34
int k8083 =
                                       k8083min8084;
                                     ((((int)(k8083))) <= (((int)(k8083max8085))));
                                     
//#line 34
k8083 += 1) {
                        
//#line 34
final int k =
                          k8083;
                        {
                            
//#line 35
harness.
                              x10Test.chk((boolean)(((int) ia.apply$G((int)(i),
                                                                      (int)(j),
                                                                      (int)(k))) ==
                                          ((int) ((((int)(((((int)(((((int)(100))) * (((int)(i))))))) + (((int)(((((int)(10))) * (((int)(j))))))))))) + (((int)(k)))))));
                        }
                    }
                }
            }
        }
        
//#line 38
return true;
    }
    
    
//#line 41
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
    							Array2v.main(args);
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
        
//#line 42
new Array2v().execute();
    }/* } */
    
    public Array2v() {
        super();
    }

}
