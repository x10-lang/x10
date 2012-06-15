package x10.compiler.ws;


@x10.core.X10Generated abstract public class CollectingFinish<$T> extends x10.compiler.ws.FinishFrame implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, CollectingFinish.class);
    
    public static final x10.rtt.RuntimeType<CollectingFinish> $RTT = x10.rtt.NamedType.<CollectingFinish> make(
    "x10.compiler.ws.CollectingFinish", /* base class */CollectingFinish.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.compiler.ws.FinishFrame.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(CollectingFinish $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + CollectingFinish.class + " calling"); } 
        x10.compiler.ws.FinishFrame.$_deserialize_body($_obj, $deserializer);
        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
        x10.lang.Reducible reducer = (x10.lang.Reducible) $deserializer.readRef();
        $_obj.reducer = reducer;
        x10.core.IndexedMemoryChunk resultRail = (x10.core.IndexedMemoryChunk) $deserializer.readRef();
        $_obj.resultRail = resultRail;
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
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
        if (reducer instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.reducer);
        } else {
        $serializer.write(this.reducer);
        }
        if (resultRail instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.resultRail);
        } else {
        $serializer.write(this.resultRail);
        }
        
    }
    
    // constructor just for allocation
    public CollectingFinish(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
    super($dummy);
    x10.compiler.ws.CollectingFinish.$initParams(this, $T);
    }
    
        private x10.rtt.Type $T;
        // initializer of type parameters
        public static void $initParams(final CollectingFinish $this, final x10.rtt.Type $T) {
        $this.$T = $T;
        }
        
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
public x10.lang.Reducible<$T> reducer;
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
public x10.core.IndexedMemoryChunk<$T> resultRail;
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"

        // constructor for non-virtual call
        final public x10.compiler.ws.CollectingFinish<$T> x10$compiler$ws$CollectingFinish$$init$S(final x10.compiler.ws.Frame up,
                                                                                                   final x10.lang.Reducible<$T> rd, __1$1x10$compiler$ws$CollectingFinish$$T$2 $dummy) { {
                                                                                                                                                                                                
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
super.$init(((x10.compiler.ws.Frame)(up)));
                                                                                                                                                                                                
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"

                                                                                                                                                                                                
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
this.reducer = ((x10.lang.Reducible)(rd));
                                                                                                                                                                                                {
                                                                                                                                                                                                    
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final int size =
                                                                                                                                                                                                      x10.lang.Runtime.NTHREADS;
                                                                                                                                                                                                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final x10.core.IndexedMemoryChunk<$T> t48867 =
                                                                                                                                                                                                      x10.core.IndexedMemoryChunk.<$T>allocate($T, ((int)(size)), false);
                                                                                                                                                                                                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
this.resultRail = ((x10.core.IndexedMemoryChunk)(t48867));
                                                                                                                                                                                                    {
                                                                                                                                                                                                        
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
int i =
                                                                                                                                                                                                          0;
                                                                                                                                                                                                        
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
for (;
                                                                                                                                                                                                                                                                                                                   true;
                                                                                                                                                                                                                                                                                                                   ) {
                                                                                                                                                                                                            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final int t48869 =
                                                                                                                                                                                                              i;
                                                                                                                                                                                                            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final boolean t48876 =
                                                                                                                                                                                                              ((t48869) < (((int)(size))));
                                                                                                                                                                                                            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
if (!(t48876)) {
                                                                                                                                                                                                                
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
break;
                                                                                                                                                                                                            }
                                                                                                                                                                                                            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final x10.core.IndexedMemoryChunk<$T> t48899 =
                                                                                                                                                                                                              ((x10.core.IndexedMemoryChunk)(resultRail));
                                                                                                                                                                                                            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final int t48900 =
                                                                                                                                                                                                              i;
                                                                                                                                                                                                            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final x10.lang.Reducible<$T> t48901 =
                                                                                                                                                                                                              ((x10.lang.Reducible)(reducer));
                                                                                                                                                                                                            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final $T t48902 =
                                                                                                                                                                                                              (($T)(((x10.lang.Reducible<$T>)t48901).zero$G()));
                                                                                                                                                                                                            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t48899))).$set(((int)(t48900)), t48902);
                                                                                                                                                                                                            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final int t48903 =
                                                                                                                                                                                                              i;
                                                                                                                                                                                                            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final int t48904 =
                                                                                                                                                                                                              ((t48903) + (((int)(1))));
                                                                                                                                                                                                            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
i = t48904;
                                                                                                                                                                                                        }
                                                                                                                                                                                                    }
                                                                                                                                                                                                }
                                                                                                                                                                                            }
                                                                                                                                                                                            return this;
                                                                                                                                                                                            }
        
        // constructor
        public x10.compiler.ws.CollectingFinish<$T> $init(final x10.compiler.ws.Frame up,
                                                          final x10.lang.Reducible<$T> rd, __1$1x10$compiler$ws$CollectingFinish$$T$2 $dummy){return x10$compiler$ws$CollectingFinish$$init$S(up,rd, $dummy);}
        
        
        
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final public void
                                                                                                                accept__0x10$compiler$ws$CollectingFinish$$T(
                                                                                                                final $T t,
                                                                                                                final x10.compiler.ws.Worker worker){
            {
                
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final int id =
                  worker.
                    id;
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final x10.core.IndexedMemoryChunk<$T> t48880 =
                  ((x10.core.IndexedMemoryChunk)(resultRail));
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final x10.lang.Reducible<$T> t48878 =
                  ((x10.lang.Reducible)(reducer));
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final x10.core.IndexedMemoryChunk<$T> t48877 =
                  ((x10.core.IndexedMemoryChunk)(resultRail));
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final $T t48879 =
                  (($T)((((x10.core.IndexedMemoryChunk<$T>)(t48877))).$apply$G(((int)(id)))));
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final $T t48881 =
                  (($T)((($T)
                          ((x10.lang.Reducible<$T>)t48878).$apply((($T)(t48879)),$T,
                                                                  (($T)(t)),$T))));
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t48880))).$set(((int)(id)), t48881);
            }
        }
        
        
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final public $T
                                                                                                                fastResult$G(
                                                                                                                final x10.compiler.ws.Worker worker){
            {
                
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final x10.core.IndexedMemoryChunk<$T> t48882 =
                  ((x10.core.IndexedMemoryChunk)(resultRail));
                
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final int t48883 =
                  worker.
                    id;
                
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final $T result =
                  (($T)((((x10.core.IndexedMemoryChunk<$T>)(t48882))).$apply$G(((int)(t48883)))));
                
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final x10.core.IndexedMemoryChunk<$T> t48884 =
                  ((x10.core.IndexedMemoryChunk)(resultRail));
                
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t48884))).deallocate();
                
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
return result;
            }
        }
        
        
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final public $T
                                                                                                                result$G(
                                                                                                                ){
            {
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final x10.core.IndexedMemoryChunk<$T> t48885 =
                  ((x10.core.IndexedMemoryChunk)(resultRail));
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
$T result =
                  (($T)((((x10.core.IndexedMemoryChunk<$T>)(t48885))).$apply$G(((int)(0)))));
                
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final int size =
                  x10.lang.Runtime.NTHREADS;
                {
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
int i =
                      1;
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
for (;
                                                                                                                                true;
                                                                                                                                ) {
                        
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final int t48887 =
                          i;
                        
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final boolean t48896 =
                          ((t48887) < (((int)(size))));
                        
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
if (!(t48896)) {
                            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
break;
                        }
                        
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final x10.lang.Reducible<$T> t48905 =
                          ((x10.lang.Reducible)(reducer));
                        
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final $T t48906 =
                          (($T)(result));
                        
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final x10.core.IndexedMemoryChunk<$T> t48907 =
                          ((x10.core.IndexedMemoryChunk)(resultRail));
                        
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final int t48908 =
                          i;
                        
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final $T t48909 =
                          (($T)((((x10.core.IndexedMemoryChunk<$T>)(t48907))).$apply$G(((int)(t48908)))));
                        
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final $T t48910 =
                          (($T)((($T)
                                  ((x10.lang.Reducible<$T>)t48905).$apply((($T)(t48906)),$T,
                                                                          (($T)(t48909)),$T))));
                        
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
result = (($T)(t48910));
                        
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final int t48911 =
                          i;
                        
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final int t48912 =
                          ((t48911) + (((int)(1))));
                        
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
i = t48912;
                    }
                }
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final x10.core.IndexedMemoryChunk<$T> t48897 =
                  ((x10.core.IndexedMemoryChunk)(resultRail));
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t48897))).deallocate();
                
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final $T t48898 =
                  (($T)(result));
                
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
return t48898;
            }
        }
        
        
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
final public x10.compiler.ws.CollectingFinish<$T>
                                                                                                                x10$compiler$ws$CollectingFinish$$x10$compiler$ws$CollectingFinish$this(
                                                                                                                ){
            
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/CollectingFinish.x10"
return x10.compiler.ws.CollectingFinish.this;
        }
    // synthetic type for parameter mangling
    public abstract static class __1$1x10$compiler$ws$CollectingFinish$$T$2 {}
    
}
