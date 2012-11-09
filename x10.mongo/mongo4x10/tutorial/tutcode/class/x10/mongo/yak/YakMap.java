package x10.mongo.yak;


@x10.core.X10Generated public class YakMap extends org.bson.BasicBSONObject implements com.mongodb.DBObject, x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, YakMap.class);
    
    public static final x10.rtt.RuntimeType<YakMap> $RTT = x10.rtt.NamedType.<YakMap> make(
    "x10.mongo.yak.YakMap", /* base class */YakMap.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.getRTT(com.mongodb.DBObject.class), x10.rtt.Types.getRTT(org.bson.BasicBSONObject.class)}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(YakMap $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + YakMap.class + " calling"); } 
        $deserializer.deserializeClassUsingReflection(org.bson.BasicBSONObject.class, $_obj, 0);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        YakMap $_obj = new YakMap((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.serializeClassUsingReflection(this, org.bson.BasicBSONObject.class);
        
    }
    
    // constructor just for allocation
    public YakMap(final java.lang.System[] $dummy) { 
    }
    
        
        
//#line 52 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public int
                                                                                                     hashCode(
                                                                                                     ){
            
//#line 52 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final int t4015 =
              super.hashCode();
            
//#line 52 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4015;
        }
        
        
//#line 56 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public java.lang.String
                                                                                                     toString(
                                                                                                     ){
            
//#line 56 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4016 =
              super.toString();
            
//#line 56 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4016;
        }
        
        
//#line 61 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public boolean
                                                                                                     equals(
                                                                                                     final java.lang.Object a){
            
//#line 61 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final boolean t4017 =
              super.equals(((java.lang.Object)(a)));
            
//#line 61 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4017;
        }
        
        
//#line 72 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.LoadedYakMap
                                                                                                     $funnel(
                                                                                                     final java.lang.String s){
            
//#line 72 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.LoadedYakMap t4018 =
              ((x10.mongo.yak.LoadedYakMap)(new x10.mongo.yak.LoadedYakMap((java.lang.System[]) null).x10$mongo$yak$LoadedYakMap$$init$S(((x10.mongo.yak.YakMap)(this)),
                                                                                                                                         ((java.lang.String)(s)))));
            
//#line 72 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4018;
        }
        
        
//#line 77 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public YakMap() {super();
                                                                                                                         {
                                                                                                                            
//#line 77 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"

                                                                                                                        }}
        
        
//#line 84 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public YakMap(final java.lang.String k,
                                                                                                                 final java.lang.Object a) {super(((java.lang.String)(k)),
                                                                                                                                                  ((java.lang.Object)(a)));
                                                                                                                                                 {
                                                                                                                                                    
//#line 84 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"

                                                                                                                                                }}
        
        
//#line 90 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public YakMap(final x10.mongo.yak.YakMap ym) {this(((java.util.AbstractMap)
                                                                                                                                                        ym));
                                                                                                                                                      {
                                                                                                                                                         
                                                                                                                                                     }}
        
        
//#line 96 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public YakMap(final java.util.AbstractMap ym) {super();
                                                                                                                                                       {
                                                                                                                                                          
//#line 96 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"

                                                                                                                                                          
//#line 97 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap it =
                                                                                                                                                            ((x10.mongo.yak.YakMap)(new x10.mongo.yak.YakMap()));
                                                                                                                                                          
//#line 98 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
it.putAll(((java.util.Map)(ym)));
                                                                                                                                                      }}
        
        
//#line 105 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public YakMap(final org.bson.BasicBSONObject b) {super(((java.util.Map)(b)));
                                                                                                                                                          {
                                                                                                                                                             
//#line 105 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"

                                                                                                                                                         }}
        
        
//#line 111 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public YakMap(final com.mongodb.DBObject b) {super(((java.util.Map)(x10.rtt.Types.<org.bson.BasicBSONObject> cast(b,x10.rtt.Types.getRTT(org.bson.BasicBSONObject.class)))));
                                                                                                                                                      {
                                                                                                                                                         
//#line 111 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"

                                                                                                                                                     }}
        
        
//#line 118 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public static x10.mongo.yak.YakMap
                                                                                                      yakifyTopLevel(
                                                                                                      final org.bson.BasicBSONObject b){
            
//#line 118 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final boolean t4019 =
              ((b) == (null));
            
//#line 118 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
x10.mongo.yak.YakMap t4020 =
               null;
            
//#line 118 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
if (t4019) {
                
//#line 118 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
t4020 = null;
            } else {
                
//#line 118 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
t4020 = new x10.mongo.yak.YakMap(((org.bson.BasicBSONObject)(b)));
            }
            
//#line 118 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4021 =
              t4020;
            
//#line 118 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4021;
        }
        
        
//#line 125 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public static x10.mongo.yak.YakMap
                                                                                                      yakifyTopLevel(
                                                                                                      final com.mongodb.DBObject b){
            
//#line 125 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final boolean t4022 =
              ((b) == (null));
            
//#line 125 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
x10.mongo.yak.YakMap t4023 =
               null;
            
//#line 125 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
if (t4022) {
                
//#line 125 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
t4023 = null;
            } else {
                
//#line 125 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
t4023 = new x10.mongo.yak.YakMap(((com.mongodb.DBObject)(b)));
            }
            
//#line 125 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4024 =
              t4023;
            
//#line 125 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4024;
        }
        
        
//#line 134 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      $over(
                                                                                                      final java.lang.String key){
            
//#line 135 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.Object o =
              super.get(((java.lang.String)(key)));
            
//#line 136 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final boolean t4026 =
              x10.mongo.yak.YakMap.$RTT.isInstance(o);
            
//#line 136 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
if (t4026) {
                
//#line 136 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4025 =
                  ((x10.mongo.yak.YakMap)(x10.rtt.Types.<x10.mongo.yak.YakMap> cast(o,x10.mongo.yak.YakMap.$RTT)));
                
//#line 136 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4025;
            }
            
//#line 137 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final boolean t4029 =
              x10.rtt.Types.getRTT(org.bson.BasicBSONObject.class).isInstance(o);
            
//#line 137 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
if (t4029) {
                
//#line 137 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final org.bson.BasicBSONObject t4027 =
                  ((org.bson.BasicBSONObject)(x10.rtt.Types.<org.bson.BasicBSONObject> cast(o,x10.rtt.Types.getRTT(org.bson.BasicBSONObject.class))));
                
//#line 137 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4028 =
                  x10.mongo.yak.YakMap.yakifyTopLevel(((org.bson.BasicBSONObject)(t4027)));
                
//#line 137 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4028;
            }
            
//#line 139 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4030 =
              x10.rtt.Types.typeName(o);
            
//#line 138 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4031 =
              (("\'YakMap./ only works for members that are maps, not ") + (t4030));
            
//#line 138 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4032 =
              ((t4031) + (" like "));
            
//#line 138 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4033 =
              ((t4032) + (o));
            
//#line 138 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.lang.RuntimeException t4034 =
              ((x10.lang.RuntimeException)(new x10.lang.RuntimeException(t4033)));
            
//#line 138 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
throw t4034;
        }
        
        
//#line 154 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public java.lang.Object
                                                                                                      $apply(
                                                                                                      final java.lang.String key){
            
//#line 155 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.Object o =
              super.get(((java.lang.String)(key)));
            
//#line 156 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final boolean t4035 =
              x10.mongo.yak.YakMap.$RTT.isInstance(o);
            
//#line 156 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
if (t4035) {
                
//#line 156 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return o;
            }
            
//#line 157 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final boolean t4038 =
              x10.rtt.Types.getRTT(org.bson.BasicBSONObject.class).isInstance(o);
            
//#line 157 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
if (t4038) {
                
//#line 157 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final org.bson.BasicBSONObject t4036 =
                  ((org.bson.BasicBSONObject)(x10.rtt.Types.<org.bson.BasicBSONObject> cast(o,x10.rtt.Types.getRTT(org.bson.BasicBSONObject.class))));
                
//#line 157 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4037 =
                  x10.mongo.yak.YakMap.yakifyTopLevel(((org.bson.BasicBSONObject)(t4036)));
                
//#line 157 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4037;
            }
            
//#line 158 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return o;
        }
        
        
//#line 167 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      $plus(
                                                                                                      final x10.mongo.yak.YakMap that){
            
//#line 168 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final boolean hasId =
              this.containsKey(((java.lang.String)("_id")));
            
//#line 169 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.Object oldId =
              this.get(((java.lang.String)("_id")));
            
//#line 170 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.util.Map t4039 =
              ((java.util.Map)
                that);
            
//#line 170 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
this.putAll(((java.util.Map)(t4039)));
            
//#line 171 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
if (hasId) {
                
//#line 171 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
this.put(((java.lang.String)("_id")),
                                                                                                                     ((java.lang.Object)(oldId)));
            }
            
//#line 172 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return this;
        }
        
        
//#line 178 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      dup(
                                                                                                      ){
            
//#line 178 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4040 =
              ((x10.mongo.yak.YakMap)(new x10.mongo.yak.YakMap(((x10.mongo.yak.YakMap)(this)))));
            
//#line 178 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4040;
        }
        
        
//#line 184 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public int
                                                                                                      kwd_int$O(
                                                                                                      final java.lang.String s){
            
//#line 184 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final int t4041 =
              this.getInt(((java.lang.String)(s)));
            
//#line 184 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4041;
        }
        
        
//#line 189 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public long
                                                                                                      kwd_long$O(
                                                                                                      final java.lang.String s){
            
//#line 189 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final long t4042 =
              this.getLong(((java.lang.String)(s)));
            
//#line 189 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4042;
        }
        
        
//#line 199 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public float
                                                                                                      kwd_float$O(
                                                                                                      final java.lang.String s){
            
//#line 199 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.Object t4043 =
              this.$apply(((java.lang.String)(s)));
            
//#line 199 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final float t4044 =
              x10.rtt.Types.asfloat(t4043,x10.rtt.Types.ANY);
            
//#line 199 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4044;
        }
        
        
//#line 205 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public double
                                                                                                      kwd_double$O(
                                                                                                      final java.lang.String s){
            
//#line 205 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.Object t4045 =
              this.$apply(((java.lang.String)(s)));
            
//#line 205 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final double t4046 =
              x10.rtt.Types.asdouble(t4045,x10.rtt.Types.ANY);
            
//#line 205 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4046;
        }
        
        
//#line 207 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public boolean
                                                                                                      bool$O(
                                                                                                      final java.lang.String s){
            
//#line 207 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final boolean t4047 =
              this.getBoolean(((java.lang.String)(s)));
            
//#line 207 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4047;
        }
        
        
//#line 212 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public java.lang.String
                                                                                                      str$O(
                                                                                                      final java.lang.String s){
            
//#line 212 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4048 =
              this.getString(((java.lang.String)(s)));
            
//#line 212 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4048;
        }
        
        
//#line 218 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public boolean
                                                                                                      has$O(
                                                                                                      final java.lang.String s){
            
//#line 218 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final boolean t4049 =
              this.containsKey(((java.lang.String)(s)));
            
//#line 218 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4049;
        }
        
        
//#line 223 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public void
                                                                                                      markAsPartialObject(
                                                                                                      ){
            
        }
        
        
//#line 230 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public boolean
                                                                                                      isPartialObject(
                                                                                                      ){
            
//#line 231 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return false;
        }
        
        
//#line 239 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public java.lang.String
                                                                                                      typeName(
                                                                                                      ){
            
//#line 239 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return "YakMap";
        }
        
        
//#line 249 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public static boolean
                                                                                                      fieldAcceptsMultiplePairs$O(
                                                                                                      final java.lang.String fieldName){
            
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
boolean t4050 =
              ("$inc").equals(fieldName);
            
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
if (!(t4050)) {
                
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
t4050 = ("$set").equals(fieldName);
            }
            
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
boolean t4051 =
              t4050;
            
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
if (!(t4051)) {
                
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
t4051 = ("$unset").equals(fieldName);
            }
            
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
boolean t4052 =
              t4051;
            
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
if (!(t4052)) {
                
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
t4052 = ("$push").equals(fieldName);
            }
            
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
boolean t4053 =
              t4052;
            
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
if (!(t4053)) {
                
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
t4053 = ("$pushAll").equals(fieldName);
            }
            
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
boolean t4054 =
              t4053;
            
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
if (!(t4054)) {
                
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
t4054 = ("$addToSet").equals(fieldName);
            }
            
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
boolean t4055 =
              t4054;
            
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
if (!(t4055)) {
                
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
t4055 = ("$each").equals(fieldName);
            }
            
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
boolean t4056 =
              t4055;
            
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
if (!(t4056)) {
                
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
t4056 = ("$pop").equals(fieldName);
            }
            
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
boolean t4057 =
              t4056;
            
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
if (!(t4057)) {
                
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
t4057 = ("$pull").equals(fieldName);
            }
            
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
boolean t4058 =
              t4057;
            
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
if (!(t4058)) {
                
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
t4058 = ("$pullAll").equals(fieldName);
            }
            
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
boolean t4059 =
              t4058;
            
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
if (!(t4059)) {
                
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
t4059 = ("$rename").equals(fieldName);
            }
            
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
boolean t4060 =
              t4059;
            
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
if (!(t4060)) {
                
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
t4060 = ("$bit").equals(fieldName);
            }
            
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final boolean t4061 =
              t4060;
            
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4061;
        }
        
        
//#line 279 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      fieldIs(
                                                                                                      final java.lang.String aa,
                                                                                                      final java.lang.String bb,
                                                                                                      final java.lang.Object cc){
            
//#line 280 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final boolean t4097 =
              this.containsKey(((java.lang.String)(aa)));
            
//#line 280 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
if (t4097) {
                
//#line 281 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final boolean t4094 =
                  x10.mongo.yak.YakMap.fieldAcceptsMultiplePairs$O(((java.lang.String)(aa)));
                
//#line 281 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
if (t4094) {
                    
//#line 282 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.Object oldCc =
                      this.get(((java.lang.String)(aa)));
                    
//#line 283 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final boolean t4082 =
                      x10.rtt.Types.getRTT(java.util.Map.class).isInstance(oldCc);
                    
//#line 283 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
if (t4082) {
                        
//#line 284 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.util.Map oldMap =
                          ((java.util.Map)(x10.rtt.Types.<java.util.Map> cast(oldCc,x10.rtt.Types.getRTT(java.util.Map.class))));
                        
//#line 285 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final boolean t4071 =
                          oldMap.containsKey(((java.lang.Object)(bb)));
                        
//#line 285 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
if (t4071) {
                            
//#line 286 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4062 =
                              (("Yak error: you\'re trying to add or overwrite a value to an update operation\'s field field ") + (aa));
                            
//#line 286 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4063 =
                              ((t4062) + (" of a record "));
                            
//#line 286 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4064 =
                              ((t4063) + (this));
                            
//#line 286 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4065 =
                              ((t4064) + ("\n Current cc of field: "));
                            
//#line 286 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4066 =
                              ((t4065) + (oldCc));
                            
//#line 286 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4067 =
                              ((t4066) + ("\n Cc being updated to: "));
                            
//#line 286 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4068 =
                              ((t4067) + (cc));
                            
//#line 286 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4069 =
                              ((t4068) + ("This is an update operation, and this operation will delete the old value\'s effects."));
                            
//#line 286 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.lang.RuntimeException t4070 =
                              ((x10.lang.RuntimeException)(new x10.lang.RuntimeException(t4069)));
                            
//#line 286 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
throw t4070;
                        }
                        
//#line 292 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.Object t4072 =
                          x10.mongo.yak.LoadedYakMap.javify(((java.lang.Object)(cc)));
                        
//#line 292 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
oldMap.put(((java.lang.Object)(bb)),
                                                                                                                               ((java.lang.Object)(t4072)));
                    } else {
                        
//#line 295 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4073 =
                          (("Yak Error: Trying to add an extra name/value pair to the ") + (aa));
                        
//#line 295 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4074 =
                          ((t4073) + (" of a record, but the cc of that record is not a BSON object\n  cc = "));
                        
//#line 295 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4075 =
                          ((t4074) + (oldCc));
                        
//#line 295 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4076 =
                          ((t4075) + ("; trying to add {"));
                        
//#line 295 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4077 =
                          ((t4076) + (bb));
                        
//#line 295 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4078 =
                          ((t4077) + (":"));
                        
//#line 295 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4079 =
                          ((t4078) + (cc));
                        
//#line 295 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4080 =
                          ((t4079) + ("}"));
                        
//#line 295 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.lang.RuntimeException t4081 =
                          ((x10.lang.RuntimeException)(new x10.lang.RuntimeException(t4080)));
                        
//#line 295 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
throw t4081;
                    }
                } else {
                    
//#line 299 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4083 =
                      (("Yak Error: attempt to add a field operator named ") + (aa));
                    
//#line 299 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4084 =
                      ((t4083) + (" to a YakMap that already had one.\n  map="));
                    
//#line 299 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4085 =
                      ((t4084) + (this));
                    
//#line 299 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4086 =
                      ((t4085) + ("\n  addition: {"));
                    
//#line 299 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4087 =
                      ((t4086) + (aa));
                    
//#line 299 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4088 =
                      ((t4087) + (": {"));
                    
//#line 299 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4089 =
                      ((t4088) + (bb));
                    
//#line 299 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4090 =
                      ((t4089) + (":"));
                    
//#line 299 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4091 =
                      ((t4090) + (cc));
                    
//#line 299 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4092 =
                      ((t4091) + ("}}"));
                    
//#line 299 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.lang.RuntimeException t4093 =
                      ((x10.lang.RuntimeException)(new x10.lang.RuntimeException(t4092)));
                    
//#line 299 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
throw t4093;
                }
            } else {
                
//#line 302 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.Object t4095 =
                  x10.mongo.yak.LoadedYakMap.javify(((java.lang.Object)(cc)));
                
//#line 302 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4096 =
                  ((x10.mongo.yak.YakMap)(new x10.mongo.yak.YakMap(((java.lang.String)(bb)),
                                                                   t4095)));
                
//#line 302 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
this.put(((java.lang.String)(aa)),
                                                                                                                     ((java.lang.Object)(t4096)));
            }
            
//#line 304 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return this;
        }
        
        
//#line 312 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      gt(
                                                                                                      final java.lang.String fieldName,
                                                                                                      final java.lang.Object v){
            
//#line 312 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4098 =
              this.fieldIs(((java.lang.String)(fieldName)),
                           ((java.lang.String)("$gt")),
                           ((java.lang.Object)(v)));
            
//#line 312 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4098;
        }
        
        
//#line 319 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      lt(
                                                                                                      final java.lang.String fieldName,
                                                                                                      final java.lang.Object v){
            
//#line 319 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4099 =
              this.fieldIs(((java.lang.String)(fieldName)),
                           ((java.lang.String)("$lt")),
                           ((java.lang.Object)(v)));
            
//#line 319 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4099;
        }
        
        
//#line 326 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      gte(
                                                                                                      final java.lang.String fieldName,
                                                                                                      final java.lang.Object v){
            
//#line 326 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4100 =
              this.fieldIs(((java.lang.String)(fieldName)),
                           ((java.lang.String)("$gte")),
                           ((java.lang.Object)(v)));
            
//#line 326 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4100;
        }
        
        
//#line 333 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      lte(
                                                                                                      final java.lang.String fieldName,
                                                                                                      final java.lang.Object v){
            
//#line 333 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4101 =
              this.fieldIs(((java.lang.String)(fieldName)),
                           ((java.lang.String)("$lte")),
                           ((java.lang.Object)(v)));
            
//#line 333 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4101;
        }
        
        
//#line 341 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      ne(
                                                                                                      final java.lang.String fieldName,
                                                                                                      final java.lang.Object v){
            
//#line 341 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4102 =
              this.fieldIs(((java.lang.String)(fieldName)),
                           ((java.lang.String)("$ne")),
                           ((java.lang.Object)(v)));
            
//#line 341 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4102;
        }
        
        
//#line 350 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      all(
                                                                                                      final java.lang.String fieldName,
                                                                                                      final java.lang.Object v){
            
//#line 350 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4103 =
              this.fieldIs(((java.lang.String)(fieldName)),
                           ((java.lang.String)("$all")),
                           ((java.lang.Object)(v)));
            
//#line 350 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4103;
        }
        
        
//#line 360 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      exists(
                                                                                                      final java.lang.String fieldName,
                                                                                                      final java.lang.Object v){
            
//#line 360 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4104 =
              this.fieldIs(((java.lang.String)(fieldName)),
                           ((java.lang.String)("$exists")),
                           ((java.lang.Object)(v)));
            
//#line 360 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4104;
        }
        
        
//#line 369 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      exists(
                                                                                                      final java.lang.String fieldName){
            
//#line 369 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4105 =
              this.exists(((java.lang.String)(fieldName)),
                          x10.core.Boolean.$box(true));
            
//#line 369 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4105;
        }
        
        
//#line 377 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      eq(
                                                                                                      final java.lang.String fieldName,
                                                                                                      final java.lang.Object v){
            
//#line 377 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.LoadedYakMap t4106 =
              ((x10.mongo.yak.LoadedYakMap)(this.$funnel(((java.lang.String)(fieldName)))));
            
//#line 377 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4107 =
              ((x10.mongo.yak.YakMap)(t4106.$lfunnel(((java.lang.Object)(v)))));
            
//#line 377 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4107;
        }
        
        
//#line 387 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      In(
                                                                                                      final java.lang.String fieldName,
                                                                                                      final java.lang.Object choices){
            
//#line 387 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4108 =
              this.fieldIs(((java.lang.String)(fieldName)),
                           ((java.lang.String)("$in")),
                           ((java.lang.Object)(choices)));
            
//#line 387 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4108;
        }
        
        
//#line 396 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      elemMatch(
                                                                                                      final java.lang.String fieldName,
                                                                                                      final x10.mongo.yak.YakMap subquery){
            
//#line 396 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4109 =
              this.fieldIs(((java.lang.String)(fieldName)),
                           ((java.lang.String)("$elemMatch")),
                           ((java.lang.Object)(subquery)));
            
//#line 396 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4109;
        }
        
        
//#line 404 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      nin(
                                                                                                      final java.lang.String fieldName,
                                                                                                      final java.lang.Object vs){
            
//#line 404 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4110 =
              this.fieldIs(((java.lang.String)(fieldName)),
                           ((java.lang.String)("$nin")),
                           ((java.lang.Object)(vs)));
            
//#line 404 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4110;
        }
        
        
//#line 414 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      mod(
                                                                                                      final java.lang.String fieldName,
                                                                                                      final long divisor,
                                                                                                      final long remainder){
            
//#line 414 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakList t4111 =
              x10.mongo.yak.YakUtil.list(x10.core.Long.$box(divisor),
                                         x10.core.Long.$box(remainder));
            
//#line 414 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4112 =
              this.fieldIs(((java.lang.String)(fieldName)),
                           ((java.lang.String)("$mod")),
                           ((java.lang.Object)(t4111)));
            
//#line 414 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4112;
        }
        
        
//#line 423 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      size(
                                                                                                      final java.lang.String fieldName,
                                                                                                      final java.lang.Object exactNumberOfElements){
            
//#line 423 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4113 =
              this.fieldIs(((java.lang.String)(fieldName)),
                           ((java.lang.String)("$size")),
                           ((java.lang.Object)(exactNumberOfElements)));
            
//#line 423 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4113;
        }
        
        
//#line 433 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      inc(
                                                                                                      final java.lang.String fieldName,
                                                                                                      final java.lang.Object by){
            
//#line 433 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4114 =
              this.fieldIs(((java.lang.String)("$inc")),
                           ((java.lang.String)(fieldName)),
                           ((java.lang.Object)(by)));
            
//#line 433 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4114;
        }
        
        
//#line 441 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      set(
                                                                                                      final java.lang.String fieldName,
                                                                                                      final java.lang.Object v){
            
//#line 441 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4115 =
              this.fieldIs(((java.lang.String)("$set")),
                           ((java.lang.String)(fieldName)),
                           ((java.lang.Object)(v)));
            
//#line 441 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4115;
        }
        
        
//#line 448 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      unset(
                                                                                                      final java.lang.String fieldName){
            
//#line 448 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4116 =
              this.fieldIs(((java.lang.String)("$unset")),
                           ((java.lang.String)(fieldName)),
                           x10.core.Int.$box(1));
            
//#line 448 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4116;
        }
        
        
//#line 456 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      push(
                                                                                                      final java.lang.String fieldName,
                                                                                                      final java.lang.Object by){
            
//#line 456 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4117 =
              this.fieldIs(((java.lang.String)("$push")),
                           ((java.lang.String)(fieldName)),
                           ((java.lang.Object)(by)));
            
//#line 456 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4117;
        }
        
        
//#line 464 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      pushAll(
                                                                                                      final java.lang.String fieldName,
                                                                                                      final java.lang.Object by){
            
//#line 464 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4118 =
              this.fieldIs(((java.lang.String)("$pushAll")),
                           ((java.lang.String)(fieldName)),
                           ((java.lang.Object)(by)));
            
//#line 464 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4118;
        }
        
        
//#line 472 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      addToSet(
                                                                                                      final java.lang.String fieldName,
                                                                                                      final java.lang.Object by){
            
//#line 472 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4119 =
              this.fieldIs(((java.lang.String)("$addToSet")),
                           ((java.lang.String)(fieldName)),
                           ((java.lang.Object)(by)));
            
//#line 472 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4119;
        }
        
        
//#line 480 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      addEachToSet(
                                                                                                      final java.lang.String fieldName,
                                                                                                      final java.lang.Object elements){
            
//#line 480 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakUtil t4120 =
              x10.mongo.yak.YakUtil.it();
            
//#line 480 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.LoadedYakMap t4121 =
              ((x10.mongo.yak.LoadedYakMap)(t4120.$funnel(((java.lang.String)("$each")))));
            
//#line 480 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4122 =
              ((x10.mongo.yak.YakMap)(t4121.$lfunnel(((java.lang.Object)(elements)))));
            
//#line 480 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4123 =
              this.fieldIs(((java.lang.String)("$addToSet")),
                           ((java.lang.String)(fieldName)),
                           ((java.lang.Object)(t4122)));
            
//#line 480 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4123;
        }
        
        
//#line 487 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      pop(
                                                                                                      final java.lang.String fieldName){
            
//#line 487 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4124 =
              this.fieldIs(((java.lang.String)("$pop")),
                           ((java.lang.String)(fieldName)),
                           x10.core.Int.$box(1));
            
//#line 487 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4124;
        }
        
        
//#line 496 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      pop(
                                                                                                      final java.lang.String fieldName,
                                                                                                      final java.lang.Object k){
            
//#line 496 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4125 =
              this.fieldIs(((java.lang.String)("$pop")),
                           ((java.lang.String)(fieldName)),
                           ((java.lang.Object)(k)));
            
//#line 496 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4125;
        }
        
        
//#line 504 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      pull(
                                                                                                      final java.lang.String fieldName,
                                                                                                      final java.lang.Object n){
            
//#line 504 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4126 =
              this.fieldIs(((java.lang.String)("$pull")),
                           ((java.lang.String)(fieldName)),
                           ((java.lang.Object)(n)));
            
//#line 504 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4126;
        }
        
        
//#line 512 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      pullAll(
                                                                                                      final java.lang.String fieldName,
                                                                                                      final java.lang.Object vs){
            
//#line 512 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4127 =
              this.fieldIs(((java.lang.String)("$pullAll")),
                           ((java.lang.String)(fieldName)),
                           ((java.lang.Object)(vs)));
            
//#line 512 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4127;
        }
        
        
//#line 522 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      rename(
                                                                                                      final java.lang.String oldName,
                                                                                                      final java.lang.String newName){
            
//#line 522 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4128 =
              this.fieldIs(((java.lang.String)("$rename")),
                           ((java.lang.String)(oldName)),
                           ((java.lang.Object)(newName)));
            
//#line 522 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4128;
        }
        
        
//#line 535 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.mongo.yak.YakMap
                                                                                                      $bar(
                                                                                                      final x10.mongo.yak.YakMap that){
            
//#line 536 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final boolean t4133 =
              this.containsKey(((java.lang.String)("$or")));
            
//#line 536 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
if (t4133) {
                
//#line 537 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.Object or =
                  this.get(((java.lang.String)("$or")));
                
//#line 538 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.util.ArrayList orlist =
                  ((java.util.ArrayList)(x10.rtt.Types.<java.util.ArrayList> cast(or,x10.rtt.Types.getRTT(java.util.ArrayList.class))));
                
//#line 539 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
orlist.add(((java.lang.Object)(that)));
                
//#line 540 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return this;
            } else {
                
//#line 543 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakUtil t4129 =
                  x10.mongo.yak.YakUtil.it();
                
//#line 543 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.LoadedYakMap t4130 =
                  ((x10.mongo.yak.LoadedYakMap)(t4129.$funnel(((java.lang.String)("$or")))));
                
//#line 543 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakList t4131 =
                  x10.mongo.yak.YakUtil.list(((java.lang.Object)(this)),
                                             ((java.lang.Object)(that)));
                
//#line 543 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.mongo.yak.YakMap t4132 =
                  ((x10.mongo.yak.YakMap)(t4130.$lfunnel(((java.lang.Object)(t4131)))));
                
//#line 543 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return t4132;
            }
        }
        
        
//#line 547 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
public x10.util.HashSet
                                                                                                      keys(
                                                                                                      ){
            
//#line 548 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final x10.util.HashSet S =
              ((x10.util.HashSet)(new x10.util.HashSet<java.lang.String>((java.lang.System[]) null, x10.rtt.Types.STRING).x10$util$HashSet$$init$S()));
            
//#line 549 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final org.bson.BSONObject t4134 =
              ((org.bson.BSONObject)
                this);
            
//#line 549 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.util.Set T =
              t4134.keySet();
            
//#line 550 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.util.Iterator ti =
              T.iterator();
            
//#line 551 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
while (true) {
                
//#line 551 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final boolean t4137 =
                  ti.hasNext();
                
//#line 551 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
if (!(t4137)) {
                    
//#line 551 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
break;
                }
                
//#line 551 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.Object t4138 =
                  ti.next();
                
//#line 551 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final java.lang.String t4139 =
                  x10.rtt.Types.<java.lang.String> castConversion(t4138,x10.rtt.Types.STRING);
                
//#line 551 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
((x10.util.MapSet<java.lang.String>)S).add__0x10$util$MapSet$$T$O(((java.lang.String)(t4139)));
            }
            
//#line 552 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return S;
        }
        
        
//#line 47 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
final public x10.mongo.yak.YakMap
                                                                                                     x10$mongo$yak$YakMap$$x10$mongo$yak$YakMap$this(
                                                                                                     ){
            
//#line 47 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMap.x10"
return x10.mongo.yak.YakMap.this;
        }
        
        public int
          java$util$AbstractMap$hashCode$S$O(
          ){
            return super.hashCode();
        }
        
        public java.lang.String
          org$bson$BasicBSONObject$toString$S$O(
          ){
            return super.toString();
        }
        
        public boolean
          org$bson$BasicBSONObject$equals$S$O(
          final java.lang.Object a0){
            return super.equals(((java.lang.Object)(a0)));
        }
        
        public java.lang.Object
          org$bson$BasicBSONObject$get$S(
          final java.lang.String a0){
            return super.get(((java.lang.String)(a0)));
        }
        
        }
        