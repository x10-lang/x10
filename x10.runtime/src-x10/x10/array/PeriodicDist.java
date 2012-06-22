package x10.array;


@x10.core.X10Generated final public class PeriodicDist extends x10.array.Dist implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, PeriodicDist.class);
    
    public static final x10.rtt.RuntimeType<PeriodicDist> $RTT = x10.rtt.NamedType.<PeriodicDist> make(
    "x10.array.PeriodicDist", /* base class */PeriodicDist.class
    , /* parents */ new x10.rtt.Type[] {x10.array.Dist.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(PeriodicDist $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + PeriodicDist.class + " calling"); } 
        x10.array.Dist.$_deserialize_body($_obj, $deserializer);
        x10.array.Dist baseDist = (x10.array.Dist) $deserializer.readRef();
        $_obj.baseDist = baseDist;
        x10.array.Array min = (x10.array.Array) $deserializer.readRef();
        $_obj.min = min;
        $_obj.min0 = $deserializer.readInt();
        $_obj.min1 = $deserializer.readInt();
        $_obj.min2 = $deserializer.readInt();
        $_obj.min3 = $deserializer.readInt();
        x10.array.Array delta = (x10.array.Array) $deserializer.readRef();
        $_obj.delta = delta;
        $_obj.delta0 = $deserializer.readInt();
        $_obj.delta1 = $deserializer.readInt();
        $_obj.delta2 = $deserializer.readInt();
        $_obj.delta3 = $deserializer.readInt();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        PeriodicDist $_obj = new PeriodicDist((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        if (baseDist instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.baseDist);
        } else {
        $serializer.write(this.baseDist);
        }
        if (min instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.min);
        } else {
        $serializer.write(this.min);
        }
        $serializer.write(this.min0);
        $serializer.write(this.min1);
        $serializer.write(this.min2);
        $serializer.write(this.min3);
        if (delta instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.delta);
        } else {
        $serializer.write(this.delta);
        }
        $serializer.write(this.delta0);
        $serializer.write(this.delta1);
        $serializer.write(this.delta2);
        $serializer.write(this.delta3);
        
    }
    
    // constructor just for allocation
    public PeriodicDist(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public x10.array.Dist baseDist;
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public x10.array.Array<x10.core.Int> min;
        
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public int min0;
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public int min1;
        
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public int min2;
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public int min3;
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public x10.array.Array<x10.core.Int> delta;
        
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public int delta0;
        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public int delta1;
        
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public int delta2;
        
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public int delta3;
        
        
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
// creation method for java code (1-phase java constructor)
        public PeriodicDist(final x10.array.Dist base){this((java.lang.System[]) null);
                                                           $init(base);}
        
        // constructor for non-virtual call
        final public x10.array.PeriodicDist x10$array$PeriodicDist$$init$S(final x10.array.Dist base) { {
                                                                                                               
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Region t39344 =
                                                                                                                 ((x10.array.Region)(base.
                                                                                                                                       region));
                                                                                                               
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
super.$init(((x10.array.Region)(t39344)));
                                                                                                               
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"

                                                                                                               
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
this.baseDist = ((x10.array.Dist)(base));
                                                                                                               
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Region reg =
                                                                                                                 ((x10.array.Region)(base.
                                                                                                                                       region));
                                                                                                               
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t38954 =
                                                                                                                 reg.isEmpty$O();
                                                                                                               
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (t38954) {
                                                                                                                   
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38898 =
                                                                                                                     this.min3 = 0;
                                                                                                                   
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38899 =
                                                                                                                     this.min2 = t38898;
                                                                                                                   
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38900 =
                                                                                                                     this.min1 = t38899;
                                                                                                                   
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
this.min0 = t38900;
                                                                                                                   
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38901 =
                                                                                                                     this.delta3 = 0;
                                                                                                                   
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38902 =
                                                                                                                     this.delta2 = t38901;
                                                                                                                   
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38903 =
                                                                                                                     this.delta1 = t38902;
                                                                                                                   
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
this.delta0 = t38903;
                                                                                                                   
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38904 =
                                                                                                                     this.rank$O();
                                                                                                                   
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t38912 =
                                                                                                                     ((t38904) > (((int)(4))));
                                                                                                                   
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (t38912) {
                                                                                                                       
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38905 =
                                                                                                                         this.rank$O();
                                                                                                                       
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t38906 =
                                                                                                                         ((x10.core.fun.Fun_0_1)(new x10.array.PeriodicDist.$Closure$26()));
                                                                                                                       
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Array<x10.core.Int> t38907 =
                                                                                                                         ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t38905)),
                                                                                                                                                                                                                                  ((x10.core.fun.Fun_0_1)(t38906)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                                                                                                                       
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
this.min = ((x10.array.Array)(t38907));
                                                                                                                       
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38908 =
                                                                                                                         this.rank$O();
                                                                                                                       
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t38909 =
                                                                                                                         ((x10.core.fun.Fun_0_1)(new x10.array.PeriodicDist.$Closure$27()));
                                                                                                                       
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Array<x10.core.Int> t38910 =
                                                                                                                         ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t38908)),
                                                                                                                                                                                                                                  ((x10.core.fun.Fun_0_1)(t38909)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                                                                                                                       
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
this.delta = ((x10.array.Array)(t38910));
                                                                                                                   } else {
                                                                                                                       
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Array<x10.core.Int> t38911 =
                                                                                                                         ((x10.array.Array)(this.delta = null));
                                                                                                                       
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
this.min = ((x10.array.Array)(t38911));
                                                                                                                   }
                                                                                                               } else {
                                                                                                                   
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38913 =
                                                                                                                     this.rank$O();
                                                                                                                   
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t38924 =
                                                                                                                     ((t38913) > (((int)(4))));
                                                                                                                   
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (t38924) {
                                                                                                                       
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38915 =
                                                                                                                         this.rank$O();
                                                                                                                       
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t38916 =
                                                                                                                         ((x10.core.fun.Fun_0_1)(new x10.array.PeriodicDist.$Closure$28(reg)));
                                                                                                                       
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Array<x10.core.Int> tmpMin =
                                                                                                                         ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t38915)),
                                                                                                                                                                                                                                  ((x10.core.fun.Fun_0_1)(t38916)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                                                                                                                       
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
this.min = ((x10.array.Array)(tmpMin));
                                                                                                                       
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38921 =
                                                                                                                         this.rank$O();
                                                                                                                       
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t38922 =
                                                                                                                         ((x10.core.fun.Fun_0_1)(new x10.array.PeriodicDist.$Closure$29(reg,
                                                                                                                                                                                        tmpMin, (x10.array.PeriodicDist.$Closure$29.__1$1x10$lang$Int$2) null)));
                                                                                                                       
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Array<x10.core.Int> t38923 =
                                                                                                                         ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t38921)),
                                                                                                                                                                                                                                  ((x10.core.fun.Fun_0_1)(t38922)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                                                                                                                       
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
this.delta = ((x10.array.Array)(t38923));
                                                                                                                   } else {
                                                                                                                       
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
this.min = null;
                                                                                                                       
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
this.delta = null;
                                                                                                                   }
                                                                                                                   
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38925 =
                                                                                                                     reg.min$O((int)(0));
                                                                                                                   
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
this.min0 = t38925;
                                                                                                                   
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38926 =
                                                                                                                     reg.max$O((int)(0));
                                                                                                                   
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38927 =
                                                                                                                     min0;
                                                                                                                   
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38928 =
                                                                                                                     ((t38926) - (((int)(t38927))));
                                                                                                                   
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38929 =
                                                                                                                     ((t38928) + (((int)(1))));
                                                                                                                   
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
this.delta0 = t38929;
                                                                                                                   
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38930 =
                                                                                                                     this.rank$O();
                                                                                                                   
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t38937 =
                                                                                                                     ((t38930) > (((int)(1))));
                                                                                                                   
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (t38937) {
                                                                                                                       
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38931 =
                                                                                                                         reg.min$O((int)(1));
                                                                                                                       
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
this.min1 = t38931;
                                                                                                                       
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38932 =
                                                                                                                         reg.max$O((int)(1));
                                                                                                                       
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38933 =
                                                                                                                         min1;
                                                                                                                       
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38934 =
                                                                                                                         ((t38932) - (((int)(t38933))));
                                                                                                                       
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38935 =
                                                                                                                         ((t38934) + (((int)(1))));
                                                                                                                       
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
this.delta1 = t38935;
                                                                                                                   } else {
                                                                                                                       
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38936 =
                                                                                                                         this.delta1 = 0;
                                                                                                                       
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
this.min1 = t38936;
                                                                                                                   }
                                                                                                                   
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38938 =
                                                                                                                     this.rank$O();
                                                                                                                   
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t38945 =
                                                                                                                     ((t38938) > (((int)(2))));
                                                                                                                   
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (t38945) {
                                                                                                                       
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38939 =
                                                                                                                         reg.min$O((int)(2));
                                                                                                                       
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
this.min2 = t38939;
                                                                                                                       
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38940 =
                                                                                                                         reg.max$O((int)(2));
                                                                                                                       
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38941 =
                                                                                                                         min2;
                                                                                                                       
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38942 =
                                                                                                                         ((t38940) - (((int)(t38941))));
                                                                                                                       
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38943 =
                                                                                                                         ((t38942) + (((int)(1))));
                                                                                                                       
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
this.delta2 = t38943;
                                                                                                                   } else {
                                                                                                                       
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38944 =
                                                                                                                         this.delta2 = 0;
                                                                                                                       
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
this.min2 = t38944;
                                                                                                                   }
                                                                                                                   
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38946 =
                                                                                                                     this.rank$O();
                                                                                                                   
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t38953 =
                                                                                                                     ((t38946) > (((int)(3))));
                                                                                                                   
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (t38953) {
                                                                                                                       
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38947 =
                                                                                                                         reg.min$O((int)(3));
                                                                                                                       
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
this.min3 = t38947;
                                                                                                                       
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38948 =
                                                                                                                         reg.max$O((int)(3));
                                                                                                                       
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38949 =
                                                                                                                         min3;
                                                                                                                       
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38950 =
                                                                                                                         ((t38948) - (((int)(t38949))));
                                                                                                                       
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38951 =
                                                                                                                         ((t38950) + (((int)(1))));
                                                                                                                       
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
this.delta3 = t38951;
                                                                                                                   } else {
                                                                                                                       
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38952 =
                                                                                                                         this.delta3 = 0;
                                                                                                                       
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
this.min3 = t38952;
                                                                                                                   }
                                                                                                               }
                                                                                                           }
                                                                                                           return this;
                                                                                                           }
        
        // constructor
        public x10.array.PeriodicDist $init(final x10.array.Dist base){return x10$array$PeriodicDist$$init$S(base);}
        
        
        
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
private int
                                                                                                      getPeriodicIndex$O(
                                                                                                      final int index,
                                                                                                      final int dim){
            
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
int regionMin =
              0;
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38955 =
              this.rank$O();
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t38963 =
              ((t38955) < (((int)(5))));
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (t38963) {
                
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
switch (dim) {
                    
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
case 0:
                        
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38956 =
                          min0;
                        
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
regionMin = t38956;
                        
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                    
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
case 1:
                        
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38957 =
                          min1;
                        
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
regionMin = t38957;
                        
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                    
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
case 2:
                        
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38958 =
                          min2;
                        
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
regionMin = t38958;
                        
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                    
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
case 3:
                        
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38959 =
                          min3;
                        
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
regionMin = t38959;
                        
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                    
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
default:
                        
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.lang.UnsupportedOperationException t38960 =
                          ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
                        
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
throw t38960;
                }
            } else {
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Array<x10.core.Int> t38961 =
                  ((x10.array.Array)(min));
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38962 =
                  x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)t38961).$apply$G((int)(dim)));
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
regionMin = t38962;
            }
            
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
int regionDelta =
              0;
            
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38964 =
              this.rank$O();
            
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t38972 =
              ((t38964) < (((int)(5))));
            
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (t38972) {
                
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
switch (dim) {
                    
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
case 0:
                        
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38965 =
                          delta0;
                        
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
regionDelta = t38965;
                        
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                    
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
case 1:
                        
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38966 =
                          delta1;
                        
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
regionDelta = t38966;
                        
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                    
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
case 2:
                        
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38967 =
                          delta2;
                        
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
regionDelta = t38967;
                        
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                    
//#line 121 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
case 3:
                        
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38968 =
                          delta3;
                        
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
regionDelta = t38968;
                        
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                    
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
default:
                        
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.lang.UnsupportedOperationException t38969 =
                          ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
                        
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
throw t38969;
                }
            } else {
                
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Array<x10.core.Int> t38970 =
                  ((x10.array.Array)(delta));
                
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38971 =
                  x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)t38970).$apply$G((int)(dim)));
                
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
regionDelta = t38971;
            }
            
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
int actualIndex =
              index;
            
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38973 =
                  actualIndex;
                
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38974 =
                  regionMin;
                
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t38978 =
                  ((t38973) < (((int)(t38974))));
                
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t38978)) {
                    
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39345 =
                  actualIndex;
                
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39346 =
                  regionDelta;
                
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39347 =
                  ((t39345) + (((int)(t39346))));
                
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
actualIndex = t39347;
            }
            
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38981 =
                  actualIndex;
                
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38979 =
                  regionMin;
                
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38980 =
                  regionDelta;
                
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38982 =
                  ((t38979) + (((int)(t38980))));
                
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t38986 =
                  ((t38981) >= (((int)(t38982))));
                
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t38986)) {
                    
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39348 =
                  actualIndex;
                
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39349 =
                  regionDelta;
                
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39350 =
                  ((t39348) - (((int)(t39349))));
                
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
actualIndex = t39350;
            }
            
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38987 =
              actualIndex;
            
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
return t38987;
        }
        
        public static int
          getPeriodicIndex$P$O(
          final int index,
          final int dim,
          final x10.array.PeriodicDist PeriodicDist){
            return PeriodicDist.getPeriodicIndex$O((int)(index),
                                                   (int)(dim));
        }
        
        
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public x10.array.PlaceGroup
                                                                                                       places(
                                                                                                       ){
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Dist t38988 =
              ((x10.array.Dist)(baseDist));
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.PlaceGroup t38989 =
              t38988.places();
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
return t38989;
        }
        
        
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public int
                                                                                                       numPlaces$O(
                                                                                                       ){
            
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Dist t38990 =
              ((x10.array.Dist)(baseDist));
            
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38991 =
              t38990.numPlaces$O();
            
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
return t38991;
        }
        
        
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public x10.lang.Iterable<x10.array.Region>
                                                                                                       regions(
                                                                                                       ){
            
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Dist t38992 =
              ((x10.array.Dist)(baseDist));
            
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.lang.Iterable<x10.array.Region> t38993 =
              t38992.regions();
            
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
return t38993;
        }
        
        
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public x10.array.Region
                                                                                                       get(
                                                                                                       final x10.lang.Place p){
            
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Dist t38994 =
              ((x10.array.Dist)(baseDist));
            
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Region t38995 =
              ((x10.array.Region)(t38994.get(((x10.lang.Place)(p)))));
            
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
return t38995;
        }
        
        
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public x10.array.Region
                                                                                                       $apply(
                                                                                                       final x10.lang.Place p){
            
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Region t38996 =
              ((x10.array.Region)(this.get(((x10.lang.Place)(p)))));
            
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
return t38996;
        }
        
        
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public x10.lang.Place
                                                                                                       $apply(
                                                                                                       final x10.array.Point pt){
            
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38999 =
              this.rank$O();
            
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t39000 =
              ((x10.core.fun.Fun_0_1)(new x10.array.PeriodicDist.$Closure$30(this,
                                                                             pt)));
            
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Point actualPt =
              ((x10.array.Point)(x10.array.Point.make__1$1x10$lang$Int$3x10$lang$Int$2((int)(t38999),
                                                                                       ((x10.core.fun.Fun_0_1)(t39000)))));
            
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Dist t39001 =
              ((x10.array.Dist)(baseDist));
            
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.lang.Place t39002 =
              t39001.$apply(((x10.array.Point)(actualPt)));
            
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
return t39002;
        }
        
        
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public x10.lang.Place
                                                                                                       $apply(
                                                                                                       final int i0){
            
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
int a0 =
              i0;
            
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39004 =
                  a0;
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39005 =
                  min0;
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39009 =
                  ((t39004) < (((int)(t39005))));
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39009)) {
                    
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39351 =
                  a0;
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39352 =
                  delta0;
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39353 =
                  ((t39351) + (((int)(t39352))));
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a0 = t39353;
            }
            
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39012 =
                  a0;
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39010 =
                  min0;
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39011 =
                  delta0;
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39013 =
                  ((t39010) + (((int)(t39011))));
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39017 =
                  ((t39012) >= (((int)(t39013))));
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39017)) {
                    
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39354 =
                  a0;
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39355 =
                  delta0;
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39356 =
                  ((t39354) - (((int)(t39355))));
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a0 = t39356;
            }
            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Dist t39018 =
              ((x10.array.Dist)(baseDist));
            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39019 =
              a0;
            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.lang.Place t39020 =
              t39018.$apply((int)(t39019));
            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
return t39020;
        }
        
        
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public x10.lang.Place
                                                                                                       $apply(
                                                                                                       final int i0,
                                                                                                       final int i1){
            
//#line 155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
int a0 =
              i0;
            
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39022 =
                  a0;
                
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39023 =
                  min0;
                
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39027 =
                  ((t39022) < (((int)(t39023))));
                
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39027)) {
                    
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39357 =
                  a0;
                
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39358 =
                  delta0;
                
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39359 =
                  ((t39357) + (((int)(t39358))));
                
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a0 = t39359;
            }
            
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39030 =
                  a0;
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39028 =
                  min0;
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39029 =
                  delta0;
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39031 =
                  ((t39028) + (((int)(t39029))));
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39035 =
                  ((t39030) >= (((int)(t39031))));
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39035)) {
                    
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39360 =
                  a0;
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39361 =
                  delta0;
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39362 =
                  ((t39360) - (((int)(t39361))));
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a0 = t39362;
            }
            
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
int a1 =
              i1;
            
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39036 =
                  a1;
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39037 =
                  min1;
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39041 =
                  ((t39036) < (((int)(t39037))));
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39041)) {
                    
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39363 =
                  a1;
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39364 =
                  delta1;
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39365 =
                  ((t39363) + (((int)(t39364))));
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a1 = t39365;
            }
            
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39044 =
                  a1;
                
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39042 =
                  min1;
                
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39043 =
                  delta1;
                
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39045 =
                  ((t39042) + (((int)(t39043))));
                
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39049 =
                  ((t39044) >= (((int)(t39045))));
                
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39049)) {
                    
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39366 =
                  a1;
                
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39367 =
                  delta1;
                
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39368 =
                  ((t39366) - (((int)(t39367))));
                
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a1 = t39368;
            }
            
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Dist t39050 =
              ((x10.array.Dist)(baseDist));
            
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39051 =
              a0;
            
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39052 =
              a1;
            
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.lang.Place t39053 =
              t39050.$apply((int)(t39051),
                            (int)(t39052));
            
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
return t39053;
        }
        
        
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public x10.lang.Place
                                                                                                       $apply(
                                                                                                       final int i0,
                                                                                                       final int i1,
                                                                                                       final int i2){
            
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
int a0 =
              i0;
            
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39055 =
                  a0;
                
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39056 =
                  min0;
                
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39060 =
                  ((t39055) < (((int)(t39056))));
                
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39060)) {
                    
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39369 =
                  a0;
                
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39370 =
                  delta0;
                
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39371 =
                  ((t39369) + (((int)(t39370))));
                
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a0 = t39371;
            }
            
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39063 =
                  a0;
                
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39061 =
                  min0;
                
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39062 =
                  delta0;
                
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39064 =
                  ((t39061) + (((int)(t39062))));
                
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39068 =
                  ((t39063) >= (((int)(t39064))));
                
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39068)) {
                    
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39372 =
                  a0;
                
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39373 =
                  delta0;
                
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39374 =
                  ((t39372) - (((int)(t39373))));
                
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a0 = t39374;
            }
            
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
int a1 =
              i1;
            
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39069 =
                  a1;
                
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39070 =
                  min1;
                
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39074 =
                  ((t39069) < (((int)(t39070))));
                
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39074)) {
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39375 =
                  a1;
                
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39376 =
                  delta1;
                
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39377 =
                  ((t39375) + (((int)(t39376))));
                
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a1 = t39377;
            }
            
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39077 =
                  a1;
                
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39075 =
                  min1;
                
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39076 =
                  delta1;
                
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39078 =
                  ((t39075) + (((int)(t39076))));
                
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39082 =
                  ((t39077) >= (((int)(t39078))));
                
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39082)) {
                    
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39378 =
                  a1;
                
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39379 =
                  delta1;
                
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39380 =
                  ((t39378) - (((int)(t39379))));
                
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a1 = t39380;
            }
            
//#line 170 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
int a2 =
              i2;
            
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39083 =
                  a2;
                
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39084 =
                  min2;
                
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39088 =
                  ((t39083) < (((int)(t39084))));
                
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39088)) {
                    
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39381 =
                  a2;
                
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39382 =
                  delta2;
                
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39383 =
                  ((t39381) + (((int)(t39382))));
                
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a2 = t39383;
            }
            
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39091 =
                  a2;
                
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39089 =
                  min2;
                
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39090 =
                  delta2;
                
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39092 =
                  ((t39089) + (((int)(t39090))));
                
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39096 =
                  ((t39091) >= (((int)(t39092))));
                
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39096)) {
                    
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39384 =
                  a2;
                
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39385 =
                  delta2;
                
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39386 =
                  ((t39384) - (((int)(t39385))));
                
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a2 = t39386;
            }
            
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Dist t39097 =
              ((x10.array.Dist)(baseDist));
            
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39098 =
              a0;
            
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39099 =
              a1;
            
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39100 =
              a2;
            
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.lang.Place t39101 =
              t39097.$apply((int)(t39098),
                            (int)(t39099),
                            (int)(t39100));
            
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
return t39101;
        }
        
        
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public x10.lang.Place
                                                                                                       $apply(
                                                                                                       final int i0,
                                                                                                       final int i1,
                                                                                                       final int i2,
                                                                                                       final int i3){
            
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
int a0 =
              i0;
            
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39103 =
                  a0;
                
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39104 =
                  min0;
                
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39108 =
                  ((t39103) < (((int)(t39104))));
                
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39108)) {
                    
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39387 =
                  a0;
                
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39388 =
                  delta0;
                
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39389 =
                  ((t39387) + (((int)(t39388))));
                
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a0 = t39389;
            }
            
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39111 =
                  a0;
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39109 =
                  min0;
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39110 =
                  delta0;
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39112 =
                  ((t39109) + (((int)(t39110))));
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39116 =
                  ((t39111) >= (((int)(t39112))));
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39116)) {
                    
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39390 =
                  a0;
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39391 =
                  delta0;
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39392 =
                  ((t39390) - (((int)(t39391))));
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a0 = t39392;
            }
            
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
int a1 =
              i1;
            
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39117 =
                  a1;
                
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39118 =
                  min1;
                
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39122 =
                  ((t39117) < (((int)(t39118))));
                
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39122)) {
                    
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39393 =
                  a1;
                
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39394 =
                  delta1;
                
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39395 =
                  ((t39393) + (((int)(t39394))));
                
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a1 = t39395;
            }
            
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39125 =
                  a1;
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39123 =
                  min1;
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39124 =
                  delta1;
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39126 =
                  ((t39123) + (((int)(t39124))));
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39130 =
                  ((t39125) >= (((int)(t39126))));
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39130)) {
                    
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39396 =
                  a1;
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39397 =
                  delta1;
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39398 =
                  ((t39396) - (((int)(t39397))));
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a1 = t39398;
            }
            
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
int a2 =
              i2;
            
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39131 =
                  a2;
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39132 =
                  min2;
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39136 =
                  ((t39131) < (((int)(t39132))));
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39136)) {
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39399 =
                  a2;
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39400 =
                  delta2;
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39401 =
                  ((t39399) + (((int)(t39400))));
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a2 = t39401;
            }
            
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39139 =
                  a2;
                
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39137 =
                  min2;
                
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39138 =
                  delta2;
                
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39140 =
                  ((t39137) + (((int)(t39138))));
                
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39144 =
                  ((t39139) >= (((int)(t39140))));
                
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39144)) {
                    
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39402 =
                  a2;
                
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39403 =
                  delta2;
                
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39404 =
                  ((t39402) - (((int)(t39403))));
                
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a2 = t39404;
            }
            
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
int a3 =
              i3;
            
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39145 =
                  a3;
                
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39146 =
                  min3;
                
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39150 =
                  ((t39145) < (((int)(t39146))));
                
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39150)) {
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39405 =
                  a3;
                
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39406 =
                  delta3;
                
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39407 =
                  ((t39405) + (((int)(t39406))));
                
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a3 = t39407;
            }
            
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39153 =
                  a3;
                
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39151 =
                  min3;
                
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39152 =
                  delta3;
                
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39154 =
                  ((t39151) + (((int)(t39152))));
                
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39158 =
                  ((t39153) >= (((int)(t39154))));
                
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39158)) {
                    
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39408 =
                  a3;
                
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39409 =
                  delta3;
                
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39410 =
                  ((t39408) - (((int)(t39409))));
                
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a3 = t39410;
            }
            
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Dist t39159 =
              ((x10.array.Dist)(baseDist));
            
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39160 =
              a0;
            
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39161 =
              a1;
            
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39162 =
              a2;
            
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39163 =
              a3;
            
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.lang.Place t39164 =
              t39159.$apply((int)(t39160),
                            (int)(t39161),
                            (int)(t39162),
                            (int)(t39163));
            
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
return t39164;
        }
        
        
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public int
                                                                                                       offset$O(
                                                                                                       final x10.array.Point pt){
            
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39167 =
              this.rank$O();
            
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t39168 =
              ((x10.core.fun.Fun_0_1)(new x10.array.PeriodicDist.$Closure$31(this,
                                                                             pt)));
            
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Point actualPt =
              ((x10.array.Point)(x10.array.Point.make__1$1x10$lang$Int$3x10$lang$Int$2((int)(t39167),
                                                                                       ((x10.core.fun.Fun_0_1)(t39168)))));
            
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Dist t39169 =
              ((x10.array.Dist)(baseDist));
            
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39170 =
              t39169.offset$O(((x10.array.Point)(actualPt)));
            
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
return t39170;
        }
        
        
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public int
                                                                                                       offset$O(
                                                                                                       final int i0){
            
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
int a0 =
              i0;
            
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39172 =
                  a0;
                
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39173 =
                  min0;
                
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39177 =
                  ((t39172) < (((int)(t39173))));
                
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39177)) {
                    
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39411 =
                  a0;
                
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39412 =
                  delta0;
                
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39413 =
                  ((t39411) + (((int)(t39412))));
                
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a0 = t39413;
            }
            
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39180 =
                  a0;
                
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39178 =
                  min0;
                
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39179 =
                  delta0;
                
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39181 =
                  ((t39178) + (((int)(t39179))));
                
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39185 =
                  ((t39180) >= (((int)(t39181))));
                
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39185)) {
                    
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39414 =
                  a0;
                
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39415 =
                  delta0;
                
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39416 =
                  ((t39414) - (((int)(t39415))));
                
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a0 = t39416;
            }
            
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Dist t39186 =
              ((x10.array.Dist)(baseDist));
            
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39187 =
              a0;
            
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39188 =
              t39186.offset$O((int)(t39187));
            
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
return t39188;
        }
        
        
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public int
                                                                                                       offset$O(
                                                                                                       final int i0,
                                                                                                       final int i1){
            
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
int a0 =
              i0;
            
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39190 =
                  a0;
                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39191 =
                  min0;
                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39195 =
                  ((t39190) < (((int)(t39191))));
                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39195)) {
                    
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39417 =
                  a0;
                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39418 =
                  delta0;
                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39419 =
                  ((t39417) + (((int)(t39418))));
                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a0 = t39419;
            }
            
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39198 =
                  a0;
                
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39196 =
                  min0;
                
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39197 =
                  delta0;
                
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39199 =
                  ((t39196) + (((int)(t39197))));
                
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39203 =
                  ((t39198) >= (((int)(t39199))));
                
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39203)) {
                    
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39420 =
                  a0;
                
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39421 =
                  delta0;
                
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39422 =
                  ((t39420) - (((int)(t39421))));
                
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a0 = t39422;
            }
            
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
int a1 =
              i1;
            
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39204 =
                  a1;
                
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39205 =
                  min1;
                
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39209 =
                  ((t39204) < (((int)(t39205))));
                
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39209)) {
                    
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39423 =
                  a1;
                
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39424 =
                  delta1;
                
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39425 =
                  ((t39423) + (((int)(t39424))));
                
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a1 = t39425;
            }
            
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39212 =
                  a1;
                
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39210 =
                  min1;
                
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39211 =
                  delta1;
                
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39213 =
                  ((t39210) + (((int)(t39211))));
                
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39217 =
                  ((t39212) >= (((int)(t39213))));
                
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39217)) {
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39426 =
                  a1;
                
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39427 =
                  delta1;
                
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39428 =
                  ((t39426) - (((int)(t39427))));
                
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a1 = t39428;
            }
            
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Dist t39218 =
              ((x10.array.Dist)(baseDist));
            
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39219 =
              a0;
            
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39220 =
              a1;
            
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39221 =
              t39218.offset$O((int)(t39219),
                              (int)(t39220));
            
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
return t39221;
        }
        
        
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public int
                                                                                                       offset$O(
                                                                                                       final int i0,
                                                                                                       final int i1,
                                                                                                       final int i2){
            
//#line 211 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
int a0 =
              i0;
            
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39223 =
                  a0;
                
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39224 =
                  min0;
                
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39228 =
                  ((t39223) < (((int)(t39224))));
                
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39228)) {
                    
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39429 =
                  a0;
                
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39430 =
                  delta0;
                
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39431 =
                  ((t39429) + (((int)(t39430))));
                
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a0 = t39431;
            }
            
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39231 =
                  a0;
                
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39229 =
                  min0;
                
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39230 =
                  delta0;
                
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39232 =
                  ((t39229) + (((int)(t39230))));
                
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39236 =
                  ((t39231) >= (((int)(t39232))));
                
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39236)) {
                    
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39432 =
                  a0;
                
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39433 =
                  delta0;
                
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39434 =
                  ((t39432) - (((int)(t39433))));
                
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a0 = t39434;
            }
            
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
int a1 =
              i1;
            
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39237 =
                  a1;
                
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39238 =
                  min1;
                
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39242 =
                  ((t39237) < (((int)(t39238))));
                
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39242)) {
                    
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39435 =
                  a1;
                
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39436 =
                  delta1;
                
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39437 =
                  ((t39435) + (((int)(t39436))));
                
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a1 = t39437;
            }
            
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39245 =
                  a1;
                
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39243 =
                  min1;
                
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39244 =
                  delta1;
                
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39246 =
                  ((t39243) + (((int)(t39244))));
                
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39250 =
                  ((t39245) >= (((int)(t39246))));
                
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39250)) {
                    
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39438 =
                  a1;
                
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39439 =
                  delta1;
                
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39440 =
                  ((t39438) - (((int)(t39439))));
                
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a1 = t39440;
            }
            
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
int a2 =
              i2;
            
//#line 218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39251 =
                  a2;
                
//#line 218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39252 =
                  min2;
                
//#line 218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39256 =
                  ((t39251) < (((int)(t39252))));
                
//#line 218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39256)) {
                    
//#line 218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39441 =
                  a2;
                
//#line 218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39442 =
                  delta2;
                
//#line 218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39443 =
                  ((t39441) + (((int)(t39442))));
                
//#line 218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a2 = t39443;
            }
            
//#line 219 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 219 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39259 =
                  a2;
                
//#line 219 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39257 =
                  min2;
                
//#line 219 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39258 =
                  delta2;
                
//#line 219 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39260 =
                  ((t39257) + (((int)(t39258))));
                
//#line 219 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39264 =
                  ((t39259) >= (((int)(t39260))));
                
//#line 219 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39264)) {
                    
//#line 219 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 219 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39444 =
                  a2;
                
//#line 219 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39445 =
                  delta2;
                
//#line 219 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39446 =
                  ((t39444) - (((int)(t39445))));
                
//#line 219 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a2 = t39446;
            }
            
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Dist t39265 =
              ((x10.array.Dist)(baseDist));
            
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39266 =
              a0;
            
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39267 =
              a1;
            
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39268 =
              a2;
            
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39269 =
              t39265.offset$O((int)(t39266),
                              (int)(t39267),
                              (int)(t39268));
            
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
return t39269;
        }
        
        
//#line 222 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public int
                                                                                                       offset$O(
                                                                                                       final int i0,
                                                                                                       final int i1,
                                                                                                       final int i2,
                                                                                                       final int i3){
            
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
int a0 =
              i0;
            
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39271 =
                  a0;
                
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39272 =
                  min0;
                
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39276 =
                  ((t39271) < (((int)(t39272))));
                
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39276)) {
                    
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39447 =
                  a0;
                
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39448 =
                  delta0;
                
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39449 =
                  ((t39447) + (((int)(t39448))));
                
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a0 = t39449;
            }
            
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39279 =
                  a0;
                
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39277 =
                  min0;
                
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39278 =
                  delta0;
                
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39280 =
                  ((t39277) + (((int)(t39278))));
                
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39284 =
                  ((t39279) >= (((int)(t39280))));
                
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39284)) {
                    
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39450 =
                  a0;
                
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39451 =
                  delta0;
                
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39452 =
                  ((t39450) - (((int)(t39451))));
                
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a0 = t39452;
            }
            
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
int a1 =
              i1;
            
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39285 =
                  a1;
                
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39286 =
                  min1;
                
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39290 =
                  ((t39285) < (((int)(t39286))));
                
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39290)) {
                    
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39453 =
                  a1;
                
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39454 =
                  delta1;
                
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39455 =
                  ((t39453) + (((int)(t39454))));
                
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a1 = t39455;
            }
            
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39293 =
                  a1;
                
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39291 =
                  min1;
                
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39292 =
                  delta1;
                
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39294 =
                  ((t39291) + (((int)(t39292))));
                
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39298 =
                  ((t39293) >= (((int)(t39294))));
                
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39298)) {
                    
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39456 =
                  a1;
                
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39457 =
                  delta1;
                
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39458 =
                  ((t39456) - (((int)(t39457))));
                
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a1 = t39458;
            }
            
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
int a2 =
              i2;
            
//#line 230 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 230 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39299 =
                  a2;
                
//#line 230 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39300 =
                  min2;
                
//#line 230 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39304 =
                  ((t39299) < (((int)(t39300))));
                
//#line 230 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39304)) {
                    
//#line 230 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 230 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39459 =
                  a2;
                
//#line 230 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39460 =
                  delta2;
                
//#line 230 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39461 =
                  ((t39459) + (((int)(t39460))));
                
//#line 230 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a2 = t39461;
            }
            
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39307 =
                  a2;
                
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39305 =
                  min2;
                
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39306 =
                  delta2;
                
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39308 =
                  ((t39305) + (((int)(t39306))));
                
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39312 =
                  ((t39307) >= (((int)(t39308))));
                
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39312)) {
                    
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39462 =
                  a2;
                
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39463 =
                  delta2;
                
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39464 =
                  ((t39462) - (((int)(t39463))));
                
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a2 = t39464;
            }
            
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
int a3 =
              i3;
            
//#line 233 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 233 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39313 =
                  a3;
                
//#line 233 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39314 =
                  min3;
                
//#line 233 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39318 =
                  ((t39313) < (((int)(t39314))));
                
//#line 233 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39318)) {
                    
//#line 233 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 233 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39465 =
                  a3;
                
//#line 233 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39466 =
                  delta3;
                
//#line 233 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39467 =
                  ((t39465) + (((int)(t39466))));
                
//#line 233 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a3 = t39467;
            }
            
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
while (true) {
                
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39321 =
                  a3;
                
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39319 =
                  min3;
                
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39320 =
                  delta3;
                
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39322 =
                  ((t39319) + (((int)(t39320))));
                
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final boolean t39326 =
                  ((t39321) >= (((int)(t39322))));
                
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
if (!(t39326)) {
                    
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
break;
                }
                
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39468 =
                  a3;
                
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39469 =
                  delta3;
                
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39470 =
                  ((t39468) - (((int)(t39469))));
                
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
a3 = t39470;
            }
            
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Dist t39327 =
              ((x10.array.Dist)(baseDist));
            
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39328 =
              a0;
            
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39329 =
              a1;
            
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39330 =
              a2;
            
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39331 =
              a3;
            
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39332 =
              t39327.offset$O((int)(t39328),
                              (int)(t39329),
                              (int)(t39330),
                              (int)(t39331));
            
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
return t39332;
        }
        
        
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public int
                                                                                                       maxOffset$O(
                                                                                                       ){
            
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Dist t39333 =
              ((x10.array.Dist)(baseDist));
            
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39334 =
              t39333.maxOffset$O();
            
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
return t39334;
        }
        
        
//#line 239 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public x10.array.Dist
                                                                                                       restriction(
                                                                                                       final x10.array.Region r){
            
//#line 239 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Dist t39335 =
              ((x10.array.Dist)(baseDist));
            
//#line 239 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Dist t39336 =
              ((x10.array.Dist)(t39335.restriction(((x10.array.Region)(r)))));
            
//#line 239 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.PeriodicDist t39337 =
              ((x10.array.PeriodicDist)(new x10.array.PeriodicDist((java.lang.System[]) null).$init(((x10.array.Dist)(t39336)))));
            
//#line 239 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
return t39337;
        }
        
        
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public x10.array.Dist
                                                                                                       restriction(
                                                                                                       final x10.lang.Place p){
            
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Dist t39338 =
              ((x10.array.Dist)(baseDist));
            
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Dist t39339 =
              ((x10.array.Dist)(t39338.restriction(((x10.lang.Place)(p)))));
            
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.PeriodicDist t39340 =
              ((x10.array.PeriodicDist)(new x10.array.PeriodicDist((java.lang.System[]) null).$init(((x10.array.Dist)(t39339)))));
            
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
return t39340;
        }
        
        
//#line 242 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
public java.lang.String
                                                                                                       toString(
                                                                                                       ){
            
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final x10.array.Dist t39341 =
              ((x10.array.Dist)(baseDist));
            
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final java.lang.String t39342 =
              t39341.toString();
            
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final java.lang.String t39343 =
              (("Periodic: ") + (t39342));
            
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
return t39343;
        }
        
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final public x10.array.PeriodicDist
                                                                                                      x10$array$PeriodicDist$$x10$array$PeriodicDist$this(
                                                                                                      ){
            
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
return x10.array.PeriodicDist.this;
        }
        
        @x10.core.X10Generated public static class $Closure$26 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$26.class);
            
            public static final x10.rtt.RuntimeType<$Closure$26> $RTT = x10.rtt.StaticFunType.<$Closure$26> make(
            /* base class */$Closure$26.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$26 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$26.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$26 $_obj = new $Closure$26((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public $Closure$26(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int id$54){
                    
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
return 0;
                }
                
                public $Closure$26() { {
                                              
                                          }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$27 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$27.class);
            
            public static final x10.rtt.RuntimeType<$Closure$27> $RTT = x10.rtt.StaticFunType.<$Closure$27> make(
            /* base class */$Closure$27.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$27 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$27.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$27 $_obj = new $Closure$27((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public $Closure$27(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int id$55){
                    
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
return 0;
                }
                
                public $Closure$27() { {
                                              
                                          }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$28 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$28.class);
            
            public static final x10.rtt.RuntimeType<$Closure$28> $RTT = x10.rtt.StaticFunType.<$Closure$28> make(
            /* base class */$Closure$28.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$28 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$28.class + " calling"); } 
                x10.array.Region reg = (x10.array.Region) $deserializer.readRef();
                $_obj.reg = reg;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$28 $_obj = new $Closure$28((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (reg instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.reg);
                } else {
                $serializer.write(this.reg);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$28(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38914 =
                      this.
                        reg.min$O((int)(i));
                    
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
return t38914;
                }
                
                public x10.array.Region reg;
                
                public $Closure$28(final x10.array.Region reg) { {
                                                                        this.reg = ((x10.array.Region)(reg));
                                                                    }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$29 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$29.class);
            
            public static final x10.rtt.RuntimeType<$Closure$29> $RTT = x10.rtt.StaticFunType.<$Closure$29> make(
            /* base class */$Closure$29.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$29 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$29.class + " calling"); } 
                x10.array.Region reg = (x10.array.Region) $deserializer.readRef();
                $_obj.reg = reg;
                x10.array.Array tmpMin = (x10.array.Array) $deserializer.readRef();
                $_obj.tmpMin = tmpMin;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$29 $_obj = new $Closure$29((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (reg instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.reg);
                } else {
                $serializer.write(this.reg);
                }
                if (tmpMin instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.tmpMin);
                } else {
                $serializer.write(this.tmpMin);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$29(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38917 =
                      this.
                        reg.max$O((int)(i));
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38918 =
                      x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)this.
                                                                            tmpMin).$apply$G((int)(i)));
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38919 =
                      ((t38917) - (((int)(t38918))));
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38920 =
                      ((t38919) + (((int)(1))));
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
return t38920;
                }
                
                public x10.array.Region reg;
                public x10.array.Array<x10.core.Int> tmpMin;
                
                public $Closure$29(final x10.array.Region reg,
                                   final x10.array.Array<x10.core.Int> tmpMin, __1$1x10$lang$Int$2 $dummy) { {
                                                                                                                    this.reg = ((x10.array.Region)(reg));
                                                                                                                    this.tmpMin = ((x10.array.Array)(tmpMin));
                                                                                                                }}
                // synthetic type for parameter mangling
                public abstract static class __1$1x10$lang$Int$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$30 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$30.class);
            
            public static final x10.rtt.RuntimeType<$Closure$30> $RTT = x10.rtt.StaticFunType.<$Closure$30> make(
            /* base class */$Closure$30.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$30 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$30.class + " calling"); } 
                x10.array.PeriodicDist out$$ = (x10.array.PeriodicDist) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.array.Point pt = (x10.array.Point) $deserializer.readRef();
                $_obj.pt = pt;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$30 $_obj = new $Closure$30((java.lang.System[]) null);
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
                if (pt instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.pt);
                } else {
                $serializer.write(this.pt);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$30(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38997 =
                      this.
                        pt.$apply$O((int)(i));
                    
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t38998 =
                      this.
                        out$$.getPeriodicIndex$O((int)(t38997),
                                                 (int)(i));
                    
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
return t38998;
                }
                
                public x10.array.PeriodicDist out$$;
                public x10.array.Point pt;
                
                public $Closure$30(final x10.array.PeriodicDist out$$,
                                   final x10.array.Point pt) { {
                                                                      this.out$$ = out$$;
                                                                      this.pt = ((x10.array.Point)(pt));
                                                                  }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$31 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$31.class);
            
            public static final x10.rtt.RuntimeType<$Closure$31> $RTT = x10.rtt.StaticFunType.<$Closure$31> make(
            /* base class */$Closure$31.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$31 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$31.class + " calling"); } 
                x10.array.PeriodicDist out$$ = (x10.array.PeriodicDist) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.array.Point pt = (x10.array.Point) $deserializer.readRef();
                $_obj.pt = pt;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$31 $_obj = new $Closure$31((java.lang.System[]) null);
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
                if (pt instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.pt);
                } else {
                $serializer.write(this.pt);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$31(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39165 =
                      this.
                        pt.$apply$O((int)(i));
                    
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
final int t39166 =
                      this.
                        out$$.getPeriodicIndex$O((int)(t39165),
                                                 (int)(i));
                    
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PeriodicDist.x10"
return t39166;
                }
                
                public x10.array.PeriodicDist out$$;
                public x10.array.Point pt;
                
                public $Closure$31(final x10.array.PeriodicDist out$$,
                                   final x10.array.Point pt) { {
                                                                      this.out$$ = out$$;
                                                                      this.pt = ((x10.array.Point)(pt));
                                                                  }}
                
            }
            
        
        }
        