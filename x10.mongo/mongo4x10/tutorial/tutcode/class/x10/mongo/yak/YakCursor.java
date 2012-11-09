package x10.mongo.yak;


@x10.core.X10Generated public class YakCursor extends x10.core.Ref implements x10.lang.Iterable, x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, YakCursor.class);
    
    public static final x10.rtt.RuntimeType<YakCursor> $RTT = x10.rtt.NamedType.<YakCursor> make(
    "x10.mongo.yak.YakCursor", /* base class */YakCursor.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Iterable.$RTT, x10.mongo.yak.YakMap.$RTT), x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(YakCursor $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + YakCursor.class + " calling"); } 
        com.mongodb.DBCursor original = (com.mongodb.DBCursor) $deserializer.readRefUsingReflection();
        $_obj.original = original;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        YakCursor $_obj = new YakCursor((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.writeObjectUsingReflection(this.original);
        
    }
    
    // constructor just for allocation
    public YakCursor(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 24 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public int
                                                                                                        hashCode(
                                                                                                        ){
            
//#line 24 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4342 =
              ((com.mongodb.DBCursor)(original));
            
//#line 24 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final int t4343 =
              x10.rtt.Types.hashCode(t4342);
            
//#line 24 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4343;
        }
        
        
//#line 29 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public boolean
                                                                                                        equals(
                                                                                                        final java.lang.Object a){
            
//#line 29 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4344 =
              ((com.mongodb.DBCursor)(original));
            
//#line 29 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final boolean t4345 =
              ((java.lang.Object)(t4344)).equals(a);
            
//#line 29 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4345;
        }
        
        
//#line 35 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
/**
   * The wrapped object.
   */
        public com.mongodb.DBCursor original;
        
        
//#line 41 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
// creation method for java code (1-phase java constructor)
        public YakCursor(final com.mongodb.DBCursor orig){this((java.lang.System[]) null);
                                                              x10$mongo$yak$YakCursor$$init$S(orig);}
        
        // constructor for non-virtual call
        final public x10.mongo.yak.YakCursor x10$mongo$yak$YakCursor$$init$S(final com.mongodb.DBCursor orig) { {
                                                                                                                       
//#line 41 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"

                                                                                                                       
//#line 41 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"

                                                                                                                       
//#line 42 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
this.original = ((com.mongodb.DBCursor)(orig));
                                                                                                                   }
                                                                                                                   return this;
                                                                                                                   }
        
        
        
//#line 46 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public static x10.mongo.yak.YakUtil y;
        
        
//#line 57 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public x10.mongo.yak.YakMap
                                                                                                        explain(
                                                                                                        ){
            
//#line 57 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final x10.mongo.yak.YakUtil t4347 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCursor.get$y()));
            
//#line 57 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4346 =
              ((com.mongodb.DBCursor)(original));
            
//#line 57 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBObject t4348 =
              t4346.explain();
            
//#line 57 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final x10.mongo.yak.YakMap t4349 =
              t4347.$apply(((com.mongodb.DBObject)(t4348)));
            
//#line 57 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4349;
        }
        
        
//#line 64 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public x10.mongo.yak.YakCursor
                                                                                                        snapshot(
                                                                                                        ){
            
//#line 64 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4350 =
              ((com.mongodb.DBCursor)(original));
            
//#line 64 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4351 =
              t4350.snapshot();
            
//#line 64 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final x10.mongo.yak.YakCursor t4352 =
              ((x10.mongo.yak.YakCursor)(new x10.mongo.yak.YakCursor((java.lang.System[]) null).x10$mongo$yak$YakCursor$$init$S(t4351)));
            
//#line 64 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4352;
        }
        
        
//#line 72 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public x10.mongo.yak.YakCursor
                                                                                                        copy(
                                                                                                        ){
            
//#line 72 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4353 =
              ((com.mongodb.DBCursor)(original));
            
//#line 72 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4354 =
              t4353.copy();
            
//#line 72 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final x10.mongo.yak.YakCursor t4355 =
              ((x10.mongo.yak.YakCursor)(new x10.mongo.yak.YakCursor((java.lang.System[]) null).x10$mongo$yak$YakCursor$$init$S(t4354)));
            
//#line 72 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4355;
        }
        
        
//#line 82 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public x10.mongo.yak.YakCursor
                                                                                                        sort(
                                                                                                        final com.mongodb.DBObject sorting){
            
//#line 83 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4356 =
              ((com.mongodb.DBCursor)(original));
            
//#line 83 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
t4356.sort(((com.mongodb.DBObject)(sorting)));
            
//#line 84 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return this;
        }
        
        
//#line 99 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public x10.mongo.yak.YakCursor
                                                                                                        addSpecial(
                                                                                                        final java.lang.String specialName,
                                                                                                        final java.lang.Object value){
            
//#line 99 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4357 =
              ((com.mongodb.DBCursor)(original));
            
//#line 99 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
t4357.addSpecial(((java.lang.String)(specialName)),
                                                                                                                           ((java.lang.Object)(value)));
            
//#line 99 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return this;
        }
        
        
//#line 108 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public x10.mongo.yak.YakCursor
                                                                                                         hint(
                                                                                                         final com.mongodb.DBObject hint){
            
//#line 108 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4358 =
              ((com.mongodb.DBCursor)(original));
            
//#line 108 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
t4358.hint(((com.mongodb.DBObject)(hint)));
            
//#line 108 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return this;
        }
        
        
//#line 117 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public x10.mongo.yak.YakCursor
                                                                                                         hint(
                                                                                                         final java.lang.String indexName){
            
//#line 117 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4359 =
              ((com.mongodb.DBCursor)(original));
            
//#line 117 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
t4359.hint(((java.lang.String)(indexName)));
            
//#line 117 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return this;
        }
        
        
//#line 125 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public x10.mongo.yak.YakCursor
                                                                                                         limit(
                                                                                                         final int limit){
            
//#line 125 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4360 =
              ((com.mongodb.DBCursor)(original));
            
//#line 125 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
t4360.limit((int)(limit));
            
//#line 125 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return this;
        }
        
        
//#line 132 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public x10.mongo.yak.YakCursor
                                                                                                         batchSize(
                                                                                                         final int size){
            
//#line 132 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4361 =
              ((com.mongodb.DBCursor)(original));
            
//#line 132 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
t4361.batchSize((int)(size));
            
//#line 132 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return this;
        }
        
        
//#line 140 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public x10.mongo.yak.YakCursor
                                                                                                         skip(
                                                                                                         final int nIgnored){
            
//#line 140 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4362 =
              ((com.mongodb.DBCursor)(original));
            
//#line 140 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
t4362.skip((int)(nIgnored));
            
//#line 140 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return this;
        }
        
        
//#line 146 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public long
                                                                                                         getCursorId$O(
                                                                                                         ){
            
//#line 146 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4363 =
              ((com.mongodb.DBCursor)(original));
            
//#line 146 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final long t4364 =
              t4363.getCursorId();
            
//#line 146 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4364;
        }
        
        
//#line 151 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public void
                                                                                                         close(
                                                                                                         ){
            
//#line 151 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4365 =
              ((com.mongodb.DBCursor)(original));
            
//#line 151 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
t4365.close();
        }
        
        
//#line 158 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public x10.mongo.yak.YakCursor
                                                                                                         slaveOk(
                                                                                                         ){
            
//#line 158 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4366 =
              ((com.mongodb.DBCursor)(original));
            
//#line 158 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
t4366.slaveOk();
            
//#line 158 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return this;
        }
        
        
//#line 166 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public x10.mongo.yak.YakCursor
                                                                                                         addOption(
                                                                                                         final int option){
            
//#line 166 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4367 =
              ((com.mongodb.DBCursor)(original));
            
//#line 166 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
t4367.addOption((int)(option));
            
//#line 166 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return this;
        }
        
        
//#line 174 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public x10.mongo.yak.YakCursor
                                                                                                         setOptions(
                                                                                                         final int options){
            
//#line 174 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4368 =
              ((com.mongodb.DBCursor)(original));
            
//#line 174 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
t4368.setOptions((int)(options));
            
//#line 174 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return this;
        }
        
        
//#line 181 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public x10.mongo.yak.YakCursor
                                                                                                         resetOptions(
                                                                                                         ){
            
//#line 181 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4369 =
              ((com.mongodb.DBCursor)(original));
            
//#line 181 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
t4369.resetOptions();
            
//#line 181 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return this;
        }
        
        
//#line 188 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public int
                                                                                                         getOptions$O(
                                                                                                         ){
            
//#line 188 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4370 =
              ((com.mongodb.DBCursor)(original));
            
//#line 188 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final int t4371 =
              t4370.getOptions();
            
//#line 188 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4371;
        }
        
        
//#line 194 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public int
                                                                                                         numGetMores$O(
                                                                                                         ){
            
//#line 194 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4372 =
              ((com.mongodb.DBCursor)(original));
            
//#line 194 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final int t4373 =
              t4372.numGetMores();
            
//#line 194 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4373;
        }
        
        
//#line 199 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public x10.util.List
                                                                                                         getSizes(
                                                                                                         ){
            
//#line 199 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4374 =
              ((com.mongodb.DBCursor)(original));
            
//#line 199 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final java.util.List t4375 =
              t4374.getSizes();
            
//#line 199 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final x10.util.List t4376 =
              x10.mongo.yak.YakUtil.convertIntList(((java.util.List)(t4375)));
            
//#line 199 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4376;
        }
        
        
//#line 204 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public int
                                                                                                         numSeen$O(
                                                                                                         ){
            
//#line 204 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4377 =
              ((com.mongodb.DBCursor)(original));
            
//#line 204 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final int t4378 =
              t4377.numSeen();
            
//#line 204 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4378;
        }
        
        
//#line 209 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public boolean
                                                                                                         hasNext$O(
                                                                                                         ){
            
//#line 209 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4379 =
              ((com.mongodb.DBCursor)(original));
            
//#line 209 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final boolean t4380 =
              t4379.hasNext();
            
//#line 209 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4380;
        }
        
        
//#line 214 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public x10.mongo.yak.YakMap
                                                                                                         next(
                                                                                                         ){
            
//#line 214 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final x10.mongo.yak.YakUtil t4382 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCursor.get$y()));
            
//#line 214 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4381 =
              ((com.mongodb.DBCursor)(original));
            
//#line 214 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBObject t4383 =
              t4381.next();
            
//#line 214 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final x10.mongo.yak.YakMap t4384 =
              t4382.$apply(((com.mongodb.DBObject)(t4383)));
            
//#line 214 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4384;
        }
        
        
//#line 219 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public com.mongodb.DBObject
                                                                                                         curr(
                                                                                                         ){
            
//#line 219 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4385 =
              ((com.mongodb.DBCursor)(original));
            
//#line 219 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBObject t4386 =
              t4385.curr();
            
//#line 219 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4386;
        }
        
        
//#line 225 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public void
                                                                                                         remove(
                                                                                                         ){
            
//#line 225 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4387 =
              ((com.mongodb.DBCursor)(original));
            
//#line 225 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
t4387.remove();
        }
        
        
//#line 231 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public int
                                                                                                         length$O(
                                                                                                         ){
            
//#line 231 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4388 =
              ((com.mongodb.DBCursor)(original));
            
//#line 231 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final int t4389 =
              t4388.length();
            
//#line 231 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4389;
        }
        
        
//#line 238 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public java.util.List
                                                                                                         toArray(
                                                                                                         ){
            
//#line 238 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4390 =
              ((com.mongodb.DBCursor)(original));
            
//#line 238 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final java.util.List t4391 =
              t4390.toArray();
            
//#line 238 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4391;
        }
        
        
//#line 246 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public x10.util.List
                                                                                                         toX10List(
                                                                                                         ){
            
//#line 246 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4392 =
              ((com.mongodb.DBCursor)(original));
            
//#line 246 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final java.util.List t4393 =
              t4392.toArray();
            
//#line 246 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final x10.util.List t4394 =
              x10.mongo.yak.YakUtil.convertListOfDBObjects(((java.util.List)(t4393)));
            
//#line 246 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4394;
        }
        
        
//#line 252 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public java.util.List
                                                                                                         toArray(
                                                                                                         final int max){
            
//#line 252 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4395 =
              ((com.mongodb.DBCursor)(original));
            
//#line 252 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final java.util.List t4396 =
              t4395.toArray((int)(max));
            
//#line 252 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4396;
        }
        
        
//#line 260 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public int
                                                                                                         count$O(
                                                                                                         ){
            
//#line 260 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4397 =
              ((com.mongodb.DBCursor)(original));
            
//#line 260 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final int t4398 =
              t4397.count();
            
//#line 260 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4398;
        }
        
        
//#line 266 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public int
                                                                                                         size$O(
                                                                                                         ){
            
//#line 266 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4399 =
              ((com.mongodb.DBCursor)(original));
            
//#line 266 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final int t4400 =
              t4399.size();
            
//#line 266 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4400;
        }
        
        
//#line 272 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public x10.mongo.yak.YakMap
                                                                                                         getKeysWanted(
                                                                                                         ){
            
//#line 272 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final x10.mongo.yak.YakUtil t4402 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCursor.get$y()));
            
//#line 272 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4401 =
              ((com.mongodb.DBCursor)(original));
            
//#line 272 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBObject t4403 =
              t4401.getKeysWanted();
            
//#line 272 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final x10.mongo.yak.YakMap t4404 =
              t4402.$apply(((com.mongodb.DBObject)(t4403)));
            
//#line 272 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4404;
        }
        
        
//#line 278 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public x10.mongo.yak.YakMap
                                                                                                         getQuery(
                                                                                                         ){
            
//#line 278 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final x10.mongo.yak.YakUtil t4406 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCursor.get$y()));
            
//#line 278 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4405 =
              ((com.mongodb.DBCursor)(original));
            
//#line 278 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBObject t4407 =
              t4405.getQuery();
            
//#line 278 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final x10.mongo.yak.YakMap t4408 =
              t4406.$apply(((com.mongodb.DBObject)(t4407)));
            
//#line 278 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4408;
        }
        
        
//#line 282 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public x10.mongo.yak.YakCollection
                                                                                                         getCollection(
                                                                                                         ){
            
//#line 282 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final x10.mongo.yak.YakUtil t4410 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCursor.get$y()));
            
//#line 282 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4409 =
              ((com.mongodb.DBCursor)(original));
            
//#line 282 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCollection t4411 =
              t4409.getCollection();
            
//#line 282 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final x10.mongo.yak.YakCollection t4412 =
              t4410.$apply(((com.mongodb.DBCollection)(t4411)));
            
//#line 282 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4412;
        }
        
        
//#line 289 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public com.mongodb.ServerAddress
                                                                                                         getServerAddress(
                                                                                                         ){
            
//#line 289 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4413 =
              ((com.mongodb.DBCursor)(original));
            
//#line 289 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.ServerAddress t4414 =
              t4413.getServerAddress();
            
//#line 289 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4414;
        }
        
        
//#line 295 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public x10.mongo.yak.YakCursor
                                                                                                         setReadPreference(
                                                                                                         final com.mongodb.ReadPreference prefs){
            
//#line 295 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4415 =
              ((com.mongodb.DBCursor)(original));
            
//#line 295 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
t4415.setReadPreference(((com.mongodb.ReadPreference)(prefs)));
            
//#line 295 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return this;
        }
        
        
//#line 300 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public com.mongodb.ReadPreference
                                                                                                         getReadPreference(
                                                                                                         ){
            
//#line 300 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4416 =
              ((com.mongodb.DBCursor)(original));
            
//#line 300 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.ReadPreference t4417 =
              t4416.getReadPreference();
            
//#line 300 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4417;
        }
        
        
//#line 306 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public x10.mongo.yak.YakCursor
                                                                                                         setDecoderFactory(
                                                                                                         final com.mongodb.DBDecoderFactory factory){
            
//#line 306 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4418 =
              ((com.mongodb.DBCursor)(original));
            
//#line 306 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
t4418.setDecoderFactory(((com.mongodb.DBDecoderFactory)(factory)));
            
//#line 306 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return this;
        }
        
        
//#line 310 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public com.mongodb.DBDecoderFactory
                                                                                                         getDecoderFactory(
                                                                                                         ){
            
//#line 310 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4419 =
              ((com.mongodb.DBCursor)(original));
            
//#line 310 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBDecoderFactory t4420 =
              t4419.getDecoderFactory();
            
//#line 310 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4420;
        }
        
        
//#line 314 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public java.lang.String
                                                                                                         toString(
                                                                                                         ){
            
//#line 314 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4421 =
              ((com.mongodb.DBCursor)(original));
            
//#line 314 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final java.lang.String t4422 =
              t4421.toString();
            
//#line 314 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4422;
        }
        
        
//#line 320 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public x10.lang.Iterator
                                                                                                         iterator(
                                                                                                         ){
            
//#line 321 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4423 =
              ((com.mongodb.DBCursor)(original));
            
//#line 321 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final java.util.Iterator origerator =
              t4423.iterator();
            
//#line 322 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final x10.mongo.yak.YakCursor.Anonymous$12424 t4424 =
              ((x10.mongo.yak.YakCursor.Anonymous$12424)(new x10.mongo.yak.YakCursor.Anonymous$12424((java.lang.System[]) null).x10$mongo$yak$YakCursor$Anonymous$12424$$init$S(this,
                                                                                                                                                                                origerator)));
            
//#line 322 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4424;
        }
        
        
//#line 332 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public x10.lang.Iterator
                                                                                                         anyIterator(
                                                                                                         ){
            
//#line 333 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBCursor t4425 =
              ((com.mongodb.DBCursor)(original));
            
//#line 333 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final java.util.Iterator origerator =
              t4425.iterator();
            
//#line 334 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final x10.mongo.yak.YakCursor.Anonymous$12853 t4426 =
              ((x10.mongo.yak.YakCursor.Anonymous$12853)(new x10.mongo.yak.YakCursor.Anonymous$12853((java.lang.System[]) null).x10$mongo$yak$YakCursor$Anonymous$12853$$init$S(this,
                                                                                                                                                                                origerator)));
            
//#line 334 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4426;
        }
        
        
//#line 19 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final public x10.mongo.yak.YakCursor
                                                                                                        x10$mongo$yak$YakCursor$$x10$mongo$yak$YakCursor$this(
                                                                                                        ){
            
//#line 19 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return x10.mongo.yak.YakCursor.this;
        }
        
        
//#line 322 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
@x10.core.X10Generated final public static class Anonymous$12424 extends x10.core.Ref implements x10.lang.Iterator, x10.x10rt.X10JavaSerializable
                                                                                                       {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Anonymous$12424.class);
            
            public static final x10.rtt.RuntimeType<Anonymous$12424> $RTT = x10.rtt.NamedType.<Anonymous$12424> make(
            "x10.mongo.yak.YakCursor.Anonymous$12424", /* base class */Anonymous$12424.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Iterator.$RTT, x10.mongo.yak.YakMap.$RTT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(Anonymous$12424 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Anonymous$12424.class + " calling"); } 
                java.util.Iterator origerator = (java.util.Iterator) $deserializer.readRefUsingReflection();
                $_obj.origerator = origerator;
                x10.mongo.yak.YakCursor out$ = (x10.mongo.yak.YakCursor) $deserializer.readRef();
                $_obj.out$ = out$;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                Anonymous$12424 $_obj = new Anonymous$12424((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.writeObjectUsingReflection(this.origerator);
                if (out$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$);
                } else {
                $serializer.write(this.out$);
                }
                
            }
            
            // constructor just for allocation
            public Anonymous$12424(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // bridge for method abstract public x10.lang.Iterator.next():T
            public x10.mongo.yak.YakMap
              next$G(){return next();}
            
                
//#line 19 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public x10.mongo.yak.YakCursor out$;
                
//#line 321 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public java.util.Iterator origerator;
                
                
//#line 323 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public boolean
                                                                                                                 hasNext$O(
                                                                                                                 ){
                    
//#line 323 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final java.util.Iterator t4427 =
                      x10.mongo.yak.YakCursor.Anonymous$12424.this.
                        origerator;
                    
//#line 323 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final boolean t4428 =
                      t4427.hasNext();
                    
//#line 323 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4428;
                }
                
                
//#line 324 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public x10.mongo.yak.YakMap
                                                                                                                 next(
                                                                                                                 ){
                    
//#line 324 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final x10.mongo.yak.YakUtil t4431 =
                      ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCursor.get$y()));
                    
//#line 324 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final java.util.Iterator t4429 =
                      x10.mongo.yak.YakCursor.Anonymous$12424.this.
                        origerator;
                    
//#line 324 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final java.lang.Object t4430 =
                      t4429.next();
                    
//#line 324 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final com.mongodb.DBObject t4432 =
                      x10.rtt.Types.<com.mongodb.DBObject> cast(t4430,x10.rtt.Types.getRTT(com.mongodb.DBObject.class));
                    
//#line 324 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final x10.mongo.yak.YakMap t4433 =
                      t4431.$apply(((com.mongodb.DBObject)(t4432)));
                    
//#line 324 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4433;
                }
                
                
//#line 322 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
// creation method for java code (1-phase java constructor)
                public Anonymous$12424(final x10.mongo.yak.YakCursor out$,
                                       final java.util.Iterator origerator){this((java.lang.System[]) null);
                                                                                x10$mongo$yak$YakCursor$Anonymous$12424$$init$S(out$,origerator);}
                
                // constructor for non-virtual call
                final public x10.mongo.yak.YakCursor.Anonymous$12424 x10$mongo$yak$YakCursor$Anonymous$12424$$init$S(final x10.mongo.yak.YakCursor out$,
                                                                                                                     final java.util.Iterator origerator) { {
                                                                                                                                                                   
//#line 322 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"

                                                                                                                                                                   
//#line 19 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
this.out$ = out$;
                                                                                                                                                                   
//#line 321 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
this.origerator = origerator;
                                                                                                                                                               }
                                                                                                                                                               return this;
                                                                                                                                                               }
                
            
        }
        
        
//#line 334 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
@x10.core.X10Generated final public static class Anonymous$12853 extends x10.core.Ref implements x10.lang.Iterator, x10.x10rt.X10JavaSerializable
                                                                                                       {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Anonymous$12853.class);
            
            public static final x10.rtt.RuntimeType<Anonymous$12853> $RTT = x10.rtt.NamedType.<Anonymous$12853> make(
            "x10.mongo.yak.YakCursor.Anonymous$12853", /* base class */Anonymous$12853.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Iterator.$RTT, x10.rtt.Types.ANY), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(Anonymous$12853 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Anonymous$12853.class + " calling"); } 
                java.util.Iterator origerator = (java.util.Iterator) $deserializer.readRefUsingReflection();
                $_obj.origerator = origerator;
                x10.mongo.yak.YakCursor out$ = (x10.mongo.yak.YakCursor) $deserializer.readRef();
                $_obj.out$ = out$;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                Anonymous$12853 $_obj = new Anonymous$12853((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.writeObjectUsingReflection(this.origerator);
                if (out$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$);
                } else {
                $serializer.write(this.out$);
                }
                
            }
            
            // constructor just for allocation
            public Anonymous$12853(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // bridge for method abstract public x10.lang.Iterator.next():T
            public java.lang.Object
              next$G(){return next();}
            
                
//#line 19 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public x10.mongo.yak.YakCursor out$;
                
//#line 333 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public java.util.Iterator origerator;
                
                
//#line 335 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public boolean
                                                                                                                 hasNext$O(
                                                                                                                 ){
                    
//#line 335 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final java.util.Iterator t4434 =
                      x10.mongo.yak.YakCursor.Anonymous$12853.this.
                        origerator;
                    
//#line 335 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final boolean t4435 =
                      t4434.hasNext();
                    
//#line 335 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4435;
                }
                
                
//#line 336 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
public java.lang.Object
                                                                                                                 next(
                                                                                                                 ){
                    
//#line 336 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final java.util.Iterator t4436 =
                      x10.mongo.yak.YakCursor.Anonymous$12853.this.
                        origerator;
                    
//#line 336 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
final java.lang.Object t4437 =
                      t4436.next();
                    
//#line 336 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
return t4437;
                }
                
                
//#line 334 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
// creation method for java code (1-phase java constructor)
                public Anonymous$12853(final x10.mongo.yak.YakCursor out$,
                                       final java.util.Iterator origerator){this((java.lang.System[]) null);
                                                                                x10$mongo$yak$YakCursor$Anonymous$12853$$init$S(out$,origerator);}
                
                // constructor for non-virtual call
                final public x10.mongo.yak.YakCursor.Anonymous$12853 x10$mongo$yak$YakCursor$Anonymous$12853$$init$S(final x10.mongo.yak.YakCursor out$,
                                                                                                                     final java.util.Iterator origerator) { {
                                                                                                                                                                   
//#line 334 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"

                                                                                                                                                                   
//#line 19 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
this.out$ = out$;
                                                                                                                                                                   
//#line 333 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCursor.x10"
this.origerator = origerator;
                                                                                                                                                               }
                                                                                                                                                               return this;
                                                                                                                                                               }
                
            
        }
        
        public static short fieldId$y;
        final public static x10.core.concurrent.AtomicInteger initStatus$y = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        
        public static void
          getDeserialized$y(
          x10.x10rt.X10JavaDeserializer deserializer){
            x10.mongo.yak.YakCursor.y = ((x10.mongo.yak.YakUtil)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
            x10.mongo.yak.YakCursor.initStatus$y.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static x10.mongo.yak.YakUtil
          get$y(
          ){
            if (((int) x10.mongo.yak.YakCursor.initStatus$y.get()) ==
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                return x10.mongo.yak.YakCursor.y;
            }
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0) &&
                  x10.mongo.yak.YakCursor.initStatus$y.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                     (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                x10.mongo.yak.YakCursor.y = x10.mongo.yak.YakUtil.it();
                if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                      ((boolean) true)) {
                    x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.mongo.yak.YakCursor.y")));
                }
                x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.mongo.yak.YakCursor.y)),
                                                                          (short)(x10.mongo.yak.YakCursor.fieldId$y));
                x10.mongo.yak.YakCursor.initStatus$y.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                x10.runtime.impl.java.InitDispatcher.notifyInitialized();
            } else {
                if (((int) x10.mongo.yak.YakCursor.initStatus$y.get()) !=
                    ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    while (((int) x10.mongo.yak.YakCursor.initStatus$y.get()) !=
                           ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                    }
                    x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                }
            }
            return x10.mongo.yak.YakCursor.y;
        }
        
        static {
                   x10.mongo.yak.YakCursor.fieldId$y = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.mongo.yak.YakCursor")),
                                                                                                                           ((java.lang.String)("y")))))));
               }
    
}
