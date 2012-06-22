package x10.util;

@x10.core.X10Generated public class Option extends x10.core.Struct implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Option.class);
    
    public static final x10.rtt.RuntimeType<Option> $RTT = x10.rtt.NamedType.<Option> make(
    "x10.util.Option", /* base class */Option.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.STRUCT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Option $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Option.class + " calling"); } 
        java.lang.String short_ = (java.lang.String) $deserializer.readRef();
        $_obj.short_ = short_;
        java.lang.String long_ = (java.lang.String) $deserializer.readRef();
        $_obj.long_ = long_;
        java.lang.String description = (java.lang.String) $deserializer.readRef();
        $_obj.description = description;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Option $_obj = new Option((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.write(this.short_);
        $serializer.write(this.long_);
        $serializer.write(this.description);
        
    }
    
    // zero value constructor
    public Option(final java.lang.System $dummy) { this.short_ = null; this.long_ = null; this.description = null; }
    // constructor just for allocation
    public Option(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
public java.lang.String short_;
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
public java.lang.String long_;
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
public java.lang.String description;
        
        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
// creation method for java code (1-phase java constructor)
        public Option(final java.lang.String s,
                      final java.lang.String l,
                      final java.lang.String d){this((java.lang.System[]) null);
                                                    $init(s,l,d);}
        
        // constructor for non-virtual call
        final public x10.util.Option x10$util$Option$$init$S(final java.lang.String s,
                                                             final java.lang.String l,
                                                             final java.lang.String d) { {
                                                                                                
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
;
                                                                                                
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"

                                                                                                
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
this.short_ = ((java.lang.String)(s));
                                                                                                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final boolean t59183 =
                                                                                                  ((s) != (null));
                                                                                                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
if (t59183) {
                                                                                                    
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final int t59180 =
                                                                                                      (s).length();
                                                                                                    
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final boolean t59182 =
                                                                                                      ((int) t59180) !=
                                                                                                    ((int) 1);
                                                                                                    
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
if (t59182) {
                                                                                                        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final x10.lang.IllegalArgumentException t59181 =
                                                                                                          ((x10.lang.IllegalArgumentException)(new x10.lang.IllegalArgumentException(((java.lang.String)("short options must be one letter only (or null)")))));
                                                                                                        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
throw t59181;
                                                                                                    }
                                                                                                }
                                                                                                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final boolean t59184 =
                                                                                                  ((l) == (null));
                                                                                                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
java.lang.String t59185 =
                                                                                                   null;
                                                                                                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
if (t59184) {
                                                                                                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
t59185 = null;
                                                                                                } else {
                                                                                                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
t59185 = (("--") + (l));
                                                                                                }
                                                                                                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final java.lang.String t59186 =
                                                                                                  t59185;
                                                                                                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
this.long_ = ((java.lang.String)(t59186));
                                                                                                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
this.description = ((java.lang.String)(d));
                                                                                            }
                                                                                            return this;
                                                                                            }
        
        // constructor
        public x10.util.Option $init(final java.lang.String s,
                                     final java.lang.String l,
                                     final java.lang.String d){return x10$util$Option$$init$S(s,l,d);}
        
        
        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final public java.lang.String
                                                                                               toString(
                                                                                               ){
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final java.lang.String t59187 =
              ((java.lang.String)(description));
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
return t59187;
        }
        
        
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final public boolean
                                                                                               equals(
                                                                                               final java.lang.Object other){
            
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final boolean t59190 =
              x10.util.Option.$RTT.isInstance(other);
            
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
if (t59190) {
                
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final x10.util.Option t59188 =
                  ((x10.util.Option)(((x10.util.Option)x10.rtt.Types.asStruct(x10.util.Option.$RTT,other))));
                
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final boolean t59189 =
                  this.equals$O(((x10.util.Option)(t59188)));
                
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
return t59189;
            } else {
                
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
return false;
            }
        }
        
        
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final public boolean
                                                                                               equals$O(
                                                                                               final x10.util.Option that){
            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final java.lang.String t59191 =
              ((java.lang.String)(description));
            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final java.lang.String t59192 =
              ((java.lang.String)(that.
                                    description));
            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final boolean t59193 =
              (t59191).equals(t59192);
            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final boolean t59194 =
              !(t59193);
            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
if (t59194) {
                
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
return false;
            }
            
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final java.lang.String t59195 =
              ((java.lang.String)(long_));
            
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final boolean t59202 =
              ((t59195) != (null));
            
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
if (t59202) {
                
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final java.lang.String t59196 =
                  ((java.lang.String)(long_));
                
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final java.lang.String t59197 =
                  ((java.lang.String)(that.
                                        long_));
                
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final boolean t59198 =
                  (t59196).equals(t59197);
                
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final boolean t59199 =
                  !(t59198);
                
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
if (t59199) {
                    
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
return false;
                }
            } else {
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final java.lang.String t59200 =
                  ((java.lang.String)(that.
                                        long_));
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final boolean t59201 =
                  ((t59200) != (null));
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
if (t59201) {
                    
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
return false;
                }
            }
            
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final java.lang.String t59203 =
              ((java.lang.String)(short_));
            
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final boolean t59206 =
              ((t59203) == (null));
            
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
if (t59206) {
                
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final java.lang.String t59204 =
                  ((java.lang.String)(that.
                                        short_));
                
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final boolean t59205 =
                  ((t59204) == (null));
                
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
return t59205;
            }
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final java.lang.String t59207 =
              ((java.lang.String)(short_));
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final java.lang.String t59208 =
              ((java.lang.String)(that.
                                    short_));
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final boolean t59209 =
              (t59207).equals(t59208);
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
return t59209;
        }
        
        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final public int
                                                                                               hashCode(
                                                                                               ){
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
int result =
              1;
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final java.lang.String t59210 =
              ((java.lang.String)(short_));
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final boolean t59216 =
              ((t59210) != (null));
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
if (t59216) {
                
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final int t59211 =
                  result;
                
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final int t59213 =
                  ((8191) * (((int)(t59211))));
                
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final java.lang.String t59212 =
                  ((java.lang.String)(short_));
                
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final int t59214 =
                  (t59212).hashCode();
                
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final int t59215 =
                  ((t59213) + (((int)(t59214))));
                
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
result = t59215;
            }
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final java.lang.String t59217 =
              ((java.lang.String)(long_));
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final boolean t59223 =
              ((t59217) != (null));
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
if (t59223) {
                
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final int t59218 =
                  result;
                
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final int t59220 =
                  ((8191) * (((int)(t59218))));
                
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final java.lang.String t59219 =
                  ((java.lang.String)(long_));
                
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final int t59221 =
                  (t59219).hashCode();
                
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final int t59222 =
                  ((t59220) + (((int)(t59221))));
                
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
result = t59222;
            }
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final int t59224 =
              result;
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final int t59226 =
              ((8191) * (((int)(t59224))));
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final java.lang.String t59225 =
              ((java.lang.String)(description));
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final int t59227 =
              (t59225).hashCode();
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final int t59228 =
              ((t59226) + (((int)(t59227))));
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
result = t59228;
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final int t59229 =
              result;
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
return t59229;
        }
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final public java.lang.String
                                                                                               typeName(
                                                                                               ){try {return x10.rtt.Types.typeName(this);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final public boolean
                                                                                               _struct_equals$O(
                                                                                               java.lang.Object other){
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final java.lang.Object t59230 =
              other;
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final boolean t59231 =
              x10.util.Option.$RTT.isInstance(t59230);
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final boolean t59232 =
              !(t59231);
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
if (t59232) {
                
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
return false;
            }
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final java.lang.Object t59233 =
              other;
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final x10.util.Option t59234 =
              ((x10.util.Option)x10.rtt.Types.asStruct(x10.util.Option.$RTT,t59233));
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final boolean t59235 =
              this._struct_equals$O(((x10.util.Option)(t59234)));
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
return t59235;
        }
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final public boolean
                                                                                               _struct_equals$O(
                                                                                               x10.util.Option other){
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final java.lang.String t59237 =
              ((java.lang.String)(this.
                                    short_));
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final x10.util.Option t59236 =
              other;
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final java.lang.String t59238 =
              ((java.lang.String)(t59236.
                                    short_));
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
boolean t59242 =
              x10.rtt.Equality.equalsequals((t59237),(t59238));
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
if (t59242) {
                
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final java.lang.String t59240 =
                  ((java.lang.String)(this.
                                        long_));
                
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final x10.util.Option t59239 =
                  other;
                
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final java.lang.String t59241 =
                  ((java.lang.String)(t59239.
                                        long_));
                
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
t59242 = x10.rtt.Equality.equalsequals((t59240),(t59241));
            }
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
boolean t59246 =
              t59242;
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
if (t59246) {
                
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final java.lang.String t59244 =
                  ((java.lang.String)(this.
                                        description));
                
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final x10.util.Option t59243 =
                  other;
                
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final java.lang.String t59245 =
                  ((java.lang.String)(t59243.
                                        description));
                
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
t59246 = x10.rtt.Equality.equalsequals((t59244),(t59245));
            }
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final boolean t59247 =
              t59246;
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
return t59247;
        }
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
final public x10.util.Option
                                                                                               x10$util$Option$$x10$util$Option$this(
                                                                                               ){
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Option.x10"
return x10.util.Option.this;
        }
    
}
