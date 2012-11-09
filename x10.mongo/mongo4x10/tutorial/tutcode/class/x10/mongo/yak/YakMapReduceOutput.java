package x10.mongo.yak;


@x10.core.X10Generated public class YakMapReduceOutput extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, YakMapReduceOutput.class);
    
    public static final x10.rtt.RuntimeType<YakMapReduceOutput> $RTT = x10.rtt.NamedType.<YakMapReduceOutput> make(
    "x10.mongo.yak.YakMapReduceOutput", /* base class */YakMapReduceOutput.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(YakMapReduceOutput $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + YakMapReduceOutput.class + " calling"); } 
        com.mongodb.MapReduceOutput original = (com.mongodb.MapReduceOutput) $deserializer.readRefUsingReflection();
        $_obj.original = original;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        YakMapReduceOutput $_obj = new YakMapReduceOutput((java.lang.System[]) null);
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
    public YakMapReduceOutput(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 13 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
public com.mongodb.MapReduceOutput original;
        
        
//#line 15 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
// creation method for java code (1-phase java constructor)
        public YakMapReduceOutput(final com.mongodb.MapReduceOutput original){this((java.lang.System[]) null);
                                                                                  x10$mongo$yak$YakMapReduceOutput$$init$S(original);}
        
        // constructor for non-virtual call
        final public x10.mongo.yak.YakMapReduceOutput x10$mongo$yak$YakMapReduceOutput$$init$S(final com.mongodb.MapReduceOutput original) { {
                                                                                                                                                    
//#line 15 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"

                                                                                                                                                    
//#line 15 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"

                                                                                                                                                    
//#line 16 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
this.original = ((com.mongodb.MapReduceOutput)(original));
                                                                                                                                                }
                                                                                                                                                return this;
                                                                                                                                                }
        
        
        
//#line 19 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
public java.lang.String
                                                                                                                 toString(
                                                                                                                 ){
            
//#line 19 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
return "YakMapReduceOutput()";
        }
        
        
//#line 21 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
public static x10.mongo.yak.YakUtil y;
        
        
//#line 23 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
public x10.mongo.yak.YakMap
                                                                                                                 getCommand(
                                                                                                                 ){
            
//#line 23 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
final x10.mongo.yak.YakUtil t4441 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakMapReduceOutput.get$y()));
            
//#line 23 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
final com.mongodb.MapReduceOutput t4440 =
              ((com.mongodb.MapReduceOutput)(original));
            
//#line 23 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
final com.mongodb.DBObject t4442 =
              t4440.getCommand();
            
//#line 23 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
final x10.mongo.yak.YakMap t4443 =
              t4441.$apply(((com.mongodb.DBObject)(t4442)));
            
//#line 23 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
return t4443;
        }
        
        
//#line 24 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
public x10.mongo.yak.YakCollection
                                                                                                                 getOutputCollection(
                                                                                                                 ){
            
//#line 24 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
final x10.mongo.yak.YakUtil t4445 =
              ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakMapReduceOutput.get$y()));
            
//#line 24 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
final com.mongodb.MapReduceOutput t4444 =
              ((com.mongodb.MapReduceOutput)(original));
            
//#line 24 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
final com.mongodb.DBCollection t4446 =
              t4444.getOutputCollection();
            
//#line 24 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
final x10.mongo.yak.YakCollection t4447 =
              t4445.$apply(((com.mongodb.DBCollection)(t4446)));
            
//#line 24 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
return t4447;
        }
        
        
//#line 27 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
public com.mongodb.ServerAddress
                                                                                                                 getServerUsed(
                                                                                                                 ){
            
//#line 27 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
final com.mongodb.MapReduceOutput t4448 =
              ((com.mongodb.MapReduceOutput)(original));
            
//#line 27 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
final com.mongodb.ServerAddress t4449 =
              t4448.getServerUsed();
            
//#line 27 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
return t4449;
        }
        
        
//#line 29 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
public x10.lang.Iterable
                                                                                                                 results(
                                                                                                                 ){
            
//#line 30 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
final com.mongodb.MapReduceOutput t4450 =
              ((com.mongodb.MapReduceOutput)(original));
            
//#line 30 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
final java.lang.Iterable origResults =
              t4450.results();
            
//#line 31 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
final java.util.Iterator origIter =
              origResults.iterator();
            
//#line 32 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
final x10.lang.Iterator resiter =
              ((x10.lang.Iterator)(new x10.mongo.yak.YakMapReduceOutput.Anonymous$899((java.lang.System[]) null).x10$mongo$yak$YakMapReduceOutput$Anonymous$899$$init$S(this,
                                                                                                                                                                        origIter)));
            
//#line 37 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
final x10.mongo.yak.YakMapReduceOutput.Anonymous$1066 res =
              ((x10.mongo.yak.YakMapReduceOutput.Anonymous$1066)(new x10.mongo.yak.YakMapReduceOutput.Anonymous$1066((java.lang.System[]) null).x10$mongo$yak$YakMapReduceOutput$Anonymous$1066$$init$S(this,
                                                                                                                                                                                                        resiter, (x10.mongo.yak.YakMapReduceOutput.Anonymous$1066.__1$1x10$mongo$yak$YakMap$2) null)));
            
//#line 41 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
return res;
        }
        
        
//#line 11 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
final public x10.mongo.yak.YakMapReduceOutput
                                                                                                                 x10$mongo$yak$YakMapReduceOutput$$x10$mongo$yak$YakMapReduceOutput$this(
                                                                                                                 ){
            
//#line 11 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
return x10.mongo.yak.YakMapReduceOutput.this;
        }
        
        
//#line 32 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
@x10.core.X10Generated final public static class Anonymous$899 extends x10.core.Ref implements x10.lang.Iterator, x10.x10rt.X10JavaSerializable
                                                                                                               {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Anonymous$899.class);
            
            public static final x10.rtt.RuntimeType<Anonymous$899> $RTT = x10.rtt.NamedType.<Anonymous$899> make(
            "x10.mongo.yak.YakMapReduceOutput.Anonymous$899", /* base class */Anonymous$899.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Iterator.$RTT, x10.mongo.yak.YakMap.$RTT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(Anonymous$899 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Anonymous$899.class + " calling"); } 
                java.util.Iterator origIter = (java.util.Iterator) $deserializer.readRefUsingReflection();
                $_obj.origIter = origIter;
                x10.mongo.yak.YakMapReduceOutput out$ = (x10.mongo.yak.YakMapReduceOutput) $deserializer.readRef();
                $_obj.out$ = out$;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                Anonymous$899 $_obj = new Anonymous$899((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.writeObjectUsingReflection(this.origIter);
                if (out$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$);
                } else {
                $serializer.write(this.out$);
                }
                
            }
            
            // constructor just for allocation
            public Anonymous$899(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // bridge for method abstract public x10.lang.Iterator.next():T
            public x10.mongo.yak.YakMap
              next$G(){return next();}
            
                
//#line 11 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
public x10.mongo.yak.YakMapReduceOutput out$;
                
//#line 31 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
public java.util.Iterator origIter;
                
                
//#line 33 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
public boolean
                                                                                                                         hasNext$O(
                                                                                                                         ){
                    
//#line 33 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
final java.util.Iterator t4451 =
                      x10.mongo.yak.YakMapReduceOutput.Anonymous$899.this.
                        origIter;
                    
//#line 33 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
final boolean t4452 =
                      t4451.hasNext();
                    
//#line 33 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
return t4452;
                }
                
                
//#line 34 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
public x10.mongo.yak.YakMap
                                                                                                                         next(
                                                                                                                         ){
                    
//#line 34 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
final x10.mongo.yak.YakUtil t4455 =
                      ((x10.mongo.yak.YakUtil)(x10.mongo.yak.YakMapReduceOutput.get$y()));
                    
//#line 34 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
final java.util.Iterator t4453 =
                      x10.mongo.yak.YakMapReduceOutput.Anonymous$899.this.
                        origIter;
                    
//#line 34 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
final java.lang.Object t4454 =
                      t4453.next();
                    
//#line 34 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
final com.mongodb.DBObject t4456 =
                      x10.rtt.Types.<com.mongodb.DBObject> cast(t4454,x10.rtt.Types.getRTT(com.mongodb.DBObject.class));
                    
//#line 34 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
final x10.mongo.yak.YakMap t4457 =
                      t4455.$apply(((com.mongodb.DBObject)(t4456)));
                    
//#line 34 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
return t4457;
                }
                
                
//#line 32 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
// creation method for java code (1-phase java constructor)
                public Anonymous$899(final x10.mongo.yak.YakMapReduceOutput out$,
                                     final java.util.Iterator origIter){this((java.lang.System[]) null);
                                                                            x10$mongo$yak$YakMapReduceOutput$Anonymous$899$$init$S(out$,origIter);}
                
                // constructor for non-virtual call
                final public x10.mongo.yak.YakMapReduceOutput.Anonymous$899 x10$mongo$yak$YakMapReduceOutput$Anonymous$899$$init$S(final x10.mongo.yak.YakMapReduceOutput out$,
                                                                                                                                   final java.util.Iterator origIter) { {
                                                                                                                                                                               
//#line 32 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"

                                                                                                                                                                               
//#line 11 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
this.out$ = out$;
                                                                                                                                                                               
//#line 31 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
this.origIter = origIter;
                                                                                                                                                                           }
                                                                                                                                                                           return this;
                                                                                                                                                                           }
                
            
        }
        
        
//#line 37 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
@x10.core.X10Generated final public static class Anonymous$1066 extends x10.core.Ref implements x10.lang.Iterable, x10.x10rt.X10JavaSerializable
                                                                                                               {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Anonymous$1066.class);
            
            public static final x10.rtt.RuntimeType<Anonymous$1066> $RTT = x10.rtt.NamedType.<Anonymous$1066> make(
            "x10.mongo.yak.YakMapReduceOutput.Anonymous$1066", /* base class */Anonymous$1066.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Iterable.$RTT, x10.mongo.yak.YakMap.$RTT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(Anonymous$1066 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Anonymous$1066.class + " calling"); } 
                x10.lang.Iterator resiter = (x10.lang.Iterator) $deserializer.readRef();
                $_obj.resiter = resiter;
                x10.mongo.yak.YakMapReduceOutput out$ = (x10.mongo.yak.YakMapReduceOutput) $deserializer.readRef();
                $_obj.out$ = out$;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                Anonymous$1066 $_obj = new Anonymous$1066((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (resiter instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.resiter);
                } else {
                $serializer.write(this.resiter);
                }
                if (out$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$);
                } else {
                $serializer.write(this.out$);
                }
                
            }
            
            // constructor just for allocation
            public Anonymous$1066(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            
                
//#line 11 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
public x10.mongo.yak.YakMapReduceOutput out$;
                
//#line 32 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
public x10.lang.Iterator<x10.mongo.yak.YakMap> resiter;
                
                
//#line 38 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
public x10.lang.Iterator
                                                                                                                         iterator(
                                                                                                                         ){
                    
//#line 38 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
final x10.lang.Iterator t4458 =
                      x10.mongo.yak.YakMapReduceOutput.Anonymous$1066.this.
                        resiter;
                    
//#line 38 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
return t4458;
                }
                
                
//#line 37 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
// creation method for java code (1-phase java constructor)
                public Anonymous$1066(final x10.mongo.yak.YakMapReduceOutput out$,
                                      final x10.lang.Iterator<x10.mongo.yak.YakMap> resiter, __1$1x10$mongo$yak$YakMap$2 $dummy){this((java.lang.System[]) null);
                                                                                                                                     x10$mongo$yak$YakMapReduceOutput$Anonymous$1066$$init$S(out$,resiter, (x10.mongo.yak.YakMapReduceOutput.Anonymous$1066.__1$1x10$mongo$yak$YakMap$2) null);}
                
                // constructor for non-virtual call
                final public x10.mongo.yak.YakMapReduceOutput.Anonymous$1066 x10$mongo$yak$YakMapReduceOutput$Anonymous$1066$$init$S(final x10.mongo.yak.YakMapReduceOutput out$,
                                                                                                                                     final x10.lang.Iterator<x10.mongo.yak.YakMap> resiter, __1$1x10$mongo$yak$YakMap$2 $dummy) { {
                                                                                                                                                                                                                                         
//#line 37 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"

                                                                                                                                                                                                                                         
//#line 11 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
this.out$ = out$;
                                                                                                                                                                                                                                         
//#line 32 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/drivers/x10/mongo/yak/YakMapReduceOutput.x10"
this.resiter = resiter;
                                                                                                                                                                                                                                     }
                                                                                                                                                                                                                                     return this;
                                                                                                                                                                                                                                     }
                
            // synthetic type for parameter mangling
            public abstract static class __1$1x10$mongo$yak$YakMap$2 {}
            
        }
        
        public static short fieldId$y;
        final public static x10.core.concurrent.AtomicInteger initStatus$y = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        
        public static void
          getDeserialized$y(
          x10.x10rt.X10JavaDeserializer deserializer){
            x10.mongo.yak.YakMapReduceOutput.y = ((x10.mongo.yak.YakUtil)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
            x10.mongo.yak.YakMapReduceOutput.initStatus$y.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static x10.mongo.yak.YakUtil
          get$y(
          ){
            if (((int) x10.mongo.yak.YakMapReduceOutput.initStatus$y.get()) ==
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                return x10.mongo.yak.YakMapReduceOutput.y;
            }
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0) &&
                  x10.mongo.yak.YakMapReduceOutput.initStatus$y.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                              (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                x10.mongo.yak.YakMapReduceOutput.y = x10.mongo.yak.YakUtil.it();
                if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                      ((boolean) true)) {
                    x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.mongo.yak.YakMapReduceOutput.y")));
                }
                x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.mongo.yak.YakMapReduceOutput.y)),
                                                                          (short)(x10.mongo.yak.YakMapReduceOutput.fieldId$y));
                x10.mongo.yak.YakMapReduceOutput.initStatus$y.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                x10.runtime.impl.java.InitDispatcher.notifyInitialized();
            } else {
                if (((int) x10.mongo.yak.YakMapReduceOutput.initStatus$y.get()) !=
                    ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    while (((int) x10.mongo.yak.YakMapReduceOutput.initStatus$y.get()) !=
                           ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                    }
                    x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                }
            }
            return x10.mongo.yak.YakMapReduceOutput.y;
        }
        
        static {
                   x10.mongo.yak.YakMapReduceOutput.fieldId$y = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.mongo.yak.YakMapReduceOutput")),
                                                                                                                                    ((java.lang.String)("y")))))));
               }
    
}
