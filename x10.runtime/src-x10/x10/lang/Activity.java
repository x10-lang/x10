package x10.lang;


@x10.core.X10Generated public class Activity extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Activity.class);
    
    public static final x10.rtt.RuntimeType<Activity> $RTT = x10.rtt.NamedType.<Activity> make(
    "x10.lang.Activity", /* base class */Activity.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Activity $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Activity.class + " calling"); } 
        x10.lang.FinishState finishState = (x10.lang.FinishState) $deserializer.readRef();
        $_obj.finishState = finishState;
        x10.core.fun.VoidFun_0_0 body = (x10.core.fun.VoidFun_0_0) $deserializer.readRef();
        $_obj.body = body;
        x10.lang.Activity.ClockPhases clockPhases = (x10.lang.Activity.ClockPhases) $deserializer.readRef();
        $_obj.clockPhases = clockPhases;
        $_obj.atomicDepth = $deserializer.readInt();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Activity $_obj = new Activity((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        if (finishState instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.finishState);
        } else {
        $serializer.write(this.finishState);
        }
        if (body instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.body);
        } else {
        $serializer.write(this.body);
        }
        if (clockPhases instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.clockPhases);
        } else {
        $serializer.write(this.clockPhases);
        }
        $serializer.write(this.atomicDepth);
        
    }
    
    // constructor just for allocation
    public Activity(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
@x10.core.X10Generated public static class ClockPhases extends x10.util.HashMap<x10.lang.Clock, x10.core.Int> implements x10.x10rt.X10JavaSerializable
                                                                                               {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, ClockPhases.class);
            
            public static final x10.rtt.RuntimeType<ClockPhases> $RTT = x10.rtt.NamedType.<ClockPhases> make(
            "x10.lang.Activity.ClockPhases", /* base class */ClockPhases.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.util.HashMap.$RTT, x10.lang.Clock.$RTT, x10.rtt.Types.INT)}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            // custom serializer
            private transient x10.io.SerialData $$serialdata;
            private Object writeReplace() { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " calling"); } $$serialdata = serialize(); if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " returned " + $$serialdata); } return this; }
            private Object readResolve() { return new ClockPhases($$serialdata); }
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
            oos.writeObject($$serialdata); }
            private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {
            $$serialdata = (x10.io.SerialData) ois.readObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(ClockPhases $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("X10JavaSerializable: $_deserialize_body() of " + ClockPhases.class + " calling"); } 
                x10.io.SerialData $$serialdata = (x10.io.SerialData) $deserializer.readRef();
                $_obj.$init($$serialdata);
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                ClockPhases $_obj = new ClockPhases((java.lang.System[]) null);
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
            public ClockPhases(final java.lang.System[] $dummy) { 
            super($dummy, x10.lang.Clock.$RTT, x10.rtt.Types.INT);
            }
            // bridge for method public x10.util.HashMap.getOrElse(k:K,orelse:V):V
            public int
              getOrElse$O(x10.lang.Clock a1,
            int a2){return x10.core.Int.$unbox(super.getOrElse__0x10$util$HashMap$$K__1x10$util$HashMap$$V$G((a1),
            x10.core.Int.$box(a2)));}
            // bridge for method public x10.util.HashMap.getOrThrow(k:K):V
            public int
              getOrThrow$O(x10.lang.Clock a1){return x10.core.Int.$unbox(super.getOrThrow__0x10$util$HashMap$$K$G((a1)));}
            // bridge for method public x10.util.HashMap.put(k:K,v:V):x10.util.Box[V]
            public x10.util.Box<x10.core.Int>
              put(x10.lang.Clock a1,
            int a2){return super.put__0x10$util$HashMap$$K__1x10$util$HashMap$$V((a1),
            x10.core.Int.$box(a2));}
            // bridge for method final protected x10.util.HashMap.putInternal(k:K,v:V):x10.util.Box[V]
            final protected x10.util.Box<x10.core.Int>
              putInternal(x10.lang.Clock a1,
            int a2){return super.putInternal__0x10$util$HashMap$$K__1x10$util$HashMap$$V((a1),
            x10.core.Int.$box(a2));}
            
                
                
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
public static x10.lang.Activity.ClockPhases
                                                                                                         make__0$1x10$lang$Clock$2(
                                                                                                         final x10.array.Array<x10.lang.Clock> clocks){
                    
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.lang.Activity.ClockPhases clockPhases =
                      ((x10.lang.Activity.ClockPhases)(new x10.lang.Activity.ClockPhases((java.lang.System[]) null).$init()));
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
int i50664 =
                      0;
                    
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
for (;
                                                                                                                true;
                                                                                                                ) {
                        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final int t50665 =
                          i50664;
                        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final int t50666 =
                          ((x10.array.Array<x10.lang.Clock>)clocks).
                            size;
                        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final boolean t50667 =
                          ((t50665) < (((int)(t50666))));
                        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
if (!(t50667)) {
                            
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
break;
                        }
                        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final int t50657 =
                          i50664;
                        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.lang.Clock t50658 =
                          ((x10.array.Array<x10.lang.Clock>)clocks).$apply$G((int)(t50657));
                        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final int t50659 =
                          i50664;
                        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.lang.Clock t50660 =
                          ((x10.array.Array<x10.lang.Clock>)clocks).$apply$G((int)(t50659));
                        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final int t50661 =
                          t50660.register$O();
                        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
((x10.util.HashMap<x10.lang.Clock, x10.core.Int>)clockPhases).put__0x10$util$HashMap$$K__1x10$util$HashMap$$V(((x10.lang.Clock)(t50658)),
                                                                                                                                                                                                                             x10.core.Int.$box(t50661));
                        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final int t50662 =
                          i50664;
                        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final int t50663 =
                          ((t50662) + (((int)(1))));
                        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
i50664 = t50663;
                    }
                    
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
return clockPhases;
                }
                
                
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
public void
                                                                                                         advanceAll(
                                                                                                         ){
                    
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.util.Set<x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>> t50672 =
                      this.entries();
                    
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.lang.Iterator<x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>> entry50673 =
                      ((x10.lang.Iterator<x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>>)
                        ((x10.lang.Iterable<x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>>)t50672).iterator());
                    
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
for (;
                                                                                                                true;
                                                                                                                ) {
                        
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final boolean t50674 =
                          ((x10.lang.Iterator<x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>>)entry50673).hasNext$O();
                        
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
if (!(t50674)) {
                            
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
break;
                        }
                        
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.util.Map.Entry<x10.lang.Clock, x10.core.Int> entry50668 =
                          ((x10.lang.Iterator<x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>>)entry50673).next$G();
                        
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.lang.Clock t50669 =
                          ((x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>)entry50668).getKey$G();
                        
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
t50669.resumeUnsafe();
                    }
                    
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.util.Set<x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>> t50675 =
                      this.entries();
                    
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.lang.Iterator<x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>> entry50676 =
                      ((x10.lang.Iterator<x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>>)
                        ((x10.lang.Iterable<x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>>)t50675).iterator());
                    
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
for (;
                                                                                                                true;
                                                                                                                ) {
                        
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final boolean t50677 =
                          ((x10.lang.Iterator<x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>>)entry50676).hasNext$O();
                        
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
if (!(t50677)) {
                            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
break;
                        }
                        
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.util.Map.Entry<x10.lang.Clock, x10.core.Int> entry50670 =
                          ((x10.lang.Iterator<x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>>)entry50676).next$G();
                        
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.lang.Clock t50671 =
                          ((x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>)entry50670).getKey$G();
                        
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
t50671.advanceUnsafe();
                    }
                }
                
                
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
public void
                                                                                                         resumeAll(
                                                                                                         ){
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.util.Set<x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>> t50630 =
                      this.entries();
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.lang.Iterator<x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>> entry50603 =
                      ((x10.lang.Iterator<x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>>)
                        ((x10.lang.Iterable<x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>>)t50630).iterator());
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
for (;
                                                                                                                true;
                                                                                                                ) {
                        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final boolean t50632 =
                          ((x10.lang.Iterator<x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>>)entry50603).hasNext$O();
                        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
if (!(t50632)) {
                            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
break;
                        }
                        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.util.Map.Entry<x10.lang.Clock, x10.core.Int> entry50678 =
                          ((x10.lang.Iterator<x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>>)entry50603).next$G();
                        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.lang.Clock t50679 =
                          ((x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>)entry50678).getKey$G();
                        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
t50679.resumeUnsafe();
                    }
                }
                
                
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
public void
                                                                                                         drop(
                                                                                                         ){
                    
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.util.Set<x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>> t50682 =
                      this.entries();
                    
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.lang.Iterator<x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>> entry50683 =
                      ((x10.lang.Iterator<x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>>)
                        ((x10.lang.Iterable<x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>>)t50682).iterator());
                    
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
for (;
                                                                                                                true;
                                                                                                                ) {
                        
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final boolean t50684 =
                          ((x10.lang.Iterator<x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>>)entry50683).hasNext$O();
                        
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
if (!(t50684)) {
                            
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
break;
                        }
                        
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.util.Map.Entry<x10.lang.Clock, x10.core.Int> entry50680 =
                          ((x10.lang.Iterator<x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>>)entry50683).next$G();
                        
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.lang.Clock t50681 =
                          ((x10.util.Map.Entry<x10.lang.Clock, x10.core.Int>)entry50680).getKey$G();
                        
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
t50681.dropInternal();
                    }
                    
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
this.clear();
                }
                
                
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
public x10.io.SerialData
                                                                                                         serialize(
                                                                                                         ){
                    
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.io.SerialData t50637 =
                      super.serialize();
                    
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
return t50637;
                }
                
                
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
// creation method for java code (1-phase java constructor)
                public ClockPhases(){this((java.lang.System[]) null);
                                         $init();}
                
                // constructor for non-virtual call
                final public x10.lang.Activity.ClockPhases x10$lang$Activity$ClockPhases$$init$S() { {
                                                                                                            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
super.$init();
                                                                                                            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"

                                                                                                        }
                                                                                                        return this;
                                                                                                        }
                
                // constructor
                public x10.lang.Activity.ClockPhases $init(){return x10$lang$Activity$ClockPhases$$init$S();}
                
                
                
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
// creation method for java code (1-phase java constructor)
                public ClockPhases(final x10.io.SerialData a){this((java.lang.System[]) null);
                                                                  $init(a);}
                
                // constructor for non-virtual call
                final public x10.lang.Activity.ClockPhases x10$lang$Activity$ClockPhases$$init$S(final x10.io.SerialData a) {x10$lang$Activity$ClockPhases$init_for_reflection(a);
                                                                                                                                 
                                                                                                                                 return this;
                                                                                                                                 }
                public void x10$lang$Activity$ClockPhases$init_for_reflection(x10.io.SerialData a) {
                     {
                        
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
super.$init(((x10.io.SerialData)(a)));
                        
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"

                    }}
                    
                // constructor
                public x10.lang.Activity.ClockPhases $init(final x10.io.SerialData a){return x10$lang$Activity$ClockPhases$$init$S(a);}
                
                
                
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final public x10.lang.Activity.ClockPhases
                                                                                                         x10$lang$Activity$ClockPhases$$x10$lang$Activity$ClockPhases$this(
                                                                                                         ){
                    
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
return x10.lang.Activity.ClockPhases.this;
                }
                
                public x10.io.SerialData
                  x10$util$HashMap$serialize$S(
                  ){
                    return super.serialize();
                }
            
        }
        
        
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
/**
     * the finish state governing the execution of this activity (may be remote)
     */
        public x10.lang.FinishState finishState;
        
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
/**
     * The user-specified code for this activity.
     */
        public x10.core.fun.VoidFun_0_0 body;
        
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
/**
     * The mapping from registered clocks to phases for this activity.
     * Lazily created.
     */
        public x10.lang.Activity.ClockPhases clockPhases;
        
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
/**
     * Depth of enclosong atomic blocks
     */
        public int atomicDepth;
        
        
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
// creation method for java code (1-phase java constructor)
        public Activity(final x10.core.fun.VoidFun_0_0 body,
                        final x10.lang.FinishState finishState){this((java.lang.System[]) null);
                                                                    $init(body,finishState);}
        
        // constructor for non-virtual call
        final public x10.lang.Activity x10$lang$Activity$$init$S(final x10.core.fun.VoidFun_0_0 body,
                                                                 final x10.lang.FinishState finishState) { {
                                                                                                                  
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"

                                                                                                                  
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"

                                                                                                                  
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
this.__fieldInitializers50567();
                                                                                                                  
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
this.finishState = ((x10.lang.FinishState)(finishState));
                                                                                                                  
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
finishState.notifyActivityCreation();
                                                                                                                  
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
this.body = ((x10.core.fun.VoidFun_0_0)(body));
                                                                                                              }
                                                                                                              return this;
                                                                                                              }
        
        // constructor
        public x10.lang.Activity $init(final x10.core.fun.VoidFun_0_0 body,
                                       final x10.lang.FinishState finishState){return x10$lang$Activity$$init$S(body,finishState);}
        
        
        
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
// creation method for java code (1-phase java constructor)
        public Activity(final x10.core.fun.VoidFun_0_0 body,
                        final x10.lang.FinishState finishState,
                        final x10.lang.Activity.ClockPhases clockPhases){this((java.lang.System[]) null);
                                                                             $init(body,finishState,clockPhases);}
        
        // constructor for non-virtual call
        final public x10.lang.Activity x10$lang$Activity$$init$S(final x10.core.fun.VoidFun_0_0 body,
                                                                 final x10.lang.FinishState finishState,
                                                                 final x10.lang.Activity.ClockPhases clockPhases) { {
                                                                                                                           
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
this.$init(((x10.core.fun.VoidFun_0_0)(body)),
                                                                                                                                                                                                                              ((x10.lang.FinishState)(finishState)));
                                                                                                                           
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
this.clockPhases = ((x10.lang.Activity.ClockPhases)(clockPhases));
                                                                                                                       }
                                                                                                                       return this;
                                                                                                                       }
        
        // constructor
        public x10.lang.Activity $init(final x10.core.fun.VoidFun_0_0 body,
                                       final x10.lang.FinishState finishState,
                                       final x10.lang.Activity.ClockPhases clockPhases){return x10$lang$Activity$$init$S(body,finishState,clockPhases);}
        
        
        
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
public x10.lang.Activity.ClockPhases
                                                                                                  clockPhases(
                                                                                                  ){
            
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.lang.Activity.ClockPhases t50638 =
              ((x10.lang.Activity.ClockPhases)(clockPhases));
            
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final boolean t50640 =
              ((null) == (t50638));
            
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
if (t50640) {
                
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.lang.Activity.ClockPhases t50639 =
                  ((x10.lang.Activity.ClockPhases)(new x10.lang.Activity.ClockPhases((java.lang.System[]) null).$init()));
                
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
this.clockPhases = ((x10.lang.Activity.ClockPhases)(t50639));
            }
            
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.lang.Activity.ClockPhases t50641 =
              ((x10.lang.Activity.ClockPhases)(clockPhases));
            
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
return t50641;
        }
        
        
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
public x10.lang.FinishState
                                                                                                  finishState(
                                                                                                  ){
            
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.lang.FinishState t50642 =
              ((x10.lang.FinishState)(finishState));
            
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
return t50642;
        }
        
        
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
public x10.lang.FinishState
                                                                                                  swapFinish(
                                                                                                  final x10.lang.FinishState f){
            
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.lang.FinishState old =
              ((x10.lang.FinishState)(finishState));
            
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
this.finishState = ((x10.lang.FinishState)(f));
            
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
return old;
        }
        
        
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
public void
                                                                                                  pushAtomic(
                                                                                                  ){
            
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.lang.Activity x50606 =
              ((x10.lang.Activity)(this));
            
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
;
            
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final int t50643 =
              x50606.
                atomicDepth;
            
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final int t50644 =
              ((t50643) + (((int)(1))));
            
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
x50606.atomicDepth = t50644;
        }
        
        
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
public void
                                                                                                  popAtomic(
                                                                                                  ){
            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.lang.Activity x50608 =
              ((x10.lang.Activity)(this));
            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
;
            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final int t50645 =
              x50608.
                atomicDepth;
            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final int t50646 =
              ((t50645) - (((int)(1))));
            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
x50608.atomicDepth = t50646;
        }
        
        
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
public void
                                                                                                  ensureNotInAtomic(
                                                                                                  ){
            
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final int t50647 =
              atomicDepth;
            
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final boolean t50649 =
              ((t50647) > (((int)(0))));
            
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
if (t50649) {
                
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.lang.IllegalOperationException t50648 =
                  ((x10.lang.IllegalOperationException)(new x10.lang.IllegalOperationException()));
                
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
throw t50648;
            }
        }
        
        
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
public void
                                                                                                  run(
                                                                                                  ){
            
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
try {try {{
                
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.core.fun.VoidFun_0_0 t50650 =
                  ((x10.core.fun.VoidFun_0_0)(body));
                
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
((x10.core.fun.VoidFun_0_0)t50650).$apply();
            }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (final x10.core.X10Throwable t) {
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.lang.FinishState t50651 =
                  ((x10.lang.FinishState)(finishState));
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
t50651.pushException(((x10.core.X10Throwable)(t)));
            }
            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.lang.Activity.ClockPhases t50652 =
              ((x10.lang.Activity.ClockPhases)(clockPhases));
            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final boolean t50654 =
              ((null) != (t50652));
            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
if (t50654) {
                
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.lang.Activity.ClockPhases t50653 =
                  ((x10.lang.Activity.ClockPhases)(clockPhases));
                
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
t50653.drop();
            }
            
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.lang.FinishState t50655 =
              ((x10.lang.FinishState)(finishState));
            
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
t50655.notifyActivityTermination();
            
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final x10.core.fun.VoidFun_0_0 t50656 =
              ((x10.core.fun.VoidFun_0_0)(body));
            
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
x10.lang.Runtime.dealloc(((x10.core.fun.VoidFun_0_0)(t50656)));
        }
        
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final public x10.lang.Activity
                                                                                                 x10$lang$Activity$$x10$lang$Activity$this(
                                                                                                 ){
            
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
return x10.lang.Activity.this;
        }
        
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
final public void
                                                                                                 __fieldInitializers50567(
                                                                                                 ){
            
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
this.finishState = null;
            
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
this.clockPhases = null;
            
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Activity.x10"
this.atomicDepth = 0;
        }
    
}
