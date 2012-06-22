package x10.lang;


@x10.core.X10Generated public interface Reducible<$T> extends x10.core.Any
{
    public static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Reducible.class);
    
    public static final x10.rtt.RuntimeType<Reducible> $RTT = x10.rtt.NamedType.<Reducible> make(
    "x10.lang.Reducible", /* base class */Reducible.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {}
    );
    
        
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
$T
                                                                                                  zero$G(
                                                                                                  );
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
java.lang.Object
                                                                                                  $apply(
                                                                                                  final java.lang.Object id$122,x10.rtt.Type t1,
                                                                                                  final java.lang.Object id$123,x10.rtt.Type t2);
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
@x10.core.X10Generated public static class AndReducer extends x10.core.Struct implements x10.lang.Reducible, x10.x10rt.X10JavaSerializable
                                                                                                {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, AndReducer.class);
            
            public static final x10.rtt.RuntimeType<AndReducer> $RTT = x10.rtt.NamedType.<AndReducer> make(
            "x10.lang.Reducible.AndReducer", /* base class */AndReducer.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Reducible.$RTT, x10.rtt.Types.BOOLEAN), x10.rtt.Types.STRUCT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(AndReducer $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + AndReducer.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                AndReducer $_obj = new AndReducer((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // zero value constructor
            public AndReducer(final java.lang.System $dummy) { }
            // constructor just for allocation
            public AndReducer(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public x10.lang.Reducible.operator()(T,T):T
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1, final java.lang.Object a2, final x10.rtt.Type t2) {
            return x10.core.Boolean.$box($apply$O(x10.core.Boolean.$unbox(a1), x10.core.Boolean.$unbox(a2)));
            }
            // bridge for method abstract public x10.lang.Reducible.zero():T
            final public x10.core.Boolean
              zero$G(){return x10.core.Boolean.$box(zero$O());}
            
                
                
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public boolean
                                                                                                          zero$O(
                                                                                                          ){
                    
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return true;
                }
                
                
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public boolean
                                                                                                          $apply$O(
                                                                                                          final boolean a,
                                                                                                          final boolean b){
                    
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
boolean t55767 =
                      a;
                    
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
if (t55767) {
                        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
t55767 = b;
                    }
                    
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55768 =
                      t55767;
                    
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55768;
                }
                
                
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public java.lang.String
                                                                                                          typeName(
                                                                                                          ){try {return x10.rtt.Types.typeName(this);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
                
                
                
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public java.lang.String
                                                                                                          toString(
                                                                                                          ){
                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return "struct x10.lang.Reducible.AndReducer";
                }
                
                
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public int
                                                                                                          hashCode(
                                                                                                          ){
                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
int result =
                      1;
                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final int t55769 =
                      result;
                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55769;
                }
                
                
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public boolean
                                                                                                          equals(
                                                                                                          java.lang.Object other){
                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final java.lang.Object t55770 =
                      other;
                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55771 =
                      x10.lang.Reducible.AndReducer.$RTT.isInstance(t55770);
                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55772 =
                      !(t55771);
                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
if (t55772) {
                        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return false;
                    }
                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final java.lang.Object t55773 =
                      other;
                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final x10.lang.Reducible.AndReducer t55774 =
                      ((x10.lang.Reducible.AndReducer)x10.rtt.Types.asStruct(x10.lang.Reducible.AndReducer.$RTT,t55773));
                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55775 =
                      this.equals$O(((x10.lang.Reducible.AndReducer)(t55774)));
                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55775;
                }
                
                
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public boolean
                                                                                                          equals$O(
                                                                                                          x10.lang.Reducible.AndReducer other){
                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return true;
                }
                
                
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public boolean
                                                                                                          _struct_equals$O(
                                                                                                          java.lang.Object other){
                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final java.lang.Object t55776 =
                      other;
                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55777 =
                      x10.lang.Reducible.AndReducer.$RTT.isInstance(t55776);
                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55778 =
                      !(t55777);
                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
if (t55778) {
                        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return false;
                    }
                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final java.lang.Object t55779 =
                      other;
                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final x10.lang.Reducible.AndReducer t55780 =
                      ((x10.lang.Reducible.AndReducer)x10.rtt.Types.asStruct(x10.lang.Reducible.AndReducer.$RTT,t55779));
                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55781 =
                      this._struct_equals$O(((x10.lang.Reducible.AndReducer)(t55780)));
                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55781;
                }
                
                
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public boolean
                                                                                                          _struct_equals$O(
                                                                                                          x10.lang.Reducible.AndReducer other){
                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return true;
                }
                
                
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public x10.lang.Reducible.AndReducer
                                                                                                          x10$lang$Reducible$AndReducer$$x10$lang$Reducible$AndReducer$this(
                                                                                                          ){
                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return x10.lang.Reducible.AndReducer.this;
                }
                
                
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
// creation method for java code (1-phase java constructor)
                public AndReducer(){this((java.lang.System[]) null);
                                        $init();}
                
                // constructor for non-virtual call
                final public x10.lang.Reducible.AndReducer x10$lang$Reducible$AndReducer$$init$S() { {
                                                                                                            
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
;
                                                                                                            
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"

                                                                                                        }
                                                                                                        return this;
                                                                                                        }
                
                // constructor
                public x10.lang.Reducible.AndReducer $init(){return x10$lang$Reducible$AndReducer$$init$S();}
                
            
        }
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
@x10.core.X10Generated public static class OrReducer extends x10.core.Struct implements x10.lang.Reducible, x10.x10rt.X10JavaSerializable
                                                                                                {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, OrReducer.class);
            
            public static final x10.rtt.RuntimeType<OrReducer> $RTT = x10.rtt.NamedType.<OrReducer> make(
            "x10.lang.Reducible.OrReducer", /* base class */OrReducer.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Reducible.$RTT, x10.rtt.Types.BOOLEAN), x10.rtt.Types.STRUCT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(OrReducer $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + OrReducer.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                OrReducer $_obj = new OrReducer((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // zero value constructor
            public OrReducer(final java.lang.System $dummy) { }
            // constructor just for allocation
            public OrReducer(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public x10.lang.Reducible.operator()(T,T):T
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1, final java.lang.Object a2, final x10.rtt.Type t2) {
            return x10.core.Boolean.$box($apply$O(x10.core.Boolean.$unbox(a1), x10.core.Boolean.$unbox(a2)));
            }
            // bridge for method abstract public x10.lang.Reducible.zero():T
            final public x10.core.Boolean
              zero$G(){return x10.core.Boolean.$box(zero$O());}
            
                
                
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public boolean
                                                                                                          zero$O(
                                                                                                          ){
                    
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return false;
                }
                
                
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public boolean
                                                                                                          $apply$O(
                                                                                                          final boolean a,
                                                                                                          final boolean b){
                    
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
boolean t55782 =
                      a;
                    
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
if (!(t55782)) {
                        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
t55782 = b;
                    }
                    
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55783 =
                      t55782;
                    
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55783;
                }
                
                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public java.lang.String
                                                                                                          typeName(
                                                                                                          ){try {return x10.rtt.Types.typeName(this);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
                
                
                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public java.lang.String
                                                                                                          toString(
                                                                                                          ){
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return "struct x10.lang.Reducible.OrReducer";
                }
                
                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public int
                                                                                                          hashCode(
                                                                                                          ){
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
int result =
                      1;
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final int t55784 =
                      result;
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55784;
                }
                
                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public boolean
                                                                                                          equals(
                                                                                                          java.lang.Object other){
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final java.lang.Object t55785 =
                      other;
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55786 =
                      x10.lang.Reducible.OrReducer.$RTT.isInstance(t55785);
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55787 =
                      !(t55786);
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
if (t55787) {
                        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return false;
                    }
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final java.lang.Object t55788 =
                      other;
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final x10.lang.Reducible.OrReducer t55789 =
                      ((x10.lang.Reducible.OrReducer)x10.rtt.Types.asStruct(x10.lang.Reducible.OrReducer.$RTT,t55788));
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55790 =
                      this.equals$O(((x10.lang.Reducible.OrReducer)(t55789)));
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55790;
                }
                
                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public boolean
                                                                                                          equals$O(
                                                                                                          x10.lang.Reducible.OrReducer other){
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return true;
                }
                
                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public boolean
                                                                                                          _struct_equals$O(
                                                                                                          java.lang.Object other){
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final java.lang.Object t55791 =
                      other;
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55792 =
                      x10.lang.Reducible.OrReducer.$RTT.isInstance(t55791);
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55793 =
                      !(t55792);
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
if (t55793) {
                        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return false;
                    }
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final java.lang.Object t55794 =
                      other;
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final x10.lang.Reducible.OrReducer t55795 =
                      ((x10.lang.Reducible.OrReducer)x10.rtt.Types.asStruct(x10.lang.Reducible.OrReducer.$RTT,t55794));
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55796 =
                      this._struct_equals$O(((x10.lang.Reducible.OrReducer)(t55795)));
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55796;
                }
                
                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public boolean
                                                                                                          _struct_equals$O(
                                                                                                          x10.lang.Reducible.OrReducer other){
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return true;
                }
                
                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public x10.lang.Reducible.OrReducer
                                                                                                          x10$lang$Reducible$OrReducer$$x10$lang$Reducible$OrReducer$this(
                                                                                                          ){
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return x10.lang.Reducible.OrReducer.this;
                }
                
                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
// creation method for java code (1-phase java constructor)
                public OrReducer(){this((java.lang.System[]) null);
                                       $init();}
                
                // constructor for non-virtual call
                final public x10.lang.Reducible.OrReducer x10$lang$Reducible$OrReducer$$init$S() { {
                                                                                                          
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
;
                                                                                                          
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"

                                                                                                      }
                                                                                                      return this;
                                                                                                      }
                
                // constructor
                public x10.lang.Reducible.OrReducer $init(){return x10$lang$Reducible$OrReducer$$init$S();}
                
            
        }
        
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
@x10.core.X10Generated public static class SumReducer<$T55764> extends x10.core.Struct implements x10.lang.Reducible, x10.x10rt.X10JavaSerializable
                                                                                                {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, SumReducer.class);
            
            public static final x10.rtt.RuntimeType<SumReducer> $RTT = x10.rtt.NamedType.<SumReducer> make(
            "x10.lang.Reducible.SumReducer", /* base class */SumReducer.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Reducible.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.Types.STRUCT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T55764;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(SumReducer $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + SumReducer.class + " calling"); } 
                $_obj.$T55764 = ( x10.rtt.Type ) $deserializer.readRef();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                SumReducer $_obj = new SumReducer((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T55764);
                
            }
            
            // zero value constructor
            public SumReducer(final x10.rtt.Type $T55764, final java.lang.System $dummy) { this.$T55764 = $T55764; }
            // constructor just for allocation
            public SumReducer(final java.lang.System[] $dummy, final x10.rtt.Type $T55764) { 
            super($dummy);
            x10.lang.Reducible.SumReducer.$initParams(this, $T55764);
            }
            // dispatcher for method abstract public x10.lang.Reducible.operator()(T,T):T
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1, final java.lang.Object a2, final x10.rtt.Type t2) {
            return $apply__0x10$lang$Reducible$SumReducer$$T55764__1x10$lang$Reducible$SumReducer$$T55764$G(($T55764)a1, ($T55764)a2);
            }
            
                private x10.rtt.Type $T55764;
                // initializer of type parameters
                public static void $initParams(final SumReducer $this, final x10.rtt.Type $T55764) {
                $this.$T55764 = $T55764;
                }
                
                
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public $T55764
                                                                                                          zero$G(
                                                                                                          ){
                    
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final $T55764 t55797 =
                      (($T55764)(($T55764) x10.rtt.Types.zeroValue($T55764)));
                    
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55797;
                }
                
                
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public $T55764
                                                                                                          $apply__0x10$lang$Reducible$SumReducer$$T55764__1x10$lang$Reducible$SumReducer$$T55764$G(
                                                                                                          final $T55764 a,
                                                                                                          final $T55764 b){
                    
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final $T55764 t55798 =
                      (($T55764)((($T55764)
                                   ((x10.lang.Arithmetic<$T55764>)x10.rtt.Types.conversion(x10.rtt.ParameterizedType.make(x10.lang.Arithmetic.$RTT, $T55764),a)).$plus((($T55764)(b)),$T55764))));
                    
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55798;
                }
                
                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public java.lang.String
                                                                                                          typeName(
                                                                                                          ){try {return x10.rtt.Types.typeName(this);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
                
                
                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public java.lang.String
                                                                                                          toString(
                                                                                                          ){
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return "struct x10.lang.Reducible.SumReducer";
                }
                
                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public int
                                                                                                          hashCode(
                                                                                                          ){
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
int result =
                      1;
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final int t55799 =
                      result;
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55799;
                }
                
                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public boolean
                                                                                                          equals(
                                                                                                          java.lang.Object other){
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final java.lang.Object t55800 =
                      other;
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55801 =
                      x10.lang.Reducible.SumReducer.$RTT.isInstance(t55800, $T55764);
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55802 =
                      !(t55801);
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
if (t55802) {
                        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return false;
                    }
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final java.lang.Object t55803 =
                      other;
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final x10.lang.Reducible.SumReducer<$T55764> t55804 =
                      ((x10.lang.Reducible.SumReducer)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.lang.Reducible.SumReducer.$RTT, $T55764),t55803));
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55805 =
                      this.equals__0$1x10$lang$Reducible$SumReducer$$T55764$2$O(((x10.lang.Reducible.SumReducer)(t55804)));
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55805;
                }
                
                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public boolean
                                                                                                          equals__0$1x10$lang$Reducible$SumReducer$$T55764$2$O(
                                                                                                          x10.lang.Reducible.SumReducer other){
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return true;
                }
                
                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public boolean
                                                                                                          _struct_equals$O(
                                                                                                          java.lang.Object other){
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final java.lang.Object t55806 =
                      other;
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55807 =
                      x10.lang.Reducible.SumReducer.$RTT.isInstance(t55806, $T55764);
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55808 =
                      !(t55807);
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
if (t55808) {
                        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return false;
                    }
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final java.lang.Object t55809 =
                      other;
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final x10.lang.Reducible.SumReducer<$T55764> t55810 =
                      ((x10.lang.Reducible.SumReducer)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.lang.Reducible.SumReducer.$RTT, $T55764),t55809));
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55811 =
                      this._struct_equals__0$1x10$lang$Reducible$SumReducer$$T55764$2$O(((x10.lang.Reducible.SumReducer)(t55810)));
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55811;
                }
                
                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public boolean
                                                                                                          _struct_equals__0$1x10$lang$Reducible$SumReducer$$T55764$2$O(
                                                                                                          x10.lang.Reducible.SumReducer other){
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return true;
                }
                
                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public x10.lang.Reducible.SumReducer<$T55764>
                                                                                                          x10$lang$Reducible$SumReducer$$x10$lang$Reducible$SumReducer$this(
                                                                                                          ){
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return x10.lang.Reducible.SumReducer.this;
                }
                
                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
// creation method for java code (1-phase java constructor)
                public SumReducer(final x10.rtt.Type $T55764){this((java.lang.System[]) null, $T55764);
                                                                  $init();}
                
                // constructor for non-virtual call
                final public x10.lang.Reducible.SumReducer<$T55764> x10$lang$Reducible$SumReducer$$init$S() { {
                                                                                                                     
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
;
                                                                                                                     
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"

                                                                                                                 }
                                                                                                                 return this;
                                                                                                                 }
                
                // constructor
                public x10.lang.Reducible.SumReducer<$T55764> $init(){return x10$lang$Reducible$SumReducer$$init$S();}
                
            
        }
        
        
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
@x10.core.X10Generated public static class MinReducer<$T55765> extends x10.core.Struct implements x10.lang.Reducible, x10.x10rt.X10JavaSerializable
                                                                                                {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, MinReducer.class);
            
            public static final x10.rtt.RuntimeType<MinReducer> $RTT = x10.rtt.NamedType.<MinReducer> make(
            "x10.lang.Reducible.MinReducer", /* base class */MinReducer.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Reducible.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.Types.STRUCT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T55765;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(MinReducer $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + MinReducer.class + " calling"); } 
                $_obj.$T55765 = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.zeroVal = $deserializer.readRef();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                MinReducer $_obj = new MinReducer((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T55765);
                if (zeroVal instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.zeroVal);
                } else {
                $serializer.write(this.zeroVal);
                }
                
            }
            
            // zero value constructor
            public MinReducer(final x10.rtt.Type $T55765, final java.lang.System $dummy) { this.$T55765 = $T55765; this.zeroVal = ($T55765) x10.rtt.Types.zeroValue($T55765); }
            // constructor just for allocation
            public MinReducer(final java.lang.System[] $dummy, final x10.rtt.Type $T55765) { 
            super($dummy);
            x10.lang.Reducible.MinReducer.$initParams(this, $T55765);
            }
            // dispatcher for method abstract public x10.lang.Reducible.operator()(T,T):T
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1, final java.lang.Object a2, final x10.rtt.Type t2) {
            return $apply__0x10$lang$Reducible$MinReducer$$T55765__1x10$lang$Reducible$MinReducer$$T55765$G(($T55765)a1, ($T55765)a2);
            }
            
                private x10.rtt.Type $T55765;
                // initializer of type parameters
                public static void $initParams(final MinReducer $this, final x10.rtt.Type $T55765) {
                $this.$T55765 = $T55765;
                }
                
                
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
public $T55765 zeroVal;
                
                
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
// creation method for java code (1-phase java constructor)
                public MinReducer(final x10.rtt.Type $T55765,
                                  final $T55765 maxValue, __0x10$lang$Reducible$MinReducer$$T55765 $dummy){this((java.lang.System[]) null, $T55765);
                                                                                                               $init(maxValue, (x10.lang.Reducible.MinReducer.__0x10$lang$Reducible$MinReducer$$T55765) null);}
                
                // constructor for non-virtual call
                final public x10.lang.Reducible.MinReducer<$T55765> x10$lang$Reducible$MinReducer$$init$S(final $T55765 maxValue, __0x10$lang$Reducible$MinReducer$$T55765 $dummy) { {
                                                                                                                                                                                            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
;
                                                                                                                                                                                            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"

                                                                                                                                                                                            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
this.zeroVal = (($T55765)(maxValue));
                                                                                                                                                                                        }
                                                                                                                                                                                        return this;
                                                                                                                                                                                        }
                
                // constructor
                public x10.lang.Reducible.MinReducer<$T55765> $init(final $T55765 maxValue, __0x10$lang$Reducible$MinReducer$$T55765 $dummy){return x10$lang$Reducible$MinReducer$$init$S(maxValue, $dummy);}
                
                
                
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public $T55765
                                                                                                          zero$G(
                                                                                                          ){
                    
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final $T55765 t55812 =
                      (($T55765)(zeroVal));
                    
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55812;
                }
                
                
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public $T55765
                                                                                                          $apply__0x10$lang$Reducible$MinReducer$$T55765__1x10$lang$Reducible$MinReducer$$T55765$G(
                                                                                                          final $T55765 a,
                                                                                                          final $T55765 b){
                    
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55813 =
                      x10.core.Boolean.$unbox(((x10.util.Ordered<$T55765>)x10.rtt.Types.conversion(x10.rtt.ParameterizedType.make(x10.util.Ordered.$RTT, $T55765),a)).$lt((($T55765)(b)),$T55765));
                    
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
$T55765 t55814 =
                       null;
                    
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
if (t55813) {
                        
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
t55814 = (($T55765)(a));
                    } else {
                        
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
t55814 = (($T55765)(b));
                    }
                    
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final $T55765 t55815 =
                      (($T55765)(t55814));
                    
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55815;
                }
                
                
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public java.lang.String
                                                                                                          typeName(
                                                                                                          ){try {return x10.rtt.Types.typeName(this);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
                
                
                
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public java.lang.String
                                                                                                          toString(
                                                                                                          ){
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final java.lang.String t55816 =
                      "struct x10.lang.Reducible.MinReducer: zeroVal=";
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final $T55765 t55817 =
                      (($T55765)(this.
                                   zeroVal));
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final java.lang.String t55818 =
                      ((t55816) + (t55817));
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55818;
                }
                
                
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public int
                                                                                                          hashCode(
                                                                                                          ){
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
int result =
                      1;
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final int t55819 =
                      result;
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final int t55821 =
                      ((8191) * (((int)(t55819))));
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final $T55765 t55820 =
                      (($T55765)(this.
                                   zeroVal));
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final int t55822 =
                      x10.rtt.Types.hashCode(((java.lang.Object)(t55820)));
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final int t55823 =
                      ((t55821) + (((int)(t55822))));
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
result = t55823;
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final int t55824 =
                      result;
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55824;
                }
                
                
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public boolean
                                                                                                          equals(
                                                                                                          java.lang.Object other){
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final java.lang.Object t55825 =
                      other;
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55826 =
                      x10.lang.Reducible.MinReducer.$RTT.isInstance(t55825, $T55765);
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55827 =
                      !(t55826);
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
if (t55827) {
                        
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return false;
                    }
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final java.lang.Object t55828 =
                      other;
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final x10.lang.Reducible.MinReducer<$T55765> t55829 =
                      ((x10.lang.Reducible.MinReducer)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.lang.Reducible.MinReducer.$RTT, $T55765),t55828));
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55830 =
                      this.equals__0$1x10$lang$Reducible$MinReducer$$T55765$2$O(((x10.lang.Reducible.MinReducer)(t55829)));
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55830;
                }
                
                
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public boolean
                                                                                                          equals__0$1x10$lang$Reducible$MinReducer$$T55765$2$O(
                                                                                                          x10.lang.Reducible.MinReducer other){
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final $T55765 t55832 =
                      (($T55765)(this.
                                   zeroVal));
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final x10.lang.Reducible.MinReducer<$T55765> t55831 =
                      other;
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final $T55765 t55833 =
                      (($T55765)(((x10.lang.Reducible.MinReducer<$T55765>)t55831).
                                   zeroVal));
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55834 =
                      x10.rtt.Equality.equalsequals((t55832),(t55833));
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55834;
                }
                
                
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public boolean
                                                                                                          _struct_equals$O(
                                                                                                          java.lang.Object other){
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final java.lang.Object t55835 =
                      other;
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55836 =
                      x10.lang.Reducible.MinReducer.$RTT.isInstance(t55835, $T55765);
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55837 =
                      !(t55836);
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
if (t55837) {
                        
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return false;
                    }
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final java.lang.Object t55838 =
                      other;
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final x10.lang.Reducible.MinReducer<$T55765> t55839 =
                      ((x10.lang.Reducible.MinReducer)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.lang.Reducible.MinReducer.$RTT, $T55765),t55838));
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55840 =
                      this._struct_equals__0$1x10$lang$Reducible$MinReducer$$T55765$2$O(((x10.lang.Reducible.MinReducer)(t55839)));
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55840;
                }
                
                
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public boolean
                                                                                                          _struct_equals__0$1x10$lang$Reducible$MinReducer$$T55765$2$O(
                                                                                                          x10.lang.Reducible.MinReducer other){
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final $T55765 t55842 =
                      (($T55765)(this.
                                   zeroVal));
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final x10.lang.Reducible.MinReducer<$T55765> t55841 =
                      other;
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final $T55765 t55843 =
                      (($T55765)(((x10.lang.Reducible.MinReducer<$T55765>)t55841).
                                   zeroVal));
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55844 =
                      x10.rtt.Equality.equalsequals((t55842),(t55843));
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55844;
                }
                
                
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public x10.lang.Reducible.MinReducer<$T55765>
                                                                                                          x10$lang$Reducible$MinReducer$$x10$lang$Reducible$MinReducer$this(
                                                                                                          ){
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return x10.lang.Reducible.MinReducer.this;
                }
            // synthetic type for parameter mangling
            public abstract static class __0x10$lang$Reducible$MinReducer$$T55765 {}
            
        }
        
        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
@x10.core.X10Generated public static class MaxReducer<$T55766> extends x10.core.Struct implements x10.lang.Reducible, x10.x10rt.X10JavaSerializable
                                                                                                {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, MaxReducer.class);
            
            public static final x10.rtt.RuntimeType<MaxReducer> $RTT = x10.rtt.NamedType.<MaxReducer> make(
            "x10.lang.Reducible.MaxReducer", /* base class */MaxReducer.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Reducible.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.Types.STRUCT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T55766;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(MaxReducer $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + MaxReducer.class + " calling"); } 
                $_obj.$T55766 = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.zeroVal = $deserializer.readRef();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                MaxReducer $_obj = new MaxReducer((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T55766);
                if (zeroVal instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.zeroVal);
                } else {
                $serializer.write(this.zeroVal);
                }
                
            }
            
            // zero value constructor
            public MaxReducer(final x10.rtt.Type $T55766, final java.lang.System $dummy) { this.$T55766 = $T55766; this.zeroVal = ($T55766) x10.rtt.Types.zeroValue($T55766); }
            // constructor just for allocation
            public MaxReducer(final java.lang.System[] $dummy, final x10.rtt.Type $T55766) { 
            super($dummy);
            x10.lang.Reducible.MaxReducer.$initParams(this, $T55766);
            }
            // dispatcher for method abstract public x10.lang.Reducible.operator()(T,T):T
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1, final java.lang.Object a2, final x10.rtt.Type t2) {
            return $apply__0x10$lang$Reducible$MaxReducer$$T55766__1x10$lang$Reducible$MaxReducer$$T55766$G(($T55766)a1, ($T55766)a2);
            }
            
                private x10.rtt.Type $T55766;
                // initializer of type parameters
                public static void $initParams(final MaxReducer $this, final x10.rtt.Type $T55766) {
                $this.$T55766 = $T55766;
                }
                
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
public $T55766 zeroVal;
                
                
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
// creation method for java code (1-phase java constructor)
                public MaxReducer(final x10.rtt.Type $T55766,
                                  final $T55766 minValue, __0x10$lang$Reducible$MaxReducer$$T55766 $dummy){this((java.lang.System[]) null, $T55766);
                                                                                                               $init(minValue, (x10.lang.Reducible.MaxReducer.__0x10$lang$Reducible$MaxReducer$$T55766) null);}
                
                // constructor for non-virtual call
                final public x10.lang.Reducible.MaxReducer<$T55766> x10$lang$Reducible$MaxReducer$$init$S(final $T55766 minValue, __0x10$lang$Reducible$MaxReducer$$T55766 $dummy) { {
                                                                                                                                                                                            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
;
                                                                                                                                                                                            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"

                                                                                                                                                                                            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
this.zeroVal = (($T55766)(minValue));
                                                                                                                                                                                        }
                                                                                                                                                                                        return this;
                                                                                                                                                                                        }
                
                // constructor
                public x10.lang.Reducible.MaxReducer<$T55766> $init(final $T55766 minValue, __0x10$lang$Reducible$MaxReducer$$T55766 $dummy){return x10$lang$Reducible$MaxReducer$$init$S(minValue, $dummy);}
                
                
                
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public $T55766
                                                                                                          zero$G(
                                                                                                          ){
                    
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final $T55766 t55845 =
                      (($T55766)(zeroVal));
                    
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55845;
                }
                
                
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public $T55766
                                                                                                          $apply__0x10$lang$Reducible$MaxReducer$$T55766__1x10$lang$Reducible$MaxReducer$$T55766$G(
                                                                                                          final $T55766 a,
                                                                                                          final $T55766 b){
                    
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55846 =
                      x10.core.Boolean.$unbox(((x10.util.Ordered<$T55766>)x10.rtt.Types.conversion(x10.rtt.ParameterizedType.make(x10.util.Ordered.$RTT, $T55766),a)).$ge((($T55766)(b)),$T55766));
                    
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
$T55766 t55847 =
                       null;
                    
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
if (t55846) {
                        
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
t55847 = (($T55766)(a));
                    } else {
                        
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
t55847 = (($T55766)(b));
                    }
                    
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final $T55766 t55848 =
                      (($T55766)(t55847));
                    
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55848;
                }
                
                
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public java.lang.String
                                                                                                          typeName(
                                                                                                          ){try {return x10.rtt.Types.typeName(this);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
                
                
                
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public java.lang.String
                                                                                                          toString(
                                                                                                          ){
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final java.lang.String t55849 =
                      "struct x10.lang.Reducible.MaxReducer: zeroVal=";
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final $T55766 t55850 =
                      (($T55766)(this.
                                   zeroVal));
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final java.lang.String t55851 =
                      ((t55849) + (t55850));
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55851;
                }
                
                
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public int
                                                                                                          hashCode(
                                                                                                          ){
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
int result =
                      1;
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final int t55852 =
                      result;
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final int t55854 =
                      ((8191) * (((int)(t55852))));
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final $T55766 t55853 =
                      (($T55766)(this.
                                   zeroVal));
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final int t55855 =
                      x10.rtt.Types.hashCode(((java.lang.Object)(t55853)));
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final int t55856 =
                      ((t55854) + (((int)(t55855))));
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
result = t55856;
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final int t55857 =
                      result;
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55857;
                }
                
                
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public boolean
                                                                                                          equals(
                                                                                                          java.lang.Object other){
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final java.lang.Object t55858 =
                      other;
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55859 =
                      x10.lang.Reducible.MaxReducer.$RTT.isInstance(t55858, $T55766);
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55860 =
                      !(t55859);
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
if (t55860) {
                        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return false;
                    }
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final java.lang.Object t55861 =
                      other;
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final x10.lang.Reducible.MaxReducer<$T55766> t55862 =
                      ((x10.lang.Reducible.MaxReducer)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.lang.Reducible.MaxReducer.$RTT, $T55766),t55861));
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55863 =
                      this.equals__0$1x10$lang$Reducible$MaxReducer$$T55766$2$O(((x10.lang.Reducible.MaxReducer)(t55862)));
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55863;
                }
                
                
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public boolean
                                                                                                          equals__0$1x10$lang$Reducible$MaxReducer$$T55766$2$O(
                                                                                                          x10.lang.Reducible.MaxReducer other){
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final $T55766 t55865 =
                      (($T55766)(this.
                                   zeroVal));
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final x10.lang.Reducible.MaxReducer<$T55766> t55864 =
                      other;
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final $T55766 t55866 =
                      (($T55766)(((x10.lang.Reducible.MaxReducer<$T55766>)t55864).
                                   zeroVal));
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55867 =
                      x10.rtt.Equality.equalsequals((t55865),(t55866));
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55867;
                }
                
                
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public boolean
                                                                                                          _struct_equals$O(
                                                                                                          java.lang.Object other){
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final java.lang.Object t55868 =
                      other;
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55869 =
                      x10.lang.Reducible.MaxReducer.$RTT.isInstance(t55868, $T55766);
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55870 =
                      !(t55869);
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
if (t55870) {
                        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return false;
                    }
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final java.lang.Object t55871 =
                      other;
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final x10.lang.Reducible.MaxReducer<$T55766> t55872 =
                      ((x10.lang.Reducible.MaxReducer)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.lang.Reducible.MaxReducer.$RTT, $T55766),t55871));
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55873 =
                      this._struct_equals__0$1x10$lang$Reducible$MaxReducer$$T55766$2$O(((x10.lang.Reducible.MaxReducer)(t55872)));
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55873;
                }
                
                
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public boolean
                                                                                                          _struct_equals__0$1x10$lang$Reducible$MaxReducer$$T55766$2$O(
                                                                                                          x10.lang.Reducible.MaxReducer other){
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final $T55766 t55875 =
                      (($T55766)(this.
                                   zeroVal));
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final x10.lang.Reducible.MaxReducer<$T55766> t55874 =
                      other;
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final $T55766 t55876 =
                      (($T55766)(((x10.lang.Reducible.MaxReducer<$T55766>)t55874).
                                   zeroVal));
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final boolean t55877 =
                      x10.rtt.Equality.equalsequals((t55875),(t55876));
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return t55877;
                }
                
                
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
final public x10.lang.Reducible.MaxReducer<$T55766>
                                                                                                          x10$lang$Reducible$MaxReducer$$x10$lang$Reducible$MaxReducer$this(
                                                                                                          ){
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Reducible.x10"
return x10.lang.Reducible.MaxReducer.this;
                }
            // synthetic type for parameter mangling
            public abstract static class __0x10$lang$Reducible$MaxReducer$$T55766 {}
            
        }
        
    
}
