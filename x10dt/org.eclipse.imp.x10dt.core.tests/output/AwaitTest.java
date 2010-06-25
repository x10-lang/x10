
public class AwaitTest
extends harness.
  x10Test
{public static final x10.rtt.RuntimeType<AwaitTest>_RTT = new x10.rtt.RuntimeType<AwaitTest>(
/* base class */AwaitTest.class
, /* parents */ new x10.rtt.Type[] {harness.x10Test._RTT}
);
public x10.rtt.RuntimeType getRTT() {return _RTT;}



    
//#line 19
int
      val;
    
    
//#line 21
public boolean
                  run(
                  ){
        
//#line 22
final x10.
          lang.
          Clock c =
          x10.
          lang.
          Clock.make();
        
//#line 23
x10.
          lang.
          Runtime.runAsync(x10.lang.Place.place(x10.core.Ref.home(this)),
                           /* template:tuple { */x10.core.RailFactory.<x10.
                             lang.
                             Clock>makeValRailFromJavaArray(x10.lang.Clock._RTT, new x10.
                             lang.
                             Clock[] { c })/* } */,
                           new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                               
//#line 24
try {{
                                   
//#line 24
x10.
                                     lang.
                                     Runtime.lock();
                                   
//#line 24
while (!((((((int)(val))) > (((int)(43)))))))
                                       
//#line 24
x10.
                                         lang.
                                         Runtime.await();
                               }}finally {{
                                     
//#line 24
x10.
                                       lang.
                                       Runtime.release();
                                 }}
                               
//#line 25
try {{
                                   
//#line 25
x10.
                                     lang.
                                     Runtime.lock();
                                   {
                                       
//#line 25
AwaitTest.this.val = 42;
                                   }
                               }}finally {{
                                     
//#line 25
x10.
                                       lang.
                                       Runtime.release();
                                 }}
                               
//#line 26
try {{
                                   
//#line 26
x10.
                                     lang.
                                     Runtime.lock();
                                   
//#line 26
while (!((((int) val) ==
                                                         ((int) 0))))
                                       
//#line 26
x10.
                                         lang.
                                         Runtime.await();
                               }}finally {{
                                     
//#line 26
x10.
                                       lang.
                                       Runtime.release();
                                 }}
                               
//#line 27
try {{
                                   
//#line 27
x10.
                                     lang.
                                     Runtime.lock();
                                   {
                                       
//#line 27
AwaitTest.this.val = 42;
                                   }
                               }}finally {{
                                     
//#line 27
x10.
                                       lang.
                                       Runtime.release();
                                 }}
                               }}catch (x10.runtime.impl.java.WrappedRuntimeException ex) {x10.lang.Runtime.pushException(ex.getCause());}}
                               });
                               
//#line 29
try {{
                                   
//#line 29
x10.
                                     lang.
                                     Runtime.lock();
                                   {
                                       
//#line 29
this.val = 44;
                                   }
                               }}finally {{
                                     
//#line 29
x10.
                                       lang.
                                       Runtime.release();
                                 }}
                               
//#line 30
try {{
                                   
//#line 30
x10.
                                     lang.
                                     Runtime.lock();
                                   
//#line 30
while (!((((int) val) ==
                                                         ((int) 42))))
                                       
//#line 30
x10.
                                         lang.
                                         Runtime.await();
                               }}finally {{
                                     
//#line 30
x10.
                                       lang.
                                       Runtime.release();
                                 }}
                               
//#line 31
int temp;
                               
//#line 32
try {{
                                   
//#line 32
x10.
                                     lang.
                                     Runtime.lock();
                                   {
                                       
//#line 32
temp = val;
                                   }
                               }}finally {{
                                     
//#line 32
x10.
                                       lang.
                                       Runtime.release();
                                 }}
                               
//#line 33
x10.
                                 io.
                                 Console.OUT.println((("temp = ") + (temp)));
                               
//#line 34
if (((int) temp) !=
                                               ((int) 42)) {
                                   
//#line 35
return false;
                               }
                               
//#line 36
try {{
                                   
//#line 36
x10.
                                     lang.
                                     Runtime.lock();
                                   {
                                       
//#line 36
this.val = 0;
                                   }
                               }}finally {{
                                     
//#line 36
x10.
                                       lang.
                                       Runtime.release();
                                 }}
                               
//#line 37
try {{
                                   
//#line 37
x10.
                                     lang.
                                     Runtime.lock();
                                   
//#line 37
while (!((((int) val) ==
                                                         ((int) 42))))
                                       
//#line 37
x10.
                                         lang.
                                         Runtime.await();
                               }}finally {{
                                     
//#line 37
x10.
                                       lang.
                                       Runtime.release();
                                 }}
                               
//#line 38
x10.
                                 lang.
                                 Runtime.next();
                               
//#line 39
int temp2;
                               
//#line 40
try {{
                                   
//#line 40
x10.
                                     lang.
                                     Runtime.lock();
                                   {
                                       
//#line 40
temp2 = val;
                                   }
                               }}finally {{
                                     
//#line 40
x10.
                                       lang.
                                       Runtime.release();
                                 }}
                               
//#line 41
x10.
                                 io.
                                 Console.OUT.println((("val = ") + (temp2)));
                               
//#line 42
return ((int) temp2) ==
                               ((int) 42);
                               }
                               
                               
//#line 45
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
                               							AwaitTest.main(args);
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
                               public static void main(final x10.core.Rail<java.lang.String> id$33680)  {
                                   
//#line 46
new AwaitTest().executeAsync();
                               }/* } */
                               
                               public AwaitTest() {
                                   super();
                                   
//#line 19
this.val = 0;
                               }
                               
                               }
                               