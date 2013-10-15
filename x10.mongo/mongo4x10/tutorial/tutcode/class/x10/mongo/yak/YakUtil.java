package x10.mongo.yak;


@x10.core.X10Generated public class YakUtil extends x10.core.Ref implements x10.io.CustomSerialization
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, YakUtil.class);
    
    public static final x10.rtt.RuntimeType<YakUtil> $RTT = x10.rtt.NamedType.<YakUtil> make(
    "x10.mongo.yak.YakUtil", /* base class */YakUtil.class
    , /* parents */ new x10.rtt.Type[] {x10.io.CustomSerialization.$RTT, x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    // custom serializer
    private transient x10.io.SerialData $$serialdata;
    private Object writeReplace() { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " calling"); } $$serialdata = serialize(); if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " returned " + $$serialdata); } return this; }
    private Object readResolve() { return new YakUtil($$serialdata); }
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
    oos.writeObject($$serialdata); }
    private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {
    $$serialdata = (x10.io.SerialData) ois.readObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(YakUtil $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("X10JavaSerializable: $_deserialize_body() of " + YakUtil.class + " calling"); } 
        x10.io.SerialData $$serialdata = (x10.io.SerialData) $deserializer.readRef();
        $_obj.x10$mongo$yak$YakUtil$$init$S($$serialdata);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        YakUtil $_obj = new YakUtil((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println(" CustomSerialization : $_serialize of " + this + " calling"); } 
        $$serialdata = serialize(); 
        $serializer.write($$serialdata);
        
    }
    
    // constructor just for allocation
    public YakUtil(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 39 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public int
                                                                                                      hashCode(
                                                                                                      ){
            
//#line 39 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return 1;
        }
        
        
//#line 43 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public java.lang.String
                                                                                                      toString(
                                                                                                      ){
            
//#line 43 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return "(YakUtil)";
        }
        
        
//#line 48 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public boolean
                                                                                                      equals(
                                                                                                      final java.lang.Object a){
            
//#line 48 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final boolean t2752 =
              x10.rtt.Equality.equalsequals((this),(a));
            
//#line 48 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2752;
        }
        
        
//#line 54 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
/**
   * A debugging flag
   */
        final public static boolean CreateNewMongoEveryTime = false;
        
        
//#line 58 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
// creation method for java code (1-phase java constructor)
        public YakUtil(){this((java.lang.System[]) null);
                             x10$mongo$yak$YakUtil$$init$S();}
        
        // constructor for non-virtual call
        final public x10.mongo.yak.YakUtil x10$mongo$yak$YakUtil$$init$S() { {
                                                                                    
//#line 58 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"

                                                                                    
//#line 58 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"

                                                                                }
                                                                                return this;
                                                                                }
        
        
        
//#line 61 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.lang.PlaceLocalHandle<x10.mongo.yak.YakUtil> them;
        
        
//#line 69 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public x10.io.SerialData
                                                                                                      serialize(
                                                                                                      ){
            
//#line 70 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.io.SerialData t2753 =
              ((x10.io.SerialData)(new x10.io.SerialData((java.lang.System[]) null).x10$io$SerialData$$init$S(((java.lang.Object)(null)),
                                                                                                              ((x10.io.SerialData)(null)))));
            
//#line 70 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2753;
        }
        
        
//#line 76 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
// creation method for java code (1-phase java constructor)
        public YakUtil(final x10.io.SerialData id$0){this((java.lang.System[]) null);
                                                         x10$mongo$yak$YakUtil$$init$S(id$0);}
        
        // constructor for non-virtual call
        final public x10.mongo.yak.YakUtil x10$mongo$yak$YakUtil$$init$S(final x10.io.SerialData id$0) {x10$mongo$yak$YakUtil$init_for_reflection(id$0);
                                                                                                            
                                                                                                            return this;
                                                                                                            }
        public void x10$mongo$yak$YakUtil$init_for_reflection(x10.io.SerialData id$0) {
             {
                
//#line 76 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"

                
//#line 76 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"

            }}
            
        
        
//#line 83 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakUtil
                                                                                                      it(
                                                                                                      ){
            
//#line 83 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.lang.PlaceLocalHandle t2754 =
              ((x10.lang.PlaceLocalHandle)(x10.mongo.yak.YakUtil.get$them()));
            
//#line 83 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakUtil t2755 =
              ((x10.lang.PlaceLocalHandle<x10.mongo.yak.YakUtil>)t2754).$apply$G();
            
//#line 83 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2755;
        }
        
        
//#line 88 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public x10.mongo.yak.LoadedYakMap
                                                                                                      $funnel(
                                                                                                      final java.lang.String s){
            
//#line 88 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2756 =
              ((x10.mongo.yak.YakMap)(new x10.mongo.yak.YakMap()));
            
//#line 88 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.LoadedYakMap t2757 =
              ((x10.mongo.yak.LoadedYakMap)(t2756.$funnel(((java.lang.String)(s)))));
            
//#line 88 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2757;
        }
        
        
//#line 93 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public x10.mongo.yak.YakMap
                                                                                                      $apply(
                                                                                                      final org.bson.BasicBSONObject b){
            
//#line 93 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2758 =
              x10.mongo.yak.YakMap.yakifyTopLevel(((org.bson.BasicBSONObject)(b)));
            
//#line 93 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2758;
        }
        
        
//#line 97 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public x10.mongo.yak.YakMap
                                                                                                      $apply(
                                                                                                      final com.mongodb.DBObject b){
            
//#line 97 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2759 =
              x10.mongo.yak.YakMap.yakifyTopLevel(((com.mongodb.DBObject)(b)));
            
//#line 97 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2759;
        }
        
        
//#line 101 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public x10.mongo.yak.YakCollection
                                                                                                       $apply(
                                                                                                       final com.mongodb.DBCollection x){
            
//#line 101 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakCollection t2760 =
              x10.mongo.yak.YakCollection.make(((com.mongodb.DBCollection)(x)));
            
//#line 101 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2760;
        }
        
        
//#line 105 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public x10.mongo.yak.YakCursor
                                                                                                       $apply(
                                                                                                       final com.mongodb.DBCursor x){
            
//#line 105 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakCursor t2761 =
              ((x10.mongo.yak.YakCursor)(new x10.mongo.yak.YakCursor((java.lang.System[]) null).x10$mongo$yak$YakCursor$$init$S(((com.mongodb.DBCursor)(x)))));
            
//#line 105 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2761;
        }
        
        
//#line 110 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public x10.mongo.yak.YakMap
                                                                                                       $apply(
                                                                                                       final x10.mongo.yak.YakMap x){
            
//#line 110 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return x;
        }
        
        
//#line 114 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public x10.mongo.yak.YakCursor
                                                                                                       $apply(
                                                                                                       final x10.mongo.yak.YakCursor x){
            
//#line 114 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return x;
        }
        
        
//#line 118 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public x10.mongo.yak.YakCollection
                                                                                                       $apply(
                                                                                                       final x10.mongo.yak.YakCollection x){
            
//#line 118 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return x;
        }
        
        
//#line 120 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public x10.mongo.yak.YakMapReduceOutput
                                                                                                       $apply(
                                                                                                       final com.mongodb.MapReduceOutput x){
            
//#line 120 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMapReduceOutput t2762 =
              ((x10.mongo.yak.YakMapReduceOutput)(new x10.mongo.yak.YakMapReduceOutput((java.lang.System[]) null).x10$mongo$yak$YakMapReduceOutput$$init$S(((com.mongodb.MapReduceOutput)(x)))));
            
//#line 120 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2762;
        }
        
        
//#line 125 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       empty(
                                                                                                       ){
            
//#line 125 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2763 =
              ((x10.mongo.yak.YakMap)(new x10.mongo.yak.YakMap()));
            
//#line 125 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2763;
        }
        
        
//#line 131 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final public x10.mongo.yak.YakMap
                                                                                                       $apply(
                                                                                                       ){
            
//#line 131 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2764 =
              ((x10.mongo.yak.YakMap)(new x10.mongo.yak.YakMap()));
            
//#line 131 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2764;
        }
        
        
//#line 137 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakList
                                                                                                       nil(
                                                                                                       ){
            
//#line 137 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakList t2765 =
              ((x10.mongo.yak.YakList)(new x10.mongo.yak.YakList()));
            
//#line 137 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2765;
        }
        
        
//#line 143 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakList
                                                                                                       list(
                                                                                                       ){
            
//#line 143 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakList t2766 =
              ((x10.mongo.yak.YakList)(new x10.mongo.yak.YakList()));
            
//#line 143 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2766;
        }
        
        
//#line 148 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakList
                                                                                                       list(
                                                                                                       final java.lang.Object a){
            
//#line 148 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakList t2767 =
              ((x10.mongo.yak.YakList)(new x10.mongo.yak.YakList()));
            
//#line 148 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakList t2768 =
              ((x10.mongo.yak.YakList)(t2767.$ampersand(((java.lang.Object)(a)))));
            
//#line 148 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2768;
        }
        
        
//#line 154 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakList
                                                                                                       list(
                                                                                                       final java.lang.Object a,
                                                                                                       final java.lang.Object b){
            
//#line 154 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakList t2769 =
              ((x10.mongo.yak.YakList)(new x10.mongo.yak.YakList()));
            
//#line 154 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakList t2770 =
              ((x10.mongo.yak.YakList)(t2769.$ampersand(((java.lang.Object)(a)))));
            
//#line 154 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakList t2771 =
              ((x10.mongo.yak.YakList)(t2770.$ampersand(((java.lang.Object)(b)))));
            
//#line 154 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2771;
        }
        
        
//#line 161 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakList
                                                                                                       list(
                                                                                                       final java.lang.Object a,
                                                                                                       final java.lang.Object b,
                                                                                                       final java.lang.Object c){
            
//#line 161 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakList t2772 =
              ((x10.mongo.yak.YakList)(new x10.mongo.yak.YakList()));
            
//#line 161 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakList t2773 =
              ((x10.mongo.yak.YakList)(t2772.$ampersand(((java.lang.Object)(a)))));
            
//#line 161 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakList t2774 =
              ((x10.mongo.yak.YakList)(t2773.$ampersand(((java.lang.Object)(b)))));
            
//#line 161 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakList t2775 =
              ((x10.mongo.yak.YakList)(t2774.$ampersand(((java.lang.Object)(c)))));
            
//#line 161 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2775;
        }
        
        
//#line 169 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakList
                                                                                                       list(
                                                                                                       final java.lang.Object a,
                                                                                                       final java.lang.Object b,
                                                                                                       final java.lang.Object c,
                                                                                                       final java.lang.Object d){
            
//#line 169 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakList t2776 =
              ((x10.mongo.yak.YakList)(new x10.mongo.yak.YakList()));
            
//#line 169 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakList t2777 =
              ((x10.mongo.yak.YakList)(t2776.$ampersand(((java.lang.Object)(a)))));
            
//#line 169 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakList t2778 =
              ((x10.mongo.yak.YakList)(t2777.$ampersand(((java.lang.Object)(b)))));
            
//#line 169 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakList t2779 =
              ((x10.mongo.yak.YakList)(t2778.$ampersand(((java.lang.Object)(c)))));
            
//#line 169 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakList t2780 =
              ((x10.mongo.yak.YakList)(t2779.$ampersand(((java.lang.Object)(d)))));
            
//#line 169 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2780;
        }
        
        
//#line 172 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.lang.PlaceLocalHandle<x10.lang.Cell<com.mongodb.Mongo>> mongoCell;
        
//#line 175 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.lang.PlaceLocalHandle<x10.util.concurrent.Lock> mongoCellLock;
        
        
//#line 184 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static com.mongodb.Mongo
                                                                                                       mongo(
                                                                                                       ){
            
//#line 185 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
try {try {{
                
//#line 186 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final com.mongodb.ServerAddress t2781 =
                  ((com.mongodb.ServerAddress)(new com.mongodb.ServerAddress()));
                
//#line 186 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final com.mongodb.Mongo t2782 =
                  x10.mongo.yak.YakUtil.mongo(((com.mongodb.ServerAddress)(t2781)));
                
//#line 186 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2782;
            }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Exception $exc$) {throw x10.core.ThrowableUtilities.convertJavaException($exc$);}}catch (final x10.lang.Exception e) {
                
//#line 189 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
e.printStackTrace();
                
//#line 190 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
throw e;
            }
        }
        
        
//#line 194 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
@x10.core.X10Generated public static class Mongoness extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
                                                                                                     {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Mongoness.class);
            
            public static final x10.rtt.RuntimeType<Mongoness> $RTT = x10.rtt.NamedType.<Mongoness> make(
            "x10.mongo.yak.YakUtil.Mongoness", /* base class */Mongoness.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(Mongoness $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Mongoness.class + " calling"); } 
                x10.util.Map mongoMap = (x10.util.Map) $deserializer.readRef();
                $_obj.mongoMap = mongoMap;
                x10.util.concurrent.Lock lock = (x10.util.concurrent.Lock) $deserializer.readRef();
                $_obj.lock = lock;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                Mongoness $_obj = new Mongoness((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (mongoMap instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.mongoMap);
                } else {
                $serializer.write(this.mongoMap);
                }
                if (lock instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.lock);
                } else {
                $serializer.write(this.lock);
                }
                
            }
            
            // constructor just for allocation
            public Mongoness(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            
                
//#line 194 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public x10.util.Map<com.mongodb.ServerAddress, com.mongodb.Mongo> mongoMap;
                
//#line 194 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public x10.util.concurrent.Lock lock;
                
                
                
//#line 195 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
// creation method for java code (1-phase java constructor)
                public Mongoness(){this((java.lang.System[]) null);
                                       x10$mongo$yak$YakUtil$Mongoness$$init$S();}
                
                // constructor for non-virtual call
                final public x10.mongo.yak.YakUtil.Mongoness x10$mongo$yak$YakUtil$Mongoness$$init$S() { {
                                                                                                                
//#line 195 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"

                                                                                                                
//#line 196 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.util.HashMap t2859 =
                                                                                                                  ((x10.util.HashMap)(new x10.util.HashMap<com.mongodb.ServerAddress, com.mongodb.Mongo>((java.lang.System[]) null, x10.rtt.Types.getRTT(com.mongodb.ServerAddress.class), x10.rtt.Types.getRTT(com.mongodb.Mongo.class)).x10$util$HashMap$$init$S()));
                                                                                                                
//#line 196 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.util.concurrent.Lock t2860 =
                                                                                                                  ((x10.util.concurrent.Lock)(new x10.util.concurrent.Lock()));
                                                                                                                
//#line 196 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
this.mongoMap = ((x10.util.Map)(t2859));
                                                                                                                this.lock = t2860;
                                                                                                                
                                                                                                            }
                                                                                                            return this;
                                                                                                            }
                
                
                
//#line 194 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final public x10.mongo.yak.YakUtil.Mongoness
                                                                                                               x10$mongo$yak$YakUtil$Mongoness$$x10$mongo$yak$YakUtil$Mongoness$this(
                                                                                                               ){
                    
//#line 194 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return x10.mongo.yak.YakUtil.Mongoness.this;
                }
            
        }
        
        
//#line 200 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.lang.PlaceLocalHandle<x10.mongo.yak.YakUtil.Mongoness> mongonessHandle;
        
        
//#line 209 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static com.mongodb.Mongo
                                                                                                       mongo(
                                                                                                       final com.mongodb.ServerAddress sa){
            
//#line 210 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final boolean t2788 =
              x10.mongo.yak.YakUtil.CreateNewMongoEveryTime;
            
//#line 210 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
if (t2788) {
                
//#line 211 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
try {try {{
                    
//#line 212 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final com.mongodb.Mongo t2785 =
                      ((com.mongodb.Mongo)(new com.mongodb.Mongo(((com.mongodb.ServerAddress)(sa)))));
                    
//#line 212 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2785;
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (final x10.core.X10Throwable e) {
                    
//#line 215 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final java.lang.String t2786 =
                      (("Oh no!  ") + (e));
                    
//#line 215 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
java.lang.System.err.println(t2786);
                    
//#line 216 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.lang.RuntimeException t2787 =
                      ((x10.lang.RuntimeException)(new x10.lang.RuntimeException(((x10.core.X10Throwable)(e)))));
                    
//#line 216 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
throw t2787;
                }
            }
            
//#line 220 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.lang.PlaceLocalHandle t2789 =
              ((x10.lang.PlaceLocalHandle)(x10.mongo.yak.YakUtil.get$mongonessHandle()));
            
//#line 220 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakUtil.Mongoness mongoness =
              ((x10.lang.PlaceLocalHandle<x10.mongo.yak.YakUtil.Mongoness>)t2789).$apply$G();
            
//#line 221 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.util.Map maphere =
              ((x10.util.Map)(mongoness.
                                mongoMap));
            
//#line 222 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.util.concurrent.Lock lock =
              ((x10.util.concurrent.Lock)(mongoness.
                                            lock));
            
//#line 223 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final boolean t2791 =
              x10.core.Boolean.$unbox(((x10.util.Map<com.mongodb.ServerAddress, com.mongodb.Mongo>)maphere).containsKey$Z(((com.mongodb.ServerAddress)(sa)),x10.rtt.Types.getRTT(com.mongodb.ServerAddress.class)));
            
//#line 223 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
if (t2791) {
                
//#line 224 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final com.mongodb.Mongo t2790 =
                  ((com.mongodb.Mongo)
                    ((x10.util.Map<com.mongodb.ServerAddress, com.mongodb.Mongo>)maphere).getOrThrow(((com.mongodb.ServerAddress)(sa)),x10.rtt.Types.getRTT(com.mongodb.ServerAddress.class)));
                
//#line 224 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2790;
            }
            
//#line 227 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
try {try {{
                
//#line 228 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
lock.lock();
                
//#line 230 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final boolean t2793 =
                  x10.core.Boolean.$unbox(((x10.util.Map<com.mongodb.ServerAddress, com.mongodb.Mongo>)maphere).containsKey$Z(((com.mongodb.ServerAddress)(sa)),x10.rtt.Types.getRTT(com.mongodb.ServerAddress.class)));
                
//#line 230 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
if (t2793) {
                    
//#line 231 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
lock.unlock();
                    
//#line 232 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final com.mongodb.Mongo t2792 =
                      ((com.mongodb.Mongo)
                        ((x10.util.Map<com.mongodb.ServerAddress, com.mongodb.Mongo>)maphere).getOrThrow(((com.mongodb.ServerAddress)(sa)),x10.rtt.Types.getRTT(com.mongodb.ServerAddress.class)));
                    
//#line 232 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2792;
                }
                
//#line 235 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final com.mongodb.Mongo mongo =
                  ((com.mongodb.Mongo)(new com.mongodb.Mongo(((com.mongodb.ServerAddress)(sa)))));
                
//#line 236 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
((x10.util.Map<com.mongodb.ServerAddress, com.mongodb.Mongo>)maphere).put(((com.mongodb.ServerAddress)(sa)),x10.rtt.Types.getRTT(com.mongodb.ServerAddress.class),
                                                                                                                                                                                       ((com.mongodb.Mongo)(mongo)),x10.rtt.Types.getRTT(com.mongodb.Mongo.class));
                
//#line 237 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return mongo;
            }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Exception $exc$) {throw x10.core.ThrowableUtilities.convertJavaException($exc$);}}catch (final x10.lang.Exception e) {
                
//#line 240 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final java.lang.String t2794 =
                  (("Oh, no! ") + (e));
                
//#line 240 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
java.lang.System.err.println(t2794);
                
//#line 241 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.lang.RuntimeException t2795 =
                  ((x10.lang.RuntimeException)(new x10.lang.RuntimeException(((x10.core.X10Throwable)(e)))));
                
//#line 241 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
throw t2795;
            }finally {{
                 
//#line 244 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
lock.unlock();
             }}
            }
        
        
//#line 253 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static com.mongodb.DB
                                                                                                       db(
                                                                                                       final java.lang.String name){
            
//#line 254 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final com.mongodb.Mongo t2796 =
              x10.mongo.yak.YakUtil.mongo();
            
//#line 254 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final com.mongodb.DB t2797 =
              t2796.getDB(((java.lang.String)(name)));
            
//#line 254 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2797;
        }
        
        
//#line 261 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakCollection
                                                                                                       collection(
                                                                                                       final java.lang.String dbName,
                                                                                                       final java.lang.String collName){
            
//#line 262 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.lang.PlaceLocalHandle t2798 =
              ((x10.lang.PlaceLocalHandle)(x10.mongo.yak.YakUtil.get$them()));
            
//#line 262 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakUtil t2800 =
              ((x10.lang.PlaceLocalHandle<x10.mongo.yak.YakUtil>)t2798).$apply$G();
            
//#line 262 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final com.mongodb.DB t2799 =
              x10.mongo.yak.YakUtil.db(((java.lang.String)(dbName)));
            
//#line 262 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final com.mongodb.DBCollection t2801 =
              t2799.getCollection(((java.lang.String)(collName)));
            
//#line 262 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakCollection t2802 =
              t2800.$apply(((com.mongodb.DBCollection)(t2801)));
            
//#line 262 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2802;
        }
        
        
//#line 271 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       fieldIs(
                                                                                                       final java.lang.String fieldName,
                                                                                                       final java.lang.String comparator,
                                                                                                       final java.lang.Object value){
            
//#line 271 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakUtil t2803 =
              x10.mongo.yak.YakUtil.it();
            
//#line 271 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.LoadedYakMap t2806 =
              ((x10.mongo.yak.LoadedYakMap)(t2803.$funnel(((java.lang.String)(fieldName)))));
            
//#line 271 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakUtil t2804 =
              x10.mongo.yak.YakUtil.it();
            
//#line 271 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.LoadedYakMap t2805 =
              ((x10.mongo.yak.LoadedYakMap)(t2804.$funnel(((java.lang.String)(comparator)))));
            
//#line 271 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2807 =
              ((x10.mongo.yak.YakMap)(t2805.$lfunnel(((java.lang.Object)(value)))));
            
//#line 271 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2808 =
              ((x10.mongo.yak.YakMap)(t2806.$lfunnel(((java.lang.Object)(t2807)))));
            
//#line 271 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2808;
        }
        
        
//#line 279 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       gt(
                                                                                                       final java.lang.String fieldName,
                                                                                                       final java.lang.Object v){
            
//#line 279 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2809 =
              ((x10.mongo.yak.YakMap)(x10.mongo.yak.YakUtil.fieldIs(((java.lang.String)(fieldName)),
                                                                    ((java.lang.String)("$gt")),
                                                                    ((java.lang.Object)(v)))));
            
//#line 279 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2809;
        }
        
        
//#line 287 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       lt(
                                                                                                       final java.lang.String fieldName,
                                                                                                       final java.lang.Object v){
            
//#line 287 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2810 =
              ((x10.mongo.yak.YakMap)(x10.mongo.yak.YakUtil.fieldIs(((java.lang.String)(fieldName)),
                                                                    ((java.lang.String)("$lt")),
                                                                    ((java.lang.Object)(v)))));
            
//#line 287 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2810;
        }
        
        
//#line 294 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       gte(
                                                                                                       final java.lang.String fieldName,
                                                                                                       final java.lang.Object v){
            
//#line 294 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2811 =
              ((x10.mongo.yak.YakMap)(x10.mongo.yak.YakUtil.fieldIs(((java.lang.String)(fieldName)),
                                                                    ((java.lang.String)("$gte")),
                                                                    ((java.lang.Object)(v)))));
            
//#line 294 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2811;
        }
        
        
//#line 301 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       lte(
                                                                                                       final java.lang.String fieldName,
                                                                                                       final java.lang.Object v){
            
//#line 301 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2812 =
              ((x10.mongo.yak.YakMap)(x10.mongo.yak.YakUtil.fieldIs(((java.lang.String)(fieldName)),
                                                                    ((java.lang.String)("$lte")),
                                                                    ((java.lang.Object)(v)))));
            
//#line 301 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2812;
        }
        
        
//#line 310 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       ne(
                                                                                                       final java.lang.String fieldName,
                                                                                                       final java.lang.Object v){
            
//#line 310 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2813 =
              ((x10.mongo.yak.YakMap)(x10.mongo.yak.YakUtil.fieldIs(((java.lang.String)(fieldName)),
                                                                    ((java.lang.String)("$ne")),
                                                                    ((java.lang.Object)(v)))));
            
//#line 310 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2813;
        }
        
        
//#line 318 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       all(
                                                                                                       final java.lang.String fieldName,
                                                                                                       final java.lang.Object v){
            
//#line 318 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2814 =
              ((x10.mongo.yak.YakMap)(x10.mongo.yak.YakUtil.fieldIs(((java.lang.String)(fieldName)),
                                                                    ((java.lang.String)("$all")),
                                                                    ((java.lang.Object)(v)))));
            
//#line 318 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2814;
        }
        
        
//#line 328 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       exists(
                                                                                                       final java.lang.String fieldName,
                                                                                                       final java.lang.Object v){
            
//#line 328 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2815 =
              ((x10.mongo.yak.YakMap)(x10.mongo.yak.YakUtil.fieldIs(((java.lang.String)(fieldName)),
                                                                    ((java.lang.String)("$exists")),
                                                                    ((java.lang.Object)(v)))));
            
//#line 328 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2815;
        }
        
        
//#line 337 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       exists(
                                                                                                       final java.lang.String fieldName){
            
//#line 337 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2816 =
              ((x10.mongo.yak.YakMap)(x10.mongo.yak.YakUtil.exists(((java.lang.String)(fieldName)),
                                                                   x10.core.Boolean.$box(true))));
            
//#line 337 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2816;
        }
        
        
//#line 346 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       eq(
                                                                                                       final java.lang.String fieldName,
                                                                                                       final java.lang.Object v){
            
//#line 346 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakUtil t2817 =
              x10.mongo.yak.YakUtil.it();
            
//#line 346 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.LoadedYakMap t2818 =
              ((x10.mongo.yak.LoadedYakMap)(t2817.$funnel(((java.lang.String)(fieldName)))));
            
//#line 346 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2819 =
              ((x10.mongo.yak.YakMap)(t2818.$lfunnel(((java.lang.Object)(v)))));
            
//#line 346 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2819;
        }
        
        
//#line 355 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       In(
                                                                                                       final java.lang.String fieldName,
                                                                                                       final java.lang.Object choices){
            
//#line 355 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2820 =
              ((x10.mongo.yak.YakMap)(x10.mongo.yak.YakUtil.fieldIs(((java.lang.String)(fieldName)),
                                                                    ((java.lang.String)("$in")),
                                                                    ((java.lang.Object)(choices)))));
            
//#line 355 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2820;
        }
        
        
//#line 363 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       elemMatch(
                                                                                                       final java.lang.String fieldName,
                                                                                                       final x10.mongo.yak.YakMap subquery){
            
//#line 363 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2821 =
              ((x10.mongo.yak.YakMap)(x10.mongo.yak.YakUtil.fieldIs(((java.lang.String)(fieldName)),
                                                                    ((java.lang.String)("$elemMatch")),
                                                                    ((java.lang.Object)(subquery)))));
            
//#line 363 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2821;
        }
        
        
//#line 371 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       nin(
                                                                                                       final java.lang.String fieldName,
                                                                                                       final java.lang.Object vs){
            
//#line 371 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2822 =
              ((x10.mongo.yak.YakMap)(x10.mongo.yak.YakUtil.fieldIs(((java.lang.String)(fieldName)),
                                                                    ((java.lang.String)("$nin")),
                                                                    ((java.lang.Object)(vs)))));
            
//#line 371 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2822;
        }
        
        
//#line 382 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       mod(
                                                                                                       final java.lang.String fieldName,
                                                                                                       final long divisor,
                                                                                                       final long remainder){
            
//#line 382 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakList t2823 =
              x10.mongo.yak.YakUtil.list(x10.core.Long.$box(divisor),
                                         x10.core.Long.$box(remainder));
            
//#line 382 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2824 =
              ((x10.mongo.yak.YakMap)(x10.mongo.yak.YakUtil.fieldIs(((java.lang.String)(fieldName)),
                                                                    ((java.lang.String)("$mod")),
                                                                    ((java.lang.Object)(t2823)))));
            
//#line 382 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2824;
        }
        
        
//#line 391 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       size(
                                                                                                       final java.lang.String fieldName,
                                                                                                       final java.lang.Object exactNumberOfElements){
            
//#line 391 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2825 =
              ((x10.mongo.yak.YakMap)(x10.mongo.yak.YakUtil.fieldIs(((java.lang.String)(fieldName)),
                                                                    ((java.lang.String)("$size")),
                                                                    ((java.lang.Object)(exactNumberOfElements)))));
            
//#line 391 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2825;
        }
        
        
//#line 399 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       inc(
                                                                                                       final java.lang.String fieldName,
                                                                                                       final java.lang.Object by){
            
//#line 399 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2826 =
              ((x10.mongo.yak.YakMap)(x10.mongo.yak.YakUtil.fieldIs(((java.lang.String)("$inc")),
                                                                    ((java.lang.String)(fieldName)),
                                                                    ((java.lang.Object)(by)))));
            
//#line 399 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2826;
        }
        
        
//#line 406 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       set(
                                                                                                       final java.lang.String fieldName,
                                                                                                       final java.lang.Object by){
            
//#line 406 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2827 =
              ((x10.mongo.yak.YakMap)(x10.mongo.yak.YakUtil.fieldIs(((java.lang.String)("$set")),
                                                                    ((java.lang.String)(fieldName)),
                                                                    ((java.lang.Object)(by)))));
            
//#line 406 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2827;
        }
        
        
//#line 413 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       unset(
                                                                                                       final java.lang.String fieldName,
                                                                                                       final java.lang.Object by){
            
//#line 413 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2828 =
              ((x10.mongo.yak.YakMap)(x10.mongo.yak.YakUtil.fieldIs(((java.lang.String)("$unset")),
                                                                    ((java.lang.String)(fieldName)),
                                                                    ((java.lang.Object)(by)))));
            
//#line 413 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2828;
        }
        
        
//#line 421 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       push(
                                                                                                       final java.lang.String fieldName,
                                                                                                       final java.lang.Object by){
            
//#line 421 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2829 =
              ((x10.mongo.yak.YakMap)(x10.mongo.yak.YakUtil.fieldIs(((java.lang.String)("$push")),
                                                                    ((java.lang.String)(fieldName)),
                                                                    ((java.lang.Object)(by)))));
            
//#line 421 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2829;
        }
        
        
//#line 429 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       pushAll(
                                                                                                       final java.lang.String fieldName,
                                                                                                       final java.lang.Object by){
            
//#line 429 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2830 =
              ((x10.mongo.yak.YakMap)(x10.mongo.yak.YakUtil.fieldIs(((java.lang.String)("$pushAll")),
                                                                    ((java.lang.String)(fieldName)),
                                                                    ((java.lang.Object)(by)))));
            
//#line 429 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2830;
        }
        
        
//#line 437 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       addToSet(
                                                                                                       final java.lang.String fieldName,
                                                                                                       final java.lang.Object by){
            
//#line 437 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2831 =
              ((x10.mongo.yak.YakMap)(x10.mongo.yak.YakUtil.fieldIs(((java.lang.String)("$addToSet")),
                                                                    ((java.lang.String)(fieldName)),
                                                                    ((java.lang.Object)(by)))));
            
//#line 437 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2831;
        }
        
        
//#line 445 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       addEachToSet(
                                                                                                       final java.lang.String fieldName,
                                                                                                       final java.lang.Object elements){
            
//#line 445 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakUtil t2832 =
              x10.mongo.yak.YakUtil.it();
            
//#line 445 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.LoadedYakMap t2833 =
              ((x10.mongo.yak.LoadedYakMap)(t2832.$funnel(((java.lang.String)("$each")))));
            
//#line 445 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2834 =
              ((x10.mongo.yak.YakMap)(t2833.$lfunnel(((java.lang.Object)(elements)))));
            
//#line 445 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2835 =
              ((x10.mongo.yak.YakMap)(x10.mongo.yak.YakUtil.fieldIs(((java.lang.String)("$addToSet")),
                                                                    ((java.lang.String)(fieldName)),
                                                                    ((java.lang.Object)(t2834)))));
            
//#line 445 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2835;
        }
        
        
//#line 452 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       pop(
                                                                                                       final java.lang.String fieldName){
            
//#line 452 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2836 =
              ((x10.mongo.yak.YakMap)(x10.mongo.yak.YakUtil.fieldIs(((java.lang.String)("$pop")),
                                                                    ((java.lang.String)(fieldName)),
                                                                    x10.core.Int.$box(1))));
            
//#line 452 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2836;
        }
        
        
//#line 461 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       pop(
                                                                                                       final java.lang.String fieldName,
                                                                                                       final java.lang.Object n){
            
//#line 461 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2837 =
              ((x10.mongo.yak.YakMap)(x10.mongo.yak.YakUtil.fieldIs(((java.lang.String)("$pop")),
                                                                    ((java.lang.String)(fieldName)),
                                                                    ((java.lang.Object)(n)))));
            
//#line 461 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2837;
        }
        
        
//#line 469 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       pull(
                                                                                                       final java.lang.String fieldName,
                                                                                                       final java.lang.Object n){
            
//#line 469 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2838 =
              ((x10.mongo.yak.YakMap)(x10.mongo.yak.YakUtil.fieldIs(((java.lang.String)("$pull")),
                                                                    ((java.lang.String)(fieldName)),
                                                                    ((java.lang.Object)(n)))));
            
//#line 469 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2838;
        }
        
        
//#line 477 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       pullAll(
                                                                                                       final java.lang.String fieldName,
                                                                                                       final java.lang.Object vs){
            
//#line 477 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2839 =
              ((x10.mongo.yak.YakMap)(x10.mongo.yak.YakUtil.fieldIs(((java.lang.String)("$pullAll")),
                                                                    ((java.lang.String)(fieldName)),
                                                                    ((java.lang.Object)(vs)))));
            
//#line 477 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2839;
        }
        
        
//#line 485 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.mongo.yak.YakMap
                                                                                                       rename(
                                                                                                       final java.lang.String oldName,
                                                                                                       final java.lang.String newName){
            
//#line 485 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2840 =
              ((x10.mongo.yak.YakMap)(x10.mongo.yak.YakUtil.fieldIs(((java.lang.String)("$rename")),
                                                                    ((java.lang.String)(oldName)),
                                                                    ((java.lang.Object)(newName)))));
            
//#line 485 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2840;
        }
        
        
//#line 498 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
/**
   * The WriteConcern to use when waiting for writes by default.  
   */
        final public static com.mongodb.WriteConcern WaitForWrite = ((com.mongodb.WriteConcern)(com.mongodb.WriteConcern.SAFE));
        
        
//#line 505 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static <$T>java.util.List
                                                                                                       bsonize__0$1x10$mongo$yak$YakUtil$$T$2(
                                                                                                       final x10.rtt.Type $T,
                                                                                                       final x10.util.List<$T> M){
            
//#line 506 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final java.util.ArrayList L =
              ((java.util.ArrayList)(new java.util.ArrayList()));
            
//#line 507 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.util.ListIterator x2862 =
              ((x10.util.ListIterator<$T>)
                ((x10.util.List<$T>)M).iterator());
            
//#line 507 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
for (;
                                                                                                              true;
                                                                                                              ) {
                
//#line 507 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final boolean t2863 =
                  ((x10.util.ListIterator<$T>)x2862).hasNext$O();
                
//#line 507 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
if (!(t2863)) {
                    
//#line 507 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
break;
                }
                
//#line 507 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final $T x2861 =
                  (($T)(((x10.util.ListIterator<$T>)x2862).next$G()));
                
//#line 507 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
L.add(((java.lang.Object)(x2861)));
            }
            
//#line 508 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return L;
        }
        
        
//#line 515 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static com.mongodb.DBObject[]
                                                                                                       convert__0$1x10$mongo$yak$YakMap$2(
                                                                                                       final x10.regionarray.Array<x10.mongo.yak.YakMap> r){
            
//#line 517 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final int t2844 =
              ((x10.regionarray.Array<x10.mongo.yak.YakMap>)r).
                size;
            
//#line 517 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.core.fun.Fun_0_1 t2845 =
              ((x10.core.fun.Fun_0_1)(new x10.mongo.yak.YakUtil.$Closure$0(r, (x10.mongo.yak.YakUtil.$Closure$0.__0$1x10$mongo$yak$YakMap$2) null)));
            
//#line 516 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.regionarray.Array dr =
              ((x10.regionarray.Array)(new x10.regionarray.Array<com.mongodb.DBObject>((java.lang.System[]) null, x10.rtt.Types.getRTT(com.mongodb.DBObject.class)).x10.regionarray$Array$$init$S(((int)(t2844)),
                                                                                                                                                                                ((x10.core.fun.Fun_0_1)(t2845)), (x10.regionarray.Array.__1$1x10$lang$Int$3x10.regionarray$Array$$T$2) null)));
            
//#line 518 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final com.mongodb.DBObject[] t2846 =
              (com.mongodb.DBObject[])dr.raw.getBackingArray();
            
//#line 518 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2846;
        }
        
        
//#line 525 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static <$T>java.util.List
                                                                                                       convert__0$1x10$mongo$yak$YakUtil$$T$2(
                                                                                                       final x10.rtt.Type $T,
                                                                                                       final x10.util.List<$T> L){
            
//#line 526 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final int t2847 =
              ((x10.util.Container<$T>)L).size$O();
            
//#line 526 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final java.util.ArrayList J =
              ((java.util.ArrayList)(new java.util.ArrayList(t2847)));
            
//#line 527 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.util.ListIterator x2865 =
              ((x10.util.ListIterator<$T>)
                ((x10.util.List<$T>)L).iterator());
            
//#line 527 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
for (;
                                                                                                              true;
                                                                                                              ) {
                
//#line 527 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final boolean t2866 =
                  ((x10.util.ListIterator<$T>)x2865).hasNext$O();
                
//#line 527 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
if (!(t2866)) {
                    
//#line 527 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
break;
                }
                
//#line 527 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final $T x2864 =
                  (($T)(((x10.util.ListIterator<$T>)x2865).next$G()));
                
//#line 527 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
J.add(((java.lang.Object)(x2864)));
            }
            
//#line 528 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return J;
        }
        
        
//#line 535 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.util.List
                                                                                                       convertListOfDBObjects(
                                                                                                       final java.util.List J){
            
//#line 536 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final int t2850 =
              J.size();
            
//#line 536 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.util.ArrayList Y =
              ((x10.util.ArrayList)(new x10.util.ArrayList<x10.mongo.yak.YakMap>((java.lang.System[]) null, x10.mongo.yak.YakMap.$RTT).x10$util$ArrayList$$init$S(t2850)));
            
//#line 537 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final java.util.Iterator Jiter =
              J.iterator();
            
//#line 538 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
while (true) {
                
//#line 538 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final boolean t2853 =
                  Jiter.hasNext();
                
//#line 538 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
if (!(t2853)) {
                    
//#line 538 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
break;
                }
                
//#line 539 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final java.lang.Object t2867 =
                  Jiter.next();
                
//#line 539 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final com.mongodb.DBObject d2868 =
                  x10.rtt.Types.<com.mongodb.DBObject> cast(t2867,x10.rtt.Types.getRTT(com.mongodb.DBObject.class));
                
//#line 540 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2869 =
                  x10.mongo.yak.YakMap.yakifyTopLevel(((com.mongodb.DBObject)(d2868)));
                
//#line 540 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
((x10.util.ArrayList<x10.mongo.yak.YakMap>)Y).add__0x10$util$ArrayList$$T$O(((x10.mongo.yak.YakMap)(t2869)));
            }
            
//#line 542 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return Y;
        }
        
        
//#line 549 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static x10.util.List
                                                                                                       convertIntList(
                                                                                                       final java.util.List J){
            
//#line 550 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final int t2854 =
              J.size();
            
//#line 550 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.util.ArrayList X =
              ((x10.util.ArrayList)(new x10.util.ArrayList<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).x10$util$ArrayList$$init$S(t2854)));
            
//#line 551 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final java.util.Iterator jiter =
              J.iterator();
            
//#line 552 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
while (true) {
                
//#line 552 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final boolean t2855 =
                  jiter.hasNext();
                
//#line 552 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
if (!(t2855)) {
                    
//#line 552 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
break;
                }
                
//#line 553 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final java.lang.Object v2870 =
                  jiter.next();
                
//#line 554 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final int iv2871 =
                  x10.rtt.Types.asint(v2870,x10.rtt.Types.ANY);
                
//#line 555 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
((x10.util.ArrayList<x10.core.Int>)X).add__0x10$util$ArrayList$$T$O(x10.core.Int.$box(iv2871));
            }
            
//#line 557 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return X;
        }
        
        
//#line 564 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
public static <$T>x10.util.List
                                                                                                       convert(
                                                                                                       final x10.rtt.Type $T,
                                                                                                       final java.util.List J){
            
//#line 565 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final int t2856 =
              J.size();
            
//#line 565 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.util.ArrayList X =
              ((x10.util.ArrayList)(new x10.util.ArrayList<$T>((java.lang.System[]) null, $T).x10$util$ArrayList$$init$S(t2856)));
            
//#line 566 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final java.util.Iterator Jiter =
              J.iterator();
            
//#line 567 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
while (true) {
                
//#line 567 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final boolean t2858 =
                  Jiter.hasNext();
                
//#line 567 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
if (!(t2858)) {
                    
//#line 567 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
break;
                }
                
//#line 568 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final java.lang.Object t2872 =
                  Jiter.next();
                
//#line 568 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final $T d2873 =
                  (($T)(x10.rtt.Types.<$T> cast(t2872,$T)));
                
//#line 569 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
((x10.util.ArrayList<$T>)X).add__0x10$util$ArrayList$$T$O((($T)(d2873)));
            }
            
//#line 571 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return X;
        }
        
        
//#line 35 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final public x10.mongo.yak.YakUtil
                                                                                                      x10$mongo$yak$YakUtil$$x10$mongo$yak$YakUtil$this(
                                                                                                      ){
            
//#line 35 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return x10.mongo.yak.YakUtil.this;
        }
        
        public static short fieldId$mongonessHandle;
        final public static x10.core.concurrent.AtomicInteger initStatus$mongonessHandle = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        public static short fieldId$mongoCellLock;
        final public static x10.core.concurrent.AtomicInteger initStatus$mongoCellLock = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        public static short fieldId$mongoCell;
        final public static x10.core.concurrent.AtomicInteger initStatus$mongoCell = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        public static short fieldId$them;
        final public static x10.core.concurrent.AtomicInteger initStatus$them = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        
        public static void
          getDeserialized$them(
          x10.x10rt.X10JavaDeserializer deserializer){
            x10.mongo.yak.YakUtil.them = ((x10.lang.PlaceLocalHandle)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
            x10.mongo.yak.YakUtil.initStatus$them.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static x10.lang.PlaceLocalHandle
          get$them(
          ){
            if (((int) x10.mongo.yak.YakUtil.initStatus$them.get()) ==
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                return x10.mongo.yak.YakUtil.them;
            }
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0) &&
                  x10.mongo.yak.YakUtil.initStatus$them.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                      (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                x10.mongo.yak.YakUtil.them = x10.lang.PlaceLocalHandle.<x10.mongo.yak.YakUtil>make__1$1x10$lang$PlaceLocalHandle$$T$2(x10.mongo.yak.YakUtil.$RTT, ((x10.regionarray.Dist)(x10.regionarray.Dist.makeUnique())),
                                                                                                                                      ((x10.core.fun.Fun_0_0)(new x10.mongo.yak.YakUtil.$Closure$1())));
                if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                      ((boolean) true)) {
                    x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.mongo.yak.YakUtil.them")));
                }
                x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.mongo.yak.YakUtil.them)),
                                                                          (short)(x10.mongo.yak.YakUtil.fieldId$them));
                x10.mongo.yak.YakUtil.initStatus$them.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                x10.runtime.impl.java.InitDispatcher.notifyInitialized();
            } else {
                if (((int) x10.mongo.yak.YakUtil.initStatus$them.get()) !=
                    ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    while (((int) x10.mongo.yak.YakUtil.initStatus$them.get()) !=
                           ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                    }
                    x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                }
            }
            return x10.mongo.yak.YakUtil.them;
        }
        
        public static void
          getDeserialized$mongoCell(
          x10.x10rt.X10JavaDeserializer deserializer){
            x10.mongo.yak.YakUtil.mongoCell = ((x10.lang.PlaceLocalHandle)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
            x10.mongo.yak.YakUtil.initStatus$mongoCell.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static x10.lang.PlaceLocalHandle
          get$mongoCell(
          ){
            if (((int) x10.mongo.yak.YakUtil.initStatus$mongoCell.get()) ==
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                return x10.mongo.yak.YakUtil.mongoCell;
            }
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0) &&
                  x10.mongo.yak.YakUtil.initStatus$mongoCell.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                           (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                x10.mongo.yak.YakUtil.mongoCell = x10.lang.PlaceLocalHandle.<x10.lang.Cell<com.mongodb.Mongo>>make__1$1x10$lang$PlaceLocalHandle$$T$2(x10.rtt.ParameterizedType.make(x10.lang.Cell.$RTT, x10.rtt.Types.getRTT(com.mongodb.Mongo.class)), ((x10.regionarray.Dist)(x10.regionarray.Dist.makeUnique())),
                                                                                                                                                      ((x10.core.fun.Fun_0_0)(new x10.mongo.yak.YakUtil.$Closure$2())));
                if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                      ((boolean) true)) {
                    x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.mongo.yak.YakUtil.mongoCell")));
                }
                x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.mongo.yak.YakUtil.mongoCell)),
                                                                          (short)(x10.mongo.yak.YakUtil.fieldId$mongoCell));
                x10.mongo.yak.YakUtil.initStatus$mongoCell.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                x10.runtime.impl.java.InitDispatcher.notifyInitialized();
            } else {
                if (((int) x10.mongo.yak.YakUtil.initStatus$mongoCell.get()) !=
                    ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    while (((int) x10.mongo.yak.YakUtil.initStatus$mongoCell.get()) !=
                           ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                    }
                    x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                }
            }
            return x10.mongo.yak.YakUtil.mongoCell;
        }
        
        public static void
          getDeserialized$mongoCellLock(
          x10.x10rt.X10JavaDeserializer deserializer){
            x10.mongo.yak.YakUtil.mongoCellLock = ((x10.lang.PlaceLocalHandle)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
            x10.mongo.yak.YakUtil.initStatus$mongoCellLock.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static x10.lang.PlaceLocalHandle
          get$mongoCellLock(
          ){
            if (((int) x10.mongo.yak.YakUtil.initStatus$mongoCellLock.get()) ==
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                return x10.mongo.yak.YakUtil.mongoCellLock;
            }
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0) &&
                  x10.mongo.yak.YakUtil.initStatus$mongoCellLock.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                               (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                x10.mongo.yak.YakUtil.mongoCellLock = x10.lang.PlaceLocalHandle.<x10.util.concurrent.Lock>make__1$1x10$lang$PlaceLocalHandle$$T$2(x10.util.concurrent.Lock.$RTT, ((x10.regionarray.Dist)(x10.regionarray.Dist.makeUnique())),
                                                                                                                                                  ((x10.core.fun.Fun_0_0)(new x10.mongo.yak.YakUtil.$Closure$3())));
                if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                      ((boolean) true)) {
                    x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.mongo.yak.YakUtil.mongoCellLock")));
                }
                x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.mongo.yak.YakUtil.mongoCellLock)),
                                                                          (short)(x10.mongo.yak.YakUtil.fieldId$mongoCellLock));
                x10.mongo.yak.YakUtil.initStatus$mongoCellLock.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                x10.runtime.impl.java.InitDispatcher.notifyInitialized();
            } else {
                if (((int) x10.mongo.yak.YakUtil.initStatus$mongoCellLock.get()) !=
                    ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    while (((int) x10.mongo.yak.YakUtil.initStatus$mongoCellLock.get()) !=
                           ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                    }
                    x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                }
            }
            return x10.mongo.yak.YakUtil.mongoCellLock;
        }
        
        public static void
          getDeserialized$mongonessHandle(
          x10.x10rt.X10JavaDeserializer deserializer){
            x10.mongo.yak.YakUtil.mongonessHandle = ((x10.lang.PlaceLocalHandle)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
            x10.mongo.yak.YakUtil.initStatus$mongonessHandle.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static x10.lang.PlaceLocalHandle
          get$mongonessHandle(
          ){
            if (((int) x10.mongo.yak.YakUtil.initStatus$mongonessHandle.get()) ==
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                return x10.mongo.yak.YakUtil.mongonessHandle;
            }
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0) &&
                  x10.mongo.yak.YakUtil.initStatus$mongonessHandle.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                                 (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                x10.mongo.yak.YakUtil.mongonessHandle = x10.lang.PlaceLocalHandle.<x10.mongo.yak.YakUtil.Mongoness>make__1$1x10$lang$PlaceLocalHandle$$T$2(x10.mongo.yak.YakUtil.Mongoness.$RTT, ((x10.regionarray.Dist)(x10.regionarray.Dist.makeUnique())),
                                                                                                                                                           ((x10.core.fun.Fun_0_0)(new x10.mongo.yak.YakUtil.$Closure$4())));
                if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                      ((boolean) true)) {
                    x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.mongo.yak.YakUtil.mongonessHandle")));
                }
                x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.mongo.yak.YakUtil.mongonessHandle)),
                                                                          (short)(x10.mongo.yak.YakUtil.fieldId$mongonessHandle));
                x10.mongo.yak.YakUtil.initStatus$mongonessHandle.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                x10.runtime.impl.java.InitDispatcher.notifyInitialized();
            } else {
                if (((int) x10.mongo.yak.YakUtil.initStatus$mongonessHandle.get()) !=
                    ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    while (((int) x10.mongo.yak.YakUtil.initStatus$mongonessHandle.get()) !=
                           ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                    }
                    x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                }
            }
            return x10.mongo.yak.YakUtil.mongonessHandle;
        }
        
        public static com.mongodb.WriteConcern
          get$WaitForWrite(
          ){
            return x10.mongo.yak.YakUtil.WaitForWrite;
        }
        
        static {
                   x10.mongo.yak.YakUtil.fieldId$them = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.mongo.yak.YakUtil")),
                                                                                                                            ((java.lang.String)("them")))))));
                   x10.mongo.yak.YakUtil.fieldId$mongoCell = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.mongo.yak.YakUtil")),
                                                                                                                                 ((java.lang.String)("mongoCell")))))));
                   x10.mongo.yak.YakUtil.fieldId$mongoCellLock = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.mongo.yak.YakUtil")),
                                                                                                                                     ((java.lang.String)("mongoCellLock")))))));
                   x10.mongo.yak.YakUtil.fieldId$mongonessHandle = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.mongo.yak.YakUtil")),
                                                                                                                                       ((java.lang.String)("mongonessHandle")))))));
               }
        
        @x10.core.X10Generated public static class $Closure$0 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$0.class);
            
            public static final x10.rtt.RuntimeType<$Closure$0> $RTT = x10.rtt.StaticFunType.<$Closure$0> make(
            /* base class */$Closure$0.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.mongo.yak.YakMap.$RTT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$0 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$0.class + " calling"); } 
                x10.regionarray.Array r = (x10.regionarray.Array) $deserializer.readRef();
                $_obj.r = r;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$0 $_obj = new $Closure$0((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (r instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.r);
                } else {
                $serializer.write(this.r);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$0(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return $apply(x10.core.Int.$unbox(a1));
            }
            
                
                public x10.mongo.yak.YakMap
                  $apply(
                  final int i){
                    
//#line 517 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
final x10.mongo.yak.YakMap t2843 =
                      ((x10.regionarray.Array<x10.mongo.yak.YakMap>)this.
                                                                r).$apply$G((int)(i));
                    
//#line 517 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return t2843;
                }
                
                public x10.regionarray.Array<x10.mongo.yak.YakMap> r;
                
                public $Closure$0(final x10.regionarray.Array<x10.mongo.yak.YakMap> r, __0$1x10$mongo$yak$YakMap$2 $dummy) { {
                                                                                                                              this.r = ((x10.regionarray.Array)(r));
                                                                                                                          }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$mongo$yak$YakMap$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$1 extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$1.class);
            
            public static final x10.rtt.RuntimeType<$Closure$1> $RTT = x10.rtt.StaticFunType.<$Closure$1> make(
            /* base class */$Closure$1.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.mongo.yak.YakUtil.$RTT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$1 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$1.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$1 $_obj = new $Closure$1((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public $Closure$1(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // bridge for method abstract public ()=>U.operator()():U
            public x10.mongo.yak.YakUtil
              $apply$G(){return $apply();}
            
                
                public x10.mongo.yak.YakUtil
                  $apply(
                  ){
                    
//#line 62 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return new x10.mongo.yak.YakUtil((java.lang.System[]) null).x10$mongo$yak$YakUtil$$init$S();
                }
                
                public $Closure$1() { {
                                             
                                         }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$2 extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$2.class);
            
            public static final x10.rtt.RuntimeType<$Closure$2> $RTT = x10.rtt.StaticFunType.<$Closure$2> make(
            /* base class */$Closure$2.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.ParameterizedType.make(x10.lang.Cell.$RTT, x10.rtt.Types.getRTT(com.mongodb.Mongo.class))), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$2 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$2.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$2 $_obj = new $Closure$2((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public $Closure$2(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // bridge for method abstract public ()=>U.operator()():U
            public x10.lang.Cell
              $apply$G(){return $apply();}
            
                
                public x10.lang.Cell
                  $apply(
                  ){
                    
//#line 173 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return new x10.lang.Cell<com.mongodb.Mongo>((java.lang.System[]) null, x10.rtt.Types.getRTT(com.mongodb.Mongo.class)).x10$lang$Cell$$init$S(((com.mongodb.Mongo)(null)), (x10.lang.Cell.__0x10$lang$Cell$$T) null);
                }
                
                public $Closure$2() { {
                                             
                                         }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$3 extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$3.class);
            
            public static final x10.rtt.RuntimeType<$Closure$3> $RTT = x10.rtt.StaticFunType.<$Closure$3> make(
            /* base class */$Closure$3.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.util.concurrent.Lock.$RTT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$3 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$3.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$3 $_obj = new $Closure$3((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public $Closure$3(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // bridge for method abstract public ()=>U.operator()():U
            public x10.util.concurrent.Lock
              $apply$G(){return $apply();}
            
                
                public x10.util.concurrent.Lock
                  $apply(
                  ){
                    
//#line 176 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return new x10.util.concurrent.Lock();
                }
                
                public $Closure$3() { {
                                             
                                         }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$4 extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$4.class);
            
            public static final x10.rtt.RuntimeType<$Closure$4> $RTT = x10.rtt.StaticFunType.<$Closure$4> make(
            /* base class */$Closure$4.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.mongo.yak.YakUtil.Mongoness.$RTT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$4 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$4.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$4 $_obj = new $Closure$4((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public $Closure$4(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // bridge for method abstract public ()=>U.operator()():U
            public x10.mongo.yak.YakUtil.Mongoness
              $apply$G(){return $apply();}
            
                
                public x10.mongo.yak.YakUtil.Mongoness
                  $apply(
                  ){
                    
//#line 202 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakUtil.x10"
return new x10.mongo.yak.YakUtil.Mongoness((java.lang.System[]) null).x10$mongo$yak$YakUtil$Mongoness$$init$S();
                }
                
                public $Closure$4() { {
                                             
                                         }}
                
            }
            
        
        }
        