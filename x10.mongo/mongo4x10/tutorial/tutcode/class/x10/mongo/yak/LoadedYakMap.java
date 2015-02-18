package x10.mongo.yak;

@x10.core.X10Generated public class LoadedYakMap extends x10.core.Struct implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, LoadedYakMap.class);
    
    public static final x10.rtt.RuntimeType<LoadedYakMap> $RTT = x10.rtt.NamedType.<LoadedYakMap> make(
    "x10.mongo.yak.LoadedYakMap", /* base class */LoadedYakMap.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.STRUCT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(LoadedYakMap $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + LoadedYakMap.class + " calling"); } 
        x10.mongo.yak.YakMap yakMap = (x10.mongo.yak.YakMap) $deserializer.readRef();
        $_obj.yakMap = yakMap;
        java.lang.String newIndex = (java.lang.String) $deserializer.readRef();
        $_obj.newIndex = newIndex;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        LoadedYakMap $_obj = new LoadedYakMap((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        if (yakMap instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.yakMap);
        } else {
        $serializer.write(this.yakMap);
        }
        $serializer.write(this.newIndex);
        
    }
    
    // zero value constructor
    public LoadedYakMap(final java.lang.System $dummy) { this.yakMap = null; this.newIndex = null; }
    // constructor just for allocation
    public LoadedYakMap(final java.lang.System[] $dummy) { 
    }
    
        
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
public x10.mongo.yak.YakMap yakMap;
        
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
public java.lang.String newIndex;
        
        
        
//#line 14 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final public int
                                                                                                           hashCode(
                                                                                                           ){
            
//#line 14 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final x10.mongo.yak.YakMap t4280 =
              ((x10.mongo.yak.YakMap)(yakMap));
            
//#line 14 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final int t4282 =
              t4280.hashCode();
            
//#line 14 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final java.lang.String t4281 =
              ((java.lang.String)(newIndex));
            
//#line 14 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final int t4283 =
              (t4281).hashCode();
            
//#line 14 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final int t4284 =
              ((t4282) ^ (((int)(t4283))));
            
//#line 14 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
return t4284;
        }
        
        
//#line 19 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final public java.lang.String
                                                                                                           toString(
                                                                                                           ){
            
//#line 19 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final x10.mongo.yak.YakMap t4285 =
              ((x10.mongo.yak.YakMap)(yakMap));
            
//#line 19 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final java.lang.String t4286 =
              (("LoadedYakMap(") + (t4285));
            
//#line 19 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final java.lang.String t4287 =
              ((t4286) + ("-<"));
            
//#line 19 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final java.lang.String t4288 =
              ((java.lang.String)(newIndex));
            
//#line 19 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final java.lang.String t4289 =
              ((t4287) + (t4288));
            
//#line 19 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final java.lang.String t4290 =
              ((t4289) + (")"));
            
//#line 19 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
return t4290;
        }
        
        
//#line 24 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final public boolean
                                                                                                           equals(
                                                                                                           final java.lang.Object a){
            
//#line 24 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final boolean t4291 =
              x10.rtt.Equality.equalsequals((this),(a));
            
//#line 24 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
return t4291;
        }
        
        
//#line 29 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final public java.lang.String
                                                                                                           typeName(
                                                                                                           ){
            
//#line 29 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
return "x10.mongo.yak.LoadedYakMap";
        }
        
        
//#line 37 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final public x10.mongo.yak.YakMap
                                                                                                           operate(
                                                                                                           final java.lang.String cmd,
                                                                                                           final java.lang.Object v){
            
//#line 38 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final x10.mongo.yak.YakMap t4293 =
              ((x10.mongo.yak.YakMap)(yakMap));
            
//#line 38 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final java.lang.String t4294 =
              ((java.lang.String)(newIndex));
            
//#line 38 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final java.lang.Object t4292 =
              x10.mongo.yak.LoadedYakMap.javify(((java.lang.Object)(v)));
            
//#line 38 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final x10.mongo.yak.YakMap t4295 =
              ((x10.mongo.yak.YakMap)(new x10.mongo.yak.YakMap(((java.lang.String)(cmd)),
                                                               t4292)));
            
//#line 38 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
t4293.put(((java.lang.String)(t4294)),
                                                                                                                       ((java.lang.Object)(t4295)));
            
//#line 39 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final x10.mongo.yak.YakMap t4296 =
              ((x10.mongo.yak.YakMap)(yakMap));
            
//#line 39 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
return t4296;
        }
        
        
//#line 49 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final public x10.mongo.yak.YakMap
                                                                                                           $gt(
                                                                                                           final java.lang.Object v){
            
//#line 49 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final x10.mongo.yak.YakMap t4297 =
              ((x10.mongo.yak.YakMap)(this.operate(((java.lang.String)("$gt")),
                                                   ((java.lang.Object)(v)))));
            
//#line 49 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
return t4297;
        }
        
        
//#line 58 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final public x10.mongo.yak.YakMap
                                                                                                           $lt(
                                                                                                           final java.lang.Object v){
            
//#line 58 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final x10.mongo.yak.YakMap t4298 =
              ((x10.mongo.yak.YakMap)(this.operate(((java.lang.String)("$lt")),
                                                   ((java.lang.Object)(v)))));
            
//#line 58 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
return t4298;
        }
        
        
//#line 68 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final public x10.mongo.yak.YakMap
                                                                                                           $ge(
                                                                                                           final java.lang.Object v){
            
//#line 68 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final x10.mongo.yak.YakMap t4299 =
              ((x10.mongo.yak.YakMap)(this.operate(((java.lang.String)("$gte")),
                                                   ((java.lang.Object)(v)))));
            
//#line 68 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
return t4299;
        }
        
        
//#line 77 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final public x10.mongo.yak.YakMap
                                                                                                           $le(
                                                                                                           final java.lang.Object v){
            
//#line 77 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final x10.mongo.yak.YakMap t4300 =
              ((x10.mongo.yak.YakMap)(this.operate(((java.lang.String)("$lte")),
                                                   ((java.lang.Object)(v)))));
            
//#line 77 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
return t4300;
        }
        
        
//#line 86 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final public x10.mongo.yak.YakMap
                                                                                                           $lfunnel(
                                                                                                           final java.lang.Object v){
            
//#line 87 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final java.lang.Object vv =
              x10.mongo.yak.LoadedYakMap.javify(((java.lang.Object)(v)));
            
//#line 88 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final x10.mongo.yak.YakMap t4301 =
              ((x10.mongo.yak.YakMap)(yakMap));
            
//#line 88 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final java.lang.String t4302 =
              ((java.lang.String)(newIndex));
            
//#line 88 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
t4301.put(((java.lang.String)(t4302)),
                                                                                                                       ((java.lang.Object)(vv)));
            
//#line 89 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final x10.mongo.yak.YakMap t4303 =
              ((x10.mongo.yak.YakMap)(yakMap));
            
//#line 89 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
return t4303;
        }
        
        
//#line 98 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final public static java.lang.Object
                                                                                                           javify(
                                                                                                           final java.lang.Object a){
            
//#line 99 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final boolean t4317 =
              x10.rtt.Types.INT.isInstance(a);
            
//#line 99 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
java.lang.Object t4318 =
               null;
            
//#line 99 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
if (t4317) {
                
//#line 99 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final int t4304 =
                  x10.rtt.Types.asint(a,x10.rtt.Types.ANY);
                
//#line 99 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
t4318 = ((java.lang.Object)(new java.lang.Integer(((int)(t4304)))));
            } else {
                
//#line 100 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final boolean t4315 =
                  x10.rtt.Types.BOOLEAN.isInstance(a);
                
//#line 100 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
java.lang.Object t4316 =
                   null;
                
//#line 100 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
if (t4315) {
                    
//#line 100 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final boolean t4305 =
                      x10.rtt.Types.asboolean(a,x10.rtt.Types.ANY);
                    
//#line 100 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
t4316 = ((java.lang.Object)(new java.lang.Boolean(((boolean)(t4305)))));
                } else {
                    
//#line 101 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final boolean t4313 =
                      x10.rtt.Types.LONG.isInstance(a);
                    
//#line 101 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
java.lang.Object t4314 =
                       null;
                    
//#line 101 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
if (t4313) {
                        
//#line 101 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final long t4306 =
                          x10.rtt.Types.aslong(a,x10.rtt.Types.ANY);
                        
//#line 101 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
t4314 = ((java.lang.Object)(new java.lang.Long(((long)(t4306)))));
                    } else {
                        
//#line 102 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final boolean t4311 =
                          x10.rtt.Types.DOUBLE.isInstance(a);
                        
//#line 102 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
java.lang.Object t4312 =
                           null;
                        
//#line 102 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
if (t4311) {
                            
//#line 102 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final double t4307 =
                              x10.rtt.Types.asdouble(a,x10.rtt.Types.ANY);
                            
//#line 102 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
t4312 = ((java.lang.Object)(new java.lang.Double(((double)(t4307)))));
                        } else {
                            
//#line 103 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final boolean t4309 =
                              x10.rtt.Types.FLOAT.isInstance(a);
                            
//#line 103 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
java.lang.Object t4310 =
                               null;
                            
//#line 103 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
if (t4309) {
                                
//#line 103 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final float t4308 =
                                  x10.rtt.Types.asfloat(a,x10.rtt.Types.ANY);
                                
//#line 103 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
t4310 = ((java.lang.Object)(new java.lang.Float(((float)(t4308)))));
                            } else {
                                
//#line 103 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
t4310 = a;
                            }
                            
//#line 102 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
t4312 = t4310;
                        }
                        
//#line 101 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
t4314 = t4312;
                    }
                    
//#line 100 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
t4316 = t4314;
                }
                
//#line 99 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
t4318 = t4316;
            }
            
//#line 99 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final java.lang.Object t4319 =
              t4318;
            
//#line 99 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
return t4319;
        }
        
        
//#line 116 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final public x10.mongo.yak.YakMap
                                                                                                            $lfunnel(
                                                                                                            final int v){
            
//#line 116 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final java.lang.Integer t4320 =
              ((java.lang.Integer)(new java.lang.Integer(((int)(v)))));
            
//#line 116 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final x10.mongo.yak.YakMap t4321 =
              ((x10.mongo.yak.YakMap)(this.$lfunnel(((java.lang.Object)(t4320)))));
            
//#line 116 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
return t4321;
        }
        
        
//#line 124 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final public x10.mongo.yak.YakMap
                                                                                                            $lfunnel(
                                                                                                            final boolean v){
            
//#line 124 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final java.lang.Boolean t4322 =
              ((java.lang.Boolean)(new java.lang.Boolean(((boolean)(v)))));
            
//#line 124 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final x10.mongo.yak.YakMap t4323 =
              ((x10.mongo.yak.YakMap)(this.$lfunnel(((java.lang.Object)(t4322)))));
            
//#line 124 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
return t4323;
        }
        
        
//#line 132 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final public x10.mongo.yak.YakMap
                                                                                                            $lfunnel(
                                                                                                            final float v){
            
//#line 132 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final java.lang.Float t4324 =
              ((java.lang.Float)(new java.lang.Float(((float)(v)))));
            
//#line 132 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final x10.mongo.yak.YakMap t4325 =
              ((x10.mongo.yak.YakMap)(this.$lfunnel(((java.lang.Object)(t4324)))));
            
//#line 132 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
return t4325;
        }
        
        
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final public boolean
                                                                                                           _struct_equals$O(
                                                                                                           java.lang.Object other){
            
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final java.lang.Object t4326 =
              other;
            
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final boolean t4327 =
              x10.mongo.yak.LoadedYakMap.$RTT.isInstance(t4326);
            
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final boolean t4328 =
              !(t4327);
            
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
if (t4328) {
                
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
return false;
            }
            
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final java.lang.Object t4329 =
              other;
            
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final x10.mongo.yak.LoadedYakMap t4330 =
              ((x10.mongo.yak.LoadedYakMap)x10.rtt.Types.asStruct(x10.mongo.yak.LoadedYakMap.$RTT,t4329));
            
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final boolean t4331 =
              this._struct_equals$O(((x10.mongo.yak.LoadedYakMap)(t4330)));
            
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
return t4331;
        }
        
        
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final public boolean
                                                                                                           _struct_equals$O(
                                                                                                           x10.mongo.yak.LoadedYakMap other){
            
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final x10.mongo.yak.YakMap t4333 =
              ((x10.mongo.yak.YakMap)(this.
                                        yakMap));
            
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final x10.mongo.yak.LoadedYakMap t4332 =
              other;
            
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final x10.mongo.yak.YakMap t4334 =
              ((x10.mongo.yak.YakMap)(t4332.
                                        yakMap));
            
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
boolean t4338 =
              x10.rtt.Equality.equalsequals((t4333),(t4334));
            
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
if (t4338) {
                
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final java.lang.String t4336 =
                  ((java.lang.String)(this.
                                        newIndex));
                
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final x10.mongo.yak.LoadedYakMap t4335 =
                  other;
                
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final java.lang.String t4337 =
                  ((java.lang.String)(t4335.
                                        newIndex));
                
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
t4338 = x10.rtt.Equality.equalsequals((t4336),(t4337));
            }
            
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final boolean t4339 =
              t4338;
            
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
return t4339;
        }
        
        
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
final public x10.mongo.yak.LoadedYakMap
                                                                                                           x10$mongo$yak$LoadedYakMap$$x10$mongo$yak$LoadedYakMap$this(
                                                                                                           ){
            
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
return x10.mongo.yak.LoadedYakMap.this;
        }
        
        
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
// creation method for java code (1-phase java constructor)
        public LoadedYakMap(final x10.mongo.yak.YakMap yakMap,
                            final java.lang.String newIndex){this((java.lang.System[]) null);
                                                                 x10$mongo$yak$LoadedYakMap$$init$S(yakMap,newIndex);}
        
        // constructor for non-virtual call
        final public x10.mongo.yak.LoadedYakMap x10$mongo$yak$LoadedYakMap$$init$S(final x10.mongo.yak.YakMap yakMap,
                                                                                   final java.lang.String newIndex) { {
                                                                                                                             
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
;
                                                                                                                             
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/LoadedYakMap.x10"
this.yakMap = yakMap;
                                                                                                                             this.newIndex = newIndex;
                                                                                                                             
                                                                                                                         }
                                                                                                                         return this;
                                                                                                                         }
        
    
}
