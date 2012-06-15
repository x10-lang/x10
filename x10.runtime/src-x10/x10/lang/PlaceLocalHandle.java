package x10.lang;


@x10.core.X10Generated final public class PlaceLocalHandle<$T> extends x10.core.Struct implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, PlaceLocalHandle.class);
    
    public static final x10.rtt.RuntimeType<PlaceLocalHandle> $RTT = x10.rtt.NamedType.<PlaceLocalHandle> make(
    "x10.lang.PlaceLocalHandle", /* base class */PlaceLocalHandle.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.STRUCT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(PlaceLocalHandle $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + PlaceLocalHandle.class + " calling"); } 
        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
        x10.runtime.impl.java.PlaceLocalHandle __NATIVE_FIELD__ = (x10.runtime.impl.java.PlaceLocalHandle) $deserializer.readRef();
        $_obj.__NATIVE_FIELD__ = __NATIVE_FIELD__;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        PlaceLocalHandle $_obj = new PlaceLocalHandle(null, (java.lang.System) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
        if (__NATIVE_FIELD__ instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.__NATIVE_FIELD__);
        } else {
        $serializer.write(this.__NATIVE_FIELD__);
        }
        
    }
    
    // zero value constructor
    public PlaceLocalHandle(final x10.rtt.Type $T, final java.lang.System $dummy) { this.$T = $T; this.__NATIVE_FIELD__ = new x10.runtime.impl.java.PlaceLocalHandle<$T>($T, $dummy); }
    // constructor just for allocation
    public PlaceLocalHandle(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
    super($dummy);
    x10.lang.PlaceLocalHandle.$initParams(this, $T);
    }
    
        private x10.rtt.Type $T;
        // initializer of type parameters
        public static void $initParams(final PlaceLocalHandle $this, final x10.rtt.Type $T) {
        $this.$T = $T;
        }
        
        
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
public x10.runtime.impl.java.PlaceLocalHandle<$T> __NATIVE_FIELD__;
        
        
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
public PlaceLocalHandle(final x10.rtt.Type $T,
                                                                                                                               final x10.runtime.impl.java.PlaceLocalHandle<$T> id0) {x10.lang.PlaceLocalHandle.$initParams(this, $T);
                                                                                                                                                                                           {
                                                                                                                                                                                              
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
this.__NATIVE_FIELD__ = id0;
                                                                                                                                                                                          }}
        
        
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
public PlaceLocalHandle(final x10.rtt.Type $T) {x10.lang.PlaceLocalHandle.$initParams(this, $T);
                                                                                                                                                            {
                                                                                                                                                               
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
this.__NATIVE_FIELD__ = new x10.runtime.impl.java.PlaceLocalHandle<$T>((java.lang.System[]) null, $T).$init();
                                                                                                                                                           }}
        
        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final public $T
                                                                                                         $apply$G(
                                                                                                         ){
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.runtime.impl.java.PlaceLocalHandle<$T> t55566 =
              this.
                __NATIVE_FIELD__;
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final $T t55567 =
              (($T)(((x10.runtime.impl.java.PlaceLocalHandle<$T>)t55566).$apply$G()));
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
return t55567;
        }
        
        
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final public void
                                                                                                         set__0x10$lang$PlaceLocalHandle$$T(
                                                                                                         final $T newVal){
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.runtime.impl.java.PlaceLocalHandle<$T> t55568 =
              this.
                __NATIVE_FIELD__;
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
((x10.runtime.impl.java.PlaceLocalHandle<$T>)t55568).set__0x10$lang$PlaceLocalHandle$$T((($T)(newVal)));
        }
        
        
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final public int
                                                                                                         hashCode(
                                                                                                         ){
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.runtime.impl.java.PlaceLocalHandle<$T> t55569 =
              this.
                __NATIVE_FIELD__;
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final int t55570 =
              ((x10.runtime.impl.java.PlaceLocalHandle<$T>)t55569).hashCode();
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
return t55570;
        }
        
        
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final public java.lang.String
                                                                                                         toString(
                                                                                                         ){
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.runtime.impl.java.PlaceLocalHandle<$T> t55571 =
              this.
                __NATIVE_FIELD__;
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final java.lang.String t55572 =
              ((x10.runtime.impl.java.PlaceLocalHandle<$T>)t55571).toString();
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
return t55572;
        }
        
        
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final public static <$T>x10.lang.PlaceLocalHandle<$T>
                                                                                                         make__1$1x10$lang$PlaceLocalHandle$$T$2(
                                                                                                         final x10.rtt.Type $T,
                                                                                                         final x10.array.Dist dist,
                                                                                                         final x10.core.fun.Fun_0_0<$T> init){
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.array.PlaceGroup t55573 =
              dist.places();
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.PlaceLocalHandle<$T> t55574 =
              x10.lang.PlaceLocalHandle.<$T>make__1$1x10$lang$PlaceLocalHandle$$T$2($T, ((x10.array.PlaceGroup)(t55573)),
                                                                                    ((x10.core.fun.Fun_0_0)(init)));
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
return t55574;
        }
        
        
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final public static <$T, $U>x10.lang.PlaceLocalHandle<$T>
                                                                                                         make__1$1x10$lang$Place$3x10$lang$PlaceLocalHandle$$U$2__2$1x10$lang$PlaceLocalHandle$$U$3x10$lang$PlaceLocalHandle$$T$2(
                                                                                                         final x10.rtt.Type $T,
                                                                                                         final x10.rtt.Type $U,
                                                                                                         final x10.array.Dist dist,
                                                                                                         final x10.core.fun.Fun_0_1<x10.lang.Place,$U> init_here,
                                                                                                         final x10.core.fun.Fun_0_1<$U,$T> init_there){
            
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.array.PlaceGroup t55575 =
              dist.places();
            
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.PlaceLocalHandle<$T> t55576 =
              x10.lang.PlaceLocalHandle.<$T,
            $U>make__1$1x10$lang$Place$3x10$lang$PlaceLocalHandle$$U$2__2$1x10$lang$PlaceLocalHandle$$U$3x10$lang$PlaceLocalHandle$$T$2($T, $U, ((x10.array.PlaceGroup)(t55575)),
                                                                                                                                        ((x10.core.fun.Fun_0_1)(init_here)),
                                                                                                                                        ((x10.core.fun.Fun_0_1)(init_there)));
            
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
return t55576;
        }
        
        
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final public static <$T>x10.lang.PlaceLocalHandle<$T>
                                                                                                         make__1$1x10$lang$PlaceLocalHandle$$T$2(
                                                                                                         final x10.rtt.Type $T,
                                                                                                         final x10.array.PlaceGroup pg,
                                                                                                         final x10.core.fun.Fun_0_0<$T> init){
            
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.Place t55551 =
              ((x10.lang.Place)(x10.lang.Place.getInitialized$FIRST_PLACE()));
            
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.PlaceLocalHandle<$T> handle =
              x10.lang.Runtime.<x10.lang.PlaceLocalHandle<$T>>evalAt__1$1x10$lang$Runtime$$T$2$G(x10.rtt.ParameterizedType.make(x10.lang.PlaceLocalHandle.$RTT, $T), ((x10.lang.Place)(t55551)),
                                                                                                 ((x10.core.fun.Fun_0_0)(new x10.lang.PlaceLocalHandle.$Closure$135<$T>($T))));
            {
                
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
x10.lang.Runtime.ensureNotInAtomic();
                
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.FinishState x10$__var6 =
                  x10.lang.Runtime.startFinish();
                
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
try {try {{
                    {
                        
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.Iterator<x10.lang.Place> p55559 =
                          pg.iterator();
                        
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
for (;
                                                                                                                            true;
                                                                                                                            ) {
                            
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final boolean t55579 =
                              ((x10.lang.Iterator<x10.lang.Place>)p55559).hasNext$O();
                            
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
if (!(t55579)) {
                                
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
break;
                            }
                            
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.Place p55607 =
                              ((x10.lang.Iterator<x10.lang.Place>)p55559).next$G();
                            
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
x10.lang.Runtime.runAsync(((x10.lang.Place)(p55607)),
                                                                                                                                                     ((x10.core.fun.VoidFun_0_0)(new x10.lang.PlaceLocalHandle.$Closure$136<$T>($T, init,
                                                                                                                                                                                                                                handle, (x10.lang.PlaceLocalHandle.$Closure$136.__0$1x10$lang$PlaceLocalHandle$$Closure$136$$T$2__1$1x10$lang$PlaceLocalHandle$$Closure$136$$T$2) null))));
                        }
                    }
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__0__) {
                    
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__0__)));
                    
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
throw new x10.lang.Exception();
                }finally {{
                     
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var6)));
                 }}
                }
            
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
return handle;
            }
        
        
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final public static <$T, $U>x10.lang.PlaceLocalHandle<$T>
                                                                                                          make__1$1x10$lang$Place$3x10$lang$PlaceLocalHandle$$U$2__2$1x10$lang$PlaceLocalHandle$$U$3x10$lang$PlaceLocalHandle$$T$2(
                                                                                                          final x10.rtt.Type $T,
                                                                                                          final x10.rtt.Type $U,
                                                                                                          final x10.array.PlaceGroup pg,
                                                                                                          final x10.core.fun.Fun_0_1<x10.lang.Place,$U> init_here,
                                                                                                          final x10.core.fun.Fun_0_1<$U,$T> init_there){
            
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.Place t55553 =
              ((x10.lang.Place)(x10.lang.Place.getInitialized$FIRST_PLACE()));
            
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.PlaceLocalHandle<$T> handle =
              x10.lang.Runtime.<x10.lang.PlaceLocalHandle<$T>>evalAt__1$1x10$lang$Runtime$$T$2$G(x10.rtt.ParameterizedType.make(x10.lang.PlaceLocalHandle.$RTT, $T), ((x10.lang.Place)(t55553)),
                                                                                                 ((x10.core.fun.Fun_0_0)(new x10.lang.PlaceLocalHandle.$Closure$137<$T, $U>($T, $U))));
            {
                
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
x10.lang.Runtime.ensureNotInAtomic();
                
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.FinishState x10$__var7 =
                  x10.lang.Runtime.startFinish();
                
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
try {try {{
                    {
                        
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.Iterator<x10.lang.Place> p55561 =
                          pg.iterator();
                        
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
for (;
                                                                                                                             true;
                                                                                                                             ) {
                            
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final boolean t55582 =
                              ((x10.lang.Iterator<x10.lang.Place>)p55561).hasNext$O();
                            
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
if (!(t55582)) {
                                
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
break;
                            }
                            
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.Place p55609 =
                              ((x10.lang.Iterator<x10.lang.Place>)p55561).next$G();
                            
//#line 121 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final $U v55610 =
                              (($U)((($U)
                                      ((x10.core.fun.Fun_0_1<x10.lang.Place,$U>)init_here).$apply(p55609,x10.lang.Place.$RTT))));
                            
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
x10.lang.Runtime.runAsync(((x10.lang.Place)(p55609)),
                                                                                                                                                      ((x10.core.fun.VoidFun_0_0)(new x10.lang.PlaceLocalHandle.$Closure$138<$T, $U>($T, $U, init_there,
                                                                                                                                                                                                                                     v55610,
                                                                                                                                                                                                                                     handle, (x10.lang.PlaceLocalHandle.$Closure$138.__0$1x10$lang$PlaceLocalHandle$$Closure$138$$U$3x10$lang$PlaceLocalHandle$$Closure$138$$T$2__1x10$lang$PlaceLocalHandle$$Closure$138$$U__2$1x10$lang$PlaceLocalHandle$$Closure$138$$T$2) null))));
                        }
                    }
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__0__) {
                    
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__0__)));
                    
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
throw new x10.lang.Exception();
                }finally {{
                     
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var7)));
                 }}
                }
            
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
return handle;
            }
        
        
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final public static <$T>x10.lang.PlaceLocalHandle<$T>
                                                                                                          makeFlat__1$1x10$lang$PlaceLocalHandle$$T$2(
                                                                                                          final x10.rtt.Type $T,
                                                                                                          final x10.array.Dist dist,
                                                                                                          final x10.core.fun.Fun_0_0<$T> init){
            
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.array.PlaceGroup t55583 =
              dist.places();
            
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.PlaceLocalHandle<$T> t55584 =
              x10.lang.PlaceLocalHandle.<$T>makeFlat__1$1x10$lang$PlaceLocalHandle$$T$2($T, ((x10.array.PlaceGroup)(t55583)),
                                                                                        ((x10.core.fun.Fun_0_0)(init)));
            
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
return t55584;
        }
        
        
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final public static <$T, $U>x10.lang.PlaceLocalHandle<$T>
                                                                                                          makeFlat__1$1x10$lang$Place$3x10$lang$PlaceLocalHandle$$U$2__2$1x10$lang$PlaceLocalHandle$$U$3x10$lang$PlaceLocalHandle$$T$2(
                                                                                                          final x10.rtt.Type $T,
                                                                                                          final x10.rtt.Type $U,
                                                                                                          final x10.array.Dist dist,
                                                                                                          final x10.core.fun.Fun_0_1<x10.lang.Place,$U> init_here,
                                                                                                          final x10.core.fun.Fun_0_1<$U,$T> init_there){
            
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.array.PlaceGroup t55585 =
              dist.places();
            
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.PlaceLocalHandle<$T> t55586 =
              x10.lang.PlaceLocalHandle.<$T,
            $U>makeFlat__1$1x10$lang$Place$3x10$lang$PlaceLocalHandle$$U$2__2$1x10$lang$PlaceLocalHandle$$U$3x10$lang$PlaceLocalHandle$$T$2($T, $U, ((x10.array.PlaceGroup)(t55585)),
                                                                                                                                            ((x10.core.fun.Fun_0_1)(init_here)),
                                                                                                                                            ((x10.core.fun.Fun_0_1)(init_there)));
            
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
return t55586;
        }
        
        
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final public static <$T>x10.lang.PlaceLocalHandle<$T>
                                                                                                          makeFlat__1$1x10$lang$PlaceLocalHandle$$T$2(
                                                                                                          final x10.rtt.Type $T,
                                                                                                          final x10.array.PlaceGroup pg,
                                                                                                          final x10.core.fun.Fun_0_0<$T> init){
            
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.Place t55555 =
              ((x10.lang.Place)(x10.lang.Place.getInitialized$FIRST_PLACE()));
            
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.PlaceLocalHandle<$T> handle =
              x10.lang.Runtime.<x10.lang.PlaceLocalHandle<$T>>evalAt__1$1x10$lang$Runtime$$T$2$G(x10.rtt.ParameterizedType.make(x10.lang.PlaceLocalHandle.$RTT, $T), ((x10.lang.Place)(t55555)),
                                                                                                 ((x10.core.fun.Fun_0_0)(new x10.lang.PlaceLocalHandle.$Closure$139<$T>($T))));
            {
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
x10.lang.Runtime.ensureNotInAtomic();
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.FinishState x10$__var8 =
                  x10.lang.Runtime.startFinish((int)(x10.compiler.Pragma.FINISH_SPMD));
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
try {try {{
                    {
                        
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.Iterator<x10.lang.Place> p55563 =
                          pg.iterator();
                        
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
for (;
                                                                                                                             true;
                                                                                                                             ) {
                            
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final boolean t55589 =
                              ((x10.lang.Iterator<x10.lang.Place>)p55563).hasNext$O();
                            
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
if (!(t55589)) {
                                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
break;
                            }
                            
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.Place p55612 =
                              ((x10.lang.Iterator<x10.lang.Place>)p55563).next$G();
                            
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
x10.lang.Runtime.runAsync(((x10.lang.Place)(p55612)),
                                                                                                                                                      ((x10.core.fun.VoidFun_0_0)(new x10.lang.PlaceLocalHandle.$Closure$140<$T>($T, init,
                                                                                                                                                                                                                                 handle, (x10.lang.PlaceLocalHandle.$Closure$140.__0$1x10$lang$PlaceLocalHandle$$Closure$140$$T$2__1$1x10$lang$PlaceLocalHandle$$Closure$140$$T$2) null))));
                        }
                    }
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__0__) {
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__0__)));
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
throw new x10.lang.Exception();
                }finally {{
                     
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var8)));
                 }}
                }
            
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
return handle;
            }
        
        
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final public static <$T, $U>x10.lang.PlaceLocalHandle<$T>
                                                                                                          makeFlat__1$1x10$lang$Place$3x10$lang$PlaceLocalHandle$$U$2__2$1x10$lang$PlaceLocalHandle$$U$3x10$lang$PlaceLocalHandle$$T$2(
                                                                                                          final x10.rtt.Type $T,
                                                                                                          final x10.rtt.Type $U,
                                                                                                          final x10.array.PlaceGroup pg,
                                                                                                          final x10.core.fun.Fun_0_1<x10.lang.Place,$U> init_here,
                                                                                                          final x10.core.fun.Fun_0_1<$U,$T> init_there){
            
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.Place t55557 =
              ((x10.lang.Place)(x10.lang.Place.getInitialized$FIRST_PLACE()));
            
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.PlaceLocalHandle<$T> handle =
              x10.lang.Runtime.<x10.lang.PlaceLocalHandle<$T>>evalAt__1$1x10$lang$Runtime$$T$2$G(x10.rtt.ParameterizedType.make(x10.lang.PlaceLocalHandle.$RTT, $T), ((x10.lang.Place)(t55557)),
                                                                                                 ((x10.core.fun.Fun_0_0)(new x10.lang.PlaceLocalHandle.$Closure$141<$T, $U>($T, $U))));
            {
                
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
x10.lang.Runtime.ensureNotInAtomic();
                
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.FinishState x10$__var9 =
                  x10.lang.Runtime.startFinish((int)(x10.compiler.Pragma.FINISH_SPMD));
                
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
try {try {{
                    {
                        
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.Iterator<x10.lang.Place> p55565 =
                          pg.iterator();
                        
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
for (;
                                                                                                                             true;
                                                                                                                             ) {
                            
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final boolean t55592 =
                              ((x10.lang.Iterator<x10.lang.Place>)p55565).hasNext$O();
                            
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
if (!(t55592)) {
                                
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
break;
                            }
                            
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.Place p55614 =
                              ((x10.lang.Iterator<x10.lang.Place>)p55565).next$G();
                            
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final $U v55615 =
                              (($U)((($U)
                                      ((x10.core.fun.Fun_0_1<x10.lang.Place,$U>)init_here).$apply(p55614,x10.lang.Place.$RTT))));
                            
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
x10.lang.Runtime.runAsync(((x10.lang.Place)(p55614)),
                                                                                                                                                      ((x10.core.fun.VoidFun_0_0)(new x10.lang.PlaceLocalHandle.$Closure$142<$T, $U>($T, $U, init_there,
                                                                                                                                                                                                                                     v55615,
                                                                                                                                                                                                                                     handle, (x10.lang.PlaceLocalHandle.$Closure$142.__0$1x10$lang$PlaceLocalHandle$$Closure$142$$U$3x10$lang$PlaceLocalHandle$$Closure$142$$T$2__1x10$lang$PlaceLocalHandle$$Closure$142$$U__2$1x10$lang$PlaceLocalHandle$$Closure$142$$T$2) null))));
                        }
                    }
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__0__) {
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__0__)));
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
throw new x10.lang.Exception();
                }finally {{
                     
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var9)));
                 }}
                }
            
//#line 211 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
return handle;
            }
        
        
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final public java.lang.String
                                                                                                         typeName(
                                                                                                         ){try {return x10.rtt.Types.typeName(this);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final public boolean
                                                                                                         equals(
                                                                                                         java.lang.Object other){
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final java.lang.Object t55595 =
              other;
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final boolean t55596 =
              x10.lang.PlaceLocalHandle.$RTT.isInstance(t55595, $T);
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final boolean t55597 =
              !(t55596);
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
if (t55597) {
                
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
return false;
            }
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final java.lang.Object t55598 =
              other;
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.PlaceLocalHandle<$T> t55599 =
              ((x10.lang.PlaceLocalHandle)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.lang.PlaceLocalHandle.$RTT, $T),t55598));
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final boolean t55600 =
              this.equals__0$1x10$lang$PlaceLocalHandle$$T$2$O(((x10.lang.PlaceLocalHandle)(t55599)));
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
return t55600;
        }
        
        
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final public boolean
                                                                                                         equals__0$1x10$lang$PlaceLocalHandle$$T$2$O(
                                                                                                         x10.lang.PlaceLocalHandle other){
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
return true;
        }
        
        
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final public boolean
                                                                                                         _struct_equals$O(
                                                                                                         java.lang.Object other){
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final java.lang.Object t55601 =
              other;
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final boolean t55602 =
              x10.lang.PlaceLocalHandle.$RTT.isInstance(t55601, $T);
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final boolean t55603 =
              !(t55602);
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
if (t55603) {
                
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
return false;
            }
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final java.lang.Object t55604 =
              other;
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.PlaceLocalHandle<$T> t55605 =
              ((x10.lang.PlaceLocalHandle)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.lang.PlaceLocalHandle.$RTT, $T),t55604));
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final boolean t55606 =
              this._struct_equals__0$1x10$lang$PlaceLocalHandle$$T$2$O(((x10.lang.PlaceLocalHandle)(t55605)));
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
return t55606;
        }
        
        
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final public boolean
                                                                                                         _struct_equals__0$1x10$lang$PlaceLocalHandle$$T$2$O(
                                                                                                         x10.lang.PlaceLocalHandle other){
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
return true;
        }
        
        
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final public x10.lang.PlaceLocalHandle<$T>
                                                                                                         x10$lang$PlaceLocalHandle$$x10$lang$PlaceLocalHandle$this(
                                                                                                         ){
            
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
return x10.lang.PlaceLocalHandle.this;
        }
        
        @x10.core.X10Generated public static class $Closure$135<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$135.class);
            
            public static final x10.rtt.RuntimeType<$Closure$135> $RTT = x10.rtt.StaticFunType.<$Closure$135> make(
            /* base class */$Closure$135.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.ParameterizedType.make(x10.lang.PlaceLocalHandle.$RTT, x10.rtt.UnresolvedType.PARAM(0))), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$135 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$135.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$135 $_obj = new $Closure$135((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                
            }
            
            // constructor just for allocation
            public $Closure$135(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.lang.PlaceLocalHandle.$Closure$135.$initParams(this, $T);
            }
            // bridge for method abstract public ()=>U.operator()():U
            public x10.lang.PlaceLocalHandle
              $apply$G(){return $apply();}
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$135 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public x10.lang.PlaceLocalHandle<$T>
                  $apply(
                  ){
                    
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.PlaceLocalHandle<$T> t55550 =
                      new x10.lang.PlaceLocalHandle<$T>($T);
                    
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
return t55550;
                }
                
                public $Closure$135(final x10.rtt.Type $T) {x10.lang.PlaceLocalHandle.$Closure$135.$initParams(this, $T);
                                                                 {
                                                                    
                                                                }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$136<$T> extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, $Closure$136.class);
            
            public static final x10.rtt.RuntimeType<$Closure$136> $RTT = x10.rtt.StaticVoidFunType.<$Closure$136> make(
            /* base class */$Closure$136.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$136 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$136.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                x10.core.fun.Fun_0_0 init = (x10.core.fun.Fun_0_0) $deserializer.readRef();
                $_obj.init = init;
                x10.lang.PlaceLocalHandle handle = (x10.lang.PlaceLocalHandle) $deserializer.readRef();
                $_obj.handle = handle;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$136 $_obj = new $Closure$136((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                if (init instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.init);
                } else {
                $serializer.write(this.init);
                }
                if (handle instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.handle);
                } else {
                $serializer.write(this.handle);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$136(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.lang.PlaceLocalHandle.$Closure$136.$initParams(this, $T);
            }
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$136 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public void
                  $apply(
                  ){
                    
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final $T t55608 =
                      (($T)(((x10.core.fun.Fun_0_0<$T>)this.
                                                         init).$apply$G()));
                    
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
((x10.lang.PlaceLocalHandle<$T>)this.
                                                                                                                                                     handle).set__0x10$lang$PlaceLocalHandle$$T((($T)(t55608)));
                }
                
                public x10.core.fun.Fun_0_0<$T> init;
                public x10.lang.PlaceLocalHandle<$T> handle;
                
                public $Closure$136(final x10.rtt.Type $T,
                                    final x10.core.fun.Fun_0_0<$T> init,
                                    final x10.lang.PlaceLocalHandle<$T> handle, __0$1x10$lang$PlaceLocalHandle$$Closure$136$$T$2__1$1x10$lang$PlaceLocalHandle$$Closure$136$$T$2 $dummy) {x10.lang.PlaceLocalHandle.$Closure$136.$initParams(this, $T);
                                                                                                                                                                                               {
                                                                                                                                                                                                  this.init = ((x10.core.fun.Fun_0_0)(init));
                                                                                                                                                                                                  this.handle = ((x10.lang.PlaceLocalHandle)(handle));
                                                                                                                                                                                              }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$lang$PlaceLocalHandle$$Closure$136$$T$2__1$1x10$lang$PlaceLocalHandle$$Closure$136$$T$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$137<$T, $U> extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$137.class);
            
            public static final x10.rtt.RuntimeType<$Closure$137> $RTT = x10.rtt.StaticFunType.<$Closure$137> make(
            /* base class */$Closure$137.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.ParameterizedType.make(x10.lang.PlaceLocalHandle.$RTT, x10.rtt.UnresolvedType.PARAM(0))), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;if (i ==1)return $U;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$137 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$137.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.$U = ( x10.rtt.Type ) $deserializer.readRef();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$137 $_obj = new $Closure$137((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$U);
                
            }
            
            // constructor just for allocation
            public $Closure$137(final java.lang.System[] $dummy, final x10.rtt.Type $T, final x10.rtt.Type $U) { 
            super($dummy);
            x10.lang.PlaceLocalHandle.$Closure$137.$initParams(this, $T, $U);
            }
            // bridge for method abstract public ()=>U.operator()():U
            public x10.lang.PlaceLocalHandle
              $apply$G(){return $apply();}
            
                private x10.rtt.Type $T;
                private x10.rtt.Type $U;
                // initializer of type parameters
                public static void $initParams(final $Closure$137 $this, final x10.rtt.Type $T, final x10.rtt.Type $U) {
                $this.$T = $T;
                $this.$U = $U;
                }
                
                
                public x10.lang.PlaceLocalHandle<$T>
                  $apply(
                  ){
                    
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.PlaceLocalHandle<$T> t55552 =
                      new x10.lang.PlaceLocalHandle<$T>($T);
                    
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
return t55552;
                }
                
                public $Closure$137(final x10.rtt.Type $T,
                                    final x10.rtt.Type $U) {x10.lang.PlaceLocalHandle.$Closure$137.$initParams(this, $T, $U);
                                                                 {
                                                                    
                                                                }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$138<$T, $U> extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, $Closure$138.class);
            
            public static final x10.rtt.RuntimeType<$Closure$138> $RTT = x10.rtt.StaticVoidFunType.<$Closure$138> make(
            /* base class */$Closure$138.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;if (i ==1)return $U;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$138 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$138.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.$U = ( x10.rtt.Type ) $deserializer.readRef();
                x10.core.fun.Fun_0_1 init_there = (x10.core.fun.Fun_0_1) $deserializer.readRef();
                $_obj.init_there = init_there;
                $_obj.v55610 = $deserializer.readRef();
                x10.lang.PlaceLocalHandle handle = (x10.lang.PlaceLocalHandle) $deserializer.readRef();
                $_obj.handle = handle;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$138 $_obj = new $Closure$138((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$U);
                if (init_there instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.init_there);
                } else {
                $serializer.write(this.init_there);
                }
                if (v55610 instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.v55610);
                } else {
                $serializer.write(this.v55610);
                }
                if (handle instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.handle);
                } else {
                $serializer.write(this.handle);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$138(final java.lang.System[] $dummy, final x10.rtt.Type $T, final x10.rtt.Type $U) { 
            super($dummy);
            x10.lang.PlaceLocalHandle.$Closure$138.$initParams(this, $T, $U);
            }
            
                private x10.rtt.Type $T;
                private x10.rtt.Type $U;
                // initializer of type parameters
                public static void $initParams(final $Closure$138 $this, final x10.rtt.Type $T, final x10.rtt.Type $U) {
                $this.$T = $T;
                $this.$U = $U;
                }
                
                
                public void
                  $apply(
                  ){
                    
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final $T t55611 =
                      (($T)((($T)
                              ((x10.core.fun.Fun_0_1<$U,$T>)this.
                                                              init_there).$apply(this.
                                                                                   v55610,$U))));
                    
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
((x10.lang.PlaceLocalHandle<$T>)this.
                                                                                                                                                      handle).set__0x10$lang$PlaceLocalHandle$$T((($T)(t55611)));
                }
                
                public x10.core.fun.Fun_0_1<$U,$T> init_there;
                public $U v55610;
                public x10.lang.PlaceLocalHandle<$T> handle;
                
                public $Closure$138(final x10.rtt.Type $T,
                                    final x10.rtt.Type $U,
                                    final x10.core.fun.Fun_0_1<$U,$T> init_there,
                                    final $U v55610,
                                    final x10.lang.PlaceLocalHandle<$T> handle, __0$1x10$lang$PlaceLocalHandle$$Closure$138$$U$3x10$lang$PlaceLocalHandle$$Closure$138$$T$2__1x10$lang$PlaceLocalHandle$$Closure$138$$U__2$1x10$lang$PlaceLocalHandle$$Closure$138$$T$2 $dummy) {x10.lang.PlaceLocalHandle.$Closure$138.$initParams(this, $T, $U);
                                                                                                                                                                                                                                                                                      {
                                                                                                                                                                                                                                                                                         this.init_there = ((x10.core.fun.Fun_0_1)(init_there));
                                                                                                                                                                                                                                                                                         this.v55610 = (($U)(v55610));
                                                                                                                                                                                                                                                                                         this.handle = ((x10.lang.PlaceLocalHandle)(handle));
                                                                                                                                                                                                                                                                                     }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$lang$PlaceLocalHandle$$Closure$138$$U$3x10$lang$PlaceLocalHandle$$Closure$138$$T$2__1x10$lang$PlaceLocalHandle$$Closure$138$$U__2$1x10$lang$PlaceLocalHandle$$Closure$138$$T$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$139<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$139.class);
            
            public static final x10.rtt.RuntimeType<$Closure$139> $RTT = x10.rtt.StaticFunType.<$Closure$139> make(
            /* base class */$Closure$139.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.ParameterizedType.make(x10.lang.PlaceLocalHandle.$RTT, x10.rtt.UnresolvedType.PARAM(0))), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$139 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$139.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$139 $_obj = new $Closure$139((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                
            }
            
            // constructor just for allocation
            public $Closure$139(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.lang.PlaceLocalHandle.$Closure$139.$initParams(this, $T);
            }
            // bridge for method abstract public ()=>U.operator()():U
            public x10.lang.PlaceLocalHandle
              $apply$G(){return $apply();}
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$139 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public x10.lang.PlaceLocalHandle<$T>
                  $apply(
                  ){
                    
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.PlaceLocalHandle<$T> t55554 =
                      new x10.lang.PlaceLocalHandle<$T>($T);
                    
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
return t55554;
                }
                
                public $Closure$139(final x10.rtt.Type $T) {x10.lang.PlaceLocalHandle.$Closure$139.$initParams(this, $T);
                                                                 {
                                                                    
                                                                }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$140<$T> extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, $Closure$140.class);
            
            public static final x10.rtt.RuntimeType<$Closure$140> $RTT = x10.rtt.StaticVoidFunType.<$Closure$140> make(
            /* base class */$Closure$140.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$140 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$140.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                x10.core.fun.Fun_0_0 init = (x10.core.fun.Fun_0_0) $deserializer.readRef();
                $_obj.init = init;
                x10.lang.PlaceLocalHandle handle = (x10.lang.PlaceLocalHandle) $deserializer.readRef();
                $_obj.handle = handle;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$140 $_obj = new $Closure$140((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                if (init instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.init);
                } else {
                $serializer.write(this.init);
                }
                if (handle instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.handle);
                } else {
                $serializer.write(this.handle);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$140(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.lang.PlaceLocalHandle.$Closure$140.$initParams(this, $T);
            }
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$140 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public void
                  $apply(
                  ){
                    
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final $T t55613 =
                      (($T)(((x10.core.fun.Fun_0_0<$T>)this.
                                                         init).$apply$G()));
                    
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
((x10.lang.PlaceLocalHandle<$T>)this.
                                                                                                                                                      handle).set__0x10$lang$PlaceLocalHandle$$T((($T)(t55613)));
                }
                
                public x10.core.fun.Fun_0_0<$T> init;
                public x10.lang.PlaceLocalHandle<$T> handle;
                
                public $Closure$140(final x10.rtt.Type $T,
                                    final x10.core.fun.Fun_0_0<$T> init,
                                    final x10.lang.PlaceLocalHandle<$T> handle, __0$1x10$lang$PlaceLocalHandle$$Closure$140$$T$2__1$1x10$lang$PlaceLocalHandle$$Closure$140$$T$2 $dummy) {x10.lang.PlaceLocalHandle.$Closure$140.$initParams(this, $T);
                                                                                                                                                                                               {
                                                                                                                                                                                                  this.init = ((x10.core.fun.Fun_0_0)(init));
                                                                                                                                                                                                  this.handle = ((x10.lang.PlaceLocalHandle)(handle));
                                                                                                                                                                                              }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$lang$PlaceLocalHandle$$Closure$140$$T$2__1$1x10$lang$PlaceLocalHandle$$Closure$140$$T$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$141<$T, $U> extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$141.class);
            
            public static final x10.rtt.RuntimeType<$Closure$141> $RTT = x10.rtt.StaticFunType.<$Closure$141> make(
            /* base class */$Closure$141.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.ParameterizedType.make(x10.lang.PlaceLocalHandle.$RTT, x10.rtt.UnresolvedType.PARAM(0))), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;if (i ==1)return $U;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$141 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$141.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.$U = ( x10.rtt.Type ) $deserializer.readRef();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$141 $_obj = new $Closure$141((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$U);
                
            }
            
            // constructor just for allocation
            public $Closure$141(final java.lang.System[] $dummy, final x10.rtt.Type $T, final x10.rtt.Type $U) { 
            super($dummy);
            x10.lang.PlaceLocalHandle.$Closure$141.$initParams(this, $T, $U);
            }
            // bridge for method abstract public ()=>U.operator()():U
            public x10.lang.PlaceLocalHandle
              $apply$G(){return $apply();}
            
                private x10.rtt.Type $T;
                private x10.rtt.Type $U;
                // initializer of type parameters
                public static void $initParams(final $Closure$141 $this, final x10.rtt.Type $T, final x10.rtt.Type $U) {
                $this.$T = $T;
                $this.$U = $U;
                }
                
                
                public x10.lang.PlaceLocalHandle<$T>
                  $apply(
                  ){
                    
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final x10.lang.PlaceLocalHandle<$T> t55556 =
                      new x10.lang.PlaceLocalHandle<$T>($T);
                    
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
return t55556;
                }
                
                public $Closure$141(final x10.rtt.Type $T,
                                    final x10.rtt.Type $U) {x10.lang.PlaceLocalHandle.$Closure$141.$initParams(this, $T, $U);
                                                                 {
                                                                    
                                                                }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$142<$T, $U> extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, $Closure$142.class);
            
            public static final x10.rtt.RuntimeType<$Closure$142> $RTT = x10.rtt.StaticVoidFunType.<$Closure$142> make(
            /* base class */$Closure$142.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;if (i ==1)return $U;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$142 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$142.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.$U = ( x10.rtt.Type ) $deserializer.readRef();
                x10.core.fun.Fun_0_1 init_there = (x10.core.fun.Fun_0_1) $deserializer.readRef();
                $_obj.init_there = init_there;
                $_obj.v55615 = $deserializer.readRef();
                x10.lang.PlaceLocalHandle handle = (x10.lang.PlaceLocalHandle) $deserializer.readRef();
                $_obj.handle = handle;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$142 $_obj = new $Closure$142((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$U);
                if (init_there instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.init_there);
                } else {
                $serializer.write(this.init_there);
                }
                if (v55615 instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.v55615);
                } else {
                $serializer.write(this.v55615);
                }
                if (handle instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.handle);
                } else {
                $serializer.write(this.handle);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$142(final java.lang.System[] $dummy, final x10.rtt.Type $T, final x10.rtt.Type $U) { 
            super($dummy);
            x10.lang.PlaceLocalHandle.$Closure$142.$initParams(this, $T, $U);
            }
            
                private x10.rtt.Type $T;
                private x10.rtt.Type $U;
                // initializer of type parameters
                public static void $initParams(final $Closure$142 $this, final x10.rtt.Type $T, final x10.rtt.Type $U) {
                $this.$T = $T;
                $this.$U = $U;
                }
                
                
                public void
                  $apply(
                  ){
                    
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
final $T t55616 =
                      (($T)((($T)
                              ((x10.core.fun.Fun_0_1<$U,$T>)this.
                                                              init_there).$apply(this.
                                                                                   v55615,$U))));
                    
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/PlaceLocalHandle.x10"
((x10.lang.PlaceLocalHandle<$T>)this.
                                                                                                                                                      handle).set__0x10$lang$PlaceLocalHandle$$T((($T)(t55616)));
                }
                
                public x10.core.fun.Fun_0_1<$U,$T> init_there;
                public $U v55615;
                public x10.lang.PlaceLocalHandle<$T> handle;
                
                public $Closure$142(final x10.rtt.Type $T,
                                    final x10.rtt.Type $U,
                                    final x10.core.fun.Fun_0_1<$U,$T> init_there,
                                    final $U v55615,
                                    final x10.lang.PlaceLocalHandle<$T> handle, __0$1x10$lang$PlaceLocalHandle$$Closure$142$$U$3x10$lang$PlaceLocalHandle$$Closure$142$$T$2__1x10$lang$PlaceLocalHandle$$Closure$142$$U__2$1x10$lang$PlaceLocalHandle$$Closure$142$$T$2 $dummy) {x10.lang.PlaceLocalHandle.$Closure$142.$initParams(this, $T, $U);
                                                                                                                                                                                                                                                                                      {
                                                                                                                                                                                                                                                                                         this.init_there = ((x10.core.fun.Fun_0_1)(init_there));
                                                                                                                                                                                                                                                                                         this.v55615 = (($U)(v55615));
                                                                                                                                                                                                                                                                                         this.handle = ((x10.lang.PlaceLocalHandle)(handle));
                                                                                                                                                                                                                                                                                     }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$lang$PlaceLocalHandle$$Closure$142$$U$3x10$lang$PlaceLocalHandle$$Closure$142$$T$2__1x10$lang$PlaceLocalHandle$$Closure$142$$U__2$1x10$lang$PlaceLocalHandle$$Closure$142$$T$2 {}
                
            }
            
        
        }
        