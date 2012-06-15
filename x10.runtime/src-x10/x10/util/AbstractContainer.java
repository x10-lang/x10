package x10.util;

@x10.core.X10Generated abstract public class AbstractContainer<$T> extends x10.core.Ref implements x10.util.Container, x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, AbstractContainer.class);
    
    public static final x10.rtt.RuntimeType<AbstractContainer> $RTT = x10.rtt.NamedType.<AbstractContainer> make(
    "x10.util.AbstractContainer", /* base class */AbstractContainer.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.util.Container.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(AbstractContainer $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + AbstractContainer.class + " calling"); } 
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
    
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
        
    }
    
    // constructor just for allocation
    public AbstractContainer(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
    super($dummy);
    x10.util.AbstractContainer.$initParams(this, $T);
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
        public static void $initParams(final AbstractContainer $this, final x10.rtt.Type $T) {
        $this.$T = $T;
        }
        
        
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractContainer.x10"
abstract public int
                                                                                                          size$O(
                                                                                                          );
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractContainer.x10"
public boolean
                                                                                                          isEmpty$O(
                                                                                                          ){
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractContainer.x10"
final int t56886 =
              this.size$O();
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractContainer.x10"
final boolean t56887 =
              ((int) t56886) ==
            ((int) 0);
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractContainer.x10"
return t56887;
        }
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractContainer.x10"
abstract public boolean
                                                                                                          contains__0x10$util$AbstractContainer$$T$O(
                                                                                                          final $T y);
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractContainer.x10"
abstract public x10.util.Container<$T>
                                                                                                          clone(
                                                                                                          );
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractContainer.x10"
abstract public x10.lang.Iterator<$T>
                                                                                                          iterator(
                                                                                                          );
        
        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractContainer.x10"
public boolean
                                                                                                          containsAll__0$1x10$util$AbstractContainer$$T$2$O(
                                                                                                          final x10.util.Container c){
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractContainer.x10"
final x10.lang.Iterator<$T> x56895 =
              ((x10.lang.Iterator<$T>)
                ((x10.lang.Iterable<$T>)c).iterator());
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractContainer.x10"
for (;
                                                                                                                 true;
                                                                                                                 ) {
                
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractContainer.x10"
final boolean t56896 =
                  ((x10.lang.Iterator<$T>)x56895).hasNext$O();
                
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractContainer.x10"
if (!(t56896)) {
                    
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractContainer.x10"
break;
                }
                
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractContainer.x10"
final $T x56892 =
                  (($T)(((x10.lang.Iterator<$T>)x56895).next$G()));
                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractContainer.x10"
final boolean t56893 =
                  this.contains__0x10$util$AbstractContainer$$T$O((($T)(x56892)));
                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractContainer.x10"
final boolean t56894 =
                  !(t56893);
                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractContainer.x10"
if (t56894) {
                    
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractContainer.x10"
return false;
                }
            }
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractContainer.x10"
return true;
        }
        
        
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractContainer.x10"
final public x10.util.AbstractContainer<$T>
                                                                                                          x10$util$AbstractContainer$$x10$util$AbstractContainer$this(
                                                                                                          ){
            
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractContainer.x10"
return x10.util.AbstractContainer.this;
        }
        
        
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractContainer.x10"

        // constructor for non-virtual call
        final public x10.util.AbstractContainer<$T> x10$util$AbstractContainer$$init$S() { {
                                                                                                  
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractContainer.x10"

                                                                                                  
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/AbstractContainer.x10"

                                                                                              }
                                                                                              return this;
                                                                                              }
        
        // constructor
        public x10.util.AbstractContainer<$T> $init(){return x10$util$AbstractContainer$$init$S();}
        
    
}
