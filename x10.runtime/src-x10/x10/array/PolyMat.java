package x10.array;


@x10.core.X10Generated public class PolyMat extends x10.array.Mat<x10.array.PolyRow> implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, PolyMat.class);
    
    public static final x10.rtt.RuntimeType<PolyMat> $RTT = x10.rtt.NamedType.<PolyMat> make(
    "x10.array.PolyMat", /* base class */PolyMat.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.array.Mat.$RTT, x10.array.PolyRow.$RTT)}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(PolyMat $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + PolyMat.class + " calling"); } 
        x10.array.Mat.$_deserialize_body($_obj, $deserializer);
        $_obj.isSimplified = $deserializer.readBoolean();
        $_obj.rank = $deserializer.readInt();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        PolyMat $_obj = new PolyMat((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        $serializer.write(this.isSimplified);
        $serializer.write(this.rank);
        
    }
    
    // constructor just for allocation
    public PolyMat(final java.lang.System[] $dummy) { 
    super($dummy, x10.array.PolyRow.$RTT);
    }
    // bridge for method public x10.array.Mat.operator()(i:x10.lang.Int):T{this(:x10.array.Mat).mat.rank==1, this(:x10.array.Mat).mat.region.rank==1, this(:x10.array.Mat).mat.region!=null, this(:x10.array.Mat).mat.rect==this(:x10.array.Mat).mat.region.rect, this(:x10.array.Mat).mat.zeroBased==this(:x10.array.Mat).mat.region.zeroBased, this(:x10.array.Mat).mat.rail==this(:x10.array.Mat).mat.region.rail}
    public x10.array.PolyRow
      $apply(int a1){return super.$apply$G((a1));}
    
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
public int rank;
        
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
public boolean isSimplified;
        
        
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
// creation method for java code (1-phase java constructor)
        public PolyMat(final int rows,
                       final int cols,
                       final x10.core.fun.Fun_0_2<x10.core.Int,x10.core.Int,x10.core.Int> init,
                       final boolean isSimplified, __2$1x10$lang$Int$3x10$lang$Int$3x10$lang$Int$2 $dummy){this((java.lang.System[]) null);
                                                                                                               $init(rows,cols,init,isSimplified, (x10.array.PolyMat.__2$1x10$lang$Int$3x10$lang$Int$3x10$lang$Int$2) null);}
        
        // constructor for non-virtual call
        final public x10.array.PolyMat x10$array$PolyMat$$init$S(final int rows,
                                                                 final int cols,
                                                                 final x10.core.fun.Fun_0_2<x10.core.Int,x10.core.Int,x10.core.Int> init,
                                                                 final boolean isSimplified, __2$1x10$lang$Int$3x10$lang$Int$3x10$lang$Int$2 $dummy) { {
                                                                                                                                                              
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.array.PolyRow> t41150 =
                                                                                                                                                                ((x10.core.fun.Fun_0_1)(new x10.array.PolyMat.$Closure$48(init,
                                                                                                                                                                                                                          cols, (x10.array.PolyMat.$Closure$48.__0$1x10$lang$Int$3x10$lang$Int$3x10$lang$Int$2) null)));
                                                                                                                                                              
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.Array<x10.array.PolyRow> t41156 =
                                                                                                                                                                ((x10.array.Array)(new x10.array.Array<x10.array.PolyRow>((java.lang.System[]) null, x10.array.PolyRow.$RTT).$init(((int)(rows)),
                                                                                                                                                                                                                                                                                   ((x10.core.fun.Fun_0_1)(t41150)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                                                                                                                                                              
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
super.$init(((int)(rows)),
                                                                                                                                                                                                                                                                 ((int)(cols)),
                                                                                                                                                                                                                                                                 ((x10.array.Array)(t41156)), (x10.array.Mat.__2$1x10$array$Mat$$T$2) null);
                                                                                                                                                              
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int cols1 =
                                                                                                                                                                ((cols) - (((int)(1))));
                                                                                                                                                              
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
this.rank = cols1;
                                                                                                                                                              
                                                                                                                                                              
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
this.isSimplified = isSimplified;
                                                                                                                                                          }
                                                                                                                                                          return this;
                                                                                                                                                          }
        
        // constructor
        public x10.array.PolyMat $init(final int rows,
                                       final int cols,
                                       final x10.core.fun.Fun_0_2<x10.core.Int,x10.core.Int,x10.core.Int> init,
                                       final boolean isSimplified, __2$1x10$lang$Int$3x10$lang$Int$3x10$lang$Int$2 $dummy){return x10$array$PolyMat$$init$S(rows,cols,init,isSimplified, $dummy);}
        
        
        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
public x10.array.PolyMat
                                                                                                 simplifyParallel(
                                                                                                 ){
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t40963 =
              rows;
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t40964 =
              ((int) t40963) ==
            ((int) 0);
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (t40964) {
                
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return this;
            }
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t40965 =
              rank;
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyMatBuilder pmb =
              ((x10.array.PolyMatBuilder)(new x10.array.PolyMatBuilder((java.lang.System[]) null).$init(((int)(t40965)))));
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
x10.array.PolyRow last =
              null;
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.lang.Iterator<x10.array.PolyRow> next41164 =
              this.iterator();
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
for (;
                                                                                                        true;
                                                                                                        ) {
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41165 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)next41164).hasNext$O();
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (!(t41165)) {
                    
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
break;
                }
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyRow next41157 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)next41164).next$G();
                
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyRow t41158 =
                  last;
                
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
boolean t41159 =
                  ((t41158) != (null));
                
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (t41159) {
                    
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyRow t41160 =
                      last;
                    
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41161 =
                      next41157.isParallel$O(((x10.array.PolyRow)(t41160)));
                    
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
t41159 = !(t41161);
                }
                
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41162 =
                  t41159;
                
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (t41162) {
                    
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyRow t41163 =
                      last;
                    
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
pmb.add(((x10.array.Row)(t41163)));
                }
                
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
last = next41157;
            }
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyRow t40974 =
              last;
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
pmb.add(((x10.array.Row)(t40974)));
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyMat t40975 =
              ((x10.array.PolyMat)(pmb.toSortedPolyMat((boolean)(false))));
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return t40975;
        }
        
        
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
public x10.array.PolyMat
                                                                                                 simplifyAll(
                                                                                                 ){
            
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t40976 =
              isSimplified;
            
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (t40976) {
                
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return this;
            }
            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t40977 =
              rank;
            
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyMatBuilder pmb =
              ((x10.array.PolyMatBuilder)(new x10.array.PolyMatBuilder((java.lang.System[]) null).$init(((int)(t40977)))));
            
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t40978 =
              rows;
            
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Boolean> t40979 =
              ((x10.core.fun.Fun_0_1)(new x10.array.PolyMat.$Closure$49()));
            
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.Array<x10.core.Boolean> removed =
              ((x10.array.Array)(new x10.array.Array<x10.core.Boolean>((java.lang.System[]) null, x10.rtt.Types.BOOLEAN).$init(((int)(t40978)),
                                                                                                                               ((x10.core.fun.Fun_0_1)(t40979)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
int i41191 =
              0;
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
for (;
                                                                                                        true;
                                                                                                        ) {
                
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41192 =
                  i41191;
                
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41193 =
                  rows;
                
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41194 =
                  ((t41192) < (((int)(t41193))));
                
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (!(t41194)) {
                    
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
break;
                }
                
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41181 =
                  i41191;
                
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyRow r41182 =
                  ((x10.array.PolyRow)(this.$apply$G((int)(t41181))));
                
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41183 =
                  rank;
                
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyMatBuilder trial41184 =
                  ((x10.array.PolyMatBuilder)(new x10.array.PolyMatBuilder((java.lang.System[]) null).$init(((int)(t41183)))));
                
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
int j41177 =
                  0;
                
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
for (;
                                                                                                            true;
                                                                                                            ) {
                    
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41178 =
                      j41177;
                    
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41179 =
                      rows;
                    
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41180 =
                      ((t41178) < (((int)(t41179))));
                    
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (!(t41180)) {
                        
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
break;
                    }
                    
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41166 =
                      j41177;
                    
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41167 =
                      x10.core.Boolean.$unbox(((x10.array.Array<x10.core.Boolean>)removed).$apply$G((int)(t41166)));
                    
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41168 =
                      !(t41167);
                    
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (t41168) {
                        
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41169 =
                          i41191;
                        
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41170 =
                          j41177;
                        
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41171 =
                          ((int) t41169) ==
                        ((int) t41170);
                        
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
x10.array.PolyRow t41172 =
                           null;
                        
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (t41171) {
                            
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
t41172 = r41182.complement();
                        } else {
                            
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41173 =
                              j41177;
                            
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
t41172 = this.$apply$G((int)(t41173));
                        }
                        
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyRow t41174 =
                          t41172;
                        
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
trial41184.add(((x10.array.Row)(t41174)));
                    }
                    
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41175 =
                      j41177;
                    
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41176 =
                      ((t41175) + (((int)(1))));
                    
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
j41177 = t41176;
                }
                
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyMat t41185 =
                  ((x10.array.PolyMat)(trial41184.toSortedPolyMat((boolean)(false))));
                
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41186 =
                  t41185.isEmpty$O();
                
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41187 =
                  !(t41186);
                
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (t41187) {
                    
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
pmb.add(((x10.array.Row)(r41182)));
                } else {
                    
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41188 =
                      i41191;
                    
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
((x10.array.Array<x10.core.Boolean>)removed).$set__1x10$array$Array$$T$G((int)(t41188),
                                                                                                                                                                                    x10.core.Boolean.$box(true));
                }
                
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41189 =
                  i41191;
                
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41190 =
                  ((t41189) + (((int)(1))));
                
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
i41191 = t41190;
            }
            
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyMat t41007 =
              ((x10.array.PolyMat)(pmb.toSortedPolyMat((boolean)(true))));
            
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return t41007;
        }
        
        
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
public x10.array.PolyMat
                                                                                                  eliminate(
                                                                                                  final int k,
                                                                                                  final boolean simplifyDegenerate){
            
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41008 =
              rank;
            
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyMatBuilder pmb =
              ((x10.array.PolyMatBuilder)(new x10.array.PolyMatBuilder((java.lang.System[]) null).$init(((int)(t41008)))));
            
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.lang.Iterator<x10.array.PolyRow> ir41258 =
              this.iterator();
            
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
for (;
                                                                                                         true;
                                                                                                         ) {
                
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41259 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)ir41258).hasNext$O();
                
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (!(t41259)) {
                    
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
break;
                }
                
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyRow ir41253 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)ir41258).next$G();
                
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int ia41254 =
                  ir41253.$apply$O((int)(k));
                
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41255 =
                  ((int) ia41254) ==
                ((int) 0);
                
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (t41255) {
                    
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
pmb.add(((x10.array.Row)(ir41253)));
                } else {
                    
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.lang.Iterator<x10.array.PolyRow> jr41256 =
                      this.iterator();
                    
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
for (;
                                                                                                                 true;
                                                                                                                 ) {
                        
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41257 =
                          ((x10.lang.Iterator<x10.array.PolyRow>)jr41256).hasNext$O();
                        
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (!(t41257)) {
                            
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
break;
                        }
                        
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyRow jr41223 =
                          ((x10.lang.Iterator<x10.array.PolyRow>)jr41256).next$G();
                        
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int ja41224 =
                          jr41223.$apply$O((int)(k));
                        
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41225 =
                          rank;
                        
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41226 =
                          ((t41225) + (((int)(1))));
                        
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.Array<x10.core.Int> as_41227 =
                          ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(t41226)));
                        
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
boolean t41228 =
                          ((ia41254) > (((int)(0))));
                        
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (t41228) {
                            
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
t41228 = ((ja41224) < (((int)(0))));
                        }
                        
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41229 =
                          t41228;
                        
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (t41229) {
                            
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
int l41230 =
                              0;
                            
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
for (;
                                                                                                                         true;
                                                                                                                         ) {
                                
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41231 =
                                  l41230;
                                
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41232 =
                                  rank;
                                
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41233 =
                                  ((t41231) <= (((int)(t41232))));
                                
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (!(t41233)) {
                                    
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
break;
                                }
                                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41195 =
                                  l41230;
                                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41196 =
                                  l41230;
                                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41197 =
                                  jr41223.$apply$O((int)(t41196));
                                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41198 =
                                  ((ia41254) * (((int)(t41197))));
                                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41199 =
                                  l41230;
                                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41200 =
                                  ir41253.$apply$O((int)(t41199));
                                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41201 =
                                  ((ja41224) * (((int)(t41200))));
                                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41202 =
                                  ((t41198) - (((int)(t41201))));
                                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
((x10.array.Array<x10.core.Int>)as_41227).$set__1x10$array$Array$$T$G((int)(t41195),
                                                                                                                                                                                              x10.core.Int.$box(t41202));
                                
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41203 =
                                  l41230;
                                
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41204 =
                                  ((t41203) + (((int)(1))));
                                
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
l41230 = t41204;
                            }
                        } else {
                            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
boolean t41234 =
                              ((ia41254) < (((int)(0))));
                            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (t41234) {
                                
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
t41234 = ((ja41224) > (((int)(0))));
                            }
                            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41235 =
                              t41234;
                            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (t41235) {
                                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
int l41236 =
                                  0;
                                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
for (;
                                                                                                                             true;
                                                                                                                             ) {
                                    
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41237 =
                                      l41236;
                                    
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41238 =
                                      rank;
                                    
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41239 =
                                      ((t41237) <= (((int)(t41238))));
                                    
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (!(t41239)) {
                                        
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
break;
                                    }
                                    
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41205 =
                                      l41236;
                                    
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41206 =
                                      l41236;
                                    
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41207 =
                                      ir41253.$apply$O((int)(t41206));
                                    
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41208 =
                                      ((ja41224) * (((int)(t41207))));
                                    
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41209 =
                                      l41236;
                                    
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41210 =
                                      jr41223.$apply$O((int)(t41209));
                                    
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41211 =
                                      ((ia41254) * (((int)(t41210))));
                                    
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41212 =
                                      ((t41208) - (((int)(t41211))));
                                    
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
((x10.array.Array<x10.core.Int>)as_41227).$set__1x10$array$Array$$T$G((int)(t41205),
                                                                                                                                                                                                  x10.core.Int.$box(t41212));
                                    
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41213 =
                                      l41236;
                                    
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41214 =
                                      ((t41213) + (((int)(1))));
                                    
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
l41236 = t41214;
                                }
                            }
                        }
                        
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
int t41240 =
                           0;
                        
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (simplifyDegenerate) {
                            
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
t41240 = rank;
                        } else {
                            
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41241 =
                              rank;
                            
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
t41240 = ((t41241) + (((int)(1))));
                        }
                        
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int lim41242 =
                          t41240;
                        
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
boolean degenerate41243 =
                          true;
                        
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
int l41220 =
                          0;
                        
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
for (;
                                                                                                                     true;
                                                                                                                     ) {
                            
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41221 =
                              l41220;
                            
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41222 =
                              ((t41221) < (((int)(lim41242))));
                            
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (!(t41222)) {
                                
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
break;
                            }
                            
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41215 =
                              l41220;
                            
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41216 =
                              x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)as_41227).$apply$G((int)(t41215)));
                            
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41217 =
                              ((int) t41216) !=
                            ((int) 0);
                            
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (t41217) {
                                
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
degenerate41243 = false;
                            }
                            
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41218 =
                              l41220;
                            
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41219 =
                              ((t41218) + (((int)(1))));
                            
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
l41220 = t41219;
                        }
                        
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41244 =
                          degenerate41243;
                        
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41245 =
                          !(t41244);
                        
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (t41245) {
                            
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41246 =
                              ((x10.array.Array<x10.core.Int>)as_41227).
                                size;
                            
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t41247 =
                              ((x10.core.fun.Fun_0_1)(new x10.array.PolyMat.$Closure$50(as_41227, (x10.array.PolyMat.$Closure$50.__0$1x10$lang$Int$2) null)));
                            
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.Array<x10.core.Int> t41250 =
                              ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t41246)),
                                                                                                                                       ((x10.core.fun.Fun_0_1)(t41247)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                            
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
x10.array.PolyRow r41251 =
                              new x10.array.PolyRow((java.lang.System[]) null).$init(((x10.array.Array)(t41250)), (x10.array.PolyRow.__0$1x10$lang$Int$2) null);
                            
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyRow t41252 =
                              r41251;
                            
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
pmb.add(((x10.array.Row)(t41252)));
                        }
                    }
                }
            }
            
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyMat t41065 =
              ((x10.array.PolyMat)(pmb.toSortedPolyMat((boolean)(false))));
            
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyMat t41066 =
              ((x10.array.PolyMat)(t41065.simplifyParallel()));
            
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return t41066;
        }
        
        
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
public boolean
                                                                                                  isRect$O(
                                                                                                  ){
            
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.lang.Iterator<x10.array.PolyRow> r41263 =
              this.iterator();
            
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
for (;
                                                                                                         true;
                                                                                                         ) {
                
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41264 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)r41263).hasNext$O();
                
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (!(t41264)) {
                    
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
break;
                }
                
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyRow r41260 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)r41263).next$G();
                
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41261 =
                  r41260.isRect$O();
                
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41262 =
                  !(t41261);
                
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (t41262) {
                    
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return false;
                }
            }
            
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return true;
        }
        
        
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
public int
                                                                                                  rectMin$O(
                                                                                                  final int axis){
            
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.lang.Iterator<x10.array.PolyRow> r41272 =
              this.iterator();
            
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
for (;
                                                                                                         true;
                                                                                                         ) {
                
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41273 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)r41272).hasNext$O();
                
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (!(t41273)) {
                    
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
break;
                }
                
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyRow r41265 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)r41272).next$G();
                
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int a41266 =
                  r41265.$apply$O((int)(axis));
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41267 =
                  ((a41266) < (((int)(0))));
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (t41267) {
                    
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41268 =
                      rank;
                    
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41269 =
                      r41265.$apply$O((int)(t41268));
                    
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41270 =
                      (-(t41269));
                    
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41271 =
                      ((t41270) / (((int)(a41266))));
                    
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return t41271;
                }
            }
            
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final java.lang.String t41078 =
              (("axis ") + ((x10.core.Int.$box(axis))));
            
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
java.lang.String msg =
              ((t41078) + (" has no minimum"));
            
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final java.lang.String t41079 =
              msg;
            
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.UnboundedRegionException t41080 =
              ((x10.array.UnboundedRegionException)(new x10.array.UnboundedRegionException(t41079)));
            
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
throw t41080;
        }
        
        
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
public int
                                                                                                  rectMax$O(
                                                                                                  final int axis){
            
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.lang.Iterator<x10.array.PolyRow> r41281 =
              this.iterator();
            
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
for (;
                                                                                                         true;
                                                                                                         ) {
                
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41282 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)r41281).hasNext$O();
                
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (!(t41282)) {
                    
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
break;
                }
                
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyRow r41274 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)r41281).next$G();
                
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int a41275 =
                  r41274.$apply$O((int)(axis));
                
//#line 190 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41276 =
                  ((a41275) > (((int)(0))));
                
//#line 190 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (t41276) {
                    
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41277 =
                      rank;
                    
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41278 =
                      r41274.$apply$O((int)(t41277));
                    
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41279 =
                      (-(t41278));
                    
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41280 =
                      ((t41279) / (((int)(a41275))));
                    
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return t41280;
                }
            }
            
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final java.lang.String t41088 =
              (("axis ") + ((x10.core.Int.$box(axis))));
            
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final java.lang.String msg =
              ((t41088) + (" has no maximum"));
            
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.UnboundedRegionException t41089 =
              ((x10.array.UnboundedRegionException)(new x10.array.UnboundedRegionException(((java.lang.String)(msg)))));
            
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
throw t41089;
        }
        
        
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
public x10.array.Array<x10.core.Int>
                                                                                                  rectMin(
                                                                                                  ){
            
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41091 =
              rank;
            
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t41092 =
              ((x10.core.fun.Fun_0_1)(new x10.array.PolyMat.$Closure$51(this)));
            
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.Array<x10.core.Int> t41093 =
              ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t41091)),
                                                                                                                       ((x10.core.fun.Fun_0_1)(t41092)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
            
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return t41093;
        }
        
        
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
public x10.array.Array<x10.core.Int>
                                                                                                  rectMax(
                                                                                                  ){
            
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41095 =
              rank;
            
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t41096 =
              ((x10.core.fun.Fun_0_1)(new x10.array.PolyMat.$Closure$52(this)));
            
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.Array<x10.core.Int> t41097 =
              ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t41095)),
                                                                                                                       ((x10.core.fun.Fun_0_1)(t41096)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
            
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return t41097;
        }
        
        
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
public boolean
                                                                                                  isZeroBased$O(
                                                                                                  ){
            
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41098 =
              this.isRect$O();
            
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41099 =
              !(t41098);
            
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (t41099) {
                
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return false;
            }
            
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
try {try {{
                
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
int i =
                  0;
                
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
for (;
                                                                                                             true;
                                                                                                             ) {
                    
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41101 =
                      i;
                    
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41102 =
                      rank;
                    
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41108 =
                      ((t41101) < (((int)(t41102))));
                    
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (!(t41108)) {
                        
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
break;
                    }
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41283 =
                      i;
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41284 =
                      this.rectMin$O((int)(t41283));
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41285 =
                      ((int) t41284) !=
                    ((int) 0);
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (t41285) {
                        
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return false;
                    }
                    
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41286 =
                      i;
                    
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41287 =
                      ((t41286) + (((int)(1))));
                    
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
i = t41287;
                }
            }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Exception $exc$) {throw x10.core.ThrowableUtilities.convertJavaException($exc$);}}catch (final x10.array.UnboundedRegionException e) {
                
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return false;
            }
            
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return true;
        }
        
        
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
public boolean
                                                                                                  isBounded$O(
                                                                                                  ){
            
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
try {try {{
                
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
int i =
                  0;
                
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
for (;
                                                                                                             true;
                                                                                                             ) {
                    
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41110 =
                      i;
                    
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41111 =
                      rank;
                    
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41116 =
                      ((t41110) < (((int)(t41111))));
                    
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (!(t41116)) {
                        
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
break;
                    }
                    
//#line 218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41288 =
                      i;
                    
//#line 218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
this.rectMin$O((int)(t41288));
                    
//#line 219 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41289 =
                      i;
                    
//#line 219 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
this.rectMax$O((int)(t41289));
                    
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41290 =
                      i;
                    
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41291 =
                      ((t41290) + (((int)(1))));
                    
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
i = t41291;
                }
            }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Exception $exc$) {throw x10.core.ThrowableUtilities.convertJavaException($exc$);}}catch (final x10.array.UnboundedRegionException e) {
                
//#line 222 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return false;
            }
            
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return true;
        }
        
        
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
public boolean
                                                                                                  isEmpty$O(
                                                                                                  ){
            
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
x10.array.PolyMat pm =
              this;
            
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
int i41301 =
              0;
            
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
for (;
                                                                                                         true;
                                                                                                         ) {
                
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41302 =
                  i41301;
                
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41303 =
                  rank;
                
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41304 =
                  ((t41302) < (((int)(t41303))));
                
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (!(t41304)) {
                    
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
break;
                }
                
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyMat t41292 =
                  pm;
                
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41293 =
                  i41301;
                
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyMat t41294 =
                  ((x10.array.PolyMat)(t41292.eliminate((int)(t41293),
                                                        (boolean)(false))));
                
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
pm = t41294;
                
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41295 =
                  i41301;
                
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41296 =
                  ((t41295) + (((int)(1))));
                
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
i41301 = t41296;
            }
            
//#line 241 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyMat t41305 =
              pm;
            
//#line 241 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.lang.Iterator<x10.array.PolyRow> r41306 =
              ((x10.array.Mat<x10.array.PolyRow>)t41305).iterator();
            
//#line 241 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
for (;
                                                                                                         true;
                                                                                                         ) {
                
//#line 241 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41307 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)r41306).hasNext$O();
                
//#line 241 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (!(t41307)) {
                    
//#line 241 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
break;
                }
                
//#line 241 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyRow r41297 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)r41306).next$G();
                
//#line 242 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41298 =
                  rank;
                
//#line 242 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41299 =
                  r41297.$apply$O((int)(t41298));
                
//#line 242 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41300 =
                  ((t41299) > (((int)(0))));
                
//#line 242 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (t41300) {
                    
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return true;
                }
            }
            
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return false;
        }
        
        
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
public x10.array.PolyMat
                                                                                                  $or(
                                                                                                  final x10.array.PolyMat that){
            
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41132 =
              rank;
            
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyMatBuilder pmb =
              ((x10.array.PolyMatBuilder)(new x10.array.PolyMatBuilder((java.lang.System[]) null).$init(((int)(t41132)))));
            
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.lang.Iterator<x10.array.PolyRow> r41310 =
              this.iterator();
            
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
for (;
                                                                                                         true;
                                                                                                         ) {
                
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41311 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)r41310).hasNext$O();
                
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (!(t41311)) {
                    
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
break;
                }
                
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyRow r41308 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)r41310).next$G();
                
//#line 257 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
pmb.add(((x10.array.Row)(r41308)));
            }
            
//#line 258 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.lang.Iterator<x10.array.PolyRow> r41312 =
              ((x10.array.Mat<x10.array.PolyRow>)that).iterator();
            
//#line 258 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
for (;
                                                                                                         true;
                                                                                                         ) {
                
//#line 258 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41313 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)r41312).hasNext$O();
                
//#line 258 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (!(t41313)) {
                    
//#line 258 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
break;
                }
                
//#line 258 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyRow r41309 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)r41312).next$G();
                
//#line 259 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
pmb.add(((x10.array.Row)(r41309)));
            }
            
//#line 260 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyMat t41137 =
              ((x10.array.PolyMat)(pmb.toSortedPolyMat((boolean)(false))));
            
//#line 260 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return t41137;
        }
        
        
//#line 264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
public java.lang.String
                                                                                                  toString(
                                                                                                  ){
            
//#line 266 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
java.lang.String s =
              "(";
            
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
boolean first =
              true;
            
//#line 269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.lang.Iterator<x10.array.PolyRow> r41322 =
              this.iterator();
            
//#line 269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
for (;
                                                                                                         true;
                                                                                                         ) {
                
//#line 269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41323 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)r41322).hasNext$O();
                
//#line 269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (!(t41323)) {
                    
//#line 269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
break;
                }
                
//#line 269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyRow r41314 =
                  ((x10.lang.Iterator<x10.array.PolyRow>)r41322).next$G();
                
//#line 270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41315 =
                  first;
                
//#line 270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final boolean t41316 =
                  !(t41315);
                
//#line 270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
if (t41316) {
                    
//#line 270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final java.lang.String t41317 =
                      s;
                    
//#line 270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final java.lang.String t41318 =
                      ((t41317) + (" && "));
                    
//#line 270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
s = t41318;
                }
                
//#line 271 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final java.lang.String t41319 =
                  s;
                
//#line 271 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final java.lang.String t41320 =
                  r41314.toString();
                
//#line 271 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final java.lang.String t41321 =
                  ((t41319) + (t41320));
                
//#line 271 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
s = t41321;
                
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
first = false;
            }
            
//#line 275 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final java.lang.String t41147 =
              s;
            
//#line 275 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final java.lang.String t41148 =
              ((t41147) + (")"));
            
//#line 275 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
s = t41148;
            
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final java.lang.String t41149 =
              s;
            
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return t41149;
        }
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final public x10.array.PolyMat
                                                                                                 x10$array$PolyMat$$x10$array$PolyMat$this(
                                                                                                 ){
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return x10.array.PolyMat.this;
        }
        
        @x10.core.X10Generated public static class $Closure$47 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$47.class);
            
            public static final x10.rtt.RuntimeType<$Closure$47> $RTT = x10.rtt.StaticFunType.<$Closure$47> make(
            /* base class */$Closure$47.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$47 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$47.class + " calling"); } 
                x10.core.fun.Fun_0_2 init = (x10.core.fun.Fun_0_2) $deserializer.readRef();
                $_obj.init = init;
                $_obj.i41151 = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$47 $_obj = new $Closure$47((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (init instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.init);
                } else {
                $serializer.write(this.init);
                }
                $serializer.write(this.i41151);
                
            }
            
            // constructor just for allocation
            public $Closure$47(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int j41153){
                    
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41154 =
                      x10.core.Int.$unbox(((x10.core.fun.Fun_0_2<x10.core.Int,x10.core.Int,x10.core.Int>)this.
                                                                                                           init).$apply(x10.core.Int.$box(this.
                                                                                                                                            i41151),x10.rtt.Types.INT,
                                                                                                                        x10.core.Int.$box(j41153),x10.rtt.Types.INT));
                    
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return t41154;
                }
                
                public x10.core.fun.Fun_0_2<x10.core.Int,x10.core.Int,x10.core.Int> init;
                public int i41151;
                
                public $Closure$47(final x10.core.fun.Fun_0_2<x10.core.Int,x10.core.Int,x10.core.Int> init,
                                   final int i41151, __0$1x10$lang$Int$3x10$lang$Int$3x10$lang$Int$2 $dummy) { {
                                                                                                                      this.init = ((x10.core.fun.Fun_0_2)(init));
                                                                                                                      this.i41151 = i41151;
                                                                                                                  }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$lang$Int$3x10$lang$Int$3x10$lang$Int$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$48 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$48.class);
            
            public static final x10.rtt.RuntimeType<$Closure$48> $RTT = x10.rtt.StaticFunType.<$Closure$48> make(
            /* base class */$Closure$48.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.array.PolyRow.$RTT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$48 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$48.class + " calling"); } 
                x10.core.fun.Fun_0_2 init = (x10.core.fun.Fun_0_2) $deserializer.readRef();
                $_obj.init = init;
                $_obj.cols = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$48 $_obj = new $Closure$48((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (init instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.init);
                } else {
                $serializer.write(this.init);
                }
                $serializer.write(this.cols);
                
            }
            
            // constructor just for allocation
            public $Closure$48(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return $apply(x10.core.Int.$unbox(a1));
            }
            
                
                public x10.array.PolyRow
                  $apply(
                  final int i41151){
                    
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t41152 =
                      ((x10.core.fun.Fun_0_1)(new x10.array.PolyMat.$Closure$47(((x10.core.fun.Fun_0_2)(this.
                                                                                                          init)),
                                                                                i41151, (x10.array.PolyMat.$Closure$47.__0$1x10$lang$Int$3x10$lang$Int$3x10$lang$Int$2) null)));
                    
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final x10.array.PolyRow t41155 =
                      ((x10.array.PolyRow)(new x10.array.PolyRow((java.lang.System[]) null).$init(this.
                                                                                                    cols,
                                                                                                  ((x10.core.fun.Fun_0_1)(t41152)), (x10.array.PolyRow.__1$1x10$lang$Int$3x10$lang$Int$2) null)));
                    
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return t41155;
                }
                
                public x10.core.fun.Fun_0_2<x10.core.Int,x10.core.Int,x10.core.Int> init;
                public int cols;
                
                public $Closure$48(final x10.core.fun.Fun_0_2<x10.core.Int,x10.core.Int,x10.core.Int> init,
                                   final int cols, __0$1x10$lang$Int$3x10$lang$Int$3x10$lang$Int$2 $dummy) { {
                                                                                                                    this.init = ((x10.core.fun.Fun_0_2)(init));
                                                                                                                    this.cols = cols;
                                                                                                                }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$lang$Int$3x10$lang$Int$3x10$lang$Int$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$49 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$49.class);
            
            public static final x10.rtt.RuntimeType<$Closure$49> $RTT = x10.rtt.StaticFunType.<$Closure$49> make(
            /* base class */$Closure$49.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.BOOLEAN), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$49 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$49.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$49 $_obj = new $Closure$49((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public $Closure$49(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Boolean.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public boolean
                  $apply$O(
                  final int id$58){
                    
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return false;
                }
                
                public $Closure$49() { {
                                              
                                          }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$50 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$50.class);
            
            public static final x10.rtt.RuntimeType<$Closure$50> $RTT = x10.rtt.StaticFunType.<$Closure$50> make(
            /* base class */$Closure$50.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$50 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$50.class + " calling"); } 
                x10.array.Array as_41227 = (x10.array.Array) $deserializer.readRef();
                $_obj.as_41227 = as_41227;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$50 $_obj = new $Closure$50((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (as_41227 instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.as_41227);
                } else {
                $serializer.write(this.as_41227);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$50(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i41248){
                    
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41249 =
                      x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)this.
                                                                            as_41227).$apply$G((int)(i41248)));
                    
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return t41249;
                }
                
                public x10.array.Array<x10.core.Int> as_41227;
                
                public $Closure$50(final x10.array.Array<x10.core.Int> as_41227, __0$1x10$lang$Int$2 $dummy) { {
                                                                                                                      this.as_41227 = ((x10.array.Array)(as_41227));
                                                                                                                  }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$lang$Int$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$51 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$51.class);
            
            public static final x10.rtt.RuntimeType<$Closure$51> $RTT = x10.rtt.StaticFunType.<$Closure$51> make(
            /* base class */$Closure$51.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$51 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$51.class + " calling"); } 
                x10.array.PolyMat out$$ = (x10.array.PolyMat) $deserializer.readRef();
                $_obj.out$$ = out$$;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$51 $_obj = new $Closure$51((java.lang.System[]) null);
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
            public $Closure$51(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41090 =
                      this.
                        out$$.rectMin$O((int)(i));
                    
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return t41090;
                }
                
                public x10.array.PolyMat out$$;
                
                public $Closure$51(final x10.array.PolyMat out$$) { {
                                                                           this.out$$ = out$$;
                                                                       }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$52 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$52.class);
            
            public static final x10.rtt.RuntimeType<$Closure$52> $RTT = x10.rtt.StaticFunType.<$Closure$52> make(
            /* base class */$Closure$52.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$52 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$52.class + " calling"); } 
                x10.array.PolyMat out$$ = (x10.array.PolyMat) $deserializer.readRef();
                $_obj.out$$ = out$$;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$52 $_obj = new $Closure$52((java.lang.System[]) null);
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
            public $Closure$52(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
final int t41094 =
                      this.
                        out$$.rectMax$O((int)(i));
                    
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMat.x10"
return t41094;
                }
                
                public x10.array.PolyMat out$$;
                
                public $Closure$52(final x10.array.PolyMat out$$) { {
                                                                           this.out$$ = out$$;
                                                                       }}
                
            }
            
        // synthetic type for parameter mangling
        public abstract static class __2$1x10$lang$Int$3x10$lang$Int$3x10$lang$Int$2 {}
        
        }
        
        