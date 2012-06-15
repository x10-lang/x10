package x10.array;


@x10.core.X10Generated final public class ConstantDist extends x10.array.Dist implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, ConstantDist.class);
    
    public static final x10.rtt.RuntimeType<ConstantDist> $RTT = x10.rtt.NamedType.<ConstantDist> make(
    "x10.array.ConstantDist", /* base class */ConstantDist.class
    , /* parents */ new x10.rtt.Type[] {x10.array.Dist.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(ConstantDist $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + ConstantDist.class + " calling"); } 
        x10.array.Dist.$_deserialize_body($_obj, $deserializer);
        x10.lang.Place onePlace = (x10.lang.Place) $deserializer.readRef();
        $_obj.onePlace = onePlace;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        ConstantDist $_obj = new ConstantDist((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        if (onePlace instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.onePlace);
        } else {
        $serializer.write(this.onePlace);
        }
        
    }
    
    // constructor just for allocation
    public ConstantDist(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
public x10.lang.Place onePlace;
        
        
        
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
// creation method for java code (1-phase java constructor)
        public ConstantDist(final x10.array.Region r,
                            final x10.lang.Place p){this((java.lang.System[]) null);
                                                        $init(r,p);}
        
        // constructor for non-virtual call
        final public x10.array.ConstantDist x10$array$ConstantDist$$init$S(final x10.array.Region r,
                                                                           final x10.lang.Place p) { {
                                                                                                            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
super.$init(((x10.array.Region)(r)));
                                                                                                            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
this.onePlace = p;
                                                                                                            
                                                                                                        }
                                                                                                        return this;
                                                                                                        }
        
        // constructor
        public x10.array.ConstantDist $init(final x10.array.Region r,
                                            final x10.lang.Place p){return x10$array$ConstantDist$$init$S(r,p);}
        
        
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
public x10.array.PlaceGroup
                                                                                                      places(
                                                                                                      ){
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.lang.Place t35235 =
              ((x10.lang.Place)(onePlace));
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.array.SparsePlaceGroup t35236 =
              ((x10.array.SparsePlaceGroup)(new x10.array.SparsePlaceGroup((java.lang.System[]) null).$init(((x10.lang.Place)(t35235)))));
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
return t35236;
        }
        
        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
public int
                                                                                                      numPlaces$O(
                                                                                                      ){
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
return 1;
        }
        
        
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
public x10.lang.Sequence<x10.array.Region>
                                                                                                      regions(
                                                                                                      ){
            
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.array.Region t35237 =
              ((x10.array.Region)(region));
            
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.array.Array<x10.array.Region> t35238 =
              ((x10.array.Array)(new x10.array.Array<x10.array.Region>((java.lang.System[]) null, x10.array.Region.$RTT).$init(((int)(1)),
                                                                                                                               ((x10.array.Region)(t35237)), (x10.array.Array.__1x10$array$Array$$T) null)));
            
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.lang.Sequence<x10.array.Region> t35239 =
              ((x10.lang.Sequence<x10.array.Region>)
                ((x10.array.Array<x10.array.Region>)t35238).sequence());
            
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
return t35239;
        }
        
        
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
public x10.array.Region
                                                                                                      get(
                                                                                                      final x10.lang.Place p){
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.lang.Place t35240 =
              ((x10.lang.Place)(onePlace));
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final boolean t35244 =
              x10.rtt.Equality.equalsequals((p),(t35240));
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35244) {
                
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.array.Region t35241 =
                  ((x10.array.Region)(region));
                
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
return t35241;
            } else {
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final int t35242 =
                  this.rank$O();
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.array.Region t35243 =
                  ((x10.array.Region)(x10.array.Region.makeEmpty((int)(t35242))));
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
return t35243;
            }
        }
        
        
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
public x10.array.Region
                                                                                                      $apply(
                                                                                                      final x10.lang.Place p){
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.array.Region t35245 =
              ((x10.array.Region)(this.get(((x10.lang.Place)(p)))));
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
return t35245;
        }
        
        
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
public x10.lang.Place
                                                                                                      $apply(
                                                                                                      final x10.array.Point pt){
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
boolean t35248 =
              true;
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35248) {
                
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.array.Region t35246 =
                  ((x10.array.Region)(region));
                
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final boolean t35247 =
                  t35246.contains$O(((x10.array.Point)(pt)));
                
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
t35248 = !(t35247);
            }
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final boolean t35249 =
              t35248;
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35249) {
                
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
x10.array.Dist.raiseBoundsError(((x10.array.Point)(pt)));
            }
            
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.lang.Place t35250 =
              ((x10.lang.Place)(onePlace));
            
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
return t35250;
        }
        
        
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
public x10.lang.Place
                                                                                                      $apply(
                                                                                                      final int i0){
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
boolean t35254 =
              true;
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35254) {
                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.array.Region t35252 =
                  ((x10.array.Region)(region));
                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final boolean t35253 =
                  t35252.contains$O((int)(i0));
                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
t35254 = !(t35253);
            }
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final boolean t35255 =
              t35254;
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35255) {
                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
x10.array.Dist.raiseBoundsError((int)(i0));
            }
            
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.lang.Place t35256 =
              ((x10.lang.Place)(onePlace));
            
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
return t35256;
        }
        
        
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
public x10.lang.Place
                                                                                                      $apply(
                                                                                                      final int i0,
                                                                                                      final int i1){
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
boolean t35260 =
              true;
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35260) {
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.array.Region t35258 =
                  ((x10.array.Region)(region));
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final boolean t35259 =
                  t35258.contains$O((int)(i0),
                                    (int)(i1));
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
t35260 = !(t35259);
            }
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final boolean t35261 =
              t35260;
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35261) {
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
x10.array.Dist.raiseBoundsError((int)(i0),
                                                                                                                                            (int)(i1));
            }
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.lang.Place t35262 =
              ((x10.lang.Place)(onePlace));
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
return t35262;
        }
        
        
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
public x10.lang.Place
                                                                                                      $apply(
                                                                                                      final int i0,
                                                                                                      final int i1,
                                                                                                      final int i2){
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
boolean t35266 =
              true;
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35266) {
                
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.array.Region t35264 =
                  ((x10.array.Region)(region));
                
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final boolean t35265 =
                  t35264.contains$O((int)(i0),
                                    (int)(i1),
                                    (int)(i2));
                
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
t35266 = !(t35265);
            }
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final boolean t35267 =
              t35266;
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35267) {
                
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
x10.array.Dist.raiseBoundsError((int)(i0),
                                                                                                                                            (int)(i1),
                                                                                                                                            (int)(i2));
            }
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.lang.Place t35268 =
              ((x10.lang.Place)(onePlace));
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
return t35268;
        }
        
        
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
public x10.lang.Place
                                                                                                      $apply(
                                                                                                      final int i0,
                                                                                                      final int i1,
                                                                                                      final int i2,
                                                                                                      final int i3){
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
boolean t35272 =
              true;
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35272) {
                
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.array.Region t35270 =
                  ((x10.array.Region)(region));
                
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final boolean t35271 =
                  t35270.contains$O((int)(i0),
                                    (int)(i1),
                                    (int)(i2),
                                    (int)(i3));
                
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
t35272 = !(t35271);
            }
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final boolean t35273 =
              t35272;
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35273) {
                
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
x10.array.Dist.raiseBoundsError((int)(i0),
                                                                                                                                            (int)(i1),
                                                                                                                                            (int)(i2),
                                                                                                                                            (int)(i3));
            }
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.lang.Place t35274 =
              ((x10.lang.Place)(onePlace));
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
return t35274;
        }
        
        
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
public int
                                                                                                      offset$O(
                                                                                                      final x10.array.Point pt){
            
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
boolean t35276 =
              true;
            
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35276) {
                
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.lang.Place t35275 =
                  ((x10.lang.Place)(onePlace));
                
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
t35276 = (!x10.rtt.Equality.equalsequals((x10.lang.Runtime.home()),(t35275)));
            }
            
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final boolean t35277 =
              t35276;
            
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35277) {
                
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
x10.array.Dist.raisePlaceError(((x10.array.Point)(pt)));
            }
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.array.Region t35278 =
              ((x10.array.Region)(region));
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final int offset =
              t35278.indexOf$O(((x10.array.Point)(pt)));
            
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
boolean t35279 =
              true;
            
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35279) {
                
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
t35279 = ((int) offset) ==
                ((int) -1);
            }
            
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final boolean t35280 =
              t35279;
            
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35280) {
                
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
x10.array.Dist.raiseBoundsError(((x10.array.Point)(pt)));
            }
            
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
return offset;
        }
        
        
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
public int
                                                                                                      offset$O(
                                                                                                      final int i0){
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
boolean t35283 =
              true;
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35283) {
                
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.lang.Place t35282 =
                  ((x10.lang.Place)(onePlace));
                
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
t35283 = (!x10.rtt.Equality.equalsequals((x10.lang.Runtime.home()),(t35282)));
            }
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final boolean t35284 =
              t35283;
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35284) {
                
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
x10.array.Dist.raisePlaceError((int)(i0));
            }
            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.array.Region t35285 =
              ((x10.array.Region)(region));
            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final int offset =
              t35285.indexOf$O((int)(i0));
            
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
boolean t35286 =
              true;
            
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35286) {
                
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
t35286 = ((int) offset) ==
                ((int) -1);
            }
            
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final boolean t35287 =
              t35286;
            
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35287) {
                
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
x10.array.Dist.raiseBoundsError((int)(i0));
            }
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
return offset;
        }
        
        
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
public int
                                                                                                      offset$O(
                                                                                                      final int i0,
                                                                                                      final int i1){
            
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
boolean t35290 =
              true;
            
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35290) {
                
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.lang.Place t35289 =
                  ((x10.lang.Place)(onePlace));
                
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
t35290 = (!x10.rtt.Equality.equalsequals((x10.lang.Runtime.home()),(t35289)));
            }
            
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final boolean t35291 =
              t35290;
            
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35291) {
                
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
x10.array.Dist.raisePlaceError((int)(i0),
                                                                                                                                           (int)(i1));
            }
            
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.array.Region t35292 =
              ((x10.array.Region)(region));
            
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final int offset =
              t35292.indexOf$O((int)(i0),
                               (int)(i1));
            
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
boolean t35293 =
              true;
            
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35293) {
                
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
t35293 = ((int) offset) ==
                ((int) -1);
            }
            
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final boolean t35294 =
              t35293;
            
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35294) {
                
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
x10.array.Dist.raiseBoundsError((int)(i0),
                                                                                                                                            (int)(i1));
            }
            
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
return offset;
        }
        
        
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
public int
                                                                                                       offset$O(
                                                                                                       final int i0,
                                                                                                       final int i1,
                                                                                                       final int i2){
            
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
boolean t35297 =
              true;
            
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35297) {
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.lang.Place t35296 =
                  ((x10.lang.Place)(onePlace));
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
t35297 = (!x10.rtt.Equality.equalsequals((x10.lang.Runtime.home()),(t35296)));
            }
            
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final boolean t35298 =
              t35297;
            
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35298) {
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
x10.array.Dist.raisePlaceError((int)(i0),
                                                                                                                                            (int)(i1),
                                                                                                                                            (int)(i2));
            }
            
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.array.Region t35299 =
              ((x10.array.Region)(region));
            
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final int offset =
              t35299.indexOf$O((int)(i0),
                               (int)(i1),
                               (int)(i2));
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
boolean t35300 =
              true;
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35300) {
                
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
t35300 = ((int) offset) ==
                ((int) -1);
            }
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final boolean t35301 =
              t35300;
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35301) {
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
x10.array.Dist.raiseBoundsError((int)(i0),
                                                                                                                                             (int)(i1),
                                                                                                                                             (int)(i2));
            }
            
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
return offset;
        }
        
        
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
public int
                                                                                                       offset$O(
                                                                                                       final int i0,
                                                                                                       final int i1,
                                                                                                       final int i2,
                                                                                                       final int i3){
            
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
boolean t35304 =
              true;
            
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35304) {
                
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.lang.Place t35303 =
                  ((x10.lang.Place)(onePlace));
                
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
t35304 = (!x10.rtt.Equality.equalsequals((x10.lang.Runtime.home()),(t35303)));
            }
            
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final boolean t35305 =
              t35304;
            
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35305) {
                
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
x10.array.Dist.raisePlaceError((int)(i0),
                                                                                                                                            (int)(i1),
                                                                                                                                            (int)(i2),
                                                                                                                                            (int)(i3));
            }
            
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.array.Region t35306 =
              ((x10.array.Region)(region));
            
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final int offset =
              t35306.indexOf$O((int)(i0),
                               (int)(i1),
                               (int)(i2),
                               (int)(i3));
            
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
boolean t35307 =
              true;
            
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35307) {
                
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
t35307 = ((int) offset) ==
                ((int) -1);
            }
            
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final boolean t35308 =
              t35307;
            
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35308) {
                
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
x10.array.Dist.raiseBoundsError((int)(i0),
                                                                                                                                             (int)(i1),
                                                                                                                                             (int)(i2),
                                                                                                                                             (int)(i3));
            }
            
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
return offset;
        }
        
        
//#line 121 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
public int
                                                                                                       maxOffset$O(
                                                                                                       ){
            
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.array.Region t35309 =
              ((x10.array.Region)(region));
            
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final int t35310 =
              t35309.size$O();
            
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
return t35310;
        }
        
        
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
public x10.array.Dist
                                                                                                       restriction(
                                                                                                       final x10.array.Region r){
            
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.array.WrappedDistRegionRestricted t35311 =
              ((x10.array.WrappedDistRegionRestricted)(new x10.array.WrappedDistRegionRestricted((java.lang.System[]) null).$init(((x10.array.Dist)(this)),
                                                                                                                                  ((x10.array.Region)(r)))));
            
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
return t35311;
        }
        
        
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
public x10.array.Dist
                                                                                                       restriction(
                                                                                                       final x10.lang.Place p){
            
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.array.WrappedDistPlaceRestricted t35312 =
              ((x10.array.WrappedDistPlaceRestricted)(new x10.array.WrappedDistPlaceRestricted((java.lang.System[]) null).$init(((x10.array.Dist)(this)),
                                                                                                                                ((x10.lang.Place)(p)))));
            
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
return t35312;
        }
        
        
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
public boolean
                                                                                                       equals(
                                                                                                       final java.lang.Object thatObj){
            
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final boolean t35313 =
              x10.array.ConstantDist.$RTT.isInstance(thatObj);
            
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final boolean t35314 =
              !(t35313);
            
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35314) {
                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
return false;
            }
            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.array.ConstantDist that =
              ((x10.array.ConstantDist)(x10.rtt.Types.<x10.array.ConstantDist> cast(thatObj,x10.array.ConstantDist.$RTT)));
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.lang.Place t35315 =
              ((x10.lang.Place)(this.
                                  onePlace));
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.lang.Place t35316 =
              ((x10.lang.Place)(that.
                                  onePlace));
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
boolean t35319 =
              t35315.equals$O(((x10.lang.Place)(t35316)));
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
if (t35319) {
                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.array.Region t35317 =
                  ((x10.array.Region)(this.
                                        region));
                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final x10.array.Region t35318 =
                  ((x10.array.Region)(that.
                                        region));
                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
t35319 = t35317.equals(((java.lang.Object)(t35318)));
            }
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final boolean t35320 =
              t35319;
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
return t35320;
        }
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
final public x10.array.ConstantDist
                                                                                                      x10$array$ConstantDist$$x10$array$ConstantDist$this(
                                                                                                      ){
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ConstantDist.x10"
return x10.array.ConstantDist.this;
        }
    
}
