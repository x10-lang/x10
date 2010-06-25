public class SeqRail1b
extends Benchmark
{public static final x10.rtt.RuntimeType<SeqRail1b>_RTT = new x10.rtt.RuntimeType<SeqRail1b>(
/* base class */SeqRail1b.class
, /* parents */ new x10.rtt.Type[] {Benchmark._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 22
final int
      N =
      1000000;
    
//#line 23
final int
      M =
      20;
    
    
//#line 24
double
                  expected(
                  ){
        
//#line 24
return ((double)(int)(((int)(((((int)(N))) * (((int)(M))))))));
    }
    
    
//#line 25
double
                  operations(
                  ){
        
//#line 25
return ((double)(int)(((int)(((((int)(N))) * (((int)(M))))))));
    }
    
    
//#line 32
final x10.core.Rail<java.lang.Integer>
      a;
    
    
//#line 34
double
                  once(
                  ){
        
//#line 35
int sum =
          0;
        
//#line 36
for (
//#line 36
int k =
                           0;
                         ((((int)(k))) < (((int)(M))));
                         
//#line 36
k += 1) {
            
//#line 37
for (
//#line 37
int i =
                               0;
                             ((((int)(i))) < (((int)(N))));
                             
//#line 37
i += 1) {
                
//#line 38
sum += ((int[])a.value)[((((int)(i))) + (((int)(k))))];
            }
        }
        
//#line 39
return ((double)(int)(((int)(sum))));
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
    							SeqRail1b.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$4557)  {
        
//#line 47
new SeqRail1b().execute();
    }/* } */
    
    public SeqRail1b() {
        super();
        
//#line 32
this.a = ((x10.core.Rail)((new java.lang.Object() {final x10.core.Rail<java.lang.Integer> apply(int length) {int[] array = new int[length];for (int id$4556$ = 0; id$4556$ < length; id$4556$++) {final int id$4556 = id$4556$;array[id$4556] = 1;}return new x10.core.Rail<java.lang.Integer>(x10.rtt.Types.INT, ((((int)(N))) + (((int)(M)))), array);}}.apply(((((int)(N))) + (((int)(M))))))));
    }

}
