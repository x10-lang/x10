package x10.array;


@x10.core.X10Generated final public class PolyScanner extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, PolyScanner.class);
    
    public static final x10.rtt.RuntimeType<PolyScanner> $RTT = x10.rtt.NamedType.<PolyScanner> make(
    "x10.array.PolyScanner", /* base class */PolyScanner.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(PolyScanner $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + PolyScanner.class + " calling"); } 
        x10.array.PolyMat C = (x10.array.PolyMat) $deserializer.readRef();
        $_obj.C = C;
        x10.array.Array myMin = (x10.array.Array) $deserializer.readRef();
        $_obj.myMin = myMin;
        x10.array.Array myMax = (x10.array.Array) $deserializer.readRef();
        $_obj.myMax = myMax;
        x10.array.Array minSum = (x10.array.Array) $deserializer.readRef();
        $_obj.minSum = minSum;
        x10.array.Array maxSum = (x10.array.Array) $deserializer.readRef();
        $_obj.maxSum = maxSum;
        x10.array.Array parFlags = (x10.array.Array) $deserializer.readRef();
        $_obj.parFlags = parFlags;
        x10.array.Array min2 = (x10.array.Array) $deserializer.readRef();
        $_obj.min2 = min2;
        x10.array.Array max2 = (x10.array.Array) $deserializer.readRef();
        $_obj.max2 = max2;
        $_obj.rank = $deserializer.readInt();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        PolyScanner $_obj = new PolyScanner((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        if (C instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.C);
        } else {
        $serializer.write(this.C);
        }
        if (myMin instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.myMin);
        } else {
        $serializer.write(this.myMin);
        }
        if (myMax instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.myMax);
        } else {
        $serializer.write(this.myMax);
        }
        if (minSum instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.minSum);
        } else {
        $serializer.write(this.minSum);
        }
        if (maxSum instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.maxSum);
        } else {
        $serializer.write(this.maxSum);
        }
        if (parFlags instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.parFlags);
        } else {
        $serializer.write(this.parFlags);
        }
        if (min2 instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.min2);
        } else {
        $serializer.write(this.min2);
        }
        if (max2 instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.max2);
        } else {
        $serializer.write(this.max2);
        }
        $serializer.write(this.rank);
        
    }
    
    // constructor just for allocation
    public PolyScanner(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
public int rank;
        
        
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
public x10.array.PolyMat C;
        
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
public x10.array.Array<x10.array.VarMat> myMin;
        
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
public x10.array.Array<x10.array.VarMat> myMax;
        
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
public x10.array.Array<x10.array.VarMat> minSum;
        
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
public x10.array.Array<x10.array.VarMat> maxSum;
        
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
public x10.array.Array<x10.core.Boolean> parFlags;
        
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
public x10.array.Array<x10.array.Array<x10.array.PolyRow>> min2;
        
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
public x10.array.Array<x10.array.Array<x10.array.PolyRow>> max2;
        
        
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
public static x10.array.PolyScanner
                                                                                                     make(
                                                                                                     final x10.array.PolyMat pm){
            
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyScanner x =
              ((x10.array.PolyScanner)(new x10.array.PolyScanner((java.lang.System[]) null).$init(((x10.array.PolyMat)(pm)))));
            
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
x.init();
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
return x;
        }
        
        
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
// creation method for java code (1-phase java constructor)
        public PolyScanner(final x10.array.PolyMat pm){this((java.lang.System[]) null);
                                                           $init(pm);}
        
        // constructor for non-virtual call
        final public x10.array.PolyScanner x10$array$PolyScanner$$init$S(final x10.array.PolyMat pm) { {
                                                                                                              
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"

                                                                                                              
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44023 =
                                                                                                                pm.
                                                                                                                  rank;
                                                                                                              
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.rank = t44023;
                                                                                                              
                                                                                                              
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
x10.array.PolyMat pm0 =
                                                                                                                pm.simplifyAll();
                                                                                                              
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.C = ((x10.array.PolyMat)(pm));
                                                                                                              
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyMat t43581 =
                                                                                                                pm0;
                                                                                                              
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int r =
                                                                                                                t43581.
                                                                                                                  rank;
                                                                                                              
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> n =
                                                                                                                ((x10.array.Array)(new x10.array.Array<x10.array.VarMat>((java.lang.System[]) null, x10.array.VarMat.$RTT).$init(((int)(r)))));
                                                                                                              
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.myMin = ((x10.array.Array)(n));
                                                                                                              
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> x =
                                                                                                                ((x10.array.Array)(new x10.array.Array<x10.array.VarMat>((java.lang.System[]) null, x10.array.VarMat.$RTT).$init(((int)(r)))));
                                                                                                              
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.myMax = ((x10.array.Array)(x));
                                                                                                              
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> nSum =
                                                                                                                ((x10.array.Array)(new x10.array.Array<x10.array.VarMat>((java.lang.System[]) null, x10.array.VarMat.$RTT).$init(((int)(r)))));
                                                                                                              
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.minSum = ((x10.array.Array)(nSum));
                                                                                                              
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> xSum =
                                                                                                                ((x10.array.Array)(new x10.array.Array<x10.array.VarMat>((java.lang.System[]) null, x10.array.VarMat.$RTT).$init(((int)(r)))));
                                                                                                              
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.maxSum = ((x10.array.Array)(xSum));
                                                                                                              
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.Array<x10.array.PolyRow>> n2 =
                                                                                                                ((x10.array.Array)(new x10.array.Array<x10.array.Array<x10.array.PolyRow>>((java.lang.System[]) null, x10.rtt.ParameterizedType.make(x10.array.Array.$RTT, x10.array.PolyRow.$RTT)).$init(((int)(r)))));
                                                                                                              
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.min2 = ((x10.array.Array)(n2));
                                                                                                              
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.Array<x10.array.PolyRow>> x2 =
                                                                                                                ((x10.array.Array)(new x10.array.Array<x10.array.Array<x10.array.PolyRow>>((java.lang.System[]) null, x10.rtt.ParameterizedType.make(x10.array.Array.$RTT, x10.array.PolyRow.$RTT)).$init(((int)(r)))));
                                                                                                              
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.max2 = ((x10.array.Array)(x2));
                                                                                                              
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.core.Boolean> t43582 =
                                                                                                                ((x10.array.Array)(new x10.array.Array<x10.core.Boolean>((java.lang.System[]) null, x10.rtt.Types.BOOLEAN).$init(((int)(r)))));
                                                                                                              
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.parFlags = ((x10.array.Array)(t43582));
                                                                                                          }
                                                                                                          return this;
                                                                                                          }
        
        // constructor
        public x10.array.PolyScanner $init(final x10.array.PolyMat pm){return x10$array$PolyScanner$$init$S(pm);}
        
        
        
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
private void
                                                                                                      init(
                                                                                                      ){
            
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
x10.array.PolyMat pm =
              C;
            
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyMat t43584 =
              pm;
            
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43583 =
              rank;
            
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43585 =
              ((t43583) - (((int)(1))));
            
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.init(((x10.array.PolyMat)(t43584)),
                                                                                                                  (int)(t43585));
            
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44032 =
              rank;
            
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
int k44033 =
              ((t44032) - (((int)(2))));
            
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
for (;
                                                                                                             true;
                                                                                                             ) {
                
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44034 =
                  k44033;
                
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44035 =
                  ((t44034) >= (((int)(0))));
                
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (!(t44035)) {
                    
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
break;
                }
                
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyMat t44024 =
                  pm;
                
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44025 =
                  k44033;
                
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44026 =
                  ((t44025) + (((int)(1))));
                
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyMat t44027 =
                  ((x10.array.PolyMat)(t44024.eliminate((int)(t44026),
                                                        (boolean)(true))));
                
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
pm = t44027;
                
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyMat t44028 =
                  pm;
                
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44029 =
                  k44033;
                
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.init(((x10.array.PolyMat)(t44028)),
                                                                                                                      (int)(t44029));
                
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44030 =
                  k44033;
                
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44031 =
                  ((t44030) - (((int)(1))));
                
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
k44033 = t44031;
            }
        }
        
        public static void
          init$P(
          final x10.array.PolyScanner PolyScanner){
            PolyScanner.init();
        }
        
        
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final private void
                                                                                                      init(
                                                                                                      final x10.array.PolyMat pm,
                                                                                                      final int axis){
            
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
int imin =
              0;
            
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
int imax =
              0;
            
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.lang.Iterator<x10.array.PolyRow> r44096 =
              ((x10.array.Mat<x10.array.PolyRow>)pm).iterator();
            
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
for (;
                                                                                                             true;
                                                                                                             ) {
                
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44097 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)r44096).hasNext$O();
                
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (!(t44097)) {
                    
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
break;
                }
                
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyRow r44036 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)r44096).next$G();
                
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44037 =
                  r44036.$apply$O((int)(axis));
                
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44038 =
                  ((t44037) < (((int)(0))));
                
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (t44038) {
                    
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44039 =
                      imin;
                    
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44040 =
                      ((t44039) + (((int)(1))));
                    
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
imin = t44040;
                }
                
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44041 =
                  r44036.$apply$O((int)(axis));
                
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44042 =
                  ((t44041) > (((int)(0))));
                
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (t44042) {
                    
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44043 =
                      imax;
                    
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44044 =
                      ((t44043) + (((int)(1))));
                    
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
imax = t44044;
                }
            }
            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43608 =
              imin;
            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
boolean t43610 =
              ((int) t43608) ==
            ((int) 0);
            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (!(t43610)) {
                
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43609 =
                  imax;
                
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
t43610 = ((int) t43609) ==
                ((int) 0);
            }
            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t43617 =
              t43610;
            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (t43617) {
                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43611 =
                  imin;
                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t43612 =
                  ((int) t43611) ==
                ((int) 0);
                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
java.lang.String t43613 =
                   null;
                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (t43612) {
                    
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
t43613 = "minimum";
                } else {
                    
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
t43613 = "maximum";
                }
                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final java.lang.String m =
                  t43613;
                
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final java.lang.String t43614 =
                  (("axis ") + ((x10.core.Int.$box(axis))));
                
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final java.lang.String t43615 =
                  ((t43614) + (" has no "));
                
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final java.lang.String msg =
                  ((t43615) + (m));
                
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.UnboundedRegionException t43616 =
                  ((x10.array.UnboundedRegionException)(new x10.array.UnboundedRegionException(((java.lang.String)(msg)))));
                
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
throw t43616;
            }
            
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t43620 =
              ((x10.array.Array)(myMin));
            
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43618 =
              imin;
            
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43619 =
              ((axis) + (((int)(1))));
            
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t43621 =
              ((x10.array.VarMat)(new x10.array.VarMat((java.lang.System[]) null).$init(t43618,
                                                                                        t43619)));
            
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
((x10.array.Array<x10.array.VarMat>)t43620).$set__1x10$array$Array$$T$G((int)(axis),
                                                                                                                                                                                ((x10.array.VarMat)(t43621)));
            
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t43624 =
              ((x10.array.Array)(myMax));
            
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43622 =
              imax;
            
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43623 =
              ((axis) + (((int)(1))));
            
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t43625 =
              ((x10.array.VarMat)(new x10.array.VarMat((java.lang.System[]) null).$init(t43622,
                                                                                        t43623)));
            
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
((x10.array.Array<x10.array.VarMat>)t43624).$set__1x10$array$Array$$T$G((int)(axis),
                                                                                                                                                                                ((x10.array.VarMat)(t43625)));
            
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t43628 =
              ((x10.array.Array)(minSum));
            
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43626 =
              imin;
            
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43627 =
              ((axis) + (((int)(1))));
            
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t43629 =
              ((x10.array.VarMat)(new x10.array.VarMat((java.lang.System[]) null).$init(t43626,
                                                                                        t43627)));
            
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
((x10.array.Array<x10.array.VarMat>)t43628).$set__1x10$array$Array$$T$G((int)(axis),
                                                                                                                                                                                ((x10.array.VarMat)(t43629)));
            
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t43632 =
              ((x10.array.Array)(maxSum));
            
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43630 =
              imax;
            
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43631 =
              ((axis) + (((int)(1))));
            
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t43633 =
              ((x10.array.VarMat)(new x10.array.VarMat((java.lang.System[]) null).$init(t43630,
                                                                                        t43631)));
            
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
((x10.array.Array<x10.array.VarMat>)t43632).$set__1x10$array$Array$$T$G((int)(axis),
                                                                                                                                                                                ((x10.array.VarMat)(t43633)));
            
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.Array<x10.array.PolyRow>> t43635 =
              ((x10.array.Array)(min2));
            
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43634 =
              imin;
            
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.PolyRow> t43636 =
              ((x10.array.Array)(new x10.array.Array<x10.array.PolyRow>((java.lang.System[]) null, x10.array.PolyRow.$RTT).$init(t43634)));
            
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
((x10.array.Array<x10.array.Array<x10.array.PolyRow>>)t43635).$set__1x10$array$Array$$T$G((int)(axis),
                                                                                                                                                                                                  ((x10.array.Array)(t43636)));
            
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.Array<x10.array.PolyRow>> t43638 =
              ((x10.array.Array)(max2));
            
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43637 =
              imax;
            
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.PolyRow> t43639 =
              ((x10.array.Array)(new x10.array.Array<x10.array.PolyRow>((java.lang.System[]) null, x10.array.PolyRow.$RTT).$init(t43637)));
            
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
((x10.array.Array<x10.array.Array<x10.array.PolyRow>>)t43638).$set__1x10$array$Array$$T$G((int)(axis),
                                                                                                                                                                                                  ((x10.array.Array)(t43639)));
            
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
imin = 0;
            
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
imax = 0;
            
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.lang.Iterator<x10.array.PolyRow> r44098 =
              ((x10.array.Mat<x10.array.PolyRow>)pm).iterator();
            
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
for (;
                                                                                                             true;
                                                                                                             ) {
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44099 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)r44098).hasNext$O();
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (!(t44099)) {
                    
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
break;
                }
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyRow r44069 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)r44098).next$G();
                
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44070 =
                  r44069.$apply$O((int)(axis));
                
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44071 =
                  ((t44070) < (((int)(0))));
                
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (t44071) {
                    
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
int i44054 =
                      0;
                    
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
for (;
                                                                                                                     true;
                                                                                                                     ) {
                        
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44055 =
                          i44054;
                        
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44056 =
                          ((t44055) <= (((int)(axis))));
                        
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (!(t44056)) {
                            
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
break;
                        }
                        
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44045 =
                          ((x10.array.Array)(myMin));
                        
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44046 =
                          ((x10.array.Array<x10.array.VarMat>)t44045).$apply$G((int)(axis));
                        
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44047 =
                          imin;
                        
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarRow t44048 =
                          ((x10.array.VarRow)(((x10.array.Mat<x10.array.VarRow>)t44046).$apply$G((int)(t44047))));
                        
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44049 =
                          i44054;
                        
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44050 =
                          i44054;
                        
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44051 =
                          r44069.$apply$O((int)(t44050));
                        
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
t44048.$set$O((int)(t44049),
                                                                                                                                  (int)(t44051));
                        
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44052 =
                          i44054;
                        
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44053 =
                          ((t44052) + (((int)(1))));
                        
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
i44054 = t44053;
                    }
                    
//#line 155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44072 =
                      ((x10.array.Array)(minSum));
                    
//#line 155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44073 =
                      ((x10.array.Array<x10.array.VarMat>)t44072).$apply$G((int)(axis));
                    
//#line 155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44074 =
                      imin;
                    
//#line 155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarRow t44075 =
                      ((x10.array.VarRow)(((x10.array.Mat<x10.array.VarRow>)t44073).$apply$G((int)(t44074))));
                    
//#line 155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44076 =
                      rank;
                    
//#line 155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44077 =
                      r44069.$apply$O((int)(t44076));
                    
//#line 155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
t44075.$set$O((int)(0),
                                                                                                                              (int)(t44077));
                    
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.Array<x10.array.PolyRow>> t44078 =
                      ((x10.array.Array)(min2));
                    
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.PolyRow> t44079 =
                      ((x10.array.Array)(((x10.array.Array<x10.array.Array<x10.array.PolyRow>>)t44078).$apply$G((int)(axis))));
                    
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44080 =
                      imin;
                    
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
((x10.array.Array<x10.array.PolyRow>)t44079).$set__1x10$array$Array$$T$G((int)(t44080),
                                                                                                                                                                                         ((x10.array.PolyRow)(r44069)));
                    
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44081 =
                      imin;
                    
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44082 =
                      ((t44081) + (((int)(1))));
                    
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
imin = t44082;
                }
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44083 =
                  r44069.$apply$O((int)(axis));
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44084 =
                  ((t44083) > (((int)(0))));
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (t44084) {
                    
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
int i44066 =
                      0;
                    
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
for (;
                                                                                                                     true;
                                                                                                                     ) {
                        
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44067 =
                          i44066;
                        
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44068 =
                          ((t44067) <= (((int)(axis))));
                        
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (!(t44068)) {
                            
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
break;
                        }
                        
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44057 =
                          ((x10.array.Array)(myMax));
                        
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44058 =
                          ((x10.array.Array<x10.array.VarMat>)t44057).$apply$G((int)(axis));
                        
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44059 =
                          imax;
                        
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarRow t44060 =
                          ((x10.array.VarRow)(((x10.array.Mat<x10.array.VarRow>)t44058).$apply$G((int)(t44059))));
                        
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44061 =
                          i44066;
                        
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44062 =
                          i44066;
                        
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44063 =
                          r44069.$apply$O((int)(t44062));
                        
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
t44060.$set$O((int)(t44061),
                                                                                                                                  (int)(t44063));
                        
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44064 =
                          i44066;
                        
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44065 =
                          ((t44064) + (((int)(1))));
                        
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
i44066 = t44065;
                    }
                    
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44085 =
                      ((x10.array.Array)(maxSum));
                    
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44086 =
                      ((x10.array.Array<x10.array.VarMat>)t44085).$apply$G((int)(axis));
                    
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44087 =
                      imax;
                    
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarRow t44088 =
                      ((x10.array.VarRow)(((x10.array.Mat<x10.array.VarRow>)t44086).$apply$G((int)(t44087))));
                    
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44089 =
                      rank;
                    
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44090 =
                      r44069.$apply$O((int)(t44089));
                    
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
t44088.$set$O((int)(0),
                                                                                                                              (int)(t44090));
                    
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.Array<x10.array.PolyRow>> t44091 =
                      ((x10.array.Array)(max2));
                    
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.PolyRow> t44092 =
                      ((x10.array.Array)(((x10.array.Array<x10.array.Array<x10.array.PolyRow>>)t44091).$apply$G((int)(axis))));
                    
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44093 =
                      imax;
                    
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
((x10.array.Array<x10.array.PolyRow>)t44092).$set__1x10$array$Array$$T$G((int)(t44093),
                                                                                                                                                                                         ((x10.array.PolyRow)(r44069)));
                    
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44094 =
                      imax;
                    
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44095 =
                      ((t44094) + (((int)(1))));
                    
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
imax = t44095;
                }
            }
        }
        
        final public static void
          init$P(
          final x10.array.PolyMat pm,
          final int axis,
          final x10.array.PolyScanner PolyScanner){
            PolyScanner.init(((x10.array.PolyMat)(pm)),
                             (int)(axis));
        }
        
        
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final public void
                                                                                                      $set(
                                                                                                      final int v,
                                                                                                      final int axis){
            
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.set((int)(axis),
                                                                                                                 (int)(v));
        }
        
        
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final public void
                                                                                                      set(
                                                                                                      final int axis,
                                                                                                      final int v){
            
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
int k44162 =
              ((axis) + (((int)(1))));
            
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
for (;
                                                                                                             true;
                                                                                                             ) {
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44163 =
                  k44162;
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44164 =
                  rank;
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44165 =
                  ((t44163) < (((int)(t44164))));
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (!(t44165)) {
                    
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
break;
                }
                
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
int l44122 =
                  0;
                
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
for (;
                                                                                                                 true;
                                                                                                                 ) {
                    
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44123 =
                      l44122;
                    
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44124 =
                      ((x10.array.Array)(minSum));
                    
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44125 =
                      k44162;
                    
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44126 =
                      ((x10.array.Array<x10.array.VarMat>)t44124).$apply$G((int)(t44125));
                    
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44127 =
                      ((x10.array.Mat<x10.array.VarRow>)t44126).
                        rows;
                    
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44128 =
                      ((t44123) < (((int)(t44127))));
                    
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (!(t44128)) {
                        
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
break;
                    }
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44100 =
                      ((x10.array.Array)(minSum));
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44101 =
                      k44162;
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44102 =
                      ((x10.array.Array<x10.array.VarMat>)t44100).$apply$G((int)(t44101));
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44103 =
                      l44122;
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarRow t44104 =
                      ((x10.array.VarRow)(((x10.array.Mat<x10.array.VarRow>)t44102).$apply$G((int)(t44103))));
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44105 =
                      ((axis) + (((int)(1))));
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44106 =
                      ((x10.array.Array)(myMin));
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44107 =
                      k44162;
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44108 =
                      ((x10.array.Array<x10.array.VarMat>)t44106).$apply$G((int)(t44107));
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44109 =
                      l44122;
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarRow t44110 =
                      ((x10.array.VarRow)(((x10.array.Mat<x10.array.VarRow>)t44108).$apply$G((int)(t44109))));
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44111 =
                      t44110.$apply$O((int)(axis));
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44112 =
                      ((t44111) * (((int)(v))));
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44113 =
                      ((x10.array.Array)(minSum));
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44114 =
                      k44162;
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44115 =
                      ((x10.array.Array<x10.array.VarMat>)t44113).$apply$G((int)(t44114));
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44116 =
                      l44122;
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarRow t44117 =
                      ((x10.array.VarRow)(((x10.array.Mat<x10.array.VarRow>)t44115).$apply$G((int)(t44116))));
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44118 =
                      t44117.$apply$O((int)(axis));
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44119 =
                      ((t44112) + (((int)(t44118))));
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
t44104.$set$O((int)(t44105),
                                                                                                                              (int)(t44119));
                    
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44120 =
                      l44122;
                    
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44121 =
                      ((t44120) + (((int)(1))));
                    
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
l44122 = t44121;
                }
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44129 =
                  k44162;
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44130 =
                  ((t44129) + (((int)(1))));
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
k44162 = t44130;
            }
            
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
int k44166 =
              ((axis) + (((int)(1))));
            
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
for (;
                                                                                                             true;
                                                                                                             ) {
                
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44167 =
                  k44166;
                
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44168 =
                  rank;
                
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44169 =
                  ((t44167) < (((int)(t44168))));
                
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (!(t44169)) {
                    
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
break;
                }
                
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
int l44153 =
                  0;
                
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
for (;
                                                                                                                 true;
                                                                                                                 ) {
                    
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44154 =
                      l44153;
                    
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44155 =
                      ((x10.array.Array)(maxSum));
                    
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44156 =
                      k44166;
                    
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44157 =
                      ((x10.array.Array<x10.array.VarMat>)t44155).$apply$G((int)(t44156));
                    
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44158 =
                      ((x10.array.Mat<x10.array.VarRow>)t44157).
                        rows;
                    
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44159 =
                      ((t44154) < (((int)(t44158))));
                    
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (!(t44159)) {
                        
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
break;
                    }
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44131 =
                      ((x10.array.Array)(maxSum));
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44132 =
                      k44166;
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44133 =
                      ((x10.array.Array<x10.array.VarMat>)t44131).$apply$G((int)(t44132));
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44134 =
                      l44153;
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarRow t44135 =
                      ((x10.array.VarRow)(((x10.array.Mat<x10.array.VarRow>)t44133).$apply$G((int)(t44134))));
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44136 =
                      ((axis) + (((int)(1))));
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44137 =
                      ((x10.array.Array)(myMax));
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44138 =
                      k44166;
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44139 =
                      ((x10.array.Array<x10.array.VarMat>)t44137).$apply$G((int)(t44138));
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44140 =
                      l44153;
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarRow t44141 =
                      ((x10.array.VarRow)(((x10.array.Mat<x10.array.VarRow>)t44139).$apply$G((int)(t44140))));
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44142 =
                      t44141.$apply$O((int)(axis));
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44143 =
                      ((t44142) * (((int)(v))));
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44144 =
                      ((x10.array.Array)(maxSum));
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44145 =
                      k44166;
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44146 =
                      ((x10.array.Array<x10.array.VarMat>)t44144).$apply$G((int)(t44145));
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44147 =
                      l44153;
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarRow t44148 =
                      ((x10.array.VarRow)(((x10.array.Mat<x10.array.VarRow>)t44146).$apply$G((int)(t44147))));
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44149 =
                      t44148.$apply$O((int)(axis));
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44150 =
                      ((t44143) + (((int)(t44149))));
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
t44135.$set$O((int)(t44136),
                                                                                                                              (int)(t44150));
                    
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44151 =
                      l44153;
                    
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44152 =
                      ((t44151) + (((int)(1))));
                    
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
l44153 = t44152;
                }
                
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44160 =
                  k44166;
                
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44161 =
                  ((t44160) + (((int)(1))));
                
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
k44166 = t44161;
            }
        }
        
        
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final public int
                                                                                                      min$O(
                                                                                                      final int axis){
            
//#line 190 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
int result =
              java.lang.Integer.MIN_VALUE;
            
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
int k44194 =
              0;
            
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
for (;
                                                                                                             true;
                                                                                                             ) {
                
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44195 =
                  k44194;
                
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44196 =
                  ((x10.array.Array)(myMin));
                
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44197 =
                  ((x10.array.Array<x10.array.VarMat>)t44196).$apply$G((int)(axis));
                
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44198 =
                  ((x10.array.Mat<x10.array.VarRow>)t44197).
                    rows;
                
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44199 =
                  ((t44195) < (((int)(t44198))));
                
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (!(t44199)) {
                    
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
break;
                }
                
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44170 =
                  ((x10.array.Array)(myMin));
                
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44171 =
                  ((x10.array.Array<x10.array.VarMat>)t44170).$apply$G((int)(axis));
                
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44172 =
                  k44194;
                
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarRow t44173 =
                  ((x10.array.VarRow)(((x10.array.Mat<x10.array.VarRow>)t44171).$apply$G((int)(t44172))));
                
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int a44174 =
                  t44173.$apply$O((int)(axis));
                
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44175 =
                  ((x10.array.Array)(minSum));
                
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44176 =
                  ((x10.array.Array<x10.array.VarMat>)t44175).$apply$G((int)(axis));
                
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44177 =
                  k44194;
                
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarRow t44178 =
                  ((x10.array.VarRow)(((x10.array.Mat<x10.array.VarRow>)t44176).$apply$G((int)(t44177))));
                
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
int b44179 =
                  t44178.$apply$O((int)(axis));
                
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44180 =
                  b44179;
                
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44181 =
                  ((t44180) > (((int)(0))));
                
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
int t44182 =
                   0;
                
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (t44181) {
                    
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44183 =
                      b44179;
                    
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44184 =
                      (-(t44183));
                    
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44185 =
                      ((t44184) + (((int)(a44174))));
                    
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44186 =
                      ((t44185) + (((int)(1))));
                    
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
t44182 = ((t44186) / (((int)(a44174))));
                } else {
                    
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44187 =
                      b44179;
                    
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44188 =
                      (-(t44187));
                    
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
t44182 = ((t44188) / (((int)(a44174))));
                }
                
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int m44189 =
                  t44182;
                
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44190 =
                  result;
                
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44191 =
                  ((m44189) > (((int)(t44190))));
                
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (t44191) {
                    
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
result = m44189;
                }
                
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44192 =
                  k44194;
                
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44193 =
                  ((t44192) + (((int)(1))));
                
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
k44194 = t44193;
            }
            
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43789 =
              result;
            
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
return t43789;
        }
        
        
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final public int
                                                                                                      max$O(
                                                                                                      final int axis){
            
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
int result =
              java.lang.Integer.MAX_VALUE;
            
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
int k44221 =
              0;
            
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
for (;
                                                                                                             true;
                                                                                                             ) {
                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44222 =
                  k44221;
                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44223 =
                  ((x10.array.Array)(myMax));
                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44224 =
                  ((x10.array.Array<x10.array.VarMat>)t44223).$apply$G((int)(axis));
                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44225 =
                  ((x10.array.Mat<x10.array.VarRow>)t44224).
                    rows;
                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44226 =
                  ((t44222) < (((int)(t44225))));
                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (!(t44226)) {
                    
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
break;
                }
                
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44200 =
                  ((x10.array.Array)(myMax));
                
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44201 =
                  ((x10.array.Array<x10.array.VarMat>)t44200).$apply$G((int)(axis));
                
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44202 =
                  k44221;
                
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarRow t44203 =
                  ((x10.array.VarRow)(((x10.array.Mat<x10.array.VarRow>)t44201).$apply$G((int)(t44202))));
                
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int a44204 =
                  t44203.$apply$O((int)(axis));
                
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44205 =
                  ((x10.array.Array)(maxSum));
                
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44206 =
                  ((x10.array.Array<x10.array.VarMat>)t44205).$apply$G((int)(axis));
                
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44207 =
                  k44221;
                
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarRow t44208 =
                  ((x10.array.VarRow)(((x10.array.Mat<x10.array.VarRow>)t44206).$apply$G((int)(t44207))));
                
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int b44209 =
                  t44208.$apply$O((int)(axis));
                
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44210 =
                  ((b44209) > (((int)(0))));
                
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
int t44211 =
                   0;
                
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (t44210) {
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44212 =
                      (-(b44209));
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44213 =
                      ((t44212) - (((int)(a44204))));
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44214 =
                      ((t44213) + (((int)(1))));
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
t44211 = ((t44214) / (((int)(a44204))));
                } else {
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44215 =
                      (-(b44209));
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
t44211 = ((t44215) / (((int)(a44204))));
                }
                
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int m44216 =
                  t44211;
                
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44217 =
                  result;
                
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44218 =
                  ((m44216) < (((int)(t44217))));
                
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (t44218) {
                    
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
result = m44216;
                }
                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44219 =
                  k44221;
                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44220 =
                  ((t44219) + (((int)(1))));
                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
k44221 = t44220;
            }
            
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43814 =
              result;
            
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
return t43814;
        }
        
        
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
@x10.core.X10Generated final public static class RailIt extends x10.core.Ref implements x10.lang.Iterator, x10.x10rt.X10JavaSerializable
                                                                                                    {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, RailIt.class);
            
            public static final x10.rtt.RuntimeType<RailIt> $RTT = x10.rtt.NamedType.<RailIt> make(
            "x10.array.PolyScanner.RailIt", /* base class */RailIt.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Iterator.$RTT, x10.rtt.ParameterizedType.make(x10.array.Array.$RTT, x10.rtt.Types.INT)), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(RailIt $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + RailIt.class + " calling"); } 
                $_obj.rank = $deserializer.readInt();
                x10.array.PolyScanner s = (x10.array.PolyScanner) $deserializer.readRef();
                $_obj.s = s;
                x10.array.Array x = (x10.array.Array) $deserializer.readRef();
                $_obj.x = x;
                x10.array.Array myMin = (x10.array.Array) $deserializer.readRef();
                $_obj.myMin = myMin;
                x10.array.Array myMax = (x10.array.Array) $deserializer.readRef();
                $_obj.myMax = myMax;
                $_obj.k = $deserializer.readInt();
                $_obj.doesHaveNext = $deserializer.readBoolean();
                x10.array.PolyScanner out$ = (x10.array.PolyScanner) $deserializer.readRef();
                $_obj.out$ = out$;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                RailIt $_obj = new RailIt((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write(this.rank);
                if (s instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.s);
                } else {
                $serializer.write(this.s);
                }
                if (x instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.x);
                } else {
                $serializer.write(this.x);
                }
                if (myMin instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.myMin);
                } else {
                $serializer.write(this.myMin);
                }
                if (myMax instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.myMax);
                } else {
                $serializer.write(this.myMax);
                }
                $serializer.write(this.k);
                $serializer.write(this.doesHaveNext);
                if (out$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$);
                } else {
                $serializer.write(this.out$);
                }
                
            }
            
            // constructor just for allocation
            public RailIt(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // bridge for method abstract public x10.lang.Iterator.next():T
            final public x10.array.Array
              next$G(){return next();}
            
                
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
public x10.array.PolyScanner out$;
                
//#line 233 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
public int rank;
                
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
public x10.array.PolyScanner s;
                
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
public x10.array.Array<x10.core.Int> x;
                
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
public x10.array.Array<x10.core.Int> myMin;
                
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
public x10.array.Array<x10.core.Int> myMax;
                
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
public int k;
                
//#line 241 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
public boolean doesHaveNext;
                
                
//#line 242 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
// creation method for java code (1-phase java constructor)
                public RailIt(final x10.array.PolyScanner out$){this((java.lang.System[]) null);
                                                                    $init(out$);}
                
                // constructor for non-virtual call
                final public x10.array.PolyScanner.RailIt x10$array$PolyScanner$RailIt$$init$S(final x10.array.PolyScanner out$) { {
                                                                                                                                          
//#line 242 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"

                                                                                                                                          
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.out$ = out$;
                                                                                                                                          
//#line 242 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"

                                                                                                                                          
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.__fieldInitializers42911();
                                                                                                                                          
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.core.Int> t43816 =
                                                                                                                                            ((x10.array.Array)(myMin));
                                                                                                                                          
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyScanner t43815 =
                                                                                                                                            ((x10.array.PolyScanner)(s));
                                                                                                                                          
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43817 =
                                                                                                                                            t43815.min$O((int)(0));
                                                                                                                                          
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
((x10.array.Array<x10.core.Int>)t43816).$set__1x10$array$Array$$T$G((int)(0),
                                                                                                                                                                                                                                                                                                          x10.core.Int.$box(t43817));
                                                                                                                                          
//#line 244 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.core.Int> t43819 =
                                                                                                                                            ((x10.array.Array)(myMax));
                                                                                                                                          
//#line 244 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyScanner t43818 =
                                                                                                                                            ((x10.array.PolyScanner)(s));
                                                                                                                                          
//#line 244 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43820 =
                                                                                                                                            t43818.max$O((int)(0));
                                                                                                                                          
//#line 244 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
((x10.array.Array<x10.core.Int>)t43819).$set__1x10$array$Array$$T$G((int)(0),
                                                                                                                                                                                                                                                                                                          x10.core.Int.$box(t43820));
                                                                                                                                          
//#line 245 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.core.Int> t43822 =
                                                                                                                                            ((x10.array.Array)(x));
                                                                                                                                          
//#line 245 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyScanner t43821 =
                                                                                                                                            ((x10.array.PolyScanner)(s));
                                                                                                                                          
//#line 245 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43823 =
                                                                                                                                            t43821.min$O((int)(0));
                                                                                                                                          
//#line 245 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
((x10.array.Array<x10.core.Int>)t43822).$set__1x10$array$Array$$T$G((int)(0),
                                                                                                                                                                                                                                                                                                          x10.core.Int.$box(t43823));
                                                                                                                                          
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.k = 1;
                                                                                                                                          
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
for (;
                                                                                                                                                                                                                                           true;
                                                                                                                                                                                                                                           ) {
                                                                                                                                              
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44249 =
                                                                                                                                                k;
                                                                                                                                              
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44250 =
                                                                                                                                                rank;
                                                                                                                                              
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44251 =
                                                                                                                                                ((t44249) < (((int)(t44250))));
                                                                                                                                              
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (!(t44251)) {
                                                                                                                                                  
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
break;
                                                                                                                                              }
                                                                                                                                              
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyScanner t44227 =
                                                                                                                                                ((x10.array.PolyScanner)(s));
                                                                                                                                              
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44228 =
                                                                                                                                                k;
                                                                                                                                              
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44229 =
                                                                                                                                                ((t44228) - (((int)(1))));
                                                                                                                                              
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.core.Int> t44230 =
                                                                                                                                                ((x10.array.Array)(x));
                                                                                                                                              
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44231 =
                                                                                                                                                k;
                                                                                                                                              
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44232 =
                                                                                                                                                ((t44231) - (((int)(1))));
                                                                                                                                              
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44233 =
                                                                                                                                                x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)t44230).$apply$G((int)(t44232)));
                                                                                                                                              
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
t44227.set((int)(t44229),
                                                                                                                                                                                                                                                     (int)(t44233));
                                                                                                                                              
//#line 248 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyScanner t44234 =
                                                                                                                                                ((x10.array.PolyScanner)(s));
                                                                                                                                              
//#line 248 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44235 =
                                                                                                                                                k;
                                                                                                                                              
//#line 248 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int m44236 =
                                                                                                                                                t44234.min$O((int)(t44235));
                                                                                                                                              
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.core.Int> t44237 =
                                                                                                                                                ((x10.array.Array)(x));
                                                                                                                                              
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44238 =
                                                                                                                                                k;
                                                                                                                                              
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
((x10.array.Array<x10.core.Int>)t44237).$set__1x10$array$Array$$T$G((int)(t44238),
                                                                                                                                                                                                                                                                                                              x10.core.Int.$box(m44236));
                                                                                                                                              
//#line 250 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.core.Int> t44239 =
                                                                                                                                                ((x10.array.Array)(myMin));
                                                                                                                                              
//#line 250 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44240 =
                                                                                                                                                k;
                                                                                                                                              
//#line 250 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
((x10.array.Array<x10.core.Int>)t44239).$set__1x10$array$Array$$T$G((int)(t44240),
                                                                                                                                                                                                                                                                                                              x10.core.Int.$box(m44236));
                                                                                                                                              
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.core.Int> t44241 =
                                                                                                                                                ((x10.array.Array)(myMax));
                                                                                                                                              
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44242 =
                                                                                                                                                k;
                                                                                                                                              
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyScanner t44243 =
                                                                                                                                                ((x10.array.PolyScanner)(s));
                                                                                                                                              
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44244 =
                                                                                                                                                k;
                                                                                                                                              
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44245 =
                                                                                                                                                t44243.max$O((int)(t44244));
                                                                                                                                              
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
((x10.array.Array<x10.core.Int>)t44241).$set__1x10$array$Array$$T$G((int)(t44242),
                                                                                                                                                                                                                                                                                                              x10.core.Int.$box(t44245));
                                                                                                                                              
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyScanner.RailIt x44246 =
                                                                                                                                                ((x10.array.PolyScanner.RailIt)(this));
                                                                                                                                              
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
;
                                                                                                                                              
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44247 =
                                                                                                                                                x44246.
                                                                                                                                                  k;
                                                                                                                                              
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44248 =
                                                                                                                                                ((t44247) + (((int)(1))));
                                                                                                                                              
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
x44246.k = t44248;
                                                                                                                                          }
                                                                                                                                          
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.core.Int> x43561 =
                                                                                                                                            ((x10.array.Array)(x));
                                                                                                                                          
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43848 =
                                                                                                                                            rank;
                                                                                                                                          
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int y43562 =
                                                                                                                                            ((t43848) - (((int)(1))));
                                                                                                                                          
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
;
                                                                                                                                          
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
int ret43565 =
                                                                                                                                             0;
                                                                                                                                          
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44252 =
                                                                                                                                            x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)x43561).$apply$G((int)(y43562)));
                                                                                                                                          
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int r44253 =
                                                                                                                                            ((t44252) - (((int)(1))));
                                                                                                                                          
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
((x10.array.Array<x10.core.Int>)x43561).$set__1x10$array$Array$$T$G((int)(y43562),
                                                                                                                                                                                                                                                                                                          x10.core.Int.$box(r44253));
                                                                                                                                          
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
ret43565 = r44253;
                                                                                                                                          
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.checkHasNext();
                                                                                                                                      }
                                                                                                                                      return this;
                                                                                                                                      }
                
                // constructor
                public x10.array.PolyScanner.RailIt $init(final x10.array.PolyScanner out$){return x10$array$PolyScanner$RailIt$$init$S(out$);}
                
                
                
//#line 257 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
public boolean
                                                                                                              hasNext$O(
                                                                                                              ){
                    
//#line 257 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t43850 =
                      doesHaveNext;
                    
//#line 257 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
return t43850;
                }
                
                
//#line 259 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
private void
                                                                                                              checkHasNext(
                                                                                                              ){
                    
//#line 260 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43851 =
                      rank;
                    
//#line 260 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43852 =
                      ((t43851) - (((int)(1))));
                    
//#line 260 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.k = t43852;
                    
//#line 261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
while (true) {
                        
//#line 261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.core.Int> t43853 =
                          ((x10.array.Array)(x));
                        
//#line 261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43854 =
                          k;
                        
//#line 261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43857 =
                          x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)t43853).$apply$G((int)(t43854)));
                        
//#line 261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.core.Int> t43855 =
                          ((x10.array.Array)(myMax));
                        
//#line 261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43856 =
                          k;
                        
//#line 261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43858 =
                          x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)t43855).$apply$G((int)(t43856)));
                        
//#line 261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t43863 =
                          ((t43857) >= (((int)(t43858))));
                        
//#line 261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (!(t43863)) {
                            
//#line 261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
break;
                        }
                        
//#line 262 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyScanner.RailIt x44254 =
                          ((x10.array.PolyScanner.RailIt)(this));
                        
//#line 262 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
;
                        
//#line 262 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44255 =
                          x44254.
                            k;
                        
//#line 262 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44256 =
                          ((t44255) - (((int)(1))));
                        
//#line 262 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44257 =
                          x44254.k = t44256;
                        
//#line 262 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44258 =
                          ((t44257) < (((int)(0))));
                        
//#line 262 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (t44258) {
                            
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.doesHaveNext = false;
                            
//#line 264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
return;
                        }
                    }
                    
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.doesHaveNext = true;
                }
                
                public static void
                  checkHasNext$P(
                  final x10.array.PolyScanner.RailIt RailIt){
                    RailIt.checkHasNext();
                }
                
                
//#line 270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final public x10.array.Array<x10.core.Int>
                                                                                                              next(
                                                                                                              ){
                    
//#line 271 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.core.Int> x43569 =
                      ((x10.array.Array)(x));
                    
//#line 271 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int y43570 =
                      k;
                    
//#line 271 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
;
                    
//#line 271 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
int ret43573 =
                       0;
                    
//#line 271 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44281 =
                      x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)x43569).$apply$G((int)(y43570)));
                    
//#line 271 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int r44282 =
                      ((t44281) + (((int)(1))));
                    
//#line 271 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
((x10.array.Array<x10.core.Int>)x43569).$set__1x10$array$Array$$T$G((int)(y43570),
                                                                                                                                                                                    x10.core.Int.$box(r44282));
                    
//#line 271 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
ret43573 = r44282;
                    
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44283 =
                      k;
                    
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44284 =
                      ((t44283) + (((int)(1))));
                    
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.k = t44284;
                    
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
for (;
                                                                                                                     true;
                                                                                                                     ) {
                        
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44285 =
                          k;
                        
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44286 =
                          rank;
                        
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44287 =
                          ((t44285) < (((int)(t44286))));
                        
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (!(t44287)) {
                            
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
break;
                        }
                        
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyScanner t44259 =
                          ((x10.array.PolyScanner)(s));
                        
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44260 =
                          k;
                        
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44261 =
                          ((t44260) - (((int)(1))));
                        
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.core.Int> t44262 =
                          ((x10.array.Array)(x));
                        
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44263 =
                          k;
                        
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44264 =
                          ((t44263) - (((int)(1))));
                        
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44265 =
                          x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)t44262).$apply$G((int)(t44264)));
                        
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
t44259.set((int)(t44261),
                                                                                                                               (int)(t44265));
                        
//#line 274 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyScanner t44266 =
                          ((x10.array.PolyScanner)(s));
                        
//#line 274 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44267 =
                          k;
                        
//#line 274 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int m44268 =
                          t44266.min$O((int)(t44267));
                        
//#line 275 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.core.Int> t44269 =
                          ((x10.array.Array)(x));
                        
//#line 275 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44270 =
                          k;
                        
//#line 275 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
((x10.array.Array<x10.core.Int>)t44269).$set__1x10$array$Array$$T$G((int)(t44270),
                                                                                                                                                                                        x10.core.Int.$box(m44268));
                        
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.core.Int> t44271 =
                          ((x10.array.Array)(myMin));
                        
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44272 =
                          k;
                        
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
((x10.array.Array<x10.core.Int>)t44271).$set__1x10$array$Array$$T$G((int)(t44272),
                                                                                                                                                                                        x10.core.Int.$box(m44268));
                        
//#line 277 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.core.Int> t44273 =
                          ((x10.array.Array)(myMax));
                        
//#line 277 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44274 =
                          k;
                        
//#line 277 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyScanner t44275 =
                          ((x10.array.PolyScanner)(s));
                        
//#line 277 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44276 =
                          k;
                        
//#line 277 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44277 =
                          t44275.max$O((int)(t44276));
                        
//#line 277 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
((x10.array.Array<x10.core.Int>)t44273).$set__1x10$array$Array$$T$G((int)(t44274),
                                                                                                                                                                                        x10.core.Int.$box(t44277));
                        
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyScanner.RailIt x44278 =
                          ((x10.array.PolyScanner.RailIt)(this));
                        
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
;
                        
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44279 =
                          x44278.
                            k;
                        
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44280 =
                          ((t44279) + (((int)(1))));
                        
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
x44278.k = t44280;
                    }
                    
//#line 279 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.checkHasNext();
                    
//#line 280 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.core.Int> t43891 =
                      ((x10.array.Array)(x));
                    
//#line 280 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
return t43891;
                }
                
                
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
public void
                                                                                                              remove(
                                                                                                              ){
                    
                }
                
                
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final public x10.array.PolyScanner.RailIt
                                                                                                              x10$array$PolyScanner$RailIt$$x10$array$PolyScanner$RailIt$this(
                                                                                                              ){
                    
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
return x10.array.PolyScanner.RailIt.this;
                }
                
                
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final public x10.array.PolyScanner
                                                                                                              x10$array$PolyScanner$RailIt$$x10$array$PolyScanner$this(
                                                                                                              ){
                    
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyScanner t43892 =
                      this.
                        out$;
                    
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
return t43892;
                }
                
                
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final public void
                                                                                                              __fieldInitializers42911(
                                                                                                              ){
                    
//#line 233 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyScanner t43893 =
                      this.
                        out$;
                    
//#line 233 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43894 =
                      t43893.
                        rank;
                    
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.rank = t43894;
                    
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyScanner t43895 =
                      this.
                        out$;
                    
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.s = ((x10.array.PolyScanner)(t43895));
                    
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43896 =
                      rank;
                    
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.core.Int> t43897 =
                      ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t43896)))));
                    
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.x = ((x10.array.Array)(t43897));
                    
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43898 =
                      rank;
                    
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.core.Int> t43899 =
                      ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t43898)))));
                    
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.myMin = ((x10.array.Array)(t43899));
                    
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43900 =
                      rank;
                    
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.core.Int> t43901 =
                      ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t43900)))));
                    
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.myMax = ((x10.array.Array)(t43901));
                    
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.k = 0;
                    
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.doesHaveNext = false;
                }
            
        }
        
        
        
//#line 293 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
public x10.lang.Iterator<x10.array.Point>
                                                                                                      iterator(
                                                                                                      ){
            
//#line 293 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyScanner.Anonymous$9371 t43902 =
              ((x10.array.PolyScanner.Anonymous$9371)(new x10.array.PolyScanner.Anonymous$9371((java.lang.System[]) null).$init(this)));
            
//#line 293 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
return t43902;
        }
        
        
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
public void
                                                                                                      printInfo(
                                                                                                      final x10.io.Printer ps){
            
//#line 307 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
ps.println(((java.lang.String)("PolyScanner")));
            
//#line 308 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyMat t43903 =
              ((x10.array.PolyMat)(C));
            
//#line 308 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
((x10.array.Mat<x10.array.PolyRow>)t43903).printInfo(((x10.io.Printer)(ps)),
                                                                                                                                                             ((java.lang.String)("  C")));
        }
        
        
//#line 311 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
public void
                                                                                                      printInfo2(
                                                                                                      final x10.io.Printer ps){
            
//#line 312 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
int k =
              0;
            
//#line 312 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
for (;
                                                                                                             true;
                                                                                                             ) {
                
//#line 312 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43906 =
                  k;
                
//#line 312 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t43905 =
                  ((x10.array.Array)(myMin));
                
//#line 312 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t43907 =
                  ((x10.array.Array<x10.array.VarMat>)t43905).
                    size;
                
//#line 312 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44006 =
                  ((t43906) < (((int)(t43907))));
                
//#line 312 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (!(t44006)) {
                    
//#line 312 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
break;
                }
                
//#line 313 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44382 =
                  k;
                
//#line 313 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final java.lang.String t44383 =
                  (("axis ") + ((x10.core.Int.$box(t44382))));
                
//#line 313 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
ps.println(((java.lang.String)(t44383)));
                
//#line 314 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
ps.println(((java.lang.String)("  min")));
                
//#line 315 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
int l44368 =
                  0;
                
//#line 315 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
for (;
                                                                                                                 true;
                                                                                                                 ) {
                    
//#line 315 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44369 =
                      l44368;
                    
//#line 315 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44370 =
                      ((x10.array.Array)(myMin));
                    
//#line 315 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44371 =
                      k;
                    
//#line 315 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44372 =
                      ((x10.array.Array<x10.array.VarMat>)t44370).$apply$G((int)(t44371));
                    
//#line 315 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44373 =
                      ((x10.array.Mat<x10.array.VarRow>)t44372).
                        rows;
                    
//#line 315 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44374 =
                      ((t44369) < (((int)(t44373))));
                    
//#line 315 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (!(t44374)) {
                        
//#line 315 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
break;
                    }
                    
//#line 316 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
ps.print(((java.lang.String)("  ")));
                    
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
int m44308 =
                      0;
                    
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
for (;
                                                                                                                     true;
                                                                                                                     ) {
                        
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44309 =
                          m44308;
                        
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44310 =
                          ((x10.array.Array)(myMin));
                        
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44311 =
                          k;
                        
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44312 =
                          ((x10.array.Array<x10.array.VarMat>)t44310).$apply$G((int)(t44311));
                        
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44313 =
                          l44368;
                        
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarRow t44314 =
                          ((x10.array.VarRow)(((x10.array.Mat<x10.array.VarRow>)t44312).$apply$G((int)(t44313))));
                        
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44315 =
                          t44314.
                            cols;
                        
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44316 =
                          ((t44309) < (((int)(t44315))));
                        
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (!(t44316)) {
                            
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
break;
                        }
                        
//#line 318 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44288 =
                          ((x10.array.Array)(myMin));
                        
//#line 318 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44289 =
                          k;
                        
//#line 318 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44290 =
                          ((x10.array.Array<x10.array.VarMat>)t44288).$apply$G((int)(t44289));
                        
//#line 318 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44291 =
                          l44368;
                        
//#line 318 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarRow t44292 =
                          ((x10.array.VarRow)(((x10.array.Mat<x10.array.VarRow>)t44290).$apply$G((int)(t44291))));
                        
//#line 318 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44293 =
                          m44308;
                        
//#line 318 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44294 =
                          t44292.$apply$O((int)(t44293));
                        
//#line 318 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final java.lang.String t44295 =
                          ((" ") + ((x10.core.Int.$box(t44294))));
                        
//#line 318 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
ps.print(((java.lang.String)(t44295)));
                        
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44296 =
                          m44308;
                        
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44297 =
                          ((t44296) + (((int)(1))));
                        
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
m44308 = t44297;
                    }
                    
//#line 319 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
ps.print(((java.lang.String)("  sum")));
                    
//#line 320 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
int m44317 =
                      0;
                    
//#line 320 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
for (;
                                                                                                                     true;
                                                                                                                     ) {
                        
//#line 320 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44318 =
                          m44317;
                        
//#line 320 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44319 =
                          ((x10.array.Array)(minSum));
                        
//#line 320 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44320 =
                          k;
                        
//#line 320 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44321 =
                          ((x10.array.Array<x10.array.VarMat>)t44319).$apply$G((int)(t44320));
                        
//#line 320 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44322 =
                          l44368;
                        
//#line 320 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarRow t44323 =
                          ((x10.array.VarRow)(((x10.array.Mat<x10.array.VarRow>)t44321).$apply$G((int)(t44322))));
                        
//#line 320 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44324 =
                          t44323.
                            cols;
                        
//#line 320 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44325 =
                          ((t44318) < (((int)(t44324))));
                        
//#line 320 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (!(t44325)) {
                            
//#line 320 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
break;
                        }
                        
//#line 321 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44298 =
                          ((x10.array.Array)(minSum));
                        
//#line 321 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44299 =
                          k;
                        
//#line 321 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44300 =
                          ((x10.array.Array<x10.array.VarMat>)t44298).$apply$G((int)(t44299));
                        
//#line 321 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44301 =
                          l44368;
                        
//#line 321 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarRow t44302 =
                          ((x10.array.VarRow)(((x10.array.Mat<x10.array.VarRow>)t44300).$apply$G((int)(t44301))));
                        
//#line 321 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44303 =
                          m44317;
                        
//#line 321 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44304 =
                          t44302.$apply$O((int)(t44303));
                        
//#line 321 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final java.lang.String t44305 =
                          ((" ") + ((x10.core.Int.$box(t44304))));
                        
//#line 321 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
ps.print(((java.lang.String)(t44305)));
                        
//#line 320 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44306 =
                          m44317;
                        
//#line 320 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44307 =
                          ((t44306) + (((int)(1))));
                        
//#line 320 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
m44317 = t44307;
                    }
                    
//#line 322 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
ps.print(((java.lang.String)("\n")));
                    
//#line 315 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44326 =
                      l44368;
                    
//#line 315 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44327 =
                      ((t44326) + (((int)(1))));
                    
//#line 315 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
l44368 = t44327;
                }
                
//#line 324 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
ps.printf(((java.lang.String)("  max\n")));
                
//#line 325 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
int l44375 =
                  0;
                
//#line 325 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
for (;
                                                                                                                 true;
                                                                                                                 ) {
                    
//#line 325 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44376 =
                      l44375;
                    
//#line 325 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44377 =
                      ((x10.array.Array)(myMax));
                    
//#line 325 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44378 =
                      k;
                    
//#line 325 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44379 =
                      ((x10.array.Array<x10.array.VarMat>)t44377).$apply$G((int)(t44378));
                    
//#line 325 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44380 =
                      ((x10.array.Mat<x10.array.VarRow>)t44379).
                        rows;
                    
//#line 325 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44381 =
                      ((t44376) < (((int)(t44380))));
                    
//#line 325 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (!(t44381)) {
                        
//#line 325 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
break;
                    }
                    
//#line 326 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
ps.print(((java.lang.String)("  ")));
                    
//#line 327 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
int m44348 =
                      0;
                    
//#line 327 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
for (;
                                                                                                                     true;
                                                                                                                     ) {
                        
//#line 327 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44349 =
                          m44348;
                        
//#line 327 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44350 =
                          ((x10.array.Array)(myMax));
                        
//#line 327 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44351 =
                          k;
                        
//#line 327 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44352 =
                          ((x10.array.Array<x10.array.VarMat>)t44350).$apply$G((int)(t44351));
                        
//#line 327 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44353 =
                          l44375;
                        
//#line 327 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarRow t44354 =
                          ((x10.array.VarRow)(((x10.array.Mat<x10.array.VarRow>)t44352).$apply$G((int)(t44353))));
                        
//#line 327 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44355 =
                          t44354.
                            cols;
                        
//#line 327 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44356 =
                          ((t44349) < (((int)(t44355))));
                        
//#line 327 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (!(t44356)) {
                            
//#line 327 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
break;
                        }
                        
//#line 328 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44328 =
                          ((x10.array.Array)(myMax));
                        
//#line 328 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44329 =
                          k;
                        
//#line 328 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44330 =
                          ((x10.array.Array<x10.array.VarMat>)t44328).$apply$G((int)(t44329));
                        
//#line 328 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44331 =
                          l44375;
                        
//#line 328 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarRow t44332 =
                          ((x10.array.VarRow)(((x10.array.Mat<x10.array.VarRow>)t44330).$apply$G((int)(t44331))));
                        
//#line 328 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44333 =
                          m44348;
                        
//#line 328 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44334 =
                          t44332.$apply$O((int)(t44333));
                        
//#line 328 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final java.lang.String t44335 =
                          ((" ") + ((x10.core.Int.$box(t44334))));
                        
//#line 328 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
ps.print(((java.lang.String)(t44335)));
                        
//#line 327 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44336 =
                          m44348;
                        
//#line 327 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44337 =
                          ((t44336) + (((int)(1))));
                        
//#line 327 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
m44348 = t44337;
                    }
                    
//#line 329 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
ps.print(((java.lang.String)("  sum")));
                    
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
int m44357 =
                      0;
                    
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
for (;
                                                                                                                     true;
                                                                                                                     ) {
                        
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44358 =
                          m44357;
                        
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44359 =
                          ((x10.array.Array)(maxSum));
                        
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44360 =
                          k;
                        
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44361 =
                          ((x10.array.Array<x10.array.VarMat>)t44359).$apply$G((int)(t44360));
                        
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44362 =
                          l44375;
                        
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarRow t44363 =
                          ((x10.array.VarRow)(((x10.array.Mat<x10.array.VarRow>)t44361).$apply$G((int)(t44362))));
                        
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44364 =
                          t44363.
                            cols;
                        
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44365 =
                          ((t44358) < (((int)(t44364))));
                        
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (!(t44365)) {
                            
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
break;
                        }
                        
//#line 331 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.array.VarMat> t44338 =
                          ((x10.array.Array)(maxSum));
                        
//#line 331 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44339 =
                          k;
                        
//#line 331 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarMat t44340 =
                          ((x10.array.Array<x10.array.VarMat>)t44338).$apply$G((int)(t44339));
                        
//#line 331 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44341 =
                          l44375;
                        
//#line 331 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.VarRow t44342 =
                          ((x10.array.VarRow)(((x10.array.Mat<x10.array.VarRow>)t44340).$apply$G((int)(t44341))));
                        
//#line 331 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44343 =
                          m44357;
                        
//#line 331 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44344 =
                          t44342.$apply$O((int)(t44343));
                        
//#line 331 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final java.lang.String t44345 =
                          ((" ") + ((x10.core.Int.$box(t44344))));
                        
//#line 331 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
ps.print(((java.lang.String)(t44345)));
                        
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44346 =
                          m44357;
                        
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44347 =
                          ((t44346) + (((int)(1))));
                        
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
m44357 = t44347;
                    }
                    
//#line 332 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
ps.println();
                    
//#line 325 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44366 =
                      l44375;
                    
//#line 325 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44367 =
                      ((t44366) + (((int)(1))));
                    
//#line 325 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
l44375 = t44367;
                }
                
//#line 312 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44384 =
                  k;
                
//#line 312 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44385 =
                  ((t44384) + (((int)(1))));
                
//#line 312 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
k = t44385;
            }
        }
        
        
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final public x10.array.PolyScanner
                                                                                                     x10$array$PolyScanner$$x10$array$PolyScanner$this(
                                                                                                     ){
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
return x10.array.PolyScanner.this;
        }
        
        
//#line 293 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
@x10.core.X10Generated final public static class Anonymous$9371 extends x10.core.Ref implements x10.lang.Iterator, x10.x10rt.X10JavaSerializable
                                                                                                    {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Anonymous$9371.class);
            
            public static final x10.rtt.RuntimeType<Anonymous$9371> $RTT = x10.rtt.NamedType.<Anonymous$9371> make(
            "x10.array.PolyScanner.Anonymous$9371", /* base class */Anonymous$9371.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Iterator.$RTT, x10.array.Point.$RTT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(Anonymous$9371 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Anonymous$9371.class + " calling"); } 
                x10.array.PolyScanner.RailIt it = (x10.array.PolyScanner.RailIt) $deserializer.readRef();
                $_obj.it = it;
                x10.array.PolyScanner out$ = (x10.array.PolyScanner) $deserializer.readRef();
                $_obj.out$ = out$;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                Anonymous$9371 $_obj = new Anonymous$9371((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (it instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.it);
                } else {
                $serializer.write(this.it);
                }
                if (out$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$);
                } else {
                $serializer.write(this.out$);
                }
                
            }
            
            // constructor just for allocation
            public Anonymous$9371(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // bridge for method abstract public x10.lang.Iterator.next():T
            final public x10.array.Point
              next$G(){return next();}
            
                
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
public x10.array.PolyScanner out$;
                
//#line 294 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
public x10.array.PolyScanner.RailIt it;
                
                
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final public boolean
                                                                                                              hasNext$O(
                                                                                                              ){
                    
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyScanner.RailIt t44007 =
                      ((x10.array.PolyScanner.RailIt)(it));
                    
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44008 =
                      t44007.hasNext$O();
                    
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
return t44008;
                }
                
                
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final public x10.array.Point
                                                                                                              next(
                                                                                                              ){
                    
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyScanner.RailIt t44009 =
                      ((x10.array.PolyScanner.RailIt)(it));
                    
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Array<x10.core.Int> t44010 =
                      ((x10.array.Array)(t44009.next()));
                    
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Point t44011 =
                      ((x10.array.Point)(x10.array.Point.<x10.core.Int>$implicit_convert__0$1x10$array$Point$$T$2(x10.rtt.Types.INT, ((x10.array.Array)(t44010)))));
                    
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Point __desugarer__var__24__43577 =
                      ((x10.array.Point)(((x10.array.Point)
                                           t44011)));
                    
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
x10.array.Point ret43578 =
                       null;
                    
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44386 =
                      __desugarer__var__24__43577.
                        rank;
                    
//#line 296 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyScanner t44387 =
                      this.
                        out$;
                    
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final int t44388 =
                      t44387.
                        rank;
                    
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44389 =
                      ((int) t44386) ==
                    ((int) t44388);
                    
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44390 =
                      !(t44389);
                    
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (t44390) {
                        
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final boolean t44391 =
                          true;
                        
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
if (t44391) {
                            
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.lang.FailedDynamicCheckException t44392 =
                              new x10.lang.FailedDynamicCheckException("x10.array.Point{self.rank==x10.array.PolyScanner.this(:<anonymous class>).rank}");
                            
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
throw t44392;
                        }
                    }
                    
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
ret43578 = ((x10.array.Point)(__desugarer__var__24__43577));
                    
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.Point t44019 =
                      ((x10.array.Point)(ret43578));
                    
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
return t44019;
                }
                
                
//#line 297 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final public void
                                                                                                              remove(
                                                                                                              ){
                    
//#line 297 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyScanner.RailIt t44020 =
                      ((x10.array.PolyScanner.RailIt)(it));
                    
//#line 297 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
t44020.remove();
                }
                
                
//#line 293 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
// creation method for java code (1-phase java constructor)
                public Anonymous$9371(final x10.array.PolyScanner out$){this((java.lang.System[]) null);
                                                                            $init(out$);}
                
                // constructor for non-virtual call
                final public x10.array.PolyScanner.Anonymous$9371 x10$array$PolyScanner$Anonymous$9371$$init$S(final x10.array.PolyScanner out$) { {
                                                                                                                                                          
//#line 293 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"

                                                                                                                                                          
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.out$ = out$;
                                                                                                                                                          
//#line 294 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyScanner t44021 =
                                                                                                                                                            this.
                                                                                                                                                              out$;
                                                                                                                                                          
//#line 294 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
final x10.array.PolyScanner.RailIt t44022 =
                                                                                                                                                            ((x10.array.PolyScanner.RailIt)(new x10.array.PolyScanner.RailIt((java.lang.System[]) null).$init(t44021)));
                                                                                                                                                          
//#line 294 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyScanner.x10"
this.it = ((x10.array.PolyScanner.RailIt)(t44022));
                                                                                                                                                      }
                                                                                                                                                      return this;
                                                                                                                                                      }
                
                // constructor
                public x10.array.PolyScanner.Anonymous$9371 $init(final x10.array.PolyScanner out$){return x10$array$PolyScanner$Anonymous$9371$$init$S(out$);}
                
            
        }
        
    
}
