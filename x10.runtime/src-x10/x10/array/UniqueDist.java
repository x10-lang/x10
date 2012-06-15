package x10.array;


@x10.core.X10Generated final public class UniqueDist extends x10.array.Dist implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, UniqueDist.class);
    
    public static final x10.rtt.RuntimeType<UniqueDist> $RTT = x10.rtt.NamedType.<UniqueDist> make(
    "x10.array.UniqueDist", /* base class */UniqueDist.class
    , /* parents */ new x10.rtt.Type[] {x10.array.Dist.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(UniqueDist $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + UniqueDist.class + " calling"); } 
        x10.array.Dist.$_deserialize_body($_obj, $deserializer);
        x10.array.PlaceGroup pg = (x10.array.PlaceGroup) $deserializer.readRef();
        $_obj.pg = pg;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        UniqueDist $_obj = new UniqueDist((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        if (pg instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.pg);
        } else {
        $serializer.write(this.pg);
        }
        
    }
    
    // constructor just for allocation
    public UniqueDist(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
/**
    * The place group for this distribution
    */
        public x10.array.PlaceGroup pg;
        
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
/**
     * Cached restricted region for the current place.
     */
        public transient x10.array.Region regionForHere;
        
        
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
// creation method for java code (1-phase java constructor)
        public UniqueDist(final x10.array.PlaceGroup g){this((java.lang.System[]) null);
                                                            $init(g);}
        
        // constructor for non-virtual call
        final public x10.array.UniqueDist x10$array$UniqueDist$$init$S(final x10.array.PlaceGroup g) { {
                                                                                                              
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final int t48322 =
                                                                                                                g.numPlaces$O();
                                                                                                              
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final int t48323 =
                                                                                                                ((t48322) - (((int)(1))));
                                                                                                              
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.lang.IntRange t48324 =
                                                                                                                ((x10.lang.IntRange)(new x10.lang.IntRange((java.lang.System[]) null).x10$lang$IntRange$$init$S(((int)(0)), ((int)(t48323)))));
                                                                                                              
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.Region t48325 =
                                                                                                                ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t48324)))));
                                                                                                              
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
super.$init(((x10.array.Region)(t48325)));
                                                                                                              
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"

                                                                                                              
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
this.__fieldInitializers48166();
                                                                                                              
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
this.pg = ((x10.array.PlaceGroup)(g));
                                                                                                          }
                                                                                                          return this;
                                                                                                          }
        
        // constructor
        public x10.array.UniqueDist $init(final x10.array.PlaceGroup g){return x10$array$UniqueDist$$init$S(g);}
        
        
        
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
// creation method for java code (1-phase java constructor)
        public UniqueDist(){this((java.lang.System[]) null);
                                $init();}
        
        // constructor for non-virtual call
        final public x10.array.UniqueDist x10$array$UniqueDist$$init$S() { {
                                                                                  
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.PlaceGroup.WorldPlaceGroup t48225 =
                                                                                    ((x10.array.PlaceGroup.WorldPlaceGroup)(x10.array.PlaceGroup.getInitialized$WORLD()));
                                                                                  
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
this.$init(((x10.array.PlaceGroup)(t48225)));
                                                                              }
                                                                              return this;
                                                                              }
        
        // constructor
        public x10.array.UniqueDist $init(){return x10$array$UniqueDist$$init$S();}
        
        
        
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
public x10.array.PlaceGroup
                                                                                                    places(
                                                                                                    ){
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.PlaceGroup t48226 =
              ((x10.array.PlaceGroup)(pg));
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
return t48226;
        }
        
        
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
public int
                                                                                                    numPlaces$O(
                                                                                                    ){
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.PlaceGroup t48227 =
              ((x10.array.PlaceGroup)(pg));
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final int t48228 =
              t48227.numPlaces$O();
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
return t48228;
        }
        
        
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
public x10.lang.Sequence<x10.array.Region>
                                                                                                    regions(
                                                                                                    ){
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.PlaceGroup t48229 =
              ((x10.array.PlaceGroup)(pg));
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final int t48240 =
              t48229.numPlaces$O();
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.array.Region> t48241 =
              ((x10.core.fun.Fun_0_1)(new x10.array.UniqueDist.$Closure$84(this)));
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.Array<x10.array.Region> t48242 =
              ((x10.array.Array)(new x10.array.Array<x10.array.Region>((java.lang.System[]) null, x10.array.Region.$RTT).$init(t48240,
                                                                                                                               ((x10.core.fun.Fun_0_1)(t48241)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.lang.Sequence<x10.array.Region> t48243 =
              ((x10.lang.Sequence<x10.array.Region>)
                ((x10.array.Array<x10.array.Region>)t48242).sequence());
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
return t48243;
        }
        
        
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
public x10.array.Region
                                                                                                    get(
                                                                                                    final x10.lang.Place p){
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final boolean t48269 =
              x10.rtt.Equality.equalsequals((p),(x10.lang.Runtime.home()));
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
if (t48269) {
                
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.Region t48244 =
                  ((x10.array.Region)(regionForHere));
                
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final boolean t48256 =
                  ((t48244) == (null));
                
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
if (t48256) {
                    
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.PlaceGroup t48245 =
                      ((x10.array.PlaceGroup)(pg));
                    
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final int idx =
                      t48245.indexOf$O(((x10.lang.Place)(x10.lang.Runtime.home())));
                    
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.lang.IntRange t48246 =
                      ((x10.lang.IntRange)(new x10.lang.IntRange((java.lang.System[]) null).x10$lang$IntRange$$init$S(((int)(idx)), ((int)(idx)))));
                    
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.Region t48247 =
                      ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t48246)))));
                    
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.Region __desugarer__var__38__48212 =
                      ((x10.array.Region)(((x10.array.Region)
                                            t48247)));
                    
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
x10.array.Region ret48213 =
                       null;
                    
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final int t48333 =
                      __desugarer__var__38__48212.
                        rank;
                    
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.Region t48334 =
                      ((x10.array.Region)(x10.array.UniqueDist.this.
                                            region));
                    
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final int t48335 =
                      t48334.
                        rank;
                    
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final boolean t48336 =
                      ((int) t48333) ==
                    ((int) t48335);
                    
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final boolean t48337 =
                      !(t48336);
                    
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
if (t48337) {
                        
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final boolean t48338 =
                          true;
                        
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
if (t48338) {
                            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.lang.FailedDynamicCheckException t48339 =
                              new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==this(:x10.array.UniqueDist).region.rank}");
                            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
throw t48339;
                        }
                    }
                    
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
ret48213 = ((x10.array.Region)(__desugarer__var__38__48212));
                    
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.Region t48255 =
                      ((x10.array.Region)(ret48213));
                    
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
this.regionForHere = ((x10.array.Region)(t48255));
                }
                
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.Region t48257 =
                  ((x10.array.Region)(regionForHere));
                
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
return t48257;
            } else {
                
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.PlaceGroup t48258 =
                  ((x10.array.PlaceGroup)(pg));
                
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final int idx =
                  t48258.indexOf$O(((x10.lang.Place)(p)));
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.lang.IntRange t48259 =
                  ((x10.lang.IntRange)(new x10.lang.IntRange((java.lang.System[]) null).x10$lang$IntRange$$init$S(((int)(idx)), ((int)(idx)))));
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.Region t48260 =
                  ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t48259)))));
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.Region __desugarer__var__39__48215 =
                  ((x10.array.Region)(((x10.array.Region)
                                        t48260)));
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
x10.array.Region ret48216 =
                   null;
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final int t48340 =
                  __desugarer__var__39__48215.
                    rank;
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.Region t48341 =
                  ((x10.array.Region)(x10.array.UniqueDist.this.
                                        region));
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final int t48342 =
                  t48341.
                    rank;
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final boolean t48343 =
                  ((int) t48340) ==
                ((int) t48342);
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final boolean t48344 =
                  !(t48343);
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
if (t48344) {
                    
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final boolean t48345 =
                      true;
                    
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
if (t48345) {
                        
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.lang.FailedDynamicCheckException t48346 =
                          new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==this(:x10.array.UniqueDist).region.rank}");
                        
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
throw t48346;
                    }
                }
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
ret48216 = ((x10.array.Region)(__desugarer__var__39__48215));
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.Region t48268 =
                  ((x10.array.Region)(ret48216));
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
return t48268;
            }
        }
        
        
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
public x10.array.Region
                                                                                                    $apply(
                                                                                                    final x10.lang.Place p){
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.Region t48270 =
              ((x10.array.Region)(this.get(((x10.lang.Place)(p)))));
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
return t48270;
        }
        
        
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
public x10.lang.Place
                                                                                                    $apply(
                                                                                                    final x10.array.Point pt){
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.PlaceGroup t48271 =
              ((x10.array.PlaceGroup)(pg));
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final int t48272 =
              pt.$apply$O((int)(0));
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.lang.Place t48273 =
              t48271.$apply((int)(t48272));
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
return t48273;
        }
        
        
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
public x10.lang.Place
                                                                                                    $apply(
                                                                                                    final int i0){
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.PlaceGroup t48275 =
              ((x10.array.PlaceGroup)(pg));
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.lang.Place t48276 =
              t48275.$apply((int)(i0));
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
return t48276;
        }
        
        
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
public int
                                                                                                    offset$O(
                                                                                                    final x10.array.Point pt){
            
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
boolean t48283 =
              true;
            
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
if (t48283) {
                
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final int t48277 =
                  pt.$apply$O((int)(0));
                
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
boolean t48281 =
                  ((t48277) >= (((int)(0))));
                
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
if (t48281) {
                    
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final int t48279 =
                      pt.$apply$O((int)(0));
                    
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.PlaceGroup t48278 =
                      ((x10.array.PlaceGroup)(pg));
                    
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final int t48280 =
                      t48278.numPlaces$O();
                    
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
t48281 = ((t48279) < (((int)(t48280))));
                }
                
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final boolean t48282 =
                  t48281;
                
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
t48283 = !(t48282);
            }
            
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final boolean t48284 =
              t48283;
            
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
if (t48284) {
                
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
x10.array.Dist.raiseBoundsError(((x10.array.Point)(pt)));
            }
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
boolean t48288 =
              true;
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
if (t48288) {
                
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final int t48286 =
                  pt.$apply$O((int)(0));
                
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.PlaceGroup t48285 =
                  ((x10.array.PlaceGroup)(pg));
                
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final int t48287 =
                  t48285.indexOf$O(((x10.lang.Place)(x10.lang.Runtime.home())));
                
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
t48288 = ((int) t48286) !=
                ((int) t48287);
            }
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final boolean t48289 =
              t48288;
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
if (t48289) {
                
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
x10.array.Dist.raisePlaceError(((x10.array.Point)(pt)));
            }
            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
return 0;
        }
        
        
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
public int
                                                                                                    offset$O(
                                                                                                    final int i0){
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
boolean t48294 =
              true;
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
if (t48294) {
                
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
boolean t48292 =
                  ((i0) >= (((int)(0))));
                
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
if (t48292) {
                    
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final int t48291 =
                      this.numPlaces$O();
                    
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
t48292 = ((i0) < (((int)(t48291))));
                }
                
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final boolean t48293 =
                  t48292;
                
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
t48294 = !(t48293);
            }
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final boolean t48295 =
              t48294;
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
if (t48295) {
                
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
x10.array.Dist.raiseBoundsError((int)(i0));
            }
            
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
boolean t48298 =
              true;
            
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
if (t48298) {
                
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.PlaceGroup t48296 =
                  ((x10.array.PlaceGroup)(pg));
                
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final int t48297 =
                  t48296.indexOf$O(((x10.lang.Place)(x10.lang.Runtime.home())));
                
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
t48298 = ((int) i0) !=
                ((int) t48297);
            }
            
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final boolean t48299 =
              t48298;
            
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
if (t48299) {
                
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
x10.array.Dist.raisePlaceError((int)(i0));
            }
            
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
return 0;
        }
        
        
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
public x10.lang.Place
                                                                                                     $apply(
                                                                                                     final int i0,
                                                                                                     final int i1){
            
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.lang.UnsupportedOperationException t48301 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(((java.lang.String)("operator(i0:int,i1:int)")))));
            
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
throw t48301;
        }
        
        
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
public x10.lang.Place
                                                                                                     $apply(
                                                                                                     final int i0,
                                                                                                     final int i1,
                                                                                                     final int i2){
            
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.lang.UnsupportedOperationException t48303 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(((java.lang.String)("operator(i0:int,i1:int,i2:int)")))));
            
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
throw t48303;
        }
        
        
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
public x10.lang.Place
                                                                                                     $apply(
                                                                                                     final int i0,
                                                                                                     final int i1,
                                                                                                     final int i2,
                                                                                                     final int i3){
            
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.lang.UnsupportedOperationException t48305 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(((java.lang.String)("operator(i0:int,i1:int,i2:int,i3:int)")))));
            
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
throw t48305;
        }
        
        
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
public int
                                                                                                     maxOffset$O(
                                                                                                     ){
            
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
return 0;
        }
        
        
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
public x10.array.Dist
                                                                                                     restriction(
                                                                                                     final x10.array.Region r){
            
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.WrappedDistRegionRestricted t48306 =
              ((x10.array.WrappedDistRegionRestricted)(new x10.array.WrappedDistRegionRestricted((java.lang.System[]) null).$init(((x10.array.Dist)(this)),
                                                                                                                                  ((x10.array.Region)(r)))));
            
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
return t48306;
        }
        
        
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
public x10.array.Dist
                                                                                                     restriction(
                                                                                                     final x10.lang.Place p){
            
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.WrappedDistPlaceRestricted t48307 =
              ((x10.array.WrappedDistPlaceRestricted)(new x10.array.WrappedDistPlaceRestricted((java.lang.System[]) null).$init(((x10.array.Dist)(this)),
                                                                                                                                ((x10.lang.Place)(p)))));
            
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.Dist __desugarer__var__40__48218 =
              ((x10.array.Dist)(((x10.array.Dist)
                                  t48307)));
            
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
x10.array.Dist ret48219 =
               null;
            
//#line 123 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.Region t48347 =
              ((x10.array.Region)(__desugarer__var__40__48218.
                                    region));
            
//#line 123 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final int t48348 =
              t48347.
                rank;
            
//#line 123 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.Region t48349 =
              ((x10.array.Region)(x10.array.UniqueDist.this.
                                    region));
            
//#line 123 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final int t48350 =
              t48349.
                rank;
            
//#line 123 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final boolean t48351 =
              ((int) t48348) ==
            ((int) t48350);
            
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final boolean t48352 =
              !(t48351);
            
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
if (t48352) {
                
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final boolean t48353 =
                  true;
                
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
if (t48353) {
                    
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.lang.FailedDynamicCheckException t48354 =
                      new x10.lang.FailedDynamicCheckException("x10.array.Dist{self.region.rank==this(:x10.array.UniqueDist).region.rank}");
                    
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
throw t48354;
                }
            }
            
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
ret48219 = ((x10.array.Dist)(__desugarer__var__40__48218));
            
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.Dist t48316 =
              ((x10.array.Dist)(ret48219));
            
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
return t48316;
        }
        
        
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
public boolean
                                                                                                     equals(
                                                                                                     final java.lang.Object thatObj){
            
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final boolean t48321 =
              x10.array.UniqueDist.$RTT.isInstance(thatObj);
            
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
if (t48321) {
                
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.UniqueDist that =
                  ((x10.array.UniqueDist)(x10.rtt.Types.<x10.array.UniqueDist> cast(thatObj,x10.array.UniqueDist.$RTT)));
                
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.PlaceGroup t48317 =
                  ((x10.array.PlaceGroup)(pg));
                
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.PlaceGroup t48318 =
                  ((x10.array.PlaceGroup)(that.
                                            pg));
                
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final boolean t48319 =
                  t48317.equals(((java.lang.Object)(t48318)));
                
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
return t48319;
            } else {
                
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final boolean t48320 =
                  super.equals(((java.lang.Object)(thatObj)));
                
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
return t48320;
            }
        }
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final public x10.array.UniqueDist
                                                                                                    x10$array$UniqueDist$$x10$array$UniqueDist$this(
                                                                                                    ){
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
return x10.array.UniqueDist.this;
        }
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final public void
                                                                                                    __fieldInitializers48166(
                                                                                                    ){
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
this.regionForHere = null;
        }
        
        @x10.core.X10Generated public static class $Closure$84 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$84.class);
            
            public static final x10.rtt.RuntimeType<$Closure$84> $RTT = x10.rtt.StaticFunType.<$Closure$84> make(
            /* base class */$Closure$84.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.array.Region.$RTT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$84 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$84.class + " calling"); } 
                x10.array.UniqueDist out$$ = (x10.array.UniqueDist) $deserializer.readRef();
                $_obj.out$$ = out$$;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$84 $_obj = new $Closure$84((java.lang.System[]) null);
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
                
            }
            
            // constructor just for allocation
            public $Closure$84(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return $apply(x10.core.Int.$unbox(a1));
            }
            
                
                public x10.array.Region
                  $apply(
                  final int i){
                    
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.lang.IntRange t48230 =
                      ((x10.lang.IntRange)(new x10.lang.IntRange((java.lang.System[]) null).x10$lang$IntRange$$init$S(((int)(i)), ((int)(i)))));
                    
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.Region t48231 =
                      ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t48230)))));
                    
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.Region __desugarer__var__37__48209 =
                      ((x10.array.Region)(((x10.array.Region)
                                            t48231)));
                    
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
x10.array.Region ret48210 =
                       null;
                    
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final int t48326 =
                      __desugarer__var__37__48209.
                        rank;
                    
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.Region t48327 =
                      ((x10.array.Region)(this.
                                            out$$.
                                            region));
                    
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final int t48328 =
                      t48327.
                        rank;
                    
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final boolean t48329 =
                      ((int) t48326) ==
                    ((int) t48328);
                    
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final boolean t48330 =
                      !(t48329);
                    
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
if (t48330) {
                        
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final boolean t48331 =
                          true;
                        
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
if (t48331) {
                            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.lang.FailedDynamicCheckException t48332 =
                              new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==this(:x10.array.UniqueDist).region.rank}");
                            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
throw t48332;
                        }
                    }
                    
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
ret48210 = ((x10.array.Region)(__desugarer__var__37__48209));
                    
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
final x10.array.Region t48239 =
                      ((x10.array.Region)(ret48210));
                    
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UniqueDist.x10"
return t48239;
                }
                
                public x10.array.UniqueDist out$$;
                
                public $Closure$84(final x10.array.UniqueDist out$$) { {
                                                                              this.out$$ = out$$;
                                                                          }}
                
            }
            
        
        public boolean
          x10$array$Dist$equals$S$O(
          final java.lang.Object a0){
            return super.equals(((java.lang.Object)(a0)));
        }
        
    }
    