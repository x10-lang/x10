
class Simple
extends x10.core.Ref
{
public static class /* Join: { */RTT/* } */ extends x10.types.RuntimeType<Simple> {
public static final /* Join: { */RTT/* } */ it = new /* Join: { */RTT/* } */();
    
    
    public RTT() {super(Simple.class);
                      }
    public boolean instanceof$(java.lang.Object o) {
    if (! (o instanceof Simple)) return false;
        return true;
    }
    public java.util.List<x10.types.Type<?>> getTypeParameters() {
    return null;
    }
}

    
    
//#line 6
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
    			x10.runtime.Runtime.start(
    				// body of main activity
    				new x10.core.fun.VoidFun_0_0() { 
    					public void apply() {
    						// preload classes
    						if (Boolean.getBoolean("x10.PRELOAD_CLASSES")) {
    							x10.runtime.impl.java.PreLoader.preLoad(this.getClass().getEnclosingClass(), Boolean.getBoolean("x10.PRELOAD_STRINGS"));
    						}
    
    						// catch and rethrow checked exceptions
    						// (closures cannot throw checked exceptions)
    						try {
    							// call the original app-main method
    							Simple.main(args);
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
    public static void main(final x10.core.Rail<java.lang.String> args)  {
        
//#line 7
((new Simple())).f();
    }/* } */
    
    
//#line 10
void
                  f(
                  ){
        
//#line 11
final x10.
          array.
          RectRegion1 r1 =
          x10.
          lang.
          Region.makeRectangular(1,
                                 42);
        
//#line 12
final x10.
          array.
          RectRegion1 r2 =
          x10.
          lang.
          Region.makeRectangular(1,
                                 10);
        
//#line 13
(x10.
          io.
          Console.OUT).println(new x10.
                                 lang.
                                 Box<java.lang.Boolean>(x10.types.Types.BOOLEAN,
                                                        r1.contains(r2)));
        
//#line 14
(x10.
          io.
          Console.OUT).println(new x10.
                                 lang.
                                 Box<java.lang.Boolean>(x10.types.Types.BOOLEAN,
                                                        r2.contains(r1)));
        
//#line 15
final x10.
          lang.
          Region r3 =
          ((x10.
            lang.
            Region)
            r1);
        
//#line 16
final x10.
          lang.
          Array<java.lang.Integer> a =
          x10.
          lang.
          Array.<java.lang.Integer>make(x10.types.Types.INT,
                                        r1);
        
//#line 17
final x10.
          lang.
          Region r4 =
          ((x10.
            lang.
            Region)
            x10.
            lang.
            Region.makeRectangular(1,
                                   5));
    }
    
    
//#line 20
void
                  pointTest(
                  final x10.
                    lang.
                    Array<java.lang.Integer> a,
                  final x10.
                    array.
                    RectRegion1 r,
                  final x10.
                    lang.
                    Point p1,
                  final x10.
                    lang.
                    Point p2,
                  final x10.
                    array.
                    Point1 p3){
        
//#line 21
(a).apply(p1);
        
//#line 22
(a).apply(p2);
        
//#line 23
(a).apply(p3);
    }
    
    
//#line 26
void
                  partialcopy(
                  final x10.
                    lang.
                    Array<java.lang.Integer> a,
                  final x10.
                    lang.
                    Array<java.lang.Integer> b){
        
//#line 28
/* template:forloop { */for (x10.core.Iterator p__ = (((a).region()).$and((b).region())).iterator(); p__.hasNext(); ) {
        	final  x10.
          lang.
          Point p = (x10.
          lang.
          Point) p__.next();
        	/* Join: { *//* Join: { *//* } */
{
            
//#line 28
(a).set((b).apply(p),
                                p);
        }/* } */
        }/* } */
    }
    
    
//#line 31
void
                  transitiveTest(
                  final x10.
                    lang.
                    Array<java.lang.Integer> a,
                  final x10.
                    lang.
                    Region r1,
                  final x10.
                    lang.
                    Region r2){
        
    }
    
    
//#line 35
void
                  intTest(
                  final x10.
                    lang.
                    Array<java.lang.Integer> a,
                  final x10.
                    array.
                    RectRegion1 r){
        
//#line 36
(a).set(42,
                            5);
    }
    
    
//#line 39
void
                  unionTest(
                  final x10.
                    lang.
                    Region r1,
                  final x10.
                    lang.
                    Region r2){
        
//#line 40
final x10.
          lang.
          Region r3 =
          (r1).$or(r2);
        
//#line 41
final x10.
          lang.
          Array<java.lang.Integer> a =
          x10.
          lang.
          Array.<java.lang.Integer>make(x10.types.Types.INT,
                                        (r1).$or(r2));
        
//#line 42
/* template:forloop { */for (x10.core.Iterator p__ = (r1).iterator(); p__.hasNext(); ) {
        	final  x10.
          lang.
          Point p = (x10.
          lang.
          Point) p__.next();
        	/* Join: { *//* Join: { *//* } */
{
            
//#line 42
(a).set(42,
                                p);
        }/* } */
        }/* } */
        
//#line 43
/* template:forloop { */for (x10.core.Iterator p__ = (r2).iterator(); p__.hasNext(); ) {
        	final  x10.
          lang.
          Point p = (x10.
          lang.
          Point) p__.next();
        	/* Join: { *//* Join: { *//* } */
{
            
//#line 43
(a).set(42,
                                p);
        }/* } */
        }/* } */
    }
    
    
//#line 46
void
                  syntaxTest(
                  final x10.
                    array.
                    RectRegion1 r){
        
//#line 47
final x10.
          array.
          RectRegion1 r2 =
          r;
    }
    
    
//#line 4
public Simple() {
        
//#line 4
super();
    }
}
