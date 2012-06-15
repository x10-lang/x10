package x10.array;


@x10.core.X10Generated final public class Point extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.util.Ordered, java.lang.Comparable<x10.array.Point>, x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Point.class);
    
    public static final x10.rtt.RuntimeType<Point> $RTT = x10.rtt.NamedType.<Point> make(
    "x10.array.Point", /* base class */Point.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.ParameterizedType.make(x10.util.Ordered.$RTT, x10.rtt.UnresolvedType.THIS), x10.rtt.ParameterizedType.make(x10.rtt.Types.COMPARABLE, x10.rtt.UnresolvedType.THIS), x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Point $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Point.class + " calling"); } 
        $_obj.c0 = $deserializer.readInt();
        $_obj.c1 = $deserializer.readInt();
        $_obj.c2 = $deserializer.readInt();
        $_obj.c3 = $deserializer.readInt();
        x10.array.Array cs = (x10.array.Array) $deserializer.readRef();
        $_obj.cs = cs;
        $_obj.rank = $deserializer.readInt();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Point $_obj = new Point((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.write(this.c0);
        $serializer.write(this.c1);
        $serializer.write(this.c2);
        $serializer.write(this.c3);
        if (cs instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.cs);
        } else {
        $serializer.write(this.cs);
        }
        $serializer.write(this.rank);
        
    }
    
    // constructor just for allocation
    public Point(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1){}:U
    public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
    return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
    }
    // dispatcher for method abstract public x10.util.Ordered.operator<(that:T):x10.lang.Boolean
    public java.lang.Object $lt(final java.lang.Object a1, final x10.rtt.Type t1) {
    return x10.core.Boolean.$box($lt$O((x10.array.Point)a1));
    }
    // dispatcher for method abstract public x10.util.Ordered.operator>(that:T):x10.lang.Boolean
    public java.lang.Object $gt(final java.lang.Object a1, final x10.rtt.Type t1) {
    return x10.core.Boolean.$box($gt$O((x10.array.Point)a1));
    }
    // dispatcher for method abstract public x10.util.Ordered.operator<=(that:T):x10.lang.Boolean
    public java.lang.Object $le(final java.lang.Object a1, final x10.rtt.Type t1) {
    return x10.core.Boolean.$box($le$O((x10.array.Point)a1));
    }
    // dispatcher for method abstract public x10.util.Ordered.operator>=(that:T):x10.lang.Boolean
    public java.lang.Object $ge(final java.lang.Object a1, final x10.rtt.Type t1) {
    return x10.core.Boolean.$box($ge$O((x10.array.Point)a1));
    }
    
        
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public int rank;
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public int c0;
        
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public int c1;
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public int c2;
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public int c3;
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public x10.array.Array<x10.core.Int> cs;
        
        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
// creation method for java code (1-phase java constructor)
        public Point(final x10.array.Array<x10.core.Int> coords, __0$1x10$lang$Int$2 $dummy){this((java.lang.System[]) null);
                                                                                                 $init(coords, (x10.array.Point.__0$1x10$lang$Int$2) null);}
        
        // constructor for non-virtual call
        final public x10.array.Point x10$array$Point$$init$S(final x10.array.Array<x10.core.Int> coords, __0$1x10$lang$Int$2 $dummy) { {
                                                                                                                                              
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"

                                                                                                                                              
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40415 =
                                                                                                                                                ((x10.array.Array<x10.core.Int>)coords).
                                                                                                                                                  size;
                                                                                                                                              
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
this.rank = t40415;
                                                                                                                                              
                                                                                                                                              
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40093 =
                                                                                                                                                x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)coords).$apply$G((int)(0)));
                                                                                                                                              
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
this.c0 = t40093;
                                                                                                                                              
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40094 =
                                                                                                                                                rank;
                                                                                                                                              
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40095 =
                                                                                                                                                ((t40094) > (((int)(1))));
                                                                                                                                              
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
int t40096 =
                                                                                                                                                 0;
                                                                                                                                              
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40095) {
                                                                                                                                                  
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
t40096 = x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)coords).$apply$G((int)(1)));
                                                                                                                                              } else {
                                                                                                                                                  
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
t40096 = 0;
                                                                                                                                              }
                                                                                                                                              
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40097 =
                                                                                                                                                t40096;
                                                                                                                                              
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
this.c1 = t40097;
                                                                                                                                              
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40098 =
                                                                                                                                                rank;
                                                                                                                                              
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40099 =
                                                                                                                                                ((t40098) > (((int)(2))));
                                                                                                                                              
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
int t40100 =
                                                                                                                                                 0;
                                                                                                                                              
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40099) {
                                                                                                                                                  
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
t40100 = x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)coords).$apply$G((int)(2)));
                                                                                                                                              } else {
                                                                                                                                                  
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
t40100 = 0;
                                                                                                                                              }
                                                                                                                                              
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40101 =
                                                                                                                                                t40100;
                                                                                                                                              
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
this.c2 = t40101;
                                                                                                                                              
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40102 =
                                                                                                                                                rank;
                                                                                                                                              
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40103 =
                                                                                                                                                ((t40102) > (((int)(3))));
                                                                                                                                              
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
int t40104 =
                                                                                                                                                 0;
                                                                                                                                              
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40103) {
                                                                                                                                                  
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
t40104 = x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)coords).$apply$G((int)(3)));
                                                                                                                                              } else {
                                                                                                                                                  
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
t40104 = 0;
                                                                                                                                              }
                                                                                                                                              
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40105 =
                                                                                                                                                t40104;
                                                                                                                                              
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
this.c3 = t40105;
                                                                                                                                              
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40106 =
                                                                                                                                                rank;
                                                                                                                                              
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40107 =
                                                                                                                                                ((t40106) > (((int)(4))));
                                                                                                                                              
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
x10.array.Array<x10.core.Int> t40108 =
                                                                                                                                                 null;
                                                                                                                                              
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40107) {
                                                                                                                                                  
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
t40108 = ((x10.array.Array)(coords));
                                                                                                                                              } else {
                                                                                                                                                  
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
t40108 = null;
                                                                                                                                              }
                                                                                                                                              
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Array<x10.core.Int> t40109 =
                                                                                                                                                ((x10.array.Array)(t40108));
                                                                                                                                              
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
this.cs = ((x10.array.Array)(t40109));
                                                                                                                                          }
                                                                                                                                          return this;
                                                                                                                                          }
        
        // constructor
        public x10.array.Point $init(final x10.array.Array<x10.core.Int> coords, __0$1x10$lang$Int$2 $dummy){return x10$array$Point$$init$S(coords, $dummy);}
        
        
        
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
// creation method for java code (1-phase java constructor)
        public Point(final int i0){this((java.lang.System[]) null);
                                       $init(i0);}
        
        // constructor for non-virtual call
        final public x10.array.Point x10$array$Point$$init$S(final int i0) { {
                                                                                    
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"

                                                                                    
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
this.rank = 1;
                                                                                    
                                                                                    
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
this.c0 = i0;
                                                                                    
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40110 =
                                                                                      this.c3 = 0;
                                                                                    
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40111 =
                                                                                      this.c2 = t40110;
                                                                                    
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
this.c1 = t40111;
                                                                                    
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
this.cs = null;
                                                                                }
                                                                                return this;
                                                                                }
        
        // constructor
        public x10.array.Point $init(final int i0){return x10$array$Point$$init$S(i0);}
        
        
        
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
// creation method for java code (1-phase java constructor)
        public Point(final int i0,
                     final int i1){this((java.lang.System[]) null);
                                       $init(i0,i1);}
        
        // constructor for non-virtual call
        final public x10.array.Point x10$array$Point$$init$S(final int i0,
                                                             final int i1) { {
                                                                                    
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"

                                                                                    
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
this.rank = 2;
                                                                                    
                                                                                    
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
this.c0 = i0;
                                                                                    
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
this.c1 = i1;
                                                                                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40112 =
                                                                                      this.c3 = 0;
                                                                                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
this.c2 = t40112;
                                                                                    
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
this.cs = null;
                                                                                }
                                                                                return this;
                                                                                }
        
        // constructor
        public x10.array.Point $init(final int i0,
                                     final int i1){return x10$array$Point$$init$S(i0,i1);}
        
        
        
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
// creation method for java code (1-phase java constructor)
        public Point(final int i0,
                     final int i1,
                     final int i2){this((java.lang.System[]) null);
                                       $init(i0,i1,i2);}
        
        // constructor for non-virtual call
        final public x10.array.Point x10$array$Point$$init$S(final int i0,
                                                             final int i1,
                                                             final int i2) { {
                                                                                    
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"

                                                                                    
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
this.rank = 3;
                                                                                    
                                                                                    
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
this.c0 = i0;
                                                                                    
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
this.c1 = i1;
                                                                                    
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
this.c2 = i2;
                                                                                    
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
this.c3 = 0;
                                                                                    
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
this.cs = null;
                                                                                }
                                                                                return this;
                                                                                }
        
        // constructor
        public x10.array.Point $init(final int i0,
                                     final int i1,
                                     final int i2){return x10$array$Point$$init$S(i0,i1,i2);}
        
        
        
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
// creation method for java code (1-phase java constructor)
        public Point(final int i0,
                     final int i1,
                     final int i2,
                     final int i3){this((java.lang.System[]) null);
                                       $init(i0,i1,i2,i3);}
        
        // constructor for non-virtual call
        final public x10.array.Point x10$array$Point$$init$S(final int i0,
                                                             final int i1,
                                                             final int i2,
                                                             final int i3) { {
                                                                                    
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"

                                                                                    
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
this.rank = 4;
                                                                                    
                                                                                    
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
this.c0 = i0;
                                                                                    
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
this.c1 = i1;
                                                                                    
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
this.c2 = i2;
                                                                                    
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
this.c3 = i3;
                                                                                    
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
this.cs = null;
                                                                                }
                                                                                return this;
                                                                                }
        
        // constructor
        public x10.array.Point $init(final int i0,
                                     final int i1,
                                     final int i2,
                                     final int i3){return x10$array$Point$$init$S(i0,i1,i2,i3);}
        
        
        
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public int
                                                                                               $apply$O(
                                                                                               final int i){
            
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
boolean t40114 =
              ((i) < (((int)(0))));
            
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (!(t40114)) {
                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40113 =
                  rank;
                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
t40114 = ((i) >= (((int)(t40113))));
            }
            
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40119 =
              t40114;
            
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40119) {
                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final java.lang.String t40115 =
                  (("index ") + ((x10.core.Int.$box(i))));
                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final java.lang.String t40116 =
                  ((t40115) + (" not contained in "));
                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final java.lang.String t40117 =
                  ((t40116) + (this));
                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.lang.ArrayIndexOutOfBoundsException t40118 =
                  ((x10.lang.ArrayIndexOutOfBoundsException)(new x10.lang.ArrayIndexOutOfBoundsException(t40117)));
                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
throw t40118;
            }
            
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
switch (i) {
                
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
case 0:
                    
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40120 =
                      c0;
                    
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40120;
                
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
case 1:
                    
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40121 =
                      c1;
                    
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40121;
                
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
case 2:
                    
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40122 =
                      c2;
                    
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40122;
                
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
case 3:
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40123 =
                      c3;
                    
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40123;
                
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
default:
                    
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Array<x10.core.Int> t40124 =
                      ((x10.array.Array)(cs));
                    
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40125 =
                      x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)t40124).$apply$G((int)(i)));
                    
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40125;
            }
        }
        
        
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>
                                                                                               coords(
                                                                                               ){
            
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t40127 =
              ((x10.core.fun.Fun_0_1)(new x10.array.Point.$Closure$32(this)));
            
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40127;
        }
        
        
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public static <$T>x10.array.Point
                                                                                                make__0$1x10$array$Point$$T$2(
                                                                                                final x10.rtt.Type $T,
                                                                                                final x10.array.Array<$T> r){
            
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40175 =
              ((x10.array.Array<$T>)r).
                size;
            
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
switch (t40175) {
                
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
case 1:
                    
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final $T t40128 =
                      (($T)(((x10.array.Array<$T>)r).$apply$G((int)(0))));
                    
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40129 =
                      ((x10.array.Point)(new x10.array.Point((java.lang.System[]) null).$init(x10.rtt.Types.asint(t40128,$T))));
                    
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point __desugarer__var__13__40068 =
                      ((x10.array.Point)(((x10.array.Point)
                                           t40129)));
                    
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
x10.array.Point ret40069 =
                       null;
                    {
                        
//#line 102 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40130 =
                          __desugarer__var__13__40068.
                            rank;
                        
//#line 102 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40131 =
                          ((x10.array.Array<$T>)r).
                            size;
                        
//#line 102 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40132 =
                          ((int) t40130) ==
                        ((int) t40131);
                        
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40135 =
                          !(t40132);
                        
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40135) {
                            
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40134 =
                              true;
                            
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40134) {
                                
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.lang.FailedDynamicCheckException t40133 =
                                  new x10.lang.FailedDynamicCheckException("x10.array.Point{self.rank==r.size}");
                                
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
throw t40133;
                            }
                        }
                        
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
ret40069 = ((x10.array.Point)(__desugarer__var__13__40068));
                    }
                    
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40136 =
                      ((x10.array.Point)(ret40069));
                    
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40136;
                
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
case 2:
                    
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final $T t40137 =
                      (($T)(((x10.array.Array<$T>)r).$apply$G((int)(0))));
                    
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final $T t40138 =
                      (($T)(((x10.array.Array<$T>)r).$apply$G((int)(1))));
                    
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40139 =
                      ((x10.array.Point)(new x10.array.Point((java.lang.System[]) null).$init(x10.rtt.Types.asint(t40137,$T),
                                                                                              x10.rtt.Types.asint(t40138,$T))));
                    
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point __desugarer__var__14__40071 =
                      ((x10.array.Point)(((x10.array.Point)
                                           t40139)));
                    
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
x10.array.Point ret40072 =
                       null;
                    {
                        
//#line 103 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40140 =
                          __desugarer__var__14__40071.
                            rank;
                        
//#line 103 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40141 =
                          ((x10.array.Array<$T>)r).
                            size;
                        
//#line 103 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40142 =
                          ((int) t40140) ==
                        ((int) t40141);
                        
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40145 =
                          !(t40142);
                        
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40145) {
                            
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40144 =
                              true;
                            
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40144) {
                                
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.lang.FailedDynamicCheckException t40143 =
                                  new x10.lang.FailedDynamicCheckException("x10.array.Point{self.rank==r.size}");
                                
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
throw t40143;
                            }
                        }
                        
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
ret40072 = ((x10.array.Point)(__desugarer__var__14__40071));
                    }
                    
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40146 =
                      ((x10.array.Point)(ret40072));
                    
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40146;
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
case 3:
                    
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final $T t40147 =
                      (($T)(((x10.array.Array<$T>)r).$apply$G((int)(0))));
                    
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final $T t40148 =
                      (($T)(((x10.array.Array<$T>)r).$apply$G((int)(1))));
                    
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final $T t40149 =
                      (($T)(((x10.array.Array<$T>)r).$apply$G((int)(2))));
                    
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40150 =
                      ((x10.array.Point)(new x10.array.Point((java.lang.System[]) null).$init(x10.rtt.Types.asint(t40147,$T),
                                                                                              x10.rtt.Types.asint(t40148,$T),
                                                                                              x10.rtt.Types.asint(t40149,$T))));
                    
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point __desugarer__var__15__40074 =
                      ((x10.array.Point)(((x10.array.Point)
                                           t40150)));
                    
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
x10.array.Point ret40075 =
                       null;
                    {
                        
//#line 104 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40151 =
                          __desugarer__var__15__40074.
                            rank;
                        
//#line 104 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40152 =
                          ((x10.array.Array<$T>)r).
                            size;
                        
//#line 104 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40153 =
                          ((int) t40151) ==
                        ((int) t40152);
                        
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40156 =
                          !(t40153);
                        
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40156) {
                            
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40155 =
                              true;
                            
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40155) {
                                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.lang.FailedDynamicCheckException t40154 =
                                  new x10.lang.FailedDynamicCheckException("x10.array.Point{self.rank==r.size}");
                                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
throw t40154;
                            }
                        }
                        
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
ret40075 = ((x10.array.Point)(__desugarer__var__15__40074));
                    }
                    
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40157 =
                      ((x10.array.Point)(ret40075));
                    
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40157;
                
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
case 4:
                    
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final $T t40158 =
                      (($T)(((x10.array.Array<$T>)r).$apply$G((int)(0))));
                    
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final $T t40159 =
                      (($T)(((x10.array.Array<$T>)r).$apply$G((int)(1))));
                    
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final $T t40160 =
                      (($T)(((x10.array.Array<$T>)r).$apply$G((int)(2))));
                    
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final $T t40161 =
                      (($T)(((x10.array.Array<$T>)r).$apply$G((int)(3))));
                    
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40162 =
                      ((x10.array.Point)(new x10.array.Point((java.lang.System[]) null).$init(x10.rtt.Types.asint(t40158,$T),
                                                                                              x10.rtt.Types.asint(t40159,$T),
                                                                                              x10.rtt.Types.asint(t40160,$T),
                                                                                              x10.rtt.Types.asint(t40161,$T))));
                    
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point __desugarer__var__16__40077 =
                      ((x10.array.Point)(((x10.array.Point)
                                           t40162)));
                    
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
x10.array.Point ret40078 =
                       null;
                    {
                        
//#line 105 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40163 =
                          __desugarer__var__16__40077.
                            rank;
                        
//#line 105 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40164 =
                          ((x10.array.Array<$T>)r).
                            size;
                        
//#line 105 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40165 =
                          ((int) t40163) ==
                        ((int) t40164);
                        
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40168 =
                          !(t40165);
                        
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40168) {
                            
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40167 =
                              true;
                            
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40167) {
                                
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.lang.FailedDynamicCheckException t40166 =
                                  new x10.lang.FailedDynamicCheckException("x10.array.Point{self.rank==r.size}");
                                
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
throw t40166;
                            }
                        }
                        
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
ret40078 = ((x10.array.Point)(__desugarer__var__16__40077));
                    }
                    
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40169 =
                      ((x10.array.Point)(ret40078));
                    
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40169;
                
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
default:
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40171 =
                      ((x10.array.Array<$T>)r).
                        size;
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,$T> t40172 =
                      ((x10.core.fun.Fun_0_1)(new x10.array.Point.$Closure$33<$T>($T, r, (x10.array.Point.$Closure$33.__0$1x10$array$Point$$Closure$33$$T$2) null)));
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Array<x10.core.Int> t40173 =
                      ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t40171)),
                                                                                                                               ((x10.core.fun.Fun_0_1)(t40172)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40174 =
                      ((x10.array.Point)(new x10.array.Point((java.lang.System[]) null).$init(((x10.array.Array)(t40173)), (x10.array.Point.__0$1x10$lang$Int$2) null)));
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40174;
            }
        }
        
        
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public static x10.array.Point
                                                                                                make__1$1x10$lang$Int$3x10$lang$Int$2(
                                                                                                final int rank,
                                                                                                final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> init){
            
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
switch (rank) {
                
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
case 1:
                    
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40176 =
                      x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)init).$apply(x10.core.Int.$box(0),x10.rtt.Types.INT));
                    
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40177 =
                      ((x10.array.Point)(new x10.array.Point((java.lang.System[]) null).$init(t40176)));
                    
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point __desugarer__var__17__40080 =
                      ((x10.array.Point)(((x10.array.Point)
                                           t40177)));
                    
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
x10.array.Point ret40081 =
                       null;
                    {
                        
//#line 115 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40178 =
                          __desugarer__var__17__40080.
                            rank;
                        
//#line 115 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40179 =
                          ((int) t40178) ==
                        ((int) rank);
                        
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40182 =
                          !(t40179);
                        
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40182) {
                            
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40181 =
                              true;
                            
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40181) {
                                
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.lang.FailedDynamicCheckException t40180 =
                                  new x10.lang.FailedDynamicCheckException("x10.array.Point{self.rank==rank}");
                                
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
throw t40180;
                            }
                        }
                        
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
ret40081 = ((x10.array.Point)(__desugarer__var__17__40080));
                    }
                    
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40183 =
                      ((x10.array.Point)(ret40081));
                    
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40183;
                
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
case 2:
                    
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40184 =
                      x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)init).$apply(x10.core.Int.$box(0),x10.rtt.Types.INT));
                    
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40185 =
                      x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)init).$apply(x10.core.Int.$box(1),x10.rtt.Types.INT));
                    
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40186 =
                      ((x10.array.Point)(new x10.array.Point((java.lang.System[]) null).$init(t40184,
                                                                                              t40185)));
                    
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point __desugarer__var__18__40083 =
                      ((x10.array.Point)(((x10.array.Point)
                                           t40186)));
                    
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
x10.array.Point ret40084 =
                       null;
                    {
                        
//#line 116 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40187 =
                          __desugarer__var__18__40083.
                            rank;
                        
//#line 116 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40188 =
                          ((int) t40187) ==
                        ((int) rank);
                        
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40191 =
                          !(t40188);
                        
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40191) {
                            
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40190 =
                              true;
                            
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40190) {
                                
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.lang.FailedDynamicCheckException t40189 =
                                  new x10.lang.FailedDynamicCheckException("x10.array.Point{self.rank==rank}");
                                
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
throw t40189;
                            }
                        }
                        
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
ret40084 = ((x10.array.Point)(__desugarer__var__18__40083));
                    }
                    
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40192 =
                      ((x10.array.Point)(ret40084));
                    
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40192;
                
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
case 3:
                    
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40193 =
                      x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)init).$apply(x10.core.Int.$box(0),x10.rtt.Types.INT));
                    
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40194 =
                      x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)init).$apply(x10.core.Int.$box(1),x10.rtt.Types.INT));
                    
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40195 =
                      x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)init).$apply(x10.core.Int.$box(2),x10.rtt.Types.INT));
                    
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40196 =
                      ((x10.array.Point)(new x10.array.Point((java.lang.System[]) null).$init(t40193,
                                                                                              t40194,
                                                                                              t40195)));
                    
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point __desugarer__var__19__40086 =
                      ((x10.array.Point)(((x10.array.Point)
                                           t40196)));
                    
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
x10.array.Point ret40087 =
                       null;
                    {
                        
//#line 117 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40197 =
                          __desugarer__var__19__40086.
                            rank;
                        
//#line 117 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40198 =
                          ((int) t40197) ==
                        ((int) rank);
                        
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40201 =
                          !(t40198);
                        
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40201) {
                            
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40200 =
                              true;
                            
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40200) {
                                
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.lang.FailedDynamicCheckException t40199 =
                                  new x10.lang.FailedDynamicCheckException("x10.array.Point{self.rank==rank}");
                                
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
throw t40199;
                            }
                        }
                        
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
ret40087 = ((x10.array.Point)(__desugarer__var__19__40086));
                    }
                    
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40202 =
                      ((x10.array.Point)(ret40087));
                    
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40202;
                
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
case 4:
                    
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40203 =
                      x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)init).$apply(x10.core.Int.$box(0),x10.rtt.Types.INT));
                    
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40204 =
                      x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)init).$apply(x10.core.Int.$box(1),x10.rtt.Types.INT));
                    
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40205 =
                      x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)init).$apply(x10.core.Int.$box(2),x10.rtt.Types.INT));
                    
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40206 =
                      x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)init).$apply(x10.core.Int.$box(3),x10.rtt.Types.INT));
                    
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40207 =
                      ((x10.array.Point)(new x10.array.Point((java.lang.System[]) null).$init(t40203,
                                                                                              t40204,
                                                                                              t40205,
                                                                                              t40206)));
                    
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point __desugarer__var__20__40089 =
                      ((x10.array.Point)(((x10.array.Point)
                                           t40207)));
                    
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
x10.array.Point ret40090 =
                       null;
                    {
                        
//#line 118 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40208 =
                          __desugarer__var__20__40089.
                            rank;
                        
//#line 118 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40209 =
                          ((int) t40208) ==
                        ((int) rank);
                        
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40212 =
                          !(t40209);
                        
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40212) {
                            
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40211 =
                              true;
                            
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40211) {
                                
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.lang.FailedDynamicCheckException t40210 =
                                  new x10.lang.FailedDynamicCheckException("x10.array.Point{self.rank==rank}");
                                
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
throw t40210;
                            }
                        }
                        
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
ret40090 = ((x10.array.Point)(__desugarer__var__20__40089));
                    }
                    
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40213 =
                      ((x10.array.Point)(ret40090));
                    
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40213;
                
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
default:
                    
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Array<x10.core.Int> t40214 =
                      ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(rank)),
                                                                                                                               ((x10.core.fun.Fun_0_1)(init)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                    
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40215 =
                      ((x10.array.Point)(new x10.array.Point((java.lang.System[]) null).$init(((x10.array.Array)(t40214)), (x10.array.Point.__0$1x10$lang$Int$2) null)));
                    
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40215;
            }
        }
        
        
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public static x10.array.Point
                                                                                                make(
                                                                                                final int i0){
            
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40216 =
              ((x10.array.Point)(new x10.array.Point((java.lang.System[]) null).$init(((int)(i0)))));
            
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40216;
        }
        
        
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public static x10.array.Point
                                                                                                make(
                                                                                                final int i0,
                                                                                                final int i1){
            
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40217 =
              ((x10.array.Point)(new x10.array.Point((java.lang.System[]) null).$init(((int)(i0)),
                                                                                      ((int)(i1)))));
            
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40217;
        }
        
        
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public static x10.array.Point
                                                                                                make(
                                                                                                final int i0,
                                                                                                final int i1,
                                                                                                final int i2){
            
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40218 =
              ((x10.array.Point)(new x10.array.Point((java.lang.System[]) null).$init(((int)(i0)),
                                                                                      ((int)(i1)),
                                                                                      ((int)(i2)))));
            
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40218;
        }
        
        
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public static x10.array.Point
                                                                                                make(
                                                                                                final int i0,
                                                                                                final int i1,
                                                                                                final int i2,
                                                                                                final int i3){
            
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40219 =
              ((x10.array.Point)(new x10.array.Point((java.lang.System[]) null).$init(((int)(i0)),
                                                                                      ((int)(i1)),
                                                                                      ((int)(i2)),
                                                                                      ((int)(i3)))));
            
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40219;
        }
        
        
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public static <$T>x10.array.Point
                                                                                                $implicit_convert__0$1x10$array$Point$$T$2(
                                                                                                final x10.rtt.Type $T,
                                                                                                final x10.array.Array<$T> a){
            
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40220 =
              ((x10.array.Point)(x10.array.Point.<$T>make__0$1x10$array$Point$$T$2($T, ((x10.array.Array)(a)))));
            
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40220;
        }
        
        
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public x10.array.Point
                                                                                                $plus(
                                                                                                ){
            
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return this;
        }
        
        
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public x10.array.Point
                                                                                                $minus(
                                                                                                ){
            
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40223 =
              rank;
            
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t40224 =
              ((x10.core.fun.Fun_0_1)(new x10.array.Point.$Closure$34(this)));
            
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40225 =
              ((x10.array.Point)(x10.array.Point.make__1$1x10$lang$Int$3x10$lang$Int$2((int)(t40223),
                                                                                       ((x10.core.fun.Fun_0_1)(t40224)))));
            
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40225;
        }
        
        
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public x10.array.Point
                                                                                                $plus(
                                                                                                final x10.array.Point that){
            
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40229 =
              rank;
            
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t40230 =
              ((x10.core.fun.Fun_0_1)(new x10.array.Point.$Closure$35(this,
                                                                      that)));
            
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40231 =
              ((x10.array.Point)(x10.array.Point.make__1$1x10$lang$Int$3x10$lang$Int$2((int)(t40229),
                                                                                       ((x10.core.fun.Fun_0_1)(t40230)))));
            
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40231;
        }
        
        
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public x10.array.Point
                                                                                                $minus(
                                                                                                final x10.array.Point that){
            
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40235 =
              rank;
            
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t40236 =
              ((x10.core.fun.Fun_0_1)(new x10.array.Point.$Closure$36(this,
                                                                      that)));
            
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40237 =
              ((x10.array.Point)(x10.array.Point.make__1$1x10$lang$Int$3x10$lang$Int$2((int)(t40235),
                                                                                       ((x10.core.fun.Fun_0_1)(t40236)))));
            
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40237;
        }
        
        
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public x10.array.Point
                                                                                                $times(
                                                                                                final x10.array.Point that){
            
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40241 =
              rank;
            
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t40242 =
              ((x10.core.fun.Fun_0_1)(new x10.array.Point.$Closure$37(this,
                                                                      that)));
            
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40243 =
              ((x10.array.Point)(x10.array.Point.make__1$1x10$lang$Int$3x10$lang$Int$2((int)(t40241),
                                                                                       ((x10.core.fun.Fun_0_1)(t40242)))));
            
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40243;
        }
        
        
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public x10.array.Point
                                                                                                $over(
                                                                                                final x10.array.Point that){
            
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40247 =
              rank;
            
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t40248 =
              ((x10.core.fun.Fun_0_1)(new x10.array.Point.$Closure$38(this,
                                                                      that)));
            
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40249 =
              ((x10.array.Point)(x10.array.Point.make__1$1x10$lang$Int$3x10$lang$Int$2((int)(t40247),
                                                                                       ((x10.core.fun.Fun_0_1)(t40248)))));
            
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40249;
        }
        
        
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public x10.array.Point
                                                                                                $plus(
                                                                                                final int c){
            
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40252 =
              rank;
            
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t40253 =
              ((x10.core.fun.Fun_0_1)(new x10.array.Point.$Closure$39(this,
                                                                      c)));
            
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40254 =
              ((x10.array.Point)(x10.array.Point.make__1$1x10$lang$Int$3x10$lang$Int$2((int)(t40252),
                                                                                       ((x10.core.fun.Fun_0_1)(t40253)))));
            
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40254;
        }
        
        
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public x10.array.Point
                                                                                                $minus(
                                                                                                final int c){
            
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40257 =
              rank;
            
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t40258 =
              ((x10.core.fun.Fun_0_1)(new x10.array.Point.$Closure$40(this,
                                                                      c)));
            
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40259 =
              ((x10.array.Point)(x10.array.Point.make__1$1x10$lang$Int$3x10$lang$Int$2((int)(t40257),
                                                                                       ((x10.core.fun.Fun_0_1)(t40258)))));
            
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40259;
        }
        
        
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public x10.array.Point
                                                                                                $times(
                                                                                                final int c){
            
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40262 =
              rank;
            
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t40263 =
              ((x10.core.fun.Fun_0_1)(new x10.array.Point.$Closure$41(this,
                                                                      c)));
            
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40264 =
              ((x10.array.Point)(x10.array.Point.make__1$1x10$lang$Int$3x10$lang$Int$2((int)(t40262),
                                                                                       ((x10.core.fun.Fun_0_1)(t40263)))));
            
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40264;
        }
        
        
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public x10.array.Point
                                                                                                $over(
                                                                                                final int c){
            
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40267 =
              rank;
            
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t40268 =
              ((x10.core.fun.Fun_0_1)(new x10.array.Point.$Closure$42(this,
                                                                      c)));
            
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40269 =
              ((x10.array.Point)(x10.array.Point.make__1$1x10$lang$Int$3x10$lang$Int$2((int)(t40267),
                                                                                       ((x10.core.fun.Fun_0_1)(t40268)))));
            
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40269;
        }
        
        
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public x10.array.Point
                                                                                                $inv_plus(
                                                                                                final int c){
            
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40272 =
              rank;
            
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t40273 =
              ((x10.core.fun.Fun_0_1)(new x10.array.Point.$Closure$43(this,
                                                                      c)));
            
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40274 =
              ((x10.array.Point)(x10.array.Point.make__1$1x10$lang$Int$3x10$lang$Int$2((int)(t40272),
                                                                                       ((x10.core.fun.Fun_0_1)(t40273)))));
            
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40274;
        }
        
        
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public x10.array.Point
                                                                                                $inv_minus(
                                                                                                final int c){
            
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40277 =
              rank;
            
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t40278 =
              ((x10.core.fun.Fun_0_1)(new x10.array.Point.$Closure$44(this,
                                                                      c)));
            
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40279 =
              ((x10.array.Point)(x10.array.Point.make__1$1x10$lang$Int$3x10$lang$Int$2((int)(t40277),
                                                                                       ((x10.core.fun.Fun_0_1)(t40278)))));
            
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40279;
        }
        
        
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public x10.array.Point
                                                                                                $inv_times(
                                                                                                final int c){
            
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40282 =
              rank;
            
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t40283 =
              ((x10.core.fun.Fun_0_1)(new x10.array.Point.$Closure$45(this,
                                                                      c)));
            
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40284 =
              ((x10.array.Point)(x10.array.Point.make__1$1x10$lang$Int$3x10$lang$Int$2((int)(t40282),
                                                                                       ((x10.core.fun.Fun_0_1)(t40283)))));
            
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40284;
        }
        
        
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public x10.array.Point
                                                                                                $inv_over(
                                                                                                final int c){
            
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40287 =
              rank;
            
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t40288 =
              ((x10.core.fun.Fun_0_1)(new x10.array.Point.$Closure$46(this,
                                                                      c)));
            
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point t40289 =
              ((x10.array.Point)(x10.array.Point.make__1$1x10$lang$Int$3x10$lang$Int$2((int)(t40287),
                                                                                       ((x10.core.fun.Fun_0_1)(t40288)))));
            
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40289;
        }
        
        
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public int
                                                                                                compareTo(
                                                                                                final x10.array.Point that){
            
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40292 =
              this.equals(((java.lang.Object)(that)));
            
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
int t40293 =
               0;
            
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40292) {
                
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
t40293 = 0;
            } else {
                
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40290 =
                  this.$lt$O(((x10.array.Point)(that)));
                
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
int t40291 =
                   0;
                
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40290) {
                    
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
t40291 = -1;
                } else {
                    
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
t40291 = 1;
                }
                
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
t40293 = t40291;
            }
            
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40294 =
              t40293;
            
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40294;
        }
        
        
//#line 219 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public int
                                                                                                hashCode(
                                                                                                ){
            
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
int hc =
              this.$apply$O((int)(0));
            
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
int i40423 =
              1;
            
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
for (;
                                                                                                       true;
                                                                                                       ) {
                
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40424 =
                  i40423;
                
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40425 =
                  rank;
                
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40426 =
                  ((t40424) < (((int)(t40425))));
                
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (!(t40426)) {
                    
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
break;
                }
                
//#line 222 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40416 =
                  hc;
                
//#line 222 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40417 =
                  ((t40416) * (((int)(17))));
                
//#line 222 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40418 =
                  i40423;
                
//#line 222 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40419 =
                  this.$apply$O((int)(t40418));
                
//#line 222 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40420 =
                  ((t40417) ^ (((int)(t40419))));
                
//#line 222 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
hc = t40420;
                
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40421 =
                  i40423;
                
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40422 =
                  ((t40421) + (((int)(1))));
                
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
i40423 = t40422;
            }
            
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40306 =
              hc;
            
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40306;
        }
        
        
//#line 230 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public boolean
                                                                                                equals(
                                                                                                final java.lang.Object other){
            
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40307 =
              x10.array.Point.$RTT.isInstance(other);
            
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40308 =
              !(t40307);
            
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40308) {
                
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return false;
            }
            
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final x10.array.Point otherPoint =
              ((x10.array.Point)(x10.rtt.Types.<x10.array.Point> cast(other,x10.array.Point.$RTT)));
            
//#line 233 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40309 =
              rank;
            
//#line 233 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40310 =
              otherPoint.
                rank;
            
//#line 233 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40311 =
              ((int) t40309) !=
            ((int) t40310);
            
//#line 233 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40311) {
                
//#line 233 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return false;
            }
            
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
int i40435 =
              0;
            
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
for (;
                                                                                                       true;
                                                                                                       ) {
                
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40436 =
                  i40435;
                
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40437 =
                  rank;
                
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40438 =
                  ((t40436) < (((int)(t40437))));
                
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (!(t40438)) {
                    
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
break;
                }
                
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40427 =
                  i40435;
                
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40428 =
                  this.$apply$O((int)(t40427));
                
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40429 =
                  i40435;
                
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40430 =
                  otherPoint.$apply$O((int)(t40429));
                
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40431 =
                  ((int) t40428) ==
                ((int) t40430);
                
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40432 =
                  !(t40431);
                
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40432) {
                    
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return false;
                }
                
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40433 =
                  i40435;
                
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40434 =
                  ((t40433) + (((int)(1))));
                
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
i40435 = t40434;
            }
            
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return true;
        }
        
        
//#line 242 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public boolean
                                                                                                $lt$O(
                                                                                                final x10.array.Point that){
            
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
int i40447 =
              0;
            
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
for (;
                                                                                                       true;
                                                                                                       ) {
                
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40448 =
                  i40447;
                
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40449 =
                  rank;
                
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40450 =
                  ((t40449) - (((int)(1))));
                
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40451 =
                  ((t40448) < (((int)(t40450))));
                
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (!(t40451)) {
                    
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
break;
                }
                
//#line 244 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40439 =
                  i40447;
                
//#line 244 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int a40440 =
                  this.$apply$O((int)(t40439));
                
//#line 245 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40441 =
                  i40447;
                
//#line 245 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int b40442 =
                  that.$apply$O((int)(t40441));
                
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40443 =
                  ((a40440) > (((int)(b40442))));
                
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40443) {
                    
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return false;
                }
                
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40444 =
                  ((a40440) < (((int)(b40442))));
                
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40444) {
                    
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return true;
                }
                
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40445 =
                  i40447;
                
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40446 =
                  ((t40445) + (((int)(1))));
                
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
i40447 = t40446;
            }
            
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40335 =
              rank;
            
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40336 =
              ((t40335) - (((int)(1))));
            
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40339 =
              this.$apply$O((int)(t40336));
            
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40337 =
              rank;
            
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40338 =
              ((t40337) - (((int)(1))));
            
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40340 =
              that.$apply$O((int)(t40338));
            
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40341 =
              ((t40339) < (((int)(t40340))));
            
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40341;
        }
        
        
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public boolean
                                                                                                $gt$O(
                                                                                                final x10.array.Point that){
            
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
int i40460 =
              0;
            
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
for (;
                                                                                                       true;
                                                                                                       ) {
                
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40461 =
                  i40460;
                
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40462 =
                  rank;
                
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40463 =
                  ((t40462) - (((int)(1))));
                
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40464 =
                  ((t40461) < (((int)(t40463))));
                
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (!(t40464)) {
                    
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
break;
                }
                
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40452 =
                  i40460;
                
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int a40453 =
                  this.$apply$O((int)(t40452));
                
//#line 257 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40454 =
                  i40460;
                
//#line 257 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int b40455 =
                  that.$apply$O((int)(t40454));
                
//#line 258 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40456 =
                  ((a40453) < (((int)(b40455))));
                
//#line 258 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40456) {
                    
//#line 258 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return false;
                }
                
//#line 259 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40457 =
                  ((a40453) > (((int)(b40455))));
                
//#line 259 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40457) {
                    
//#line 259 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return true;
                }
                
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40458 =
                  i40460;
                
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40459 =
                  ((t40458) + (((int)(1))));
                
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
i40460 = t40459;
            }
            
//#line 261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40353 =
              rank;
            
//#line 261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40354 =
              ((t40353) - (((int)(1))));
            
//#line 261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40357 =
              this.$apply$O((int)(t40354));
            
//#line 261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40355 =
              rank;
            
//#line 261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40356 =
              ((t40355) - (((int)(1))));
            
//#line 261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40358 =
              that.$apply$O((int)(t40356));
            
//#line 261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40359 =
              ((t40357) > (((int)(t40358))));
            
//#line 261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40359;
        }
        
        
//#line 266 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public boolean
                                                                                                $le$O(
                                                                                                final x10.array.Point that){
            
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
int i40473 =
              0;
            
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
for (;
                                                                                                       true;
                                                                                                       ) {
                
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40474 =
                  i40473;
                
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40475 =
                  rank;
                
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40476 =
                  ((t40475) - (((int)(1))));
                
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40477 =
                  ((t40474) < (((int)(t40476))));
                
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (!(t40477)) {
                    
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
break;
                }
                
//#line 268 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40465 =
                  i40473;
                
//#line 268 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int a40466 =
                  this.$apply$O((int)(t40465));
                
//#line 269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40467 =
                  i40473;
                
//#line 269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int b40468 =
                  that.$apply$O((int)(t40467));
                
//#line 270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40469 =
                  ((a40466) > (((int)(b40468))));
                
//#line 270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40469) {
                    
//#line 270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return false;
                }
                
//#line 271 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40470 =
                  ((a40466) < (((int)(b40468))));
                
//#line 271 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40470) {
                    
//#line 271 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return true;
                }
                
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40471 =
                  i40473;
                
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40472 =
                  ((t40471) + (((int)(1))));
                
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
i40473 = t40472;
            }
            
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40371 =
              rank;
            
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40372 =
              ((t40371) - (((int)(1))));
            
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40375 =
              this.$apply$O((int)(t40372));
            
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40373 =
              rank;
            
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40374 =
              ((t40373) - (((int)(1))));
            
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40376 =
              that.$apply$O((int)(t40374));
            
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40377 =
              ((t40375) <= (((int)(t40376))));
            
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40377;
        }
        
        
//#line 278 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public boolean
                                                                                                $ge$O(
                                                                                                final x10.array.Point that){
            
//#line 279 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
int i40486 =
              0;
            
//#line 279 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
for (;
                                                                                                       true;
                                                                                                       ) {
                
//#line 279 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40487 =
                  i40486;
                
//#line 279 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40488 =
                  rank;
                
//#line 279 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40489 =
                  ((t40488) - (((int)(1))));
                
//#line 279 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40490 =
                  ((t40487) < (((int)(t40489))));
                
//#line 279 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (!(t40490)) {
                    
//#line 279 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
break;
                }
                
//#line 280 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40478 =
                  i40486;
                
//#line 280 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int a40479 =
                  this.$apply$O((int)(t40478));
                
//#line 281 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40480 =
                  i40486;
                
//#line 281 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int b40481 =
                  that.$apply$O((int)(t40480));
                
//#line 282 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40482 =
                  ((a40479) < (((int)(b40481))));
                
//#line 282 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40482) {
                    
//#line 282 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return false;
                }
                
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40483 =
                  ((a40479) > (((int)(b40481))));
                
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40483) {
                    
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return true;
                }
                
//#line 279 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40484 =
                  i40486;
                
//#line 279 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40485 =
                  ((t40484) + (((int)(1))));
                
//#line 279 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
i40486 = t40485;
            }
            
//#line 285 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40389 =
              rank;
            
//#line 285 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40390 =
              ((t40389) - (((int)(1))));
            
//#line 285 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40393 =
              this.$apply$O((int)(t40390));
            
//#line 285 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40391 =
              rank;
            
//#line 285 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40392 =
              ((t40391) - (((int)(1))));
            
//#line 285 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40394 =
              that.$apply$O((int)(t40392));
            
//#line 285 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40395 =
              ((t40393) >= (((int)(t40394))));
            
//#line 285 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40395;
        }
        
        
//#line 290 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
public java.lang.String
                                                                                                toString(
                                                                                                ){
            
//#line 291 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
java.lang.String s =
              "[";
            
//#line 292 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40396 =
              rank;
            
//#line 292 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40400 =
              ((t40396) > (((int)(0))));
            
//#line 292 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (t40400) {
                
//#line 292 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final java.lang.String t40397 =
                  s;
                
//#line 292 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40398 =
                  this.$apply$O((int)(0));
                
//#line 292 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final java.lang.String t40399 =
                  ((t40397) + ((x10.core.Int.$box(t40398))));
                
//#line 292 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
s = t40399;
            }
            
//#line 293 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
int i40498 =
              1;
            
//#line 293 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
for (;
                                                                                                       true;
                                                                                                       ) {
                
//#line 293 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40499 =
                  i40498;
                
//#line 293 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40500 =
                  rank;
                
//#line 293 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final boolean t40501 =
                  ((t40499) < (((int)(t40500))));
                
//#line 293 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
if (!(t40501)) {
                    
//#line 293 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
break;
                }
                
//#line 294 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final java.lang.String t40491 =
                  s;
                
//#line 294 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40492 =
                  i40498;
                
//#line 294 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40493 =
                  this.$apply$O((int)(t40492));
                
//#line 294 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final java.lang.String t40494 =
                  ((",") + ((x10.core.Int.$box(t40493))));
                
//#line 294 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final java.lang.String t40495 =
                  ((t40491) + (t40494));
                
//#line 294 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
s = t40495;
                
//#line 293 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40496 =
                  i40498;
                
//#line 293 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40497 =
                  ((t40496) + (((int)(1))));
                
//#line 293 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
i40498 = t40497;
            }
            
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final java.lang.String t40412 =
              s;
            
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final java.lang.String t40413 =
              ((t40412) + ("]"));
            
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
s = t40413;
            
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final java.lang.String t40414 =
              s;
            
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40414;
        }
        
        
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final public x10.array.Point
                                                                                               x10$array$Point$$x10$array$Point$this(
                                                                                               ){
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return x10.array.Point.this;
        }
        
        @x10.core.X10Generated public static class $Closure$32 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$32.class);
            
            public static final x10.rtt.RuntimeType<$Closure$32> $RTT = x10.rtt.StaticFunType.<$Closure$32> make(
            /* base class */$Closure$32.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$32 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$32.class + " calling"); } 
                x10.array.Point out$$ = (x10.array.Point) $deserializer.readRef();
                $_obj.out$$ = out$$;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$32 $_obj = new $Closure$32((java.lang.System[]) null);
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
            public $Closure$32(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40126 =
                      this.
                        out$$.$apply$O((int)(i));
                    
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40126;
                }
                
                public x10.array.Point out$$;
                
                public $Closure$32(final x10.array.Point out$$) { {
                                                                         this.out$$ = out$$;
                                                                     }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$33<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$33.class);
            
            public static final x10.rtt.RuntimeType<$Closure$33> $RTT = x10.rtt.StaticFunType.<$Closure$33> make(
            /* base class */$Closure$33.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$33 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$33.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                x10.array.Array r = (x10.array.Array) $deserializer.readRef();
                $_obj.r = r;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$33 $_obj = new $Closure$33((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                if (r instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.r);
                } else {
                $serializer.write(this.r);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$33(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.array.Point.$Closure$33.$initParams(this, $T);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return $apply$G(x10.core.Int.$unbox(a1));
            }
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$33 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public $T
                  $apply$G(
                  final int i){
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final $T t40170 =
                      (($T)(((x10.array.Array<$T>)this.
                                                    r).$apply$G((int)(i))));
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40170;
                }
                
                public x10.array.Array<$T> r;
                
                public $Closure$33(final x10.rtt.Type $T,
                                   final x10.array.Array<$T> r, __0$1x10$array$Point$$Closure$33$$T$2 $dummy) {x10.array.Point.$Closure$33.$initParams(this, $T);
                                                                                                                    {
                                                                                                                       this.r = ((x10.array.Array)(r));
                                                                                                                   }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$array$Point$$Closure$33$$T$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$34 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$34.class);
            
            public static final x10.rtt.RuntimeType<$Closure$34> $RTT = x10.rtt.StaticFunType.<$Closure$34> make(
            /* base class */$Closure$34.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$34 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$34.class + " calling"); } 
                x10.array.Point out$$ = (x10.array.Point) $deserializer.readRef();
                $_obj.out$$ = out$$;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$34 $_obj = new $Closure$34((java.lang.System[]) null);
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
            public $Closure$34(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40221 =
                      this.
                        out$$.$apply$O((int)(i));
                    
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40222 =
                      (-(t40221));
                    
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40222;
                }
                
                public x10.array.Point out$$;
                
                public $Closure$34(final x10.array.Point out$$) { {
                                                                         this.out$$ = out$$;
                                                                     }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$35 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$35.class);
            
            public static final x10.rtt.RuntimeType<$Closure$35> $RTT = x10.rtt.StaticFunType.<$Closure$35> make(
            /* base class */$Closure$35.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$35 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$35.class + " calling"); } 
                x10.array.Point out$$ = (x10.array.Point) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.array.Point that = (x10.array.Point) $deserializer.readRef();
                $_obj.that = that;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$35 $_obj = new $Closure$35((java.lang.System[]) null);
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
                if (that instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.that);
                } else {
                $serializer.write(this.that);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$35(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40226 =
                      this.
                        out$$.$apply$O((int)(i));
                    
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40227 =
                      this.
                        that.$apply$O((int)(i));
                    
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40228 =
                      ((t40226) + (((int)(t40227))));
                    
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40228;
                }
                
                public x10.array.Point out$$;
                public x10.array.Point that;
                
                public $Closure$35(final x10.array.Point out$$,
                                   final x10.array.Point that) { {
                                                                        this.out$$ = out$$;
                                                                        this.that = ((x10.array.Point)(that));
                                                                    }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$36 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$36.class);
            
            public static final x10.rtt.RuntimeType<$Closure$36> $RTT = x10.rtt.StaticFunType.<$Closure$36> make(
            /* base class */$Closure$36.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$36 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$36.class + " calling"); } 
                x10.array.Point out$$ = (x10.array.Point) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.array.Point that = (x10.array.Point) $deserializer.readRef();
                $_obj.that = that;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$36 $_obj = new $Closure$36((java.lang.System[]) null);
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
                if (that instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.that);
                } else {
                $serializer.write(this.that);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$36(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40232 =
                      this.
                        out$$.$apply$O((int)(i));
                    
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40233 =
                      this.
                        that.$apply$O((int)(i));
                    
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40234 =
                      ((t40232) - (((int)(t40233))));
                    
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40234;
                }
                
                public x10.array.Point out$$;
                public x10.array.Point that;
                
                public $Closure$36(final x10.array.Point out$$,
                                   final x10.array.Point that) { {
                                                                        this.out$$ = out$$;
                                                                        this.that = ((x10.array.Point)(that));
                                                                    }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$37 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$37.class);
            
            public static final x10.rtt.RuntimeType<$Closure$37> $RTT = x10.rtt.StaticFunType.<$Closure$37> make(
            /* base class */$Closure$37.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$37 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$37.class + " calling"); } 
                x10.array.Point out$$ = (x10.array.Point) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.array.Point that = (x10.array.Point) $deserializer.readRef();
                $_obj.that = that;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$37 $_obj = new $Closure$37((java.lang.System[]) null);
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
                if (that instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.that);
                } else {
                $serializer.write(this.that);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$37(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40238 =
                      this.
                        out$$.$apply$O((int)(i));
                    
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40239 =
                      this.
                        that.$apply$O((int)(i));
                    
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40240 =
                      ((t40238) * (((int)(t40239))));
                    
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40240;
                }
                
                public x10.array.Point out$$;
                public x10.array.Point that;
                
                public $Closure$37(final x10.array.Point out$$,
                                   final x10.array.Point that) { {
                                                                        this.out$$ = out$$;
                                                                        this.that = ((x10.array.Point)(that));
                                                                    }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$38 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$38.class);
            
            public static final x10.rtt.RuntimeType<$Closure$38> $RTT = x10.rtt.StaticFunType.<$Closure$38> make(
            /* base class */$Closure$38.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$38 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$38.class + " calling"); } 
                x10.array.Point out$$ = (x10.array.Point) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.array.Point that = (x10.array.Point) $deserializer.readRef();
                $_obj.that = that;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$38 $_obj = new $Closure$38((java.lang.System[]) null);
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
                if (that instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.that);
                } else {
                $serializer.write(this.that);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$38(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40244 =
                      this.
                        out$$.$apply$O((int)(i));
                    
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40245 =
                      this.
                        that.$apply$O((int)(i));
                    
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40246 =
                      ((t40244) / (((int)(t40245))));
                    
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40246;
                }
                
                public x10.array.Point out$$;
                public x10.array.Point that;
                
                public $Closure$38(final x10.array.Point out$$,
                                   final x10.array.Point that) { {
                                                                        this.out$$ = out$$;
                                                                        this.that = ((x10.array.Point)(that));
                                                                    }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$39 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$39.class);
            
            public static final x10.rtt.RuntimeType<$Closure$39> $RTT = x10.rtt.StaticFunType.<$Closure$39> make(
            /* base class */$Closure$39.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$39 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$39.class + " calling"); } 
                x10.array.Point out$$ = (x10.array.Point) $deserializer.readRef();
                $_obj.out$$ = out$$;
                $_obj.c = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$39 $_obj = new $Closure$39((java.lang.System[]) null);
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
                $serializer.write(this.c);
                
            }
            
            // constructor just for allocation
            public $Closure$39(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40250 =
                      this.
                        out$$.$apply$O((int)(i));
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40251 =
                      ((t40250) + (((int)(this.
                                            c))));
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40251;
                }
                
                public x10.array.Point out$$;
                public int c;
                
                public $Closure$39(final x10.array.Point out$$,
                                   final int c) { {
                                                         this.out$$ = out$$;
                                                         this.c = c;
                                                     }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$40 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$40.class);
            
            public static final x10.rtt.RuntimeType<$Closure$40> $RTT = x10.rtt.StaticFunType.<$Closure$40> make(
            /* base class */$Closure$40.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$40 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$40.class + " calling"); } 
                x10.array.Point out$$ = (x10.array.Point) $deserializer.readRef();
                $_obj.out$$ = out$$;
                $_obj.c = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$40 $_obj = new $Closure$40((java.lang.System[]) null);
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
                $serializer.write(this.c);
                
            }
            
            // constructor just for allocation
            public $Closure$40(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40255 =
                      this.
                        out$$.$apply$O((int)(i));
                    
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40256 =
                      ((t40255) - (((int)(this.
                                            c))));
                    
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40256;
                }
                
                public x10.array.Point out$$;
                public int c;
                
                public $Closure$40(final x10.array.Point out$$,
                                   final int c) { {
                                                         this.out$$ = out$$;
                                                         this.c = c;
                                                     }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$41 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$41.class);
            
            public static final x10.rtt.RuntimeType<$Closure$41> $RTT = x10.rtt.StaticFunType.<$Closure$41> make(
            /* base class */$Closure$41.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$41 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$41.class + " calling"); } 
                x10.array.Point out$$ = (x10.array.Point) $deserializer.readRef();
                $_obj.out$$ = out$$;
                $_obj.c = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$41 $_obj = new $Closure$41((java.lang.System[]) null);
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
                $serializer.write(this.c);
                
            }
            
            // constructor just for allocation
            public $Closure$41(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40260 =
                      this.
                        out$$.$apply$O((int)(i));
                    
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40261 =
                      ((t40260) * (((int)(this.
                                            c))));
                    
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40261;
                }
                
                public x10.array.Point out$$;
                public int c;
                
                public $Closure$41(final x10.array.Point out$$,
                                   final int c) { {
                                                         this.out$$ = out$$;
                                                         this.c = c;
                                                     }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$42 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$42.class);
            
            public static final x10.rtt.RuntimeType<$Closure$42> $RTT = x10.rtt.StaticFunType.<$Closure$42> make(
            /* base class */$Closure$42.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$42 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$42.class + " calling"); } 
                x10.array.Point out$$ = (x10.array.Point) $deserializer.readRef();
                $_obj.out$$ = out$$;
                $_obj.c = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$42 $_obj = new $Closure$42((java.lang.System[]) null);
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
                $serializer.write(this.c);
                
            }
            
            // constructor just for allocation
            public $Closure$42(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40265 =
                      this.
                        out$$.$apply$O((int)(i));
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40266 =
                      ((t40265) / (((int)(this.
                                            c))));
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40266;
                }
                
                public x10.array.Point out$$;
                public int c;
                
                public $Closure$42(final x10.array.Point out$$,
                                   final int c) { {
                                                         this.out$$ = out$$;
                                                         this.c = c;
                                                     }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$43 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$43.class);
            
            public static final x10.rtt.RuntimeType<$Closure$43> $RTT = x10.rtt.StaticFunType.<$Closure$43> make(
            /* base class */$Closure$43.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$43 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$43.class + " calling"); } 
                x10.array.Point out$$ = (x10.array.Point) $deserializer.readRef();
                $_obj.out$$ = out$$;
                $_obj.c = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$43 $_obj = new $Closure$43((java.lang.System[]) null);
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
                $serializer.write(this.c);
                
            }
            
            // constructor just for allocation
            public $Closure$43(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40270 =
                      this.
                        out$$.$apply$O((int)(i));
                    
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40271 =
                      ((this.
                          c) + (((int)(t40270))));
                    
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40271;
                }
                
                public x10.array.Point out$$;
                public int c;
                
                public $Closure$43(final x10.array.Point out$$,
                                   final int c) { {
                                                         this.out$$ = out$$;
                                                         this.c = c;
                                                     }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$44 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$44.class);
            
            public static final x10.rtt.RuntimeType<$Closure$44> $RTT = x10.rtt.StaticFunType.<$Closure$44> make(
            /* base class */$Closure$44.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$44 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$44.class + " calling"); } 
                x10.array.Point out$$ = (x10.array.Point) $deserializer.readRef();
                $_obj.out$$ = out$$;
                $_obj.c = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$44 $_obj = new $Closure$44((java.lang.System[]) null);
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
                $serializer.write(this.c);
                
            }
            
            // constructor just for allocation
            public $Closure$44(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40275 =
                      this.
                        out$$.$apply$O((int)(i));
                    
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40276 =
                      ((this.
                          c) - (((int)(t40275))));
                    
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40276;
                }
                
                public x10.array.Point out$$;
                public int c;
                
                public $Closure$44(final x10.array.Point out$$,
                                   final int c) { {
                                                         this.out$$ = out$$;
                                                         this.c = c;
                                                     }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$45 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$45.class);
            
            public static final x10.rtt.RuntimeType<$Closure$45> $RTT = x10.rtt.StaticFunType.<$Closure$45> make(
            /* base class */$Closure$45.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$45 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$45.class + " calling"); } 
                x10.array.Point out$$ = (x10.array.Point) $deserializer.readRef();
                $_obj.out$$ = out$$;
                $_obj.c = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$45 $_obj = new $Closure$45((java.lang.System[]) null);
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
                $serializer.write(this.c);
                
            }
            
            // constructor just for allocation
            public $Closure$45(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40280 =
                      this.
                        out$$.$apply$O((int)(i));
                    
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40281 =
                      ((this.
                          c) * (((int)(t40280))));
                    
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40281;
                }
                
                public x10.array.Point out$$;
                public int c;
                
                public $Closure$45(final x10.array.Point out$$,
                                   final int c) { {
                                                         this.out$$ = out$$;
                                                         this.c = c;
                                                     }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$46 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$46.class);
            
            public static final x10.rtt.RuntimeType<$Closure$46> $RTT = x10.rtt.StaticFunType.<$Closure$46> make(
            /* base class */$Closure$46.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$46 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$46.class + " calling"); } 
                x10.array.Point out$$ = (x10.array.Point) $deserializer.readRef();
                $_obj.out$$ = out$$;
                $_obj.c = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$46 $_obj = new $Closure$46((java.lang.System[]) null);
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
                $serializer.write(this.c);
                
            }
            
            // constructor just for allocation
            public $Closure$46(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40285 =
                      this.
                        out$$.$apply$O((int)(i));
                    
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
final int t40286 =
                      ((this.
                          c) / (((int)(t40285))));
                    
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Point.x10"
return t40286;
                }
                
                public x10.array.Point out$$;
                public int c;
                
                public $Closure$46(final x10.array.Point out$$,
                                   final int c) { {
                                                         this.out$$ = out$$;
                                                         this.c = c;
                                                     }}
                
            }
            
        // synthetic type for parameter mangling
        public abstract static class __0$1x10$lang$Int$2 {}
        
        }
        
        