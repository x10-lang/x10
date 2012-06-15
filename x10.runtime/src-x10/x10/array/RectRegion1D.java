package x10.array;


@x10.core.X10Generated final public class RectRegion1D extends x10.array.Region implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, RectRegion1D.class);
    
    public static final x10.rtt.RuntimeType<RectRegion1D> $RTT = x10.rtt.NamedType.<RectRegion1D> make(
    "x10.array.RectRegion1D", /* base class */RectRegion1D.class
    , /* parents */ new x10.rtt.Type[] {x10.array.Region.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(RectRegion1D $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + RectRegion1D.class + " calling"); } 
        x10.array.Region.$_deserialize_body($_obj, $deserializer);
        $_obj.size = $deserializer.readInt();
        $_obj.min = $deserializer.readInt();
        $_obj.max = $deserializer.readInt();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        RectRegion1D $_obj = new RectRegion1D((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        $serializer.write(this.size);
        $serializer.write(this.min);
        $serializer.write(this.max);
        
    }
    
    // constructor just for allocation
    public RectRegion1D(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public int size;
        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public int min;
        
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public int max;
        
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
// creation method for java code (1-phase java constructor)
        public RectRegion1D(final int minArg,
                            final int maxArg){this((java.lang.System[]) null);
                                                  $init(minArg,maxArg);}
        
        // constructor for non-virtual call
        final public x10.array.RectRegion1D x10$array$RectRegion1D$$init$S(final int minArg,
                                                                           final int maxArg) { {
                                                                                                      
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final boolean t47146 =
                                                                                                        ((int) minArg) ==
                                                                                                      ((int) 0);
                                                                                                      
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
super.$init(((int)(1)),
                                                                                                                                                                                                              ((boolean)(true)),
                                                                                                                                                                                                              t47146);
                                                                                                      
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"

                                                                                                      
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final long t47049 =
                                                                                                        ((long)(((int)(maxArg))));
                                                                                                      
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final long t47050 =
                                                                                                        ((long)(((int)(minArg))));
                                                                                                      
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final long t47051 =
                                                                                                        ((t47049) - (((long)(t47050))));
                                                                                                      
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final long s =
                                                                                                        ((t47051) + (((long)(1L))));
                                                                                                      
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47052 =
                                                                                                        java.lang.Integer.MAX_VALUE;
                                                                                                      
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final long t47053 =
                                                                                                        ((long)(((int)(t47052))));
                                                                                                      
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final boolean t47055 =
                                                                                                        ((s) > (((long)(t47053))));
                                                                                                      
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
if (t47055) {
                                                                                                          
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
this.size = -1;
                                                                                                      } else {
                                                                                                          
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47054 =
                                                                                                            ((int)(long)(((long)(s))));
                                                                                                          
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
this.size = t47054;
                                                                                                      }
                                                                                                      
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
this.min = minArg;
                                                                                                      
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
this.max = maxArg;
                                                                                                  }
                                                                                                  return this;
                                                                                                  }
        
        // constructor
        public x10.array.RectRegion1D $init(final int minArg,
                                            final int maxArg){return x10$array$RectRegion1D$$init$S(minArg,maxArg);}
        
        
        
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
// creation method for java code (1-phase java constructor)
        public RectRegion1D(final int maxArg){this((java.lang.System[]) null);
                                                  $init(maxArg);}
        
        // constructor for non-virtual call
        final public x10.array.RectRegion1D x10$array$RectRegion1D$$init$S(final int maxArg) { {
                                                                                                      
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
super.$init(((int)(1)));
                                                                                                      
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"

                                                                                                      
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final long t47056 =
                                                                                                        ((long)(((int)(maxArg))));
                                                                                                      
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final long s =
                                                                                                        ((t47056) + (((long)(1L))));
                                                                                                      
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47057 =
                                                                                                        java.lang.Integer.MAX_VALUE;
                                                                                                      
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final long t47058 =
                                                                                                        ((long)(((int)(t47057))));
                                                                                                      
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final boolean t47060 =
                                                                                                        ((s) > (((long)(t47058))));
                                                                                                      
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
if (t47060) {
                                                                                                          
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
this.size = -1;
                                                                                                      } else {
                                                                                                          
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47059 =
                                                                                                            ((int)(long)(((long)(s))));
                                                                                                          
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
this.size = t47059;
                                                                                                      }
                                                                                                      
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
this.min = 0;
                                                                                                      
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
this.max = maxArg;
                                                                                                  }
                                                                                                  return this;
                                                                                                  }
        
        // constructor
        public x10.array.RectRegion1D $init(final int maxArg){return x10$array$RectRegion1D$$init$S(maxArg);}
        
        
        
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public int
                                                                                                      size$O(
                                                                                                      ){
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47061 =
              size;
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final boolean t47063 =
              ((t47061) < (((int)(0))));
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
if (t47063) {
                
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final x10.array.UnboundedRegionException t47062 =
                  ((x10.array.UnboundedRegionException)(new x10.array.UnboundedRegionException(((java.lang.String)("size exceeds capacity of int")))));
                
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
throw t47062;
            }
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47064 =
              size;
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return t47064;
        }
        
        
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public boolean
                                                                                                      isConvex$O(
                                                                                                      ){
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return true;
        }
        
        
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public boolean
                                                                                                      isEmpty$O(
                                                                                                      ){
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47065 =
              size;
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final boolean t47066 =
              ((int) t47065) ==
            ((int) 0);
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return t47066;
        }
        
        
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public int
                                                                                                      indexOf$O(
                                                                                                      final x10.array.Point pt){
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final boolean t47067 =
              this.contains$O(((x10.array.Point)(pt)));
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final boolean t47068 =
              !(t47067);
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
if (t47068) {
                
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return -1;
            }
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47069 =
              pt.$apply$O((int)(0));
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47070 =
              min;
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47071 =
              ((t47069) - (((int)(t47070))));
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return t47071;
        }
        
        
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public int
                                                                                                      indexOf$O(
                                                                                                      final int i0){
            
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final boolean t47078 =
              zeroBased;
            
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
if (t47078) {
                
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final boolean t47072 =
                  this.containsInternal$O((int)(i0));
                
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final boolean t47073 =
                  !(t47072);
                
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
if (t47073) {
                    
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return -1;
                }
                
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return i0;
            } else {
                
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final boolean t47074 =
                  this.containsInternal$O((int)(i0));
                
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final boolean t47075 =
                  !(t47074);
                
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
if (t47075) {
                    
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return -1;
                }
                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47076 =
                  min;
                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47077 =
                  ((i0) - (((int)(t47076))));
                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return t47077;
            }
        }
        
        
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public int
                                                                                                      indexOf$O(
                                                                                                      final int i0,
                                                                                                      final int i1){
            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return -1;
        }
        
        
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public int
                                                                                                      indexOf$O(
                                                                                                      final int i0,
                                                                                                      final int i1,
                                                                                                      final int i2){
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return -1;
        }
        
        
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public int
                                                                                                      indexOf$O(
                                                                                                      final int i0,
                                                                                                      final int i1,
                                                                                                      final int i2,
                                                                                                      final int i3){
            
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return -1;
        }
        
        
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public int
                                                                                                      min$O(
                                                                                                      final int i){
            
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final boolean t47083 =
              ((int) i) !=
            ((int) 0);
            
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
if (t47083) {
                
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final java.lang.String t47079 =
                  (("min: ") + ((x10.core.Int.$box(i))));
                
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final java.lang.String t47080 =
                  ((t47079) + (" is not a valid rank for "));
                
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final java.lang.String t47081 =
                  ((t47080) + (this));
                
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final x10.lang.ArrayIndexOutOfBoundsException t47082 =
                  ((x10.lang.ArrayIndexOutOfBoundsException)(new x10.lang.ArrayIndexOutOfBoundsException(t47081)));
                
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
throw t47082;
            }
            
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47084 =
              min;
            
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return t47084;
        }
        
        
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public int
                                                                                                       max$O(
                                                                                                       final int i){
            
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final boolean t47089 =
              ((int) i) !=
            ((int) 0);
            
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
if (t47089) {
                
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final java.lang.String t47085 =
                  (("max: ") + ((x10.core.Int.$box(i))));
                
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final java.lang.String t47086 =
                  ((t47085) + (" is not a valid rank for "));
                
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final java.lang.String t47087 =
                  ((t47086) + (this));
                
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final x10.lang.ArrayIndexOutOfBoundsException t47088 =
                  ((x10.lang.ArrayIndexOutOfBoundsException)(new x10.lang.ArrayIndexOutOfBoundsException(t47087)));
                
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
throw t47088;
            }
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47090 =
              max;
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return t47090;
        }
        
        
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public x10.array.Region
                                                                                                       computeBoundingBox(
                                                                                                       ){
            
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return this;
        }
        
        
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public x10.array.RectRegion
                                                                                                       toRectRegion(
                                                                                                       ){
            
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47091 =
              min;
            
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47092 =
              max;
            
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final x10.array.RectRegion t47093 =
              ((x10.array.RectRegion)(new x10.array.RectRegion((java.lang.System[]) null).$init(((int)(t47091)),
                                                                                                ((int)(t47092)))));
            
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return t47093;
        }
        
        
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>
                                                                                                       min(
                                                                                                       ){
            
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t47095 =
              ((x10.core.fun.Fun_0_1)(new x10.array.RectRegion1D.$Closure$74(this)));
            
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return t47095;
        }
        
        
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>
                                                                                                       max(
                                                                                                       ){
            
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t47097 =
              ((x10.core.fun.Fun_0_1)(new x10.array.RectRegion1D.$Closure$75(this)));
            
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return t47097;
        }
        
        
//#line 121 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public boolean
                                                                                                       contains$O(
                                                                                                       final x10.array.Region that){
            
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final x10.array.RectRegion t47098 =
              ((x10.array.RectRegion)(this.toRectRegion()));
            
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final boolean t47099 =
              t47098.contains$O(((x10.array.Region)(that)));
            
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return t47099;
        }
        
        
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public boolean
                                                                                                       contains$O(
                                                                                                       final x10.array.Point p){
            
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final x10.array.RectRegion t47100 =
              ((x10.array.RectRegion)(this.toRectRegion()));
            
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final boolean t47101 =
              t47100.contains$O(((x10.array.Point)(p)));
            
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return t47101;
        }
        
        
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public boolean
                                                                                                       contains$O(
                                                                                                       final int i0){
            
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final boolean t47103 =
              this.containsInternal$O((int)(i0));
            
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return t47103;
        }
        
        
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
private boolean
                                                                                                       containsInternal$O(
                                                                                                       final int i0){
            
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47104 =
              min;
            
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
boolean t47106 =
              ((i0) >= (((int)(t47104))));
            
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
if (t47106) {
                
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47105 =
                  max;
                
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
t47106 = ((i0) <= (((int)(t47105))));
            }
            
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final boolean t47107 =
              t47106;
            
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return t47107;
        }
        
        public static boolean
          containsInternal$P$O(
          final int i0,
          final x10.array.RectRegion1D RectRegion1D){
            return RectRegion1D.containsInternal$O((int)(i0));
        }
        
        
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public x10.array.Region
                                                                                                       toPolyRegion(
                                                                                                       ){
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final x10.array.RectRegion t47108 =
              ((x10.array.RectRegion)(this.toRectRegion()));
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final x10.array.Region t47109 =
              ((x10.array.Region)(t47108.toPolyRegion()));
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return t47109;
        }
        
        
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public x10.array.Region
                                                                                                       intersection(
                                                                                                       final x10.array.Region that){
            
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final x10.array.RectRegion t47110 =
              ((x10.array.RectRegion)(this.toRectRegion()));
            
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final x10.array.Region t47111 =
              ((x10.array.Region)(t47110.intersection(((x10.array.Region)(that)))));
            
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return t47111;
        }
        
        
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public x10.array.Region
                                                                                                       product(
                                                                                                       final x10.array.Region that){
            
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final x10.array.RectRegion t47112 =
              ((x10.array.RectRegion)(this.toRectRegion()));
            
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final x10.array.Region t47113 =
              ((x10.array.Region)(t47112.product(((x10.array.Region)(that)))));
            
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return t47113;
        }
        
        
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public x10.array.Region
                                                                                                       translate(
                                                                                                       final x10.array.Point v){
            
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47114 =
              min;
            
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47115 =
              v.$apply$O((int)(0));
            
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47118 =
              ((t47114) + (((int)(t47115))));
            
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47116 =
              max;
            
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47117 =
              v.$apply$O((int)(0));
            
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47119 =
              ((t47116) + (((int)(t47117))));
            
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final x10.array.RectRegion1D t47120 =
              ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null).$init(t47118,
                                                                                                    t47119)));
            
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return t47120;
        }
        
        
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public x10.array.Region
                                                                                                       projection(
                                                                                                       final int axis){
            
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final boolean t47121 =
              ((int) axis) ==
            ((int) 0);
            
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
if (t47121) {
                
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return this;
            }
            
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final java.lang.String t47122 =
              (("projection: ") + ((x10.core.Int.$box(axis))));
            
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final java.lang.String t47123 =
              ((t47122) + (" is not a valid rank for "));
            
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final java.lang.String t47124 =
              ((t47123) + (this));
            
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final x10.lang.ArrayIndexOutOfBoundsException t47125 =
              ((x10.lang.ArrayIndexOutOfBoundsException)(new x10.lang.ArrayIndexOutOfBoundsException(t47124)));
            
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
throw t47125;
        }
        
        
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public x10.array.Region
                                                                                                       eliminate(
                                                                                                       final int axis){
            
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final x10.array.RectRegion t47126 =
              ((x10.array.RectRegion)(this.toRectRegion()));
            
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final x10.array.Region t47127 =
              ((x10.array.Region)(t47126.eliminate((int)(axis))));
            
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return t47127;
        }
        
        
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
@x10.core.X10Generated public static class RRIterator extends x10.core.Ref implements x10.lang.Iterator, x10.x10rt.X10JavaSerializable
                                                                                                     {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, RRIterator.class);
            
            public static final x10.rtt.RuntimeType<RRIterator> $RTT = x10.rtt.NamedType.<RRIterator> make(
            "x10.array.RectRegion1D.RRIterator", /* base class */RRIterator.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Iterator.$RTT, x10.array.Point.$RTT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(RRIterator $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + RRIterator.class + " calling"); } 
                $_obj.min = $deserializer.readInt();
                $_obj.max = $deserializer.readInt();
                $_obj.cur = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                RRIterator $_obj = new RRIterator((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write(this.min);
                $serializer.write(this.max);
                $serializer.write(this.cur);
                
            }
            
            // constructor just for allocation
            public RRIterator(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // bridge for method abstract public x10.lang.Iterator.next():T
            public x10.array.Point
              next$G(){return next();}
            
                
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public int min;
                
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public int max;
                
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public int cur;
                
                
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
// creation method for java code (1-phase java constructor)
                public RRIterator(final x10.array.RectRegion1D rr){this((java.lang.System[]) null);
                                                                       $init(rr);}
                
                // constructor for non-virtual call
                final public x10.array.RectRegion1D.RRIterator x10$array$RectRegion1D$RRIterator$$init$S(final x10.array.RectRegion1D rr) { {
                                                                                                                                                   
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"

                                                                                                                                                   
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"

                                                                                                                                                   
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
this.__fieldInitializers46693();
                                                                                                                                                   
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47128 =
                                                                                                                                                     rr.
                                                                                                                                                       min;
                                                                                                                                                   
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
this.min = t47128;
                                                                                                                                                   
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47129 =
                                                                                                                                                     rr.
                                                                                                                                                       max;
                                                                                                                                                   
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
this.max = t47129;
                                                                                                                                                   
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47130 =
                                                                                                                                                     min;
                                                                                                                                                   
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
this.cur = t47130;
                                                                                                                                               }
                                                                                                                                               return this;
                                                                                                                                               }
                
                // constructor
                public x10.array.RectRegion1D.RRIterator $init(final x10.array.RectRegion1D rr){return x10$array$RectRegion1D$RRIterator$$init$S(rr);}
                
                
                
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public boolean
                                                                                                               hasNext$O(
                                                                                                               ){
                    
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47131 =
                      cur;
                    
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47132 =
                      max;
                    
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final boolean t47133 =
                      ((t47131) <= (((int)(t47132))));
                    
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return t47133;
                }
                
                
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public x10.array.Point
                                                                                                               next(
                                                                                                               ){
                    
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final x10.array.RectRegion1D.RRIterator x47046 =
                      ((x10.array.RectRegion1D.RRIterator)(this));
                    
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
;
                    
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47134 =
                      x47046.
                        cur;
                    
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47135 =
                      ((t47134) + (((int)(1))));
                    
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47136 =
                      x47046.cur = t47135;
                    
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47137 =
                      ((t47136) - (((int)(1))));
                    
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final x10.array.Point t47138 =
                      ((x10.array.Point)(x10.array.Point.make((int)(t47137))));
                    
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return t47138;
                }
                
                
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final public x10.array.RectRegion1D.RRIterator
                                                                                                               x10$array$RectRegion1D$RRIterator$$x10$array$RectRegion1D$RRIterator$this(
                                                                                                               ){
                    
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return x10.array.RectRegion1D.RRIterator.this;
                }
                
                
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final public void
                                                                                                               __fieldInitializers46693(
                                                                                                               ){
                    
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
this.cur = 0;
                }
            
        }
        
        
        
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public x10.lang.Iterator<x10.array.Point>
                                                                                                       iterator(
                                                                                                       ){
            
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final x10.array.RectRegion1D.RRIterator t47139 =
              ((x10.array.RectRegion1D.RRIterator)(new x10.array.RectRegion1D.RRIterator((java.lang.System[]) null).$init(((x10.array.RectRegion1D)(this)))));
            
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return t47139;
        }
        
        
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
public java.lang.String
                                                                                                       toString(
                                                                                                       ){
            
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47140 =
              min;
            
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final java.lang.String t47141 =
              (("[") + ((x10.core.Int.$box(t47140))));
            
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final java.lang.String t47142 =
              ((t47141) + (".."));
            
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47143 =
              max;
            
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final java.lang.String t47144 =
              ((t47142) + ((x10.core.Int.$box(t47143))));
            
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final java.lang.String t47145 =
              ((t47144) + ("]"));
            
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return t47145;
        }
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final public x10.array.RectRegion1D
                                                                                                      x10$array$RectRegion1D$$x10$array$RectRegion1D$this(
                                                                                                      ){
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return x10.array.RectRegion1D.this;
        }
        
        @x10.core.X10Generated public static class $Closure$74 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$74.class);
            
            public static final x10.rtt.RuntimeType<$Closure$74> $RTT = x10.rtt.StaticFunType.<$Closure$74> make(
            /* base class */$Closure$74.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$74 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$74.class + " calling"); } 
                x10.array.RectRegion1D out$$ = (x10.array.RectRegion1D) $deserializer.readRef();
                $_obj.out$$ = out$$;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$74 $_obj = new $Closure$74((java.lang.System[]) null);
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
            public $Closure$74(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47094 =
                      this.
                        out$$.min$O((int)(i));
                    
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return t47094;
                }
                
                public x10.array.RectRegion1D out$$;
                
                public $Closure$74(final x10.array.RectRegion1D out$$) { {
                                                                                this.out$$ = out$$;
                                                                            }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$75 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$75.class);
            
            public static final x10.rtt.RuntimeType<$Closure$75> $RTT = x10.rtt.StaticFunType.<$Closure$75> make(
            /* base class */$Closure$75.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$75 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$75.class + " calling"); } 
                x10.array.RectRegion1D out$$ = (x10.array.RectRegion1D) $deserializer.readRef();
                $_obj.out$$ = out$$;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$75 $_obj = new $Closure$75((java.lang.System[]) null);
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
            public $Closure$75(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
final int t47096 =
                      this.
                        out$$.max$O((int)(i));
                    
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion1D.x10"
return t47096;
                }
                
                public x10.array.RectRegion1D out$$;
                
                public $Closure$75(final x10.array.RectRegion1D out$$) { {
                                                                                this.out$$ = out$$;
                                                                            }}
                
            }
            
        
        }
        