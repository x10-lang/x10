package x10.array;

@x10.core.X10Generated final public class SparsePlaceGroup extends x10.array.PlaceGroup implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, SparsePlaceGroup.class);
    
    public static final x10.rtt.RuntimeType<SparsePlaceGroup> $RTT = x10.rtt.NamedType.<SparsePlaceGroup> make(
    "x10.array.SparsePlaceGroup", /* base class */SparsePlaceGroup.class
    , /* parents */ new x10.rtt.Type[] {x10.array.PlaceGroup.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(SparsePlaceGroup $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + SparsePlaceGroup.class + " calling"); } 
        x10.array.PlaceGroup.$_deserialize_body($_obj, $deserializer);
        x10.array.Array places = (x10.array.Array) $deserializer.readRef();
        $_obj.places = places;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        SparsePlaceGroup $_obj = new SparsePlaceGroup((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        if (places instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.places);
        } else {
        $serializer.write(this.places);
        }
        
    }
    
    // constructor just for allocation
    public SparsePlaceGroup(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
/**
   * The set of places.
   * The array is in sorted order by Place.id.
   * Only places that are in the group are in the array.
   */
        public x10.array.Array<x10.lang.Place> places;
        
        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
// creation method for java code (1-phase java constructor)
        public SparsePlaceGroup(final x10.lang.Sequence<x10.lang.Place> ps, __0$1x10$lang$Place$2 $dummy){this((java.lang.System[]) null);
                                                                                                              $init(ps, (x10.array.SparsePlaceGroup.__0$1x10$lang$Place$2) null);}
        
        // constructor for non-virtual call
        final public x10.array.SparsePlaceGroup x10$array$SparsePlaceGroup$$init$S(final x10.lang.Sequence<x10.lang.Place> ps, __0$1x10$lang$Place$2 $dummy) { {
                                                                                                                                                                      
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
super.$init();
                                                                                                                                                                      
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"

                                                                                                                                                                      
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final int t48066 =
                                                                                                                                                                        ((x10.lang.Sequence<x10.lang.Place>)ps).size$O();
                                                                                                                                                                      
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.lang.Place> t48067 =
                                                                                                                                                                        ((x10.core.fun.Fun_0_1)(new x10.array.SparsePlaceGroup.$Closure$83(ps, (x10.array.SparsePlaceGroup.$Closure$83.__0$1x10$lang$Place$2) null)));
                                                                                                                                                                      
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final x10.array.Array<x10.lang.Place> t48068 =
                                                                                                                                                                        ((x10.array.Array)(new x10.array.Array<x10.lang.Place>((java.lang.System[]) null, x10.lang.Place.$RTT).$init(((int)(t48066)),
                                                                                                                                                                                                                                                                                     ((x10.core.fun.Fun_0_1)(t48067)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                                                                                                                                                                      
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
this.places = ((x10.array.Array)(t48068));
                                                                                                                                                                      
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final x10.array.Array<x10.lang.Place> t48131 =
                                                                                                                                                                        ((x10.array.Array)(places));
                                                                                                                                                                      
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final int t48132 =
                                                                                                                                                                        ((x10.array.Array<x10.lang.Place>)t48131).
                                                                                                                                                                          size;
                                                                                                                                                                      
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final int t48133 =
                                                                                                                                                                        ((t48132) - (((int)(1))));
                                                                                                                                                                      
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final x10.lang.IntRange t48134 =
                                                                                                                                                                        ((x10.lang.IntRange)(new x10.lang.IntRange((java.lang.System[]) null).x10$lang$IntRange$$init$S(((int)(1)), ((int)(t48133)))));
                                                                                                                                                                      
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final x10.array.Region p48135 =
                                                                                                                                                                        ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t48134)))));
                                                                                                                                                                      
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final int i48010min48136 =
                                                                                                                                                                        p48135.min$O((int)(0));
                                                                                                                                                                      
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final int i48010max48137 =
                                                                                                                                                                        p48135.max$O((int)(0));
                                                                                                                                                                      
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
int i48128 =
                                                                                                                                                                        i48010min48136;
                                                                                                                                                                      
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
for (;
                                                                                                                                                                                                                                                                           true;
                                                                                                                                                                                                                                                                           ) {
                                                                                                                                                                          
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final int t48129 =
                                                                                                                                                                            i48128;
                                                                                                                                                                          
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final boolean t48130 =
                                                                                                                                                                            ((t48129) <= (((int)(i48010max48137))));
                                                                                                                                                                          
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
if (!(t48130)) {
                                                                                                                                                                              
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
break;
                                                                                                                                                                          }
                                                                                                                                                                          
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final int i48125 =
                                                                                                                                                                            i48128;
                                                                                                                                                                          
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final x10.array.Array<x10.lang.Place> t48116 =
                                                                                                                                                                            ((x10.array.Array)(places));
                                                                                                                                                                          
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final x10.lang.Place t48117 =
                                                                                                                                                                            ((x10.array.Array<x10.lang.Place>)t48116).$apply$G((int)(i48125));
                                                                                                                                                                          
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final int t48118 =
                                                                                                                                                                            t48117.
                                                                                                                                                                              id;
                                                                                                                                                                          
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final x10.array.Array<x10.lang.Place> t48119 =
                                                                                                                                                                            ((x10.array.Array)(places));
                                                                                                                                                                          
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final int t48120 =
                                                                                                                                                                            ((i48125) - (((int)(1))));
                                                                                                                                                                          
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final x10.lang.Place t48121 =
                                                                                                                                                                            ((x10.array.Array<x10.lang.Place>)t48119).$apply$G((int)(t48120));
                                                                                                                                                                          
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final int t48122 =
                                                                                                                                                                            t48121.
                                                                                                                                                                              id;
                                                                                                                                                                          
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final boolean t48123 =
                                                                                                                                                                            ((t48118) <= (((int)(t48122))));
                                                                                                                                                                          
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
if (t48123) {
                                                                                                                                                                              
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final x10.lang.IllegalArgumentException t48124 =
                                                                                                                                                                                ((x10.lang.IllegalArgumentException)(new x10.lang.IllegalArgumentException(((java.lang.String)("Argument sequence was not sorted")))));
                                                                                                                                                                              
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
throw t48124;
                                                                                                                                                                          }
                                                                                                                                                                          
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final int t48126 =
                                                                                                                                                                            i48128;
                                                                                                                                                                          
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final int t48127 =
                                                                                                                                                                            ((t48126) + (((int)(1))));
                                                                                                                                                                          
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
i48128 = t48127;
                                                                                                                                                                      }
                                                                                                                                                                  }
                                                                                                                                                                  return this;
                                                                                                                                                                  }
        
        // constructor
        public x10.array.SparsePlaceGroup $init(final x10.lang.Sequence<x10.lang.Place> ps, __0$1x10$lang$Place$2 $dummy){return x10$array$SparsePlaceGroup$$init$S(ps, $dummy);}
        
        
        
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
// creation method for java code (1-phase java constructor)
        public SparsePlaceGroup(final x10.lang.Place p){this((java.lang.System[]) null);
                                                            $init(p);}
        
        // constructor for non-virtual call
        final public x10.array.SparsePlaceGroup x10$array$SparsePlaceGroup$$init$S(final x10.lang.Place p) { {
                                                                                                                    
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
super.$init();
                                                                                                                    
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"

                                                                                                                    
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final x10.lang.Place t48087 =
                                                                                                                      ((x10.lang.Place)x10.rtt.Types.asStruct(x10.lang.Place.$RTT,p));
                                                                                                                    
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final x10.array.Array<x10.lang.Place> t48088 =
                                                                                                                      ((x10.array.Array)(x10.core.ArrayFactory.<x10.lang.Place> makeArrayFromJavaArray(x10.lang.Place.$RTT, new x10.lang.Place[] {t48087})));
                                                                                                                    
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
this.places = ((x10.array.Array)(t48088));
                                                                                                                }
                                                                                                                return this;
                                                                                                                }
        
        // constructor
        public x10.array.SparsePlaceGroup $init(final x10.lang.Place p){return x10$array$SparsePlaceGroup$$init$S(p);}
        
        
        
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
public x10.lang.Place
                                                                                                          $apply(
                                                                                                          final int i){
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final x10.array.Array<x10.lang.Place> t48089 =
              ((x10.array.Array)(places));
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final x10.lang.Place t48090 =
              ((x10.array.Array<x10.lang.Place>)t48089).$apply$G((int)(i));
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
return t48090;
        }
        
        
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
public x10.lang.Iterator<x10.lang.Place>
                                                                                                          iterator(
                                                                                                          ){
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final x10.array.Array<x10.lang.Place> t48091 =
              ((x10.array.Array)(places));
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final x10.lang.Iterable<x10.lang.Place> t48092 =
              ((x10.lang.Iterable<x10.lang.Place>)
                ((x10.array.Array<x10.lang.Place>)t48091).values());
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final x10.lang.Iterator<x10.lang.Place> t48093 =
              ((x10.lang.Iterator<x10.lang.Place>)
                ((x10.lang.Iterable<x10.lang.Place>)t48092).iterator());
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
return t48093;
        }
        
        
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
public int
                                                                                                          numPlaces$O(
                                                                                                          ){
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final x10.array.Array<x10.lang.Place> t48094 =
              ((x10.array.Array)(places));
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final int t48095 =
              ((x10.array.Array<x10.lang.Place>)t48094).
                size;
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
return t48095;
        }
        
        
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
public boolean
                                                                                                          contains$O(
                                                                                                          final int id){
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final x10.array.Array<x10.lang.Place> t48148 =
              ((x10.array.Array)(places));
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final x10.array.Region p48149 =
              ((x10.array.Region)(((x10.array.Array<x10.lang.Place>)t48148).
                                    region));
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final int i48029min48150 =
              p48149.min$O((int)(0));
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final int i48029max48151 =
              p48149.max$O((int)(0));
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
int i48145 =
              i48029min48150;
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
for (;
                                                                                                                 true;
                                                                                                                 ) {
                
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final int t48146 =
                  i48145;
                
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final boolean t48147 =
                  ((t48146) <= (((int)(i48029max48151))));
                
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
if (!(t48147)) {
                    
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
break;
                }
                
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final int i48142 =
                  i48145;
                
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final x10.array.Array<x10.lang.Place> t48138 =
                  ((x10.array.Array)(places));
                
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final x10.lang.Place t48139 =
                  ((x10.array.Array<x10.lang.Place>)t48138).$apply$G((int)(i48142));
                
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final int t48140 =
                  t48139.
                    id;
                
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final boolean t48141 =
                  ((int) t48140) ==
                ((int) id);
                
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
if (t48141) {
                    
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
return true;
                }
                
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final int t48143 =
                  i48145;
                
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final int t48144 =
                  ((t48143) + (((int)(1))));
                
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
i48145 = t48144;
            }
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
return false;
        }
        
        
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
public int
                                                                                                          indexOf$O(
                                                                                                          final int id){
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final x10.array.Array<x10.lang.Place> t48162 =
              ((x10.array.Array)(places));
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final x10.array.Region p48163 =
              ((x10.array.Region)(((x10.array.Array<x10.lang.Place>)t48162).
                                    region));
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final int i48048min48164 =
              p48163.min$O((int)(0));
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final int i48048max48165 =
              p48163.max$O((int)(0));
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
int i48159 =
              i48048min48164;
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
for (;
                                                                                                                 true;
                                                                                                                 ) {
                
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final int t48160 =
                  i48159;
                
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final boolean t48161 =
                  ((t48160) <= (((int)(i48048max48165))));
                
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
if (!(t48161)) {
                    
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
break;
                }
                
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final int i48156 =
                  i48159;
                
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final x10.array.Array<x10.lang.Place> t48152 =
                  ((x10.array.Array)(places));
                
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final x10.lang.Place t48153 =
                  ((x10.array.Array<x10.lang.Place>)t48152).$apply$G((int)(i48156));
                
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final int t48154 =
                  t48153.
                    id;
                
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final boolean t48155 =
                  ((int) t48154) ==
                ((int) id);
                
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
if (t48155) {
                    
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
return i48156;
                }
                
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final int t48157 =
                  i48159;
                
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final int t48158 =
                  ((t48157) + (((int)(1))));
                
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
i48159 = t48158;
            }
            
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
return -1;
        }
        
        
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final public x10.array.SparsePlaceGroup
                                                                                                          x10$array$SparsePlaceGroup$$x10$array$SparsePlaceGroup$this(
                                                                                                          ){
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
return x10.array.SparsePlaceGroup.this;
        }
        
        @x10.core.X10Generated public static class $Closure$83 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$83.class);
            
            public static final x10.rtt.RuntimeType<$Closure$83> $RTT = x10.rtt.StaticFunType.<$Closure$83> make(
            /* base class */$Closure$83.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.lang.Place.$RTT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$83 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$83.class + " calling"); } 
                x10.lang.Sequence ps = (x10.lang.Sequence) $deserializer.readRef();
                $_obj.ps = ps;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$83 $_obj = new $Closure$83((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (ps instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.ps);
                } else {
                $serializer.write(this.ps);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$83(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return $apply(x10.core.Int.$unbox(a1));
            }
            
                
                public x10.lang.Place
                  $apply(
                  final int i){
                    
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
final x10.lang.Place t48065 =
                      ((x10.lang.Sequence<x10.lang.Place>)this.
                                                            ps).$apply$G((int)(i));
                    
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/SparsePlaceGroup.x10"
return t48065;
                }
                
                public x10.lang.Sequence<x10.lang.Place> ps;
                
                public $Closure$83(final x10.lang.Sequence<x10.lang.Place> ps, __0$1x10$lang$Place$2 $dummy) { {
                                                                                                                      this.ps = ((x10.lang.Sequence)(ps));
                                                                                                                  }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$lang$Place$2 {}
                
            }
            
        // synthetic type for parameter mangling
        public abstract static class __0$1x10$lang$Place$2 {}
        
    }
    