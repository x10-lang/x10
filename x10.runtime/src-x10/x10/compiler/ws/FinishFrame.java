package x10.compiler.ws;


@x10.core.X10Generated abstract public class FinishFrame extends x10.compiler.ws.Frame implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, FinishFrame.class);
    
    public static final x10.rtt.RuntimeType<FinishFrame> $RTT = x10.rtt.NamedType.<FinishFrame> make(
    "x10.compiler.ws.FinishFrame", /* base class */FinishFrame.class
    , /* parents */ new x10.rtt.Type[] {x10.compiler.ws.Frame.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(FinishFrame $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + FinishFrame.class + " calling"); } 
        x10.compiler.ws.Frame.$_deserialize_body($_obj, $deserializer);
        $_obj.asyncs = $deserializer.readInt();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        return null;
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        $serializer.write(this.asyncs);
        
    }
    
    // constructor just for allocation
    public FinishFrame(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
public int asyncs;
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
public transient x10.util.Stack<x10.core.X10Throwable> stack;
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"

        // constructor for non-virtual call
        final public x10.compiler.ws.FinishFrame x10$compiler$ws$FinishFrame$$init$S(final x10.compiler.ws.Frame up) { {
                                                                                                                              
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
super.$init(((x10.compiler.ws.Frame)(up)));
                                                                                                                              
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"

                                                                                                                              
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
this.stack = null;
                                                                                                                              {
                                                                                                                                  
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
this.asyncs = 1;
                                                                                                                              }
                                                                                                                          }
                                                                                                                          return this;
                                                                                                                          }
        
        // constructor
        public x10.compiler.ws.FinishFrame $init(final x10.compiler.ws.Frame up){return x10$compiler$ws$FinishFrame$$init$S(up);}
        
        
        
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
public void
                                                                                                           wrapBack(
                                                                                                           final x10.compiler.ws.Worker worker,
                                                                                                           final x10.compiler.ws.Frame frame){
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.core.X10Throwable t48922 =
              ((x10.core.X10Throwable)(worker.
                                         throwable));
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final boolean t48924 =
              ((null) != (t48922));
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
if (t48924) {
                
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.core.X10Throwable t48923 =
                  ((x10.core.X10Throwable)(worker.
                                             throwable));
                
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
this.caught(((x10.core.X10Throwable)(t48923)));
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
worker.throwable = null;
            }
        }
        
        
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
public void
                                                                                                           wrapResume(
                                                                                                           final x10.compiler.ws.Worker worker){
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
int n =
               0;
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.util.concurrent.Monitor t48925 =
              ((x10.util.concurrent.Monitor)(x10.lang.Runtime.atomicMonitor));
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
t48925.lock();
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.compiler.ws.FinishFrame x48920 =
              ((x10.compiler.ws.FinishFrame)(this));
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
;
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final int t48926 =
              x48920.
                asyncs;
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final int t48927 =
              ((t48926) - (((int)(1))));
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final int t48928 =
              x48920.asyncs = t48927;
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
n = t48928;
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.util.concurrent.Monitor t48929 =
              ((x10.util.concurrent.Monitor)(x10.lang.Runtime.atomicMonitor));
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
t48929.unlock();
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final int t48930 =
              n;
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final boolean t48932 =
              ((int) 0) !=
            ((int) t48930);
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
if (t48932) {
                
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.compiler.Abort t48931 =
                  ((x10.compiler.Abort)(x10.compiler.Abort.getInitialized$ABORT()));
                
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
throw t48931;
            }
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.util.Stack<x10.core.X10Throwable> t48933 =
              ((x10.util.Stack)(stack));
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.lang.MultipleExceptions t48934 =
              x10.lang.MultipleExceptions.make__0$1x10$lang$Throwable$2(((x10.util.Stack)(t48933)));
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
worker.throwable = ((x10.core.X10Throwable)(t48934));
        }
        
        
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final public void
                                                                                                           append__0$1x10$lang$Throwable$2(
                                                                                                           final x10.util.Stack s){
            
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final boolean t48944 =
              ((null) != (s));
            
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
if (t48944) {
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.util.concurrent.Monitor t48935 =
                  ((x10.util.concurrent.Monitor)(x10.lang.Runtime.atomicMonitor));
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
t48935.lock();
                
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.util.Stack<x10.core.X10Throwable> t48936 =
                  ((x10.util.Stack)(stack));
                
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final boolean t48938 =
                  ((null) == (t48936));
                
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
if (t48938) {
                    
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.util.Stack<x10.core.X10Throwable> t48937 =
                      ((x10.util.Stack)(new x10.util.Stack<x10.core.X10Throwable>((java.lang.System[]) null, x10.core.X10Throwable.$RTT).$init()));
                    
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
this.stack = ((x10.util.Stack)(t48937));
                }
                
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
while (true) {
                    
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final boolean t48939 =
                      ((x10.util.ArrayList<x10.core.X10Throwable>)s).isEmpty$O();
                    
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final boolean t48942 =
                      !(t48939);
                    
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
if (!(t48942)) {
                        
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
break;
                    }
                    
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.util.Stack<x10.core.X10Throwable> t48965 =
                      ((x10.util.Stack)(stack));
                    
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.core.X10Throwable t48966 =
                      ((x10.util.Stack<x10.core.X10Throwable>)s).pop$G();
                    
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
((x10.util.Stack<x10.core.X10Throwable>)t48965).push__0x10$util$Stack$$T$O(((x10.core.X10Throwable)(t48966)));
                }
                
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.util.concurrent.Monitor t48943 =
                  ((x10.util.concurrent.Monitor)(x10.lang.Runtime.atomicMonitor));
                
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
t48943.unlock();
            }
        }
        
        
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final public void
                                                                                                           append(
                                                                                                           final x10.compiler.ws.FinishFrame ff){
            
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.util.Stack<x10.core.X10Throwable> t48945 =
              ((x10.util.Stack)(ff.
                                  stack));
            
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
this.append__0$1x10$lang$Throwable$2(((x10.util.Stack)(t48945)));
        }
        
        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final public void
                                                                                                           caught(
                                                                                                           final x10.core.X10Throwable t){
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.compiler.Abort t48946 =
              ((x10.compiler.Abort)(x10.compiler.Abort.getInitialized$ABORT()));
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final boolean t48947 =
              x10.rtt.Equality.equalsequals((t),(t48946));
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
if (t48947) {
                
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
throw t;
            }
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.util.concurrent.Monitor t48948 =
              ((x10.util.concurrent.Monitor)(x10.lang.Runtime.atomicMonitor));
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
t48948.lock();
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.util.Stack<x10.core.X10Throwable> t48949 =
              ((x10.util.Stack)(stack));
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final boolean t48951 =
              ((null) == (t48949));
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
if (t48951) {
                
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.util.Stack<x10.core.X10Throwable> t48950 =
                  ((x10.util.Stack)(new x10.util.Stack<x10.core.X10Throwable>((java.lang.System[]) null, x10.core.X10Throwable.$RTT).$init()));
                
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
this.stack = ((x10.util.Stack)(t48950));
            }
            
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.util.Stack<x10.core.X10Throwable> t48952 =
              ((x10.util.Stack)(stack));
            
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
((x10.util.Stack<x10.core.X10Throwable>)t48952).push__0x10$util$Stack$$T$O(((x10.core.X10Throwable)(t)));
            
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.util.concurrent.Monitor t48953 =
              ((x10.util.concurrent.Monitor)(x10.lang.Runtime.atomicMonitor));
            
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
t48953.unlock();
        }
        
        
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final public void
                                                                                                           rethrow(
                                                                                                           ){
            
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.util.Stack<x10.core.X10Throwable> t48954 =
              ((x10.util.Stack)(stack));
            
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final boolean t48955 =
              ((null) != (t48954));
            
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
if (t48955) {
                
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
this.rethrowSlow();
            }
        }
        
        
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final public void
                                                                                                            rethrowSlow(
                                                                                                            ){
            
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.util.Stack<x10.core.X10Throwable> t48956 =
              ((x10.util.Stack)(stack));
            
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.lang.MultipleExceptions t48957 =
              ((x10.lang.MultipleExceptions)(new x10.lang.MultipleExceptions(t48956, (x10.lang.MultipleExceptions.__0$1x10$lang$Throwable$2) null)));
            
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
throw t48957;
        }
        
        
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final public void
                                                                                                            check(
                                                                                                            ){
            
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.util.Stack<x10.core.X10Throwable> t48958 =
              ((x10.util.Stack)(stack));
            
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final boolean t48964 =
              ((null) != (t48958));
            
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
if (t48964) {
                
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
while (true) {
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.util.Stack<x10.core.X10Throwable> t48959 =
                      ((x10.util.Stack)(stack));
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final boolean t48960 =
                      ((x10.util.ArrayList<x10.core.X10Throwable>)t48959).isEmpty$O();
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final boolean t48963 =
                      !(t48960);
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
if (!(t48963)) {
                        
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
break;
                    }
                    
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.util.Stack<x10.core.X10Throwable> t48967 =
                      ((x10.util.Stack)(stack));
                    
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final x10.core.X10Throwable t48968 =
                      ((x10.util.Stack<x10.core.X10Throwable>)t48967).pop$G();
                    
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(t48968)));
                }
            }
        }
        
        
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
final public x10.compiler.ws.FinishFrame
                                                                                                           x10$compiler$ws$FinishFrame$$x10$compiler$ws$FinishFrame$this(
                                                                                                           ){
            
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/FinishFrame.x10"
return x10.compiler.ws.FinishFrame.this;
        }
    
}
