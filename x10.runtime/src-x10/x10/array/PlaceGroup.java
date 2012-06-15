package x10.array;

@x10.core.X10Generated abstract public class PlaceGroup extends x10.core.Ref implements x10.lang.Sequence, x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, PlaceGroup.class);
    
    public static final x10.rtt.RuntimeType<PlaceGroup> $RTT = x10.rtt.NamedType.<PlaceGroup> make(
    "x10.array.PlaceGroup", /* base class */PlaceGroup.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Sequence.$RTT, x10.lang.Place.$RTT), x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(PlaceGroup $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + PlaceGroup.class + " calling"); } 
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        return null;
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        
    }
    
    // constructor just for allocation
    public PlaceGroup(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1){}:U
    public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
    return $apply(x10.core.Int.$unbox(a1));
    }
    // bridge for method abstract public x10.lang.Sequence.operator()(x10.lang.Int):T
    public x10.lang.Place
      $apply$G(int a1){return $apply(a1);}
    
        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
public static x10.array.PlaceGroup.WorldPlaceGroup WORLD;
        
        
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final public int
                                                                                                    size$O(
                                                                                                    ){
            
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final int t39495 =
              this.numPlaces$O();
            
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
return t39495;
        }
        
        
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
abstract public int
                                                                                                    numPlaces$O(
                                                                                                    );
        
        
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
public boolean
                                                                                                    contains$O(
                                                                                                    final x10.lang.Place place){
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final int t39496 =
              place.
                id;
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final boolean t39497 =
              this.contains$O((int)(t39496));
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
return t39497;
        }
        
        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
abstract public boolean
                                                                                                    contains$O(
                                                                                                    final int id);
        
        
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
public int
                                                                                                    indexOf$O(
                                                                                                    final x10.lang.Place place){
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final int t39498 =
              place.
                id;
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final int t39499 =
              this.indexOf$O((int)(t39498));
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
return t39499;
        }
        
        
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
abstract public int
                                                                                                    indexOf$O(
                                                                                                    final int id);
        
        
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
abstract public x10.lang.Place
                                                                                                    $apply(
                                                                                                    final int i);
        
        
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
public boolean
                                                                                                    equals(
                                                                                                    final java.lang.Object thatObj){
            
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final boolean t39500 =
              x10.rtt.Equality.equalsequals((this),(thatObj));
            
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
if (t39500) {
                
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
return true;
            }
            
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final boolean t39501 =
              x10.array.PlaceGroup.$RTT.isInstance(thatObj);
            
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final boolean t39502 =
              !(t39501);
            
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
if (t39502) {
                
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
return false;
            }
            
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final x10.array.PlaceGroup that =
              ((x10.array.PlaceGroup)(x10.rtt.Types.<x10.array.PlaceGroup> cast(thatObj,x10.array.PlaceGroup.$RTT)));
            
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final int t39503 =
              this.numPlaces$O();
            
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final int t39504 =
              that.numPlaces$O();
            
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final boolean t39505 =
              ((int) t39503) !=
            ((int) t39504);
            
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
if (t39505) {
                
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
return false;
            }
            
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
int i39541 =
              0;
            
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
for (;
                                                                                                            true;
                                                                                                            ) {
                
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final int t39542 =
                  i39541;
                
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final int t39543 =
                  this.numPlaces$O();
                
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final boolean t39544 =
                  ((t39542) < (((int)(t39543))));
                
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
if (!(t39544)) {
                    
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
break;
                }
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final int t39533 =
                  i39541;
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final x10.lang.Place t39534 =
                  this.$apply((int)(t39533));
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final int t39535 =
                  i39541;
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final x10.lang.Place t39536 =
                  that.$apply((int)(t39535));
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final boolean t39537 =
                  t39534.equals$O(((x10.lang.Place)(t39536)));
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final boolean t39538 =
                  !(t39537);
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
if (t39538) {
                    
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
return false;
                }
                
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final int t39539 =
                  i39541;
                
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final int t39540 =
                  ((t39539) + (((int)(1))));
                
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
i39541 = t39540;
            }
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
return true;
        }
        
        
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
@x10.core.X10Generated public static class WorldPlaceGroup extends x10.array.PlaceGroup implements x10.x10rt.X10JavaSerializable
                                                                                                   {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, WorldPlaceGroup.class);
            
            public static final x10.rtt.RuntimeType<WorldPlaceGroup> $RTT = x10.rtt.NamedType.<WorldPlaceGroup> make(
            "x10.array.PlaceGroup.WorldPlaceGroup", /* base class */WorldPlaceGroup.class
            , /* parents */ new x10.rtt.Type[] {x10.array.PlaceGroup.$RTT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(WorldPlaceGroup $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + WorldPlaceGroup.class + " calling"); } 
                x10.array.PlaceGroup.$_deserialize_body($_obj, $deserializer);
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                WorldPlaceGroup $_obj = new WorldPlaceGroup((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                super.$_serialize($serializer);
                
            }
            
            // constructor just for allocation
            public WorldPlaceGroup(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            
                
                
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
public x10.lang.Place
                                                                                                             $apply(
                                                                                                             final int i){
                    
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final x10.lang.Place t39518 =
                      ((x10.lang.Place)(x10.lang.Place.place((int)(i))));
                    
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
return t39518;
                }
                
                
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
public x10.lang.Iterator<x10.lang.Place>
                                                                                                             iterator(
                                                                                                             ){
                    
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final x10.lang.Sequence<x10.lang.Place> t39519 =
                      x10.lang.Place.places();
                    
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final x10.lang.Iterator<x10.lang.Place> t39520 =
                      ((x10.lang.Iterator<x10.lang.Place>)
                        ((x10.lang.Iterable<x10.lang.Place>)t39519).iterator());
                    
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
return t39520;
                }
                
                
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
public int
                                                                                                             numPlaces$O(
                                                                                                             ){
                    
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final int t39521 =
                      x10.lang.Place.numPlaces$O();
                    
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
return t39521;
                }
                
                
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
public boolean
                                                                                                             contains$O(
                                                                                                             final int id){
                    
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
boolean t39523 =
                      ((id) >= (((int)(0))));
                    
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
if (t39523) {
                        
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final int t39522 =
                          x10.lang.Place.numPlaces$O();
                        
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
t39523 = ((id) < (((int)(t39522))));
                    }
                    
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final boolean t39524 =
                      t39523;
                    
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
return t39524;
                }
                
                
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
public int
                                                                                                             indexOf$O(
                                                                                                             final int id){
                    
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final boolean t39525 =
                      this.contains$O((int)(id));
                    
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
int t39526 =
                       0;
                    
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
if (t39525) {
                        
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
t39526 = id;
                    } else {
                        
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
t39526 = -1;
                    }
                    
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final int t39527 =
                      t39526;
                    
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
return t39527;
                }
                
                
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
public boolean
                                                                                                             equals(
                                                                                                             final java.lang.Object thatObj){
                    
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final boolean t39528 =
                      x10.array.PlaceGroup.WorldPlaceGroup.$RTT.isInstance(thatObj);
                    
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
boolean t39529 =
                       false;
                    
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
if (t39528) {
                        
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
t39529 = true;
                    } else {
                        
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
t39529 = super.equals(((java.lang.Object)(thatObj)));
                    }
                    
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final boolean t39530 =
                      t39529;
                    
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
return t39530;
                }
                
                
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
public int
                                                                                                             hashCode(
                                                                                                             ){
                    
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final int t39531 =
                      x10.lang.Place.numPlaces$O();
                    
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final int t39532 =
                      x10.rtt.Types.hashCode(t39531);
                    
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
return t39532;
                }
                
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final public x10.array.PlaceGroup.WorldPlaceGroup
                                                                                                             x10$array$PlaceGroup$WorldPlaceGroup$$x10$array$PlaceGroup$WorldPlaceGroup$this(
                                                                                                             ){
                    
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
return x10.array.PlaceGroup.WorldPlaceGroup.this;
                }
                
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
// creation method for java code (1-phase java constructor)
                public WorldPlaceGroup(){this((java.lang.System[]) null);
                                             $init();}
                
                // constructor for non-virtual call
                final public x10.array.PlaceGroup.WorldPlaceGroup x10$array$PlaceGroup$WorldPlaceGroup$$init$S() { {
                                                                                                                          
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
super.$init();
                                                                                                                          
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"

                                                                                                                      }
                                                                                                                      return this;
                                                                                                                      }
                
                // constructor
                public x10.array.PlaceGroup.WorldPlaceGroup $init(){return x10$array$PlaceGroup$WorldPlaceGroup$$init$S();}
                
                
                public boolean
                  x10$array$PlaceGroup$equals$S$O(
                  final java.lang.Object a0){
                    return super.equals(((java.lang.Object)(a0)));
                }
            
        }
        
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
final public x10.array.PlaceGroup
                                                                                                    x10$array$PlaceGroup$$x10$array$PlaceGroup$this(
                                                                                                    ){
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
return x10.array.PlaceGroup.this;
        }
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"

        // constructor for non-virtual call
        final public x10.array.PlaceGroup x10$array$PlaceGroup$$init$S() { {
                                                                                  
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"

                                                                                  
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"

                                                                              }
                                                                              return this;
                                                                              }
        
        // constructor
        public x10.array.PlaceGroup $init(){return x10$array$PlaceGroup$$init$S();}
        
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PlaceGroup.x10"
abstract public x10.lang.Iterator<x10.lang.Place>
                                                                                                    iterator(
                                                                                                    );
        
        public static short fieldId$WORLD;
        final public static x10.core.concurrent.AtomicInteger initStatus$WORLD = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        
        public static void
          getDeserialized$WORLD(
          x10.x10rt.X10JavaDeserializer deserializer){
            x10.array.PlaceGroup.WORLD = ((x10.array.PlaceGroup.WorldPlaceGroup)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
            x10.array.PlaceGroup.initStatus$WORLD.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static x10.array.PlaceGroup.WorldPlaceGroup
          getInitialized$WORLD(
          ){
            if (((int) x10.array.PlaceGroup.initStatus$WORLD.get()) ==
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                return x10.array.PlaceGroup.WORLD;
            }
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0) &&
                  x10.array.PlaceGroup.initStatus$WORLD.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                      (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                x10.array.PlaceGroup.WORLD = ((x10.array.PlaceGroup.WorldPlaceGroup)(new x10.array.PlaceGroup.WorldPlaceGroup((java.lang.System[]) null).$init()));
                if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                      ((boolean) true)) {
                    x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.array.PlaceGroup.WORLD")));
                }
                x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.array.PlaceGroup.WORLD)),
                                                                          (short)(x10.array.PlaceGroup.fieldId$WORLD));
                x10.array.PlaceGroup.initStatus$WORLD.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                x10.runtime.impl.java.InitDispatcher.notifyInitialized();
            } else {
                if (((int) x10.array.PlaceGroup.initStatus$WORLD.get()) !=
                    ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    while (((int) x10.array.PlaceGroup.initStatus$WORLD.get()) !=
                           ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                    }
                    x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                }
            }
            return x10.array.PlaceGroup.WORLD;
        }
        
        static {
                   x10.array.PlaceGroup.fieldId$WORLD = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.array.PlaceGroup")),
                                                                                                                            ((java.lang.String)("WORLD")))))));
               }
    
}
