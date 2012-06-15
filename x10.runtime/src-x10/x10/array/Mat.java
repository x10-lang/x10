package x10.array;


@x10.core.X10Generated abstract public class Mat<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.lang.Iterable, x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Mat.class);
    
    public static final x10.rtt.RuntimeType<Mat> $RTT = x10.rtt.NamedType.<Mat> make(
    "x10.array.Mat", /* base class */Mat.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.ParameterizedType.make(x10.lang.Iterable.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Mat $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Mat.class + " calling"); } 
        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
        x10.array.Array mat = (x10.array.Array) $deserializer.readRef();
        $_obj.mat = mat;
        $_obj.rows = $deserializer.readInt();
        $_obj.cols = $deserializer.readInt();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        return null;
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
        if (mat instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.mat);
        } else {
        $serializer.write(this.mat);
        }
        $serializer.write(this.rows);
        $serializer.write(this.cols);
        
    }
    
    // constructor just for allocation
    public Mat(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
    super($dummy);
    x10.array.Mat.$initParams(this, $T);
    }
    // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1){}:U
    public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
    return $apply$G(x10.core.Int.$unbox(a1));
    }
    
        private x10.rtt.Type $T;
        // initializer of type parameters
        public static void $initParams(final Mat $this, final x10.rtt.Type $T) {
        $this.$T = $T;
        }
        
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
public int rows;
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
public int cols;
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
public x10.array.Array<$T> mat;
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"

        // constructor for non-virtual call
        final public x10.array.Mat<$T> x10$array$Mat$$init$S(final int rows,
                                                             final int cols,
                                                             final x10.array.Array<$T> mat, __2$1x10$array$Mat$$T$2 $dummy) { {
                                                                                                                                     
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"

                                                                                                                                     
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
this.rows = rows;
                                                                                                                                     this.cols = cols;
                                                                                                                                     
                                                                                                                                     
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
this.mat = ((x10.array.Array)(mat));
                                                                                                                                 }
                                                                                                                                 return this;
                                                                                                                                 }
        
        // constructor
        public x10.array.Mat<$T> $init(final int rows,
                                       final int cols,
                                       final x10.array.Array<$T> mat, __2$1x10$array$Mat$$T$2 $dummy){return x10$array$Mat$$init$S(rows,cols,mat, $dummy);}
        
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
public $T
                                                                                             $apply$G(
                                                                                             final int i){
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
final x10.array.Array<$T> t37923 =
              ((x10.array.Array)(mat));
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
final $T t37924 =
              (($T)(((x10.array.Array<$T>)t37923).$apply$G((int)(i))));
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
return t37924;
        }
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
public x10.lang.Iterator<$T>
                                                                                             iterator(
                                                                                             ){
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
final x10.array.Array<$T> t37925 =
              ((x10.array.Array)(mat));
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
final x10.lang.Iterable<$T> t37926 =
              ((x10.lang.Iterable<$T>)
                ((x10.array.Array<$T>)t37925).values());
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
final x10.lang.Iterator<$T> t37927 =
              ((x10.lang.Iterator<$T>)
                ((x10.lang.Iterable<$T>)t37926).iterator());
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
return t37927;
        }
        
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
public void
                                                                                             printInfo(
                                                                                             final x10.io.Printer ps,
                                                                                             final java.lang.String label){
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
ps.printf(((java.lang.String)("%s\n")),
                                                                                                         ((java.lang.Object)(label)));
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
int row =
              0;
            
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
final x10.lang.Iterator<$T> r37941 =
              this.iterator();
            
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
for (;
                                                                                                    true;
                                                                                                    ) {
                
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
final boolean t37942 =
                  ((x10.lang.Iterator<$T>)r37941).hasNext$O();
                
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
if (!(t37942)) {
                    
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
break;
                }
                
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
final $T t37935 =
                  (($T)(((x10.lang.Iterator<$T>)r37941).next$G()));
                
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
final x10.array.Row r37936 =
                  ((x10.array.Row)
                    t37935);
                
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
ps.printf(((java.lang.String)("    ")));
                
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
final int t37937 =
                  row;
                
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
final int t37938 =
                  ((t37937) + (((int)(1))));
                
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
final int t37939 =
                  row = t37938;
                
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
final int t37940 =
                  ((t37939) - (((int)(1))));
                
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
r37936.printInfo(((x10.io.Printer)(ps)),
                                                                                                                    (int)(t37940));
            }
        }
        
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
final public x10.array.Mat<$T>
                                                                                             x10$array$Mat$$x10$array$Mat$this(
                                                                                             ){
            
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Mat.x10"
return x10.array.Mat.this;
        }
    // synthetic type for parameter mangling
    public abstract static class __2$1x10$array$Mat$$T$2 {}
    
}
