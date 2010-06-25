
public class CheckDescendEntails_MustFailCompile
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<CheckDescendEntails_MustFailCompile>_RTT = new x10.rtt.RuntimeType<CheckDescendEntails_MustFailCompile>(
/* base class */CheckDescendEntails_MustFailCompile.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 21
static class Test
                extends x10.core.Ref
                {public static final x10.rtt.RuntimeType<CheckDescendEntails_MustFailCompile.
      Test>_RTT = new x10.rtt.RuntimeType<CheckDescendEntails_MustFailCompile.
      Test>(
    /* base class */CheckDescendEntails_MustFailCompile.
      Test.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.runtimeType(java.lang.Object.class)}
    );
    public x10.rtt.RuntimeType getRTT() {return _RTT;}
    
    
    
        
//#line 21
final public Prop
          a;
        
//#line 21
final public Prop
          b;
        
        
//#line 19
final private CheckDescendEntails_MustFailCompile
          out$;
        
        
//#line 22
public Test(final CheckDescendEntails_MustFailCompile out$,
                                final Prop a,
                                final Prop b) {
            
//#line 22
super();
            
//#line 19
this.out$ = out$;
            
//#line 23
this.a = a;
            
//#line 23
this.b = b;
        }
        
        final public Prop
          a(
          ){
            return this.
                     a;
        }
        
        final public Prop
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
final Prop p =
          ((Prop)(new Prop(1,
                           2)));
        
//#line 29
CheckDescendEntails_MustFailCompile.
          Test t =
          ((CheckDescendEntails_MustFailCompile.
          Test)(new CheckDescendEntails_MustFailCompile.
          Test(this,
               p,
               p)));
        
//#line 30
CheckDescendEntails_MustFailCompile.
          Test u =
          ((CheckDescendEntails_MustFailCompile.
          Test)(new x10.core.fun.Fun_0_1<CheckDescendEntails_MustFailCompile.
          Test, CheckDescendEntails_MustFailCompile.
          Test>() {public final CheckDescendEntails_MustFailCompile.
          Test apply$G(final CheckDescendEntails_MustFailCompile.
          Test __desugarer__var__529__) { return apply(__desugarer__var__529__);}
        public final CheckDescendEntails_MustFailCompile.
          Test apply(final CheckDescendEntails_MustFailCompile.
          Test __desugarer__var__529__) { {
            
//#line 30
if (/* template:notequalsequals { */(!x10.rtt.Equality.equalsequals(__desugarer__var__529__,null))/* } */ &&
                              !(((int) __desugarer__var__529__.
                                         a.
                                         i) ==
                                ((int) __desugarer__var__529__.
                                         b.
                                         j))) {
                
//#line 30
throw new java.lang.ClassCastException("CheckDescendEntails_MustFailCompile.Test{self.a.i==self.b.j}");
            }
            
//#line 30
return __desugarer__var__529__;
        }}
        public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {if (i ==0) return CheckDescendEntails_MustFailCompile.Test._RTT;if (i ==1) return CheckDescendEntails_MustFailCompile.Test._RTT;return null;
        }
        }.apply(((CheckDescendEntails_MustFailCompile.
                  Test)
                  t))));
        
//#line 31
return true;
    }
    
    
//#line 33
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
    							CheckDescendEntails_MustFailCompile.main(args);
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
        
//#line 34
new CheckDescendEntails_MustFailCompile().execute();
    }/* } */
    
    public CheckDescendEntails_MustFailCompile() {
        super();
    }

}
