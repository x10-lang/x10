package x10.lang;


@x10.core.X10Generated final public class Clock extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Clock.class);
    
    public static final x10.rtt.RuntimeType<Clock> $RTT = x10.rtt.NamedType.<Clock> make(
    "x10.lang.Clock", /* base class */Clock.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Clock $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Clock.class + " calling"); } 
        x10.core.GlobalRef root = (x10.core.GlobalRef) $deserializer.readRef();
        $_obj.root = root;
        java.lang.String name = (java.lang.String) $deserializer.readRef();
        $_obj.name = name;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Clock $_obj = new Clock((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        if (root instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.root);
        } else {
        $serializer.write(this.root);
        }
        $serializer.write(this.name);
        
    }
    
    // constructor just for allocation
    public Clock(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
public java.lang.String name;
        
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
public x10.core.GlobalRef<x10.lang.Clock> root;
        
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
public boolean
                                                                                              equals(
                                                                                              final java.lang.Object a){
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
boolean t51069 =
              ((a) == (null));
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
if (!(t51069)) {
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final boolean t51068 =
                  x10.lang.Clock.$RTT.isInstance(a);
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
t51069 = !(t51068);
            }
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final boolean t51070 =
              t51069;
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
if (t51070) {
                
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
return false;
            }
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Clock t51071 =
              ((x10.lang.Clock)(x10.rtt.Types.<x10.lang.Clock> cast(a,x10.lang.Clock.$RTT)));
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.core.GlobalRef<x10.lang.Clock> t51072 =
              ((x10.core.GlobalRef)(t51071.
                                      root));
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.core.GlobalRef<x10.lang.Clock> t51073 =
              ((x10.core.GlobalRef)(this.
                                      root));
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final boolean t51074 =
              x10.rtt.Equality.equalsequals((t51072),(t51073));
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
return t51074;
        }
        
        
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
public int
                                                                                              hashCode(
                                                                                              ){
            
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.core.GlobalRef<x10.lang.Clock> t51075 =
              ((x10.core.GlobalRef)(root));
            
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51076 =
              (((x10.core.GlobalRef<x10.lang.Clock>)(t51075))).hashCode();
            
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
return t51076;
        }
        
        
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
public static x10.lang.Clock
                                                                                              make(
                                                                                              ){
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Clock t51077 =
              x10.lang.Clock.make(((java.lang.String)("")));
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
return t51077;
        }
        
        
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
public static x10.lang.Clock
                                                                                              make(
                                                                                              final java.lang.String name){
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final boolean t51079 =
              x10.lang.Runtime.STATIC_THREADS;
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
if (t51079) {
                
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.ClockUseException t51078 =
                  ((x10.lang.ClockUseException)(new x10.lang.ClockUseException(((java.lang.String)("Clocks are not compatible with static threads.")))));
                
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
throw t51078;
            }
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Clock clock =
              ((x10.lang.Clock)(new x10.lang.Clock((java.lang.System[]) null).$init(((java.lang.String)(name)))));
            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Activity t51080 =
              x10.lang.Runtime.activity();
            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Activity.ClockPhases t51081 =
              t51080.clockPhases();
            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51082 =
              x10.lang.Clock.FIRST_PHASE;
            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
((x10.util.HashMap<x10.lang.Clock, x10.core.Int>)t51081).put__0x10$util$HashMap$$K__1x10$util$HashMap$$V(((x10.lang.Clock)(clock)),
                                                                                                                                                                                                         x10.core.Int.$box(t51082));
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
return clock;
        }
        
        
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final public static int FIRST_PHASE = 1;
        
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
public transient int count;
        
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
public transient int alive;
        
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
public transient int phase;
        
        
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
// creation method for java code (1-phase java constructor)
        public Clock(final java.lang.String name){this((java.lang.System[]) null);
                                                      $init(name);}
        
        // constructor for non-virtual call
        final public x10.lang.Clock x10$lang$Clock$$init$S(final java.lang.String name) { {
                                                                                                 
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"

                                                                                                 
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
this.name = name;
                                                                                                 
                                                                                                 
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
this.__fieldInitializers50801();
                                                                                             }
                                                                                             return this;
                                                                                             }
        
        // constructor
        public x10.lang.Clock $init(final java.lang.String name){return x10$lang$Clock$$init$S(name);}
        
        
        
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
private void
                                                                                              resumeLocal(
                                                                                              ){
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
try {{
                
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
x10.lang.Runtime.enterAtomic();
                {
                    
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Clock x51054 =
                      ((x10.lang.Clock)(this));
                    
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
;
                    
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51083 =
                      x51054.
                        alive;
                    
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51084 =
                      ((t51083) - (((int)(1))));
                    
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51085 =
                      x51054.alive = t51084;
                    
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final boolean t51089 =
                      ((int) t51085) ==
                    ((int) 0);
                    
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
if (t51089) {
                        
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51086 =
                          count;
                        
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
this.alive = t51086;
                        
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Clock x51056 =
                          ((x10.lang.Clock)(this));
                        
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
;
                        
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51087 =
                          x51056.
                            phase;
                        
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51088 =
                          ((t51087) + (((int)(1))));
                        
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
x51056.phase = t51088;
                    }
                }
            }}finally {{
                  
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
x10.lang.Runtime.exitAtomic();
              }}
            }
        
        public static void
          resumeLocal$P(
          final x10.lang.Clock Clock){
            Clock.resumeLocal();
        }
        
        
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
private void
                                                                                              dropLocal(
                                                                                              final int ph){
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
try {{
                
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
x10.lang.Runtime.enterAtomic();
                {
                    
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Clock x51058 =
                      ((x10.lang.Clock)(this));
                    
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
;
                    
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51090 =
                      x51058.
                        count;
                    
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51091 =
                      ((t51090) - (((int)(1))));
                    
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
x51058.count = t51091;
                    
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51092 =
                      (-(ph));
                    
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51093 =
                      phase;
                    
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final boolean t51101 =
                      ((int) t51092) !=
                    ((int) t51093);
                    
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
if (t51101) {
                        
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Clock x51060 =
                          ((x10.lang.Clock)(this));
                        
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
;
                        
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51094 =
                          x51060.
                            alive;
                        
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51095 =
                          ((t51094) - (((int)(1))));
                        
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51096 =
                          x51060.alive = t51095;
                        
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final boolean t51100 =
                          ((int) t51096) ==
                        ((int) 0);
                        
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
if (t51100) {
                            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51097 =
                              count;
                            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
this.alive = t51097;
                            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Clock x51062 =
                              ((x10.lang.Clock)(this));
                            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
;
                            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51098 =
                              x51062.
                                phase;
                            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51099 =
                              ((t51098) + (((int)(1))));
                            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
x51062.phase = t51099;
                        }
                    }
                }
            }}finally {{
                  
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
x10.lang.Runtime.exitAtomic();
              }}
            }
        
        public static void
          dropLocal$P(
          final int ph,
          final x10.lang.Clock Clock){
            Clock.dropLocal((int)(ph));
        }
        
        
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
private int
                                                                                              get$O(
                                                                                              ){
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Activity t51102 =
              x10.lang.Runtime.activity();
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Activity.ClockPhases t51103 =
              t51102.clockPhases();
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.util.Box<x10.core.Int> t51104 =
              ((x10.util.HashMap<x10.lang.Clock, x10.core.Int>)t51103).get__0x10$util$HashMap$$K(((x10.lang.Clock)(this)));
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51105 =
              x10.core.Int.$unbox(((x10.util.Box<x10.core.Int>)t51104).
                                    value);
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
return t51105;
        }
        
        public static int
          get$P$O(
          final x10.lang.Clock Clock){
            return Clock.get$O();
        }
        
        
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
private x10.util.Box<x10.core.Int>
                                                                                              put(
                                                                                              final int ph){
            
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Activity t51106 =
              x10.lang.Runtime.activity();
            
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Activity.ClockPhases t51107 =
              t51106.clockPhases();
            
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.util.Box<x10.core.Int> t51108 =
              ((x10.util.HashMap<x10.lang.Clock, x10.core.Int>)t51107).put__0x10$util$HashMap$$K__1x10$util$HashMap$$V(((x10.lang.Clock)(this)),
                                                                                                                       x10.core.Int.$box(ph));
            
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
return t51108;
        }
        
        public static x10.util.Box<x10.core.Int>
          put$P(
          final int ph,
          final x10.lang.Clock Clock){
            return Clock.put((int)(ph));
        }
        
        
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
private int
                                                                                              remove$O(
                                                                                              ){
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Activity t51109 =
              x10.lang.Runtime.activity();
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Activity.ClockPhases t51110 =
              t51109.clockPhases();
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.util.Box<x10.core.Int> t51111 =
              ((x10.util.HashMap<x10.lang.Clock, x10.core.Int>)t51110).remove__0x10$util$HashMap$$K(((x10.lang.Clock)(this)));
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51112 =
              x10.core.Int.$unbox(((x10.util.Box<x10.core.Int>)t51111).
                                    value);
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
return t51112;
        }
        
        public static int
          remove$P$O(
          final x10.lang.Clock Clock){
            return Clock.remove$O();
        }
        
        
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
public int
                                                                                              register$O(
                                                                                              ){
            
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final boolean t51113 =
              this.dropped$O();
            
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
if (t51113) {
                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
this.clockUseException(((java.lang.String)("async clocked")));
            }
            
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int ph =
              this.get$O();
            
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.core.GlobalRef<x10.lang.Clock> t51114 =
              ((x10.core.GlobalRef)(root));
            
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Place t51123 =
              ((x10.lang.Place)((t51114).home));
            
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
x10.lang.Runtime.runAt(((x10.lang.Place)(t51123)),
                                                                                                                       ((x10.core.fun.VoidFun_0_0)(new x10.lang.Clock.$Closure$103(this,
                                                                                                                                                                                   root,
                                                                                                                                                                                   ph, (x10.lang.Clock.$Closure$103.__1$1x10$lang$Clock$2) null))));
            
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
return ph;
        }
        
        
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
public void
                                                                                              resumeUnsafe(
                                                                                              ){
            
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
x10.lang.Runtime.ensureNotInAtomic();
            
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int ph =
              this.get$O();
            
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final boolean t51124 =
              ((ph) < (((int)(0))));
            
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
if (t51124) {
                
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
return;
            }
            
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.core.GlobalRef<x10.lang.Clock> t51125 =
              ((x10.core.GlobalRef)(root));
            
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Place t51127 =
              ((x10.lang.Place)((t51125).home));
            
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
x10.lang.Runtime.runAt(((x10.lang.Place)(t51127)),
                                                                                                                       ((x10.core.fun.VoidFun_0_0)(new x10.lang.Clock.$Closure$104(this,
                                                                                                                                                                                   root, (x10.lang.Clock.$Closure$104.__1$1x10$lang$Clock$2) null))));
            
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51128 =
              (-(ph));
            
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
this.put((int)(t51128));
        }
        
        
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
public void
                                                                                               advanceUnsafe(
                                                                                               ){
            
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
x10.lang.Runtime.ensureNotInAtomic();
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int ph =
              this.get$O();
            
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int abs =
              x10.lang.Math.abs$O((int)(ph));
            
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.core.GlobalRef<x10.lang.Clock> t51129 =
              ((x10.core.GlobalRef)(root));
            
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Place t51132 =
              ((x10.lang.Place)((t51129).home));
            
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
x10.lang.Runtime.runAt(((x10.lang.Place)(t51132)),
                                                                                                                        ((x10.core.fun.VoidFun_0_0)(new x10.lang.Clock.$Closure$105(this,
                                                                                                                                                                                    root,
                                                                                                                                                                                    ph,
                                                                                                                                                                                    abs, (x10.lang.Clock.$Closure$105.__1$1x10$lang$Clock$2) null))));
            
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51133 =
              ((abs) + (((int)(1))));
            
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
this.put((int)(t51133));
        }
        
        
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
public void
                                                                                               dropUnsafe(
                                                                                               ){
            
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int ph =
              this.remove$O();
            
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.core.GlobalRef<x10.lang.Clock> t51134 =
              ((x10.core.GlobalRef)(root));
            
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Place t51136 =
              ((x10.lang.Place)((t51134).home));
            
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
x10.lang.Runtime.runAt(((x10.lang.Place)(t51136)),
                                                                                                                        ((x10.core.fun.VoidFun_0_0)(new x10.lang.Clock.$Closure$106(this,
                                                                                                                                                                                    root,
                                                                                                                                                                                    ph, (x10.lang.Clock.$Closure$106.__1$1x10$lang$Clock$2) null))));
        }
        
        
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
public void
                                                                                               dropInternal(
                                                                                               ){
            
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int ph =
              this.get$O();
            
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.core.GlobalRef<x10.lang.Clock> t51137 =
              ((x10.core.GlobalRef)(root));
            
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Place t51139 =
              ((x10.lang.Place)((t51137).home));
            
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
x10.lang.Runtime.runAt(((x10.lang.Place)(t51139)),
                                                                                                                        ((x10.core.fun.VoidFun_0_0)(new x10.lang.Clock.$Closure$107(this,
                                                                                                                                                                                    root,
                                                                                                                                                                                    ph, (x10.lang.Clock.$Closure$107.__1$1x10$lang$Clock$2) null))));
        }
        
        
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
public boolean
                                                                                               registered$O(
                                                                                               ){
            
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Activity t51140 =
              x10.lang.Runtime.activity();
            
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Activity.ClockPhases t51141 =
              t51140.clockPhases();
            
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final boolean t51142 =
              ((x10.util.HashMap<x10.lang.Clock, x10.core.Int>)t51141).containsKey__0x10$util$HashMap$$K$O(((x10.lang.Clock)(this)));
            
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
return t51142;
        }
        
        
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
public boolean
                                                                                               dropped$O(
                                                                                               ){
            
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final boolean t51143 =
              this.registered$O();
            
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final boolean t51144 =
              !(t51143);
            
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
return t51144;
        }
        
        
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
public int
                                                                                               phase$O(
                                                                                               ){
            
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final boolean t51145 =
              this.dropped$O();
            
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
if (t51145) {
                
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
this.clockUseException(((java.lang.String)("phase")));
            }
            
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51146 =
              this.get$O();
            
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51147 =
              x10.lang.Math.abs$O((int)(t51146));
            
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
return t51147;
        }
        
        
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
public void
                                                                                               resume(
                                                                                               ){
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final boolean t51148 =
              this.dropped$O();
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
if (t51148) {
                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
this.clockUseException(((java.lang.String)("resume")));
            }
            
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
this.resumeUnsafe();
        }
        
        
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
public void
                                                                                               advance(
                                                                                               ){
            
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final boolean t51149 =
              this.dropped$O();
            
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
if (t51149) {
                
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
this.clockUseException(((java.lang.String)("advance")));
            }
            
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
this.advanceUnsafe();
        }
        
        
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
public void
                                                                                               drop(
                                                                                               ){
            
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final boolean t51150 =
              this.dropped$O();
            
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
if (t51150) {
                
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
this.clockUseException(((java.lang.String)("drop")));
            }
            
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
this.dropUnsafe();
        }
        
        
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
public java.lang.String
                                                                                               toString(
                                                                                               ){
            
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final java.lang.String t51151 =
              ((java.lang.String)(name));
            
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final boolean t51152 =
              (t51151).equals("");
            
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
java.lang.String t51153 =
               null;
            
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
if (t51152) {
                
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
t51153 = super.toString();
            } else {
                
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
t51153 = name;
            }
            
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final java.lang.String t51154 =
              t51153;
            
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
return t51154;
        }
        
        
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
private void
                                                                                               clockUseException(
                                                                                               final java.lang.String method){
            
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final boolean t51161 =
              this.dropped$O();
            
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
if (t51161) {
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final java.lang.String t51155 =
                  (("invalid invocation of ") + (method));
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final java.lang.String t51156 =
                  ((t51155) + ("() on clock "));
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final java.lang.String t51157 =
                  this.toString();
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final java.lang.String t51158 =
                  ((t51156) + (t51157));
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final java.lang.String t51159 =
                  ((t51158) + ("; calling activity is not clocked on this clock"));
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.ClockUseException t51160 =
                  ((x10.lang.ClockUseException)(new x10.lang.ClockUseException(t51159)));
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
throw t51160;
            }
        }
        
        public static void
          clockUseException$P(
          final java.lang.String method,
          final x10.lang.Clock Clock){
            Clock.clockUseException(((java.lang.String)(method)));
        }
        
        
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
public static void
                                                                                               advanceAll(
                                                                                               ){
            
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
x10.lang.Runtime.ensureNotInAtomic();
            
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Activity t51162 =
              x10.lang.Runtime.activity();
            
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Activity.ClockPhases t51163 =
              t51162.clockPhases();
            
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
t51163.advanceAll();
        }
        
        
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
public static void
                                                                                               resumeAll(
                                                                                               ){
            
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Activity t51164 =
              x10.lang.Runtime.activity();
            
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Activity.ClockPhases t51165 =
              t51164.clockPhases();
            
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
t51165.resumeAll();
        }
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final public x10.lang.Clock
                                                                                              x10$lang$Clock$$x10$lang$Clock$this(
                                                                                              ){
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
return x10.lang.Clock.this;
        }
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final public void
                                                                                              __fieldInitializers50801(
                                                                                              ){
            
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.core.GlobalRef<x10.lang.Clock> t51166 =
              ((x10.core.GlobalRef)(new x10.core.GlobalRef<x10.lang.Clock>(x10.lang.Clock.$RTT, ((x10.lang.Clock)(this)), (x10.core.GlobalRef.__0x10$lang$GlobalRef$$T) null)));
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
this.root = ((x10.core.GlobalRef)(t51166));
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
this.count = 1;
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
this.alive = 1;
            
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51167 =
              x10.lang.Clock.FIRST_PHASE;
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
this.phase = t51167;
        }
        
        public static int
          getInitialized$FIRST_PHASE(
          ){
            return x10.lang.Clock.FIRST_PHASE;
        }
        
        @x10.core.X10Generated public static class $Closure$103 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$103.class);
            
            public static final x10.rtt.RuntimeType<$Closure$103> $RTT = x10.rtt.StaticVoidFunType.<$Closure$103> make(
            /* base class */$Closure$103.class
            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$103 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$103.class + " calling"); } 
                x10.lang.Clock out$$ = (x10.lang.Clock) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.core.GlobalRef root = (x10.core.GlobalRef) $deserializer.readRef();
                $_obj.root = root;
                $_obj.ph = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$103 $_obj = new $Closure$103((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (out$$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$$);
                } else {
                $serializer.write(this.out$$);
                }
                if (root instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.root);
                } else {
                $serializer.write(this.root);
                }
                $serializer.write(this.ph);
                
            }
            
            // constructor just for allocation
            public $Closure$103(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            
                
                public void
                  $apply(
                  ){
                    
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.core.GlobalRef<x10.lang.Clock> t51115 =
                      ((x10.core.GlobalRef)(this.
                                              root));
                    
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Clock me =
                      (((x10.core.GlobalRef<x10.lang.Clock>)(t51115))).$apply$G();
                    
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
try {{
                        
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
x10.lang.Runtime.enterAtomic();
                        {
                            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Clock x51064 =
                              ((x10.lang.Clock)(me));
                            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
;
                            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51116 =
                              x51064.
                                count;
                            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51117 =
                              ((t51116) + (((int)(1))));
                            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
x51064.count = t51117;
                            
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51118 =
                              (-(this.
                                   ph));
                            
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51119 =
                              me.
                                phase;
                            
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final boolean t51122 =
                              ((int) t51118) !=
                            ((int) t51119);
                            
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
if (t51122) {
                                
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Clock x51066 =
                                  ((x10.lang.Clock)(me));
                                
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
;
                                
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51120 =
                                  x51066.
                                    alive;
                                
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final int t51121 =
                                  ((t51120) + (((int)(1))));
                                
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
x51066.alive = t51121;
                            }
                        }
                    }}finally {{
                          
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
x10.lang.Runtime.exitAtomic();
                      }}
                    }
                
                public x10.lang.Clock out$$;
                public x10.core.GlobalRef<x10.lang.Clock> root;
                public int ph;
                
                public $Closure$103(final x10.lang.Clock out$$,
                                    final x10.core.GlobalRef<x10.lang.Clock> root,
                                    final int ph, __1$1x10$lang$Clock$2 $dummy) { {
                                                                                         this.out$$ = out$$;
                                                                                         this.root = ((x10.core.GlobalRef)(root));
                                                                                         this.ph = ph;
                                                                                     }}
                // synthetic type for parameter mangling
                public abstract static class __1$1x10$lang$Clock$2 {}
                
                }
                
            @x10.core.X10Generated public static class $Closure$104 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
            {
                private static final long serialVersionUID = 1L;
                private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$104.class);
                
                public static final x10.rtt.RuntimeType<$Closure$104> $RTT = x10.rtt.StaticVoidFunType.<$Closure$104> make(
                /* base class */$Closure$104.class
                , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                );
                public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                
                
                private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$104 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$104.class + " calling"); } 
                    x10.lang.Clock out$$ = (x10.lang.Clock) $deserializer.readRef();
                    $_obj.out$$ = out$$;
                    x10.core.GlobalRef root = (x10.core.GlobalRef) $deserializer.readRef();
                    $_obj.root = root;
                    return $_obj;
                    
                }
                
                public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    $Closure$104 $_obj = new $Closure$104((java.lang.System[]) null);
                    $deserializer.record_reference($_obj);
                    return $_deserialize_body($_obj, $deserializer);
                    
                }
                
                public short $_get_serialization_id() {
                
                     return $_serialization_id;
                    
                }
                
                public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                
                    if (out$$ instanceof x10.x10rt.X10JavaSerializable) {
                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$$);
                    } else {
                    $serializer.write(this.out$$);
                    }
                    if (root instanceof x10.x10rt.X10JavaSerializable) {
                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.root);
                    } else {
                    $serializer.write(this.root);
                    }
                    
                }
                
                // constructor just for allocation
                public $Closure$104(final java.lang.System[] $dummy) { 
                super($dummy);
                }
                
                    
                    public void
                      $apply(
                      ){
                        
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.core.GlobalRef<x10.lang.Clock> t51126 =
                          ((x10.core.GlobalRef)(this.
                                                  root));
                        
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Clock me =
                          (((x10.core.GlobalRef<x10.lang.Clock>)(t51126))).$apply$G();
                        
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
me.resumeLocal();
                    }
                    
                    public x10.lang.Clock out$$;
                    public x10.core.GlobalRef<x10.lang.Clock> root;
                    
                    public $Closure$104(final x10.lang.Clock out$$,
                                        final x10.core.GlobalRef<x10.lang.Clock> root, __1$1x10$lang$Clock$2 $dummy) { {
                                                                                                                              this.out$$ = out$$;
                                                                                                                              this.root = ((x10.core.GlobalRef)(root));
                                                                                                                          }}
                    // synthetic type for parameter mangling
                    public abstract static class __1$1x10$lang$Clock$2 {}
                    
                }
                
            @x10.core.X10Generated public static class $Closure$105 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
            {
                private static final long serialVersionUID = 1L;
                private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$105.class);
                
                public static final x10.rtt.RuntimeType<$Closure$105> $RTT = x10.rtt.StaticVoidFunType.<$Closure$105> make(
                /* base class */$Closure$105.class
                , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                );
                public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                
                
                private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$105 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$105.class + " calling"); } 
                    x10.lang.Clock out$$ = (x10.lang.Clock) $deserializer.readRef();
                    $_obj.out$$ = out$$;
                    x10.core.GlobalRef root = (x10.core.GlobalRef) $deserializer.readRef();
                    $_obj.root = root;
                    $_obj.ph = $deserializer.readInt();
                    $_obj.abs = $deserializer.readInt();
                    return $_obj;
                    
                }
                
                public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    $Closure$105 $_obj = new $Closure$105((java.lang.System[]) null);
                    $deserializer.record_reference($_obj);
                    return $_deserialize_body($_obj, $deserializer);
                    
                }
                
                public short $_get_serialization_id() {
                
                     return $_serialization_id;
                    
                }
                
                public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                
                    if (out$$ instanceof x10.x10rt.X10JavaSerializable) {
                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$$);
                    } else {
                    $serializer.write(this.out$$);
                    }
                    if (root instanceof x10.x10rt.X10JavaSerializable) {
                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.root);
                    } else {
                    $serializer.write(this.root);
                    }
                    $serializer.write(this.ph);
                    $serializer.write(this.abs);
                    
                }
                
                // constructor just for allocation
                public $Closure$105(final java.lang.System[] $dummy) { 
                super($dummy);
                }
                
                    
                    public void
                      $apply(
                      ){
                        
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.core.GlobalRef<x10.lang.Clock> t51130 =
                          ((x10.core.GlobalRef)(this.
                                                  root));
                        
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Clock me =
                          (((x10.core.GlobalRef<x10.lang.Clock>)(t51130))).$apply$G();
                        
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final boolean t51131 =
                          ((this.
                              ph) > (((int)(0))));
                        
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
if (t51131) {
                            
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
me.resumeLocal();
                        }
                        {
                            
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
x10.lang.Runtime.ensureNotInAtomic();
                            
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
try {{
                                
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
x10.lang.Runtime.enterAtomic();
                                
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
while (true) {
                                    
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
if (((this.
                                                                                                                                 abs) < (((int)(me.
                                                                                                                                                  phase))))) {
                                        {
                                            
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
;
                                        }
                                        
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
break;
                                    }
                                    
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
x10.lang.Runtime.awaitAtomic();
                                }
                            }}finally {{
                                  
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
x10.lang.Runtime.exitAtomic();
                              }}
                            }
                        }
                    
                    public x10.lang.Clock out$$;
                    public x10.core.GlobalRef<x10.lang.Clock> root;
                    public int ph;
                    public int abs;
                    
                    public $Closure$105(final x10.lang.Clock out$$,
                                        final x10.core.GlobalRef<x10.lang.Clock> root,
                                        final int ph,
                                        final int abs, __1$1x10$lang$Clock$2 $dummy) { {
                                                                                              this.out$$ = out$$;
                                                                                              this.root = ((x10.core.GlobalRef)(root));
                                                                                              this.ph = ph;
                                                                                              this.abs = abs;
                                                                                          }}
                    // synthetic type for parameter mangling
                    public abstract static class __1$1x10$lang$Clock$2 {}
                    
                    }
                    
                @x10.core.X10Generated public static class $Closure$106 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$106.class);
                    
                    public static final x10.rtt.RuntimeType<$Closure$106> $RTT = x10.rtt.StaticVoidFunType.<$Closure$106> make(
                    /* base class */$Closure$106.class
                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    
                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$106 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$106.class + " calling"); } 
                        x10.lang.Clock out$$ = (x10.lang.Clock) $deserializer.readRef();
                        $_obj.out$$ = out$$;
                        x10.core.GlobalRef root = (x10.core.GlobalRef) $deserializer.readRef();
                        $_obj.root = root;
                        $_obj.ph = $deserializer.readInt();
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        $Closure$106 $_obj = new $Closure$106((java.lang.System[]) null);
                        $deserializer.record_reference($_obj);
                        return $_deserialize_body($_obj, $deserializer);
                        
                    }
                    
                    public short $_get_serialization_id() {
                    
                         return $_serialization_id;
                        
                    }
                    
                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                    
                        if (out$$ instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$$);
                        } else {
                        $serializer.write(this.out$$);
                        }
                        if (root instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.root);
                        } else {
                        $serializer.write(this.root);
                        }
                        $serializer.write(this.ph);
                        
                    }
                    
                    // constructor just for allocation
                    public $Closure$106(final java.lang.System[] $dummy) { 
                    super($dummy);
                    }
                    
                        
                        public void
                          $apply(
                          ){
                            
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.core.GlobalRef<x10.lang.Clock> t51135 =
                              ((x10.core.GlobalRef)(this.
                                                      root));
                            
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Clock me =
                              (((x10.core.GlobalRef<x10.lang.Clock>)(t51135))).$apply$G();
                            
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
me.dropLocal((int)(this.
                                                                                                                                      ph));
                        }
                        
                        public x10.lang.Clock out$$;
                        public x10.core.GlobalRef<x10.lang.Clock> root;
                        public int ph;
                        
                        public $Closure$106(final x10.lang.Clock out$$,
                                            final x10.core.GlobalRef<x10.lang.Clock> root,
                                            final int ph, __1$1x10$lang$Clock$2 $dummy) { {
                                                                                                 this.out$$ = out$$;
                                                                                                 this.root = ((x10.core.GlobalRef)(root));
                                                                                                 this.ph = ph;
                                                                                             }}
                        // synthetic type for parameter mangling
                        public abstract static class __1$1x10$lang$Clock$2 {}
                        
                    }
                    
                @x10.core.X10Generated public static class $Closure$107 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$107.class);
                    
                    public static final x10.rtt.RuntimeType<$Closure$107> $RTT = x10.rtt.StaticVoidFunType.<$Closure$107> make(
                    /* base class */$Closure$107.class
                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    
                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$107 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$107.class + " calling"); } 
                        x10.lang.Clock out$$ = (x10.lang.Clock) $deserializer.readRef();
                        $_obj.out$$ = out$$;
                        x10.core.GlobalRef root = (x10.core.GlobalRef) $deserializer.readRef();
                        $_obj.root = root;
                        $_obj.ph = $deserializer.readInt();
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        $Closure$107 $_obj = new $Closure$107((java.lang.System[]) null);
                        $deserializer.record_reference($_obj);
                        return $_deserialize_body($_obj, $deserializer);
                        
                    }
                    
                    public short $_get_serialization_id() {
                    
                         return $_serialization_id;
                        
                    }
                    
                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                    
                        if (out$$ instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$$);
                        } else {
                        $serializer.write(this.out$$);
                        }
                        if (root instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.root);
                        } else {
                        $serializer.write(this.root);
                        }
                        $serializer.write(this.ph);
                        
                    }
                    
                    // constructor just for allocation
                    public $Closure$107(final java.lang.System[] $dummy) { 
                    super($dummy);
                    }
                    
                        
                        public void
                          $apply(
                          ){
                            
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.core.GlobalRef<x10.lang.Clock> t51138 =
                              ((x10.core.GlobalRef)(this.
                                                      root));
                            
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
final x10.lang.Clock me =
                              (((x10.core.GlobalRef<x10.lang.Clock>)(t51138))).$apply$G();
                            
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Clock.x10"
me.dropLocal((int)(this.
                                                                                                                                      ph));
                        }
                        
                        public x10.lang.Clock out$$;
                        public x10.core.GlobalRef<x10.lang.Clock> root;
                        public int ph;
                        
                        public $Closure$107(final x10.lang.Clock out$$,
                                            final x10.core.GlobalRef<x10.lang.Clock> root,
                                            final int ph, __1$1x10$lang$Clock$2 $dummy) { {
                                                                                                 this.out$$ = out$$;
                                                                                                 this.root = ((x10.core.GlobalRef)(root));
                                                                                                 this.ph = ph;
                                                                                             }}
                        // synthetic type for parameter mangling
                        public abstract static class __1$1x10$lang$Clock$2 {}
                        
                    }
                    
                
                public java.lang.String
                  x10$lang$Object$toString$S$O(
                  ){
                    return super.toString();
                }
                
                }
                