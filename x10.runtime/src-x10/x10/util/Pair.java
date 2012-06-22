package x10.util;

@x10.core.X10Generated public class Pair<$T, $U> extends x10.core.Struct implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Pair.class);
    
    public static final x10.rtt.RuntimeType<Pair> $RTT = x10.rtt.NamedType.<Pair> make(
    "x10.util.Pair", /* base class */Pair.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.STRUCT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;if (i ==1)return $U;return null;}
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Pair $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Pair.class + " calling"); } 
        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
        $_obj.$U = ( x10.rtt.Type ) $deserializer.readRef();
        $_obj.first = $deserializer.readRef();
        $_obj.second = $deserializer.readRef();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Pair $_obj = new Pair((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$U);
        if (first instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.first);
        } else {
        $serializer.write(this.first);
        }
        if (second instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.second);
        } else {
        $serializer.write(this.second);
        }
        
    }
    
    // zero value constructor
    public Pair(final x10.rtt.Type $T, final x10.rtt.Type $U, final java.lang.System $dummy) { this.$T = $T; this.$U = $U; this.first = ($T) x10.rtt.Types.zeroValue($T); this.second = ($U) x10.rtt.Types.zeroValue($U); }
    // constructor just for allocation
    public Pair(final java.lang.System[] $dummy, final x10.rtt.Type $T, final x10.rtt.Type $U) { 
    super($dummy);
    x10.util.Pair.$initParams(this, $T, $U);
    }
    
        private x10.rtt.Type $T;
        private x10.rtt.Type $U;
        // initializer of type parameters
        public static void $initParams(final Pair $this, final x10.rtt.Type $T, final x10.rtt.Type $U) {
        $this.$T = $T;
        $this.$U = $U;
        }
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
public $T first;
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
public $U second;
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
// creation method for java code (1-phase java constructor)
        public Pair(final x10.rtt.Type $T,
                    final x10.rtt.Type $U,
                    final $T first,
                    final $U second, __0x10$util$Pair$$T__1x10$util$Pair$$U $dummy){this((java.lang.System[]) null, $T, $U);
                                                                                        $init(first,second, (x10.util.Pair.__0x10$util$Pair$$T__1x10$util$Pair$$U) null);}
        
        // constructor for non-virtual call
        final public x10.util.Pair<$T, $U> x10$util$Pair$$init$S(final $T first,
                                                                 final $U second, __0x10$util$Pair$$T__1x10$util$Pair$$U $dummy) { {
                                                                                                                                          
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
;
                                                                                                                                          
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"

                                                                                                                                          
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
this.first = (($T)(first));
                                                                                                                                          
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
this.second = (($U)(second));
                                                                                                                                      }
                                                                                                                                      return this;
                                                                                                                                      }
        
        // constructor
        public x10.util.Pair<$T, $U> $init(final $T first,
                                           final $U second, __0x10$util$Pair$$T__1x10$util$Pair$$U $dummy){return x10$util$Pair$$init$S(first,second, $dummy);}
        
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final public java.lang.String
                                                                                             toString(
                                                                                             ){
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final $T t62171 =
              (($T)(first));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final java.lang.String t62172 =
              (("(") + (t62171));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final java.lang.String t62173 =
              ((t62172) + (", "));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final $U t62174 =
              (($U)(second));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final java.lang.String t62175 =
              ((t62173) + (t62174));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final java.lang.String t62176 =
              ((t62175) + (")"));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
return t62176;
        }
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final public java.lang.String
                                                                                             typeName(
                                                                                             ){try {return x10.rtt.Types.typeName(this);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final public int
                                                                                             hashCode(
                                                                                             ){
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
int result =
              1;
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final int t62177 =
              result;
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final int t62179 =
              ((8191) * (((int)(t62177))));
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final $T t62178 =
              (($T)(this.
                      first));
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final int t62180 =
              x10.rtt.Types.hashCode(((java.lang.Object)(t62178)));
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final int t62181 =
              ((t62179) + (((int)(t62180))));
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
result = t62181;
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final int t62182 =
              result;
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final int t62184 =
              ((8191) * (((int)(t62182))));
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final $U t62183 =
              (($U)(this.
                      second));
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final int t62185 =
              x10.rtt.Types.hashCode(((java.lang.Object)(t62183)));
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final int t62186 =
              ((t62184) + (((int)(t62185))));
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
result = t62186;
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final int t62187 =
              result;
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
return t62187;
        }
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final public boolean
                                                                                             equals(
                                                                                             java.lang.Object other){
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final java.lang.Object t62188 =
              other;
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final boolean t62189 =
              x10.util.Pair.$RTT.isInstance(t62188, $T, $U);
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final boolean t62190 =
              !(t62189);
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
if (t62190) {
                
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
return false;
            }
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final java.lang.Object t62191 =
              other;
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final x10.util.Pair<$T, $U> t62192 =
              ((x10.util.Pair)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.util.Pair.$RTT, $T, $U),t62191));
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final boolean t62193 =
              this.equals__0$1x10$util$Pair$$T$3x10$util$Pair$$U$2$O(((x10.util.Pair)(t62192)));
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
return t62193;
        }
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final public boolean
                                                                                             equals__0$1x10$util$Pair$$T$3x10$util$Pair$$U$2$O(
                                                                                             x10.util.Pair other){
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final $T t62195 =
              (($T)(this.
                      first));
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final x10.util.Pair<$T, $U> t62194 =
              other;
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final $T t62196 =
              (($T)(((x10.util.Pair<$T, $U>)t62194).
                      first));
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
boolean t62200 =
              x10.rtt.Equality.equalsequals((t62195),(t62196));
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
if (t62200) {
                
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final $U t62198 =
                  (($U)(this.
                          second));
                
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final x10.util.Pair<$T, $U> t62197 =
                  other;
                
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final $U t62199 =
                  (($U)(((x10.util.Pair<$T, $U>)t62197).
                          second));
                
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
t62200 = x10.rtt.Equality.equalsequals((t62198),(t62199));
            }
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final boolean t62201 =
              t62200;
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
return t62201;
        }
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final public boolean
                                                                                             _struct_equals$O(
                                                                                             java.lang.Object other){
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final java.lang.Object t62202 =
              other;
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final boolean t62203 =
              x10.util.Pair.$RTT.isInstance(t62202, $T, $U);
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final boolean t62204 =
              !(t62203);
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
if (t62204) {
                
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
return false;
            }
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final java.lang.Object t62205 =
              other;
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final x10.util.Pair<$T, $U> t62206 =
              ((x10.util.Pair)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.util.Pair.$RTT, $T, $U),t62205));
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final boolean t62207 =
              this._struct_equals__0$1x10$util$Pair$$T$3x10$util$Pair$$U$2$O(((x10.util.Pair)(t62206)));
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
return t62207;
        }
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final public boolean
                                                                                             _struct_equals__0$1x10$util$Pair$$T$3x10$util$Pair$$U$2$O(
                                                                                             x10.util.Pair other){
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final $T t62209 =
              (($T)(this.
                      first));
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final x10.util.Pair<$T, $U> t62208 =
              other;
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final $T t62210 =
              (($T)(((x10.util.Pair<$T, $U>)t62208).
                      first));
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
boolean t62214 =
              x10.rtt.Equality.equalsequals((t62209),(t62210));
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
if (t62214) {
                
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final $U t62212 =
                  (($U)(this.
                          second));
                
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final x10.util.Pair<$T, $U> t62211 =
                  other;
                
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final $U t62213 =
                  (($U)(((x10.util.Pair<$T, $U>)t62211).
                          second));
                
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
t62214 = x10.rtt.Equality.equalsequals((t62212),(t62213));
            }
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final boolean t62215 =
              t62214;
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
return t62215;
        }
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
final public x10.util.Pair<$T, $U>
                                                                                             x10$util$Pair$$x10$util$Pair$this(
                                                                                             ){
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Pair.x10"
return x10.util.Pair.this;
        }
    // synthetic type for parameter mangling
    public abstract static class __0x10$util$Pair$$T__1x10$util$Pair$$U {}
    
}
