package x10.mongo.yak;


@x10.core.X10Generated public class YakCollection extends x10.core.Ref implements x10.io.CustomSerialization
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, YakCollection.class);
    
    public static final x10.rtt.RuntimeType<YakCollection> $RTT = x10.rtt.NamedType.<YakCollection> make(
    "x10.mongo.yak.YakCollection", /* base class */YakCollection.class
    , /* parents */ new x10.rtt.Type[] {x10.io.CustomSerialization.$RTT, x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    // custom serializer
    private transient x10.io.SerialData $$serialdata;
    private Object writeReplace() { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " calling"); } $$serialdata = serialize(); if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " returned " + $$serialdata); } return this; }
    private Object readResolve() { return new YakCollection($$serialdata); }
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
    oos.writeObject($$serialdata); }
    private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {
    $$serialdata = (x10.io.SerialData) ois.readObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(YakCollection $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("X10JavaSerializable: $_deserialize_body() of " + YakCollection.class + " calling"); } 
        x10.io.SerialData $$serialdata = (x10.io.SerialData) $deserializer.readRef();
        $_obj.x10$mongo$yak$YakCollection$$init$S($$serialdata);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        YakCollection $_obj = new YakCollection((java.lang.System[]) null);
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
    public YakCollection(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 49 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
/**
   * The <kbd>com.mongodb.DBCollection</kbd> object underlying this <kbd>YakCollection</kbd>, 
   * available as public data in case there's anything you need to do with it that you can't 
   * do on the <kbd>YakCollection</kbd>.
   */
        public com.mongodb.DBCollection original;
        
        
//#line 55 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
// creation method for java code (1-phase java constructor)
        public YakCollection(final com.mongodb.DBCollection original){this((java.lang.System[]) null);
                                                                          x10$mongo$yak$YakCollection$$init$S(original);}
        
        // constructor for non-virtual call
        final public x10.mongo.yak.YakCollection x10$mongo$yak$YakCollection$$init$S(final com.mongodb.DBCollection original) { {
                                                                                                                                       
//#line 55 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"

                                                                                                                                       
//#line 55 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"

                                                                                                                                       
//#line 56 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
this.original = ((com.mongodb.DBCollection)(original));
                                                                                                                                       
//#line 57 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.WriteConcern t2874 =
                                                                                                                                         ((com.mongodb.WriteConcern)(x10.mongo.yak.YakUtil.get$WaitForWrite()));
                                                                                                                                       
//#line 57 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
original.setWriteConcern(((com.mongodb.WriteConcern)(t2874)));
                                                                                                                                   }
                                                                                                                                   return this;
                                                                                                                                   }
        
        
        
//#line 65 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public static x10.mongo.yak.YakCollection
                                                                                                            make(
                                                                                                            final com.mongodb.DBCollection coll){
            
//#line 66 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakCollection t2875 =
              ((x10.mongo.yak.YakCollection)(new x10.mongo.yak.YakCollection((java.lang.System[]) null).x10$mongo$yak$YakCollection$$init$S(((com.mongodb.DBCollection)(coll)))));
            
//#line 66 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2875;
        }
        
        
//#line 74 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
@x10.core.X10Generated public static class SerializationClues extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
                                                                                                          {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, SerializationClues.class);
            
            public static final x10.rtt.RuntimeType<SerializationClues> $RTT = x10.rtt.NamedType.<SerializationClues> make(
            "x10.mongo.yak.YakCollection.SerializationClues", /* base class */SerializationClues.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(SerializationClues $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + SerializationClues.class + " calling"); } 
                java.lang.String dbName = (java.lang.String) $deserializer.readRef();
                $_obj.dbName = dbName;
                java.lang.String collName = (java.lang.String) $deserializer.readRef();
                $_obj.collName = collName;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                SerializationClues $_obj = new SerializationClues((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write(this.dbName);
                $serializer.write(this.collName);
                
            }
            
            // constructor just for allocation
            public SerializationClues(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            
                
//#line 75 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public java.lang.String dbName;
                
//#line 76 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public java.lang.String collName;
                
                
                
//#line 74 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final public x10.mongo.yak.YakCollection.SerializationClues
                                                                                                                    x10$mongo$yak$YakCollection$SerializationClues$$x10$mongo$yak$YakCollection$SerializationClues$this(
                                                                                                                    ){
                    
//#line 74 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return x10.mongo.yak.YakCollection.SerializationClues.this;
                }
                
                
//#line 77 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
// creation method for java code (1-phase java constructor)
                public SerializationClues(final java.lang.String dbName,
                                          final java.lang.String collName){this((java.lang.System[]) null);
                                                                               x10$mongo$yak$YakCollection$SerializationClues$$init$S(dbName,collName);}
                
                // constructor for non-virtual call
                final public x10.mongo.yak.YakCollection.SerializationClues x10$mongo$yak$YakCollection$SerializationClues$$init$S(final java.lang.String dbName,
                                                                                                                                   final java.lang.String collName) { {
                                                                                                                                                                             
//#line 77 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"

                                                                                                                                                                             
//#line 77 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
this.dbName = dbName;
                                                                                                                                                                             this.collName = collName;
                                                                                                                                                                             
                                                                                                                                                                         }
                                                                                                                                                                         return this;
                                                                                                                                                                         }
                
            
        }
        
        
        
//#line 83 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public x10.io.SerialData
                                                                                                            serialize(
                                                                                                            ){
            
//#line 84 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DB t2876 =
              this.getDB();
            
//#line 84 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final java.lang.String t2877 =
              t2876.getName();
            
//#line 84 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final java.lang.String t2878 =
              this.getName$O();
            
//#line 84 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakCollection.SerializationClues t2879 =
              ((x10.mongo.yak.YakCollection.SerializationClues)(new x10.mongo.yak.YakCollection.SerializationClues((java.lang.System[]) null).x10$mongo$yak$YakCollection$SerializationClues$$init$S(t2877,
                                                                                                                                                                                                     t2878)));
            
//#line 84 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.io.SerialData t2880 =
              ((x10.io.SerialData)(new x10.io.SerialData((java.lang.System[]) null).x10$io$SerialData$$init$S(((java.lang.Object)(t2879)),
                                                                                                              ((x10.io.SerialData)(null)))));
            
//#line 84 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2880;
        }
        
        
//#line 91 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
// creation method for java code (1-phase java constructor)
        public YakCollection(final x10.io.SerialData sd){this((java.lang.System[]) null);
                                                             x10$mongo$yak$YakCollection$$init$S(sd);}
        
        // constructor for non-virtual call
        final public x10.mongo.yak.YakCollection x10$mongo$yak$YakCollection$$init$S(final x10.io.SerialData sd) {x10$mongo$yak$YakCollection$init_for_reflection(sd);
                                                                                                                      
                                                                                                                      return this;
                                                                                                                      }
        public void x10$mongo$yak$YakCollection$init_for_reflection(x10.io.SerialData sd) {
             {
                
//#line 91 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"

                
//#line 91 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"

                
//#line 92 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final java.lang.Object t2881 =
                  ((java.lang.Object)(sd.
                                        data));
                
//#line 92 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakCollection.SerializationClues sc =
                  ((x10.mongo.yak.YakCollection.SerializationClues)(x10.rtt.Types.<x10.mongo.yak.YakCollection.SerializationClues> cast(t2881,x10.mongo.yak.YakCollection.SerializationClues.$RTT)));
                
//#line 93 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final java.lang.String dbName =
                  ((java.lang.String)(sc.
                                        dbName));
                
//#line 94 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final java.lang.String collName =
                  ((java.lang.String)(sc.
                                        collName));
                
//#line 95 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DB db =
                  x10.mongo.yak.YakUtil.db(((java.lang.String)(dbName)));
                
//#line 96 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2882 =
                  db.getCollection(((java.lang.String)(collName)));
                
//#line 96 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
this.original = ((com.mongodb.DBCollection)(t2882));
            }}
            
        
        
//#line 104 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public static x10.mongo.yak.YakUtil y;
        
        
//#line 113 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public com.mongodb.WriteResult
                                                                                                             insert__0$1x10$mongo$yak$YakMap$2(
                                                                                                             final x10.regionarray.Array newElements,
                                                                                                             final com.mongodb.WriteConcern concern){
            
//#line 114 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2883 =
              ((com.mongodb.DBCollection)(original));
            
//#line 114 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBObject[] t2884 =
              x10.mongo.yak.YakUtil.convert__0$1x10$mongo$yak$YakMap$2(((x10.regionarray.Array)(newElements)));
            
//#line 114 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.WriteResult t2885 =
              t2883.insert(((com.mongodb.DBObject[])(t2884)),
                           ((com.mongodb.WriteConcern)(concern)));
            
//#line 114 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2885;
        }
        
        
//#line 122 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public com.mongodb.WriteResult
                                                                                                             insert__0$1x10$mongo$yak$YakMap$2(
                                                                                                             final x10.regionarray.Array newElements,
                                                                                                             final com.mongodb.WriteConcern concern,
                                                                                                             final com.mongodb.DBEncoder encoder){
            
//#line 122 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2886 =
              ((com.mongodb.DBCollection)(original));
            
//#line 122 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBObject[] t2887 =
              x10.mongo.yak.YakUtil.convert__0$1x10$mongo$yak$YakMap$2(((x10.regionarray.Array)(newElements)));
            
//#line 122 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.WriteResult t2888 =
              t2886.insert(((com.mongodb.DBObject[])(t2887)),
                           ((com.mongodb.WriteConcern)(concern)),
                           ((com.mongodb.DBEncoder)(encoder)));
            
//#line 122 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2888;
        }
        
        
//#line 129 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public com.mongodb.WriteResult
                                                                                                             insert(
                                                                                                             final com.mongodb.DBObject newObject,
                                                                                                             final com.mongodb.WriteConcern concern){
            
//#line 129 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2889 =
              ((com.mongodb.DBCollection)(original));
            
//#line 129 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.WriteResult t2890 =
              t2889.insert(((com.mongodb.DBObject)(newObject)),
                           ((com.mongodb.WriteConcern)(concern)));
            
//#line 129 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2890;
        }
        
        
//#line 137 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public com.mongodb.WriteResult
                                                                                                             insert__0$1x10$mongo$yak$YakMap$2(
                                                                                                             final x10.regionarray.Array newElements){
            
//#line 137 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2891 =
              ((com.mongodb.DBCollection)(original));
            
//#line 137 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBObject[] t2892 =
              x10.mongo.yak.YakUtil.convert__0$1x10$mongo$yak$YakMap$2(((x10.regionarray.Array)(newElements)));
            
//#line 137 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.WriteResult t2893 =
              t2891.insert(((com.mongodb.DBObject[])(t2892)));
            
//#line 137 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2893;
        }
        
        
//#line 145 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public com.mongodb.WriteResult
                                                                                                             insert__1$1x10$mongo$yak$YakMap$2(
                                                                                                             final com.mongodb.WriteConcern concern,
                                                                                                             final x10.regionarray.Array newElements){
            
//#line 145 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2894 =
              ((com.mongodb.DBCollection)(original));
            
//#line 145 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBObject[] t2895 =
              x10.mongo.yak.YakUtil.convert__0$1x10$mongo$yak$YakMap$2(((x10.regionarray.Array)(newElements)));
            
//#line 145 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.WriteResult t2896 =
              t2894.insert(((com.mongodb.WriteConcern)(concern)),
                           ((com.mongodb.DBObject[])(t2895)));
            
//#line 145 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2896;
        }
        
        
//#line 164 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public com.mongodb.WriteResult
                                                                                                             update(
                                                                                                             final com.mongodb.DBObject query,
                                                                                                             final com.mongodb.DBObject mod,
                                                                                                             final boolean upsert,
                                                                                                             final boolean multi,
                                                                                                             final com.mongodb.WriteConcern concern){
            
//#line 165 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2897 =
              ((com.mongodb.DBCollection)(original));
            
//#line 165 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.WriteResult t2898 =
              t2897.update(((com.mongodb.DBObject)(query)),
                           ((com.mongodb.DBObject)(mod)),
                           (boolean)(upsert),
                           (boolean)(multi),
                           ((com.mongodb.WriteConcern)(concern)));
            
//#line 165 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2898;
        }
        
        
//#line 181 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public com.mongodb.WriteResult
                                                                                                             update(
                                                                                                             final com.mongodb.DBObject query,
                                                                                                             final com.mongodb.DBObject mod,
                                                                                                             final boolean upsert,
                                                                                                             final boolean a22,
                                                                                                             final com.mongodb.WriteConcern concern,
                                                                                                             final com.mongodb.DBEncoder encoder){
            
//#line 182 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2899 =
              ((com.mongodb.DBCollection)(original));
            
//#line 182 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.WriteResult t2900 =
              t2899.update(((com.mongodb.DBObject)(query)),
                           ((com.mongodb.DBObject)(mod)),
                           (boolean)(upsert),
                           (boolean)(a22),
                           ((com.mongodb.WriteConcern)(concern)),
                           ((com.mongodb.DBEncoder)(encoder)));
            
//#line 182 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2900;
        }
        
        
//#line 199 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public com.mongodb.WriteResult
                                                                                                             update(
                                                                                                             final com.mongodb.DBObject query,
                                                                                                             final com.mongodb.DBObject mod,
                                                                                                             final boolean upsert,
                                                                                                             final boolean multi){
            
//#line 200 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2901 =
              ((com.mongodb.DBCollection)(original));
            
//#line 200 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.WriteResult t2902 =
              t2901.update(((com.mongodb.DBObject)(query)),
                           ((com.mongodb.DBObject)(mod)),
                           (boolean)(upsert),
                           (boolean)(multi));
            
//#line 200 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2902;
        }
        
        
//#line 214 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public com.mongodb.WriteResult
                                                                                                             update(
                                                                                                             final com.mongodb.DBObject query,
                                                                                                             final com.mongodb.DBObject mod){
            
//#line 214 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2903 =
              ((com.mongodb.DBCollection)(original));
            
//#line 214 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.WriteResult t2904 =
              t2903.update(((com.mongodb.DBObject)(query)),
                           ((com.mongodb.DBObject)(mod)),
                           (boolean)(false),
                           (boolean)(false));
            
//#line 214 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2904;
        }
        
        
//#line 232 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public com.mongodb.WriteResult
                                                                                                             updateMulti(
                                                                                                             final com.mongodb.DBObject query,
                                                                                                             final com.mongodb.DBObject mod){
            
//#line 232 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2905 =
              ((com.mongodb.DBCollection)(original));
            
//#line 232 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.WriteResult t2906 =
              t2905.update(((com.mongodb.DBObject)(query)),
                           ((com.mongodb.DBObject)(mod)),
                           (boolean)(false),
                           (boolean)(true));
            
//#line 232 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2906;
        }
        
        
//#line 242 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public com.mongodb.WriteResult
                                                                                                             remove(
                                                                                                             final com.mongodb.DBObject query,
                                                                                                             final com.mongodb.WriteConcern concern){
            
//#line 242 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2907 =
              ((com.mongodb.DBCollection)(original));
            
//#line 242 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.WriteResult t2908 =
              t2907.remove(((com.mongodb.DBObject)(query)),
                           ((com.mongodb.WriteConcern)(concern)));
            
//#line 242 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2908;
        }
        
        
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public com.mongodb.WriteResult
                                                                                                             remove(
                                                                                                             final com.mongodb.DBObject query,
                                                                                                             final com.mongodb.WriteConcern concern,
                                                                                                             final com.mongodb.DBEncoder encoder){
            
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2909 =
              ((com.mongodb.DBCollection)(original));
            
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.WriteResult t2910 =
              t2909.remove(((com.mongodb.DBObject)(query)),
                           ((com.mongodb.WriteConcern)(concern)),
                           ((com.mongodb.DBEncoder)(encoder)));
            
//#line 250 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2910;
        }
        
        
//#line 260 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public com.mongodb.WriteResult
                                                                                                             remove(
                                                                                                             final com.mongodb.DBObject query){
            
//#line 260 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2911 =
              ((com.mongodb.DBCollection)(original));
            
//#line 260 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.WriteResult t2912 =
              t2911.remove(((com.mongodb.DBObject)(query)));
            
//#line 260 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2912;
        }
        
        
//#line 276 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final public x10.mongo.yak.YakCursor
                                                                                                             find(
                                                                                                             final com.mongodb.DBObject query,
                                                                                                             final com.mongodb.DBObject fields,
                                                                                                             final int numToSkip,
                                                                                                             final int batchSize,
                                                                                                             final int options){
            
//#line 276 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakUtil t2914 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCollection.get$y()));
            
//#line 276 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2913 =
              ((com.mongodb.DBCollection)(original));
            
//#line 276 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCursor t2915 =
              t2913.find(((com.mongodb.DBObject)(query)),
                         ((com.mongodb.DBObject)(fields)),
                         (int)(numToSkip),
                         (int)(batchSize),
                         (int)(options));
            
//#line 276 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakCursor t2916 =
              t2914.$apply(((com.mongodb.DBCursor)(t2915)));
            
//#line 276 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2916;
        }
        
        
//#line 288 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final public x10.mongo.yak.YakCursor
                                                                                                             find(
                                                                                                             final com.mongodb.DBObject query,
                                                                                                             final com.mongodb.DBObject fields,
                                                                                                             final int numToSkip,
                                                                                                             final int batchSize){
            
//#line 288 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakUtil t2918 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCollection.get$y()));
            
//#line 288 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2917 =
              ((com.mongodb.DBCollection)(original));
            
//#line 288 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCursor t2919 =
              t2917.find(((com.mongodb.DBObject)(query)),
                         ((com.mongodb.DBObject)(fields)),
                         (int)(numToSkip),
                         (int)(batchSize));
            
//#line 288 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakCursor t2920 =
              t2918.$apply(((com.mongodb.DBCursor)(t2919)));
            
//#line 288 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2920;
        }
        
        
//#line 294 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final public x10.mongo.yak.YakMap
                                                                                                             findOne(
                                                                                                             final java.lang.Object query){
            
//#line 294 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakUtil t2922 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCollection.get$y()));
            
//#line 294 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2921 =
              ((com.mongodb.DBCollection)(original));
            
//#line 294 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBObject t2923 =
              t2921.findOne(((java.lang.Object)(query)));
            
//#line 294 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakMap t2924 =
              t2922.$apply(((com.mongodb.DBObject)(t2923)));
            
//#line 294 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2924;
        }
        
        
//#line 304 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final public x10.mongo.yak.YakMap
                                                                                                             findOne(
                                                                                                             final java.lang.Object query,
                                                                                                             final com.mongodb.DBObject fields){
            
//#line 304 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakUtil t2926 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCollection.get$y()));
            
//#line 304 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2925 =
              ((com.mongodb.DBCollection)(original));
            
//#line 304 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBObject t2927 =
              t2925.findOne(((java.lang.Object)(query)),
                            ((com.mongodb.DBObject)(fields)));
            
//#line 304 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakMap t2928 =
              t2926.$apply(((com.mongodb.DBObject)(t2927)));
            
//#line 304 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2928;
        }
        
        
//#line 316 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public x10.mongo.yak.YakMap
                                                                                                             findAndModify(
                                                                                                             final com.mongodb.DBObject query,
                                                                                                             final com.mongodb.DBObject fields,
                                                                                                             final com.mongodb.DBObject sort,
                                                                                                             final boolean remove,
                                                                                                             final com.mongodb.DBObject mod,
                                                                                                             final boolean returnNew,
                                                                                                             final boolean upsert){
            
//#line 316 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakUtil t2930 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCollection.get$y()));
            
//#line 316 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2929 =
              ((com.mongodb.DBCollection)(original));
            
//#line 316 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBObject t2931 =
              t2929.findAndModify(((com.mongodb.DBObject)(query)),
                                  ((com.mongodb.DBObject)(fields)),
                                  ((com.mongodb.DBObject)(sort)),
                                  (boolean)(remove),
                                  ((com.mongodb.DBObject)(mod)),
                                  (boolean)(returnNew),
                                  (boolean)(upsert));
            
//#line 316 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakMap t2932 =
              t2930.$apply(((com.mongodb.DBObject)(t2931)));
            
//#line 316 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2932;
        }
        
        
//#line 324 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public x10.mongo.yak.YakMap
                                                                                                             findAndModify(
                                                                                                             final com.mongodb.DBObject query,
                                                                                                             final com.mongodb.DBObject sort,
                                                                                                             final com.mongodb.DBObject mod){
            
//#line 324 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakUtil t2934 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCollection.get$y()));
            
//#line 324 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2933 =
              ((com.mongodb.DBCollection)(original));
            
//#line 324 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBObject t2935 =
              t2933.findAndModify(((com.mongodb.DBObject)(query)),
                                  ((com.mongodb.DBObject)(sort)),
                                  ((com.mongodb.DBObject)(mod)));
            
//#line 324 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakMap t2936 =
              t2934.$apply(((com.mongodb.DBObject)(t2935)));
            
//#line 324 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2936;
        }
        
        
//#line 332 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public x10.mongo.yak.YakMap
                                                                                                             findAndModify(
                                                                                                             final com.mongodb.DBObject query,
                                                                                                             final com.mongodb.DBObject mod){
            
//#line 332 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakUtil t2938 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCollection.get$y()));
            
//#line 332 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2937 =
              ((com.mongodb.DBCollection)(original));
            
//#line 332 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBObject t2939 =
              t2937.findAndModify(((com.mongodb.DBObject)(query)),
                                  ((com.mongodb.DBObject)(mod)));
            
//#line 332 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakMap t2940 =
              t2938.$apply(((com.mongodb.DBObject)(t2939)));
            
//#line 332 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2940;
        }
        
        
//#line 341 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public x10.mongo.yak.YakMap
                                                                                                             findAndRemove(
                                                                                                             final com.mongodb.DBObject query){
            
//#line 341 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakUtil t2942 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCollection.get$y()));
            
//#line 341 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2941 =
              ((com.mongodb.DBCollection)(original));
            
//#line 341 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBObject t2943 =
              t2941.findAndRemove(((com.mongodb.DBObject)(query)));
            
//#line 341 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakMap t2944 =
              t2942.$apply(((com.mongodb.DBObject)(t2943)));
            
//#line 341 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2944;
        }
        
        
//#line 348 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final public void
                                                                                                             createIndex(
                                                                                                             final com.mongodb.DBObject keys){
            
//#line 348 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2945 =
              ((com.mongodb.DBCollection)(original));
            
//#line 348 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
t2945.createIndex(((com.mongodb.DBObject)(keys)));
        }
        
        
//#line 354 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public void
                                                                                                             createIndex(
                                                                                                             final com.mongodb.DBObject keys,
                                                                                                             final com.mongodb.DBObject options){
            
//#line 354 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2946 =
              ((com.mongodb.DBCollection)(original));
            
//#line 354 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
t2946.createIndex(((com.mongodb.DBObject)(keys)),
                                                                                                                                 ((com.mongodb.DBObject)(options)));
        }
        
        
//#line 362 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public void
                                                                                                             createIndex(
                                                                                                             final com.mongodb.DBObject keys,
                                                                                                             final com.mongodb.DBObject options,
                                                                                                             final com.mongodb.DBEncoder encoder){
            
//#line 362 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2947 =
              ((com.mongodb.DBCollection)(original));
            
//#line 362 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
t2947.createIndex(((com.mongodb.DBObject)(keys)),
                                                                                                                                 ((com.mongodb.DBObject)(options)),
                                                                                                                                 ((com.mongodb.DBEncoder)(encoder)));
        }
        
        
//#line 368 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final public void
                                                                                                             ensureIndex(
                                                                                                             final java.lang.String fieldName){
            
//#line 368 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2948 =
              ((com.mongodb.DBCollection)(original));
            
//#line 368 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
t2948.ensureIndex(((java.lang.String)(fieldName)));
        }
        
        
//#line 374 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final public void
                                                                                                             ensureIndex(
                                                                                                             final com.mongodb.DBObject keys){
            
//#line 374 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2949 =
              ((com.mongodb.DBCollection)(original));
            
//#line 374 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
t2949.ensureIndex(((com.mongodb.DBObject)(keys)));
        }
        
        
//#line 381 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public void
                                                                                                             ensureIndex(
                                                                                                             final com.mongodb.DBObject keys,
                                                                                                             final java.lang.String name){
            
//#line 381 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2950 =
              ((com.mongodb.DBCollection)(original));
            
//#line 381 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
t2950.ensureIndex(((com.mongodb.DBObject)(keys)),
                                                                                                                                 ((java.lang.String)(name)));
        }
        
        
//#line 389 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public void
                                                                                                             ensureIndex(
                                                                                                             final com.mongodb.DBObject keys,
                                                                                                             final java.lang.String name,
                                                                                                             final boolean unique){
            
//#line 389 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2951 =
              ((com.mongodb.DBCollection)(original));
            
//#line 389 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
t2951.ensureIndex(((com.mongodb.DBObject)(keys)),
                                                                                                                                 ((java.lang.String)(name)),
                                                                                                                                 (boolean)(unique));
        }
        
        
//#line 391 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final public void
                                                                                                             ensureIndex(
                                                                                                             final com.mongodb.DBObject keys,
                                                                                                             final com.mongodb.DBObject a87){
            
//#line 391 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2952 =
              ((com.mongodb.DBCollection)(original));
            
//#line 391 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
t2952.ensureIndex(((com.mongodb.DBObject)(keys)),
                                                                                                                                 ((com.mongodb.DBObject)(a87)));
        }
        
        
//#line 397 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public void
                                                                                                             resetIndexCache(
                                                                                                             ){
            
//#line 397 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2953 =
              ((com.mongodb.DBCollection)(original));
            
//#line 397 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
t2953.resetIndexCache();
        }
        
        
//#line 405 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public void
                                                                                                             setHintFields__0$1x10$mongo$yak$YakMap$2(
                                                                                                             final x10.util.List listOfHints){
            
//#line 405 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2954 =
              ((com.mongodb.DBCollection)(original));
            
//#line 405 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final java.util.List t2955 =
              x10.mongo.yak.YakUtil.<x10.mongo.yak.YakMap>convert__0$1x10$mongo$yak$YakUtil$$T$2(x10.mongo.yak.YakMap.$RTT, ((x10.util.List)(listOfHints)));
            
//#line 405 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
t2954.setHintFields(((java.util.List)(t2955)));
        }
        
        
//#line 414 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final public x10.mongo.yak.YakCursor
                                                                                                             find(
                                                                                                             final com.mongodb.DBObject query,
                                                                                                             final com.mongodb.DBObject fields){
            
//#line 414 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakUtil t2957 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCollection.get$y()));
            
//#line 414 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2956 =
              ((com.mongodb.DBCollection)(original));
            
//#line 414 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCursor t2958 =
              t2956.find(((com.mongodb.DBObject)(query)),
                         ((com.mongodb.DBObject)(fields)));
            
//#line 414 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakCursor t2959 =
              t2957.$apply(((com.mongodb.DBCursor)(t2958)));
            
//#line 414 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2959;
        }
        
        
//#line 421 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final public x10.mongo.yak.YakCursor
                                                                                                             find(
                                                                                                             final com.mongodb.DBObject query){
            
//#line 421 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakUtil t2961 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCollection.get$y()));
            
//#line 421 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2960 =
              ((com.mongodb.DBCollection)(original));
            
//#line 421 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCursor t2962 =
              t2960.find(((com.mongodb.DBObject)(query)));
            
//#line 421 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakCursor t2963 =
              t2961.$apply(((com.mongodb.DBCursor)(t2962)));
            
//#line 421 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2963;
        }
        
        
//#line 426 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final public x10.mongo.yak.YakCursor
                                                                                                             find(
                                                                                                             ){
            
//#line 426 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakUtil t2965 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCollection.get$y()));
            
//#line 426 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2964 =
              ((com.mongodb.DBCollection)(original));
            
//#line 426 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCursor t2966 =
              t2964.find();
            
//#line 426 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakCursor t2967 =
              t2965.$apply(((com.mongodb.DBCursor)(t2966)));
            
//#line 426 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2967;
        }
        
        
//#line 431 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final public x10.mongo.yak.YakMap
                                                                                                             findOne(
                                                                                                             ){
            
//#line 431 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakUtil t2969 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCollection.get$y()));
            
//#line 431 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2968 =
              ((com.mongodb.DBCollection)(original));
            
//#line 431 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBObject t2970 =
              t2968.findOne();
            
//#line 431 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakMap t2971 =
              t2969.$apply(((com.mongodb.DBObject)(t2970)));
            
//#line 431 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2971;
        }
        
        
//#line 437 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final public x10.mongo.yak.YakMap
                                                                                                             findOne(
                                                                                                             final com.mongodb.DBObject query){
            
//#line 437 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakUtil t2973 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCollection.get$y()));
            
//#line 437 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2972 =
              ((com.mongodb.DBCollection)(original));
            
//#line 437 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBObject t2974 =
              t2972.findOne(((com.mongodb.DBObject)(query)));
            
//#line 437 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakMap t2975 =
              t2973.$apply(((com.mongodb.DBObject)(t2974)));
            
//#line 437 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2975;
        }
        
        
//#line 444 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final public x10.mongo.yak.YakMap
                                                                                                             findOne(
                                                                                                             final com.mongodb.DBObject query,
                                                                                                             final com.mongodb.DBObject fields){
            
//#line 444 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakUtil t2977 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCollection.get$y()));
            
//#line 444 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2976 =
              ((com.mongodb.DBCollection)(original));
            
//#line 444 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBObject t2978 =
              t2976.findOne(((com.mongodb.DBObject)(query)),
                            ((com.mongodb.DBObject)(fields)));
            
//#line 444 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakMap t2979 =
              t2977.$apply(((com.mongodb.DBObject)(t2978)));
            
//#line 444 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2979;
        }
        
        
//#line 452 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final public x10.mongo.yak.YakMap
                                                                                                             findOne(
                                                                                                             final com.mongodb.DBObject query,
                                                                                                             final com.mongodb.DBObject fields,
                                                                                                             final com.mongodb.ReadPreference readPreference){
            
//#line 452 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakUtil t2981 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCollection.get$y()));
            
//#line 452 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2980 =
              ((com.mongodb.DBCollection)(original));
            
//#line 452 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBObject t2982 =
              t2980.findOne(((com.mongodb.DBObject)(query)),
                            ((com.mongodb.DBObject)(fields)),
                            ((com.mongodb.ReadPreference)(readPreference)));
            
//#line 452 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakMap t2983 =
              t2981.$apply(((com.mongodb.DBObject)(t2982)));
            
//#line 452 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2983;
        }
        
        
//#line 460 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final public java.lang.Object
                                                                                                             apply(
                                                                                                             final com.mongodb.DBObject a103){
            
//#line 460 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2984 =
              ((com.mongodb.DBCollection)(original));
            
//#line 460 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final java.lang.Object t2985 =
              t2984.apply(((com.mongodb.DBObject)(a103)));
            
//#line 460 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2985;
        }
        
        
//#line 466 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final public java.lang.Object
                                                                                                             apply(
                                                                                                             final com.mongodb.DBObject a104,
                                                                                                             final boolean a105){
            
//#line 466 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2986 =
              ((com.mongodb.DBCollection)(original));
            
//#line 466 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final java.lang.Object t2987 =
              t2986.apply(((com.mongodb.DBObject)(a104)),
                          (boolean)(a105));
            
//#line 466 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2987;
        }
        
        
//#line 474 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final public com.mongodb.WriteResult
                                                                                                             save(
                                                                                                             final com.mongodb.DBObject newRecord,
                                                                                                             final com.mongodb.WriteConcern concern){
            
//#line 474 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2988 =
              ((com.mongodb.DBCollection)(original));
            
//#line 474 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.WriteResult t2989 =
              t2988.save(((com.mongodb.DBObject)(newRecord)),
                         ((com.mongodb.WriteConcern)(concern)));
            
//#line 474 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2989;
        }
        
        
//#line 480 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final public com.mongodb.WriteResult
                                                                                                             save(
                                                                                                             final com.mongodb.DBObject newRecord){
            
//#line 480 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2990 =
              ((com.mongodb.DBCollection)(original));
            
//#line 480 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.WriteResult t2991 =
              t2990.save(((com.mongodb.DBObject)(newRecord)));
            
//#line 480 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2991;
        }
        
        
//#line 486 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public void
                                                                                                             dropIndexes(
                                                                                                             ){
            
//#line 486 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2992 =
              ((com.mongodb.DBCollection)(original));
            
//#line 486 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
t2992.dropIndexes();
        }
        
        
//#line 492 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public void
                                                                                                             dropIndexes(
                                                                                                             final java.lang.String name){
            
//#line 492 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2993 =
              ((com.mongodb.DBCollection)(original));
            
//#line 492 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
t2993.dropIndexes(((java.lang.String)(name)));
        }
        
        
//#line 497 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public void
                                                                                                             drop(
                                                                                                             ){
            
//#line 497 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2994 =
              ((com.mongodb.DBCollection)(original));
            
//#line 497 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
t2994.drop();
        }
        
        
//#line 502 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public long
                                                                                                             count$O(
                                                                                                             ){
            
//#line 502 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2995 =
              ((com.mongodb.DBCollection)(original));
            
//#line 502 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final long t2996 =
              t2995.count();
            
//#line 502 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2996;
        }
        
        
//#line 509 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public long
                                                                                                             count$O(
                                                                                                             final com.mongodb.DBObject query){
            
//#line 509 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2997 =
              ((com.mongodb.DBCollection)(original));
            
//#line 509 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final long t2998 =
              t2997.count(((com.mongodb.DBObject)(query)));
            
//#line 509 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t2998;
        }
        
        
//#line 516 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public long
                                                                                                             getCount$O(
                                                                                                             ){
            
//#line 516 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t2999 =
              ((com.mongodb.DBCollection)(original));
            
//#line 516 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final long t3000 =
              t2999.getCount();
            
//#line 516 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3000;
        }
        
        
//#line 522 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public long
                                                                                                             getCount$O(
                                                                                                             final com.mongodb.DBObject query){
            
//#line 522 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3001 =
              ((com.mongodb.DBCollection)(original));
            
//#line 522 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final long t3002 =
              t3001.getCount(((com.mongodb.DBObject)(query)));
            
//#line 522 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3002;
        }
        
        
//#line 532 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public long
                                                                                                             getCount$O(
                                                                                                             final com.mongodb.DBObject query,
                                                                                                             final com.mongodb.DBObject fields){
            
//#line 532 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3003 =
              ((com.mongodb.DBCollection)(original));
            
//#line 532 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final long t3004 =
              t3003.getCount(((com.mongodb.DBObject)(query)),
                             ((com.mongodb.DBObject)(fields)));
            
//#line 532 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3004;
        }
        
        
//#line 546 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public long
                                                                                                             getCount$O(
                                                                                                             final com.mongodb.DBObject query,
                                                                                                             final com.mongodb.DBObject fields,
                                                                                                             final long limit,
                                                                                                             final long skip){
            
//#line 546 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3005 =
              ((com.mongodb.DBCollection)(original));
            
//#line 546 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final long t3006 =
              t3005.getCount(((com.mongodb.DBObject)(query)),
                             ((com.mongodb.DBObject)(fields)),
                             (long)(limit),
                             (long)(skip));
            
//#line 546 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3006;
        }
        
        
//#line 553 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public x10.mongo.yak.YakCollection
                                                                                                             rename(
                                                                                                             final java.lang.String newName){
            
//#line 554 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakCollection t3007 =
              this.rename(((java.lang.String)(newName)),
                          (boolean)(false));
            
//#line 554 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3007;
        }
        
        
//#line 565 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public x10.mongo.yak.YakCollection
                                                                                                             rename(
                                                                                                             final java.lang.String newName,
                                                                                                             final boolean dropExistingCollectionWithThatName){
            
//#line 566 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
x10.mongo.yak.YakCollection res =
              this;
            
//#line 567 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
try {{
                
//#line 568 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakUtil t3009 =
                  ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCollection.get$y()));
                
//#line 568 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3008 =
                  ((com.mongodb.DBCollection)(original));
                
//#line 568 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3010 =
                  t3008.rename(((java.lang.String)(newName)),
                               (boolean)(dropExistingCollectionWithThatName));
                
//#line 568 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakCollection t3011 =
                  t3009.$apply(((com.mongodb.DBCollection)(t3010)));
                
//#line 568 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
res = t3011;
            }}catch (x10.runtime.impl.java.UnknownJavaThrowable $ex) {
            if ($ex.getCause() instanceof java.lang.Throwable) {
            final java.lang.Throwable e = (java.lang.Throwable) $ex.getCause();
            {
                
//#line 571 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final java.lang.String t3012 =
                  e.toString();
                
//#line 571 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.lang.Exception t3013 =
                  ((x10.lang.Exception)(new x10.lang.Exception(t3012)));
                
//#line 571 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
throw t3013;
            }
            }
            else {
            throw $ex;
            }
            }catch (x10.core.Throwable $ex) {
            throw $ex;
            }catch (final java.lang.Throwable e) {
                
//#line 571 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final java.lang.String t3012 =
                  e.toString();
                
//#line 571 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.lang.Exception t3013 =
                  ((x10.lang.Exception)(new x10.lang.Exception(t3012)));
                
//#line 571 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
throw t3013;
            }
            
//#line 573 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakCollection t3014 =
              res;
            
//#line 573 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3014;
        }
        
        
//#line 592 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public x10.mongo.yak.YakMap
                                                                                                             group(
                                                                                                             final com.mongodb.DBObject key,
                                                                                                             final com.mongodb.DBObject cond,
                                                                                                             final com.mongodb.DBObject initial,
                                                                                                             final java.lang.String reducer,
                                                                                                             final java.lang.String finalizer){
            
//#line 592 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakUtil t3016 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCollection.get$y()));
            
//#line 592 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3015 =
              ((com.mongodb.DBCollection)(original));
            
//#line 592 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBObject t3017 =
              t3015.group(((com.mongodb.DBObject)(key)),
                          ((com.mongodb.DBObject)(cond)),
                          ((com.mongodb.DBObject)(initial)),
                          ((java.lang.String)(reducer)),
                          ((java.lang.String)(finalizer)));
            
//#line 592 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakMap t3018 =
              t3016.$apply(((com.mongodb.DBObject)(t3017)));
            
//#line 592 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3018;
        }
        
        
//#line 607 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public com.mongodb.DBObject
                                                                                                             group(
                                                                                                             final com.mongodb.DBObject key,
                                                                                                             final com.mongodb.DBObject cond,
                                                                                                             final com.mongodb.DBObject initial,
                                                                                                             final java.lang.String reducer){
            
//#line 607 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakUtil t3020 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCollection.get$y()));
            
//#line 607 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3019 =
              ((com.mongodb.DBCollection)(original));
            
//#line 607 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBObject t3021 =
              t3019.group(((com.mongodb.DBObject)(key)),
                          ((com.mongodb.DBObject)(cond)),
                          ((com.mongodb.DBObject)(initial)),
                          ((java.lang.String)(reducer)));
            
//#line 607 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakMap t3022 =
              t3020.$apply(((com.mongodb.DBObject)(t3021)));
            
//#line 607 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3022;
        }
        
        
//#line 614 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public x10.mongo.yak.YakMap
                                                                                                             group(
                                                                                                             final com.mongodb.GroupCommand groupCommand){
            
//#line 614 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakUtil t3024 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCollection.get$y()));
            
//#line 614 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3023 =
              ((com.mongodb.DBCollection)(original));
            
//#line 614 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBObject t3025 =
              t3023.group(((com.mongodb.GroupCommand)(groupCommand)));
            
//#line 614 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakMap t3026 =
              t3024.$apply(((com.mongodb.DBObject)(t3025)));
            
//#line 614 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3026;
        }
        
        
//#line 621 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public x10.mongo.yak.YakMap
                                                                                                             group(
                                                                                                             final com.mongodb.DBObject key){
            
//#line 621 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakUtil t3028 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCollection.get$y()));
            
//#line 621 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3027 =
              ((com.mongodb.DBCollection)(original));
            
//#line 621 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBObject t3029 =
              t3027.group(((com.mongodb.DBObject)(key)));
            
//#line 621 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakMap t3030 =
              t3028.$apply(((com.mongodb.DBObject)(t3029)));
            
//#line 621 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3030;
        }
        
        
//#line 630 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public java.util.List
                                                                                                             distinct(
                                                                                                             final java.lang.String key,
                                                                                                             final com.mongodb.DBObject query){
            
//#line 630 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3031 =
              ((com.mongodb.DBCollection)(original));
            
//#line 630 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final java.util.List t3032 =
              t3031.distinct(((java.lang.String)(key)),
                             ((com.mongodb.DBObject)(query)));
            
//#line 630 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3032;
        }
        
        
//#line 636 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public java.util.List
                                                                                                             distinct(
                                                                                                             final java.lang.String key){
            
//#line 636 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3033 =
              ((com.mongodb.DBCollection)(original));
            
//#line 636 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final java.util.List t3034 =
              t3033.distinct(((java.lang.String)(key)));
            
//#line 636 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3034;
        }
        
        
//#line 647 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public x10.mongo.yak.YakMapReduceOutput
                                                                                                             mapReduce(
                                                                                                             final java.lang.String map,
                                                                                                             final java.lang.String reduce,
                                                                                                             final java.lang.String outputTarget,
                                                                                                             final com.mongodb.MapReduceCommand.OutputType outputType,
                                                                                                             final com.mongodb.DBObject query){
            
//#line 647 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakUtil t3036 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCollection.get$y()));
            
//#line 647 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3035 =
              ((com.mongodb.DBCollection)(original));
            
//#line 647 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.MapReduceOutput t3037 =
              t3035.mapReduce(((java.lang.String)(map)),
                              ((java.lang.String)(reduce)),
                              ((java.lang.String)(outputTarget)),
                              ((com.mongodb.MapReduceCommand.OutputType)(outputType)),
                              ((com.mongodb.DBObject)(query)));
            
//#line 647 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakMapReduceOutput t3038 =
              t3036.$apply(((com.mongodb.MapReduceOutput)(t3037)));
            
//#line 647 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3038;
        }
        
        
//#line 659 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public x10.mongo.yak.YakMapReduceOutput
                                                                                                             mapReduce(
                                                                                                             final java.lang.String map,
                                                                                                             final java.lang.String reduce,
                                                                                                             final java.lang.String outputTarget,
                                                                                                             final com.mongodb.DBObject query){
            
//#line 659 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakUtil t3040 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCollection.get$y()));
            
//#line 659 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3039 =
              ((com.mongodb.DBCollection)(original));
            
//#line 659 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.MapReduceOutput t3041 =
              t3039.mapReduce(((java.lang.String)(map)),
                              ((java.lang.String)(reduce)),
                              ((java.lang.String)(outputTarget)),
                              ((com.mongodb.DBObject)(query)));
            
//#line 659 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakMapReduceOutput t3042 =
              t3040.$apply(((com.mongodb.MapReduceOutput)(t3041)));
            
//#line 659 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3042;
        }
        
        
//#line 668 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public x10.mongo.yak.YakMapReduceOutput
                                                                                                             mapReduce(
                                                                                                             final com.mongodb.MapReduceCommand mapReduceCommand){
            
//#line 668 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakUtil t3044 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCollection.get$y()));
            
//#line 668 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3043 =
              ((com.mongodb.DBCollection)(original));
            
//#line 668 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.MapReduceOutput t3045 =
              t3043.mapReduce(((com.mongodb.MapReduceCommand)(mapReduceCommand)));
            
//#line 668 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakMapReduceOutput t3046 =
              t3044.$apply(((com.mongodb.MapReduceOutput)(t3045)));
            
//#line 668 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3046;
        }
        
        
//#line 675 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public x10.mongo.yak.YakMapReduceOutput
                                                                                                             mapReduce(
                                                                                                             final com.mongodb.DBObject command){
            
//#line 675 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakUtil t3048 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCollection.get$y()));
            
//#line 675 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3047 =
              ((com.mongodb.DBCollection)(original));
            
//#line 675 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.MapReduceOutput t3049 =
              t3047.mapReduce(((com.mongodb.DBObject)(command)));
            
//#line 675 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakMapReduceOutput t3050 =
              t3048.$apply(((com.mongodb.MapReduceOutput)(t3049)));
            
//#line 675 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3050;
        }
        
        
//#line 682 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public x10.util.List
                                                                                                             getIndexInfo(
                                                                                                             ){
            
//#line 682 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3051 =
              ((com.mongodb.DBCollection)(original));
            
//#line 682 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final java.util.List t3052 =
              t3051.getIndexInfo();
            
//#line 682 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.util.List t3053 =
              x10.mongo.yak.YakUtil.convertListOfDBObjects(((java.util.List)(t3052)));
            
//#line 682 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3053;
        }
        
        
//#line 689 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public void
                                                                                                             dropIndex(
                                                                                                             final com.mongodb.DBObject keys){
            
//#line 689 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3054 =
              ((com.mongodb.DBCollection)(original));
            
//#line 689 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
t3054.dropIndex(((com.mongodb.DBObject)(keys)));
        }
        
        
//#line 695 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public void
                                                                                                             dropIndex(
                                                                                                             final java.lang.String indexName){
            
//#line 695 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3055 =
              ((com.mongodb.DBCollection)(original));
            
//#line 695 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
t3055.dropIndex(((java.lang.String)(indexName)));
        }
        
        
//#line 701 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public com.mongodb.CommandResult
                                                                                                             getStats(
                                                                                                             ){
            
//#line 701 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3056 =
              ((com.mongodb.DBCollection)(original));
            
//#line 701 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.CommandResult t3057 =
              t3056.getStats();
            
//#line 701 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3057;
        }
        
        
//#line 706 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public boolean
                                                                                                             isCapped$O(
                                                                                                             ){
            
//#line 706 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3058 =
              ((com.mongodb.DBCollection)(original));
            
//#line 706 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final boolean t3059 =
              t3058.isCapped();
            
//#line 706 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3059;
        }
        
        
//#line 715 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public x10.mongo.yak.YakCollection
                                                                                                             getCollection(
                                                                                                             final java.lang.String subname){
            
//#line 715 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakUtil t3061 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakCollection.get$y()));
            
//#line 715 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3060 =
              ((com.mongodb.DBCollection)(original));
            
//#line 715 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3062 =
              t3060.getCollection(((java.lang.String)(subname)));
            
//#line 715 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final x10.mongo.yak.YakCollection t3063 =
              t3061.$apply(((com.mongodb.DBCollection)(t3062)));
            
//#line 715 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3063;
        }
        
        
//#line 723 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public java.lang.String
                                                                                                             getName$O(
                                                                                                             ){
            
//#line 723 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3064 =
              ((com.mongodb.DBCollection)(original));
            
//#line 723 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final java.lang.String t3065 =
              t3064.getName();
            
//#line 723 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3065;
        }
        
        
//#line 729 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public java.lang.String
                                                                                                             getFullName$O(
                                                                                                             ){
            
//#line 729 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3066 =
              ((com.mongodb.DBCollection)(original));
            
//#line 729 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final java.lang.String t3067 =
              t3066.getFullName();
            
//#line 729 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3067;
        }
        
        
//#line 734 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public com.mongodb.DB
                                                                                                             getDB(
                                                                                                             ){
            
//#line 734 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3068 =
              ((com.mongodb.DBCollection)(original));
            
//#line 734 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DB t3069 =
              t3068.getDB();
            
//#line 734 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3069;
        }
        
        
//#line 740 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public int
                                                                                                             hashCode(
                                                                                                             ){
            
//#line 740 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3070 =
              ((com.mongodb.DBCollection)(original));
            
//#line 740 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final int t3071 =
              t3070.hashCode();
            
//#line 740 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3071;
        }
        
        
//#line 745 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public boolean
                                                                                                             equals(
                                                                                                             final java.lang.Object other){
            
//#line 745 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3072 =
              ((com.mongodb.DBCollection)(original));
            
//#line 745 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final boolean t3073 =
              t3072.equals(((java.lang.Object)(other)));
            
//#line 745 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3073;
        }
        
        
//#line 749 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public java.lang.String
                                                                                                             toString(
                                                                                                             ){
            
//#line 749 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3074 =
              ((com.mongodb.DBCollection)(original));
            
//#line 749 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final java.lang.String t3075 =
              t3074.toString();
            
//#line 749 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3075;
        }
        
        
//#line 760 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public void
                                                                                                             setWriteConcern(
                                                                                                             final com.mongodb.WriteConcern concern){
            
//#line 760 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3076 =
              ((com.mongodb.DBCollection)(original));
            
//#line 760 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
t3076.setWriteConcern(((com.mongodb.WriteConcern)(concern)));
        }
        
        
//#line 764 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public com.mongodb.WriteConcern
                                                                                                             getWriteConcern(
                                                                                                             ){
            
//#line 764 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3077 =
              ((com.mongodb.DBCollection)(original));
            
//#line 764 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.WriteConcern t3078 =
              t3077.getWriteConcern();
            
//#line 764 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3078;
        }
        
        
//#line 769 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public void
                                                                                                             setReadPreference(
                                                                                                             final com.mongodb.ReadPreference pref){
            
//#line 769 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3079 =
              ((com.mongodb.DBCollection)(original));
            
//#line 769 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
t3079.setReadPreference(((com.mongodb.ReadPreference)(pref)));
        }
        
        
//#line 773 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public com.mongodb.ReadPreference
                                                                                                             getReadPreference(
                                                                                                             ){
            
//#line 773 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3080 =
              ((com.mongodb.DBCollection)(original));
            
//#line 773 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.ReadPreference t3081 =
              t3080.getReadPreference();
            
//#line 773 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3081;
        }
        
        
//#line 782 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public void
                                                                                                             addOption(
                                                                                                             final int option){
            
//#line 782 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3082 =
              ((com.mongodb.DBCollection)(original));
            
//#line 782 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
t3082.addOption((int)(option));
        }
        
        
//#line 789 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public void
                                                                                                             setOptions(
                                                                                                             final int option){
            
//#line 789 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3083 =
              ((com.mongodb.DBCollection)(original));
            
//#line 789 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
t3083.setOptions((int)(option));
        }
        
        
//#line 795 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public void
                                                                                                             resetOptions(
                                                                                                             ){
            
//#line 795 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3084 =
              ((com.mongodb.DBCollection)(original));
            
//#line 795 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
t3084.resetOptions();
        }
        
        
//#line 801 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
public int
                                                                                                             getOptions$O(
                                                                                                             ){
            
//#line 801 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final com.mongodb.DBCollection t3085 =
              ((com.mongodb.DBCollection)(original));
            
//#line 801 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final int t3086 =
              t3085.getOptions();
            
//#line 801 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return t3086;
        }
        
        
//#line 42 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
final public x10.mongo.yak.YakCollection
                                                                                                            x10$mongo$yak$YakCollection$$x10$mongo$yak$YakCollection$this(
                                                                                                            ){
            
//#line 42 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakCollection.x10"
return x10.mongo.yak.YakCollection.this;
        }
        
        public static short fieldId$y;
        final public static x10.core.concurrent.AtomicInteger initStatus$y = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        
        public static void
          getDeserialized$y(
          x10.x10rt.X10JavaDeserializer deserializer){
            x10.mongo.yak.YakCollection.y = ((x10.mongo.yak.YakUtil)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
            x10.mongo.yak.YakCollection.initStatus$y.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static x10.mongo.yak.YakUtil
          get$y(
          ){
            if (((int) x10.mongo.yak.YakCollection.initStatus$y.get()) ==
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                return x10.mongo.yak.YakCollection.y;
            }
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0) &&
                  x10.mongo.yak.YakCollection.initStatus$y.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                         (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                x10.mongo.yak.YakCollection.y = x10.mongo.yak.YakUtil.it();
                if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                      ((boolean) true)) {
                    x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.mongo.yak.YakCollection.y")));
                }
                x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.mongo.yak.YakCollection.y)),
                                                                          (short)(x10.mongo.yak.YakCollection.fieldId$y));
                x10.mongo.yak.YakCollection.initStatus$y.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                x10.runtime.impl.java.InitDispatcher.notifyInitialized();
            } else {
                if (((int) x10.mongo.yak.YakCollection.initStatus$y.get()) !=
                    ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    while (((int) x10.mongo.yak.YakCollection.initStatus$y.get()) !=
                           ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                    }
                    x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                }
            }
            return x10.mongo.yak.YakCollection.y;
        }
        
        static {
                   x10.mongo.yak.YakCollection.fieldId$y = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.mongo.yak.YakCollection")),
                                                                                                                               ((java.lang.String)("y")))))));
               }
    
}
