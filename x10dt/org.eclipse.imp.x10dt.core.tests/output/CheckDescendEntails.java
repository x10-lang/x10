
public class CheckDescendEntails
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<CheckDescendEntails>_RTT = new x10.rtt.RuntimeType<CheckDescendEntails>(
/* base class */CheckDescendEntails.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 15
static class Prop
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<CheckDescendEntails.
      Prop>_RTT = new x10.rtt.RuntimeType<CheckDescendEntails.
      Prop>(
    /* base class */CheckDescendEntails.
      Prop.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 15
final public int
          i;
        
//#line 15
final public int
          j;
        
        
//#line 14
final private CheckDescendEntails
          out$;
        
        
//#line 16
public Prop(final CheckDescendEntails out$,
                                final int i,
                                final int j) {
            
//#line 16
super();
            
//#line 14
this.out$ = out$;
            
//#line 17
this.i = i;
            
//#line 17
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
    
    
//#line 20
static class Test
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<CheckDescendEntails.
      Test>_RTT = new x10.rtt.RuntimeType<CheckDescendEntails.
      Test>(
    /* base class */CheckDescendEntails.
      Test.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 20
final public CheckDescendEntails.
          Prop
          a;
        
//#line 20
final public CheckDescendEntails.
          Prop
          b;
        
        
//#line 14
final private CheckDescendEntails
          out$;
        
        
//#line 21
public Test(final CheckDescendEntails out$,
                                final CheckDescendEntails.
                                  Prop a,
                                final CheckDescendEntails.
                                  Prop b) {
            
//#line 21
super();
            
//#line 14
this.out$ = out$;
            
//#line 22
this.a = a;
            
//#line 22
this.b = b;
        }
        
        final public CheckDescendEntails.
          Prop
          a(
          ){
            return this.
                     a;
        }
        
        final public CheckDescendEntails.
          Prop
          b(
          ){
            return this.
                     b;
        }
    
    }
    
    
    
//#line 26
public boolean
                  run(
                  ){
        
//#line 27
final CheckDescendEntails.
          Prop p =
          new CheckDescendEntails.
          Prop(this,
               1,
               2);
        
//#line 28
CheckDescendEntails.
          Test t =
          ((CheckDescendEntails.
          Test)(new CheckDescendEntails.
          Test(this,
               p,
               p)));
        
//#line 29
CheckDescendEntails.
          Test u =
          ((CheckDescendEntails.
          Test)(t));
        
//#line 30
return true;
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
    							CheckDescendEntails.main(args);
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
        
//#line 33
new CheckDescendEntails().execute();
    }/* } */
    
    public CheckDescendEntails() {
        super();
    }

}
