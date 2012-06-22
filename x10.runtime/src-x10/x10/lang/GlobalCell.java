package x10.lang;

@x10.core.X10Generated public class GlobalCell<$T> extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, GlobalCell.class);
    
    public static final x10.rtt.RuntimeType<GlobalCell> $RTT = x10.rtt.NamedType.<GlobalCell> make(
    "x10.lang.GlobalCell", /* base class */GlobalCell.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(GlobalCell $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + GlobalCell.class + " calling"); } 
        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
        x10.core.GlobalRef root = (x10.core.GlobalRef) $deserializer.readRef();
        $_obj.root = root;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        GlobalCell $_obj = new GlobalCell((java.lang.System[]) null, (x10.rtt.Type) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
        if (root instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.root);
        } else {
        $serializer.write(this.root);
        }
        
    }
    
    // constructor just for allocation
    public GlobalCell(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
    super($dummy);
    x10.lang.GlobalCell.$initParams(this, $T);
    }
    
        private x10.rtt.Type $T;
        // initializer of type parameters
        public static void $initParams(final GlobalCell $this, final x10.rtt.Type $T) {
        $this.$T = $T;
        }
        
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
public x10.core.GlobalRef<x10.lang.Cell<$T>> root;
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
// creation method for java code (1-phase java constructor)
        public GlobalCell(final x10.rtt.Type $T,
                          final $T v, __0x10$lang$GlobalCell$$T $dummy){this((java.lang.System[]) null, $T);
                                                                            $init(v, (x10.lang.GlobalCell.__0x10$lang$GlobalCell$$T) null);}
        
        // constructor for non-virtual call
        final public x10.lang.GlobalCell<$T> x10$lang$GlobalCell$$init$S(final $T v, __0x10$lang$GlobalCell$$T $dummy) { {
                                                                                                                                
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"

                                                                                                                                
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"

                                                                                                                                
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
final x10.lang.Cell<$T> t53076 =
                                                                                                                                  ((x10.lang.Cell)(new x10.lang.Cell<$T>((java.lang.System[]) null, $T).$init((($T)(v)), (x10.lang.Cell.__0x10$lang$Cell$$T) null)));
                                                                                                                                
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
final x10.core.GlobalRef<x10.lang.Cell<$T>> t53077 =
                                                                                                                                  ((x10.core.GlobalRef)(new x10.core.GlobalRef<x10.lang.Cell<$T>>(x10.rtt.ParameterizedType.make(x10.lang.Cell.$RTT, $T), ((x10.lang.Cell<$T>)(t53076)), (x10.core.GlobalRef.__0x10$lang$GlobalRef$$T) null)));
                                                                                                                                
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
this.root = ((x10.core.GlobalRef)(t53077));
                                                                                                                            }
                                                                                                                            return this;
                                                                                                                            }
        
        // constructor
        public x10.lang.GlobalCell<$T> $init(final $T v, __0x10$lang$GlobalCell$$T $dummy){return x10$lang$GlobalCell$$init$S(v, $dummy);}
        
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
public java.lang.String
                                                                                                   toString(
                                                                                                   ){
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
final x10.core.GlobalRef<x10.lang.Cell<$T>> t53078 =
              ((x10.core.GlobalRef)(root));
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
final java.lang.String t53079 =
              (((x10.core.GlobalRef<x10.lang.Cell<$T>>)(t53078))).toString();
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
return t53079;
        }
        
        
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
public $T
                                                                                                   $apply$G(
                                                                                                   ){
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
final x10.core.GlobalRef<x10.lang.Cell<$T>> t53070 =
              ((x10.core.GlobalRef)(root));
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
final x10.lang.Place t53074 =
              ((x10.lang.Place)((t53070).home));
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
final $T t53075 =
              (($T)(x10.lang.Runtime.<$T>evalAt__1$1x10$lang$Runtime$$T$2$G($T, ((x10.lang.Place)(t53074)),
                                                                            ((x10.core.fun.Fun_0_0)(new x10.lang.GlobalCell.$Closure$127<$T>($T, ((x10.lang.GlobalCell<$T>)(this)),
                                                                                                                                             root, (x10.lang.GlobalCell.$Closure$127.__0$1x10$lang$GlobalCell$$Closure$127$$T$2__1$1x10$lang$Cell$1x10$lang$GlobalCell$$Closure$127$$T$2$2) null))))));
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
return t53075;
        }
        
        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
public void
                                                                                                   $apply__0x10$lang$GlobalCell$$T(
                                                                                                   final $T x){
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
final x10.core.GlobalRef<x10.lang.Cell<$T>> t53080 =
              ((x10.core.GlobalRef)(root));
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
final x10.lang.Place t53083 =
              ((x10.lang.Place)((t53080).home));
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
x10.lang.Runtime.runAt(((x10.lang.Place)(t53083)),
                                                                                                                            ((x10.core.fun.VoidFun_0_0)(new x10.lang.GlobalCell.$Closure$128<$T>($T, ((x10.lang.GlobalCell<$T>)(this)),
                                                                                                                                                                                                 root,
                                                                                                                                                                                                 x, (x10.lang.GlobalCell.$Closure$128.__0$1x10$lang$GlobalCell$$Closure$128$$T$2__1$1x10$lang$Cell$1x10$lang$GlobalCell$$Closure$128$$T$2$2__2x10$lang$GlobalCell$$Closure$128$$T) null))));
        }
        
        
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
public void
                                                                                                   $set__0x10$lang$GlobalCell$$T(
                                                                                                   final $T x){
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
this.set__0x10$lang$GlobalCell$$T$G((($T)(x)));
        }
        
        
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
public $T
                                                                                                   set__0x10$lang$GlobalCell$$T$G(
                                                                                                   final $T x){
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
final x10.core.GlobalRef<x10.lang.Cell<$T>> t53084 =
              ((x10.core.GlobalRef)(root));
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
final x10.lang.Place t53087 =
              ((x10.lang.Place)((t53084).home));
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
x10.lang.Runtime.runAt(((x10.lang.Place)(t53087)),
                                                                                                                            ((x10.core.fun.VoidFun_0_0)(new x10.lang.GlobalCell.$Closure$129<$T>($T, ((x10.lang.GlobalCell<$T>)(this)),
                                                                                                                                                                                                 root,
                                                                                                                                                                                                 x, (x10.lang.GlobalCell.$Closure$129.__0$1x10$lang$GlobalCell$$Closure$129$$T$2__1$1x10$lang$Cell$1x10$lang$GlobalCell$$Closure$129$$T$2$2__2x10$lang$GlobalCell$$Closure$129$$T) null))));
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
return x;
        }
        
        
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
public static <$T>x10.lang.GlobalCell<$T>
                                                                                                   make__0x10$lang$GlobalCell$$T(
                                                                                                   final x10.rtt.Type $T,
                                                                                                   final $T x){
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
final x10.lang.GlobalCell<$T> t53088 =
              ((x10.lang.GlobalCell)(new x10.lang.GlobalCell<$T>((java.lang.System[]) null, $T).$init((($T)(x)), (x10.lang.GlobalCell.__0x10$lang$GlobalCell$$T) null)));
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
return t53088;
        }
        
        
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
final public x10.lang.GlobalCell<$T>
                                                                                                   x10$lang$GlobalCell$$x10$lang$GlobalCell$this(
                                                                                                   ){
            
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
return x10.lang.GlobalCell.this;
        }
        
        @x10.core.X10Generated public static class $Closure$127<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$127.class);
            
            public static final x10.rtt.RuntimeType<$Closure$127> $RTT = x10.rtt.StaticFunType.<$Closure$127> make(
            /* base class */$Closure$127.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$127 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$127.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                x10.lang.GlobalCell out$$ = (x10.lang.GlobalCell) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.core.GlobalRef root = (x10.core.GlobalRef) $deserializer.readRef();
                $_obj.root = root;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$127 $_obj = new $Closure$127((java.lang.System[]) null, (x10.rtt.Type) null);
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
                if (root instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.root);
                } else {
                $serializer.write(this.root);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$127(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.lang.GlobalCell.$Closure$127.$initParams(this, $T);
            }
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$127 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public $T
                  $apply$G(
                  ){
                    
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
final x10.core.GlobalRef<x10.lang.Cell<$T>> t53071 =
                      ((x10.core.GlobalRef)(this.
                                              root));
                    
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
final x10.lang.Cell<$T> t53072 =
                      (((x10.core.GlobalRef<x10.lang.Cell<$T>>)(t53071))).$apply$G();
                    
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
final $T t53073 =
                      (($T)(((x10.lang.Cell<$T>)t53072).
                              value));
                    
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
return t53073;
                }
                
                public x10.lang.GlobalCell<$T> out$$;
                public x10.core.GlobalRef<x10.lang.Cell<$T>> root;
                
                public $Closure$127(final x10.rtt.Type $T,
                                    final x10.lang.GlobalCell<$T> out$$,
                                    final x10.core.GlobalRef<x10.lang.Cell<$T>> root, __0$1x10$lang$GlobalCell$$Closure$127$$T$2__1$1x10$lang$Cell$1x10$lang$GlobalCell$$Closure$127$$T$2$2 $dummy) {x10.lang.GlobalCell.$Closure$127.$initParams(this, $T);
                                                                                                                                                                                                          {
                                                                                                                                                                                                             this.out$$ = out$$;
                                                                                                                                                                                                             this.root = ((x10.core.GlobalRef)(root));
                                                                                                                                                                                                         }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$lang$GlobalCell$$Closure$127$$T$2__1$1x10$lang$Cell$1x10$lang$GlobalCell$$Closure$127$$T$2$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$128<$T> extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$128.class);
            
            public static final x10.rtt.RuntimeType<$Closure$128> $RTT = x10.rtt.StaticVoidFunType.<$Closure$128> make(
            /* base class */$Closure$128.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$128 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$128.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                x10.lang.GlobalCell out$$ = (x10.lang.GlobalCell) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.core.GlobalRef root = (x10.core.GlobalRef) $deserializer.readRef();
                $_obj.root = root;
                $_obj.x = $deserializer.readRef();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$128 $_obj = new $Closure$128((java.lang.System[]) null, (x10.rtt.Type) null);
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
                if (root instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.root);
                } else {
                $serializer.write(this.root);
                }
                if (x instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.x);
                } else {
                $serializer.write(this.x);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$128(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.lang.GlobalCell.$Closure$128.$initParams(this, $T);
            }
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$128 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public void
                  $apply(
                  ){
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
final x10.core.GlobalRef<x10.lang.Cell<$T>> t53081 =
                      ((x10.core.GlobalRef)(this.
                                              root));
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
final x10.lang.Cell<$T> t53082 =
                      (((x10.core.GlobalRef<x10.lang.Cell<$T>>)(t53081))).$apply$G();
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
t53082.value = (($T)(this.
                                                                                                                                    x));
                }
                
                public x10.lang.GlobalCell<$T> out$$;
                public x10.core.GlobalRef<x10.lang.Cell<$T>> root;
                public $T x;
                
                public $Closure$128(final x10.rtt.Type $T,
                                    final x10.lang.GlobalCell<$T> out$$,
                                    final x10.core.GlobalRef<x10.lang.Cell<$T>> root,
                                    final $T x, __0$1x10$lang$GlobalCell$$Closure$128$$T$2__1$1x10$lang$Cell$1x10$lang$GlobalCell$$Closure$128$$T$2$2__2x10$lang$GlobalCell$$Closure$128$$T $dummy) {x10.lang.GlobalCell.$Closure$128.$initParams(this, $T);
                                                                                                                                                                                                          {
                                                                                                                                                                                                             this.out$$ = out$$;
                                                                                                                                                                                                             this.root = ((x10.core.GlobalRef)(root));
                                                                                                                                                                                                             this.x = (($T)(x));
                                                                                                                                                                                                         }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$lang$GlobalCell$$Closure$128$$T$2__1$1x10$lang$Cell$1x10$lang$GlobalCell$$Closure$128$$T$2$2__2x10$lang$GlobalCell$$Closure$128$$T {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$129<$T> extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$129.class);
            
            public static final x10.rtt.RuntimeType<$Closure$129> $RTT = x10.rtt.StaticVoidFunType.<$Closure$129> make(
            /* base class */$Closure$129.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$129 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$129.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                x10.lang.GlobalCell out$$ = (x10.lang.GlobalCell) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.core.GlobalRef root = (x10.core.GlobalRef) $deserializer.readRef();
                $_obj.root = root;
                $_obj.x = $deserializer.readRef();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$129 $_obj = new $Closure$129((java.lang.System[]) null, (x10.rtt.Type) null);
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
                if (root instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.root);
                } else {
                $serializer.write(this.root);
                }
                if (x instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.x);
                } else {
                $serializer.write(this.x);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$129(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.lang.GlobalCell.$Closure$129.$initParams(this, $T);
            }
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$129 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public void
                  $apply(
                  ){
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
final x10.core.GlobalRef<x10.lang.Cell<$T>> t53085 =
                      ((x10.core.GlobalRef)(this.
                                              root));
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
final x10.lang.Cell<$T> t53086 =
                      (((x10.core.GlobalRef<x10.lang.Cell<$T>>)(t53085))).$apply$G();
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/GlobalCell.x10"
t53086.value = (($T)(this.
                                                                                                                                    x));
                }
                
                public x10.lang.GlobalCell<$T> out$$;
                public x10.core.GlobalRef<x10.lang.Cell<$T>> root;
                public $T x;
                
                public $Closure$129(final x10.rtt.Type $T,
                                    final x10.lang.GlobalCell<$T> out$$,
                                    final x10.core.GlobalRef<x10.lang.Cell<$T>> root,
                                    final $T x, __0$1x10$lang$GlobalCell$$Closure$129$$T$2__1$1x10$lang$Cell$1x10$lang$GlobalCell$$Closure$129$$T$2$2__2x10$lang$GlobalCell$$Closure$129$$T $dummy) {x10.lang.GlobalCell.$Closure$129.$initParams(this, $T);
                                                                                                                                                                                                          {
                                                                                                                                                                                                             this.out$$ = out$$;
                                                                                                                                                                                                             this.root = ((x10.core.GlobalRef)(root));
                                                                                                                                                                                                             this.x = (($T)(x));
                                                                                                                                                                                                         }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$lang$GlobalCell$$Closure$129$$T$2__1$1x10$lang$Cell$1x10$lang$GlobalCell$$Closure$129$$T$2$2__2x10$lang$GlobalCell$$Closure$129$$T {}
                
            }
            
        // synthetic type for parameter mangling
        public abstract static class __0x10$lang$GlobalCell$$T {}
        
        }
        