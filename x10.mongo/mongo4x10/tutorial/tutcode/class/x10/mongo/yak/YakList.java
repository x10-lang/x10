package x10.mongo.yak;


@x10.core.X10Generated public class YakList extends java.util.ArrayList implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, YakList.class);
    
    public static final x10.rtt.RuntimeType<YakList> $RTT = x10.rtt.NamedType.<YakList> make(
    "x10.mongo.yak.YakList", /* base class */YakList.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.getRTT(java.util.ArrayList.class)}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(YakList $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + YakList.class + " calling"); } 
        $deserializer.deserializeClassUsingReflection(java.util.ArrayList.class, $_obj, 0);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        YakList $_obj = new YakList((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.serializeClassUsingReflection(this, java.util.ArrayList.class);
        
    }
    
    // constructor just for allocation
    public YakList(final java.lang.System[] $dummy) { 
    }
    
        
        
//#line 16 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakList.x10"
public YakList() {super();
                                                                                                                           {
                                                                                                                              
//#line 16 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakList.x10"

                                                                                                                          }}
        
        
//#line 26 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakList.x10"
public x10.mongo.yak.YakList
                                                                                                      $ampersand(
                                                                                                      final java.lang.Object a){
            
//#line 27 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakList.x10"
final java.lang.Object t4459 =
              x10.mongo.yak.LoadedYakMap.javify(((java.lang.Object)(a)));
            
//#line 27 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakList.x10"
this.add(((java.lang.Object)(t4459)));
            
//#line 28 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakList.x10"
return this;
        }
        
        
//#line 15 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakList.x10"
final public x10.mongo.yak.YakList
                                                                                                      x10$mongo$yak$YakList$$x10$mongo$yak$YakList$this(
                                                                                                      ){
            
//#line 15 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakList.x10"
return x10.mongo.yak.YakList.this;
        }
        
    }
    