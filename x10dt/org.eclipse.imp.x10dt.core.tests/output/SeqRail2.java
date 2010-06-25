public class SeqRail2
extends Benchmark
{public static final x10.rtt.RuntimeType<SeqRail2>_RTT = new x10.rtt.RuntimeType<SeqRail2>(
/* base class */SeqRail2.class
, /* parents */ new x10.rtt.Type[] {Benchmark._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 33
final int
      N =
      2000;
    
    
//#line 34
double
                  expected(
                  ){
        
//#line 34
return ((((double)(((((double)(((((double)(1.0))) * (((double)(((double)(int)(((int)(N))))))))))) * (((double)(((double)(int)(((int)(N))))))))))) * (((double)(((double)(int)(((int)((((((int)(N))) - (((int)(1)))))))))))));
    }
    
    
//#line 35
double
                  operations(
                  ){
        
//#line 35
return ((((double)(((((double)(2.0))) * (((double)(((double)(int)(((int)(N))))))))))) * (((double)(((double)(int)(((int)(N))))))));
    }
    
    
//#line 42
final x10.core.Rail<java.lang.Double>
      a;
    
    
//#line 44
double
                  once(
                  ){
        
//#line 45
for (
//#line 45
int i =
                           0;
                         ((((int)(i))) < (((int)(N))));
                         
//#line 45
i += 1) {
            
//#line 46
for (
//#line 46
int j =
                               0;
                             ((((int)(j))) < (((int)(N))));
                             
//#line 46
j += 1) {
                
//#line 47
((double[])a.value)[((((int)(((((int)(i))) * (((int)(N))))))) + (((int)(j))))] = ((double)(int)(((int)((((((int)(i))) + (((int)(j)))))))));
            }
        }
        
//#line 48
double sum =
          0.0;
        
//#line 49
for (
//#line 49
int i =
                           0;
                         ((((int)(i))) < (((int)(N))));
                         
//#line 49
i += 1) {
            
//#line 50
for (
//#line 50
int j =
                               0;
                             ((((int)(j))) < (((int)(N))));
                             
//#line 50
j += 1) {
                
//#line 51
sum += ((double[])a.value)[((((int)(((((int)(i))) * (((int)(N))))))) + (((int)(j))))];
            }
        }
        
//#line 52
return sum;
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
    							SeqRail2.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> id$4693)  {
        
//#line 60
new SeqRail2().execute();
    }/* } */
    
    public SeqRail2() {
        super();
        
//#line 42
this.a = ((x10.core.Rail)(x10.core.RailFactory.<java.lang.Double>makeVarRail(x10.rtt.Types.DOUBLE, ((int)(((((int)(N))) * (((int)(N)))))))));
    }

}
