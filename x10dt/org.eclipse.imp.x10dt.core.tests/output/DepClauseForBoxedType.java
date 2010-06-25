
public class DepClauseForBoxedType
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<DepClauseForBoxedType>_RTT = new x10.rtt.RuntimeType<DepClauseForBoxedType>(
/* base class */DepClauseForBoxedType.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 19
static class Prop
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<DepClauseForBoxedType.
      Prop>_RTT = new x10.rtt.RuntimeType<DepClauseForBoxedType.
      Prop>(
    /* base class */DepClauseForBoxedType.
      Prop.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 19
final public int
          i;
        
//#line 19
final public int
          j;
        
        
//#line 18
final private DepClauseForBoxedType
          out$;
        
        
//#line 20
public Prop(final DepClauseForBoxedType out$,
                                final int i,
                                final int j) {
            
//#line 20
super();
            
//#line 18
this.out$ = out$;
            
//#line 21
this.i = i;
            
//#line 21
this.j = j;
        }
        
        final public int
          i(
          ){
            return this.
                     i;
        }
        
        final public int
          j(
          ){
            return this.
                     j;
        }
    
    }
    
    
    
//#line 25
public boolean
                  run(
                  ){
        
//#line 26
DepClauseForBoxedType.
          Prop p =
          new DepClauseForBoxedType.
          Prop(this,
               1,
               2);
        
//#line 27
return true;
    }
    
    
//#line 30
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
    							DepClauseForBoxedType.main(args);
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
        
//#line 31
new DepClauseForBoxedType().execute();
    }/* } */
    
    public DepClauseForBoxedType() {
        super();
    }

}
