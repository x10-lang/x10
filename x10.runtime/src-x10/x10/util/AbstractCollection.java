package x10.util;

@x10.core.X10Generated abstract public class AbstractCollection<$T> extends x10.util.AbstractContainer<$T> implements x10.util.Collection, x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, AbstractCollection.class);
    
    public static final x10.rtt.RuntimeType<AbstractCollection> $RTT = x10.rtt.NamedType.<AbstractCollection> make(
    "x10.util.AbstractCollection", /* base class */AbstractCollection.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.util.Collection.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.ParameterizedType.make(x10.util.AbstractContainer.$RTT, x10.rtt.UnresolvedType.PARAM(0))}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(AbstractCollection $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + AbstractCollection.class + " calling"); } 
        x10.util.AbstractContainer.$_deserialize_body($_obj, $deserializer);
        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
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
        
    }
    
    // constructor just for allocation
    public AbstractCollection(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
    super($dummy, $T);
    x10.util.AbstractCollection.$initParams(this, $T);
    }
    // dispatcher for method abstract public x10.util.Collection.add(T):x10.lang.Boolean
    public java.lang.Object add(final java.lang.Object a1, final x10.rtt.Type t1) {
    return x10.core.Boolean.$box(add__0x10$util$AbstractCollection$$T$O(($T)a1));
    }
    // dispatcher for method abstract public x10.util.Collection.remove(T):x10.lang.Boolean
    public java.lang.Object remove(final java.lang.Object a1, final x10.rtt.Type t1) {
    return x10.core.Boolean.$box(remove__0x10$util$AbstractCollection$$T$O(($T)a1));
    }
    // dispatcher for method abstract public x10.util.Collection.addAll(x10.util.Container[T]):x10.lang.Boolean
    public java.lang.Object addAll(final x10.util.Container a1, final x10.rtt.Type t1) {
    return x10.core.Boolean.$box(addAll__0$1x10$util$AbstractCollection$$T$2$O((x10.util.Container)a1));
    }
    // dispatcher for method abstract public x10.util.Collection.retainAll(x10.util.Container[T]):x10.lang.Boolean
    public java.lang.Object retainAll(final x10.util.Container a1, final x10.rtt.Type t1) {
    return x10.core.Boolean.$box(retainAll__0$1x10$util$AbstractCollection$$T$2$O((x10.util.Container)a1));
    }
    // dispatcher for method abstract public x10.util.Collection.removeAll(x10.util.Container[T]):x10.lang.Boolean
    public java.lang.Object removeAll(final x10.util.Container a1, final x10.rtt.Type t1) {
    return x10.core.Boolean.$box(removeAll__0$1x10$util$AbstractCollection$$T$2$O((x10.util.Container)a1));
    }
    // dispatcher for method abstract public x10.util.Collection.addAllWhere(x10.util.Container[T],(a1:T)=>x10.lang.Boolean):x10.lang.Boolean
    public java.lang.Object addAllWhere(final x10.util.Container a1, final x10.rtt.Type t1, final x10.core.fun.Fun_0_1 a2, final x10.rtt.Type t2) {
    return x10.core.Boolean.$box(addAllWhere__0$1x10$util$AbstractCollection$$T$2__1$1x10$util$AbstractCollection$$T$3x10$lang$Boolean$2$O((x10.util.Container)a1, (x10.core.fun.Fun_0_1)a2));
    }
    // dispatcher for method abstract public x10.util.Collection.removeAllWhere((a1:T)=>x10.lang.Boolean):x10.lang.Boolean
    public java.lang.Object removeAllWhere(final x10.core.fun.Fun_0_1 a1, final x10.rtt.Type t1) {
    return x10.core.Boolean.$box(removeAllWhere__0$1x10$util$AbstractCollection$$T$3x10$lang$Boolean$2$O((x10.core.fun.Fun_0_1)a1));
    }
    // dispatcher for method abstract public x10.util.Container.contains(T):x10.lang.Boolean
    public java.lang.Object contains(final java.lang.Object a1, final x10.rtt.Type t1) {
    return x10.core.Boolean.$box(contains__0x10$util$AbstractContainer$$T$O(($T)a1));
    }
    // dispatcher for method abstract public x10.util.Container.containsAll(x10.util.Container[T]):x10.lang.Boolean
    public java.lang.Object containsAll(final x10.util.Container a1, final x10.rtt.Type t1) {
    return x10.core.Boolean.$box(containsAll__0$1x10$util$AbstractContainer$$T$2$O((x10.util.Container)a1));
    }
    
        private x10.rtt.Type $T;
        // initializer of type parameters
        public static void $initParams(final AbstractCollection $this, final x10.rtt.Type $T) {
        $this.$T = $T;
        }
        
        
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
abstract public boolean
                                                                                                           add__0x10$util$AbstractCollection$$T$O(
                                                                                                           final $T id$128);
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
abstract public boolean
                                                                                                           remove__0x10$util$AbstractCollection$$T$O(
                                                                                                           final $T id$129);
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
public boolean
                                                                                                           addAll__0$1x10$util$AbstractCollection$$T$2$O(
                                                                                                           final x10.util.Container c){
            
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final x10.core.fun.Fun_0_1<$T,x10.core.Boolean> t56844 =
              ((x10.core.fun.Fun_0_1)(new x10.util.AbstractCollection.$Closure$158<$T>($T)));
            
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final boolean t56845 =
              this.addAllWhere__0$1x10$util$AbstractCollection$$T$2__1$1x10$util$AbstractCollection$$T$3x10$lang$Boolean$2$O(((x10.util.Container)(c)),
                                                                                                                             ((x10.core.fun.Fun_0_1)(t56844)));
            
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
return t56845;
        }
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
public boolean
                                                                                                           retainAll__0$1x10$util$AbstractCollection$$T$2$O(
                                                                                                           final x10.util.Container c){
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final x10.core.fun.Fun_0_1<$T,x10.core.Boolean> t56848 =
              ((x10.core.fun.Fun_0_1)(new x10.util.AbstractCollection.$Closure$159<$T>($T, c, (x10.util.AbstractCollection.$Closure$159.__0$1x10$util$AbstractCollection$$Closure$159$$T$2) null)));
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final boolean t56849 =
              this.removeAllWhere__0$1x10$util$AbstractCollection$$T$3x10$lang$Boolean$2$O(((x10.core.fun.Fun_0_1)(t56848)));
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
return t56849;
        }
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
public boolean
                                                                                                           removeAll__0$1x10$util$AbstractCollection$$T$2$O(
                                                                                                           final x10.util.Container c){
            
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final x10.core.fun.Fun_0_1<$T,x10.core.Boolean> t56851 =
              ((x10.core.fun.Fun_0_1)(new x10.util.AbstractCollection.$Closure$160<$T>($T, c, (x10.util.AbstractCollection.$Closure$160.__0$1x10$util$AbstractCollection$$Closure$160$$T$2) null)));
            
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final boolean t56852 =
              this.removeAllWhere__0$1x10$util$AbstractCollection$$T$3x10$lang$Boolean$2$O(((x10.core.fun.Fun_0_1)(t56851)));
            
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
return t56852;
        }
        
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
public boolean
                                                                                                           addAllWhere__0$1x10$util$AbstractCollection$$T$2__1$1x10$util$AbstractCollection$$T$3x10$lang$Boolean$2$O(
                                                                                                           final x10.util.Container c,
                                                                                                           final x10.core.fun.Fun_0_1 p){
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
boolean result =
              false;
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final x10.lang.Iterator<$T> x56874 =
              ((x10.lang.Iterator<$T>)
                ((x10.lang.Iterable<$T>)c).iterator());
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
for (;
                                                                                                                  true;
                                                                                                                  ) {
                
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final boolean t56875 =
                  ((x10.lang.Iterator<$T>)x56874).hasNext$O();
                
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
if (!(t56875)) {
                    
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
break;
                }
                
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final $T x56869 =
                  (($T)(((x10.lang.Iterator<$T>)x56874).next$G()));
                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final boolean t56870 =
                  x10.core.Boolean.$unbox(((x10.core.fun.Fun_0_1<$T,x10.core.Boolean>)p).$apply(x56869,$T));
                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
if (t56870) {
                    
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final boolean t56871 =
                      result;
                    
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final boolean t56872 =
                      this.add__0x10$util$AbstractCollection$$T$O((($T)(x56869)));
                    
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final boolean t56873 =
                      ((t56871) | (((boolean)(t56872))));
                    
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
result = t56873;
                }
            }
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final boolean t56859 =
              result;
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
return t56859;
        }
        
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
public boolean
                                                                                                           removeAllWhere__0$1x10$util$AbstractCollection$$T$3x10$lang$Boolean$2$O(
                                                                                                           final x10.core.fun.Fun_0_1 p){
            
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
boolean result =
              false;
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final x10.util.Collection<$T> t56881 =
              ((x10.util.Collection<$T>)
                this.clone());
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final x10.lang.Iterator<$T> x56882 =
              ((x10.lang.Iterator<$T>)
                ((x10.lang.Iterable<$T>)t56881).iterator());
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
for (;
                                                                                                                  true;
                                                                                                                  ) {
                
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final boolean t56883 =
                  ((x10.lang.Iterator<$T>)x56882).hasNext$O();
                
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
if (!(t56883)) {
                    
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
break;
                }
                
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final $T x56876 =
                  (($T)(((x10.lang.Iterator<$T>)x56882).next$G()));
                
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final boolean t56877 =
                  x10.core.Boolean.$unbox(((x10.core.fun.Fun_0_1<$T,x10.core.Boolean>)p).$apply(x56876,$T));
                
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
if (t56877) {
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final boolean t56878 =
                      result;
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final boolean t56879 =
                      this.remove__0x10$util$AbstractCollection$$T$O((($T)(x56876)));
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final boolean t56880 =
                      ((t56878) | (((boolean)(t56879))));
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
result = t56880;
                }
            }
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final boolean t56867 =
              result;
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
return t56867;
        }
        
        
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
public void
                                                                                                           clear(
                                                                                                           ){
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final x10.core.fun.Fun_0_1<$T,x10.core.Boolean> t56868 =
              ((x10.core.fun.Fun_0_1)(new x10.util.AbstractCollection.$Closure$161<$T>($T)));
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
this.removeAllWhere__0$1x10$util$AbstractCollection$$T$3x10$lang$Boolean$2$O(((x10.core.fun.Fun_0_1)(t56868)));
        }
        
        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
abstract public x10.util.Collection<$T>
                                                                                                           clone(
                                                                                                           );
        
        
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final public x10.util.AbstractCollection<$T>
                                                                                                           x10$util$AbstractCollection$$x10$util$AbstractCollection$this(
                                                                                                           ){
            
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
return x10.util.AbstractCollection.this;
        }
        
        
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"

        // constructor for non-virtual call
        final public x10.util.AbstractCollection<$T> x10$util$AbstractCollection$$init$S() { {
                                                                                                    
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
super.$init();
                                                                                                    
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"

                                                                                                }
                                                                                                return this;
                                                                                                }
        
        // constructor
        public x10.util.AbstractCollection<$T> $init(){return x10$util$AbstractCollection$$init$S();}
        
        
        @x10.core.X10Generated public static class $Closure$158<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$158.class);
            
            public static final x10.rtt.RuntimeType<$Closure$158> $RTT = x10.rtt.StaticFunType.<$Closure$158> make(
            /* base class */$Closure$158.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.UnresolvedType.PARAM(0), x10.rtt.Types.BOOLEAN), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$158 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$158.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$158 $_obj = new $Closure$158((java.lang.System[]) null, (x10.rtt.Type) null);
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
            public $Closure$158(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.util.AbstractCollection.$Closure$158.$initParams(this, $T);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Boolean.$box($apply__0x10$util$AbstractCollection$$Closure$158$$T$O(($T)a1));
            }
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$158 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public boolean
                  $apply__0x10$util$AbstractCollection$$Closure$158$$T$O(
                  final $T id$130){
                    
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
return true;
                }
                
                public $Closure$158(final x10.rtt.Type $T) {x10.util.AbstractCollection.$Closure$158.$initParams(this, $T);
                                                                 {
                                                                    
                                                                }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$159<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$159.class);
            
            public static final x10.rtt.RuntimeType<$Closure$159> $RTT = x10.rtt.StaticFunType.<$Closure$159> make(
            /* base class */$Closure$159.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.UnresolvedType.PARAM(0), x10.rtt.Types.BOOLEAN), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$159 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$159.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                x10.util.Container c = (x10.util.Container) $deserializer.readRef();
                $_obj.c = c;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$159 $_obj = new $Closure$159((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                if (c instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.c);
                } else {
                $serializer.write(this.c);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$159(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.util.AbstractCollection.$Closure$159.$initParams(this, $T);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Boolean.$box($apply__0x10$util$AbstractCollection$$Closure$159$$T$O(($T)a1));
            }
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$159 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public boolean
                  $apply__0x10$util$AbstractCollection$$Closure$159$$T$O(
                  final $T x){
                    
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final boolean t56846 =
                      x10.core.Boolean.$unbox(((x10.util.Container<$T>)this.
                                                                         c).contains((($T)(x)),$T));
                    
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final boolean t56847 =
                      !(t56846);
                    
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
return t56847;
                }
                
                public x10.util.Container<$T> c;
                
                public $Closure$159(final x10.rtt.Type $T,
                                    final x10.util.Container<$T> c, __0$1x10$util$AbstractCollection$$Closure$159$$T$2 $dummy) {x10.util.AbstractCollection.$Closure$159.$initParams(this, $T);
                                                                                                                                     {
                                                                                                                                        this.c = ((x10.util.Container)(c));
                                                                                                                                    }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$util$AbstractCollection$$Closure$159$$T$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$160<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$160.class);
            
            public static final x10.rtt.RuntimeType<$Closure$160> $RTT = x10.rtt.StaticFunType.<$Closure$160> make(
            /* base class */$Closure$160.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.UnresolvedType.PARAM(0), x10.rtt.Types.BOOLEAN), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$160 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$160.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                x10.util.Container c = (x10.util.Container) $deserializer.readRef();
                $_obj.c = c;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$160 $_obj = new $Closure$160((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                if (c instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.c);
                } else {
                $serializer.write(this.c);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$160(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.util.AbstractCollection.$Closure$160.$initParams(this, $T);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Boolean.$box($apply__0x10$util$AbstractCollection$$Closure$160$$T$O(($T)a1));
            }
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$160 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public boolean
                  $apply__0x10$util$AbstractCollection$$Closure$160$$T$O(
                  final $T x){
                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
final boolean t56850 =
                      x10.core.Boolean.$unbox(((x10.util.Container<$T>)this.
                                                                         c).contains((($T)(x)),$T));
                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
return t56850;
                }
                
                public x10.util.Container<$T> c;
                
                public $Closure$160(final x10.rtt.Type $T,
                                    final x10.util.Container<$T> c, __0$1x10$util$AbstractCollection$$Closure$160$$T$2 $dummy) {x10.util.AbstractCollection.$Closure$160.$initParams(this, $T);
                                                                                                                                     {
                                                                                                                                        this.c = ((x10.util.Container)(c));
                                                                                                                                    }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$util$AbstractCollection$$Closure$160$$T$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$161<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$161.class);
            
            public static final x10.rtt.RuntimeType<$Closure$161> $RTT = x10.rtt.StaticFunType.<$Closure$161> make(
            /* base class */$Closure$161.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.UnresolvedType.PARAM(0), x10.rtt.Types.BOOLEAN), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$161 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$161.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$161 $_obj = new $Closure$161((java.lang.System[]) null, (x10.rtt.Type) null);
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
            public $Closure$161(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.util.AbstractCollection.$Closure$161.$initParams(this, $T);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Boolean.$box($apply__0x10$util$AbstractCollection$$Closure$161$$T$O(($T)a1));
            }
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$161 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public boolean
                  $apply__0x10$util$AbstractCollection$$Closure$161$$T$O(
                  final $T id$133){
                    
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractCollection.x10"
return true;
                }
                
                public $Closure$161(final x10.rtt.Type $T) {x10.util.AbstractCollection.$Closure$161.$initParams(this, $T);
                                                                 {
                                                                    
                                                                }}
                
            }
            
        
        }
        