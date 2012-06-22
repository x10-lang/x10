package x10.array;


@x10.core.X10Generated public class MatBuilder extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, MatBuilder.class);
    
    public static final x10.rtt.RuntimeType<MatBuilder> $RTT = x10.rtt.NamedType.<MatBuilder> make(
    "x10.array.MatBuilder", /* base class */MatBuilder.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(MatBuilder $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + MatBuilder.class + " calling"); } 
        x10.util.ArrayList mat = (x10.util.ArrayList) $deserializer.readRef();
        $_obj.mat = mat;
        $_obj.cols = $deserializer.readInt();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        MatBuilder $_obj = new MatBuilder((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        if (mat instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.mat);
        } else {
        $serializer.write(this.mat);
        }
        $serializer.write(this.cols);
        
    }
    
    // constructor just for allocation
    public MatBuilder(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
public x10.util.ArrayList<x10.array.Row> mat;
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
public int cols;
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
// creation method for java code (1-phase java constructor)
        public MatBuilder(final int cols){this((java.lang.System[]) null);
                                              $init(cols);}
        
        // constructor for non-virtual call
        final public x10.array.MatBuilder x10$array$MatBuilder$$init$S(final int cols) { {
                                                                                                
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"

                                                                                                
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"

                                                                                                
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
this.cols = cols;
                                                                                                
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final x10.util.ArrayList<x10.array.Row> t38033 =
                                                                                                  ((x10.util.ArrayList)(new x10.util.ArrayList<x10.array.Row>((java.lang.System[]) null, x10.array.Row.$RTT).$init()));
                                                                                                
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
this.mat = ((x10.util.ArrayList)(t38033));
                                                                                            }
                                                                                            return this;
                                                                                            }
        
        // constructor
        public x10.array.MatBuilder $init(final int cols){return x10$array$MatBuilder$$init$S(cols);}
        
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
// creation method for java code (1-phase java constructor)
        public MatBuilder(final int rows,
                          final int cols){this((java.lang.System[]) null);
                                              $init(rows,cols);}
        
        // constructor for non-virtual call
        final public x10.array.MatBuilder x10$array$MatBuilder$$init$S(final int rows,
                                                                       final int cols) { {
                                                                                                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"

                                                                                                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"

                                                                                                
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
this.cols = cols;
                                                                                                
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final x10.util.ArrayList<x10.array.Row> m =
                                                                                                  ((x10.util.ArrayList)(new x10.util.ArrayList<x10.array.Row>((java.lang.System[]) null, x10.array.Row.$RTT).$init(((int)(rows)))));
                                                                                                
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
this.mat = ((x10.util.ArrayList)(m));
                                                                                                
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
x10.array.MatBuilder.need__1$1x10$array$Row$2((int)(rows),
                                                                                                                                                                                                                                        ((x10.util.ArrayList)(m)),
                                                                                                                                                                                                                                        (int)(cols));
                                                                                            }
                                                                                            return this;
                                                                                            }
        
        // constructor
        public x10.array.MatBuilder $init(final int rows,
                                          final int cols){return x10$array$MatBuilder$$init$S(rows,cols);}
        
        
        
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
public void
                                                                                                    add(
                                                                                                    final x10.array.Row row){
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final x10.util.ArrayList<x10.array.Row> t38034 =
              ((x10.util.ArrayList)(mat));
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
((x10.util.ArrayList<x10.array.Row>)t38034).add__0x10$util$ArrayList$$T$O(((x10.array.Row)(row)));
        }
        
        
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
public void
                                                                                                    add__0$1x10$lang$Int$3x10$lang$Int$2(
                                                                                                    final x10.core.fun.Fun_0_1 a){
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final x10.util.ArrayList<x10.array.Row> t38036 =
              ((x10.util.ArrayList)(mat));
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38035 =
              cols;
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final x10.array.VarRow t38037 =
              ((x10.array.VarRow)(new x10.array.VarRow((java.lang.System[]) null).$init(((int)(t38035)),
                                                                                        ((x10.core.fun.Fun_0_1)(a)), (x10.array.VarRow.__1$1x10$lang$Int$3x10$lang$Int$2) null)));
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
((x10.util.ArrayList<x10.array.Row>)t38036).add__0x10$util$ArrayList$$T$O(((x10.array.Row)(t38037)));
        }
        
        
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
public int
                                                                                                    $apply$O(
                                                                                                    final int i,
                                                                                                    final int j){
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final x10.util.ArrayList<x10.array.Row> t38038 =
              ((x10.util.ArrayList)(mat));
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final x10.array.Row t38039 =
              ((x10.array.Row)(((x10.util.ArrayList<x10.array.Row>)t38038).$apply$G((int)(i))));
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38040 =
              t38039.$apply$O((int)(j));
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
return t38040;
        }
        
        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
public void
                                                                                                    $set(
                                                                                                    final int i,
                                                                                                    final int j,
                                                                                                    final int v){
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38041 =
              ((i) + (((int)(1))));
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
this.need((int)(t38041));
            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final x10.util.ArrayList<x10.array.Row> t38042 =
              ((x10.util.ArrayList)(mat));
            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final x10.array.Row t38043 =
              ((x10.array.Row)(((x10.util.ArrayList<x10.array.Row>)t38042).$apply$G((int)(i))));
            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
t38043.$set$O((int)(j),
                                                                                                                    (int)(v));
        }
        
        
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
public void
                                                                                                    setDiagonal__3$1x10$lang$Int$3x10$lang$Int$2(
                                                                                                    final int i,
                                                                                                    final int j,
                                                                                                    final int n,
                                                                                                    final x10.core.fun.Fun_0_1 v){
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38044 =
              ((i) + (((int)(n))));
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
this.need((int)(t38044));
            
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
int k38097 =
              0;
            
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
for (;
                                                                                                           true;
                                                                                                           ) {
                
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38098 =
                  k38097;
                
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final boolean t38099 =
                  ((t38098) < (((int)(n))));
                
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
if (!(t38099)) {
                    
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
break;
                }
                
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final x10.util.ArrayList<x10.array.Row> t38087 =
                  ((x10.util.ArrayList)(mat));
                
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38088 =
                  k38097;
                
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38089 =
                  ((i) + (((int)(t38088))));
                
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final x10.array.Row t38090 =
                  ((x10.array.Row)(((x10.util.ArrayList<x10.array.Row>)t38087).$apply$G((int)(t38089))));
                
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38091 =
                  k38097;
                
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38092 =
                  ((j) + (((int)(t38091))));
                
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38093 =
                  k38097;
                
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38094 =
                  x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)v).$apply(x10.core.Int.$box(t38093),x10.rtt.Types.INT));
                
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
t38090.$set$O((int)(t38092),
                                                                                                                        (int)(t38094));
                
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38095 =
                  k38097;
                
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38096 =
                  ((t38095) + (((int)(1))));
                
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
k38097 = t38096;
            }
        }
        
        
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
public void
                                                                                                    setColumn__3$1x10$lang$Int$3x10$lang$Int$2(
                                                                                                    final int i,
                                                                                                    final int j,
                                                                                                    final int n,
                                                                                                    final x10.core.fun.Fun_0_1 v){
            
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38058 =
              ((i) + (((int)(n))));
            
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
this.need((int)(t38058));
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
int k38108 =
              0;
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
for (;
                                                                                                           true;
                                                                                                           ) {
                
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38109 =
                  k38108;
                
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final boolean t38110 =
                  ((t38109) < (((int)(n))));
                
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
if (!(t38110)) {
                    
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
break;
                }
                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final x10.util.ArrayList<x10.array.Row> t38100 =
                  ((x10.util.ArrayList)(mat));
                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38101 =
                  k38108;
                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38102 =
                  ((i) + (((int)(t38101))));
                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final x10.array.Row t38103 =
                  ((x10.array.Row)(((x10.util.ArrayList<x10.array.Row>)t38100).$apply$G((int)(t38102))));
                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38104 =
                  k38108;
                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38105 =
                  x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)v).$apply(x10.core.Int.$box(t38104),x10.rtt.Types.INT));
                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
t38103.$set$O((int)(j),
                                                                                                                        (int)(t38105));
                
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38106 =
                  k38108;
                
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38107 =
                  ((t38106) + (((int)(1))));
                
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
k38108 = t38107;
            }
        }
        
        
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
public void
                                                                                                    setRow__3$1x10$lang$Int$3x10$lang$Int$2(
                                                                                                    final int i,
                                                                                                    final int j,
                                                                                                    final int n,
                                                                                                    final x10.core.fun.Fun_0_1 v){
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38070 =
              ((i) + (((int)(1))));
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
this.need((int)(t38070));
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
int k38119 =
              0;
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
for (;
                                                                                                           true;
                                                                                                           ) {
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38120 =
                  k38119;
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final boolean t38121 =
                  ((t38120) < (((int)(n))));
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
if (!(t38121)) {
                    
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
break;
                }
                
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final x10.util.ArrayList<x10.array.Row> t38111 =
                  ((x10.util.ArrayList)(mat));
                
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final x10.array.Row t38112 =
                  ((x10.array.Row)(((x10.util.ArrayList<x10.array.Row>)t38111).$apply$G((int)(i))));
                
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38113 =
                  k38119;
                
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38114 =
                  ((j) + (((int)(t38113))));
                
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38115 =
                  k38119;
                
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38116 =
                  x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)v).$apply(x10.core.Int.$box(t38115),x10.rtt.Types.INT));
                
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
t38112.$set$O((int)(t38114),
                                                                                                                        (int)(t38116));
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38117 =
                  k38119;
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38118 =
                  ((t38117) + (((int)(1))));
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
k38119 = t38118;
            }
        }
        
        
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
private void
                                                                                                    need(
                                                                                                    final int n){
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final x10.util.ArrayList<x10.array.Row> t38082 =
              ((x10.util.ArrayList)(this.
                                      mat));
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38083 =
              this.
                cols;
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
x10.array.MatBuilder.need__1$1x10$array$Row$2((int)(n),
                                                                                                                                                    ((x10.util.ArrayList)(t38082)),
                                                                                                                                                    (int)(t38083));
        }
        
        public static void
          need$P(
          final int n,
          final x10.array.MatBuilder MatBuilder){
            MatBuilder.need((int)(n));
        }
        
        
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
private static void
                                                                                                    need__1$1x10$array$Row$2(
                                                                                                    final int n,
                                                                                                    final x10.util.ArrayList<x10.array.Row> mat,
                                                                                                    final int cols){
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
while (true) {
                
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final int t38084 =
                  ((x10.util.ArrayList<x10.array.Row>)mat).size$O();
                
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final boolean t38086 =
                  ((t38084) < (((int)(n))));
                
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
if (!(t38086)) {
                    
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
break;
                }
                
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final x10.array.VarRow t38122 =
                  ((x10.array.VarRow)(new x10.array.VarRow((java.lang.System[]) null).$init(((int)(cols)))));
                
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
((x10.util.ArrayList<x10.array.Row>)mat).add__0x10$util$ArrayList$$T$O(((x10.array.Row)(t38122)));
            }
        }
        
        public static void
          need$P__1$1x10$array$Row$2(
          final int n,
          final x10.util.ArrayList<x10.array.Row> mat,
          final int cols){
            x10.array.MatBuilder.need__1$1x10$array$Row$2((int)(n),
                                                          ((x10.util.ArrayList)(mat)),
                                                          (int)(cols));
        }
        
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
final public x10.array.MatBuilder
                                                                                                    x10$array$MatBuilder$$x10$array$MatBuilder$this(
                                                                                                    ){
            
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/MatBuilder.x10"
return x10.array.MatBuilder.this;
        }
    
}
