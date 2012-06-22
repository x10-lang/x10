package x10.lang;


@x10.core.X10Generated final public class Place extends x10.core.Struct implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Place.class);
    
    public static final x10.rtt.RuntimeType<Place> $RTT = x10.rtt.NamedType.<Place> make(
    "x10.lang.Place", /* base class */Place.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.STRUCT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Place $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Place.class + " calling"); } 
        $_obj.id = $deserializer.readInt();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Place $_obj = new Place((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.write(this.id);
        
    }
    
    // zero value constructor
    public Place(final java.lang.System $dummy) { this.id = 0; }
    // constructor just for allocation
    public Place(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
public int id;
        
        
        
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public int
                                                                                              id$O(
                                                                                              ){
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55474 =
              id;
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return t55474;
        }
        
        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
/** The number of places including accelerators.
     * Accelerator places have limitations on the kinds of code they can run.
     */
        final public static int ALL_PLACES = 4;
        
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
/** The number of places not including accelerators. */
        final public static int MAX_PLACES = 4;
        
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
/** The number of accelerators. */
        final public static int NUM_ACCELS = ((x10.runtime.impl.java.Runtime.MAX_PLACES) - (((int)(x10.runtime.impl.java.Runtime.MAX_PLACES))));
        
        
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public static int
                                                                                              numChildren$O(
                                                                                              final int id){
            
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return 0;
        }
        
        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public static boolean
                                                                                              isHost$O(
                                                                                              final int id){
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return true;
        }
        
        
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public static boolean
                                                                                              isSPE$O(
                                                                                              final int id){
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return false;
        }
        
        
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public static boolean
                                                                                              isCUDA$O(
                                                                                              final int id){
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return false;
        }
        
        
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public static int
                                                                                              parent$O(
                                                                                              final int id){
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return id;
        }
        
        
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public static int
                                                                                              child$O(
                                                                                              final int p,
                                                                                              final int i){
            
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final x10.lang.BadPlaceException t55475 =
              ((x10.lang.BadPlaceException)(new x10.lang.BadPlaceException()));
            
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
throw t55475;
        }
        
        
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public static int
                                                                                              childIndex$O(
                                                                                              final int id){
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final x10.lang.BadPlaceException t55476 =
              ((x10.lang.BadPlaceException)(new x10.lang.BadPlaceException()));
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
throw t55476;
        }
        
        
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
public static x10.array.Array<x10.array.Array<x10.lang.Place>> childrenArray;
        
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
public static x10.array.Array<x10.lang.Place> places;
        
        
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public static x10.lang.Sequence<x10.lang.Place>
                                                                                               places(
                                                                                               ){
            
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final x10.array.Array<x10.lang.Place> t55477 =
              ((x10.array.Array)(x10.lang.Place.getInitialized$places()));
            
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final x10.lang.Sequence<x10.lang.Place> t55478 =
              ((x10.lang.Sequence<x10.lang.Place>)
                ((x10.array.Array<x10.lang.Place>)t55477).sequence());
            
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return t55478;
        }
        
        
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
public static x10.lang.Iterable<x10.array.Array<x10.lang.Place>> children;
        
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
public static x10.lang.Place FIRST_PLACE;
        
        
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
// creation method for java code (1-phase java constructor)
        public Place(final int id){this((java.lang.System[]) null);
                                       $init(id);}
        
        // constructor for non-virtual call
        final public x10.lang.Place x10$lang$Place$$init$S(final int id) { {
                                                                                  
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
;
                                                                                  
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
this.id = id;
                                                                                  
                                                                                  
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
boolean t55481 =
                                                                                    true;
                                                                                  
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
if (t55481) {
                                                                                      
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
boolean t55480 =
                                                                                        ((id) < (((int)(0))));
                                                                                      
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
if (!(t55480)) {
                                                                                          
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55479 =
                                                                                            x10.runtime.impl.java.Runtime.MAX_PLACES;
                                                                                          
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
t55480 = ((id) >= (((int)(t55479))));
                                                                                      }
                                                                                      
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
t55481 = t55480;
                                                                                  }
                                                                                  
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final boolean t55484 =
                                                                                    t55481;
                                                                                  
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
if (t55484) {
                                                                                      
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final java.lang.String t55482 =
                                                                                        (((x10.core.Int.$box(id))) + (" is not a valid Place id"));
                                                                                      
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final x10.lang.IllegalArgumentException t55483 =
                                                                                        ((x10.lang.IllegalArgumentException)(new x10.lang.IllegalArgumentException(t55482)));
                                                                                      
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
throw t55483;
                                                                                  }
                                                                              }
                                                                              return this;
                                                                              }
        
        // constructor
        public x10.lang.Place $init(final int id){return x10$lang$Place$$init$S(id);}
        
        
        
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public static x10.lang.Place
                                                                                               place(
                                                                                               final int id){
            
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final x10.lang.Place t55485 =
              ((x10.lang.Place)(new x10.lang.Place((java.lang.System[]) null).$init(((int)(id)))));
            
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return t55485;
        }
        
        
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public x10.lang.Place
                                                                                               next(
                                                                                               ){
            
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final x10.lang.Place t55486 =
              this.next((int)(1));
            
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return t55486;
        }
        
        
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public x10.lang.Place
                                                                                               prev(
                                                                                               ){
            
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final x10.lang.Place t55487 =
              this.next((int)(-1));
            
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return t55487;
        }
        
        
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public x10.lang.Place
                                                                                               prev(
                                                                                               final int i){
            
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55488 =
              (-(i));
            
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final x10.lang.Place t55489 =
              this.next((int)(t55488));
            
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return t55489;
        }
        
        
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public x10.lang.Place
                                                                                               next(
                                                                                               final int i){
            
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55490 =
              id;
            
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final boolean t55499 =
              x10.lang.Place.isHost$O((int)(t55490));
            
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
if (t55499) {
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55492 =
                  id;
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55491 =
                  x10.runtime.impl.java.Runtime.MAX_PLACES;
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55493 =
                  ((i) % (((int)(t55491))));
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55494 =
                  ((t55492) + (((int)(t55493))));
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55495 =
                  x10.runtime.impl.java.Runtime.MAX_PLACES;
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55496 =
                  ((t55494) + (((int)(t55495))));
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55497 =
                  x10.runtime.impl.java.Runtime.MAX_PLACES;
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int k =
                  ((t55496) % (((int)(t55497))));
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final x10.lang.Place t55498 =
                  ((x10.lang.Place)(x10.lang.Place.place((int)(k))));
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return t55498;
            }
            
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return this;
        }
        
        
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public static int
                                                                                               numPlaces$O(
                                                                                               ){
            
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55500 =
              x10.runtime.impl.java.Runtime.MAX_PLACES;
            
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return t55500;
        }
        
        
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public boolean
                                                                                               isFirst$O(
                                                                                               ){
            
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55501 =
              id;
            
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final boolean t55502 =
              ((int) t55501) ==
            ((int) 0);
            
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return t55502;
        }
        
        
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public boolean
                                                                                               isLast$O(
                                                                                               ){
            
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55504 =
              id;
            
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55503 =
              x10.runtime.impl.java.Runtime.MAX_PLACES;
            
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55505 =
              ((t55503) - (((int)(1))));
            
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final boolean t55506 =
              ((int) t55504) ==
            ((int) t55505);
            
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return t55506;
        }
        
        
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public boolean
                                                                                               isHost$O(
                                                                                               ){
            
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55507 =
              id;
            
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final boolean t55508 =
              x10.lang.Place.isHost$O((int)(t55507));
            
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return t55508;
        }
        
        
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public boolean
                                                                                               isSPE$O(
                                                                                               ){
            
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55509 =
              id;
            
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final boolean t55510 =
              x10.lang.Place.isSPE$O((int)(t55509));
            
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return t55510;
        }
        
        
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public boolean
                                                                                               isCUDA$O(
                                                                                               ){
            
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55511 =
              id;
            
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final boolean t55512 =
              x10.lang.Place.isCUDA$O((int)(t55511));
            
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return t55512;
        }
        
        
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public int
                                                                                               numChildren$O(
                                                                                               ){
            
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55513 =
              id;
            
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55514 =
              x10.lang.Place.numChildren$O((int)(t55513));
            
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return t55514;
        }
        
        
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public x10.lang.Place
                                                                                               child(
                                                                                               final int i){
            
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55515 =
              id;
            
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55516 =
              x10.lang.Place.child$O((int)(t55515),
                                     (int)(i));
            
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final x10.lang.Place t55517 =
              ((x10.lang.Place)(new x10.lang.Place((java.lang.System[]) null).$init(t55516)));
            
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return t55517;
        }
        
        
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public x10.array.Array<x10.lang.Place>
                                                                                               children(
                                                                                               ){
            
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final x10.array.Array<x10.array.Array<x10.lang.Place>> t55518 =
              ((x10.array.Array)(x10.lang.Place.getInitialized$childrenArray()));
            
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55519 =
              id;
            
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final x10.array.Array<x10.lang.Place> t55520 =
              ((x10.array.Array)(((x10.array.Array<x10.array.Array<x10.lang.Place>>)t55518).$apply$G((int)(t55519))));
            
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return t55520;
        }
        
        
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public x10.lang.Place
                                                                                               parent(
                                                                                               ){
            
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55521 =
              id;
            
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55522 =
              x10.lang.Place.parent$O((int)(t55521));
            
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final x10.lang.Place t55523 =
              ((x10.lang.Place)(new x10.lang.Place((java.lang.System[]) null).$init(t55522)));
            
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return t55523;
        }
        
        
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public int
                                                                                               childIndex$O(
                                                                                               ){
            
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final boolean t55525 =
              this.isHost$O();
            
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
if (t55525) {
                
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final x10.lang.BadPlaceException t55524 =
                  ((x10.lang.BadPlaceException)(new x10.lang.BadPlaceException()));
                
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
throw t55524;
            }
            
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55526 =
              id;
            
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55527 =
              x10.lang.Place.childIndex$O((int)(t55526));
            
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return t55527;
        }
        
        
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public java.lang.String
                                                                                               toString(
                                                                                               ){
            
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55528 =
              this.
                id;
            
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final java.lang.String t55529 =
              (("Place(") + ((x10.core.Int.$box(t55528))));
            
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final java.lang.String t55530 =
              ((t55529) + (")"));
            
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return t55530;
        }
        
        
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public boolean
                                                                                               equals$O(
                                                                                               final x10.lang.Place p){
            
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55531 =
              p.
                id;
            
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55532 =
              this.
                id;
            
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final boolean t55533 =
              ((int) t55531) ==
            ((int) t55532);
            
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return t55533;
        }
        
        
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public boolean
                                                                                               equals(
                                                                                               final java.lang.Object p){
            
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
boolean t55537 =
              x10.lang.Place.$RTT.isInstance(p);
            
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
if (t55537) {
                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final x10.lang.Place t55534 =
                  ((x10.lang.Place)(((x10.lang.Place)x10.rtt.Types.asStruct(x10.lang.Place.$RTT,p))));
                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55535 =
                  t55534.
                    id;
                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55536 =
                  this.
                    id;
                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
t55537 = ((int) t55535) ==
                ((int) t55536);
            }
            
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final boolean t55538 =
              t55537;
            
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return t55538;
        }
        
        
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public int
                                                                                               hashCode(
                                                                                               ){
            
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55539 =
              id;
            
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return t55539;
        }
        
        
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public static <$T>x10.lang.Place
                                                                                               $implicit_convert__0$1x10$lang$Place$$T$2(
                                                                                               final x10.rtt.Type $T,
                                                                                               final x10.core.GlobalRef<$T> r){try {return (r).home;}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public java.lang.String
                                                                                              typeName(
                                                                                              ){try {return x10.rtt.Types.typeName(this);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public boolean
                                                                                              _struct_equals$O(
                                                                                              java.lang.Object other){
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final java.lang.Object t55540 =
              other;
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final boolean t55541 =
              x10.lang.Place.$RTT.isInstance(t55540);
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final boolean t55542 =
              !(t55541);
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
if (t55542) {
                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return false;
            }
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final java.lang.Object t55543 =
              other;
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final x10.lang.Place t55544 =
              ((x10.lang.Place)x10.rtt.Types.asStruct(x10.lang.Place.$RTT,t55543));
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final boolean t55545 =
              this._struct_equals$O(((x10.lang.Place)(t55544)));
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return t55545;
        }
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public boolean
                                                                                              _struct_equals$O(
                                                                                              x10.lang.Place other){
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55547 =
              this.
                id;
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final x10.lang.Place t55546 =
              other;
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final int t55548 =
              t55546.
                id;
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final boolean t55549 =
              ((int) t55547) ==
            ((int) t55548);
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return t55549;
        }
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
final public x10.lang.Place
                                                                                              x10$lang$Place$$x10$lang$Place$this(
                                                                                              ){
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return x10.lang.Place.this;
        }
        
        public static short fieldId$FIRST_PLACE;
        final public static x10.core.concurrent.AtomicInteger initStatus$FIRST_PLACE = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        public static short fieldId$children;
        final public static x10.core.concurrent.AtomicInteger initStatus$children = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        public static short fieldId$places;
        final public static x10.core.concurrent.AtomicInteger initStatus$places = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        public static short fieldId$childrenArray;
        final public static x10.core.concurrent.AtomicInteger initStatus$childrenArray = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        
        public static int
          getInitialized$ALL_PLACES(
          ){
            return x10.runtime.impl.java.Runtime.MAX_PLACES;
        }
        
        public static int
          getInitialized$MAX_PLACES(
          ){
            return x10.runtime.impl.java.Runtime.MAX_PLACES;
        }
        
        public static int
          getInitialized$NUM_ACCELS(
          ){
            return x10.lang.Place.NUM_ACCELS;
        }
        
        public static void
          getDeserialized$childrenArray(
          x10.x10rt.X10JavaDeserializer deserializer){
            x10.lang.Place.childrenArray = ((x10.array.Array)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
            x10.lang.Place.initStatus$childrenArray.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static x10.array.Array<x10.array.Array<x10.lang.Place>>
          getInitialized$childrenArray(
          ){
            if (((int) x10.lang.Place.initStatus$childrenArray.get()) ==
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                return x10.lang.Place.childrenArray;
            }
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0) &&
                  x10.lang.Place.initStatus$childrenArray.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                        (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                x10.lang.Place.childrenArray = ((x10.array.Array)(new x10.array.Array<x10.array.Array<x10.lang.Place>>((java.lang.System[]) null, x10.rtt.ParameterizedType.make(x10.array.Array.$RTT, x10.lang.Place.$RTT)).$init(((int)(x10.runtime.impl.java.Runtime.MAX_PLACES)),
                                                                                                                                                                                                                                   ((x10.core.fun.Fun_0_1)(new x10.lang.Place.$Closure$133())), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                      ((boolean) true)) {
                    x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.lang.Place.childrenArray")));
                }
                x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.lang.Place.childrenArray)),
                                                                          (short)(x10.lang.Place.fieldId$childrenArray));
                x10.lang.Place.initStatus$childrenArray.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                x10.runtime.impl.java.InitDispatcher.notifyInitialized();
            } else {
                if (((int) x10.lang.Place.initStatus$childrenArray.get()) !=
                    ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    while (((int) x10.lang.Place.initStatus$childrenArray.get()) !=
                           ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                    }
                    x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                }
            }
            return x10.lang.Place.childrenArray;
        }
        
        public static void
          getDeserialized$places(
          x10.x10rt.X10JavaDeserializer deserializer){
            x10.lang.Place.places = ((x10.array.Array)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
            x10.lang.Place.initStatus$places.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static x10.array.Array<x10.lang.Place>
          getInitialized$places(
          ){
            if (((int) x10.lang.Place.initStatus$places.get()) ==
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                return x10.lang.Place.places;
            }
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0) &&
                  x10.lang.Place.initStatus$places.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                 (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                x10.lang.Place.places = ((x10.array.Array)(new x10.array.Array<x10.lang.Place>((java.lang.System[]) null, x10.lang.Place.$RTT).$init(((int)(x10.runtime.impl.java.Runtime.MAX_PLACES)),
                                                                                                                                                     ((x10.core.fun.Fun_0_1)(new x10.lang.Place.$Closure$134())), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                      ((boolean) true)) {
                    x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.lang.Place.places")));
                }
                x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.lang.Place.places)),
                                                                          (short)(x10.lang.Place.fieldId$places));
                x10.lang.Place.initStatus$places.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                x10.runtime.impl.java.InitDispatcher.notifyInitialized();
            } else {
                if (((int) x10.lang.Place.initStatus$places.get()) !=
                    ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    while (((int) x10.lang.Place.initStatus$places.get()) !=
                           ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                    }
                    x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                }
            }
            return x10.lang.Place.places;
        }
        
        public static void
          getDeserialized$children(
          x10.x10rt.X10JavaDeserializer deserializer){
            x10.lang.Place.children = ((x10.lang.Iterable)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
            x10.lang.Place.initStatus$children.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static x10.lang.Iterable<x10.array.Array<x10.lang.Place>>
          getInitialized$children(
          ){
            if (((int) x10.lang.Place.initStatus$children.get()) ==
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                return x10.lang.Place.children;
            }
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0) &&
                  x10.lang.Place.initStatus$children.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                   (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                x10.lang.Place.children = ((x10.lang.Iterable<x10.array.Array<x10.lang.Place>>)
                                            ((x10.array.Array<x10.array.Array<x10.lang.Place>>)x10.lang.Place.getInitialized$childrenArray()).values());
                if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                      ((boolean) true)) {
                    x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.lang.Place.children")));
                }
                x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.lang.Place.children)),
                                                                          (short)(x10.lang.Place.fieldId$children));
                x10.lang.Place.initStatus$children.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                x10.runtime.impl.java.InitDispatcher.notifyInitialized();
            } else {
                if (((int) x10.lang.Place.initStatus$children.get()) !=
                    ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    while (((int) x10.lang.Place.initStatus$children.get()) !=
                           ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                    }
                    x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                }
            }
            return x10.lang.Place.children;
        }
        
        public static void
          getDeserialized$FIRST_PLACE(
          x10.x10rt.X10JavaDeserializer deserializer){
            x10.lang.Place.FIRST_PLACE = ((x10.lang.Place)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
            x10.lang.Place.initStatus$FIRST_PLACE.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static x10.lang.Place
          getInitialized$FIRST_PLACE(
          ){
            if (((int) x10.lang.Place.initStatus$FIRST_PLACE.get()) ==
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                return x10.lang.Place.FIRST_PLACE;
            }
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0) &&
                  x10.lang.Place.initStatus$FIRST_PLACE.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                      (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                x10.lang.Place.FIRST_PLACE = ((x10.lang.Place)(new x10.lang.Place((java.lang.System[]) null).$init(((int)(0)))));
                if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                      ((boolean) true)) {
                    x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.lang.Place.FIRST_PLACE")));
                }
                x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.lang.Place.FIRST_PLACE)),
                                                                          (short)(x10.lang.Place.fieldId$FIRST_PLACE));
                x10.lang.Place.initStatus$FIRST_PLACE.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                x10.runtime.impl.java.InitDispatcher.notifyInitialized();
            } else {
                if (((int) x10.lang.Place.initStatus$FIRST_PLACE.get()) !=
                    ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    while (((int) x10.lang.Place.initStatus$FIRST_PLACE.get()) !=
                           ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                    }
                    x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                }
            }
            return x10.lang.Place.FIRST_PLACE;
        }
        
        static {
                   x10.lang.Place.fieldId$childrenArray = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.lang.Place")),
                                                                                                                              ((java.lang.String)("childrenArray")))))));
                   x10.lang.Place.fieldId$places = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.lang.Place")),
                                                                                                                       ((java.lang.String)("places")))))));
                   x10.lang.Place.fieldId$children = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.lang.Place")),
                                                                                                                         ((java.lang.String)("children")))))));
                   x10.lang.Place.fieldId$FIRST_PLACE = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.lang.Place")),
                                                                                                                            ((java.lang.String)("FIRST_PLACE")))))));
               }
        
        @x10.core.X10Generated public static class $Closure$132 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$132.class);
            
            public static final x10.rtt.RuntimeType<$Closure$132> $RTT = x10.rtt.StaticFunType.<$Closure$132> make(
            /* base class */$Closure$132.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.lang.Place.$RTT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$132 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$132.class + " calling"); } 
                $_obj.p = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$132 $_obj = new $Closure$132((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write(this.p);
                
            }
            
            // constructor just for allocation
            public $Closure$132(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return $apply(x10.core.Int.$unbox(a1));
            }
            
                
                public x10.lang.Place
                  $apply(
                  final int i){
                    
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return new x10.lang.Place((java.lang.System[]) null).$init(x10.lang.Place.child$O((int)(this.
                                                                                                                                                                                                  p),
                                                                                                                                                                                          (int)(i)));
                }
                
                public int p;
                
                public $Closure$132(final int p) { {
                                                          this.p = p;
                                                      }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$133 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$133.class);
            
            public static final x10.rtt.RuntimeType<$Closure$133> $RTT = x10.rtt.StaticFunType.<$Closure$133> make(
            /* base class */$Closure$133.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.ParameterizedType.make(x10.array.Array.$RTT, x10.lang.Place.$RTT)), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$133 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$133.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$133 $_obj = new $Closure$133((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public $Closure$133(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return $apply(x10.core.Int.$unbox(a1));
            }
            
                
                public x10.array.Array<x10.lang.Place>
                  $apply(
                  final int p){
                    
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return new x10.array.Array<x10.lang.Place>((java.lang.System[]) null, x10.lang.Place.$RTT).$init(x10.lang.Place.numChildren$O((int)(p)),
                                                                                                                                                                                                         ((x10.core.fun.Fun_0_1)(new x10.lang.Place.$Closure$132(p))), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null);
                }
                
                public $Closure$133() { {
                                               
                                           }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$134 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$134.class);
            
            public static final x10.rtt.RuntimeType<$Closure$134> $RTT = x10.rtt.StaticFunType.<$Closure$134> make(
            /* base class */$Closure$134.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.lang.Place.$RTT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$134 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$134.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$134 $_obj = new $Closure$134((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public $Closure$134(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return $apply(x10.core.Int.$unbox(a1));
            }
            
                
                public x10.lang.Place
                  $apply(
                  final int id){
                    
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Place.x10"
return new x10.lang.Place((java.lang.System[]) null).$init(((int)(id)));
                }
                
                public $Closure$134() { {
                                               
                                           }}
                
            }
            
        
        }
        
        
        