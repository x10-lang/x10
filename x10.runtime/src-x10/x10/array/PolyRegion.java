package x10.array;


@x10.core.X10Generated public class PolyRegion extends x10.array.Region implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, PolyRegion.class);
    
    public static final x10.rtt.RuntimeType<PolyRegion> $RTT = x10.rtt.NamedType.<PolyRegion> make(
    "x10.array.PolyRegion", /* base class */PolyRegion.class
    , /* parents */ new x10.rtt.Type[] {x10.array.Region.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(PolyRegion $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + PolyRegion.class + " calling"); } 
        x10.array.Region.$_deserialize_body($_obj, $deserializer);
        x10.array.PolyMat mat = (x10.array.PolyMat) $deserializer.readRef();
        $_obj.mat = mat;
        $_obj.size = $deserializer.readInt();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        PolyRegion $_obj = new PolyRegion((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        if (mat instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.mat);
        } else {
        $serializer.write(this.mat);
        }
        $serializer.write(this.size);
        
    }
    
    // constructor just for allocation
    public PolyRegion(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
public x10.array.PolyMat mat;
        
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
public boolean
                                                                                                    isConvex$O(
                                                                                                    ){
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
return true;
        }
        
        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
public int size;
        
        
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
public int
                                                                                                    size$O(
                                                                                                    ){
            
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t41830 =
              size;
            
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t41837 =
              ((t41830) < (((int)(0))));
            
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
if (t41837) {
                
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
int s =
                  0;
                
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.lang.Iterator<x10.array.Point> it =
                  this.iterator();
                
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.lang.Iterator<x10.array.Point> p42065 =
                  this.iterator();
                
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
for (;
                                                                                                               true;
                                                                                                               ) {
                    
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t42066 =
                      ((x10.lang.Iterator<x10.array.Point>)p42065).hasNext$O();
                    
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
if (!(t42066)) {
                        
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
break;
                    }
                    
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.Point t42061 =
                      ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)p42065).next$G()));
                    
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.Point p42062 =
                      ((x10.array.Point)
                        t42061);
                    
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42063 =
                      s;
                    
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42064 =
                      ((t42063) + (((int)(1))));
                    
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
s = t42064;
                }
                
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t41836 =
                  s;
                
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
this.size = t41836;
            }
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t41838 =
              size;
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
return t41838;
        }
        
        
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
public int
                                                                                                    indexOf$O(
                                                                                                    final x10.array.Point id$59){
            
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.lang.UnsupportedOperationException t41839 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
            
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
throw t41839;
        }
        
        
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
public x10.lang.Iterator<x10.array.Point>
                                                                                                    iterator(
                                                                                                    ){
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat t41840 =
              ((x10.array.PolyMat)(mat));
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyScanner t41841 =
              ((x10.array.PolyScanner)(x10.array.PolyScanner.make(((x10.array.PolyMat)(t41840)))));
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.lang.Iterator<x10.array.Point> t41842 =
              t41841.iterator();
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
return t41842;
        }
        
        
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
public x10.array.Region
                                                                                                    intersection(
                                                                                                    final x10.array.Region t){
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t41870 =
              x10.array.PolyRegion.$RTT.isInstance(t);
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
if (t41870) {
                
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyRegion that =
                  ((x10.array.PolyRegion)(x10.rtt.Types.<x10.array.PolyRegion> cast(t,x10.array.PolyRegion.$RTT)));
                
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t41843 =
                  rank;
                
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMatBuilder pmb =
                  ((x10.array.PolyMatBuilder)(new x10.array.PolyMatBuilder((java.lang.System[]) null).$init(((int)(t41843)))));
                
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat t42069 =
                  ((x10.array.PolyMat)(this.
                                         mat));
                
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.lang.Iterator<x10.array.PolyRow> r42070 =
                  ((x10.array.Mat<x10.array.PolyRow>)t42069).iterator();
                
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
for (;
                                                                                                               true;
                                                                                                               ) {
                    
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t42071 =
                      ((x10.lang.Iterator<x10.array.PolyRow>)r42070).hasNext$O();
                    
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
if (!(t42071)) {
                        
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
break;
                    }
                    
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyRow r42067 =
                      ((x10.lang.Iterator<x10.array.PolyRow>)r42070).next$G();
                    
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
pmb.add(((x10.array.Row)(r42067)));
                }
                
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat t42072 =
                  ((x10.array.PolyMat)(that.
                                         mat));
                
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.lang.Iterator<x10.array.PolyRow> r42073 =
                  ((x10.array.Mat<x10.array.PolyRow>)t42072).iterator();
                
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
for (;
                                                                                                               true;
                                                                                                               ) {
                    
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t42074 =
                      ((x10.lang.Iterator<x10.array.PolyRow>)r42073).hasNext$O();
                    
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
if (!(t42074)) {
                        
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
break;
                    }
                    
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyRow r42068 =
                      ((x10.lang.Iterator<x10.array.PolyRow>)r42073).next$G();
                    
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
pmb.add(((x10.array.Row)(r42068)));
                }
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat pm =
                  ((x10.array.PolyMat)(pmb.toSortedPolyMat((boolean)(false))));
                
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.Region t41850 =
                  ((x10.array.Region)(x10.array.PolyRegion.make(((x10.array.PolyMat)(pm)))));
                
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
return t41850;
            } else {
                
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t41869 =
                  x10.array.RectRegion.$RTT.isInstance(t);
                
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
if (t41869) {
                    
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.RectRegion t41851 =
                      ((x10.array.RectRegion)(x10.rtt.Types.<x10.array.RectRegion> cast(t,x10.array.RectRegion.$RTT)));
                    
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.Region t41852 =
                      ((x10.array.Region)(t41851.toPolyRegion()));
                    
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.Region t41853 =
                      ((x10.array.Region)(this.intersection(((x10.array.Region)(t41852)))));
                    
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
return t41853;
                } else {
                    
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t41868 =
                      x10.array.RectRegion1D.$RTT.isInstance(t);
                    
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
if (t41868) {
                        
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.RectRegion1D t41854 =
                          ((x10.array.RectRegion1D)(x10.rtt.Types.<x10.array.RectRegion1D> cast(t,x10.array.RectRegion1D.$RTT)));
                        
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.RectRegion t41855 =
                          ((x10.array.RectRegion)(t41854.toRectRegion()));
                        
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.Region t41856 =
                          ((x10.array.Region)(t41855.toPolyRegion()));
                        
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.Region __desugarer__var__22__41824 =
                          ((x10.array.Region)(((x10.array.Region)
                                                t41856)));
                        
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
x10.array.Region ret41825 =
                           null;
                        
//#line 82 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42075 =
                          __desugarer__var__22__41824.
                            rank;
                        
//#line 82 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42076 =
                          x10.array.PolyRegion.this.
                            rank;
                        
//#line 82 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t42077 =
                          ((int) t42075) ==
                        ((int) t42076);
                        
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t42078 =
                          !(t42077);
                        
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
if (t42078) {
                            
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t42079 =
                              true;
                            
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
if (t42079) {
                                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.lang.FailedDynamicCheckException t42080 =
                                  new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==this(:x10.array.PolyRegion).rank}");
                                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
throw t42080;
                            }
                        }
                        
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
ret41825 = ((x10.array.Region)(__desugarer__var__22__41824));
                        
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.Region t41863 =
                          ((x10.array.Region)(ret41825));
                        
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.Region t41864 =
                          ((x10.array.Region)(this.intersection(((x10.array.Region)(t41863)))));
                        
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
return t41864;
                    } else {
                        
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final java.lang.String t41865 =
                          (("intersection(") + (t));
                        
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final java.lang.String t41866 =
                          ((t41865) + (")"));
                        
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.lang.UnsupportedOperationException t41867 =
                          ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(t41866)));
                        
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
throw t41867;
                    }
                }
            }
        }
        
        
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
public boolean
                                                                                                    contains$O(
                                                                                                    final x10.array.Region that){
            
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.Region t41871 =
              ((x10.array.Region)(this.computeBoundingBox()));
            
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.Region t41872 =
              ((x10.array.Region)(that.computeBoundingBox()));
            
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t41873 =
              t41871.contains$O(((x10.array.Region)(t41872)));
            
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
return t41873;
        }
        
        
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
public x10.array.Region
                                                                                                     projection(
                                                                                                     final int axis){
            
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
x10.array.PolyMat pm =
              ((x10.array.PolyMat)(mat));
            
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
int k42088 =
              0;
            
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
for (;
                                                                                                            true;
                                                                                                            ) {
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42089 =
                  k42088;
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42090 =
                  rank;
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t42091 =
                  ((t42089) < (((int)(t42090))));
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
if (!(t42091)) {
                    
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
break;
                }
                
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42081 =
                  k42088;
                
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t42082 =
                  ((int) t42081) !=
                ((int) axis);
                
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
if (t42082) {
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat t42083 =
                      ((x10.array.PolyMat)(pm));
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42084 =
                      k42088;
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat t42085 =
                      ((x10.array.PolyMat)(t42083.eliminate((int)(t42084),
                                                            (boolean)(true))));
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
pm = ((x10.array.PolyMat)(t42085));
                }
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42086 =
                  k42088;
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42087 =
                  ((t42086) + (((int)(1))));
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
k42088 = t42087;
            }
            
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat t41885 =
              ((x10.array.PolyMat)(pm));
            
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t41887 =
              t41885.rectMin$O((int)(axis));
            
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat t41886 =
              ((x10.array.PolyMat)(pm));
            
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t41888 =
              t41886.rectMax$O((int)(axis));
            
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.Region t41889 =
              ((x10.array.Region)(x10.array.Region.makeRectangular((int)(t41887),
                                                                   (int)(t41888))));
            
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
return t41889;
        }
        
        
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
public x10.array.Region
                                                                                                     eliminate(
                                                                                                     final int axis){
            
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat t41890 =
              ((x10.array.PolyMat)(mat));
            
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat pm =
              ((x10.array.PolyMat)(t41890.eliminate((int)(axis),
                                                    (boolean)(true))));
            
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.Region result =
              ((x10.array.Region)(x10.array.PolyRegion.make(((x10.array.PolyMat)(pm)))));
            
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
return result;
        }
        
        
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
public x10.array.Region
                                                                                                     product(
                                                                                                     final x10.array.Region r){
            
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t41891 =
              x10.array.PolyRegion.$RTT.isInstance(r);
            
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t41895 =
              !(t41891);
            
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
if (t41895) {
                
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final java.lang.String t41892 =
                  (("product(") + (r));
                
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final java.lang.String t41893 =
                  ((t41892) + (")"));
                
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.lang.UnsupportedOperationException t41894 =
                  ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(t41893)));
                
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
throw t41894;
            }
            
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyRegion that =
              ((x10.array.PolyRegion)(x10.rtt.Types.<x10.array.PolyRegion> cast(r,x10.array.PolyRegion.$RTT)));
            
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t41896 =
              this.
                rank;
            
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t41897 =
              that.
                rank;
            
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t41898 =
              ((t41896) + (((int)(t41897))));
            
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMatBuilder pmb =
              ((x10.array.PolyMatBuilder)(new x10.array.PolyMatBuilder((java.lang.System[]) null).$init(t41898)));
            
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat t41899 =
              ((x10.array.PolyMat)(this.
                                     mat));
            
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
x10.array.PolyRegion.copy(((x10.array.PolyMatBuilder)(pmb)),
                                                                                                                                 ((x10.array.PolyMat)(t41899)),
                                                                                                                                 (int)(0));
            
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat t41900 =
              ((x10.array.PolyMat)(that.
                                     mat));
            
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t41901 =
              this.
                rank;
            
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
x10.array.PolyRegion.copy(((x10.array.PolyMatBuilder)(pmb)),
                                                                                                                                 ((x10.array.PolyMat)(t41900)),
                                                                                                                                 (int)(t41901));
            
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat pm =
              ((x10.array.PolyMat)(pmb.toSortedPolyMat((boolean)(false))));
            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.Region t41902 =
              ((x10.array.Region)(x10.array.PolyRegion.make(((x10.array.PolyMat)(pm)))));
            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
return t41902;
        }
        
        
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
private static void
                                                                                                     copy(
                                                                                                     final x10.array.PolyMatBuilder tt,
                                                                                                     final x10.array.PolyMat ff,
                                                                                                     final int offset){
            
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.lang.Iterator<x10.array.PolyRow> r41819 =
              ((x10.array.Mat<x10.array.PolyRow>)ff).iterator();
            
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
for (;
                                                                                                            true;
                                                                                                            ) {
                
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t41920 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)r41819).hasNext$O();
                
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
if (!(t41920)) {
                    
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
break;
                }
                
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyRow r42102 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)r41819).next$G();
                
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyRow f42103 =
                  ((x10.array.PolyRow)(r42102));
                
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42104 =
                  tt.
                    rank;
                
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42105 =
                  ((t42104) + (((int)(1))));
                
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.Array<x10.core.Int> t42106 =
                  ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(t42105)));
                
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
int i42098 =
                  0;
                
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
for (;
                                                                                                                true;
                                                                                                                ) {
                    
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42099 =
                      i42098;
                    
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42100 =
                      ff.
                        rank;
                    
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t42101 =
                      ((t42099) < (((int)(t42100))));
                    
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
if (!(t42101)) {
                        
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
break;
                    }
                    
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42092 =
                      i42098;
                    
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42093 =
                      ((offset) + (((int)(t42092))));
                    
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42094 =
                      i42098;
                    
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42095 =
                      f42103.$apply$O((int)(t42094));
                    
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
((x10.array.Array<x10.core.Int>)t42106).$set__1x10$array$Array$$T$G((int)(t42093),
                                                                                                                                                                                   x10.core.Int.$box(t42095));
                    
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42096 =
                      i42098;
                    
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42097 =
                      ((t42096) + (((int)(1))));
                    
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
i42098 = t42097;
                }
                
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42107 =
                  tt.
                    rank;
                
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42108 =
                  ff.
                    rank;
                
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42109 =
                  f42103.$apply$O((int)(t42108));
                
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
((x10.array.Array<x10.core.Int>)t42106).$set__1x10$array$Array$$T$G((int)(t42107),
                                                                                                                                                                               x10.core.Int.$box(t42109));
                
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyRow t42110 =
                  ((x10.array.PolyRow)(new x10.array.PolyRow((java.lang.System[]) null).$init(((x10.array.Array)(t42106)), (x10.array.PolyRow.__0$1x10$lang$Int$2) null)));
                
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
tt.add(((x10.array.Row)(t42110)));
            }
        }
        
        public static void
          copy$P(
          final x10.array.PolyMatBuilder tt,
          final x10.array.PolyMat ff,
          final int offset){
            x10.array.PolyRegion.copy(((x10.array.PolyMatBuilder)(tt)),
                                      ((x10.array.PolyMat)(ff)),
                                      (int)(offset));
        }
        
        
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
public x10.array.Region
                                                                                                     translate(
                                                                                                     final x10.array.Point v){
            
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t41921 =
              this.
                rank;
            
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMatBuilder pmb =
              ((x10.array.PolyMatBuilder)(new x10.array.PolyMatBuilder((java.lang.System[]) null).$init(((int)(t41921)))));
            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat t41922 =
              ((x10.array.PolyMat)(this.
                                     mat));
            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
x10.array.PolyRegion.translate(((x10.array.PolyMatBuilder)(pmb)),
                                                                                                                                      ((x10.array.PolyMat)(t41922)),
                                                                                                                                      ((x10.array.Point)(v)));
            
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat pm =
              ((x10.array.PolyMat)(pmb.toSortedPolyMat((boolean)(false))));
            
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.Region t41923 =
              ((x10.array.Region)(x10.array.PolyRegion.make(((x10.array.PolyMat)(pm)))));
            
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
return t41923;
        }
        
        
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
private static void
                                                                                                     translate(
                                                                                                     final x10.array.PolyMatBuilder tt,
                                                                                                     final x10.array.PolyMat ff,
                                                                                                     final x10.array.Point v){
            
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.lang.Iterator<x10.array.PolyRow> r41821 =
              ((x10.array.Mat<x10.array.PolyRow>)ff).iterator();
            
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
for (;
                                                                                                            true;
                                                                                                            ) {
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t41949 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)r41821).hasNext$O();
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
if (!(t41949)) {
                    
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
break;
                }
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyRow r42127 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)r41821).next$G();
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyRow f42128 =
                  ((x10.array.PolyRow)(r42127));
                
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42129 =
                  ff.
                    rank;
                
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42130 =
                  ((t42129) + (((int)(1))));
                
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.Array<x10.core.Int> t42131 =
                  ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(t42130)));
                
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
int s42132 =
                  0;
                
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
int i42123 =
                  0;
                
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
for (;
                                                                                                                true;
                                                                                                                ) {
                    
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42124 =
                      i42123;
                    
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42125 =
                      ff.
                        rank;
                    
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t42126 =
                      ((t42124) < (((int)(t42125))));
                    
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
if (!(t42126)) {
                        
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
break;
                    }
                    
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42111 =
                      i42123;
                    
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42112 =
                      i42123;
                    
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42113 =
                      f42128.$apply$O((int)(t42112));
                    
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
((x10.array.Array<x10.core.Int>)t42131).$set__1x10$array$Array$$T$G((int)(t42111),
                                                                                                                                                                                   x10.core.Int.$box(t42113));
                    
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42114 =
                      s42132;
                    
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42115 =
                      i42123;
                    
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42116 =
                      f42128.$apply$O((int)(t42115));
                    
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42117 =
                      i42123;
                    
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42118 =
                      v.$apply$O((int)(t42117));
                    
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42119 =
                      ((t42116) * (((int)(t42118))));
                    
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42120 =
                      ((t42114) + (((int)(t42119))));
                    
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
s42132 = t42120;
                    
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42121 =
                      i42123;
                    
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42122 =
                      ((t42121) + (((int)(1))));
                    
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
i42123 = t42122;
                }
                
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42133 =
                  ff.
                    rank;
                
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42134 =
                  ff.
                    rank;
                
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42135 =
                  f42128.$apply$O((int)(t42134));
                
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42136 =
                  s42132;
                
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42137 =
                  ((t42135) - (((int)(t42136))));
                
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
((x10.array.Array<x10.core.Int>)t42131).$set__1x10$array$Array$$T$G((int)(t42133),
                                                                                                                                                                               x10.core.Int.$box(t42137));
                
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyRow t42138 =
                  ((x10.array.PolyRow)(new x10.array.PolyRow((java.lang.System[]) null).$init(((x10.array.Array)(t42131)), (x10.array.PolyRow.__0$1x10$lang$Int$2) null)));
                
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
tt.add(((x10.array.Row)(t42138)));
            }
        }
        
        public static void
          translate$P(
          final x10.array.PolyMatBuilder tt,
          final x10.array.PolyMat ff,
          final x10.array.Point v){
            x10.array.PolyRegion.translate(((x10.array.PolyMatBuilder)(tt)),
                                           ((x10.array.PolyMat)(ff)),
                                           ((x10.array.Point)(v)));
        }
        
        
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
public boolean
                                                                                                     isEmpty$O(
                                                                                                     ){
            
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat t41950 =
              ((x10.array.PolyMat)(mat));
            
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean tmp =
              t41950.isEmpty$O();
            
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
return tmp;
        }
        
        
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
public x10.array.Region
                                                                                                     computeBoundingBox(
                                                                                                     ){
            
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t41951 =
              rank;
            
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.Array<x10.core.Int> min =
              ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t41951)))));
            
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t41952 =
              rank;
            
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.Array<x10.core.Int> max =
              ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t41952)))));
            
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
x10.array.PolyMat pm =
              ((x10.array.PolyMat)(mat));
            
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
int axis42163 =
              0;
            
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
for (;
                                                                                                            true;
                                                                                                            ) {
                
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42164 =
                  axis42163;
                
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42165 =
                  rank;
                
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t42166 =
                  ((t42164) < (((int)(t42165))));
                
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
if (!(t42166)) {
                    
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
break;
                }
                
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
x10.array.PolyMat x42149 =
                  pm;
                
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42144 =
                  axis42163;
                
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
int k42145 =
                  ((t42144) + (((int)(1))));
                
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
for (;
                                                                                                                true;
                                                                                                                ) {
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42146 =
                      k42145;
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42147 =
                      rank;
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t42148 =
                      ((t42146) < (((int)(t42147))));
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
if (!(t42148)) {
                        
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
break;
                    }
                    
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat t42139 =
                      x42149;
                    
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42140 =
                      k42145;
                    
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat t42141 =
                      ((x10.array.PolyMat)(t42139.eliminate((int)(t42140),
                                                            (boolean)(true))));
                    
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
x42149 = t42141;
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42142 =
                      k42145;
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42143 =
                      ((t42142) + (((int)(1))));
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
k42145 = t42143;
                }
                
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42150 =
                  axis42163;
                
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat t42151 =
                  x42149;
                
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42152 =
                  axis42163;
                
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42153 =
                  t42151.rectMin$O((int)(t42152));
                
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
((x10.array.Array<x10.core.Int>)min).$set__1x10$array$Array$$T$G((int)(t42150),
                                                                                                                                                                            x10.core.Int.$box(t42153));
                
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42154 =
                  axis42163;
                
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat t42155 =
                  x42149;
                
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42156 =
                  axis42163;
                
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42157 =
                  t42155.rectMax$O((int)(t42156));
                
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
((x10.array.Array<x10.core.Int>)max).$set__1x10$array$Array$$T$G((int)(t42154),
                                                                                                                                                                            x10.core.Int.$box(t42157));
                
//#line 211 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat t42158 =
                  ((x10.array.PolyMat)(pm));
                
//#line 211 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42159 =
                  axis42163;
                
//#line 211 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat t42160 =
                  ((x10.array.PolyMat)(t42158.eliminate((int)(t42159),
                                                        (boolean)(true))));
                
//#line 211 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
pm = ((x10.array.PolyMat)(t42160));
                
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42161 =
                  axis42163;
                
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42162 =
                  ((t42161) + (((int)(1))));
                
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
axis42163 = t42162;
            }
            
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.Region t41980 =
              ((x10.array.Region)(x10.array.Region.<x10.core.Int,
            x10.core.Int>makeRectangular__0$1x10$array$Region$$S$2__1$1x10$array$Region$$T$2(x10.rtt.Types.INT, x10.rtt.Types.INT, ((x10.array.Array)(min)),
                                                                                             ((x10.array.Array)(max)))));
            
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
return t41980;
        }
        
        
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
public boolean
                                                                                                     contains$O(
                                                                                                     final x10.array.Point p){
            
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat t42170 =
              ((x10.array.PolyMat)(mat));
            
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.lang.Iterator<x10.array.PolyRow> r42171 =
              ((x10.array.Mat<x10.array.PolyRow>)t42170).iterator();
            
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
for (;
                                                                                                            true;
                                                                                                            ) {
                
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t42172 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)r42171).hasNext$O();
                
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
if (!(t42172)) {
                    
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
break;
                }
                
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyRow r42167 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)r42171).next$G();
                
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t42168 =
                  r42167.contains$O(((x10.array.Point)(p)));
                
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t42169 =
                  !(t42168);
                
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
if (t42169) {
                    
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
return false;
                }
            }
            
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
return true;
        }
        
        
//#line 244 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
public static int ROW = 0;
        
//#line 245 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
public static int COL = 0;
        
        
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
public static x10.array.Region
                                                                                                     makeBanded(
                                                                                                     final int rowMin,
                                                                                                     final int colMin,
                                                                                                     final int rowMax,
                                                                                                     final int colMax,
                                                                                                     final int upper,
                                                                                                     final int lower){
            
//#line 248 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMatBuilder pmb =
              ((x10.array.PolyMatBuilder)(new x10.array.PolyMatBuilder((java.lang.System[]) null).$init(((int)(2)))));
            
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t41986 =
              x10.array.PolyRegion.getInitialized$ROW();
            
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t41987 =
              x10.array.PolyMatBuilder.getInitialized$GE();
            
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
pmb.add((int)(t41986),
                                                                                                               (int)(t41987),
                                                                                                               (int)(rowMin));
            
//#line 250 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t41988 =
              x10.array.PolyRegion.getInitialized$ROW();
            
//#line 250 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t41989 =
              x10.array.PolyMatBuilder.getInitialized$LE();
            
//#line 250 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
pmb.add((int)(t41988),
                                                                                                               (int)(t41989),
                                                                                                               (int)(rowMax));
            
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t41990 =
              x10.array.PolyRegion.getInitialized$COL();
            
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t41991 =
              x10.array.PolyMatBuilder.getInitialized$GE();
            
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
pmb.add((int)(t41990),
                                                                                                               (int)(t41991),
                                                                                                               (int)(colMin));
            
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t41992 =
              x10.array.PolyRegion.getInitialized$COL();
            
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t41993 =
              x10.array.PolyMatBuilder.getInitialized$LE();
            
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
pmb.add((int)(t41992),
                                                                                                               (int)(t41993),
                                                                                                               (int)(colMax));
            
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t41994 =
              x10.array.PolyRegion.getInitialized$COL();
            
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t41995 =
              x10.array.PolyRegion.getInitialized$ROW();
            
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t41998 =
              ((t41994) - (((int)(t41995))));
            
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t41999 =
              x10.array.PolyMatBuilder.getInitialized$GE();
            
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t41996 =
              ((colMin) - (((int)(rowMin))));
            
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t41997 =
              ((lower) - (((int)(1))));
            
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42000 =
              ((t41996) - (((int)(t41997))));
            
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
pmb.add((int)(t41998),
                                                                                                               (int)(t41999),
                                                                                                               (int)(t42000));
            
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42001 =
              x10.array.PolyRegion.getInitialized$COL();
            
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42002 =
              x10.array.PolyRegion.getInitialized$ROW();
            
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42005 =
              ((t42001) - (((int)(t42002))));
            
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42006 =
              x10.array.PolyMatBuilder.getInitialized$LE();
            
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42003 =
              ((colMin) - (((int)(rowMin))));
            
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42004 =
              ((upper) - (((int)(1))));
            
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42007 =
              ((t42003) + (((int)(t42004))));
            
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
pmb.add((int)(t42005),
                                                                                                               (int)(t42006),
                                                                                                               (int)(t42007));
            
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat pm =
              ((x10.array.PolyMat)(pmb.toSortedPolyMat((boolean)(false))));
            
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.Region t42008 =
              ((x10.array.Region)(x10.array.PolyRegion.make(((x10.array.PolyMat)(pm)))));
            
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
return t42008;
        }
        
        
//#line 259 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
public static x10.array.Region
                                                                                                     makeBanded(
                                                                                                     final int size,
                                                                                                     final int upper,
                                                                                                     final int lower){
            
//#line 260 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42009 =
              ((size) - (((int)(1))));
            
//#line 260 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42010 =
              ((size) - (((int)(1))));
            
//#line 260 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.Region t42011 =
              ((x10.array.Region)(x10.array.PolyRegion.makeBanded((int)(0),
                                                                  (int)(0),
                                                                  (int)(t42009),
                                                                  (int)(t42010),
                                                                  (int)(upper),
                                                                  (int)(lower))));
            
//#line 260 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
return t42011;
        }
        
        
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
public static x10.array.Region
                                                                                                     makeUpperTriangular2(
                                                                                                     final int rowMin,
                                                                                                     final int colMin,
                                                                                                     final int size){
            
//#line 264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMatBuilder pmb =
              ((x10.array.PolyMatBuilder)(new x10.array.PolyMatBuilder((java.lang.System[]) null).$init(((int)(2)))));
            
//#line 265 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42012 =
              x10.array.PolyRegion.getInitialized$ROW();
            
//#line 265 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42013 =
              x10.array.PolyMatBuilder.getInitialized$GE();
            
//#line 265 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
pmb.add((int)(t42012),
                                                                                                               (int)(t42013),
                                                                                                               (int)(rowMin));
            
//#line 266 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42015 =
              x10.array.PolyRegion.getInitialized$COL();
            
//#line 266 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42016 =
              x10.array.PolyMatBuilder.getInitialized$LE();
            
//#line 266 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42014 =
              ((colMin) + (((int)(size))));
            
//#line 266 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42017 =
              ((t42014) - (((int)(1))));
            
//#line 266 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
pmb.add((int)(t42015),
                                                                                                               (int)(t42016),
                                                                                                               (int)(t42017));
            
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42018 =
              x10.array.PolyRegion.getInitialized$COL();
            
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42019 =
              x10.array.PolyRegion.getInitialized$ROW();
            
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42020 =
              ((t42018) - (((int)(t42019))));
            
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42021 =
              x10.array.PolyMatBuilder.getInitialized$GE();
            
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42022 =
              ((colMin) - (((int)(rowMin))));
            
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
pmb.add((int)(t42020),
                                                                                                               (int)(t42021),
                                                                                                               (int)(t42022));
            
//#line 268 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat pm =
              ((x10.array.PolyMat)(pmb.toSortedPolyMat((boolean)(true))));
            
//#line 269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.Region t42023 =
              ((x10.array.Region)(x10.array.PolyRegion.make(((x10.array.PolyMat)(pm)))));
            
//#line 269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
return t42023;
        }
        
        
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
public static x10.array.Region
                                                                                                     makeLowerTriangular2(
                                                                                                     final int rowMin,
                                                                                                     final int colMin,
                                                                                                     final int size){
            
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMatBuilder pmb =
              ((x10.array.PolyMatBuilder)(new x10.array.PolyMatBuilder((java.lang.System[]) null).$init(((int)(2)))));
            
//#line 274 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42024 =
              x10.array.PolyRegion.getInitialized$COL();
            
//#line 274 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42025 =
              x10.array.PolyMatBuilder.getInitialized$GE();
            
//#line 274 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
pmb.add((int)(t42024),
                                                                                                               (int)(t42025),
                                                                                                               (int)(colMin));
            
//#line 275 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42027 =
              x10.array.PolyRegion.getInitialized$ROW();
            
//#line 275 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42028 =
              x10.array.PolyMatBuilder.getInitialized$LE();
            
//#line 275 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42026 =
              ((rowMin) + (((int)(size))));
            
//#line 275 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42029 =
              ((t42026) - (((int)(1))));
            
//#line 275 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
pmb.add((int)(t42027),
                                                                                                               (int)(t42028),
                                                                                                               (int)(t42029));
            
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42030 =
              x10.array.PolyRegion.getInitialized$ROW();
            
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42031 =
              x10.array.PolyRegion.getInitialized$COL();
            
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42032 =
              ((t42030) - (((int)(t42031))));
            
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42033 =
              x10.array.PolyMatBuilder.getInitialized$GE();
            
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42034 =
              ((rowMin) - (((int)(colMin))));
            
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
pmb.add((int)(t42032),
                                                                                                               (int)(t42033),
                                                                                                               (int)(t42034));
            
//#line 277 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat pm =
              ((x10.array.PolyMat)(pmb.toSortedPolyMat((boolean)(true))));
            
//#line 278 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.Region t42035 =
              ((x10.array.Region)(x10.array.PolyRegion.make(((x10.array.PolyMat)(pm)))));
            
//#line 278 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
return t42035;
        }
        
        
//#line 288 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
public static x10.array.Region
                                                                                                     make(
                                                                                                     final x10.array.PolyMat pm){
            
//#line 289 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t42039 =
              pm.isEmpty$O();
            
//#line 289 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
if (t42039) {
                
//#line 290 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42036 =
                  pm.
                    rank;
                
//#line 290 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.EmptyRegion t42037 =
                  ((x10.array.EmptyRegion)(new x10.array.EmptyRegion((java.lang.System[]) null).$init(((int)(t42036)))));
                
//#line 290 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
return t42037;
            } else {
                
//#line 292 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyRegion t42038 =
                  ((x10.array.PolyRegion)(new x10.array.PolyRegion((java.lang.System[]) null).$init(((x10.array.PolyMat)(pm)),
                                                                                                    ((boolean)(false)))));
                
//#line 292 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
return t42038;
            }
        }
        
        
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
// creation method for java code (1-phase java constructor)
        public PolyRegion(final x10.array.PolyMat pm,
                          final boolean hack198){this((java.lang.System[]) null);
                                                     $init(pm,hack198);}
        
        // constructor for non-virtual call
        final public x10.array.PolyRegion x10$array$PolyRegion$$init$S(final x10.array.PolyMat pm,
                                                                       final boolean hack198) { {
                                                                                                       
//#line 298 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42173 =
                                                                                                         pm.
                                                                                                           rank;
                                                                                                       
//#line 298 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t42174 =
                                                                                                         pm.isRect$O();
                                                                                                       
//#line 298 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t42175 =
                                                                                                         pm.isZeroBased$O();
                                                                                                       
//#line 298 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
super.$init(((int)(t42173)),
                                                                                                                                                                                                              t42174,
                                                                                                                                                                                                              t42175);
                                                                                                       
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"

                                                                                                       
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
this.__fieldInitializers41477();
                                                                                                       
//#line 302 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat t42043 =
                                                                                                         ((x10.array.PolyMat)(pm.simplifyAll()));
                                                                                                       
//#line 302 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat __desugarer__var__23__41827 =
                                                                                                         ((x10.array.PolyMat)(((x10.array.PolyMat)
                                                                                                                                t42043)));
                                                                                                       
//#line 302 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
x10.array.PolyMat ret41828 =
                                                                                                          null;
                                                                                                       
//#line 302 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42176 =
                                                                                                         __desugarer__var__23__41827.
                                                                                                           rank;
                                                                                                       
//#line 302 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42177 =
                                                                                                         x10.array.PolyRegion.this.
                                                                                                           rank;
                                                                                                       
//#line 302 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t42178 =
                                                                                                         ((int) t42176) ==
                                                                                                       ((int) t42177);
                                                                                                       
//#line 302 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t42179 =
                                                                                                         !(t42178);
                                                                                                       
//#line 302 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
if (t42179) {
                                                                                                           
//#line 302 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final boolean t42180 =
                                                                                                             true;
                                                                                                           
//#line 302 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
if (t42180) {
                                                                                                               
//#line 302 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.lang.FailedDynamicCheckException t42181 =
                                                                                                                 new x10.lang.FailedDynamicCheckException("x10.array.PolyMat{self.rank==this(:x10.array.PolyRegion).rank}");
                                                                                                               
//#line 302 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
throw t42181;
                                                                                                           }
                                                                                                       }
                                                                                                       
//#line 302 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
ret41828 = ((x10.array.PolyMat)(__desugarer__var__23__41827));
                                                                                                       
//#line 302 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat t42050 =
                                                                                                         ((x10.array.PolyMat)(ret41828));
                                                                                                       
//#line 302 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
this.mat = ((x10.array.PolyMat)(t42050));
                                                                                                   }
                                                                                                   return this;
                                                                                                   }
        
        // constructor
        public x10.array.PolyRegion $init(final x10.array.PolyMat pm,
                                          final boolean hack198){return x10$array$PolyRegion$$init$S(pm,hack198);}
        
        
        
//#line 310 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>
                                                                                                     min(
                                                                                                     ){
            
//#line 311 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.Region t42051 =
              ((x10.array.Region)(this.boundingBox()));
            
//#line 311 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t =
              t42051.min();
            
//#line 312 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t42053 =
              ((x10.core.fun.Fun_0_1)(new x10.array.PolyRegion.$Closure$56(t, (x10.array.PolyRegion.$Closure$56.__0$1x10$lang$Int$3x10$lang$Int$2) null)));
            
//#line 312 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
return t42053;
        }
        
        
//#line 315 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>
                                                                                                     max(
                                                                                                     ){
            
//#line 316 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.Region t42054 =
              ((x10.array.Region)(this.boundingBox()));
            
//#line 316 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t =
              t42054.max();
            
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t42056 =
              ((x10.core.fun.Fun_0_1)(new x10.array.PolyRegion.$Closure$57(t, (x10.array.PolyRegion.$Closure$57.__0$1x10$lang$Int$3x10$lang$Int$2) null)));
            
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
return t42056;
        }
        
        
//#line 325 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
public void
                                                                                                     printInfo(
                                                                                                     final x10.io.Printer out){
            
//#line 326 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat t42057 =
              ((x10.array.PolyMat)(mat));
            
//#line 326 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final java.lang.String t42058 =
              this.toString();
            
//#line 326 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
((x10.array.Mat<x10.array.PolyRow>)t42057).printInfo(((x10.io.Printer)(out)),
                                                                                                                                                            ((java.lang.String)(t42058)));
        }
        
        
//#line 329 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
public java.lang.String
                                                                                                     toString(
                                                                                                     ){
            
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final x10.array.PolyMat t42059 =
              ((x10.array.PolyMat)(mat));
            
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final java.lang.String t42060 =
              t42059.toString();
            
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
return t42060;
        }
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final public x10.array.PolyRegion
                                                                                                    x10$array$PolyRegion$$x10$array$PolyRegion$this(
                                                                                                    ){
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
return x10.array.PolyRegion.this;
        }
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final public void
                                                                                                    __fieldInitializers41477(
                                                                                                    ){
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
this.size = -1;
        }
        
        public static short fieldId$COL;
        final public static x10.core.concurrent.AtomicInteger initStatus$COL = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        public static short fieldId$ROW;
        final public static x10.core.concurrent.AtomicInteger initStatus$ROW = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        
        public static void
          getDeserialized$ROW(
          x10.x10rt.X10JavaDeserializer deserializer){
            x10.array.PolyRegion.ROW = (x10.core.Int.$unbox(x10.runtime.impl.java.InitDispatcher.deserializeInt(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
            x10.array.PolyRegion.initStatus$ROW.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static int
          getInitialized$ROW(
          ){
            if (((int) x10.array.PolyRegion.initStatus$ROW.get()) ==
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                return x10.array.PolyRegion.ROW;
            }
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0) &&
                  x10.array.PolyRegion.initStatus$ROW.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                    (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                x10.array.PolyRegion.ROW = x10.array.PolyMatBuilder.X$O((int)(0));
                if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                      ((boolean) true)) {
                    x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.array.PolyRegion.ROW")));
                }
                x10.runtime.impl.java.InitDispatcher.broadcastStaticField((int)(x10.array.PolyRegion.ROW),
                                                                          (short)(x10.array.PolyRegion.fieldId$ROW));
                x10.array.PolyRegion.initStatus$ROW.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                x10.runtime.impl.java.InitDispatcher.notifyInitialized();
            } else {
                if (((int) x10.array.PolyRegion.initStatus$ROW.get()) !=
                    ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    while (((int) x10.array.PolyRegion.initStatus$ROW.get()) !=
                           ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                    }
                    x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                }
            }
            return x10.array.PolyRegion.ROW;
        }
        
        public static void
          getDeserialized$COL(
          x10.x10rt.X10JavaDeserializer deserializer){
            x10.array.PolyRegion.COL = (x10.core.Int.$unbox(x10.runtime.impl.java.InitDispatcher.deserializeInt(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
            x10.array.PolyRegion.initStatus$COL.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static int
          getInitialized$COL(
          ){
            if (((int) x10.array.PolyRegion.initStatus$COL.get()) ==
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                return x10.array.PolyRegion.COL;
            }
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0) &&
                  x10.array.PolyRegion.initStatus$COL.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                    (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                x10.array.PolyRegion.COL = x10.array.PolyMatBuilder.X$O((int)(1));
                if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                      ((boolean) true)) {
                    x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.array.PolyRegion.COL")));
                }
                x10.runtime.impl.java.InitDispatcher.broadcastStaticField((int)(x10.array.PolyRegion.COL),
                                                                          (short)(x10.array.PolyRegion.fieldId$COL));
                x10.array.PolyRegion.initStatus$COL.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                x10.runtime.impl.java.InitDispatcher.notifyInitialized();
            } else {
                if (((int) x10.array.PolyRegion.initStatus$COL.get()) !=
                    ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    while (((int) x10.array.PolyRegion.initStatus$COL.get()) !=
                           ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                    }
                    x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                }
            }
            return x10.array.PolyRegion.COL;
        }
        
        static {
                   x10.array.PolyRegion.fieldId$ROW = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.array.PolyRegion")),
                                                                                                                          ((java.lang.String)("ROW")))))));
                   x10.array.PolyRegion.fieldId$COL = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.array.PolyRegion")),
                                                                                                                          ((java.lang.String)("COL")))))));
               }
        
        @x10.core.X10Generated public static class $Closure$56 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$56.class);
            
            public static final x10.rtt.RuntimeType<$Closure$56> $RTT = x10.rtt.StaticFunType.<$Closure$56> make(
            /* base class */$Closure$56.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$56 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$56.class + " calling"); } 
                x10.core.fun.Fun_0_1 t = (x10.core.fun.Fun_0_1) $deserializer.readRef();
                $_obj.t = t;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$56 $_obj = new $Closure$56((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (t instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.t);
                } else {
                $serializer.write(this.t);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$56(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 312 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42052 =
                      x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)this.
                                                                                              t).$apply(x10.core.Int.$box(i),x10.rtt.Types.INT));
                    
//#line 312 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
return t42052;
                }
                
                public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t;
                
                public $Closure$56(final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t, __0$1x10$lang$Int$3x10$lang$Int$2 $dummy) { {
                                                                                                                                               this.t = ((x10.core.fun.Fun_0_1)(t));
                                                                                                                                           }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$lang$Int$3x10$lang$Int$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$57 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$57.class);
            
            public static final x10.rtt.RuntimeType<$Closure$57> $RTT = x10.rtt.StaticFunType.<$Closure$57> make(
            /* base class */$Closure$57.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$57 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$57.class + " calling"); } 
                x10.core.fun.Fun_0_1 t = (x10.core.fun.Fun_0_1) $deserializer.readRef();
                $_obj.t = t;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$57 $_obj = new $Closure$57((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (t instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.t);
                } else {
                $serializer.write(this.t);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$57(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
final int t42055 =
                      x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)this.
                                                                                              t).$apply(x10.core.Int.$box(i),x10.rtt.Types.INT));
                    
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyRegion.x10"
return t42055;
                }
                
                public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t;
                
                public $Closure$57(final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t, __0$1x10$lang$Int$3x10$lang$Int$2 $dummy) { {
                                                                                                                                               this.t = ((x10.core.fun.Fun_0_1)(t));
                                                                                                                                           }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$lang$Int$3x10$lang$Int$2 {}
                
            }
            
        
        }
        
        