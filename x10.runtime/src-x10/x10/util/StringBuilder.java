package x10.util;


@x10.core.X10Generated public class StringBuilder extends x10.core.Ref implements x10.util.Builder, x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, StringBuilder.class);
    
    public static final x10.rtt.RuntimeType<StringBuilder> $RTT = x10.rtt.NamedType.<StringBuilder> make(
    "x10.util.StringBuilder", /* base class */StringBuilder.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.util.Builder.$RTT, x10.rtt.Types.ANY, x10.rtt.Types.STRING), x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(StringBuilder $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + StringBuilder.class + " calling"); } 
        x10.util.ArrayList buf = (x10.util.ArrayList) $deserializer.readRef();
        $_obj.buf = buf;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        StringBuilder $_obj = new StringBuilder((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        if (buf instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.buf);
        } else {
        $serializer.write(this.buf);
        }
        
    }
    
    // constructor just for allocation
    public StringBuilder(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    // dispatcher for method abstract public x10.util.Builder.add(Element):x10.util.Builder[Element, Collection]
    public java.lang.Object add(final java.lang.Object a1, final x10.rtt.Type t1) {
    return add((java.lang.Object)a1);
    }
    // bridge for method abstract public x10.util.Builder.result():Collection
    public java.lang.String
      result$G(){return result$O();}
    
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
public x10.util.ArrayList<x10.core.Char> buf;
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
// creation method for java code (1-phase java constructor)
        public StringBuilder(){this((java.lang.System[]) null);
                                   $init();}
        
        // constructor for non-virtual call
        final public x10.util.StringBuilder x10$util$StringBuilder$$init$S() { {
                                                                                      
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"

                                                                                      
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"

                                                                                      
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.ArrayList<x10.core.Char> t63200 =
                                                                                        ((x10.util.ArrayList)(new x10.util.ArrayList<x10.core.Char>((java.lang.System[]) null, x10.rtt.Types.CHAR).$init()));
                                                                                      
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
this.buf = ((x10.util.ArrayList)(t63200));
                                                                                  }
                                                                                  return this;
                                                                                  }
        
        // constructor
        public x10.util.StringBuilder $init(){return x10$util$StringBuilder$$init$S();}
        
        
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
public java.lang.String
                                                                                                      toString(
                                                                                                      ){
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final java.lang.String t63201 =
              this.result$O();
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return t63201;
        }
        
        
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
public x10.util.StringBuilder
                                                                                                      add(
                                                                                                      final java.lang.Object o){
            
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final boolean t63205 =
              ((o) == (null));
            
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
if (t63205) {
                
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.StringBuilder t63202 =
                  this.addString(((java.lang.String)("null")));
                
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return t63202;
            } else {
                
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final java.lang.String t63203 =
                  x10.rtt.Types.toString(o);
                
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.StringBuilder t63204 =
                  this.addString(((java.lang.String)(t63203)));
                
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return t63204;
            }
        }
        
        
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
public x10.util.StringBuilder
                                                                                                      insert(
                                                                                                      final int p,
                                                                                                      final java.lang.Object o){
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final boolean t63209 =
              ((o) == (null));
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
if (t63209) {
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.StringBuilder t63206 =
                  this.insertString((int)(p),
                                    ((java.lang.String)("null")));
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return t63206;
            } else {
                
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final java.lang.String t63207 =
                  x10.rtt.Types.toString(o);
                
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.StringBuilder t63208 =
                  this.insertString((int)(p),
                                    ((java.lang.String)(t63207)));
                
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return t63208;
            }
        }
        
        
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
public x10.util.StringBuilder
                                                                                                      add(
                                                                                                      final char x){
            
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.ArrayList<x10.core.Char> t63210 =
              ((x10.util.ArrayList)(buf));
            
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
((x10.util.ArrayList<x10.core.Char>)t63210).add__0x10$util$ArrayList$$T$O(x10.core.Char.$box(x));
            
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return this;
        }
        
        
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
public x10.util.StringBuilder
                                                                                                      add(
                                                                                                      final boolean x){
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final java.lang.String t63211 =
              java.lang.Boolean.toString(x);
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.StringBuilder t63212 =
              this.addString(((java.lang.String)(t63211)));
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return t63212;
        }
        
        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
public x10.util.StringBuilder
                                                                                                      add(
                                                                                                      final byte x){
            
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final java.lang.String t63213 =
              java.lang.Byte.toString((byte)x);
            
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.StringBuilder t63214 =
              this.addString(((java.lang.String)(t63213)));
            
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return t63214;
        }
        
        
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
public x10.util.StringBuilder
                                                                                                      add(
                                                                                                      final short x){
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final java.lang.String t63215 =
              java.lang.Short.toString((short)x);
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.StringBuilder t63216 =
              this.addString(((java.lang.String)(t63215)));
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return t63216;
        }
        
        
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
public x10.util.StringBuilder
                                                                                                      add(
                                                                                                      final int x){
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final java.lang.String t63217 =
              java.lang.Integer.toString(x);
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.StringBuilder t63218 =
              this.addString(((java.lang.String)(t63217)));
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return t63218;
        }
        
        
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
public x10.util.StringBuilder
                                                                                                      add(
                                                                                                      final long x){
            
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final java.lang.String t63219 =
              java.lang.Long.toString(x);
            
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.StringBuilder t63220 =
              this.addString(((java.lang.String)(t63219)));
            
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return t63220;
        }
        
        
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
public x10.util.StringBuilder
                                                                                                      add(
                                                                                                      final float x){
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final java.lang.String t63221 =
              java.lang.Float.toString(x);
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.StringBuilder t63222 =
              this.addString(((java.lang.String)(t63221)));
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return t63222;
        }
        
        
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
public x10.util.StringBuilder
                                                                                                      add(
                                                                                                      final double x){
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final java.lang.String t63223 =
              java.lang.Double.toString(x);
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.StringBuilder t63224 =
              this.addString(((java.lang.String)(t63223)));
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return t63224;
        }
        
        
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
public x10.util.StringBuilder
                                                                                                      add(
                                                                                                      final java.lang.String x){
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final boolean t63225 =
              ((x) == (null));
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
x10.util.StringBuilder t63226 =
               null;
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
if (t63225) {
                
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
t63226 = this.addString(((java.lang.String)("null")));
            } else {
                
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
t63226 = this.addString(((java.lang.String)(x)));
            }
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.StringBuilder t63227 =
              t63226;
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return t63227;
        }
        
        
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
public x10.util.StringBuilder
                                                                                                      insert(
                                                                                                      final int p,
                                                                                                      final boolean x){
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final java.lang.String t63228 =
              java.lang.Boolean.toString(x);
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.StringBuilder t63229 =
              this.insertString((int)(p),
                                ((java.lang.String)(t63228)));
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return t63229;
        }
        
        
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
public x10.util.StringBuilder
                                                                                                      insert(
                                                                                                      final int p,
                                                                                                      final byte x){
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final java.lang.String t63230 =
              java.lang.Byte.toString((byte)x);
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.StringBuilder t63231 =
              this.insertString((int)(p),
                                ((java.lang.String)(t63230)));
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return t63231;
        }
        
        
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
public x10.util.StringBuilder
                                                                                                      insert(
                                                                                                      final int p,
                                                                                                      final char x){
            
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final java.lang.String t63232 =
              java.lang.Character.toString(x);
            
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.StringBuilder t63233 =
              this.insertString((int)(p),
                                ((java.lang.String)(t63232)));
            
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return t63233;
        }
        
        
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
public x10.util.StringBuilder
                                                                                                      insert(
                                                                                                      final int p,
                                                                                                      final short x){
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final java.lang.String t63234 =
              java.lang.Short.toString((short)x);
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.StringBuilder t63235 =
              this.insertString((int)(p),
                                ((java.lang.String)(t63234)));
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return t63235;
        }
        
        
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
public x10.util.StringBuilder
                                                                                                      insert(
                                                                                                      final int p,
                                                                                                      final int x){
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final java.lang.String t63236 =
              java.lang.Integer.toString(x);
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.StringBuilder t63237 =
              this.insertString((int)(p),
                                ((java.lang.String)(t63236)));
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return t63237;
        }
        
        
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
public x10.util.StringBuilder
                                                                                                      insert(
                                                                                                      final int p,
                                                                                                      final long x){
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final java.lang.String t63238 =
              java.lang.Long.toString(x);
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.StringBuilder t63239 =
              this.insertString((int)(p),
                                ((java.lang.String)(t63238)));
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return t63239;
        }
        
        
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
public x10.util.StringBuilder
                                                                                                      insert(
                                                                                                      final int p,
                                                                                                      final float x){
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final java.lang.String t63240 =
              java.lang.Float.toString(x);
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.StringBuilder t63241 =
              this.insertString((int)(p),
                                ((java.lang.String)(t63240)));
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return t63241;
        }
        
        
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
public x10.util.StringBuilder
                                                                                                      insert(
                                                                                                      final int p,
                                                                                                      final double x){
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final java.lang.String t63242 =
              java.lang.Double.toString(x);
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.StringBuilder t63243 =
              this.insertString((int)(p),
                                ((java.lang.String)(t63242)));
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return t63243;
        }
        
        
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
public x10.util.StringBuilder
                                                                                                      insert(
                                                                                                      final int p,
                                                                                                      final java.lang.String x){
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final boolean t63244 =
              ((x) == (null));
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
x10.util.StringBuilder t63245 =
               null;
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
if (t63244) {
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
t63245 = this.insertString((int)(p),
                                                                                                                                       ((java.lang.String)("null")));
            } else {
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
t63245 = this.insertString((int)(p),
                                                                                                                                       ((java.lang.String)(x)));
            }
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.StringBuilder t63246 =
              t63245;
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return t63246;
        }
        
        
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
public x10.util.StringBuilder
                                                                                                      addString(
                                                                                                      final java.lang.String s){
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
int i63285 =
              0;
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
for (;
                                                                                                             true;
                                                                                                             ) {
                
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final int t63286 =
                  i63285;
                
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final int t63287 =
                  (s).length();
                
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final boolean t63288 =
                  ((t63286) < (((int)(t63287))));
                
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
if (!(t63288)) {
                    
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
break;
                }
                
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final int t63280 =
                  i63285;
                
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final char ch63281 =
                  (s).charAt(((int)(t63280)));
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.ArrayList<x10.core.Char> t63282 =
                  ((x10.util.ArrayList)(buf));
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
((x10.util.ArrayList<x10.core.Char>)t63282).add__0x10$util$ArrayList$$T$O(x10.core.Char.$box(ch63281));
                
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final int t63283 =
                  i63285;
                
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final int t63284 =
                  ((t63283) + (((int)(1))));
                
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
i63285 = t63284;
            }
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return this;
        }
        
        
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
public x10.util.StringBuilder
                                                                                                      insertString(
                                                                                                      final int pos,
                                                                                                      final java.lang.String s){
            
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
int loc =
              pos;
            
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final int t63255 =
              (s).length();
            
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final boolean t63256 =
              ((int) t63255) ==
            ((int) 0);
            
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
if (t63256) {
                
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return this;
            }
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final int t63258 =
              loc;
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.ArrayList<x10.core.Char> t63257 =
              ((x10.util.ArrayList)(buf));
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final int t63259 =
              ((x10.util.ArrayList<x10.core.Char>)t63257).size$O();
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final boolean t63261 =
              ((t63258) > (((int)(t63259))));
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
if (t63261) {
                
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.StringBuilder t63260 =
                  this.addString(((java.lang.String)(s)));
                
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return t63260;
            }
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final int t63262 =
              loc;
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final boolean t63263 =
              ((t63262) < (((int)(0))));
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
if (t63263) {
                
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
loc = 0;
            }
            
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
int i63297 =
              0;
            
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
for (;
                                                                                                             true;
                                                                                                             ) {
                
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final int t63298 =
                  i63297;
                
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final int t63299 =
                  (s).length();
                
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final boolean t63300 =
                  ((t63298) < (((int)(t63299))));
                
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
if (!(t63300)) {
                    
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
break;
                }
                
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final int t63289 =
                  i63297;
                
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final char ch63290 =
                  (s).charAt(((int)(t63289)));
                
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.ArrayList<x10.core.Char> t63291 =
                  ((x10.util.ArrayList)(buf));
                
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final int t63292 =
                  loc;
                
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final int t63293 =
                  i63297;
                
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final int t63294 =
                  ((t63292) + (((int)(t63293))));
                
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
((x10.util.ArrayList<x10.core.Char>)t63291).$set__1x10$util$ArrayList$$T$G((int)(t63294),
                                                                                                                                                                                       x10.core.Char.$box(ch63290));
                
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final int t63295 =
                  i63297;
                
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final int t63296 =
                  ((t63295) + (((int)(1))));
                
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
i63297 = t63296;
            }
            
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return this;
        }
        
        
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
public int
                                                                                                       length$O(
                                                                                                       ){
            
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.ArrayList<x10.core.Char> t63275 =
              ((x10.util.ArrayList)(buf));
            
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final int t63276 =
              ((x10.util.ArrayList<x10.core.Char>)t63275).size$O();
            
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return t63276;
        }
        
        
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
public java.lang.String
                                                                                                       result$O(
                                                                                                       ){
            
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.util.ArrayList<x10.core.Char> t63277 =
              ((x10.util.ArrayList)(buf));
            
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final x10.array.Array<x10.core.Char> array =
              ((x10.array.Array)(((x10.array.Array<x10.core.Char>)
                                   ((x10.util.ArrayList<x10.core.Char>)t63277).toArray())));
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final int t63278 =
              ((x10.array.Array<x10.core.Char>)array).
                size;
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final java.lang.String t63279 =
              ((java.lang.String)(new java.lang.String((array).raw().getCharArray(),((int)(0)),((int)(t63278)))));
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return t63279;
        }
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
final public x10.util.StringBuilder
                                                                                                      x10$util$StringBuilder$$x10$util$StringBuilder$this(
                                                                                                      ){
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/StringBuilder.x10"
return x10.util.StringBuilder.this;
        }
    
}
