
@x10.core.X10Generated public class A_Main extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, A_Main.class);
    
    public static final x10.rtt.RuntimeType<A_Main> $RTT = x10.rtt.NamedType.<A_Main> make(
    "A_Main", /* base class */A_Main.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(A_Main $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + A_Main.class + " calling"); } 
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        A_Main $_obj = new A_Main((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        
    }
    
    // constructor just for allocation
    public A_Main(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 4 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
public static class $Main extends x10.runtime.impl.java.Runtime {
        private static final long serialVersionUID = 1L;
        public static void main(java.lang.String[] args)  {
        // start native runtime
        new $Main().start(args);
        }
        
        // called by native runtime inside main x10 thread
        public void runtimeCallback(final x10.regionarray.Array<java.lang.String> args)  {
        // call the original app-main method
        A_Main.main(args);
        }
        }
        
        // the original app-main method
        public static void main(final x10.regionarray.Array<java.lang.String> argv)  {
            
//#line 5 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
final x10.mongo.yak.YakUtil y =
              x10.mongo.yak.YakUtil.it();
            
//#line 6 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
final x10.mongo.yak.YakCollection hello =
              y.collection(((java.lang.String)("test")),
                           ((java.lang.String)("x10tutorial.hello")));
            
//#line 7 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
hello.drop();
            
//#line 9 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
final x10.mongo.yak.LoadedYakMap t2696 =
              ((x10.mongo.yak.LoadedYakMap)(y.$funnel(((java.lang.String)("txt")))));
            
//#line 9 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
final x10.mongo.yak.YakMap t2697 =
              ((x10.mongo.yak.YakMap)(t2696.$lfunnel(((java.lang.Object)("world!")))));
            
//#line 9 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
hello.save(((com.mongodb.DBObject)(t2697)));
            
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
final x10.mongo.yak.LoadedYakMap t2698 =
              ((x10.mongo.yak.LoadedYakMap)(y.$funnel(((java.lang.String)("txt")))));
            
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
final x10.mongo.yak.YakMap t2699 =
              ((x10.mongo.yak.YakMap)(t2698.$lfunnel(((java.lang.Object)("ll")))));
            
//#line 10 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
hello.save(((com.mongodb.DBObject)(t2699)));
            
//#line 11 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
final x10.mongo.yak.LoadedYakMap t2700 =
              ((x10.mongo.yak.LoadedYakMap)(y.$funnel(((java.lang.String)("txt")))));
            
//#line 11 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
final x10.mongo.yak.YakMap t2701 =
              ((x10.mongo.yak.YakMap)(t2700.$lfunnel(((java.lang.Object)("o, ")))));
            
//#line 11 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
hello.save(((com.mongodb.DBObject)(t2701)));
            
//#line 12 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
final x10.mongo.yak.LoadedYakMap t2702 =
              ((x10.mongo.yak.LoadedYakMap)(y.$funnel(((java.lang.String)("txt")))));
            
//#line 12 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
final x10.mongo.yak.YakMap t2703 =
              ((x10.mongo.yak.YakMap)(t2702.$lfunnel(((java.lang.Object)("He")))));
            
//#line 12 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
hello.save(((com.mongodb.DBObject)(t2703)));
            
//#line 14 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
final x10.mongo.yak.YakCursor t2705 =
              hello.find();
            
//#line 14 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
final x10.mongo.yak.LoadedYakMap t2704 =
              ((x10.mongo.yak.LoadedYakMap)(y.$funnel(((java.lang.String)("txt")))));
            
//#line 14 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
final x10.mongo.yak.YakMap t2706 =
              ((x10.mongo.yak.YakMap)(t2704.$lfunnel((int)(1))));
            
//#line 14 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
final x10.mongo.yak.YakCursor cursor =
              t2705.sort(((com.mongodb.DBObject)(t2706)));
            
//#line 15 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
final x10.lang.Iterator rec2715 =
              cursor.iterator();
            
//#line 15 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
for (;
                                                                                                       true;
                                                                                                       ) {
                
//#line 15 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
final boolean t2716 =
                  ((x10.lang.Iterator<x10.mongo.yak.YakMap>)rec2715).hasNext$O();
                
//#line 15 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
if (!(t2716)) {
                    
//#line 15 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
break;
                }
                
//#line 15 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
final x10.mongo.yak.YakMap rec2712 =
                  ((x10.lang.Iterator<x10.mongo.yak.YakMap>)rec2715).next$G();
                
//#line 15 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
final x10.io.Printer t2713 =
                  ((x10.io.Printer)(x10.io.Console.OUT));
                
//#line 15 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
final java.lang.String t2714 =
                  rec2712.str$O(((java.lang.String)("txt")));
                
//#line 15 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
t2713.print(((java.lang.String)(t2714)));
            }
            
//#line 16 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
final x10.io.Printer t2711 =
              ((x10.io.Printer)(x10.io.Console.OUT));
            
//#line 16 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
t2711.println();
            
//#line 17 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
hello.drop();
        }
        
        
//#line 3 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
final public A_Main
                                                                                               A_Main$$A_Main$this(
                                                                                               ){
            
//#line 3 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
return A_Main.this;
        }
        
        
//#line 3 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"
// creation method for java code (1-phase java constructor)
        public A_Main(){this((java.lang.System[]) null);
                            A_Main$$init$S();}
        
        // constructor for non-virtual call
        final public A_Main A_Main$$init$S() { {
                                                      
//#line 3 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"

                                                      
//#line 3 "/Users/bard/x10/juss/Actors/mongo/mongo4x10/tutorial/tutcode/A_Main.x10"

                                                  }
                                                  return this;
                                                  }
        
    
}
