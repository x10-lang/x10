package x10.lang;


@x10.core.X10Generated public class Accumulator<$T> extends x10.lang.Acc implements x10.io.CustomSerialization
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Accumulator.class);
    
    public static final x10.rtt.RuntimeType<Accumulator> $RTT = x10.rtt.NamedType.<Accumulator> make(
    "x10.lang.Accumulator", /* base class */Accumulator.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.io.CustomSerialization.$RTT, x10.lang.Acc.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
    // custom serializer
    private transient x10.io.SerialData $$serialdata;
    private Object writeReplace() { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " calling"); } $$serialdata = serialize(); if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " returned " + $$serialdata); } return this; }
    private Object readResolve() { return new Accumulator($T, $$serialdata); }
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
    oos.writeObject($T);
    oos.writeObject($$serialdata); }
    private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {
    $T = (x10.rtt.Type) ois.readObject();
    $$serialdata = (x10.io.SerialData) ois.readObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Accumulator $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("X10JavaSerializable: $_deserialize_body() of " + Accumulator.class + " calling"); } 
        x10.io.SerialData $$serialdata = (x10.io.SerialData) $deserializer.readRef();
        x10.rtt.Type $T = ( x10.rtt.Type ) $deserializer.readRef();
        $_obj.$T = $T;
        $_obj.$init($$serialdata);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Accumulator $_obj = new Accumulator((java.lang.System[]) null, (x10.rtt.Type) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println(" CustomSerialization : $_serialize of " + this + " calling"); } 
        $$serialdata = serialize(); 
        $serializer.write($$serialdata);
        $serializer.write($T);
        
    }
    
    // constructor just for allocation
    public Accumulator(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
    super($dummy);
    x10.lang.Accumulator.$initParams(this, $T);
    }
    
        private x10.rtt.Type $T;
        // initializer of type parameters
        public static void $initParams(final Accumulator $this, final x10.rtt.Type $T) {
        $this.$T = $T;
        }
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
public x10.core.GlobalRef<x10.lang.Acc> root;
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
public $T curr;
        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
public x10.lang.Reducible<$T> red;
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
public static void
                                                                                                    MYPRINT(
                                                                                                    final java.lang.String msg){
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final int t50502 =
              x10.lang.Runtime.workerId$O();
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final java.lang.String t50503 =
              (("Worker=") + ((x10.core.Int.$box(t50502))));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final java.lang.String t50504 =
              ((t50503) + (" "));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final java.lang.String t50505 =
              ((t50504) + (msg));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
java.lang.System.err.println(t50505);
        }
        
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
// creation method for java code (1-phase java constructor)
        public Accumulator(final x10.rtt.Type $T,
                           final x10.lang.Reducible<$T> red, __0$1x10$lang$Accumulator$$T$2 $dummy){this((java.lang.System[]) null, $T);
                                                                                                        $init(red, (x10.lang.Accumulator.__0$1x10$lang$Accumulator$$T$2) null);}
        
        // constructor for non-virtual call
        final public x10.lang.Accumulator<$T> x10$lang$Accumulator$$init$S(final x10.lang.Reducible<$T> red, __0$1x10$lang$Accumulator$$T$2 $dummy) { {
                                                                                                                                                             
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
super.$init();
                                                                                                                                                             
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"

                                                                                                                                                             
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
this.red = ((x10.lang.Reducible)(red));
                                                                                                                                                             
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.core.GlobalRef<x10.lang.Acc> t50506 =
                                                                                                                                                               ((x10.core.GlobalRef)(new x10.core.GlobalRef<x10.lang.Acc>(x10.lang.Acc.$RTT, ((x10.lang.Acc)(this)), (x10.core.GlobalRef.__0x10$lang$GlobalRef$$T) null)));
                                                                                                                                                             
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
this.root = ((x10.core.GlobalRef)(t50506));
                                                                                                                                                             
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final $T t50507 =
                                                                                                                                                               (($T)(((x10.lang.Reducible<$T>)red).zero$G()));
                                                                                                                                                             
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
this.curr = (($T)(t50507));
                                                                                                                                                         }
                                                                                                                                                         return this;
                                                                                                                                                         }
        
        // constructor
        public x10.lang.Accumulator<$T> $init(final x10.lang.Reducible<$T> red, __0$1x10$lang$Accumulator$$T$2 $dummy){return x10$lang$Accumulator$$init$S(red, $dummy);}
        
        
        
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
// creation method for java code (1-phase java constructor)
        public Accumulator(final x10.rtt.Type $T,
                           final x10.io.SerialData data){this((java.lang.System[]) null, $T);
                                                             $init(data);}
        
        // constructor for non-virtual call
        final public x10.lang.Accumulator<$T> x10$lang$Accumulator$$init$S(final x10.io.SerialData data) {x10$lang$Accumulator$init_for_reflection(data);
                                                                                                              
                                                                                                              return this;
                                                                                                              }
        public void x10$lang$Accumulator$init_for_reflection(x10.io.SerialData data) {
             {
                
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
super.$init();
                
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"

                
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final java.lang.Object t50508 =
                  ((java.lang.Object)(data.
                                        data));
                
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.array.Array<java.lang.Object> __desugarer__var__44__50496 =
                  ((x10.array.Array)(x10.rtt.Types.<x10.array.Array<java.lang.Object>> cast(t50508,x10.rtt.ParameterizedType.make(x10.array.Array.$RTT, x10.rtt.Types.ANY))));
                
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
x10.array.Array<java.lang.Object> ret50497 =
                   null;
                
//#line 38 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final int t50551 =
                  ((x10.array.Array<java.lang.Object>)__desugarer__var__44__50496).
                    rank;
                
//#line 38 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final boolean t50552 =
                  ((int) t50551) ==
                ((int) 1);
                
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final boolean t50553 =
                  !(t50552);
                
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
if (t50553) {
                    
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final boolean t50554 =
                      true;
                    
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
if (t50554) {
                        
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.lang.FailedDynamicCheckException t50555 =
                          new x10.lang.FailedDynamicCheckException("x10.array.Array[x10.lang.Any]{self.rank==1}");
                        
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
throw t50555;
                    }
                }
                
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
ret50497 = ((x10.array.Array)(__desugarer__var__44__50496));
                
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.array.Array<java.lang.Object> arr =
                  ((x10.array.Array)(ret50497));
                
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final java.lang.Object t50514 =
                  ((x10.array.Array<java.lang.Object>)arr).$apply$G((int)(0));
                
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.lang.Reducible<$T> t50515 =
                  x10.rtt.Types.<x10.lang.Reducible<$T>> cast(t50514,x10.rtt.ParameterizedType.make(x10.lang.Reducible.$RTT, $T));
                
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
this.red = ((x10.lang.Reducible)(t50515));
                
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final java.lang.Object t50516 =
                  ((x10.array.Array<java.lang.Object>)arr).$apply$G((int)(1));
                
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.core.GlobalRef<x10.lang.Acc> t50517 =
                  ((x10.core.GlobalRef)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.lang.Acc.$RTT),t50516));
                
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
this.root = ((x10.core.GlobalRef)(t50517));
                
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.lang.Reducible<$T> t50518 =
                  ((x10.lang.Reducible)(red));
                
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final $T t50519 =
                  (($T)(((x10.lang.Reducible<$T>)t50518).zero$G()));
                
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
this.curr = (($T)(t50519));
            }}
            
        // constructor
        public x10.lang.Accumulator<$T> $init(final x10.io.SerialData data){return x10$lang$Accumulator$$init$S(data);}
        
        
        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
public x10.io.SerialData
                                                                                                    serialize(
                                                                                                    ){
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.lang.Reducible<$T> t50520 =
              ((x10.lang.Reducible)(red));
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final java.lang.Object t50522 =
              ((java.lang.Object)
                t50520);
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.core.GlobalRef<x10.lang.Acc> t50521 =
              ((x10.core.GlobalRef)(root));
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final java.lang.Object t50523 =
              ((java.lang.Object)
                t50521);
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.array.Array<java.lang.Object> t50524 =
              ((x10.array.Array)(x10.core.ArrayFactory.<java.lang.Object> makeArrayFromJavaArray(x10.rtt.Types.ANY, new java.lang.Object[] {t50522, t50523})));
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.io.SerialData t50525 =
              ((x10.io.SerialData)(new x10.io.SerialData((java.lang.System[]) null).$init(((java.lang.Object)(t50524)),
                                                                                          ((x10.io.SerialData)(null)))));
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
return t50525;
        }
        
        
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
public x10.lang.Accumulator<$T>
                                                                                                    me(
                                                                                                    ){
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.core.GlobalRef<x10.lang.Acc> t50526 =
              ((x10.core.GlobalRef)(root));
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.core.GlobalRef<x10.lang.Acc> __desugarer__var__45__50499 =
              ((x10.core.GlobalRef)(((x10.core.GlobalRef)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.lang.Acc.$RTT),t50526))));
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
x10.core.GlobalRef<x10.lang.Acc> ret50500 =
               null;
            
//#line 59 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.lang.Place t50556 =
              ((x10.lang.Place)((__desugarer__var__45__50499).home));
            
//#line 59 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final boolean t50557 =
              x10.rtt.Equality.equalsequals((t50556),(x10.lang.Runtime.home()));
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final boolean t50558 =
              !(t50557);
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
if (t50558) {
                
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final boolean t50559 =
                  true;
                
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
if (t50559) {
                    
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.lang.FailedDynamicCheckException t50560 =
                      new x10.lang.FailedDynamicCheckException("x10.lang.GlobalRef[x10.lang.Acc]{self.home==here}");
                    
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
throw t50560;
                }
            }
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
ret50500 = ((x10.core.GlobalRef)(__desugarer__var__45__50499));
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.core.GlobalRef<x10.lang.Acc> t50532 =
              ((x10.core.GlobalRef)(ret50500));
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.lang.Acc t50533 =
              (((x10.core.GlobalRef<x10.lang.Acc>)(t50532))).$apply$G();
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.lang.Accumulator<$T> t50534 =
              x10.rtt.Types.<x10.lang.Accumulator<$T>> cast(t50533,x10.rtt.ParameterizedType.make(x10.lang.Accumulator.$RTT, $T));
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
return t50534;
        }
        
        
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
public void
                                                                                                    $larrow__0x10$lang$Accumulator$$T(
                                                                                                    final $T t){
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.core.GlobalRef<x10.lang.Acc> t50535 =
              ((x10.core.GlobalRef)(root));
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.lang.Place t50539 =
              ((x10.lang.Place)((t50535).home));
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
x10.lang.Runtime.runAt(((x10.lang.Place)(t50539)),
                                                                                                                             ((x10.core.fun.VoidFun_0_0)(new x10.lang.Accumulator.$Closure$100<$T>($T, ((x10.lang.Accumulator<$T>)(this)),
                                                                                                                                                                                                   t, (x10.lang.Accumulator.$Closure$100.__0$1x10$lang$Accumulator$$Closure$100$$T$2__1x10$lang$Accumulator$$Closure$100$$T) null))));
        }
        
        
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
public void
                                                                                                    $set__0x10$lang$Accumulator$$T(
                                                                                                    final $T t){
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.core.GlobalRef<x10.lang.Acc> t50540 =
              ((x10.core.GlobalRef)(root));
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.lang.Place t50541 =
              ((x10.lang.Place)((t50540).home));
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
x10.lang.Runtime.runAt(((x10.lang.Place)(t50541)),
                                                                                                                             ((x10.core.fun.VoidFun_0_0)(new x10.lang.Accumulator.$Closure$101<$T>($T, ((x10.lang.Accumulator<$T>)(this)),
                                                                                                                                                                                                   t, (x10.lang.Accumulator.$Closure$101.__0$1x10$lang$Accumulator$$Closure$101$$T$2__1x10$lang$Accumulator$$Closure$101$$T) null))));
        }
        
        
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
private $T
                                                                                                    localGetResult$G(
                                                                                                    ){
            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final $T t50542 =
              (($T)(curr));
            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
return t50542;
        }
        
        public static <$T>$T
          localGetResult$P__0$1x10$lang$Accumulator$$T$2$G(
          final x10.rtt.Type $T,
          final x10.lang.Accumulator<$T> Accumulator){
            return ((x10.lang.Accumulator<$T>)Accumulator).localGetResult$G();
        }
        
        
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
public $T
                                                                                                    $apply$G(
                                                                                                    ){
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.core.GlobalRef<x10.lang.Acc> t50399 =
              ((x10.core.GlobalRef)(root));
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.lang.Place t50402 =
              ((x10.lang.Place)((t50399).home));
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final $T t50403 =
              (($T)(x10.lang.Runtime.<$T>evalAt__1$1x10$lang$Runtime$$T$2$G($T, ((x10.lang.Place)(t50402)),
                                                                            ((x10.core.fun.Fun_0_0)(new x10.lang.Accumulator.$Closure$102<$T>($T, ((x10.lang.Accumulator<$T>)(this)), (x10.lang.Accumulator.$Closure$102.__0$1x10$lang$Accumulator$$Closure$102$$T$2) null))))));
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
return t50403;
        }
        
        
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
public void
                                                                                                    supply(
                                                                                                    final java.lang.Object t){
            
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final $T t50543 =
              (($T)(x10.rtt.Types.<$T> cast(t,$T)));
            
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
this.$larrow__0x10$lang$Accumulator$$T((($T)(t50543)));
        }
        
        
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
public void
                                                                                                    reset(
                                                                                                    final java.lang.Object t){
            
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final $T t50544 =
              (($T)(x10.rtt.Types.<$T> cast(t,$T)));
            
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
this.$set__0x10$lang$Accumulator$$T((($T)(t50544)));
        }
        
        
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
public java.lang.Object
                                                                                                     result(
                                                                                                     ){
            
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final $T t50545 =
              (($T)(this.$apply$G()));
            
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
return ((java.lang.Object)
                                                                                                                t50545);
        }
        
        
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
public java.lang.Object
                                                                                                     calcResult(
                                                                                                     ){
            
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.lang.IllegalOperationException t50546 =
              ((x10.lang.IllegalOperationException)(new x10.lang.IllegalOperationException()));
            
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
throw t50546;
        }
        
        
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
public void
                                                                                                     acceptResult(
                                                                                                     final java.lang.Object a){
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.lang.IllegalOperationException t50547 =
              ((x10.lang.IllegalOperationException)(new x10.lang.IllegalOperationException()));
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
throw t50547;
        }
        
        
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
public x10.core.GlobalRef<x10.lang.Acc>
                                                                                                     getRoot(
                                                                                                     ){
            
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.core.GlobalRef<x10.lang.Acc> t50548 =
              ((x10.core.GlobalRef)(root));
            
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
return t50548;
        }
        
        
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
public x10.lang.Place
                                                                                                     home(
                                                                                                     ){
            
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.core.GlobalRef<x10.lang.Acc> t50549 =
              ((x10.core.GlobalRef)(root));
            
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.lang.Place t50550 =
              ((x10.lang.Place)((t50549).home));
            
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
return t50550;
        }
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final public x10.lang.Accumulator<$T>
                                                                                                    x10$lang$Accumulator$$x10$lang$Accumulator$this(
                                                                                                    ){
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
return x10.lang.Accumulator.this;
        }
        
        @x10.core.X10Generated public static class $Closure$100<$T> extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$100.class);
            
            public static final x10.rtt.RuntimeType<$Closure$100> $RTT = x10.rtt.StaticVoidFunType.<$Closure$100> make(
            /* base class */$Closure$100.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$100 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$100.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                x10.lang.Accumulator out$$ = (x10.lang.Accumulator) $deserializer.readRef();
                $_obj.out$$ = out$$;
                $_obj.t = $deserializer.readRef();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$100 $_obj = new $Closure$100((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                if (out$$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$$);
                } else {
                $serializer.write(this.out$$);
                }
                if (t instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.t);
                } else {
                $serializer.write(this.t);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$100(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.lang.Accumulator.$Closure$100.$initParams(this, $T);
            }
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$100 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public void
                  $apply(
                  ){
                    
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.lang.Accumulator<$T> me =
                      ((x10.lang.Accumulator<$T>)
                        ((x10.lang.Accumulator<$T>)this.
                                                     out$$).me());
                    
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
try {{
                        
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
x10.lang.Runtime.enterAtomic();
                        {
                            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.lang.Reducible<$T> t50536 =
                              ((x10.lang.Reducible)(((x10.lang.Accumulator<$T>)me).
                                                      red));
                            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final $T t50537 =
                              (($T)(((x10.lang.Accumulator<$T>)me).
                                      curr));
                            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final $T t50538 =
                              (($T)((($T)
                                      ((x10.lang.Reducible<$T>)t50536).$apply((($T)(t50537)),$T,
                                                                              (($T)(this.
                                                                                      t)),$T))));
                            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
me.curr = (($T)(t50538));
                        }
                    }}finally {{
                          
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
x10.lang.Runtime.exitAtomic();
                      }}
                    }
                
                public x10.lang.Accumulator<$T> out$$;
                public $T t;
                
                public $Closure$100(final x10.rtt.Type $T,
                                    final x10.lang.Accumulator<$T> out$$,
                                    final $T t, __0$1x10$lang$Accumulator$$Closure$100$$T$2__1x10$lang$Accumulator$$Closure$100$$T $dummy) {x10.lang.Accumulator.$Closure$100.$initParams(this, $T);
                                                                                                                                                 {
                                                                                                                                                    this.out$$ = out$$;
                                                                                                                                                    this.t = (($T)(t));
                                                                                                                                                }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$lang$Accumulator$$Closure$100$$T$2__1x10$lang$Accumulator$$Closure$100$$T {}
                
                }
                
            @x10.core.X10Generated public static class $Closure$101<$T> extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
            {
                private static final long serialVersionUID = 1L;
                private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$101.class);
                
                public static final x10.rtt.RuntimeType<$Closure$101> $RTT = x10.rtt.StaticVoidFunType.<$Closure$101> make(
                /* base class */$Closure$101.class, 
                /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
                , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                );
                public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                
                public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
                private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$101 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$101.class + " calling"); } 
                    $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                    x10.lang.Accumulator out$$ = (x10.lang.Accumulator) $deserializer.readRef();
                    $_obj.out$$ = out$$;
                    $_obj.t = $deserializer.readRef();
                    return $_obj;
                    
                }
                
                public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    $Closure$101 $_obj = new $Closure$101((java.lang.System[]) null, (x10.rtt.Type) null);
                    $deserializer.record_reference($_obj);
                    return $_deserialize_body($_obj, $deserializer);
                    
                }
                
                public short $_get_serialization_id() {
                
                     return $_serialization_id;
                    
                }
                
                public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                
                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                    if (out$$ instanceof x10.x10rt.X10JavaSerializable) {
                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$$);
                    } else {
                    $serializer.write(this.out$$);
                    }
                    if (t instanceof x10.x10rt.X10JavaSerializable) {
                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.t);
                    } else {
                    $serializer.write(this.t);
                    }
                    
                }
                
                // constructor just for allocation
                public $Closure$101(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
                super($dummy);
                x10.lang.Accumulator.$Closure$101.$initParams(this, $T);
                }
                
                    private x10.rtt.Type $T;
                    // initializer of type parameters
                    public static void $initParams(final $Closure$101 $this, final x10.rtt.Type $T) {
                    $this.$T = $T;
                    }
                    
                    
                    public void
                      $apply(
                      ){
                        
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.lang.Accumulator<$T> me =
                          ((x10.lang.Accumulator<$T>)
                            ((x10.lang.Accumulator<$T>)this.
                                                         out$$).me());
                        
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
try {{
                            
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
x10.lang.Runtime.enterAtomic();
                            {
                                
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
me.curr = (($T)(this.
                                                                                                                                            t));
                            }
                        }}finally {{
                              
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
x10.lang.Runtime.exitAtomic();
                          }}
                        }
                    
                    public x10.lang.Accumulator<$T> out$$;
                    public $T t;
                    
                    public $Closure$101(final x10.rtt.Type $T,
                                        final x10.lang.Accumulator<$T> out$$,
                                        final $T t, __0$1x10$lang$Accumulator$$Closure$101$$T$2__1x10$lang$Accumulator$$Closure$101$$T $dummy) {x10.lang.Accumulator.$Closure$101.$initParams(this, $T);
                                                                                                                                                     {
                                                                                                                                                        this.out$$ = out$$;
                                                                                                                                                        this.t = (($T)(t));
                                                                                                                                                    }}
                    // synthetic type for parameter mangling
                    public abstract static class __0$1x10$lang$Accumulator$$Closure$101$$T$2__1x10$lang$Accumulator$$Closure$101$$T {}
                    
                    }
                    
                @x10.core.X10Generated public static class $Closure$102<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
                {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$102.class);
                    
                    public static final x10.rtt.RuntimeType<$Closure$102> $RTT = x10.rtt.StaticFunType.<$Closure$102> make(
                    /* base class */$Closure$102.class, 
                    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
                    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.Types.OBJECT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$102 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$102.class + " calling"); } 
                        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                        x10.lang.Accumulator out$$ = (x10.lang.Accumulator) $deserializer.readRef();
                        $_obj.out$$ = out$$;
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        $Closure$102 $_obj = new $Closure$102((java.lang.System[]) null, (x10.rtt.Type) null);
                        $deserializer.record_reference($_obj);
                        return $_deserialize_body($_obj, $deserializer);
                        
                    }
                    
                    public short $_get_serialization_id() {
                    
                         return $_serialization_id;
                        
                    }
                    
                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                    
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                        if (out$$ instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$$);
                        } else {
                        $serializer.write(this.out$$);
                        }
                        
                    }
                    
                    // constructor just for allocation
                    public $Closure$102(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
                    super($dummy);
                    x10.lang.Accumulator.$Closure$102.$initParams(this, $T);
                    }
                    
                        private x10.rtt.Type $T;
                        // initializer of type parameters
                        public static void $initParams(final $Closure$102 $this, final x10.rtt.Type $T) {
                        $this.$T = $T;
                        }
                        
                        
                        public $T
                          $apply$G(
                          ){
                            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final x10.lang.Accumulator<$T> t50400 =
                              ((x10.lang.Accumulator<$T>)
                                ((x10.lang.Accumulator<$T>)this.
                                                             out$$).me());
                            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
final $T t50401 =
                              (($T)(((x10.lang.Accumulator<$T>)t50400).localGetResult$G()));
                            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Accumulator.x10"
return t50401;
                        }
                        
                        public x10.lang.Accumulator<$T> out$$;
                        
                        public $Closure$102(final x10.rtt.Type $T,
                                            final x10.lang.Accumulator<$T> out$$, __0$1x10$lang$Accumulator$$Closure$102$$T$2 $dummy) {x10.lang.Accumulator.$Closure$102.$initParams(this, $T);
                                                                                                                                            {
                                                                                                                                               this.out$$ = out$$;
                                                                                                                                           }}
                        // synthetic type for parameter mangling
                        public abstract static class __0$1x10$lang$Accumulator$$Closure$102$$T$2 {}
                        
                    }
                    
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$lang$Accumulator$$T$2 {}
                
            }
            