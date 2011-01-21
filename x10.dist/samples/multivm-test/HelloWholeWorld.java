
public class HelloWholeWorld
extends x10.core.Ref
{
    public static final x10.rtt.RuntimeType<HelloWholeWorld> _RTT = new x10.rtt.RuntimeType<HelloWholeWorld>(
    /* base class */HelloWholeWorld.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    ) {public String typeName() {return "HelloWholeWorld";}};
    public x10.rtt.RuntimeType<?> getRTT() {return _RTT;}
    
    
    
        
        
//#line 19
public static class Main extends x10.runtime.impl.java.Runtime {
        public static void main(java.lang.String[] args) {
        // start native runtime
        new Main().start(args);
        }
        
        // called by native runtime inside main x10 thread
        public void runtimeCallback(final x10.array.Array<java.lang.String> args) {
        // call the original app-main method
        HelloWholeWorld.main(args);
        }
        }
        
        // the original app-main method
        public static void main(final x10.
          array.
          Array<java.lang.String> id$0)  {
            {
                
//#line 20
x10.
                  lang.
                  Runtime.ensureNotInAtomic();
                
//#line 20
final x10.
                  lang.
                  FinishState x10$__var0 =
                  x10.
                  lang.
                  Runtime.startFinish();
                
//#line 20
try {try {{
                    {
                        
//#line 20
for (
//#line 20
final x10.
                                           lang.
                                           Iterator<x10.
                                           lang.
                                           Place> p69 =
                                           ((x10.
                                             lang.
                                             Iterator<x10.
                                             lang.
                                             Place>)(x10.
                                             lang.
                                             Iterator)
                                             (((x10.
                                             lang.
                                             Iterable<x10.
                                             lang.
                                             Place>)(x10.
                                             lang.
                                             Place.places()))).iterator());
                                         ((x10.
                                           lang.
                                           Iterator<x10.
                                           lang.
                                           Place>)(p69)).hasNext();
                                         ) {
                            
//#line 20
final x10.
                              lang.
                              Place p =
                              ((x10.
                              lang.
                              Iterator<x10.
                              lang.
                              Place>)(p69)).next$G();
                            
//#line 21
x10.
                              lang.
                              Runtime.runAsync(((x10.
                                                 lang.
                                                 Place)(p)),
                                               new x10.core.fun.VoidFun_0_0() {public final void apply() { try {{
                                                   
//#line 21
x10.
                                                     io.
                                                     Console.OUT.println(((java.lang.Object)((("Hello World from place ".toString()) + (((java.lang.Integer)(p.
                                                                                                                                                               id)))))));
                                               }}catch (x10.runtime.impl.java.X10WrappedThrowable ex) {x10.lang.Runtime.pushException(ex);}}
                                               public x10.rtt.RuntimeType<?> getRTT() { return _RTT;}public x10.rtt.Type<?> getParam(int i) {return null;
                                               }
                                               });
                        }
                    }
                }}catch (x10.core.Throwable __t__) {throw __t__;}catch (java.lang.RuntimeException __e__) {throw x10.core.ThrowableUtilities.getCorrespondingX10Exception(__e__);}catch (java.lang.Error __e__) {throw x10.core.ThrowableUtilities.getCorrespondingX10Error(__e__);}}catch (x10.core.Throwable __desugarer__var__0__) {
                    
//#line 20
x10.
                      lang.
                      Runtime.pushException(((x10.core.Throwable)(__desugarer__var__0__)));
                    
//#line 20
throw new x10.
                      lang.
                      RuntimeException();
                }finally {{
                     
//#line 20
x10.
                       lang.
                       Runtime.stopFinish(((x10.
                                            lang.
                                            FinishState)(x10$__var0)));
                 }}
                }
            }
        
        
//#line 18
public HelloWholeWorld() {
            
//#line 18
super();
            {
                
            }
        }
        
    }
    