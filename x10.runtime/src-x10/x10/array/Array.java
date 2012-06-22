package x10.array;


@x10.core.X10Generated final public class Array<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.lang.Iterable, x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Array.class);
    
    public static final x10.rtt.RuntimeType<Array> $RTT = x10.rtt.NamedType.<Array> make(
    "x10.array.Array", /* base class */Array.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.array.Point.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.ParameterizedType.make(x10.lang.Iterable.$RTT, x10.array.Point.$RTT), x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Array $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Array.class + " calling"); } 
        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
        x10.core.IndexedMemoryChunk raw = (x10.core.IndexedMemoryChunk) $deserializer.readRef();
        $_obj.raw = raw;
        $_obj.layout_min0 = $deserializer.readInt();
        $_obj.layout_stride1 = $deserializer.readInt();
        $_obj.layout_min1 = $deserializer.readInt();
        x10.array.Array layout = (x10.array.Array) $deserializer.readRef();
        $_obj.layout = layout;
        x10.array.Region region = (x10.array.Region) $deserializer.readRef();
        $_obj.region = region;
        $_obj.rank = $deserializer.readInt();
        $_obj.rect = $deserializer.readBoolean();
        $_obj.zeroBased = $deserializer.readBoolean();
        $_obj.rail = $deserializer.readBoolean();
        $_obj.size = $deserializer.readInt();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Array $_obj = new Array((java.lang.System[]) null, (x10.rtt.Type) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
        if (raw instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.raw);
        } else {
        $serializer.write(this.raw);
        }
        $serializer.write(this.layout_min0);
        $serializer.write(this.layout_stride1);
        $serializer.write(this.layout_min1);
        if (layout instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.layout);
        } else {
        $serializer.write(this.layout);
        }
        if (region instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.region);
        } else {
        $serializer.write(this.region);
        }
        $serializer.write(this.rank);
        $serializer.write(this.rect);
        $serializer.write(this.zeroBased);
        $serializer.write(this.rail);
        $serializer.write(this.size);
        
    }
    
    // constructor just for allocation
    public Array(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
    super($dummy);
    x10.array.Array.$initParams(this, $T);
    }
    // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1){}:U
    public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
    return $apply$G((x10.array.Point)a1);
    }
    
        private x10.rtt.Type $T;
        // initializer of type parameters
        public static void $initParams(final Array $this, final x10.rtt.Type $T) {
        $this.$T = $T;
        }
        
        
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public x10.array.Region region;
        
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public int rank;
        
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public boolean rect;
        
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public boolean zeroBased;
        
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public boolean rail;
        
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public int size;
        
        
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
/**
     * The backing storage for the array's elements
     */
        public x10.core.IndexedMemoryChunk<$T> raw;
        
        
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public x10.core.IndexedMemoryChunk<$T>
                                                                                                raw(
                                                                                                ){
            
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33122 =
              ((x10.core.IndexedMemoryChunk)(raw));
            
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33122;
        }
        
        
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
// creation method for java code (1-phase java constructor)
        public Array(final x10.rtt.Type $T,
                     final x10.array.Region reg){this((java.lang.System[]) null, $T);
                                                     $init(reg);}
        
        // constructor for non-virtual call
        final public x10.array.Array<$T> x10$array$Array$$init$S(final x10.array.Region reg) { {
                                                                                                      
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"

                                                                                                      
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region __desugarer__var__0__33846 =
                                                                                                        ((x10.array.Region)(((x10.array.Region)
                                                                                                                              reg)));
                                                                                                      
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
x10.array.Region ret33847 =
                                                                                                         null;
                                                                                                      
//#line 125 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33842 =
                                                                                                        ((__desugarer__var__0__33846) != (null));
                                                                                                      
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33843 =
                                                                                                        !(t33842);
                                                                                                      
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33843) {
                                                                                                          
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33844 =
                                                                                                            true;
                                                                                                          
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33844) {
                                                                                                              
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t33845 =
                                                                                                                new x10.lang.FailedDynamicCheckException("x10.array.Region{self!=null}");
                                                                                                              
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
throw t33845;
                                                                                                          }
                                                                                                      }
                                                                                                      
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
ret33847 = ((x10.array.Region)(__desugarer__var__0__33846));
                                                                                                      
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33848 =
                                                                                                        ((x10.array.Region)(ret33847));
                                                                                                      
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33849 =
                                                                                                        reg.
                                                                                                          rank;
                                                                                                      
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33850 =
                                                                                                        reg.
                                                                                                          rect;
                                                                                                      
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33851 =
                                                                                                        reg.
                                                                                                          zeroBased;
                                                                                                      
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33852 =
                                                                                                        reg.
                                                                                                          rail;
                                                                                                      
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33853 =
                                                                                                        reg.size$O();
                                                                                                      
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.region = ((x10.array.Region)(t33848));
                                                                                                      this.rank = t33849;
                                                                                                      this.rect = t33850;
                                                                                                      this.zeroBased = t33851;
                                                                                                      this.rail = t33852;
                                                                                                      this.size = t33853;
                                                                                                      
                                                                                                      
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array.LayoutHelper crh =
                                                                                                        new x10.array.Array.LayoutHelper((java.lang.System[]) null).$init(((x10.array.Region)(reg)));
                                                                                                      
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33133 =
                                                                                                        crh.
                                                                                                          min0;
                                                                                                      
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout_min0 = t33133;
                                                                                                      
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33134 =
                                                                                                        crh.
                                                                                                          stride1;
                                                                                                      
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout_stride1 = t33134;
                                                                                                      
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33135 =
                                                                                                        crh.
                                                                                                          min1;
                                                                                                      
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout_min1 = t33135;
                                                                                                      
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t33136 =
                                                                                                        ((x10.array.Array)(crh.
                                                                                                                             layout));
                                                                                                      
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout = ((x10.array.Array)(t33136));
                                                                                                      
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int n =
                                                                                                        crh.
                                                                                                          size;
                                                                                                      
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33137 =
                                                                                                        ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<$T>allocate($T, ((int)(n)), true)));
                                                                                                      
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.raw = ((x10.core.IndexedMemoryChunk)(t33137));
                                                                                                  }
                                                                                                  return this;
                                                                                                  }
        
        // constructor
        public x10.array.Array<$T> $init(final x10.array.Region reg){return x10$array$Array$$init$S(reg);}
        
        
        
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
// creation method for java code (1-phase java constructor)
        public Array(final x10.rtt.Type $T,
                     final x10.array.Region reg,
                     final x10.core.fun.Fun_0_1<x10.array.Point,$T> init, __1$1x10$array$Point$3x10$array$Array$$T$2 $dummy){this((java.lang.System[]) null, $T);
                                                                                                                                 $init(reg,init, (x10.array.Array.__1$1x10$array$Point$3x10$array$Array$$T$2) null);}
        
        // constructor for non-virtual call
        final public x10.array.Array<$T> x10$array$Array$$init$S(final x10.array.Region reg,
                                                                 final x10.core.fun.Fun_0_1<x10.array.Point,$T> init, __1$1x10$array$Point$3x10$array$Array$$T$2 $dummy) { {
                                                                                                                                                                                  
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"

                                                                                                                                                                                  
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region __desugarer__var__1__33858 =
                                                                                                                                                                                    ((x10.array.Region)(((x10.array.Region)
                                                                                                                                                                                                          reg)));
                                                                                                                                                                                  
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
x10.array.Region ret33859 =
                                                                                                                                                                                     null;
                                                                                                                                                                                  
//#line 153 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33854 =
                                                                                                                                                                                    ((__desugarer__var__1__33858) != (null));
                                                                                                                                                                                  
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33855 =
                                                                                                                                                                                    !(t33854);
                                                                                                                                                                                  
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33855) {
                                                                                                                                                                                      
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33856 =
                                                                                                                                                                                        true;
                                                                                                                                                                                      
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33856) {
                                                                                                                                                                                          
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t33857 =
                                                                                                                                                                                            new x10.lang.FailedDynamicCheckException("x10.array.Region{self!=null}");
                                                                                                                                                                                          
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
throw t33857;
                                                                                                                                                                                      }
                                                                                                                                                                                  }
                                                                                                                                                                                  
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
ret33859 = ((x10.array.Region)(__desugarer__var__1__33858));
                                                                                                                                                                                  
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33860 =
                                                                                                                                                                                    ((x10.array.Region)(ret33859));
                                                                                                                                                                                  
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33861 =
                                                                                                                                                                                    reg.
                                                                                                                                                                                      rank;
                                                                                                                                                                                  
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33862 =
                                                                                                                                                                                    reg.
                                                                                                                                                                                      rect;
                                                                                                                                                                                  
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33863 =
                                                                                                                                                                                    reg.
                                                                                                                                                                                      zeroBased;
                                                                                                                                                                                  
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33864 =
                                                                                                                                                                                    reg.
                                                                                                                                                                                      rail;
                                                                                                                                                                                  
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33865 =
                                                                                                                                                                                    reg.size$O();
                                                                                                                                                                                  
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.region = ((x10.array.Region)(t33860));
                                                                                                                                                                                  this.rank = t33861;
                                                                                                                                                                                  this.rect = t33862;
                                                                                                                                                                                  this.zeroBased = t33863;
                                                                                                                                                                                  this.rail = t33864;
                                                                                                                                                                                  this.size = t33865;
                                                                                                                                                                                  
                                                                                                                                                                                  
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array.LayoutHelper crh =
                                                                                                                                                                                    new x10.array.Array.LayoutHelper((java.lang.System[]) null).$init(((x10.array.Region)(reg)));
                                                                                                                                                                                  
//#line 155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33148 =
                                                                                                                                                                                    crh.
                                                                                                                                                                                      min0;
                                                                                                                                                                                  
//#line 155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout_min0 = t33148;
                                                                                                                                                                                  
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33149 =
                                                                                                                                                                                    crh.
                                                                                                                                                                                      stride1;
                                                                                                                                                                                  
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout_stride1 = t33149;
                                                                                                                                                                                  
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33150 =
                                                                                                                                                                                    crh.
                                                                                                                                                                                      min1;
                                                                                                                                                                                  
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout_min1 = t33150;
                                                                                                                                                                                  
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t33151 =
                                                                                                                                                                                    ((x10.array.Array)(crh.
                                                                                                                                                                                                         layout));
                                                                                                                                                                                  
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout = ((x10.array.Array)(t33151));
                                                                                                                                                                                  
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int n =
                                                                                                                                                                                    crh.
                                                                                                                                                                                      size;
                                                                                                                                                                                  
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> r =
                                                                                                                                                                                    x10.core.IndexedMemoryChunk.<$T>allocate($T, ((int)(n)), false);
                                                                                                                                                                                  
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.Iterator<x10.array.Point> p33869 =
                                                                                                                                                                                    reg.iterator();
                                                                                                                                                                                  
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
for (;
                                                                                                                                                                                                                                                                             true;
                                                                                                                                                                                                                                                                             ) {
                                                                                                                                                                                      
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33870 =
                                                                                                                                                                                        ((x10.lang.Iterator<x10.array.Point>)p33869).hasNext$O();
                                                                                                                                                                                      
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (!(t33870)) {
                                                                                                                                                                                          
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
break;
                                                                                                                                                                                      }
                                                                                                                                                                                      
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Point p33866 =
                                                                                                                                                                                        ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)p33869).next$G()));
                                                                                                                                                                                      
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33867 =
                                                                                                                                                                                        this.offset$O(((x10.array.Point)(p33866)));
                                                                                                                                                                                      
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $T t33868 =
                                                                                                                                                                                        (($T)((($T)
                                                                                                                                                                                                ((x10.core.fun.Fun_0_1<x10.array.Point,$T>)init).$apply(p33866,x10.array.Point.$RTT))));
                                                                                                                                                                                      
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
(((x10.core.IndexedMemoryChunk<$T>)(r))).$set(((int)(t33867)), t33868);
                                                                                                                                                                                  }
                                                                                                                                                                                  
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.raw = ((x10.core.IndexedMemoryChunk)(r));
                                                                                                                                                                              }
                                                                                                                                                                              return this;
                                                                                                                                                                              }
        
        // constructor
        public x10.array.Array<$T> $init(final x10.array.Region reg,
                                         final x10.core.fun.Fun_0_1<x10.array.Point,$T> init, __1$1x10$array$Point$3x10$array$Array$$T$2 $dummy){return x10$array$Array$$init$S(reg,init, $dummy);}
        
        
        
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
// creation method for java code (1-phase java constructor)
        public Array(final x10.rtt.Type $T,
                     final x10.array.Region reg,
                     final $T init, __1x10$array$Array$$T $dummy){this((java.lang.System[]) null, $T);
                                                                      $init(reg,init, (x10.array.Array.__1x10$array$Array$$T) null);}
        
        // constructor for non-virtual call
        final public x10.array.Array<$T> x10$array$Array$$init$S(final x10.array.Region reg,
                                                                 final $T init, __1x10$array$Array$$T $dummy) { {
                                                                                                                       
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"

                                                                                                                       
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region __desugarer__var__2__33875 =
                                                                                                                         ((x10.array.Region)(((x10.array.Region)
                                                                                                                                               reg)));
                                                                                                                       
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
x10.array.Region ret33876 =
                                                                                                                          null;
                                                                                                                       
//#line 176 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33871 =
                                                                                                                         ((__desugarer__var__2__33875) != (null));
                                                                                                                       
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33872 =
                                                                                                                         !(t33871);
                                                                                                                       
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33872) {
                                                                                                                           
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33873 =
                                                                                                                             true;
                                                                                                                           
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33873) {
                                                                                                                               
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t33874 =
                                                                                                                                 new x10.lang.FailedDynamicCheckException("x10.array.Region{self!=null}");
                                                                                                                               
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
throw t33874;
                                                                                                                           }
                                                                                                                       }
                                                                                                                       
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
ret33876 = ((x10.array.Region)(__desugarer__var__2__33875));
                                                                                                                       
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33877 =
                                                                                                                         ((x10.array.Region)(ret33876));
                                                                                                                       
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33878 =
                                                                                                                         reg.
                                                                                                                           rank;
                                                                                                                       
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33879 =
                                                                                                                         reg.
                                                                                                                           rect;
                                                                                                                       
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33880 =
                                                                                                                         reg.
                                                                                                                           zeroBased;
                                                                                                                       
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33881 =
                                                                                                                         reg.
                                                                                                                           rail;
                                                                                                                       
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33882 =
                                                                                                                         reg.size$O();
                                                                                                                       
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.region = ((x10.array.Region)(t33877));
                                                                                                                       this.rank = t33878;
                                                                                                                       this.rect = t33879;
                                                                                                                       this.zeroBased = t33880;
                                                                                                                       this.rail = t33881;
                                                                                                                       this.size = t33882;
                                                                                                                       
                                                                                                                       
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array.LayoutHelper crh =
                                                                                                                         new x10.array.Array.LayoutHelper((java.lang.System[]) null).$init(((x10.array.Region)(reg)));
                                                                                                                       
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33166 =
                                                                                                                         crh.
                                                                                                                           min0;
                                                                                                                       
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout_min0 = t33166;
                                                                                                                       
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33167 =
                                                                                                                         crh.
                                                                                                                           stride1;
                                                                                                                       
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout_stride1 = t33167;
                                                                                                                       
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33168 =
                                                                                                                         crh.
                                                                                                                           min1;
                                                                                                                       
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout_min1 = t33168;
                                                                                                                       
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t33169 =
                                                                                                                         ((x10.array.Array)(crh.
                                                                                                                                              layout));
                                                                                                                       
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout = ((x10.array.Array)(t33169));
                                                                                                                       
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int n =
                                                                                                                         crh.
                                                                                                                           size;
                                                                                                                       
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> r =
                                                                                                                         x10.core.IndexedMemoryChunk.<$T>allocate($T, ((int)(n)), false);
                                                                                                                       
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33179 =
                                                                                                                         reg.
                                                                                                                           rect;
                                                                                                                       
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33179) {
                                                                                                                           
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
int i =
                                                                                                                             0;
                                                                                                                           
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
for (;
                                                                                                                                                                                                                      true;
                                                                                                                                                                                                                      ) {
                                                                                                                               
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33171 =
                                                                                                                                 i;
                                                                                                                               
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33175 =
                                                                                                                                 ((t33171) < (((int)(n))));
                                                                                                                               
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (!(t33175)) {
                                                                                                                                   
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
break;
                                                                                                                               }
                                                                                                                               
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33883 =
                                                                                                                                 i;
                                                                                                                               
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
(((x10.core.IndexedMemoryChunk<$T>)(r))).$set(((int)(t33883)), init);
                                                                                                                               
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33884 =
                                                                                                                                 i;
                                                                                                                               
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33885 =
                                                                                                                                 ((t33884) + (((int)(1))));
                                                                                                                               
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
i = t33885;
                                                                                                                           }
                                                                                                                       } else {
                                                                                                                           
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.Iterator<x10.array.Point> p32953 =
                                                                                                                             reg.iterator();
                                                                                                                           
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
for (;
                                                                                                                                                                                                                      true;
                                                                                                                                                                                                                      ) {
                                                                                                                               
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33178 =
                                                                                                                                 ((x10.lang.Iterator<x10.array.Point>)p32953).hasNext$O();
                                                                                                                               
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (!(t33178)) {
                                                                                                                                   
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
break;
                                                                                                                               }
                                                                                                                               
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Point p33886 =
                                                                                                                                 ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)p32953).next$G()));
                                                                                                                               
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33887 =
                                                                                                                                 this.offset$O(((x10.array.Point)(p33886)));
                                                                                                                               
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
(((x10.core.IndexedMemoryChunk<$T>)(r))).$set(((int)(t33887)), init);
                                                                                                                           }
                                                                                                                       }
                                                                                                                       
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.raw = ((x10.core.IndexedMemoryChunk)(r));
                                                                                                                   }
                                                                                                                   return this;
                                                                                                                   }
        
        // constructor
        public x10.array.Array<$T> $init(final x10.array.Region reg,
                                         final $T init, __1x10$array$Array$$T $dummy){return x10$array$Array$$init$S(reg,init, $dummy);}
        
        
        
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
// creation method for java code (1-phase java constructor)
        public Array(final x10.rtt.Type $T,
                     final x10.array.Region reg,
                     final x10.core.IndexedMemoryChunk<$T> backingStore, __1$1x10$array$Array$$T$2 $dummy){this((java.lang.System[]) null, $T);
                                                                                                               $init(reg,backingStore, (x10.array.Array.__1$1x10$array$Array$$T$2) null);}
        
        // constructor for non-virtual call
        final public x10.array.Array<$T> x10$array$Array$$init$S(final x10.array.Region reg,
                                                                 final x10.core.IndexedMemoryChunk<$T> backingStore, __1$1x10$array$Array$$T$2 $dummy) { {
                                                                                                                                                                
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"

                                                                                                                                                                
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region __desugarer__var__3__33892 =
                                                                                                                                                                  ((x10.array.Region)(((x10.array.Region)
                                                                                                                                                                                        reg)));
                                                                                                                                                                
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
x10.array.Region ret33893 =
                                                                                                                                                                   null;
                                                                                                                                                                
//#line 212 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33888 =
                                                                                                                                                                  ((__desugarer__var__3__33892) != (null));
                                                                                                                                                                
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33889 =
                                                                                                                                                                  !(t33888);
                                                                                                                                                                
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33889) {
                                                                                                                                                                    
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33890 =
                                                                                                                                                                      true;
                                                                                                                                                                    
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33890) {
                                                                                                                                                                        
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t33891 =
                                                                                                                                                                          new x10.lang.FailedDynamicCheckException("x10.array.Region{self!=null}");
                                                                                                                                                                        
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
throw t33891;
                                                                                                                                                                    }
                                                                                                                                                                }
                                                                                                                                                                
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
ret33893 = ((x10.array.Region)(__desugarer__var__3__33892));
                                                                                                                                                                
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33894 =
                                                                                                                                                                  ((x10.array.Region)(ret33893));
                                                                                                                                                                
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33895 =
                                                                                                                                                                  reg.
                                                                                                                                                                    rank;
                                                                                                                                                                
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33896 =
                                                                                                                                                                  reg.
                                                                                                                                                                    rect;
                                                                                                                                                                
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33897 =
                                                                                                                                                                  reg.
                                                                                                                                                                    zeroBased;
                                                                                                                                                                
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33898 =
                                                                                                                                                                  reg.
                                                                                                                                                                    rail;
                                                                                                                                                                
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33899 =
                                                                                                                                                                  reg.size$O();
                                                                                                                                                                
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.region = ((x10.array.Region)(t33894));
                                                                                                                                                                this.rank = t33895;
                                                                                                                                                                this.rect = t33896;
                                                                                                                                                                this.zeroBased = t33897;
                                                                                                                                                                this.rail = t33898;
                                                                                                                                                                this.size = t33899;
                                                                                                                                                                
                                                                                                                                                                
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array.LayoutHelper crh =
                                                                                                                                                                  new x10.array.Array.LayoutHelper((java.lang.System[]) null).$init(((x10.array.Region)(reg)));
                                                                                                                                                                
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33190 =
                                                                                                                                                                  crh.
                                                                                                                                                                    min0;
                                                                                                                                                                
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout_min0 = t33190;
                                                                                                                                                                
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33191 =
                                                                                                                                                                  crh.
                                                                                                                                                                    stride1;
                                                                                                                                                                
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout_stride1 = t33191;
                                                                                                                                                                
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33192 =
                                                                                                                                                                  crh.
                                                                                                                                                                    min1;
                                                                                                                                                                
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout_min1 = t33192;
                                                                                                                                                                
//#line 218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t33193 =
                                                                                                                                                                  ((x10.array.Array)(crh.
                                                                                                                                                                                       layout));
                                                                                                                                                                
//#line 218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout = ((x10.array.Array)(t33193));
                                                                                                                                                                
//#line 219 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int n =
                                                                                                                                                                  crh.
                                                                                                                                                                    size;
                                                                                                                                                                
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33194 =
                                                                                                                                                                  ((((x10.core.IndexedMemoryChunk<$T>)(backingStore))).length);
                                                                                                                                                                
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33196 =
                                                                                                                                                                  ((n) > (((int)(t33194))));
                                                                                                                                                                
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33196) {
                                                                                                                                                                    
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.IllegalArgumentException t33195 =
                                                                                                                                                                      ((x10.lang.IllegalArgumentException)(new x10.lang.IllegalArgumentException(((java.lang.String)("backingStore too small")))));
                                                                                                                                                                    
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
throw t33195;
                                                                                                                                                                }
                                                                                                                                                                
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.raw = ((x10.core.IndexedMemoryChunk)(backingStore));
                                                                                                                                                            }
                                                                                                                                                            return this;
                                                                                                                                                            }
        
        // constructor
        public x10.array.Array<$T> $init(final x10.array.Region reg,
                                         final x10.core.IndexedMemoryChunk<$T> backingStore, __1$1x10$array$Array$$T$2 $dummy){return x10$array$Array$$init$S(reg,backingStore, $dummy);}
        
        
        
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
// creation method for java code (1-phase java constructor)
        public Array(final x10.rtt.Type $T,
                     final x10.core.IndexedMemoryChunk<$T> backingStore, __0$1x10$array$Array$$T$2 $dummy){this((java.lang.System[]) null, $T);
                                                                                                               $init(backingStore, (x10.array.Array.__0$1x10$array$Array$$T$2) null);}
        
        // constructor for non-virtual call
        final public x10.array.Array<$T> x10$array$Array$$init$S(final x10.core.IndexedMemoryChunk<$T> backingStore, __0$1x10$array$Array$$T$2 $dummy) { {
                                                                                                                                                                
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"

                                                                                                                                                                
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33197 =
                                                                                                                                                                  ((((x10.core.IndexedMemoryChunk<$T>)(backingStore))).length);
                                                                                                                                                                
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33198 =
                                                                                                                                                                  ((t33197) - (((int)(1))));
                                                                                                                                                                
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.RectRegion1D myReg =
                                                                                                                                                                  ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null).$init(t33198)));
                                                                                                                                                                
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33900 =
                                                                                                                                                                  ((((x10.core.IndexedMemoryChunk<$T>)(backingStore))).length);
                                                                                                                                                                
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.region = ((x10.array.Region)(myReg));
                                                                                                                                                                this.rank = 1;
                                                                                                                                                                this.rect = true;
                                                                                                                                                                this.zeroBased = true;
                                                                                                                                                                this.rail = true;
                                                                                                                                                                this.size = t33900;
                                                                                                                                                                
                                                                                                                                                                
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33200 =
                                                                                                                                                                  this.layout_min1 = 0;
                                                                                                                                                                
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33201 =
                                                                                                                                                                  this.layout_stride1 = t33200;
                                                                                                                                                                
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout_min0 = t33201;
                                                                                                                                                                
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout = null;
                                                                                                                                                                
//#line 239 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.raw = ((x10.core.IndexedMemoryChunk)(backingStore));
                                                                                                                                                            }
                                                                                                                                                            return this;
                                                                                                                                                            }
        
        // constructor
        public x10.array.Array<$T> $init(final x10.core.IndexedMemoryChunk<$T> backingStore, __0$1x10$array$Array$$T$2 $dummy){return x10$array$Array$$init$S(backingStore, $dummy);}
        
        
        
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
// creation method for java code (1-phase java constructor)
        public Array(final x10.rtt.Type $T,
                     final int size){this((java.lang.System[]) null, $T);
                                         $init(size);}
        
        // constructor for non-virtual call
        final public x10.array.Array<$T> x10$array$Array$$init$S(final int size) { {
                                                                                          
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"

                                                                                          
//#line 248 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33202 =
                                                                                            ((size) - (((int)(1))));
                                                                                          
//#line 248 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.RectRegion1D myReg =
                                                                                            ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null).$init(t33202)));
                                                                                          
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.region = ((x10.array.Region)(myReg));
                                                                                          this.rank = 1;
                                                                                          this.rect = true;
                                                                                          this.zeroBased = true;
                                                                                          this.rail = true;
                                                                                          this.size = size;
                                                                                          
                                                                                          
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33203 =
                                                                                            this.layout_min1 = 0;
                                                                                          
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33204 =
                                                                                            this.layout_stride1 = t33203;
                                                                                          
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout_min0 = t33204;
                                                                                          
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout = null;
                                                                                          
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33205 =
                                                                                            ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<$T>allocate($T, ((int)(size)), true)));
                                                                                          
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.raw = ((x10.core.IndexedMemoryChunk)(t33205));
                                                                                      }
                                                                                      return this;
                                                                                      }
        
        // constructor
        public x10.array.Array<$T> $init(final int size){return x10$array$Array$$init$S(size);}
        
        
        
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
// creation method for java code (1-phase java constructor)
        public Array(final x10.rtt.Type $T,
                     final int size,
                     final x10.core.fun.Fun_0_1<x10.core.Int,$T> init, __1$1x10$lang$Int$3x10$array$Array$$T$2 $dummy){this((java.lang.System[]) null, $T);
                                                                                                                           $init(size,init, (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null);}
        
        // constructor for non-virtual call
        final public x10.array.Array<$T> x10$array$Array$$init$S(final int size,
                                                                 final x10.core.fun.Fun_0_1<x10.core.Int,$T> init, __1$1x10$lang$Int$3x10$array$Array$$T$2 $dummy) { {
                                                                                                                                                                            
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"

                                                                                                                                                                            
//#line 275 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33206 =
                                                                                                                                                                              ((size) - (((int)(1))));
                                                                                                                                                                            
//#line 275 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.RectRegion1D myReg =
                                                                                                                                                                              ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null).$init(t33206)));
                                                                                                                                                                            
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.region = ((x10.array.Region)(myReg));
                                                                                                                                                                            this.rank = 1;
                                                                                                                                                                            this.rect = true;
                                                                                                                                                                            this.zeroBased = true;
                                                                                                                                                                            this.rail = true;
                                                                                                                                                                            this.size = size;
                                                                                                                                                                            
                                                                                                                                                                            
//#line 278 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33207 =
                                                                                                                                                                              this.layout_min1 = 0;
                                                                                                                                                                            
//#line 278 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33208 =
                                                                                                                                                                              this.layout_stride1 = t33207;
                                                                                                                                                                            
//#line 278 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout_min0 = t33208;
                                                                                                                                                                            
//#line 279 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout = null;
                                                                                                                                                                            
//#line 280 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> r =
                                                                                                                                                                              x10.core.IndexedMemoryChunk.<$T>allocate($T, ((int)(size)), false);
                                                                                                                                                                            
//#line 281 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int i32955min33908 =
                                                                                                                                                                              0;
                                                                                                                                                                            
//#line 281 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int i32955max33909 =
                                                                                                                                                                              ((size) - (((int)(1))));
                                                                                                                                                                            
//#line 281 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
int i33905 =
                                                                                                                                                                              i32955min33908;
                                                                                                                                                                            
//#line 281 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
for (;
                                                                                                                                                                                                                                                                       true;
                                                                                                                                                                                                                                                                       ) {
                                                                                                                                                                                
//#line 281 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33906 =
                                                                                                                                                                                  i33905;
                                                                                                                                                                                
//#line 281 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33907 =
                                                                                                                                                                                  ((t33906) <= (((int)(i32955max33909))));
                                                                                                                                                                                
//#line 281 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (!(t33907)) {
                                                                                                                                                                                    
//#line 281 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
break;
                                                                                                                                                                                }
                                                                                                                                                                                
//#line 281 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int i33902 =
                                                                                                                                                                                  i33905;
                                                                                                                                                                                
//#line 282 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $T t33901 =
                                                                                                                                                                                  (($T)((($T)
                                                                                                                                                                                          ((x10.core.fun.Fun_0_1<x10.core.Int,$T>)init).$apply(x10.core.Int.$box(i33902),x10.rtt.Types.INT))));
                                                                                                                                                                                
//#line 282 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
(((x10.core.IndexedMemoryChunk<$T>)(r))).$set(((int)(i33902)), t33901);
                                                                                                                                                                                
//#line 281 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33903 =
                                                                                                                                                                                  i33905;
                                                                                                                                                                                
//#line 281 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33904 =
                                                                                                                                                                                  ((t33903) + (((int)(1))));
                                                                                                                                                                                
//#line 281 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
i33905 = t33904;
                                                                                                                                                                            }
                                                                                                                                                                            
//#line 284 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.raw = ((x10.core.IndexedMemoryChunk)(r));
                                                                                                                                                                        }
                                                                                                                                                                        return this;
                                                                                                                                                                        }
        
        // constructor
        public x10.array.Array<$T> $init(final int size,
                                         final x10.core.fun.Fun_0_1<x10.core.Int,$T> init, __1$1x10$lang$Int$3x10$array$Array$$T$2 $dummy){return x10$array$Array$$init$S(size,init, $dummy);}
        
        
        
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
// creation method for java code (1-phase java constructor)
        public Array(final x10.rtt.Type $T,
                     final int size,
                     final $T init, __1x10$array$Array$$T $dummy){this((java.lang.System[]) null, $T);
                                                                      $init(size,init, (x10.array.Array.__1x10$array$Array$$T) null);}
        
        // constructor for non-virtual call
        final public x10.array.Array<$T> x10$array$Array$$init$S(final int size,
                                                                 final $T init, __1x10$array$Array$$T $dummy) { {
                                                                                                                       
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"

                                                                                                                       
//#line 297 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33215 =
                                                                                                                         ((size) - (((int)(1))));
                                                                                                                       
//#line 297 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.RectRegion1D myReg =
                                                                                                                         ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null).$init(t33215)));
                                                                                                                       
//#line 298 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.region = ((x10.array.Region)(myReg));
                                                                                                                       this.rank = 1;
                                                                                                                       this.rect = true;
                                                                                                                       this.zeroBased = true;
                                                                                                                       this.rail = true;
                                                                                                                       this.size = size;
                                                                                                                       
                                                                                                                       
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33216 =
                                                                                                                         this.layout_min1 = 0;
                                                                                                                       
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33217 =
                                                                                                                         this.layout_stride1 = t33216;
                                                                                                                       
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout_min0 = t33217;
                                                                                                                       
//#line 301 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout = null;
                                                                                                                       
//#line 302 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> r =
                                                                                                                         x10.core.IndexedMemoryChunk.<$T>allocate($T, ((int)(size)), false);
                                                                                                                       
//#line 303 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int i32971min33916 =
                                                                                                                         0;
                                                                                                                       
//#line 303 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int i32971max33917 =
                                                                                                                         ((size) - (((int)(1))));
                                                                                                                       
//#line 303 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
int i33913 =
                                                                                                                         i32971min33916;
                                                                                                                       
//#line 303 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
for (;
                                                                                                                                                                                                                  true;
                                                                                                                                                                                                                  ) {
                                                                                                                           
//#line 303 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33914 =
                                                                                                                             i33913;
                                                                                                                           
//#line 303 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33915 =
                                                                                                                             ((t33914) <= (((int)(i32971max33917))));
                                                                                                                           
//#line 303 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (!(t33915)) {
                                                                                                                               
//#line 303 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
break;
                                                                                                                           }
                                                                                                                           
//#line 303 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int i33910 =
                                                                                                                             i33913;
                                                                                                                           
//#line 304 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
(((x10.core.IndexedMemoryChunk<$T>)(r))).$set(((int)(i33910)), init);
                                                                                                                           
//#line 303 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33911 =
                                                                                                                             i33913;
                                                                                                                           
//#line 303 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33912 =
                                                                                                                             ((t33911) + (((int)(1))));
                                                                                                                           
//#line 303 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
i33913 = t33912;
                                                                                                                       }
                                                                                                                       
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.raw = ((x10.core.IndexedMemoryChunk)(r));
                                                                                                                   }
                                                                                                                   return this;
                                                                                                                   }
        
        // constructor
        public x10.array.Array<$T> $init(final int size,
                                         final $T init, __1x10$array$Array$$T $dummy){return x10$array$Array$$init$S(size,init, $dummy);}
        
        
        
//#line 315 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
// creation method for java code (1-phase java constructor)
        public Array(final x10.rtt.Type $T,
                     final x10.array.Array<$T> init, __0$1x10$array$Array$$T$2 $dummy){this((java.lang.System[]) null, $T);
                                                                                           $init(init, (x10.array.Array.__0$1x10$array$Array$$T$2) null);}
        
        // constructor for non-virtual call
        final public x10.array.Array<$T> x10$array$Array$$init$S(final x10.array.Array<$T> init, __0$1x10$array$Array$$T$2 $dummy) { {
                                                                                                                                            
//#line 315 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"

                                                                                                                                            
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33918 =
                                                                                                                                              ((x10.array.Region)(((x10.array.Array<$T>)init).
                                                                                                                                                                    region));
                                                                                                                                            
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33919 =
                                                                                                                                              ((x10.array.Array<$T>)init).
                                                                                                                                                rank;
                                                                                                                                            
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33920 =
                                                                                                                                              ((x10.array.Array<$T>)init).
                                                                                                                                                rect;
                                                                                                                                            
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33921 =
                                                                                                                                              ((x10.array.Array<$T>)init).
                                                                                                                                                zeroBased;
                                                                                                                                            
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33922 =
                                                                                                                                              ((x10.array.Array<$T>)init).
                                                                                                                                                rail;
                                                                                                                                            
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33923 =
                                                                                                                                              ((x10.array.Array<$T>)init).
                                                                                                                                                size;
                                                                                                                                            
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.region = ((x10.array.Region)(t33918));
                                                                                                                                            this.rank = t33919;
                                                                                                                                            this.rect = t33920;
                                                                                                                                            this.zeroBased = t33921;
                                                                                                                                            this.rail = t33922;
                                                                                                                                            this.size = t33923;
                                                                                                                                            
                                                                                                                                            
//#line 318 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33229 =
                                                                                                                                              ((x10.array.Array<$T>)init).
                                                                                                                                                layout_min0;
                                                                                                                                            
//#line 318 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout_min0 = t33229;
                                                                                                                                            
//#line 319 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33230 =
                                                                                                                                              ((x10.array.Array<$T>)init).
                                                                                                                                                layout_stride1;
                                                                                                                                            
//#line 319 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout_stride1 = t33230;
                                                                                                                                            
//#line 320 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33231 =
                                                                                                                                              ((x10.array.Array<$T>)init).
                                                                                                                                                layout_min1;
                                                                                                                                            
//#line 320 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout_min1 = t33231;
                                                                                                                                            
//#line 321 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t33232 =
                                                                                                                                              ((x10.array.Array)(((x10.array.Array<$T>)init).
                                                                                                                                                                   layout));
                                                                                                                                            
//#line 321 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout = ((x10.array.Array)(t33232));
                                                                                                                                            
//#line 322 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33233 =
                                                                                                                                              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<$T>)init).
                                                                                                                                                                               raw));
                                                                                                                                            
//#line 322 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33234 =
                                                                                                                                              ((((x10.core.IndexedMemoryChunk<$T>)(t33233))).length);
                                                                                                                                            
//#line 322 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> r =
                                                                                                                                              x10.core.IndexedMemoryChunk.<$T>allocate($T, ((int)(t33234)), false);
                                                                                                                                            
//#line 323 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33235 =
                                                                                                                                              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<$T>)init).
                                                                                                                                                                               raw));
                                                                                                                                            
//#line 323 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33236 =
                                                                                                                                              ((((x10.core.IndexedMemoryChunk<$T>)(r))).length);
                                                                                                                                            
//#line 323 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
x10.core.IndexedMemoryChunk.<$T>copy(t33235,((int)(0)),r,((int)(0)),((int)(t33236)));
                                                                                                                                            
//#line 324 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.raw = ((x10.core.IndexedMemoryChunk)(r));
                                                                                                                                        }
                                                                                                                                        return this;
                                                                                                                                        }
        
        // constructor
        public x10.array.Array<$T> $init(final x10.array.Array<$T> init, __0$1x10$array$Array$$T$2 $dummy){return x10$array$Array$$init$S(init, $dummy);}
        
        
        
//#line 333 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
// creation method for java code (1-phase java constructor)
        public Array(final x10.rtt.Type $T,
                     final x10.array.RemoteArray<$T> init, __0$1x10$array$Array$$T$2 $dummy){this((java.lang.System[]) null, $T);
                                                                                                 $init(init, (x10.array.Array.__0$1x10$array$Array$$T$2) null);}
        
        // constructor for non-virtual call
        final public x10.array.Array<$T> x10$array$Array$$init$S(final x10.array.RemoteArray<$T> init, __0$1x10$array$Array$$T$2 $dummy) { {
                                                                                                                                                  
//#line 335 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.GlobalRef<x10.array.Array<$T>> t33237 =
                                                                                                                                                    ((x10.core.GlobalRef)(((x10.array.RemoteArray<$T>)init).
                                                                                                                                                                            array));
                                                                                                                                                  
//#line 335 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<$T> t33238 =
                                                                                                                                                    (((x10.core.GlobalRef<x10.array.Array<$T>>)(t33237))).$apply$G();
                                                                                                                                                  
//#line 335 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.$init(t33238, (x10.array.Array.__0$1x10$array$Array$$T$2) null);
                                                                                                                                              }
                                                                                                                                              return this;
                                                                                                                                              }
        
        // constructor
        public x10.array.Array<$T> $init(final x10.array.RemoteArray<$T> init, __0$1x10$array$Array$$T$2 $dummy){return x10$array$Array$$init$S(init, $dummy);}
        
        
        
//#line 343 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public java.lang.String
                                                                                                toString(
                                                                                                ){
            
//#line 344 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33261 =
              rail;
            
//#line 344 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33261) {
                
//#line 345 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.util.StringBuilder sb =
                  ((x10.util.StringBuilder)(new x10.util.StringBuilder((java.lang.System[]) null).$init()));
                
//#line 346 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
sb.add(((java.lang.String)("[")));
                
//#line 347 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33239 =
                  size;
                
//#line 347 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int sz =
                  x10.lang.Math.min$O((int)(t33239),
                                      (int)(10));
                
//#line 348 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
int i33932 =
                  0;
                
//#line 348 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
for (;
                                                                                                           true;
                                                                                                           ) {
                    
//#line 348 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33933 =
                      i33932;
                    
//#line 348 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33934 =
                      ((t33933) < (((int)(sz))));
                    
//#line 348 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (!(t33934)) {
                        
//#line 348 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
break;
                    }
                    
//#line 349 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33924 =
                      i33932;
                    
//#line 349 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33925 =
                      ((t33924) > (((int)(0))));
                    
//#line 349 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33925) {
                        
//#line 349 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
sb.add(((java.lang.String)(",")));
                    }
                    
//#line 350 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33926 =
                      ((x10.core.IndexedMemoryChunk)(raw));
                    
//#line 350 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33927 =
                      i33932;
                    
//#line 350 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $T t33928 =
                      (($T)((((x10.core.IndexedMemoryChunk<$T>)(t33926))).$apply$G(((int)(t33927)))));
                    
//#line 350 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33929 =
                      (("") + (t33928));
                    
//#line 350 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
sb.add(((java.lang.String)(t33929)));
                    
//#line 348 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33930 =
                      i33932;
                    
//#line 348 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33931 =
                      ((t33930) + (((int)(1))));
                    
//#line 348 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
i33932 = t33931;
                }
                
//#line 352 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33251 =
                  size;
                
//#line 352 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33256 =
                  ((sz) < (((int)(t33251))));
                
//#line 352 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33256) {
                    
//#line 352 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33252 =
                      size;
                    
//#line 352 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33253 =
                      ((t33252) - (((int)(sz))));
                    
//#line 352 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33254 =
                      (("...(omitted ") + ((x10.core.Int.$box(t33253))));
                    
//#line 352 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33255 =
                      ((t33254) + (" elements)"));
                    
//#line 352 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
sb.add(((java.lang.String)(t33255)));
                }
                
//#line 353 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
sb.add(((java.lang.String)("]")));
                
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33257 =
                  sb.toString();
                
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33257;
            } else {
                
//#line 356 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33258 =
                  ((x10.array.Region)(region));
                
//#line 356 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33259 =
                  (("Array(") + (t33258));
                
//#line 356 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33260 =
                  ((t33259) + (")"));
                
//#line 356 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33260;
            }
        }
        
        
//#line 366 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public x10.lang.Iterator<x10.array.Point>
                                                                                                iterator(
                                                                                                ){
            
//#line 366 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33262 =
              ((x10.array.Region)(region));
            
//#line 366 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.Iterator<x10.array.Point> t33263 =
              t33262.iterator();
            
//#line 366 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33263;
        }
        
        
//#line 374 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public x10.lang.Iterable<$T>
                                                                                                values(
                                                                                                ){
            
//#line 375 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33266 =
              rect;
            
//#line 375 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33266) {
                
//#line 376 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array.Anonymous$13425<$T> t33264 =
                  ((x10.array.Array.Anonymous$13425)(new x10.array.Array.Anonymous$13425<$T>((java.lang.System[]) null, $T).$init(this, (x10.array.Array.Anonymous$13425.__0$1x10$array$Array$Anonymous$13425$$T32945$2) null)));
                
//#line 376 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33264;
            } else {
                
//#line 384 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array.Anonymous$13713<$T> t33265 =
                  ((x10.array.Array.Anonymous$13713)(new x10.array.Array.Anonymous$13713<$T>((java.lang.System[]) null, $T).$init(this, (x10.array.Array.Anonymous$13713.__0$1x10$array$Array$Anonymous$13713$$T32947$2) null)));
                
//#line 384 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33265;
            }
        }
        
        
//#line 394 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public x10.lang.Sequence<$T>
                                                                                                sequence(
                                                                                                ){
            
//#line 394 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array.Anonymous$14047<$T> t33268 =
              ((x10.array.Array.Anonymous$14047)(new x10.array.Array.Anonymous$14047<$T>((java.lang.System[]) null, $T).$init(this, (x10.array.Array.Anonymous$14047.__0$1x10$array$Array$Anonymous$14047$$T32949$2) null)));
            
//#line 394 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33268;
        }
        
        
//#line 412 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public $T
                                                                                                $apply$G(
                                                                                                final int i0){
            
//#line 414 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33280 =
              rail;
            
//#line 414 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33280) {
                
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33270 =
                  ((x10.core.IndexedMemoryChunk)(raw));
                
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $T t33271 =
                  (($T)((((x10.core.IndexedMemoryChunk<$T>)(t33270))).$apply$G(((int)(i0)))));
                
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33271;
            } else {
                
//#line 418 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
boolean t33274 =
                  true;
                
//#line 418 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33274) {
                    
//#line 418 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33272 =
                      ((x10.array.Region)(region));
                    
//#line 418 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33273 =
                      t33272.contains$O((int)(i0));
                    
//#line 418 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
t33274 = !(t33273);
                }
                
//#line 418 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33275 =
                  t33274;
                
//#line 418 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33275) {
                    
//#line 419 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
x10.array.Array.raiseBoundsError((int)(i0));
                }
                
//#line 421 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33277 =
                  ((x10.core.IndexedMemoryChunk)(raw));
                
//#line 421 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33276 =
                  layout_min0;
                
//#line 421 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33278 =
                  ((i0) - (((int)(t33276))));
                
//#line 421 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $T t33279 =
                  (($T)((((x10.core.IndexedMemoryChunk<$T>)(t33277))).$apply$G(((int)(t33278)))));
                
//#line 421 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33279;
            }
        }
        
        
//#line 436 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public $T
                                                                                                $apply$G(
                                                                                                final int i0,
                                                                                                final int i1){
            
//#line 437 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
boolean t33284 =
              true;
            
//#line 437 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33284) {
                
//#line 437 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33282 =
                  ((x10.array.Region)(region));
                
//#line 437 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33283 =
                  t33282.contains$O((int)(i0),
                                    (int)(i1));
                
//#line 437 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
t33284 = !(t33283);
            }
            
//#line 437 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33285 =
              t33284;
            
//#line 437 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33285) {
                
//#line 438 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
x10.array.Array.raiseBoundsError((int)(i0),
                                                                                                                                       (int)(i1));
            }
            
//#line 440 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33286 =
              layout_min0;
            
//#line 440 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
int offset =
              ((i0) - (((int)(t33286))));
            
//#line 441 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33287 =
              offset;
            
//#line 441 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33288 =
              layout_stride1;
            
//#line 441 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33289 =
              ((t33287) * (((int)(t33288))));
            
//#line 441 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33290 =
              ((t33289) + (((int)(i1))));
            
//#line 441 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33291 =
              layout_min1;
            
//#line 441 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33292 =
              ((t33290) - (((int)(t33291))));
            
//#line 441 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
offset = t33292;
            
//#line 442 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33293 =
              ((x10.core.IndexedMemoryChunk)(raw));
            
//#line 442 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33294 =
              offset;
            
//#line 442 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $T t33295 =
              (($T)((((x10.core.IndexedMemoryChunk<$T>)(t33293))).$apply$G(((int)(t33294)))));
            
//#line 442 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33295;
        }
        
        
//#line 457 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public $T
                                                                                                $apply$G(
                                                                                                final int i0,
                                                                                                final int i1,
                                                                                                final int i2){
            
//#line 458 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
boolean t33299 =
              true;
            
//#line 458 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33299) {
                
//#line 458 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33297 =
                  ((x10.array.Region)(region));
                
//#line 458 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33298 =
                  t33297.contains$O((int)(i0),
                                    (int)(i1),
                                    (int)(i2));
                
//#line 458 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
t33299 = !(t33298);
            }
            
//#line 458 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33300 =
              t33299;
            
//#line 458 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33300) {
                
//#line 459 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
x10.array.Array.raiseBoundsError((int)(i0),
                                                                                                                                       (int)(i1),
                                                                                                                                       (int)(i2));
            }
            
//#line 461 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33301 =
              ((x10.core.IndexedMemoryChunk)(raw));
            
//#line 461 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33302 =
              this.offset$O((int)(i0),
                            (int)(i1),
                            (int)(i2));
            
//#line 461 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $T t33303 =
              (($T)((((x10.core.IndexedMemoryChunk<$T>)(t33301))).$apply$G(((int)(t33302)))));
            
//#line 461 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33303;
        }
        
        
//#line 477 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public $T
                                                                                                $apply$G(
                                                                                                final int i0,
                                                                                                final int i1,
                                                                                                final int i2,
                                                                                                final int i3){
            
//#line 478 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
boolean t33307 =
              true;
            
//#line 478 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33307) {
                
//#line 478 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33305 =
                  ((x10.array.Region)(region));
                
//#line 478 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33306 =
                  t33305.contains$O((int)(i0),
                                    (int)(i1),
                                    (int)(i2),
                                    (int)(i3));
                
//#line 478 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
t33307 = !(t33306);
            }
            
//#line 478 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33308 =
              t33307;
            
//#line 478 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33308) {
                
//#line 479 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
x10.array.Array.raiseBoundsError((int)(i0),
                                                                                                                                       (int)(i1),
                                                                                                                                       (int)(i2),
                                                                                                                                       (int)(i3));
            }
            
//#line 481 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33309 =
              ((x10.core.IndexedMemoryChunk)(raw));
            
//#line 481 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33310 =
              this.offset$O((int)(i0),
                            (int)(i1),
                            (int)(i2),
                            (int)(i3));
            
//#line 481 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $T t33311 =
              (($T)((((x10.core.IndexedMemoryChunk<$T>)(t33309))).$apply$G(((int)(t33310)))));
            
//#line 481 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33311;
        }
        
        
//#line 493 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public $T
                                                                                                $apply$G(
                                                                                                final x10.array.Point pt){
            
//#line 494 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
boolean t33314 =
              true;
            
//#line 494 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33314) {
                
//#line 494 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33312 =
                  ((x10.array.Region)(region));
                
//#line 494 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33313 =
                  t33312.contains$O(((x10.array.Point)(pt)));
                
//#line 494 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
t33314 = !(t33313);
            }
            
//#line 494 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33315 =
              t33314;
            
//#line 494 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33315) {
                
//#line 495 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
x10.array.Array.raiseBoundsError(((x10.array.Point)(pt)));
            }
            
//#line 497 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33316 =
              ((x10.core.IndexedMemoryChunk)(raw));
            
//#line 497 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33317 =
              this.offset$O(((x10.array.Point)(pt)));
            
//#line 497 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $T t33318 =
              (($T)((((x10.core.IndexedMemoryChunk<$T>)(t33316))).$apply$G(((int)(t33317)))));
            
//#line 497 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33318;
        }
        
        
//#line 513 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public $T
                                                                                                $set__1x10$array$Array$$T$G(
                                                                                                final int i0,
                                                                                                final $T v){
            
//#line 515 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33328 =
              rail;
            
//#line 515 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33328) {
                
//#line 517 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33320 =
                  ((x10.core.IndexedMemoryChunk)(raw));
                
//#line 517 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t33320))).$set(((int)(i0)), v);
            } else {
                
//#line 519 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
boolean t33323 =
                  true;
                
//#line 519 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33323) {
                    
//#line 519 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33321 =
                      ((x10.array.Region)(region));
                    
//#line 519 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33322 =
                      t33321.contains$O((int)(i0));
                    
//#line 519 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
t33323 = !(t33322);
                }
                
//#line 519 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33324 =
                  t33323;
                
//#line 519 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33324) {
                    
//#line 520 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
x10.array.Array.raiseBoundsError((int)(i0));
                }
                
//#line 522 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33326 =
                  ((x10.core.IndexedMemoryChunk)(raw));
                
//#line 522 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33325 =
                  layout_min0;
                
//#line 522 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33327 =
                  ((i0) - (((int)(t33325))));
                
//#line 522 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t33326))).$set(((int)(t33327)), v);
            }
            
//#line 524 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return v;
        }
        
        
//#line 540 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public $T
                                                                                                $set__2x10$array$Array$$T$G(
                                                                                                final int i0,
                                                                                                final int i1,
                                                                                                final $T v){
            
//#line 541 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
boolean t33332 =
              true;
            
//#line 541 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33332) {
                
//#line 541 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33330 =
                  ((x10.array.Region)(region));
                
//#line 541 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33331 =
                  t33330.contains$O((int)(i0),
                                    (int)(i1));
                
//#line 541 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
t33332 = !(t33331);
            }
            
//#line 541 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33333 =
              t33332;
            
//#line 541 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33333) {
                
//#line 542 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
x10.array.Array.raiseBoundsError((int)(i0),
                                                                                                                                       (int)(i1));
            }
            
//#line 544 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33334 =
              layout_min0;
            
//#line 544 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
int offset =
              ((i0) - (((int)(t33334))));
            
//#line 545 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33335 =
              offset;
            
//#line 545 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33336 =
              layout_stride1;
            
//#line 545 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33337 =
              ((t33335) * (((int)(t33336))));
            
//#line 545 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33338 =
              ((t33337) + (((int)(i1))));
            
//#line 545 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33339 =
              layout_min1;
            
//#line 545 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33340 =
              ((t33338) - (((int)(t33339))));
            
//#line 545 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
offset = t33340;
            
//#line 546 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33341 =
              ((x10.core.IndexedMemoryChunk)(raw));
            
//#line 546 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33342 =
              offset;
            
//#line 546 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t33341))).$set(((int)(t33342)), v);
            
//#line 547 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return v;
        }
        
        
//#line 564 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public $T
                                                                                                $set__3x10$array$Array$$T$G(
                                                                                                final int i0,
                                                                                                final int i1,
                                                                                                final int i2,
                                                                                                final $T v){
            
//#line 565 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
boolean t33346 =
              true;
            
//#line 565 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33346) {
                
//#line 565 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33344 =
                  ((x10.array.Region)(region));
                
//#line 565 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33345 =
                  t33344.contains$O((int)(i0),
                                    (int)(i1),
                                    (int)(i2));
                
//#line 565 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
t33346 = !(t33345);
            }
            
//#line 565 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33347 =
              t33346;
            
//#line 565 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33347) {
                
//#line 566 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
x10.array.Array.raiseBoundsError((int)(i0),
                                                                                                                                       (int)(i1),
                                                                                                                                       (int)(i2));
            }
            
//#line 568 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33348 =
              ((x10.core.IndexedMemoryChunk)(raw));
            
//#line 568 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33349 =
              this.offset$O((int)(i0),
                            (int)(i1),
                            (int)(i2));
            
//#line 568 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t33348))).$set(((int)(t33349)), v);
            
//#line 569 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return v;
        }
        
        
//#line 587 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public $T
                                                                                                $set__4x10$array$Array$$T$G(
                                                                                                final int i0,
                                                                                                final int i1,
                                                                                                final int i2,
                                                                                                final int i3,
                                                                                                final $T v){
            
//#line 588 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
boolean t33353 =
              true;
            
//#line 588 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33353) {
                
//#line 588 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33351 =
                  ((x10.array.Region)(region));
                
//#line 588 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33352 =
                  t33351.contains$O((int)(i0),
                                    (int)(i1),
                                    (int)(i2),
                                    (int)(i3));
                
//#line 588 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
t33353 = !(t33352);
            }
            
//#line 588 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33354 =
              t33353;
            
//#line 588 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33354) {
                
//#line 589 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
x10.array.Array.raiseBoundsError((int)(i0),
                                                                                                                                       (int)(i1),
                                                                                                                                       (int)(i2),
                                                                                                                                       (int)(i3));
            }
            
//#line 591 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33355 =
              ((x10.core.IndexedMemoryChunk)(raw));
            
//#line 591 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33356 =
              this.offset$O((int)(i0),
                            (int)(i1),
                            (int)(i2),
                            (int)(i3));
            
//#line 591 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t33355))).$set(((int)(t33356)), v);
            
//#line 592 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return v;
        }
        
        
//#line 606 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public $T
                                                                                                $set__1x10$array$Array$$T$G(
                                                                                                final x10.array.Point p,
                                                                                                final $T v){
            
//#line 607 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
boolean t33359 =
              true;
            
//#line 607 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33359) {
                
//#line 607 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33357 =
                  ((x10.array.Region)(region));
                
//#line 607 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33358 =
                  t33357.contains$O(((x10.array.Point)(p)));
                
//#line 607 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
t33359 = !(t33358);
            }
            
//#line 607 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33360 =
              t33359;
            
//#line 607 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33360) {
                
//#line 608 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
x10.array.Array.raiseBoundsError(((x10.array.Point)(p)));
            }
            
//#line 610 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33361 =
              ((x10.core.IndexedMemoryChunk)(raw));
            
//#line 610 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33362 =
              this.offset$O(((x10.array.Point)(p)));
            
//#line 610 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t33361))).$set(((int)(t33362)), v);
            
//#line 611 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return v;
        }
        
        
//#line 620 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public void
                                                                                                fill__0x10$array$Array$$T(
                                                                                                final $T v){
            
//#line 621 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33376 =
              rect;
            
//#line 621 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33376) {
                
//#line 625 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int i32987min32988 =
                  0;
                
//#line 625 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33363 =
                  ((x10.core.IndexedMemoryChunk)(raw));
                
//#line 625 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33364 =
                  ((((x10.core.IndexedMemoryChunk<$T>)(t33363))).length);
                
//#line 625 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int i32987max32989 =
                  ((t33364) - (((int)(1))));
                
//#line 625 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
int i33939 =
                  i32987min32988;
                
//#line 625 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
for (;
                                                                                                           true;
                                                                                                           ) {
                    
//#line 625 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33940 =
                      i33939;
                    
//#line 625 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33941 =
                      ((t33940) <= (((int)(i32987max32989))));
                    
//#line 625 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (!(t33941)) {
                        
//#line 625 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
break;
                    }
                    
//#line 625 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int i33936 =
                      i33939;
                    
//#line 626 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33935 =
                      ((x10.core.IndexedMemoryChunk)(raw));
                    
//#line 626 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t33935))).$set(((int)(i33936)), v);
                    
//#line 625 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33937 =
                      i33939;
                    
//#line 625 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33938 =
                      ((t33937) + (((int)(1))));
                    
//#line 625 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
i33939 = t33938;
                }
            } else {
                
//#line 629 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33372 =
                  ((x10.array.Region)(region));
                
//#line 629 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.Iterator<x10.array.Point> p33003 =
                  t33372.iterator();
                
//#line 629 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
for (;
                                                                                                           true;
                                                                                                           ) {
                    
//#line 629 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33375 =
                      ((x10.lang.Iterator<x10.array.Point>)p33003).hasNext$O();
                    
//#line 629 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (!(t33375)) {
                        
//#line 629 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
break;
                    }
                    
//#line 629 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Point p33942 =
                      ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)p33003).next$G()));
                    
//#line 630 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33943 =
                      ((x10.core.IndexedMemoryChunk)(raw));
                    
//#line 630 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33944 =
                      this.offset$O(((x10.array.Point)(p33942)));
                    
//#line 630 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t33943))).$set(((int)(t33944)), v);
                }
            }
        }
        
        
//#line 640 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public void
                                                                                                clear(
                                                                                                ){
            
//#line 641 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33378 =
              ((x10.core.IndexedMemoryChunk)(raw));
            
//#line 641 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33377 =
              ((x10.core.IndexedMemoryChunk)(raw));
            
//#line 641 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33379 =
              ((((x10.core.IndexedMemoryChunk<$T>)(t33377))).length);
            
//#line 641 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t33378))).clear(((int)(0)), ((int)(t33379)));
        }
        
        
//#line 657 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public <$U>x10.array.Array<$U>
                                                                                                map__0$1x10$array$Array$$T$3x10$array$Array$$U$2(
                                                                                                final x10.rtt.Type $U,
                                                                                                final x10.core.fun.Fun_0_1 op){
            
//#line 658 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33382 =
              ((x10.array.Region)(region));
            
//#line 658 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.fun.Fun_0_1<x10.array.Point,$U> t33383 =
              ((x10.core.fun.Fun_0_1)(new x10.array.Array.$Closure$0<$T, $U>($T, $U, ((x10.array.Array)(this)),
                                                                             op, (x10.array.Array.$Closure$0.__0$1x10$array$Array$$Closure$0$$T$2__1$1x10$array$Array$$Closure$0$$T$3x10$array$Array$$Closure$0$$U$2) null)));
            
//#line 658 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<$U> t33384 =
              ((x10.array.Array)(new x10.array.Array<$U>((java.lang.System[]) null, $U).$init(((x10.array.Region)(t33382)),
                                                                                              ((x10.core.fun.Fun_0_1)(t33383)), (x10.array.Array.__1$1x10$array$Point$3x10$array$Array$$T$2) null)));
            
//#line 658 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33384;
        }
        
        
//#line 675 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public <$U>x10.array.Array<$U>
                                                                                                map__0$1x10$array$Array$$U$2__1$1x10$array$Array$$T$3x10$array$Array$$U$2(
                                                                                                final x10.rtt.Type $U,
                                                                                                final x10.array.Array dst,
                                                                                                final x10.core.fun.Fun_0_1 op){
            
//#line 677 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33401 =
              rect;
            
//#line 677 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33401) {
                
//#line 681 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int i33005min33006 =
                  0;
                
//#line 681 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33385 =
                  ((x10.core.IndexedMemoryChunk)(raw));
                
//#line 681 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33386 =
                  ((((x10.core.IndexedMemoryChunk<$T>)(t33385))).length);
                
//#line 681 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int i33005max33007 =
                  ((t33386) - (((int)(1))));
                
//#line 681 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
int i33952 =
                  i33005min33006;
                
//#line 681 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
for (;
                                                                                                           true;
                                                                                                           ) {
                    
//#line 681 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33953 =
                      i33952;
                    
//#line 681 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33954 =
                      ((t33953) <= (((int)(i33005max33007))));
                    
//#line 681 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (!(t33954)) {
                        
//#line 681 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
break;
                    }
                    
//#line 681 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int i33949 =
                      i33952;
                    
//#line 682 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$U> t33945 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<$U>)dst).
                                                       raw));
                    
//#line 682 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33946 =
                      ((x10.core.IndexedMemoryChunk)(raw));
                    
//#line 682 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $T t33947 =
                      (($T)((((x10.core.IndexedMemoryChunk<$T>)(t33946))).$apply$G(((int)(i33949)))));
                    
//#line 682 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $U t33948 =
                      (($U)((($U)
                              ((x10.core.fun.Fun_0_1<$T,$U>)op).$apply(t33947,$T))));
                    
//#line 682 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
(((x10.core.IndexedMemoryChunk<$U>)(t33945))).$set(((int)(i33949)), t33948);
                    
//#line 681 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33950 =
                      i33952;
                    
//#line 681 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33951 =
                      ((t33950) + (((int)(1))));
                    
//#line 681 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
i33952 = t33951;
                }
            } else {
                
//#line 685 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33397 =
                  ((x10.array.Region)(region));
                
//#line 685 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.Iterator<x10.array.Point> p33021 =
                  t33397.iterator();
                
//#line 685 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
for (;
                                                                                                           true;
                                                                                                           ) {
                    
//#line 685 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33400 =
                      ((x10.lang.Iterator<x10.array.Point>)p33021).hasNext$O();
                    
//#line 685 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (!(t33400)) {
                        
//#line 685 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
break;
                    }
                    
//#line 685 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Point p33955 =
                      ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)p33021).next$G()));
                    
//#line 686 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $T t33956 =
                      (($T)(this.$apply$G(((x10.array.Point)(p33955)))));
                    
//#line 686 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $U t33957 =
                      (($U)((($U)
                              ((x10.core.fun.Fun_0_1<$T,$U>)op).$apply(t33956,$T))));
                    
//#line 686 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
((x10.array.Array<$U>)dst).$set__1x10$array$Array$$T$G(((x10.array.Point)(p33955)),
                                                                                                                                                                 (($U)(t33957)));
                }
            }
            
//#line 689 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return dst;
        }
        
        
//#line 707 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public <$U>x10.array.Array<$U>
                                                                                                map__0$1x10$array$Array$$U$2__2$1x10$array$Array$$T$3x10$array$Array$$U$2(
                                                                                                final x10.rtt.Type $U,
                                                                                                final x10.array.Array dst,
                                                                                                final x10.array.Region filter,
                                                                                                final x10.core.fun.Fun_0_1 op){
            
//#line 708 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33402 =
              ((x10.array.Region)(region));
            
//#line 708 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region fregion =
              ((x10.array.Region)(t33402.$and(((x10.array.Region)(filter)))));
            
//#line 709 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.Iterator<x10.array.Point> p33961 =
              fregion.iterator();
            
//#line 709 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
for (;
                                                                                                       true;
                                                                                                       ) {
                
//#line 709 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33962 =
                  ((x10.lang.Iterator<x10.array.Point>)p33961).hasNext$O();
                
//#line 709 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (!(t33962)) {
                    
//#line 709 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
break;
                }
                
//#line 709 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Point p33958 =
                  ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)p33961).next$G()));
                
//#line 710 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $T t33959 =
                  (($T)(this.$apply$G(((x10.array.Point)(p33958)))));
                
//#line 710 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $U t33960 =
                  (($U)((($U)
                          ((x10.core.fun.Fun_0_1<$T,$U>)op).$apply(t33959,$T))));
                
//#line 710 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
((x10.array.Array<$U>)dst).$set__1x10$array$Array$$T$G(((x10.array.Point)(p33958)),
                                                                                                                                                             (($U)(t33960)));
            }
            
//#line 712 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return dst;
        }
        
        
//#line 728 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public <$S, $U>x10.array.Array<$S>
                                                                                                map__0$1x10$array$Array$$U$2__1$1x10$array$Array$$T$3x10$array$Array$$U$3x10$array$Array$$S$2(
                                                                                                final x10.rtt.Type $S,
                                                                                                final x10.rtt.Type $U,
                                                                                                final x10.array.Array src,
                                                                                                final x10.core.fun.Fun_0_2 op){
            
//#line 729 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33410 =
              ((x10.array.Region)(region));
            
//#line 729 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.fun.Fun_0_1<x10.array.Point,$S> t33411 =
              ((x10.core.fun.Fun_0_1)(new x10.array.Array.$Closure$1<$T, $S, $U>($T, $S, $U, ((x10.array.Array)(this)),
                                                                                 src,
                                                                                 op, (x10.array.Array.$Closure$1.__0$1x10$array$Array$$Closure$1$$T$2__1$1x10$array$Array$$Closure$1$$U$2__2$1x10$array$Array$$Closure$1$$T$3x10$array$Array$$Closure$1$$U$3x10$array$Array$$Closure$1$$S$2) null)));
            
//#line 729 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<$S> t33412 =
              ((x10.array.Array)(new x10.array.Array<$S>((java.lang.System[]) null, $S).$init(((x10.array.Region)(t33410)),
                                                                                              ((x10.core.fun.Fun_0_1)(t33411)), (x10.array.Array.__1$1x10$array$Point$3x10$array$Array$$T$2) null)));
            
//#line 729 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33412;
        }
        
        
//#line 746 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public <$S, $U>x10.array.Array<$S>
                                                                                                map__0$1x10$array$Array$$S$2__1$1x10$array$Array$$U$2__2$1x10$array$Array$$T$3x10$array$Array$$U$3x10$array$Array$$S$2(
                                                                                                final x10.rtt.Type $S,
                                                                                                final x10.rtt.Type $U,
                                                                                                final x10.array.Array dst,
                                                                                                final x10.array.Array src,
                                                                                                final x10.core.fun.Fun_0_2 op){
            
//#line 748 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33432 =
              rect;
            
//#line 748 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33432) {
                
//#line 752 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int i33025min33026 =
                  0;
                
//#line 752 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33413 =
                  ((x10.core.IndexedMemoryChunk)(raw));
                
//#line 752 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33414 =
                  ((((x10.core.IndexedMemoryChunk<$T>)(t33413))).length);
                
//#line 752 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int i33025max33027 =
                  ((t33414) - (((int)(1))));
                
//#line 752 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
int i33972 =
                  i33025min33026;
                
//#line 752 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
for (;
                                                                                                           true;
                                                                                                           ) {
                    
//#line 752 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33973 =
                      i33972;
                    
//#line 752 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33974 =
                      ((t33973) <= (((int)(i33025max33027))));
                    
//#line 752 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (!(t33974)) {
                        
//#line 752 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
break;
                    }
                    
//#line 752 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int i33969 =
                      i33972;
                    
//#line 753 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$S> t33963 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<$S>)dst).
                                                       raw));
                    
//#line 753 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33964 =
                      ((x10.core.IndexedMemoryChunk)(raw));
                    
//#line 753 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $T t33965 =
                      (($T)((((x10.core.IndexedMemoryChunk<$T>)(t33964))).$apply$G(((int)(i33969)))));
                    
//#line 753 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$U> t33966 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<$U>)src).
                                                       raw));
                    
//#line 753 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $U t33967 =
                      (($U)((((x10.core.IndexedMemoryChunk<$U>)(t33966))).$apply$G(((int)(i33969)))));
                    
//#line 753 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $S t33968 =
                      (($S)((($S)
                              ((x10.core.fun.Fun_0_2<$T,$U,$S>)op).$apply(t33965,$T,
                                                                          t33967,$U))));
                    
//#line 753 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
(((x10.core.IndexedMemoryChunk<$S>)(t33963))).$set(((int)(i33969)), t33968);
                    
//#line 752 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33970 =
                      i33972;
                    
//#line 752 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33971 =
                      ((t33970) + (((int)(1))));
                    
//#line 752 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
i33972 = t33971;
                }
            } else {
                
//#line 756 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33427 =
                  ((x10.array.Region)(region));
                
//#line 756 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.Iterator<x10.array.Point> p33041 =
                  t33427.iterator();
                
//#line 756 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
for (;
                                                                                                           true;
                                                                                                           ) {
                    
//#line 756 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33431 =
                      ((x10.lang.Iterator<x10.array.Point>)p33041).hasNext$O();
                    
//#line 756 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (!(t33431)) {
                        
//#line 756 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
break;
                    }
                    
//#line 756 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Point p33975 =
                      ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)p33041).next$G()));
                    
//#line 757 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $T t33976 =
                      (($T)(this.$apply$G(((x10.array.Point)(p33975)))));
                    
//#line 757 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $U t33977 =
                      (($U)(((x10.array.Array<$U>)src).$apply$G(((x10.array.Point)(p33975)))));
                    
//#line 757 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $S t33978 =
                      (($S)((($S)
                              ((x10.core.fun.Fun_0_2<$T,$U,$S>)op).$apply(t33976,$T,
                                                                          t33977,$U))));
                    
//#line 757 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
((x10.array.Array<$S>)dst).$set__1x10$array$Array$$T$G(((x10.array.Point)(p33975)),
                                                                                                                                                                 (($S)(t33978)));
                }
            }
            
//#line 760 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return dst;
        }
        
        
//#line 779 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public <$S, $U>x10.array.Array<$S>
                                                                                                map__0$1x10$array$Array$$S$2__1$1x10$array$Array$$U$2__3$1x10$array$Array$$T$3x10$array$Array$$U$3x10$array$Array$$S$2(
                                                                                                final x10.rtt.Type $S,
                                                                                                final x10.rtt.Type $U,
                                                                                                final x10.array.Array dst,
                                                                                                final x10.array.Array src,
                                                                                                final x10.array.Region filter,
                                                                                                final x10.core.fun.Fun_0_2 op){
            
//#line 780 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33433 =
              ((x10.array.Region)(region));
            
//#line 780 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region fregion =
              ((x10.array.Region)(t33433.$and(((x10.array.Region)(filter)))));
            
//#line 781 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.Iterator<x10.array.Point> p33983 =
              fregion.iterator();
            
//#line 781 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
for (;
                                                                                                       true;
                                                                                                       ) {
                
//#line 781 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33984 =
                  ((x10.lang.Iterator<x10.array.Point>)p33983).hasNext$O();
                
//#line 781 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (!(t33984)) {
                    
//#line 781 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
break;
                }
                
//#line 781 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Point p33979 =
                  ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)p33983).next$G()));
                
//#line 782 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $T t33980 =
                  (($T)(this.$apply$G(((x10.array.Point)(p33979)))));
                
//#line 782 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $U t33981 =
                  (($U)(((x10.array.Array<$U>)src).$apply$G(((x10.array.Point)(p33979)))));
                
//#line 782 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $S t33982 =
                  (($S)((($S)
                          ((x10.core.fun.Fun_0_2<$T,$U,$S>)op).$apply(t33980,$T,
                                                                      t33981,$U))));
                
//#line 782 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
((x10.array.Array<$S>)dst).$set__1x10$array$Array$$T$G(((x10.array.Point)(p33979)),
                                                                                                                                                             (($S)(t33982)));
            }
            
//#line 784 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return dst;
        }
        
        
//#line 800 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public <$U>$U
                                                                                                reduce__0$1x10$array$Array$$U$3x10$array$Array$$T$3x10$array$Array$$U$2__1x10$array$Array$$U$G(
                                                                                                final x10.rtt.Type $U,
                                                                                                final x10.core.fun.Fun_0_2 op,
                                                                                                final $U unit){
            
//#line 803 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
$U accum =
              (($U)(unit));
            
//#line 804 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33456 =
              rect;
            
//#line 804 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33456) {
                
//#line 808 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int i33045min33046 =
                  0;
                
//#line 808 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33439 =
                  ((x10.core.IndexedMemoryChunk)(raw));
                
//#line 808 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33440 =
                  ((((x10.core.IndexedMemoryChunk<$T>)(t33439))).length);
                
//#line 808 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int i33045max33047 =
                  ((t33440) - (((int)(1))));
                
//#line 808 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
int i33992 =
                  i33045min33046;
                
//#line 808 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
for (;
                                                                                                           true;
                                                                                                           ) {
                    
//#line 808 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33993 =
                      i33992;
                    
//#line 808 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33994 =
                      ((t33993) <= (((int)(i33045max33047))));
                    
//#line 808 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (!(t33994)) {
                        
//#line 808 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
break;
                    }
                    
//#line 808 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int i33989 =
                      i33992;
                    
//#line 809 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $U t33985 =
                      (($U)(accum));
                    
//#line 809 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33986 =
                      ((x10.core.IndexedMemoryChunk)(raw));
                    
//#line 809 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $T t33987 =
                      (($T)((((x10.core.IndexedMemoryChunk<$T>)(t33986))).$apply$G(((int)(i33989)))));
                    
//#line 809 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $U t33988 =
                      (($U)((($U)
                              ((x10.core.fun.Fun_0_2<$U,$T,$U>)op).$apply(t33985,$U,
                                                                          t33987,$T))));
                    
//#line 809 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
accum = (($U)(t33988));
                    
//#line 808 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33990 =
                      i33992;
                    
//#line 808 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33991 =
                      ((t33990) + (((int)(1))));
                    
//#line 808 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
i33992 = t33991;
                }
            } else {
                
//#line 812 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33451 =
                  ((x10.array.Region)(region));
                
//#line 812 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.Iterator<x10.array.Point> p33061 =
                  t33451.iterator();
                
//#line 812 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
for (;
                                                                                                           true;
                                                                                                           ) {
                    
//#line 812 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33455 =
                      ((x10.lang.Iterator<x10.array.Point>)p33061).hasNext$O();
                    
//#line 812 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (!(t33455)) {
                        
//#line 812 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
break;
                    }
                    
//#line 812 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Point p33995 =
                      ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)p33061).next$G()));
                    
//#line 813 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $U t33996 =
                      (($U)(accum));
                    
//#line 813 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $T t33997 =
                      (($T)(this.$apply$G(((x10.array.Point)(p33995)))));
                    
//#line 813 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $U t33998 =
                      (($U)((($U)
                              ((x10.core.fun.Fun_0_2<$U,$T,$U>)op).$apply(t33996,$U,
                                                                          t33997,$T))));
                    
//#line 813 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
accum = (($U)(t33998));
                }
            }
            
//#line 816 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $U t33457 =
              (($U)(accum));
            
//#line 816 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33457;
        }
        
        
//#line 833 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public <$U>x10.array.Array<$U>
                                                                                                scan__0$1x10$array$Array$$U$3x10$array$Array$$T$3x10$array$Array$$U$2__1x10$array$Array$$U(
                                                                                                final x10.rtt.Type $U,
                                                                                                final x10.core.fun.Fun_0_2 op,
                                                                                                final $U unit){
            
//#line 834 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33458 =
              ((x10.array.Region)(region));
            
//#line 834 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<$U> t33459 =
              ((x10.array.Array)(new x10.array.Array<$U>((java.lang.System[]) null, $U).$init(((x10.array.Region)(t33458)))));
            
//#line 834 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<$U> t33460 =
              ((x10.array.Array)(this.<$U>scan__0$1x10$array$Array$$U$2__1$1x10$array$Array$$U$3x10$array$Array$$T$3x10$array$Array$$U$2__2x10$array$Array$$U($U, ((x10.array.Array)(t33459)),
                                                                                                                                                              ((x10.core.fun.Fun_0_2)(op)),
                                                                                                                                                              (($U)(unit)))));
            
//#line 834 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33460;
        }
        
        
//#line 851 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public <$U>x10.array.Array<$U>
                                                                                                scan__0$1x10$array$Array$$U$2__1$1x10$array$Array$$U$3x10$array$Array$$T$3x10$array$Array$$U$2__2x10$array$Array$$U(
                                                                                                final x10.rtt.Type $U,
                                                                                                final x10.array.Array dst,
                                                                                                final x10.core.fun.Fun_0_2 op,
                                                                                                final $U unit){
            
//#line 852 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
$U accum =
              (($U)(unit));
            
//#line 853 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t34004 =
              ((x10.array.Region)(region));
            
//#line 853 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.Iterator<x10.array.Point> p34005 =
              t34004.iterator();
            
//#line 853 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
for (;
                                                                                                       true;
                                                                                                       ) {
                
//#line 853 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t34006 =
                  ((x10.lang.Iterator<x10.array.Point>)p34005).hasNext$O();
                
//#line 853 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (!(t34006)) {
                    
//#line 853 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
break;
                }
                
//#line 853 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Point p33999 =
                  ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)p34005).next$G()));
                
//#line 854 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $U t34000 =
                  (($U)(accum));
                
//#line 854 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $T t34001 =
                  (($T)(this.$apply$G(((x10.array.Point)(p33999)))));
                
//#line 854 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $U t34002 =
                  (($U)((($U)
                          ((x10.core.fun.Fun_0_2<$U,$T,$U>)op).$apply(t34000,$U,
                                                                      t34001,$T))));
                
//#line 854 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
accum = (($U)(t34002));
                
//#line 855 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $U t34003 =
                  (($U)(accum));
                
//#line 855 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
((x10.array.Array<$U>)dst).$set__1x10$array$Array$$T$G(((x10.array.Point)(p33999)),
                                                                                                                                                             (($U)(t34003)));
            }
            
//#line 857 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return dst;
        }
        
        
//#line 882 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public static <$T>void
                                                                                                asyncCopy__0$1x10$array$Array$$T$2__1$1x10$array$Array$$T$2(
                                                                                                final x10.rtt.Type $T,
                                                                                                final x10.array.Array<$T> src,
                                                                                                final x10.array.RemoteArray<$T> dst){
            
//#line 883 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33468 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<$T>)src).
                                               raw));
            
//#line 883 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33470 =
              ((((x10.core.IndexedMemoryChunk<$T>)(t33468))).length);
            
//#line 883 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.RemoteIndexedMemoryChunk<$T> t33469 =
              ((x10.core.RemoteIndexedMemoryChunk)(((x10.array.RemoteArray<$T>)dst).
                                                     rawData));
            
//#line 883 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33471 =
              ((((x10.core.RemoteIndexedMemoryChunk<$T>)(t33469))).length);
            
//#line 883 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33473 =
              ((int) t33470) !=
            ((int) t33471);
            
//#line 883 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33473) {
                
//#line 883 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.IllegalArgumentException t33472 =
                  ((x10.lang.IllegalArgumentException)(new x10.lang.IllegalArgumentException(((java.lang.String)("source and destination do not have equal size")))));
                
//#line 883 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
throw t33472;
            }
            
//#line 884 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33475 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<$T>)src).
                                               raw));
            
//#line 884 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.RemoteIndexedMemoryChunk<$T> t33476 =
              ((x10.core.RemoteIndexedMemoryChunk)(((x10.array.RemoteArray<$T>)dst).
                                                     rawData));
            
//#line 884 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33474 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<$T>)src).
                                               raw));
            
//#line 884 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33477 =
              ((((x10.core.IndexedMemoryChunk<$T>)(t33474))).length);
            
//#line 884 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
x10.core.IndexedMemoryChunk.<$T>asyncCopy(t33475,((int)(0)),t33476,((int)(0)),((int)(t33477)));
        }
        
        
//#line 914 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public static <$T>void
                                                                                                asyncCopy__0$1x10$array$Array$$T$2__2$1x10$array$Array$$T$2(
                                                                                                final x10.rtt.Type $T,
                                                                                                final x10.array.Array<$T> src,
                                                                                                final x10.array.Point srcPoint,
                                                                                                final x10.array.RemoteArray<$T> dst,
                                                                                                final x10.array.Point dstPoint,
                                                                                                final int numElems){
            
//#line 917 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.GlobalRef<x10.array.Array<$T>> gra =
              ((x10.core.GlobalRef)(((x10.array.RemoteArray<$T>)dst).
                                      array));
            
//#line 918 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.Place t31347 =
              ((x10.lang.Place)((gra).home));
            
//#line 918 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int dstIndex =
              x10.core.Int.$unbox(x10.lang.Runtime.<x10.core.Int>evalAt__1$1x10$lang$Runtime$$T$2$G(x10.rtt.Types.INT, ((x10.lang.Place)(t31347)),
                                                                                                    ((x10.core.fun.Fun_0_0)(new x10.array.Array.$Closure$2<$T>($T, gra,
                                                                                                                                                               dstPoint, (x10.array.Array.$Closure$2.__0$1x10$array$Array$1x10$array$Array$$Closure$2$$T$2$2) null)))));
            
//#line 919 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33478 =
              ((x10.array.Region)(((x10.array.Array<$T>)src).
                                    region));
            
//#line 919 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33479 =
              t33478.indexOf$O(((x10.array.Point)(srcPoint)));
            
//#line 919 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
x10.array.Array.<$T>asyncCopy__0$1x10$array$Array$$T$2__2$1x10$array$Array$$T$2($T, ((x10.array.Array)(src)),
                                                                                                                                                                                  (int)(t33479),
                                                                                                                                                                                  ((x10.array.RemoteArray)(dst)),
                                                                                                                                                                                  (int)(dstIndex),
                                                                                                                                                                                  (int)(numElems));
        }
        
        
//#line 958 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public static <$T>void
                                                                                                asyncCopy__0$1x10$array$Array$$T$2__2$1x10$array$Array$$T$2(
                                                                                                final x10.rtt.Type $T,
                                                                                                final x10.array.Array<$T> src,
                                                                                                final int srcIndex,
                                                                                                final x10.array.RemoteArray<$T> dst,
                                                                                                final int dstIndex,
                                                                                                final int numElems){
            
//#line 961 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
boolean t33483 =
              ((srcIndex) < (((int)(0))));
            
//#line 961 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (!(t33483)) {
                
//#line 961 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33481 =
                  ((srcIndex) + (((int)(numElems))));
                
//#line 961 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33480 =
                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<$T>)src).
                                                   raw));
                
//#line 961 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33482 =
                  ((((x10.core.IndexedMemoryChunk<$T>)(t33480))).length);
                
//#line 961 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
t33483 = ((t33481) > (((int)(t33482))));
            }
            
//#line 961 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33485 =
              t33483;
            
//#line 961 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33485) {
                
//#line 962 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.IllegalArgumentException t33484 =
                  ((x10.lang.IllegalArgumentException)(new x10.lang.IllegalArgumentException(((java.lang.String)("Specified range is beyond bounds of source array")))));
                
//#line 962 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
throw t33484;
            }
            
//#line 964 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
boolean t33489 =
              ((dstIndex) < (((int)(0))));
            
//#line 964 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (!(t33489)) {
                
//#line 964 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33487 =
                  ((dstIndex) + (((int)(numElems))));
                
//#line 964 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.RemoteIndexedMemoryChunk<$T> t33486 =
                  ((x10.core.RemoteIndexedMemoryChunk)(((x10.array.RemoteArray<$T>)dst).
                                                         rawData));
                
//#line 964 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33488 =
                  ((((x10.core.RemoteIndexedMemoryChunk<$T>)(t33486))).length);
                
//#line 964 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
t33489 = ((t33487) > (((int)(t33488))));
            }
            
//#line 964 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33491 =
              t33489;
            
//#line 964 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33491) {
                
//#line 965 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.IllegalArgumentException t33490 =
                  ((x10.lang.IllegalArgumentException)(new x10.lang.IllegalArgumentException(((java.lang.String)("Specified range is beyond bounds of destination array")))));
                
//#line 965 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
throw t33490;
            }
            
//#line 967 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33492 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<$T>)src).
                                               raw));
            
//#line 967 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.RemoteIndexedMemoryChunk<$T> t33493 =
              ((x10.core.RemoteIndexedMemoryChunk)(((x10.array.RemoteArray<$T>)dst).
                                                     rawData));
            
//#line 967 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
x10.core.IndexedMemoryChunk.<$T>asyncCopy(t33492,((int)(srcIndex)),t33493,((int)(dstIndex)),((int)(numElems)));
        }
        
        
//#line 992 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public static <$T>void
                                                                                                asyncCopy__0$1x10$array$Array$$T$2__1$1x10$array$Array$$T$2(
                                                                                                final x10.rtt.Type $T,
                                                                                                final x10.array.RemoteArray<$T> src,
                                                                                                final x10.array.Array<$T> dst){
            
//#line 993 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.RemoteIndexedMemoryChunk<$T> t33494 =
              ((x10.core.RemoteIndexedMemoryChunk)(((x10.array.RemoteArray<$T>)src).
                                                     rawData));
            
//#line 993 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33496 =
              ((((x10.core.RemoteIndexedMemoryChunk<$T>)(t33494))).length);
            
//#line 993 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33495 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<$T>)dst).
                                               raw));
            
//#line 993 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33497 =
              ((((x10.core.IndexedMemoryChunk<$T>)(t33495))).length);
            
//#line 993 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33499 =
              ((int) t33496) !=
            ((int) t33497);
            
//#line 993 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33499) {
                
//#line 993 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.IllegalArgumentException t33498 =
                  ((x10.lang.IllegalArgumentException)(new x10.lang.IllegalArgumentException(((java.lang.String)("source and destination do not have equal size")))));
                
//#line 993 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
throw t33498;
            }
            
//#line 994 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.RemoteIndexedMemoryChunk<$T> t33501 =
              ((x10.core.RemoteIndexedMemoryChunk)(((x10.array.RemoteArray<$T>)src).
                                                     rawData));
            
//#line 994 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33502 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<$T>)dst).
                                               raw));
            
//#line 994 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33500 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<$T>)dst).
                                               raw));
            
//#line 994 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33503 =
              ((((x10.core.IndexedMemoryChunk<$T>)(t33500))).length);
            
//#line 994 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
x10.core.IndexedMemoryChunk.<$T>asyncCopy(t33501,((int)(0)),t33502,((int)(0)),((int)(t33503)));
        }
        
        
//#line 1024 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public static <$T>void
                                                                                                 asyncCopy__0$1x10$array$Array$$T$2__2$1x10$array$Array$$T$2(
                                                                                                 final x10.rtt.Type $T,
                                                                                                 final x10.array.RemoteArray<$T> src,
                                                                                                 final x10.array.Point srcPoint,
                                                                                                 final x10.array.Array<$T> dst,
                                                                                                 final x10.array.Point dstPoint,
                                                                                                 final int numElems){
            
//#line 1027 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.GlobalRef<x10.array.Array<$T>> gra =
              ((x10.core.GlobalRef)(((x10.array.RemoteArray<$T>)src).
                                      array));
            
//#line 1028 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.Place t31351 =
              ((x10.lang.Place)((gra).home));
            
//#line 1028 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int srcIndex =
              x10.core.Int.$unbox(x10.lang.Runtime.<x10.core.Int>evalAt__1$1x10$lang$Runtime$$T$2$G(x10.rtt.Types.INT, ((x10.lang.Place)(t31351)),
                                                                                                    ((x10.core.fun.Fun_0_0)(new x10.array.Array.$Closure$3<$T>($T, gra,
                                                                                                                                                               srcPoint, (x10.array.Array.$Closure$3.__0$1x10$array$Array$1x10$array$Array$$Closure$3$$T$2$2) null)))));
            
//#line 1029 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33504 =
              ((x10.array.Region)(((x10.array.Array<$T>)dst).
                                    region));
            
//#line 1029 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33505 =
              t33504.indexOf$O(((x10.array.Point)(dstPoint)));
            
//#line 1029 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
x10.array.Array.<$T>asyncCopy__0$1x10$array$Array$$T$2__2$1x10$array$Array$$T$2($T, ((x10.array.RemoteArray)(src)),
                                                                                                                                                                                   (int)(srcIndex),
                                                                                                                                                                                   ((x10.array.Array)(dst)),
                                                                                                                                                                                   (int)(t33505),
                                                                                                                                                                                   (int)(numElems));
        }
        
        
//#line 1068 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public static <$T>void
                                                                                                 asyncCopy__0$1x10$array$Array$$T$2__2$1x10$array$Array$$T$2(
                                                                                                 final x10.rtt.Type $T,
                                                                                                 final x10.array.RemoteArray<$T> src,
                                                                                                 final int srcIndex,
                                                                                                 final x10.array.Array<$T> dst,
                                                                                                 final int dstIndex,
                                                                                                 final int numElems){
            
//#line 1071 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
boolean t33509 =
              ((srcIndex) < (((int)(0))));
            
//#line 1071 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (!(t33509)) {
                
//#line 1071 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33507 =
                  ((srcIndex) + (((int)(numElems))));
                
//#line 1071 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.RemoteIndexedMemoryChunk<$T> t33506 =
                  ((x10.core.RemoteIndexedMemoryChunk)(((x10.array.RemoteArray<$T>)src).
                                                         rawData));
                
//#line 1071 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33508 =
                  ((((x10.core.RemoteIndexedMemoryChunk<$T>)(t33506))).length);
                
//#line 1071 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
t33509 = ((t33507) > (((int)(t33508))));
            }
            
//#line 1071 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33511 =
              t33509;
            
//#line 1071 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33511) {
                
//#line 1072 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.IllegalArgumentException t33510 =
                  ((x10.lang.IllegalArgumentException)(new x10.lang.IllegalArgumentException(((java.lang.String)("Specified range is beyond bounds of source array")))));
                
//#line 1072 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
throw t33510;
            }
            
//#line 1074 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
boolean t33515 =
              ((dstIndex) < (((int)(0))));
            
//#line 1074 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (!(t33515)) {
                
//#line 1074 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33513 =
                  ((dstIndex) + (((int)(numElems))));
                
//#line 1074 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33512 =
                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<$T>)dst).
                                                   raw));
                
//#line 1074 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33514 =
                  ((((x10.core.IndexedMemoryChunk<$T>)(t33512))).length);
                
//#line 1074 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
t33515 = ((t33513) > (((int)(t33514))));
            }
            
//#line 1074 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33517 =
              t33515;
            
//#line 1074 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33517) {
                
//#line 1075 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.IllegalArgumentException t33516 =
                  ((x10.lang.IllegalArgumentException)(new x10.lang.IllegalArgumentException(((java.lang.String)("Specified range is beyond bounds of destination array")))));
                
//#line 1075 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
throw t33516;
            }
            
//#line 1077 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.RemoteIndexedMemoryChunk<$T> t33518 =
              ((x10.core.RemoteIndexedMemoryChunk)(((x10.array.RemoteArray<$T>)src).
                                                     rawData));
            
//#line 1077 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33519 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<$T>)dst).
                                               raw));
            
//#line 1077 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
x10.core.IndexedMemoryChunk.<$T>asyncCopy(t33518,((int)(srcIndex)),t33519,((int)(dstIndex)),((int)(numElems)));
        }
        
        
//#line 1092 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public static <$T>void
                                                                                                 copy__0$1x10$array$Array$$T$2__1$1x10$array$Array$$T$2(
                                                                                                 final x10.rtt.Type $T,
                                                                                                 final x10.array.Array<$T> src,
                                                                                                 final x10.array.Array<$T> dst){
            
//#line 1093 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33520 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<$T>)src).
                                               raw));
            
//#line 1093 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33522 =
              ((((x10.core.IndexedMemoryChunk<$T>)(t33520))).length);
            
//#line 1093 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33521 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<$T>)dst).
                                               raw));
            
//#line 1093 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33523 =
              ((((x10.core.IndexedMemoryChunk<$T>)(t33521))).length);
            
//#line 1093 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33525 =
              ((int) t33522) !=
            ((int) t33523);
            
//#line 1093 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33525) {
                
//#line 1093 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.IllegalArgumentException t33524 =
                  ((x10.lang.IllegalArgumentException)(new x10.lang.IllegalArgumentException(((java.lang.String)("source and destination do not have equal size")))));
                
//#line 1093 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
throw t33524;
            }
            
//#line 1094 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33527 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<$T>)src).
                                               raw));
            
//#line 1094 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33528 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<$T>)dst).
                                               raw));
            
//#line 1094 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33526 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<$T>)src).
                                               raw));
            
//#line 1094 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33529 =
              ((((x10.core.IndexedMemoryChunk<$T>)(t33526))).length);
            
//#line 1094 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
x10.core.IndexedMemoryChunk.<$T>copy(t33527,((int)(0)),t33528,((int)(0)),((int)(t33529)));
        }
        
        
//#line 1115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public static <$T>void
                                                                                                 copy__0$1x10$array$Array$$T$2__2$1x10$array$Array$$T$2(
                                                                                                 final x10.rtt.Type $T,
                                                                                                 final x10.array.Array<$T> src,
                                                                                                 final x10.array.Point srcPoint,
                                                                                                 final x10.array.Array<$T> dst,
                                                                                                 final x10.array.Point dstPoint,
                                                                                                 final int numElems){
            
//#line 1118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33530 =
              ((x10.array.Region)(((x10.array.Array<$T>)src).
                                    region));
            
//#line 1118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33532 =
              t33530.indexOf$O(((x10.array.Point)(srcPoint)));
            
//#line 1118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t33531 =
              ((x10.array.Region)(((x10.array.Array<$T>)dst).
                                    region));
            
//#line 1118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33533 =
              t33531.indexOf$O(((x10.array.Point)(dstPoint)));
            
//#line 1118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
x10.array.Array.<$T>copy__0$1x10$array$Array$$T$2__2$1x10$array$Array$$T$2($T, ((x10.array.Array)(src)),
                                                                                                                                                                              (int)(t33532),
                                                                                                                                                                              ((x10.array.Array)(dst)),
                                                                                                                                                                              (int)(t33533),
                                                                                                                                                                              (int)(numElems));
        }
        
        
//#line 1148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public static <$T>void
                                                                                                 copy__0$1x10$array$Array$$T$2__2$1x10$array$Array$$T$2(
                                                                                                 final x10.rtt.Type $T,
                                                                                                 final x10.array.Array<$T> src,
                                                                                                 final int srcIndex,
                                                                                                 final x10.array.Array<$T> dst,
                                                                                                 final int dstIndex,
                                                                                                 final int numElems){
            
//#line 1151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
boolean t33537 =
              ((srcIndex) < (((int)(0))));
            
//#line 1151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (!(t33537)) {
                
//#line 1151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33535 =
                  ((srcIndex) + (((int)(numElems))));
                
//#line 1151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33534 =
                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<$T>)src).
                                                   raw));
                
//#line 1151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33536 =
                  ((((x10.core.IndexedMemoryChunk<$T>)(t33534))).length);
                
//#line 1151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
t33537 = ((t33535) > (((int)(t33536))));
            }
            
//#line 1151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33539 =
              t33537;
            
//#line 1151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33539) {
                
//#line 1152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.IllegalArgumentException t33538 =
                  ((x10.lang.IllegalArgumentException)(new x10.lang.IllegalArgumentException(((java.lang.String)("Specified range is beyond bounds of source array")))));
                
//#line 1152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
throw t33538;
            }
            
//#line 1154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
boolean t33543 =
              ((dstIndex) < (((int)(0))));
            
//#line 1154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (!(t33543)) {
                
//#line 1154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33541 =
                  ((dstIndex) + (((int)(numElems))));
                
//#line 1154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33540 =
                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<$T>)dst).
                                                   raw));
                
//#line 1154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33542 =
                  ((((x10.core.IndexedMemoryChunk<$T>)(t33540))).length);
                
//#line 1154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
t33543 = ((t33541) > (((int)(t33542))));
            }
            
//#line 1154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33545 =
              t33543;
            
//#line 1154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33545) {
                
//#line 1155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.IllegalArgumentException t33544 =
                  ((x10.lang.IllegalArgumentException)(new x10.lang.IllegalArgumentException(((java.lang.String)("Specified range is beyond bounds of destination array")))));
                
//#line 1155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
throw t33544;
            }
            
//#line 1157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33546 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<$T>)src).
                                               raw));
            
//#line 1157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T> t33547 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<$T>)dst).
                                               raw));
            
//#line 1157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
x10.core.IndexedMemoryChunk.<$T>copy(t33546,((int)(srcIndex)),t33547,((int)(dstIndex)),((int)(numElems)));
        }
        
        
//#line 1161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
private static void
                                                                                                 raiseBoundsError(
                                                                                                 final int i0){
            
//#line 1162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33548 =
              (("point (") + ((x10.core.Int.$box(i0))));
            
//#line 1162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33549 =
              ((t33548) + (") not contained in array"));
            
//#line 1162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.ArrayIndexOutOfBoundsException t33550 =
              ((x10.lang.ArrayIndexOutOfBoundsException)(new x10.lang.ArrayIndexOutOfBoundsException(t33549)));
            
//#line 1162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
throw t33550;
        }
        
        public static void
          raiseBoundsError$P(
          final int i0){
            x10.array.Array.raiseBoundsError((int)(i0));
        }
        
        
//#line 1164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
private static void
                                                                                                 raiseBoundsError(
                                                                                                 final int i0,
                                                                                                 final int i1){
            
//#line 1165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33551 =
              (("point (") + ((x10.core.Int.$box(i0))));
            
//#line 1165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33552 =
              ((t33551) + (", "));
            
//#line 1165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33553 =
              ((t33552) + ((x10.core.Int.$box(i1))));
            
//#line 1165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33554 =
              ((t33553) + (") not contained in array"));
            
//#line 1165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.ArrayIndexOutOfBoundsException t33555 =
              ((x10.lang.ArrayIndexOutOfBoundsException)(new x10.lang.ArrayIndexOutOfBoundsException(t33554)));
            
//#line 1165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
throw t33555;
        }
        
        public static void
          raiseBoundsError$P(
          final int i0,
          final int i1){
            x10.array.Array.raiseBoundsError((int)(i0),
                                             (int)(i1));
        }
        
        
//#line 1167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
private static void
                                                                                                 raiseBoundsError(
                                                                                                 final int i0,
                                                                                                 final int i1,
                                                                                                 final int i2){
            
//#line 1168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33556 =
              (("point (") + ((x10.core.Int.$box(i0))));
            
//#line 1168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33557 =
              ((t33556) + (", "));
            
//#line 1168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33558 =
              ((t33557) + ((x10.core.Int.$box(i1))));
            
//#line 1168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33559 =
              ((t33558) + (", "));
            
//#line 1168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33560 =
              ((t33559) + ((x10.core.Int.$box(i2))));
            
//#line 1168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33561 =
              ((t33560) + (") not contained in array"));
            
//#line 1168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.ArrayIndexOutOfBoundsException t33562 =
              ((x10.lang.ArrayIndexOutOfBoundsException)(new x10.lang.ArrayIndexOutOfBoundsException(t33561)));
            
//#line 1168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
throw t33562;
        }
        
        public static void
          raiseBoundsError$P(
          final int i0,
          final int i1,
          final int i2){
            x10.array.Array.raiseBoundsError((int)(i0),
                                             (int)(i1),
                                             (int)(i2));
        }
        
        
//#line 1170 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
private static void
                                                                                                 raiseBoundsError(
                                                                                                 final int i0,
                                                                                                 final int i1,
                                                                                                 final int i2,
                                                                                                 final int i3){
            
//#line 1171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33563 =
              (("point (") + ((x10.core.Int.$box(i0))));
            
//#line 1171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33564 =
              ((t33563) + (", "));
            
//#line 1171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33565 =
              ((t33564) + ((x10.core.Int.$box(i1))));
            
//#line 1171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33566 =
              ((t33565) + (", "));
            
//#line 1171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33567 =
              ((t33566) + ((x10.core.Int.$box(i2))));
            
//#line 1171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33568 =
              ((t33567) + (", "));
            
//#line 1171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33569 =
              ((t33568) + ((x10.core.Int.$box(i3))));
            
//#line 1171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33570 =
              ((t33569) + (") not contained in array"));
            
//#line 1171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.ArrayIndexOutOfBoundsException t33571 =
              ((x10.lang.ArrayIndexOutOfBoundsException)(new x10.lang.ArrayIndexOutOfBoundsException(t33570)));
            
//#line 1171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
throw t33571;
        }
        
        public static void
          raiseBoundsError$P(
          final int i0,
          final int i1,
          final int i2,
          final int i3){
            x10.array.Array.raiseBoundsError((int)(i0),
                                             (int)(i1),
                                             (int)(i2),
                                             (int)(i3));
        }
        
        
//#line 1173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
private static void
                                                                                                 raiseBoundsError(
                                                                                                 final x10.array.Point pt){
            
//#line 1174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33572 =
              (("point ") + (pt));
            
//#line 1174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33573 =
              ((t33572) + (" not contained in array"));
            
//#line 1174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.ArrayIndexOutOfBoundsException t33574 =
              ((x10.lang.ArrayIndexOutOfBoundsException)(new x10.lang.ArrayIndexOutOfBoundsException(t33573)));
            
//#line 1174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
throw t33574;
        }
        
        public static void
          raiseBoundsError$P(
          final x10.array.Point pt){
            x10.array.Array.raiseBoundsError(((x10.array.Point)(pt)));
        }
        
        
//#line 1184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public int layout_min0;
        
//#line 1185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public int layout_stride1;
        
//#line 1186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public int layout_min1;
        
//#line 1194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public x10.array.Array<x10.core.Int> layout;
        
        
//#line 1197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
private int
                                                                                                 offset$O(
                                                                                                 final int i0){
            
//#line 1197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33575 =
              layout_min0;
            
//#line 1197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33576 =
              ((i0) - (((int)(t33575))));
            
//#line 1197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33576;
        }
        
        public static <$T>int
          offset$P__1$1x10$array$Array$$T$2$O(
          final x10.rtt.Type $T,
          final int i0,
          final x10.array.Array<$T> Array){
            return ((x10.array.Array<$T>)Array).offset$O((int)(i0));
        }
        
        
//#line 1200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
private int
                                                                                                 offset$O(
                                                                                                 final int i0,
                                                                                                 final int i1){
            
//#line 1201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33577 =
              layout_min0;
            
//#line 1201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
int offset =
              ((i0) - (((int)(t33577))));
            
//#line 1202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33578 =
              offset;
            
//#line 1202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33579 =
              layout_stride1;
            
//#line 1202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33580 =
              ((t33578) * (((int)(t33579))));
            
//#line 1202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33581 =
              ((t33580) + (((int)(i1))));
            
//#line 1202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33582 =
              layout_min1;
            
//#line 1202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33583 =
              ((t33581) - (((int)(t33582))));
            
//#line 1202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
offset = t33583;
            
//#line 1203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33584 =
              offset;
            
//#line 1203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33584;
        }
        
        public static <$T>int
          offset$P__2$1x10$array$Array$$T$2$O(
          final x10.rtt.Type $T,
          final int i0,
          final int i1,
          final x10.array.Array<$T> Array){
            return ((x10.array.Array<$T>)Array).offset$O((int)(i0),
                                                         (int)(i1));
        }
        
        
//#line 1206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
private int
                                                                                                 offset$O(
                                                                                                 final int i0,
                                                                                                 final int i1,
                                                                                                 final int i2){
            
//#line 1207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33585 =
              layout_min0;
            
//#line 1207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
int offset =
              ((i0) - (((int)(t33585))));
            
//#line 1208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33586 =
              offset;
            
//#line 1208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33587 =
              layout_stride1;
            
//#line 1208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33588 =
              ((t33586) * (((int)(t33587))));
            
//#line 1208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33589 =
              ((t33588) + (((int)(i1))));
            
//#line 1208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33590 =
              layout_min1;
            
//#line 1208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33591 =
              ((t33589) - (((int)(t33590))));
            
//#line 1208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
offset = t33591;
            
//#line 1209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33593 =
              offset;
            
//#line 1209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t33592 =
              ((x10.array.Array)(layout));
            
//#line 1209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33594 =
              x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)t33592).$apply$G((int)(0)));
            
//#line 1209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33595 =
              ((t33593) * (((int)(t33594))));
            
//#line 1209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33597 =
              ((t33595) + (((int)(i2))));
            
//#line 1209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t33596 =
              ((x10.array.Array)(layout));
            
//#line 1209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33598 =
              x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)t33596).$apply$G((int)(1)));
            
//#line 1209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33599 =
              ((t33597) - (((int)(t33598))));
            
//#line 1209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
offset = t33599;
            
//#line 1210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33600 =
              offset;
            
//#line 1210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33600;
        }
        
        public static <$T>int
          offset$P__3$1x10$array$Array$$T$2$O(
          final x10.rtt.Type $T,
          final int i0,
          final int i1,
          final int i2,
          final x10.array.Array<$T> Array){
            return ((x10.array.Array<$T>)Array).offset$O((int)(i0),
                                                         (int)(i1),
                                                         (int)(i2));
        }
        
        
//#line 1213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
private int
                                                                                                 offset$O(
                                                                                                 final int i0,
                                                                                                 final int i1,
                                                                                                 final int i2,
                                                                                                 final int i3){
            
//#line 1214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33601 =
              layout_min0;
            
//#line 1214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
int offset =
              ((i0) - (((int)(t33601))));
            
//#line 1215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33602 =
              offset;
            
//#line 1215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33603 =
              layout_stride1;
            
//#line 1215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33604 =
              ((t33602) * (((int)(t33603))));
            
//#line 1215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33605 =
              ((t33604) + (((int)(i1))));
            
//#line 1215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33606 =
              layout_min1;
            
//#line 1215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33607 =
              ((t33605) - (((int)(t33606))));
            
//#line 1215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
offset = t33607;
            
//#line 1216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33609 =
              offset;
            
//#line 1216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t33608 =
              ((x10.array.Array)(layout));
            
//#line 1216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33610 =
              x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)t33608).$apply$G((int)(0)));
            
//#line 1216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33611 =
              ((t33609) * (((int)(t33610))));
            
//#line 1216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33613 =
              ((t33611) + (((int)(i2))));
            
//#line 1216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t33612 =
              ((x10.array.Array)(layout));
            
//#line 1216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33614 =
              x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)t33612).$apply$G((int)(1)));
            
//#line 1216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33615 =
              ((t33613) - (((int)(t33614))));
            
//#line 1216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
offset = t33615;
            
//#line 1217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33617 =
              offset;
            
//#line 1217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t33616 =
              ((x10.array.Array)(layout));
            
//#line 1217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33618 =
              x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)t33616).$apply$G((int)(2)));
            
//#line 1217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33619 =
              ((t33617) * (((int)(t33618))));
            
//#line 1217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33621 =
              ((t33619) + (((int)(i3))));
            
//#line 1217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t33620 =
              ((x10.array.Array)(layout));
            
//#line 1217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33622 =
              x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)t33620).$apply$G((int)(3)));
            
//#line 1217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33623 =
              ((t33621) - (((int)(t33622))));
            
//#line 1217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
offset = t33623;
            
//#line 1218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33624 =
              offset;
            
//#line 1218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33624;
        }
        
        public static <$T>int
          offset$P__4$1x10$array$Array$$T$2$O(
          final x10.rtt.Type $T,
          final int i0,
          final int i1,
          final int i2,
          final int i3,
          final x10.array.Array<$T> Array){
            return ((x10.array.Array<$T>)Array).offset$O((int)(i0),
                                                         (int)(i1),
                                                         (int)(i2),
                                                         (int)(i3));
        }
        
        
//#line 1221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
private int
                                                                                                 offset$O(
                                                                                                 final x10.array.Point pt){
            
//#line 1222 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33625 =
              pt.$apply$O((int)(0));
            
//#line 1222 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33626 =
              layout_min0;
            
//#line 1222 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
int offset =
              ((t33625) - (((int)(t33626))));
            
//#line 1223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33627 =
              pt.
                rank;
            
//#line 1223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33655 =
              ((t33627) > (((int)(1))));
            
//#line 1223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33655) {
                
//#line 1224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33628 =
                  offset;
                
//#line 1224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33629 =
                  layout_stride1;
                
//#line 1224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33630 =
                  ((t33628) * (((int)(t33629))));
                
//#line 1224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33631 =
                  pt.$apply$O((int)(1));
                
//#line 1224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33632 =
                  ((t33630) + (((int)(t33631))));
                
//#line 1224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33633 =
                  layout_min1;
                
//#line 1224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33634 =
                  ((t33632) - (((int)(t33633))));
                
//#line 1224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
offset = t33634;
                
//#line 1225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int i33065min34027 =
                  2;
                
//#line 1225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34028 =
                  pt.
                    rank;
                
//#line 1225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int i33065max34029 =
                  ((t34028) - (((int)(1))));
                
//#line 1225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
int i34024 =
                  i33065min34027;
                
//#line 1225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
for (;
                                                                                                            true;
                                                                                                            ) {
                    
//#line 1225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34025 =
                      i34024;
                    
//#line 1225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t34026 =
                      ((t34025) <= (((int)(i33065max34029))));
                    
//#line 1225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (!(t34026)) {
                        
//#line 1225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
break;
                    }
                    
//#line 1225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int i34021 =
                      i34024;
                    
//#line 1226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34007 =
                      offset;
                    
//#line 1226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t34008 =
                      ((x10.array.Array)(layout));
                    
//#line 1226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34009 =
                      ((i34021) - (((int)(2))));
                    
//#line 1226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34010 =
                      ((2) * (((int)(t34009))));
                    
//#line 1226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34011 =
                      x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)t34008).$apply$G((int)(t34010)));
                    
//#line 1226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34012 =
                      ((t34007) * (((int)(t34011))));
                    
//#line 1226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34013 =
                      pt.$apply$O((int)(i34021));
                    
//#line 1226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34014 =
                      ((t34012) + (((int)(t34013))));
                    
//#line 1226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t34015 =
                      ((x10.array.Array)(layout));
                    
//#line 1226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34016 =
                      ((i34021) - (((int)(2))));
                    
//#line 1226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34017 =
                      ((2) * (((int)(t34016))));
                    
//#line 1226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34018 =
                      ((t34017) + (((int)(1))));
                    
//#line 1226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34019 =
                      x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)t34015).$apply$G((int)(t34018)));
                    
//#line 1226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34020 =
                      ((t34014) - (((int)(t34019))));
                    
//#line 1226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
offset = t34020;
                    
//#line 1225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34022 =
                      i34024;
                    
//#line 1225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34023 =
                      ((t34022) + (((int)(1))));
                    
//#line 1225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
i34024 = t34023;
                }
            }
            
//#line 1229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33656 =
              offset;
            
//#line 1229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33656;
        }
        
        public static <$T>int
          offset$P__1$1x10$array$Array$$T$2$O(
          final x10.rtt.Type $T,
          final x10.array.Point pt,
          final x10.array.Array<$T> Array){
            return ((x10.array.Array<$T>)Array).offset$O(((x10.array.Point)(pt)));
        }
        
        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
@x10.core.X10Generated public static class LayoutHelper extends x10.core.Struct implements x10.x10rt.X10JavaSerializable
                                                                                               {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, LayoutHelper.class);
            
            public static final x10.rtt.RuntimeType<LayoutHelper> $RTT = x10.rtt.NamedType.<LayoutHelper> make(
            "x10.array.Array.LayoutHelper", /* base class */LayoutHelper.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.STRUCT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(LayoutHelper $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + LayoutHelper.class + " calling"); } 
                $_obj.min0 = $deserializer.readInt();
                $_obj.stride1 = $deserializer.readInt();
                $_obj.min1 = $deserializer.readInt();
                $_obj.size = $deserializer.readInt();
                x10.array.Array layout = (x10.array.Array) $deserializer.readRef();
                $_obj.layout = layout;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                LayoutHelper $_obj = new LayoutHelper((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write(this.min0);
                $serializer.write(this.stride1);
                $serializer.write(this.min1);
                $serializer.write(this.size);
                if (layout instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.layout);
                } else {
                $serializer.write(this.layout);
                }
                
            }
            
            // zero value constructor
            public LayoutHelper(final java.lang.System $dummy) { this.min0 = 0; this.stride1 = 0; this.min1 = 0; this.size = 0; this.layout = null; }
            // constructor just for allocation
            public LayoutHelper(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            
                
//#line 1239 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public int min0;
                
//#line 1240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public int stride1;
                
//#line 1241 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public int min1;
                
//#line 1242 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public int size;
                
//#line 1243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public x10.array.Array<x10.core.Int> layout;
                
                
//#line 1245 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
// creation method for java code (1-phase java constructor)
                public LayoutHelper(final x10.array.Region reg){this((java.lang.System[]) null);
                                                                    $init(reg);}
                
                // constructor for non-virtual call
                final public x10.array.Array.LayoutHelper x10$array$Array$LayoutHelper$$init$S(final x10.array.Region reg) { {
                                                                                                                                    
//#line 1245 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
;
                                                                                                                                    
//#line 1245 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"

                                                                                                                                    
//#line 1246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33715 =
                                                                                                                                      reg.isEmpty$O();
                                                                                                                                    
//#line 1246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33715) {
                                                                                                                                        
//#line 1247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33657 =
                                                                                                                                          this.min1 = 0;
                                                                                                                                        
//#line 1247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33658 =
                                                                                                                                          this.stride1 = t33657;
                                                                                                                                        
//#line 1247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.min0 = t33658;
                                                                                                                                        
//#line 1248 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.size = 0;
                                                                                                                                        
//#line 1249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout = null;
                                                                                                                                    } else {
                                                                                                                                        
//#line 1251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33659 =
                                                                                                                                          reg.
                                                                                                                                            rank;
                                                                                                                                        
//#line 1251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33714 =
                                                                                                                                          ((int) t33659) ==
                                                                                                                                        ((int) 1);
                                                                                                                                        
//#line 1251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33714) {
                                                                                                                                            
//#line 1252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33660 =
                                                                                                                                              reg.min$O((int)(0));
                                                                                                                                            
//#line 1252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.min0 = t33660;
                                                                                                                                            
//#line 1253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.stride1 = 0;
                                                                                                                                            
//#line 1254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.min1 = 0;
                                                                                                                                            
//#line 1255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33661 =
                                                                                                                                              reg.max$O((int)(0));
                                                                                                                                            
//#line 1255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33662 =
                                                                                                                                              reg.min$O((int)(0));
                                                                                                                                            
//#line 1255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33663 =
                                                                                                                                              ((t33661) - (((int)(t33662))));
                                                                                                                                            
//#line 1255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33664 =
                                                                                                                                              ((t33663) + (((int)(1))));
                                                                                                                                            
//#line 1255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.size = t33664;
                                                                                                                                            
//#line 1256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout = null;
                                                                                                                                        } else {
                                                                                                                                            
//#line 1257 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33665 =
                                                                                                                                              reg.
                                                                                                                                                rank;
                                                                                                                                            
//#line 1257 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33713 =
                                                                                                                                              ((int) t33665) ==
                                                                                                                                            ((int) 2);
                                                                                                                                            
//#line 1257 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33713) {
                                                                                                                                                
//#line 1258 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33666 =
                                                                                                                                                  reg.min$O((int)(0));
                                                                                                                                                
//#line 1258 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.min0 = t33666;
                                                                                                                                                
//#line 1259 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33667 =
                                                                                                                                                  reg.min$O((int)(1));
                                                                                                                                                
//#line 1259 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.min1 = t33667;
                                                                                                                                                
//#line 1260 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33668 =
                                                                                                                                                  reg.max$O((int)(1));
                                                                                                                                                
//#line 1260 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33669 =
                                                                                                                                                  reg.min$O((int)(1));
                                                                                                                                                
//#line 1260 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33670 =
                                                                                                                                                  ((t33668) - (((int)(t33669))));
                                                                                                                                                
//#line 1260 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33671 =
                                                                                                                                                  ((t33670) + (((int)(1))));
                                                                                                                                                
//#line 1260 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.stride1 = t33671;
                                                                                                                                                
//#line 1261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33675 =
                                                                                                                                                  stride1;
                                                                                                                                                
//#line 1261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33672 =
                                                                                                                                                  reg.max$O((int)(0));
                                                                                                                                                
//#line 1261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33673 =
                                                                                                                                                  reg.min$O((int)(0));
                                                                                                                                                
//#line 1261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33674 =
                                                                                                                                                  ((t33672) - (((int)(t33673))));
                                                                                                                                                
//#line 1261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33676 =
                                                                                                                                                  ((t33674) + (((int)(1))));
                                                                                                                                                
//#line 1261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33677 =
                                                                                                                                                  ((t33675) * (((int)(t33676))));
                                                                                                                                                
//#line 1261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.size = t33677;
                                                                                                                                                
//#line 1262 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout = null;
                                                                                                                                            } else {
                                                                                                                                                
//#line 1264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33678 =
                                                                                                                                                  reg.
                                                                                                                                                    rank;
                                                                                                                                                
//#line 1264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33679 =
                                                                                                                                                  ((t33678) - (((int)(2))));
                                                                                                                                                
//#line 1264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33680 =
                                                                                                                                                  ((2) * (((int)(t33679))));
                                                                                                                                                
//#line 1264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t33681 =
                                                                                                                                                  ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(t33680)));
                                                                                                                                                
//#line 1264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.layout = ((x10.array.Array)(t33681));
                                                                                                                                                
//#line 1265 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33682 =
                                                                                                                                                  reg.min$O((int)(0));
                                                                                                                                                
//#line 1265 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.min0 = t33682;
                                                                                                                                                
//#line 1266 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33683 =
                                                                                                                                                  reg.min$O((int)(1));
                                                                                                                                                
//#line 1266 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.min1 = t33683;
                                                                                                                                                
//#line 1267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33684 =
                                                                                                                                                  reg.max$O((int)(1));
                                                                                                                                                
//#line 1267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33685 =
                                                                                                                                                  reg.min$O((int)(1));
                                                                                                                                                
//#line 1267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33686 =
                                                                                                                                                  ((t33684) - (((int)(t33685))));
                                                                                                                                                
//#line 1267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33687 =
                                                                                                                                                  ((t33686) + (((int)(1))));
                                                                                                                                                
//#line 1267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.stride1 = t33687;
                                                                                                                                                
//#line 1268 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33691 =
                                                                                                                                                  stride1;
                                                                                                                                                
//#line 1268 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33688 =
                                                                                                                                                  reg.max$O((int)(0));
                                                                                                                                                
//#line 1268 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33689 =
                                                                                                                                                  reg.min$O((int)(0));
                                                                                                                                                
//#line 1268 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33690 =
                                                                                                                                                  ((t33688) - (((int)(t33689))));
                                                                                                                                                
//#line 1268 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33692 =
                                                                                                                                                  ((t33690) + (((int)(1))));
                                                                                                                                                
//#line 1268 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
int sz =
                                                                                                                                                  ((t33691) * (((int)(t33692))));
                                                                                                                                                
//#line 1269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int i33081min34050 =
                                                                                                                                                  2;
                                                                                                                                                
//#line 1269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34051 =
                                                                                                                                                  reg.
                                                                                                                                                    rank;
                                                                                                                                                
//#line 1269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int i33081max34052 =
                                                                                                                                                  ((t34051) - (((int)(1))));
                                                                                                                                                
//#line 1269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
int i34047 =
                                                                                                                                                  i33081min34050;
                                                                                                                                                
//#line 1269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
for (;
                                                                                                                                                                                                                                            true;
                                                                                                                                                                                                                                            ) {
                                                                                                                                                    
//#line 1269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34048 =
                                                                                                                                                      i34047;
                                                                                                                                                    
//#line 1269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t34049 =
                                                                                                                                                      ((t34048) <= (((int)(i33081max34052))));
                                                                                                                                                    
//#line 1269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (!(t34049)) {
                                                                                                                                                        
//#line 1269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
break;
                                                                                                                                                    }
                                                                                                                                                    
//#line 1269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int i34044 =
                                                                                                                                                      i34047;
                                                                                                                                                    
//#line 1270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34030 =
                                                                                                                                                      reg.max$O((int)(i34044));
                                                                                                                                                    
//#line 1270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34031 =
                                                                                                                                                      reg.min$O((int)(i34044));
                                                                                                                                                    
//#line 1270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34032 =
                                                                                                                                                      ((t34030) - (((int)(t34031))));
                                                                                                                                                    
//#line 1270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int stride34033 =
                                                                                                                                                      ((t34032) + (((int)(1))));
                                                                                                                                                    
//#line 1271 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34034 =
                                                                                                                                                      sz;
                                                                                                                                                    
//#line 1271 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34035 =
                                                                                                                                                      ((t34034) * (((int)(stride34033))));
                                                                                                                                                    
//#line 1271 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
sz = t34035;
                                                                                                                                                    
//#line 1272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t34036 =
                                                                                                                                                      ((x10.array.Array)(layout));
                                                                                                                                                    
//#line 1272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34037 =
                                                                                                                                                      ((i34044) - (((int)(2))));
                                                                                                                                                    
//#line 1272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34038 =
                                                                                                                                                      ((2) * (((int)(t34037))));
                                                                                                                                                    
//#line 1272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
((x10.array.Array<x10.core.Int>)t34036).$set__1x10$array$Array$$T$G((int)(t34038),
                                                                                                                                                                                                                                                                                                               x10.core.Int.$box(stride34033));
                                                                                                                                                    
//#line 1273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t34039 =
                                                                                                                                                      ((x10.array.Array)(layout));
                                                                                                                                                    
//#line 1273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34040 =
                                                                                                                                                      ((i34044) - (((int)(2))));
                                                                                                                                                    
//#line 1273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34041 =
                                                                                                                                                      ((2) * (((int)(t34040))));
                                                                                                                                                    
//#line 1273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34042 =
                                                                                                                                                      ((t34041) + (((int)(1))));
                                                                                                                                                    
//#line 1273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34043 =
                                                                                                                                                      reg.min$O((int)(i34044));
                                                                                                                                                    
//#line 1273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
((x10.array.Array<x10.core.Int>)t34039).$set__1x10$array$Array$$T$G((int)(t34042),
                                                                                                                                                                                                                                                                                                               x10.core.Int.$box(t34043));
                                                                                                                                                    
//#line 1269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34045 =
                                                                                                                                                      i34047;
                                                                                                                                                    
//#line 1269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t34046 =
                                                                                                                                                      ((t34045) + (((int)(1))));
                                                                                                                                                    
//#line 1269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
i34047 = t34046;
                                                                                                                                                }
                                                                                                                                                
//#line 1275 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33712 =
                                                                                                                                                  sz;
                                                                                                                                                
//#line 1275 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.size = t33712;
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                }
                                                                                                                                return this;
                                                                                                                                }
                
                // constructor
                public x10.array.Array.LayoutHelper $init(final x10.array.Region reg){return x10$array$Array$LayoutHelper$$init$S(reg);}
                
                
                
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final public java.lang.String
                                                                                                         typeName(
                                                                                                         ){try {return x10.rtt.Types.typeName(this);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
                
                
                
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final public java.lang.String
                                                                                                         toString(
                                                                                                         ){
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33716 =
                      "struct x10.array.Array.LayoutHelper: min0=";
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33717 =
                      this.
                        min0;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33718 =
                      ((t33716) + ((x10.core.Int.$box(t33717))));
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33719 =
                      ((t33718) + (" stride1="));
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33720 =
                      this.
                        stride1;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33721 =
                      ((t33719) + ((x10.core.Int.$box(t33720))));
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33722 =
                      ((t33721) + (" min1="));
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33723 =
                      this.
                        min1;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33724 =
                      ((t33722) + ((x10.core.Int.$box(t33723))));
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33725 =
                      ((t33724) + (" size="));
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33726 =
                      this.
                        size;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33727 =
                      ((t33725) + ((x10.core.Int.$box(t33726))));
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33728 =
                      ((t33727) + (" layout="));
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t33729 =
                      ((x10.array.Array)(this.
                                           layout));
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.String t33730 =
                      ((t33728) + (t33729));
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33730;
                }
                
                
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final public int
                                                                                                         hashCode(
                                                                                                         ){
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
int result =
                      1;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33731 =
                      result;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33733 =
                      ((8191) * (((int)(t33731))));
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33732 =
                      this.
                        min0;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33734 =
                      x10.rtt.Types.hashCode(t33732);
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33735 =
                      ((t33733) + (((int)(t33734))));
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
result = t33735;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33736 =
                      result;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33738 =
                      ((8191) * (((int)(t33736))));
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33737 =
                      this.
                        stride1;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33739 =
                      x10.rtt.Types.hashCode(t33737);
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33740 =
                      ((t33738) + (((int)(t33739))));
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
result = t33740;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33741 =
                      result;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33743 =
                      ((8191) * (((int)(t33741))));
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33742 =
                      this.
                        min1;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33744 =
                      x10.rtt.Types.hashCode(t33742);
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33745 =
                      ((t33743) + (((int)(t33744))));
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
result = t33745;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33746 =
                      result;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33748 =
                      ((8191) * (((int)(t33746))));
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33747 =
                      this.
                        size;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33749 =
                      x10.rtt.Types.hashCode(t33747);
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33750 =
                      ((t33748) + (((int)(t33749))));
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
result = t33750;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33751 =
                      result;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33753 =
                      ((8191) * (((int)(t33751))));
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t33752 =
                      ((x10.array.Array)(this.
                                           layout));
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33754 =
                      ((x10.core.RefI)(t33752)).hashCode();
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33755 =
                      ((t33753) + (((int)(t33754))));
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
result = t33755;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33756 =
                      result;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33756;
                }
                
                
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final public boolean
                                                                                                         equals(
                                                                                                         java.lang.Object other){
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.Object t33757 =
                      other;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33758 =
                      x10.array.Array.LayoutHelper.$RTT.isInstance(t33757);
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33759 =
                      !(t33758);
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33759) {
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return false;
                    }
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.Object t33760 =
                      other;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array.LayoutHelper t33761 =
                      ((x10.array.Array.LayoutHelper)x10.rtt.Types.asStruct(x10.array.Array.LayoutHelper.$RTT,t33760));
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33762 =
                      this.equals$O(((x10.array.Array.LayoutHelper)(t33761)));
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33762;
                }
                
                
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final public boolean
                                                                                                         equals$O(
                                                                                                         x10.array.Array.LayoutHelper other){
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33764 =
                      this.
                        min0;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array.LayoutHelper t33763 =
                      other;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33765 =
                      t33763.
                        min0;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
boolean t33769 =
                      ((int) t33764) ==
                    ((int) t33765);
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33769) {
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33767 =
                          this.
                            stride1;
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array.LayoutHelper t33766 =
                          other;
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33768 =
                          t33766.
                            stride1;
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
t33769 = ((int) t33767) ==
                        ((int) t33768);
                    }
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
boolean t33773 =
                      t33769;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33773) {
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33771 =
                          this.
                            min1;
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array.LayoutHelper t33770 =
                          other;
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33772 =
                          t33770.
                            min1;
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
t33773 = ((int) t33771) ==
                        ((int) t33772);
                    }
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
boolean t33777 =
                      t33773;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33777) {
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33775 =
                          this.
                            size;
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array.LayoutHelper t33774 =
                          other;
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33776 =
                          t33774.
                            size;
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
t33777 = ((int) t33775) ==
                        ((int) t33776);
                    }
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
boolean t33781 =
                      t33777;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33781) {
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t33779 =
                          ((x10.array.Array)(this.
                                               layout));
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array.LayoutHelper t33778 =
                          other;
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t33780 =
                          ((x10.array.Array)(t33778.
                                               layout));
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
t33781 = x10.rtt.Equality.equalsequals((t33779),(t33780));
                    }
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33782 =
                      t33781;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33782;
                }
                
                
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final public boolean
                                                                                                         _struct_equals$O(
                                                                                                         java.lang.Object other){
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.Object t33783 =
                      other;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33784 =
                      x10.array.Array.LayoutHelper.$RTT.isInstance(t33783);
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33785 =
                      !(t33784);
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33785) {
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return false;
                    }
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final java.lang.Object t33786 =
                      other;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array.LayoutHelper t33787 =
                      ((x10.array.Array.LayoutHelper)x10.rtt.Types.asStruct(x10.array.Array.LayoutHelper.$RTT,t33786));
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33788 =
                      this._struct_equals$O(((x10.array.Array.LayoutHelper)(t33787)));
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33788;
                }
                
                
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final public boolean
                                                                                                         _struct_equals$O(
                                                                                                         x10.array.Array.LayoutHelper other){
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33790 =
                      this.
                        min0;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array.LayoutHelper t33789 =
                      other;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33791 =
                      t33789.
                        min0;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
boolean t33795 =
                      ((int) t33790) ==
                    ((int) t33791);
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33795) {
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33793 =
                          this.
                            stride1;
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array.LayoutHelper t33792 =
                          other;
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33794 =
                          t33792.
                            stride1;
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
t33795 = ((int) t33793) ==
                        ((int) t33794);
                    }
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
boolean t33799 =
                      t33795;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33799) {
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33797 =
                          this.
                            min1;
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array.LayoutHelper t33796 =
                          other;
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33798 =
                          t33796.
                            min1;
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
t33799 = ((int) t33797) ==
                        ((int) t33798);
                    }
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
boolean t33803 =
                      t33799;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33803) {
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33801 =
                          this.
                            size;
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array.LayoutHelper t33800 =
                          other;
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33802 =
                          t33800.
                            size;
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
t33803 = ((int) t33801) ==
                        ((int) t33802);
                    }
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
boolean t33807 =
                      t33803;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
if (t33807) {
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t33805 =
                          ((x10.array.Array)(this.
                                               layout));
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array.LayoutHelper t33804 =
                          other;
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t33806 =
                          ((x10.array.Array)(t33804.
                                               layout));
                        
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
t33807 = x10.rtt.Equality.equalsequals((t33805),(t33806));
                    }
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33808 =
                      t33807;
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33808;
                }
                
                
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final public x10.array.Array.LayoutHelper
                                                                                                         x10$array$Array$LayoutHelper$$x10$array$Array$LayoutHelper$this(
                                                                                                         ){
                    
//#line 1238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return x10.array.Array.LayoutHelper.this;
                }
            
        }
        
        
        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final public x10.array.Array<$T>
                                                                                               x10$array$Array$$x10$array$Array$this(
                                                                                               ){
            
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return x10.array.Array.this;
        }
        
        
//#line 376 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
@x10.core.X10Generated final public static class Anonymous$13425<$T32945> extends x10.core.Ref implements x10.lang.Iterable, x10.x10rt.X10JavaSerializable
                                                                                              {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Anonymous$13425.class);
            
            public static final x10.rtt.RuntimeType<Anonymous$13425> $RTT = x10.rtt.NamedType.<Anonymous$13425> make(
            "x10.array.Array.Anonymous$13425", /* base class */Anonymous$13425.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Iterable.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T32945;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(Anonymous$13425 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Anonymous$13425.class + " calling"); } 
                $_obj.$T32945 = ( x10.rtt.Type ) $deserializer.readRef();
                x10.array.Array out$ = (x10.array.Array) $deserializer.readRef();
                $_obj.out$ = out$;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                Anonymous$13425 $_obj = new Anonymous$13425((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T32945);
                if (out$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$);
                } else {
                $serializer.write(this.out$);
                }
                
            }
            
            // constructor just for allocation
            public Anonymous$13425(final java.lang.System[] $dummy, final x10.rtt.Type $T32945) { 
            super($dummy);
            x10.array.Array.Anonymous$13425.$initParams(this, $T32945);
            }
            
                private x10.rtt.Type $T32945;
                // initializer of type parameters
                public static void $initParams(final Anonymous$13425 $this, final x10.rtt.Type $T32945) {
                $this.$T32945 = $T32945;
                }
                
                
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public x10.array.Array<$T32945> out$;
                
                
//#line 377 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public x10.array.Array.Anonymous$13425.Anonymous$13482<$T32945>
                                                                                                        iterator(
                                                                                                        ){
                    
//#line 377 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array.Anonymous$13425.Anonymous$13482<$T32945> t33809 =
                      ((x10.array.Array.Anonymous$13425.Anonymous$13482)(new x10.array.Array.Anonymous$13425.Anonymous$13482<$T32945>((java.lang.System[]) null, $T32945).$init(this, (x10.array.Array.Anonymous$13425.Anonymous$13482.__0$1x10$array$Array$Anonymous$13425$Anonymous$13482$$T32946$2) null)));
                    
//#line 377 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33809;
                }
                
                
//#line 376 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
// creation method for java code (1-phase java constructor)
                public Anonymous$13425(final x10.rtt.Type $T32945,
                                       final x10.array.Array<$T32945> out$, __0$1x10$array$Array$Anonymous$13425$$T32945$2 $dummy){this((java.lang.System[]) null, $T32945);
                                                                                                                                       $init(out$, (x10.array.Array.Anonymous$13425.__0$1x10$array$Array$Anonymous$13425$$T32945$2) null);}
                
                // constructor for non-virtual call
                final public x10.array.Array.Anonymous$13425<$T32945> x10$array$Array$Anonymous$13425$$init$S(final x10.array.Array<$T32945> out$, __0$1x10$array$Array$Anonymous$13425$$T32945$2 $dummy) { {
                                                                                                                                                                                                                   
//#line 376 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"

                                                                                                                                                                                                                   
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.out$ = out$;
                                                                                                                                                                                                               }
                                                                                                                                                                                                               return this;
                                                                                                                                                                                                               }
                
                // constructor
                public x10.array.Array.Anonymous$13425<$T32945> $init(final x10.array.Array<$T32945> out$, __0$1x10$array$Array$Anonymous$13425$$T32945$2 $dummy){return x10$array$Array$Anonymous$13425$$init$S(out$, $dummy);}
                
                
                
//#line 377 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
@x10.core.X10Generated final public static class Anonymous$13482<$T32946> extends x10.core.Ref implements x10.lang.Iterator, x10.x10rt.X10JavaSerializable
                                                                                                      {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Anonymous$13482.class);
                    
                    public static final x10.rtt.RuntimeType<Anonymous$13482> $RTT = x10.rtt.NamedType.<Anonymous$13482> make(
                    "x10.array.Array.Anonymous$13425.Anonymous$13482", /* base class */Anonymous$13482.class, 
                    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
                    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Iterator.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.Types.OBJECT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T32946;return null;}
                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Anonymous$13482 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Anonymous$13482.class + " calling"); } 
                        $_obj.$T32946 = ( x10.rtt.Type ) $deserializer.readRef();
                        $_obj.cur = $deserializer.readInt();
                        x10.array.Array.Anonymous$13425 out$ = (x10.array.Array.Anonymous$13425) $deserializer.readRef();
                        $_obj.out$ = out$;
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        Anonymous$13482 $_obj = new Anonymous$13482((java.lang.System[]) null, (x10.rtt.Type) null);
                        $deserializer.record_reference($_obj);
                        return $_deserialize_body($_obj, $deserializer);
                        
                    }
                    
                    public short $_get_serialization_id() {
                    
                         return $_serialization_id;
                        
                    }
                    
                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                    
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T32946);
                        $serializer.write(this.cur);
                        if (out$ instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$);
                        } else {
                        $serializer.write(this.out$);
                        }
                        
                    }
                    
                    // constructor just for allocation
                    public Anonymous$13482(final java.lang.System[] $dummy, final x10.rtt.Type $T32946) { 
                    super($dummy);
                    x10.array.Array.Anonymous$13425.Anonymous$13482.$initParams(this, $T32946);
                    }
                    
                        private x10.rtt.Type $T32946;
                        // initializer of type parameters
                        public static void $initParams(final Anonymous$13482 $this, final x10.rtt.Type $T32946) {
                        $this.$T32946 = $T32946;
                        }
                        
                        
//#line 376 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public x10.array.Array.Anonymous$13425<$T32946> out$;
                        
//#line 378 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public int cur = 0;
                        
                        
//#line 379 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public $T32946
                                                                                                                next$G(
                                                                                                                ){
                            
//#line 379 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array.Anonymous$13425<$T32946> t33810 =
                              this.
                                out$;
                            
//#line 379 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<$T32946> t33811 =
                              ((x10.array.Array.Anonymous$13425<$T32946>)t33810).
                                out$;
                            
//#line 379 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T32946> t33815 =
                              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<$T32946>)t33811).
                                                               raw));
                            
//#line 379 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array.Anonymous$13425.Anonymous$13482<$T32946> x33108 =
                              ((x10.array.Array.Anonymous$13425.Anonymous$13482)(this));
                            
//#line 379 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
;
                            
//#line 379 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33812 =
                              ((x10.array.Array.Anonymous$13425.Anonymous$13482<$T32946>)x33108).
                                cur;
                            
//#line 379 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33813 =
                              ((t33812) + (((int)(1))));
                            
//#line 379 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33814 =
                              x33108.cur = t33813;
                            
//#line 379 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33816 =
                              ((t33814) - (((int)(1))));
                            
//#line 379 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $T32946 t33817 =
                              (($T32946)((((x10.core.IndexedMemoryChunk<$T32946>)(t33815))).$apply$G(((int)(t33816)))));
                            
//#line 379 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33817;
                        }
                        
                        
//#line 380 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public boolean
                                                                                                                hasNext$O(
                                                                                                                ){
                            
//#line 380 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33821 =
                              cur;
                            
//#line 380 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array.Anonymous$13425<$T32946> t33818 =
                              this.
                                out$;
                            
//#line 380 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<$T32946> t33819 =
                              ((x10.array.Array.Anonymous$13425<$T32946>)t33818).
                                out$;
                            
//#line 380 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<$T32946> t33820 =
                              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<$T32946>)t33819).
                                                               raw));
                            
//#line 380 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33822 =
                              ((((x10.core.IndexedMemoryChunk<$T32946>)(t33820))).length);
                            
//#line 380 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33823 =
                              ((t33821) < (((int)(t33822))));
                            
//#line 380 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33823;
                        }
                        
                        
//#line 377 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
// creation method for java code (1-phase java constructor)
                        public Anonymous$13482(final x10.rtt.Type $T32946,
                                               final x10.array.Array.Anonymous$13425<$T32946> out$, __0$1x10$array$Array$Anonymous$13425$Anonymous$13482$$T32946$2 $dummy){this((java.lang.System[]) null, $T32946);
                                                                                                                                                                               $init(out$, (x10.array.Array.Anonymous$13425.Anonymous$13482.__0$1x10$array$Array$Anonymous$13425$Anonymous$13482$$T32946$2) null);}
                        
                        // constructor for non-virtual call
                        final public x10.array.Array.Anonymous$13425.Anonymous$13482<$T32946> x10$array$Array$Anonymous$13425$Anonymous$13482$$init$S(final x10.array.Array.Anonymous$13425<$T32946> out$, __0$1x10$array$Array$Anonymous$13425$Anonymous$13482$$T32946$2 $dummy) { {
                                                                                                                                                                                                                                                                                           
//#line 377 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"

                                                                                                                                                                                                                                                                                           
//#line 376 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.out$ = out$;
                                                                                                                                                                                                                                                                                       }
                                                                                                                                                                                                                                                                                       return this;
                                                                                                                                                                                                                                                                                       }
                        
                        // constructor
                        public x10.array.Array.Anonymous$13425.Anonymous$13482<$T32946> $init(final x10.array.Array.Anonymous$13425<$T32946> out$, __0$1x10$array$Array$Anonymous$13425$Anonymous$13482$$T32946$2 $dummy){return x10$array$Array$Anonymous$13425$Anonymous$13482$$init$S(out$, $dummy);}
                        
                    // synthetic type for parameter mangling
                    public abstract static class __0$1x10$array$Array$Anonymous$13425$Anonymous$13482$$T32946$2 {}
                    
                }
                
            // synthetic type for parameter mangling
            public abstract static class __0$1x10$array$Array$Anonymous$13425$$T32945$2 {}
            
        }
        
        
//#line 384 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
@x10.core.X10Generated final public static class Anonymous$13713<$T32947> extends x10.core.Ref implements x10.lang.Iterable, x10.x10rt.X10JavaSerializable
                                                                                              {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Anonymous$13713.class);
            
            public static final x10.rtt.RuntimeType<Anonymous$13713> $RTT = x10.rtt.NamedType.<Anonymous$13713> make(
            "x10.array.Array.Anonymous$13713", /* base class */Anonymous$13713.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Iterable.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T32947;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(Anonymous$13713 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Anonymous$13713.class + " calling"); } 
                $_obj.$T32947 = ( x10.rtt.Type ) $deserializer.readRef();
                x10.array.Array out$ = (x10.array.Array) $deserializer.readRef();
                $_obj.out$ = out$;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                Anonymous$13713 $_obj = new Anonymous$13713((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T32947);
                if (out$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$);
                } else {
                $serializer.write(this.out$);
                }
                
            }
            
            // constructor just for allocation
            public Anonymous$13713(final java.lang.System[] $dummy, final x10.rtt.Type $T32947) { 
            super($dummy);
            x10.array.Array.Anonymous$13713.$initParams(this, $T32947);
            }
            
                private x10.rtt.Type $T32947;
                // initializer of type parameters
                public static void $initParams(final Anonymous$13713 $this, final x10.rtt.Type $T32947) {
                $this.$T32947 = $T32947;
                }
                
                
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public x10.array.Array<$T32947> out$;
                
                
//#line 385 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public x10.array.Array.Anonymous$13713.Anonymous$13770<$T32947>
                                                                                                        iterator(
                                                                                                        ){
                    
//#line 385 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array.Anonymous$13713.Anonymous$13770<$T32947> t33824 =
                      ((x10.array.Array.Anonymous$13713.Anonymous$13770)(new x10.array.Array.Anonymous$13713.Anonymous$13770<$T32947>((java.lang.System[]) null, $T32947).$init(this, (x10.array.Array.Anonymous$13713.Anonymous$13770.__0$1x10$array$Array$Anonymous$13713$Anonymous$13770$$T32948$2) null)));
                    
//#line 385 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33824;
                }
                
                
//#line 384 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
// creation method for java code (1-phase java constructor)
                public Anonymous$13713(final x10.rtt.Type $T32947,
                                       final x10.array.Array<$T32947> out$, __0$1x10$array$Array$Anonymous$13713$$T32947$2 $dummy){this((java.lang.System[]) null, $T32947);
                                                                                                                                       $init(out$, (x10.array.Array.Anonymous$13713.__0$1x10$array$Array$Anonymous$13713$$T32947$2) null);}
                
                // constructor for non-virtual call
                final public x10.array.Array.Anonymous$13713<$T32947> x10$array$Array$Anonymous$13713$$init$S(final x10.array.Array<$T32947> out$, __0$1x10$array$Array$Anonymous$13713$$T32947$2 $dummy) { {
                                                                                                                                                                                                                   
//#line 384 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"

                                                                                                                                                                                                                   
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.out$ = out$;
                                                                                                                                                                                                               }
                                                                                                                                                                                                               return this;
                                                                                                                                                                                                               }
                
                // constructor
                public x10.array.Array.Anonymous$13713<$T32947> $init(final x10.array.Array<$T32947> out$, __0$1x10$array$Array$Anonymous$13713$$T32947$2 $dummy){return x10$array$Array$Anonymous$13713$$init$S(out$, $dummy);}
                
                
                
//#line 385 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
@x10.core.X10Generated final public static class Anonymous$13770<$T32948> extends x10.core.Ref implements x10.lang.Iterator, x10.x10rt.X10JavaSerializable
                                                                                                      {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Anonymous$13770.class);
                    
                    public static final x10.rtt.RuntimeType<Anonymous$13770> $RTT = x10.rtt.NamedType.<Anonymous$13770> make(
                    "x10.array.Array.Anonymous$13713.Anonymous$13770", /* base class */Anonymous$13770.class, 
                    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
                    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Iterator.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.Types.OBJECT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T32948;return null;}
                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Anonymous$13770 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Anonymous$13770.class + " calling"); } 
                        $_obj.$T32948 = ( x10.rtt.Type ) $deserializer.readRef();
                        x10.lang.Iterator regIt = (x10.lang.Iterator) $deserializer.readRef();
                        $_obj.regIt = regIt;
                        x10.array.Array.Anonymous$13713 out$ = (x10.array.Array.Anonymous$13713) $deserializer.readRef();
                        $_obj.out$ = out$;
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        Anonymous$13770 $_obj = new Anonymous$13770((java.lang.System[]) null, (x10.rtt.Type) null);
                        $deserializer.record_reference($_obj);
                        return $_deserialize_body($_obj, $deserializer);
                        
                    }
                    
                    public short $_get_serialization_id() {
                    
                         return $_serialization_id;
                        
                    }
                    
                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                    
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T32948);
                        if (regIt instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.regIt);
                        } else {
                        $serializer.write(this.regIt);
                        }
                        if (out$ instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$);
                        } else {
                        $serializer.write(this.out$);
                        }
                        
                    }
                    
                    // constructor just for allocation
                    public Anonymous$13770(final java.lang.System[] $dummy, final x10.rtt.Type $T32948) { 
                    super($dummy);
                    x10.array.Array.Anonymous$13713.Anonymous$13770.$initParams(this, $T32948);
                    }
                    
                        private x10.rtt.Type $T32948;
                        // initializer of type parameters
                        public static void $initParams(final Anonymous$13770 $this, final x10.rtt.Type $T32948) {
                        $this.$T32948 = $T32948;
                        }
                        
                        
//#line 384 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public x10.array.Array.Anonymous$13713<$T32948> out$;
                        
//#line 386 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public x10.lang.Iterator<x10.array.Point> regIt;
                        
                        
//#line 387 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public $T32948
                                                                                                                next$G(
                                                                                                                ){
                            
//#line 387 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array.Anonymous$13713<$T32948> t33825 =
                              this.
                                out$;
                            
//#line 387 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<$T32948> t33827 =
                              ((x10.array.Array.Anonymous$13713<$T32948>)t33825).
                                out$;
                            
//#line 387 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.Iterator<x10.array.Point> t33826 =
                              ((x10.lang.Iterator)(regIt));
                            
//#line 387 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Point t33828 =
                              ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)t33826).next$G()));
                            
//#line 387 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $T32948 t33829 =
                              (($T32948)(((x10.array.Array<$T32948>)t33827).$apply$G(((x10.array.Point)(t33828)))));
                            
//#line 387 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33829;
                        }
                        
                        
//#line 388 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public boolean
                                                                                                                hasNext$O(
                                                                                                                ){
                            
//#line 388 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.Iterator<x10.array.Point> t33830 =
                              ((x10.lang.Iterator)(regIt));
                            
//#line 388 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final boolean t33831 =
                              ((x10.lang.Iterator<x10.array.Point>)t33830).hasNext$O();
                            
//#line 388 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33831;
                        }
                        
                        
//#line 385 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
// creation method for java code (1-phase java constructor)
                        public Anonymous$13770(final x10.rtt.Type $T32948,
                                               final x10.array.Array.Anonymous$13713<$T32948> out$, __0$1x10$array$Array$Anonymous$13713$Anonymous$13770$$T32948$2 $dummy){this((java.lang.System[]) null, $T32948);
                                                                                                                                                                               $init(out$, (x10.array.Array.Anonymous$13713.Anonymous$13770.__0$1x10$array$Array$Anonymous$13713$Anonymous$13770$$T32948$2) null);}
                        
                        // constructor for non-virtual call
                        final public x10.array.Array.Anonymous$13713.Anonymous$13770<$T32948> x10$array$Array$Anonymous$13713$Anonymous$13770$$init$S(final x10.array.Array.Anonymous$13713<$T32948> out$, __0$1x10$array$Array$Anonymous$13713$Anonymous$13770$$T32948$2 $dummy) { {
                                                                                                                                                                                                                                                                                           
//#line 385 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"

                                                                                                                                                                                                                                                                                           
//#line 384 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.out$ = out$;
                                                                                                                                                                                                                                                                                           
//#line 386 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array.Anonymous$13713<$T32948> t33832 =
                                                                                                                                                                                                                                                                                             this.
                                                                                                                                                                                                                                                                                               out$;
                                                                                                                                                                                                                                                                                           
//#line 386 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<$T32948> t33833 =
                                                                                                                                                                                                                                                                                             ((x10.array.Array.Anonymous$13713<$T32948>)t33832).
                                                                                                                                                                                                                                                                                               out$;
                                                                                                                                                                                                                                                                                           
//#line 386 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.Iterator<x10.array.Point> t33834 =
                                                                                                                                                                                                                                                                                             ((x10.array.Array<$T32948>)t33833).iterator();
                                                                                                                                                                                                                                                                                           
//#line 386 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.regIt = t33834;
                                                                                                                                                                                                                                                                                       }
                                                                                                                                                                                                                                                                                       return this;
                                                                                                                                                                                                                                                                                       }
                        
                        // constructor
                        public x10.array.Array.Anonymous$13713.Anonymous$13770<$T32948> $init(final x10.array.Array.Anonymous$13713<$T32948> out$, __0$1x10$array$Array$Anonymous$13713$Anonymous$13770$$T32948$2 $dummy){return x10$array$Array$Anonymous$13713$Anonymous$13770$$init$S(out$, $dummy);}
                        
                    // synthetic type for parameter mangling
                    public abstract static class __0$1x10$array$Array$Anonymous$13713$Anonymous$13770$$T32948$2 {}
                    
                }
                
            // synthetic type for parameter mangling
            public abstract static class __0$1x10$array$Array$Anonymous$13713$$T32947$2 {}
            
        }
        
        
//#line 394 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
@x10.core.X10Generated final public static class Anonymous$14047<$T32949> extends x10.core.Ref implements x10.lang.Sequence, x10.x10rt.X10JavaSerializable
                                                                                              {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Anonymous$14047.class);
            
            public static final x10.rtt.RuntimeType<Anonymous$14047> $RTT = x10.rtt.NamedType.<Anonymous$14047> make(
            "x10.array.Array.Anonymous$14047", /* base class */Anonymous$14047.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Sequence.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T32949;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(Anonymous$14047 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Anonymous$14047.class + " calling"); } 
                $_obj.$T32949 = ( x10.rtt.Type ) $deserializer.readRef();
                x10.array.Array out$ = (x10.array.Array) $deserializer.readRef();
                $_obj.out$ = out$;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                Anonymous$14047 $_obj = new Anonymous$14047((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T32949);
                if (out$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$);
                } else {
                $serializer.write(this.out$);
                }
                
            }
            
            // constructor just for allocation
            public Anonymous$14047(final java.lang.System[] $dummy, final x10.rtt.Type $T32949) { 
            super($dummy);
            x10.array.Array.Anonymous$14047.$initParams(this, $T32949);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1){}:U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return $apply$G(x10.core.Int.$unbox(a1));
            }
            
                private x10.rtt.Type $T32949;
                // initializer of type parameters
                public static void $initParams(final Anonymous$14047 $this, final x10.rtt.Type $T32949) {
                $this.$T32949 = $T32949;
                }
                
                
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public x10.array.Array<$T32949> out$;
                
                
//#line 395 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public x10.lang.Iterator<$T32949>
                                                                                                        iterator(
                                                                                                        ){
                    
//#line 395 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<$T32949> t33835 =
                      this.
                        out$;
                    
//#line 395 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.Iterable<$T32949> t33836 =
                      ((x10.lang.Iterable<$T32949>)
                        ((x10.array.Array<$T32949>)t33835).values());
                    
//#line 395 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.lang.Iterator<$T32949> t33837 =
                      ((x10.lang.Iterator<$T32949>)
                        ((x10.lang.Iterable<$T32949>)t33836).iterator());
                    
//#line 395 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33837;
                }
                
                
//#line 397 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
public $T32949
                                                                                                        $apply$G(
                                                                                                        final int i){
                    
//#line 397 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<$T32949> t33838 =
                      this.
                        out$;
                    
//#line 397 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $T32949 t33839 =
                      (($T32949)(((x10.array.Array<$T32949>)t33838).$apply$G((int)(i))));
                    
//#line 397 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33839;
                }
                
                
//#line 398 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final public int
                                                                                                        size$O(
                                                                                                        ){
                    
//#line 398 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<$T32949> t33840 =
                      this.
                        out$;
                    
//#line 398 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t33841 =
                      ((x10.array.Array<$T32949>)t33840).
                        size;
                    
//#line 398 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33841;
                }
                
                
//#line 394 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
// creation method for java code (1-phase java constructor)
                public Anonymous$14047(final x10.rtt.Type $T32949,
                                       final x10.array.Array<$T32949> out$, __0$1x10$array$Array$Anonymous$14047$$T32949$2 $dummy){this((java.lang.System[]) null, $T32949);
                                                                                                                                       $init(out$, (x10.array.Array.Anonymous$14047.__0$1x10$array$Array$Anonymous$14047$$T32949$2) null);}
                
                // constructor for non-virtual call
                final public x10.array.Array.Anonymous$14047<$T32949> x10$array$Array$Anonymous$14047$$init$S(final x10.array.Array<$T32949> out$, __0$1x10$array$Array$Anonymous$14047$$T32949$2 $dummy) { {
                                                                                                                                                                                                                   
//#line 394 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"

                                                                                                                                                                                                                   
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
this.out$ = out$;
                                                                                                                                                                                                               }
                                                                                                                                                                                                               return this;
                                                                                                                                                                                                               }
                
                // constructor
                public x10.array.Array.Anonymous$14047<$T32949> $init(final x10.array.Array<$T32949> out$, __0$1x10$array$Array$Anonymous$14047$$T32949$2 $dummy){return x10$array$Array$Anonymous$14047$$init$S(out$, $dummy);}
                
            // synthetic type for parameter mangling
            public abstract static class __0$1x10$array$Array$Anonymous$14047$$T32949$2 {}
            
        }
        
        @x10.core.X10Generated public static class $Closure$0<$T, $U> extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$0.class);
            
            public static final x10.rtt.RuntimeType<$Closure$0> $RTT = x10.rtt.StaticFunType.<$Closure$0> make(
            /* base class */$Closure$0.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.array.Point.$RTT, x10.rtt.UnresolvedType.PARAM(1)), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;if (i ==1)return $U;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$0 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$0.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.$U = ( x10.rtt.Type ) $deserializer.readRef();
                x10.array.Array out$$ = (x10.array.Array) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.core.fun.Fun_0_1 op = (x10.core.fun.Fun_0_1) $deserializer.readRef();
                $_obj.op = op;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$0 $_obj = new $Closure$0((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$U);
                if (out$$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$$);
                } else {
                $serializer.write(this.out$$);
                }
                if (op instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.op);
                } else {
                $serializer.write(this.op);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$0(final java.lang.System[] $dummy, final x10.rtt.Type $T, final x10.rtt.Type $U) { 
            super($dummy);
            x10.array.Array.$Closure$0.$initParams(this, $T, $U);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return $apply$G((x10.array.Point)a1);
            }
            
                private x10.rtt.Type $T;
                private x10.rtt.Type $U;
                // initializer of type parameters
                public static void $initParams(final $Closure$0 $this, final x10.rtt.Type $T, final x10.rtt.Type $U) {
                $this.$T = $T;
                $this.$U = $U;
                }
                
                
                public $U
                  $apply$G(
                  final x10.array.Point p){
                    
//#line 658 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $T t33380 =
                      (($T)(((x10.array.Array<$T>)this.
                                                    out$$).$apply$G(((x10.array.Point)(p)))));
                    
//#line 658 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $U t33381 =
                      (($U)((($U)
                              ((x10.core.fun.Fun_0_1<$T,$U>)this.
                                                              op).$apply(t33380,$T))));
                    
//#line 658 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33381;
                }
                
                public x10.array.Array<$T> out$$;
                public x10.core.fun.Fun_0_1<$T,$U> op;
                
                public $Closure$0(final x10.rtt.Type $T,
                                  final x10.rtt.Type $U,
                                  final x10.array.Array<$T> out$$,
                                  final x10.core.fun.Fun_0_1<$T,$U> op, __0$1x10$array$Array$$Closure$0$$T$2__1$1x10$array$Array$$Closure$0$$T$3x10$array$Array$$Closure$0$$U$2 $dummy) {x10.array.Array.$Closure$0.$initParams(this, $T, $U);
                                                                                                                                                                                              {
                                                                                                                                                                                                 this.out$$ = out$$;
                                                                                                                                                                                                 this.op = ((x10.core.fun.Fun_0_1)(op));
                                                                                                                                                                                             }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$array$Array$$Closure$0$$T$2__1$1x10$array$Array$$Closure$0$$T$3x10$array$Array$$Closure$0$$U$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$1<$T, $S, $U> extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$1.class);
            
            public static final x10.rtt.RuntimeType<$Closure$1> $RTT = x10.rtt.StaticFunType.<$Closure$1> make(
            /* base class */$Closure$1.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(3)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.array.Point.$RTT, x10.rtt.UnresolvedType.PARAM(1)), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;if (i ==1)return $S;if (i ==2)return $U;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$1 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$1.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.$S = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.$U = ( x10.rtt.Type ) $deserializer.readRef();
                x10.array.Array out$$ = (x10.array.Array) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.array.Array src = (x10.array.Array) $deserializer.readRef();
                $_obj.src = src;
                x10.core.fun.Fun_0_2 op = (x10.core.fun.Fun_0_2) $deserializer.readRef();
                $_obj.op = op;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$1 $_obj = new $Closure$1((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$S);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$U);
                if (out$$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$$);
                } else {
                $serializer.write(this.out$$);
                }
                if (src instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.src);
                } else {
                $serializer.write(this.src);
                }
                if (op instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.op);
                } else {
                $serializer.write(this.op);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$1(final java.lang.System[] $dummy, final x10.rtt.Type $T, final x10.rtt.Type $S, final x10.rtt.Type $U) { 
            super($dummy);
            x10.array.Array.$Closure$1.$initParams(this, $T, $S, $U);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return $apply$G((x10.array.Point)a1);
            }
            
                private x10.rtt.Type $T;
                private x10.rtt.Type $S;
                private x10.rtt.Type $U;
                // initializer of type parameters
                public static void $initParams(final $Closure$1 $this, final x10.rtt.Type $T, final x10.rtt.Type $S, final x10.rtt.Type $U) {
                $this.$T = $T;
                $this.$S = $S;
                $this.$U = $U;
                }
                
                
                public $S
                  $apply$G(
                  final x10.array.Point p){
                    
//#line 729 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $T t33407 =
                      (($T)(((x10.array.Array<$T>)this.
                                                    out$$).$apply$G(((x10.array.Point)(p)))));
                    
//#line 729 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $U t33408 =
                      (($U)(((x10.array.Array<$U>)this.
                                                    src).$apply$G(((x10.array.Point)(p)))));
                    
//#line 729 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final $S t33409 =
                      (($S)((($S)
                              ((x10.core.fun.Fun_0_2<$T,$U,$S>)this.
                                                                 op).$apply(t33407,$T,
                                                                            t33408,$U))));
                    
//#line 729 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t33409;
                }
                
                public x10.array.Array<$T> out$$;
                public x10.array.Array<$U> src;
                public x10.core.fun.Fun_0_2<$T,$U,$S> op;
                
                public $Closure$1(final x10.rtt.Type $T,
                                  final x10.rtt.Type $S,
                                  final x10.rtt.Type $U,
                                  final x10.array.Array<$T> out$$,
                                  final x10.array.Array<$U> src,
                                  final x10.core.fun.Fun_0_2<$T,$U,$S> op, __0$1x10$array$Array$$Closure$1$$T$2__1$1x10$array$Array$$Closure$1$$U$2__2$1x10$array$Array$$Closure$1$$T$3x10$array$Array$$Closure$1$$U$3x10$array$Array$$Closure$1$$S$2 $dummy) {x10.array.Array.$Closure$1.$initParams(this, $T, $S, $U);
                                                                                                                                                                                                                                                                    {
                                                                                                                                                                                                                                                                       this.out$$ = out$$;
                                                                                                                                                                                                                                                                       this.src = ((x10.array.Array)(src));
                                                                                                                                                                                                                                                                       this.op = ((x10.core.fun.Fun_0_2)(op));
                                                                                                                                                                                                                                                                   }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$array$Array$$Closure$1$$T$2__1$1x10$array$Array$$Closure$1$$U$2__2$1x10$array$Array$$Closure$1$$T$3x10$array$Array$$Closure$1$$U$3x10$array$Array$$Closure$1$$S$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$2<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$2.class);
            
            public static final x10.rtt.RuntimeType<$Closure$2> $RTT = x10.rtt.StaticFunType.<$Closure$2> make(
            /* base class */$Closure$2.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$2 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$2.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                x10.core.GlobalRef gra = (x10.core.GlobalRef) $deserializer.readRef();
                $_obj.gra = gra;
                x10.array.Point dstPoint = (x10.array.Point) $deserializer.readRef();
                $_obj.dstPoint = dstPoint;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$2 $_obj = new $Closure$2((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                if (gra instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.gra);
                } else {
                $serializer.write(this.gra);
                }
                if (dstPoint instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.dstPoint);
                } else {
                $serializer.write(this.dstPoint);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$2(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.array.Array.$Closure$2.$initParams(this, $T);
            }
            // bridge for method abstract public ()=>U.operator()():U
            public x10.core.Int
              $apply$G(){return x10.core.Int.$box($apply$O());}
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$2 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public int
                  $apply$O(
                  ){
                    
//#line 918 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<$T> t31344 =
                      (((x10.core.GlobalRef<x10.array.Array<$T>>)(this.
                                                                    gra))).$apply$G();
                    
//#line 918 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t31345 =
                      ((x10.array.Region)(((x10.array.Array<$T>)t31344).
                                            region));
                    
//#line 918 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t31346 =
                      t31345.indexOf$O(((x10.array.Point)(this.
                                                            dstPoint)));
                    
//#line 918 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t31346;
                }
                
                public x10.core.GlobalRef<x10.array.Array<$T>> gra;
                public x10.array.Point dstPoint;
                
                public $Closure$2(final x10.rtt.Type $T,
                                  final x10.core.GlobalRef<x10.array.Array<$T>> gra,
                                  final x10.array.Point dstPoint, __0$1x10$array$Array$1x10$array$Array$$Closure$2$$T$2$2 $dummy) {x10.array.Array.$Closure$2.$initParams(this, $T);
                                                                                                                                        {
                                                                                                                                           this.gra = ((x10.core.GlobalRef)(gra));
                                                                                                                                           this.dstPoint = ((x10.array.Point)(dstPoint));
                                                                                                                                       }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$array$Array$1x10$array$Array$$Closure$2$$T$2$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$3<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$3.class);
            
            public static final x10.rtt.RuntimeType<$Closure$3> $RTT = x10.rtt.StaticFunType.<$Closure$3> make(
            /* base class */$Closure$3.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$3 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$3.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                x10.core.GlobalRef gra = (x10.core.GlobalRef) $deserializer.readRef();
                $_obj.gra = gra;
                x10.array.Point srcPoint = (x10.array.Point) $deserializer.readRef();
                $_obj.srcPoint = srcPoint;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$3 $_obj = new $Closure$3((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                if (gra instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.gra);
                } else {
                $serializer.write(this.gra);
                }
                if (srcPoint instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.srcPoint);
                } else {
                $serializer.write(this.srcPoint);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$3(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.array.Array.$Closure$3.$initParams(this, $T);
            }
            // bridge for method abstract public ()=>U.operator()():U
            public x10.core.Int
              $apply$G(){return x10.core.Int.$box($apply$O());}
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$3 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public int
                  $apply$O(
                  ){
                    
//#line 1028 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Array<$T> t31348 =
                      (((x10.core.GlobalRef<x10.array.Array<$T>>)(this.
                                                                    gra))).$apply$G();
                    
//#line 1028 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final x10.array.Region t31349 =
                      ((x10.array.Region)(((x10.array.Array<$T>)t31348).
                                            region));
                    
//#line 1028 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
final int t31350 =
                      t31349.indexOf$O(((x10.array.Point)(this.
                                                            srcPoint)));
                    
//#line 1028 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Array.x10"
return t31350;
                }
                
                public x10.core.GlobalRef<x10.array.Array<$T>> gra;
                public x10.array.Point srcPoint;
                
                public $Closure$3(final x10.rtt.Type $T,
                                  final x10.core.GlobalRef<x10.array.Array<$T>> gra,
                                  final x10.array.Point srcPoint, __0$1x10$array$Array$1x10$array$Array$$Closure$3$$T$2$2 $dummy) {x10.array.Array.$Closure$3.$initParams(this, $T);
                                                                                                                                        {
                                                                                                                                           this.gra = ((x10.core.GlobalRef)(gra));
                                                                                                                                           this.srcPoint = ((x10.array.Point)(srcPoint));
                                                                                                                                       }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$array$Array$1x10$array$Array$$Closure$3$$T$2$2 {}
                
            }
            
        // synthetic type for parameter mangling
        public abstract static class __1$1x10$array$Point$3x10$array$Array$$T$2 {}
        // synthetic type for parameter mangling
        public abstract static class __1x10$array$Array$$T {}
        // synthetic type for parameter mangling
        public abstract static class __1$1x10$array$Array$$T$2 {}
        // synthetic type for parameter mangling
        public abstract static class __0$1x10$array$Array$$T$2 {}
        // synthetic type for parameter mangling
        public abstract static class __1$1x10$lang$Int$3x10$array$Array$$T$2 {}
        
        }
        
        
        
        