public class SeqMethodCall1
extends Benchmark
{public static final x10.rtt.RuntimeType<SeqMethodCall1>_RTT = new x10.rtt.RuntimeType<SeqMethodCall1>(
/* base class */SeqMethodCall1.class
, /* parents */ new x10.rtt.Type[] {Benchmark._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 18
final int
      N =
      2000000;
    
    
//#line 19
double
                  expected(
                  ){
        
//#line 19
return ((double)(int)(((int)(N))));
    }
    
    
//#line 20
double
                  operations(
                  ){
        
//#line 20
return ((((double)(((double)(int)(((int)(N))))))) * (((double)(5.0))));
    }
    
    
//#line 26
final static class X
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<SeqMethodCall1.
      X>_RTT = new x10.rtt.RuntimeType<SeqMethodCall1.
      X>(
    /* base class */SeqMethodCall1.
      X.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 27
double
          x;
        
        
//#line 28
double
                      foo(
                      final double y){
            
//#line 28
return (this.x += y);
        }
        
        public X() {
            super();
            
//#line 27
this.x = 0.0;
        }
    
    }
    
    
//#line 31
final SeqMethodCall1.
      X
      x;
    
    
//#line 33
double
                  once(
                  ){
        
//#line 34
double sum =
          0.0;
        
//#line 35
final double a =
          ((double)(int)(((int)(0))));
        
//#line 36
final double b =
          ((double)(int)(((int)(-1))));
        
//#line 37
final double c =
          ((double)(int)(((int)(1))));
        
//#line 38
final double d =
          ((double)(int)(((int)(2))));
        
//#line 39
final double e =
          ((double)(int)(((int)(-2))));
        
//#line 40
for (
//#line 40
int i =
                           0;
                         ((((int)(i))) < (((int)(N))));
                         
//#line 40
i += 1) {
            
//#line 41
sum += x.foo((double)(a));
            
//#line 42
sum += x.foo((double)(b));
            
//#line 43
sum += x.foo((double)(c));
            
//#line 44
sum += x.foo((double)(d));
            
//#line 45
sum += x.foo((double)(e));
        }
        
//#line 47
return sum;
    }
    
    
//#line 55
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
    							SeqMethodCall1.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$3865)  {
        
//#line 56
new SeqMethodCall1().execute();
    }/* } */
    
    public SeqMethodCall1() {
        super();
        
//#line 31
this.x = ((SeqMethodCall1.
          X)(new SeqMethodCall1.
          X()));
    }

}
