package x10.lang;

@x10.core.X10Generated public class StringHelper extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, StringHelper.class);
    
    public static final x10.rtt.RuntimeType<StringHelper> $RTT = x10.rtt.NamedType.<StringHelper> make(
    "x10.lang.StringHelper", /* base class */StringHelper.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(StringHelper $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + StringHelper.class + " calling"); } 
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        StringHelper $_obj = new StringHelper((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        
    }
    
    // constructor just for allocation
    public StringHelper(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 555 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
public static x10.array.Array<java.lang.String>
                                                                                                split(
                                                                                                final java.lang.String delim,
                                                                                                final java.lang.String str){
            
//#line 556 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
final boolean t56602 =
              (delim).equals("");
            
//#line 556 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
if (t56602) {
                
//#line 557 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
final int t56599 =
                  (str).length();
                
//#line 557 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,java.lang.String> t56600 =
                  ((x10.core.fun.Fun_0_1)(new x10.lang.StringHelper.$Closure$157(str)));
                
//#line 557 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
final x10.array.Array<java.lang.String> t56601 =
                  ((x10.array.Array)(new x10.array.Array<java.lang.String>((java.lang.System[]) null, x10.rtt.Types.STRING).$init(t56599,
                                                                                                                                  ((x10.core.fun.Fun_0_1)(t56600)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                
//#line 557 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
return t56601;
            }
            
//#line 559 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
final x10.util.ArrayList<java.lang.String> ans =
              ((x10.util.ArrayList)(new x10.util.ArrayList<java.lang.String>((java.lang.System[]) null, x10.rtt.Types.STRING).$init()));
            
//#line 560 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
int pos =
              0;
            
//#line 561 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
final int t56603 =
              pos;
            
//#line 561 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
int nextMatch =
              (str).indexOf(delim, ((int)(t56603)));
            
//#line 562 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
while (true) {
                
//#line 562 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
final int t56604 =
                  nextMatch;
                
//#line 562 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
final boolean t56613 =
                  ((int) t56604) !=
                ((int) -1);
                
//#line 562 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
if (!(t56613)) {
                    
//#line 562 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
break;
                }
                
//#line 563 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
final int t56621 =
                  pos;
                
//#line 563 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
final int t56622 =
                  nextMatch;
                
//#line 563 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
final java.lang.String t56623 =
                  (str).substring(((int)(t56621)), ((int)(t56622)));
                
//#line 563 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
((x10.util.ArrayList<java.lang.String>)ans).add__0x10$util$ArrayList$$T$O(((java.lang.String)(t56623)));
                
//#line 564 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
final int t56624 =
                  nextMatch;
                
//#line 564 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
final int t56625 =
                  (delim).length();
                
//#line 564 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
final int t56626 =
                  ((t56624) + (((int)(t56625))));
                
//#line 564 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
pos = t56626;
                
//#line 565 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
final int t56627 =
                  pos;
                
//#line 565 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
final int t56628 =
                  (str).indexOf(delim, ((int)(t56627)));
                
//#line 565 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
nextMatch = t56628;
            }
            
//#line 567 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
final int t56614 =
              pos;
            
//#line 567 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
final int t56615 =
              (str).length();
            
//#line 567 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
final boolean t56619 =
              ((t56614) < (((int)(t56615))));
            
//#line 567 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
if (t56619) {
                
//#line 568 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
final int t56616 =
                  pos;
                
//#line 568 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
final int t56617 =
                  (str).length();
                
//#line 568 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
final java.lang.String t56618 =
                  (str).substring(((int)(t56616)), ((int)(t56617)));
                
//#line 568 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
((x10.util.ArrayList<java.lang.String>)ans).add__0x10$util$ArrayList$$T$O(((java.lang.String)(t56618)));
            }
            
//#line 570 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
final x10.array.Array<java.lang.String> t56620 =
              ((x10.array.Array)(((x10.array.Array<java.lang.String>)
                                   ((x10.util.ArrayList<java.lang.String>)ans).toArray())));
            
//#line 570 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
return t56620;
        }
        
        
//#line 554 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
final public x10.lang.StringHelper
                                                                                                x10$lang$StringHelper$$x10$lang$StringHelper$this(
                                                                                                ){
            
//#line 554 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
return x10.lang.StringHelper.this;
        }
        
        
//#line 554 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
// creation method for java code (1-phase java constructor)
        public StringHelper(){this((java.lang.System[]) null);
                                  $init();}
        
        // constructor for non-virtual call
        final public x10.lang.StringHelper x10$lang$StringHelper$$init$S() { {
                                                                                    
//#line 554 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"

                                                                                    
//#line 554 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"

                                                                                }
                                                                                return this;
                                                                                }
        
        // constructor
        public x10.lang.StringHelper $init(){return x10$lang$StringHelper$$init$S();}
        
        
        @x10.core.X10Generated public static class $Closure$157 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$157.class);
            
            public static final x10.rtt.RuntimeType<$Closure$157> $RTT = x10.rtt.StaticFunType.<$Closure$157> make(
            /* base class */$Closure$157.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.STRING), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$157 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$157.class + " calling"); } 
                java.lang.String str = (java.lang.String) $deserializer.readRef();
                $_obj.str = str;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$157 $_obj = new $Closure$157((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write(this.str);
                
            }
            
            // constructor just for allocation
            public $Closure$157(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return $apply$O(x10.core.Int.$unbox(a1));
            }
            
                
                public java.lang.String
                  $apply$O(
                  final int i){
                    
//#line 557 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
final int t56597 =
                      ((i) + (((int)(1))));
                    
//#line 557 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
final java.lang.String t56598 =
                      (this.
                         str).substring(((int)(i)), ((int)(t56597)));
                    
//#line 557 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/String.x10"
return t56598;
                }
                
                public java.lang.String str;
                
                public $Closure$157(final java.lang.String str) { {
                                                                         this.str = ((java.lang.String)(str));
                                                                     }}
                
            }
            
        
    }
    