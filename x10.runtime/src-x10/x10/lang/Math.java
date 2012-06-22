package x10.lang;


@x10.core.X10Generated final public class Math extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Math.class);
    
    public static final x10.rtt.RuntimeType<Math> $RTT = x10.rtt.NamedType.<Math> make(
    "x10.lang.Math", /* base class */Math.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Math $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Math.class + " calling"); } 
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Math $_obj = new Math((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        
    }
    
    // constructor just for allocation
    public Math(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final public static double E = 2.718281828459045;
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final public static double PI = 3.141592653589793;
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static int
                                                                                             abs$O(
                                                                                             final int a){
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t54988 =
              ((a) < (((int)(0))));
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
int t54989 =
               0;
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t54988) {
                
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t54989 = (-(a));
            } else {
                
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t54989 = a;
            }
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final int t54990 =
              t54989;
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t54990;
        }
        
        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static long
                                                                                             abs$O(
                                                                                             final long a){
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t54991 =
              ((a) < (((long)(0L))));
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
long t54992 =
               0;
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t54991) {
                
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t54992 = (-(a));
            } else {
                
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t54992 = a;
            }
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final long t54993 =
              t54992;
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t54993;
        }
        
        
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static float
                                                                                             abs$O(
                                                                                             final float a){
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t54994 =
              ((a) < (((float)(0.0F))));
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
float t54995 =
               0;
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t54994) {
                
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t54995 = (-(a));
            } else {
                
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t54995 = a;
            }
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final float t54996 =
              t54995;
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t54996;
        }
        
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                             abs$O(
                                                                                             final double a){
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t54997 =
              ((a) < (((double)(0.0))));
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
double t54998 =
               0;
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t54997) {
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t54998 = (-(a));
            } else {
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t54998 = a;
            }
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t54999 =
              t54998;
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t54999;
        }
        
        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                             ceil$O(
                                                                                             final double a){try {return java.lang.Math.ceil(a);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                             floor$O(
                                                                                             final double a){try {return java.lang.Math.floor(a);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                             round$O(
                                                                                             final double a){try {return java.lang.Math.round(a);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static int
                                                                                             getExponent$O(
                                                                                             final float a){try {return java.lang.Math.getExponent(a);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static int
                                                                                             getExponent$O(
                                                                                             final double a){try {return java.lang.Math.getExponent(a);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static float
                                                                                             powf$O(
                                                                                             final float a,
                                                                                             final float b){try {return (float)java.lang.Math.pow(a, b);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                             pow$O(
                                                                                             final double a,
                                                                                             final double b){try {return java.lang.Math.pow(a, b);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static x10.lang.Complex
                                                                                             pow(
                                                                                             final x10.lang.Complex a,
                                                                                             final x10.lang.Complex b){
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55000 =
              x10.lang.Math.log(((x10.lang.Complex)(a)));
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55001 =
              t55000.$times(((x10.lang.Complex)(b)));
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55002 =
              x10.lang.Math.exp(((x10.lang.Complex)(t55001)));
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55002;
        }
        
        
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                             exp$O(
                                                                                             final double a){try {return java.lang.Math.exp(a);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static float
                                                                                             expf$O(
                                                                                             final float a){try {return (float)java.lang.Math.exp(a);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static x10.lang.Complex
                                                                                             exp(
                                                                                             final x10.lang.Complex a){
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55004 =
              a.isNaN$O();
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55004) {
                
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55003 =
                  ((x10.lang.Complex)(x10.lang.Complex.getInitialized$NaN()));
                
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55003;
            }
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55005 =
              a.
                re;
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double expRe =
              java.lang.Math.exp(((double)(t55005)));
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55006 =
              a.
                im;
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55007 =
              java.lang.Math.cos(((double)(t55006)));
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55010 =
              ((expRe) * (((double)(t55007))));
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55008 =
              a.
                im;
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55009 =
              java.lang.Math.sin(((double)(t55008)));
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55011 =
              ((expRe) * (((double)(t55009))));
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55012 =
              new x10.lang.Complex((java.lang.System[]) null).$init(t55010,
                                                                    t55011);
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55012;
        }
        
        
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                             expm1$O(
                                                                                             final double a){try {return java.lang.Math.expm1(a);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                             cos$O(
                                                                                             final double a){try {return java.lang.Math.cos(a);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static x10.lang.Complex
                                                                                              cos(
                                                                                              final x10.lang.Complex z){
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55013 =
              z.
                im;
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55029 =
              ((double) t55013) ==
            ((double) 0.0);
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55029) {
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55014 =
                  z.
                    re;
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55015 =
                  java.lang.Math.cos(((double)(t55014)));
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55016 =
                  new x10.lang.Complex((java.lang.System[]) null).$init(t55015,
                                                                        ((double)(0.0)));
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55016;
            } else {
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55017 =
                  z.
                    re;
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55019 =
                  java.lang.Math.cos(((double)(t55017)));
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55018 =
                  z.
                    im;
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55020 =
                  java.lang.Math.cosh(((double)(t55018)));
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55026 =
                  ((t55019) * (((double)(t55020))));
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55021 =
                  z.
                    re;
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55024 =
                  java.lang.Math.sin(((double)(t55021)));
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55022 =
                  z.
                    im;
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55023 =
                  (-(t55022));
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55025 =
                  java.lang.Math.sinh(((double)(t55023)));
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55027 =
                  ((t55024) * (((double)(t55025))));
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55028 =
                  new x10.lang.Complex((java.lang.System[]) null).$init(t55026,
                                                                        t55027);
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55028;
            }
        }
        
        
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                              sin$O(
                                                                                              final double a){try {return java.lang.Math.sin(a);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 121 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static x10.lang.Complex
                                                                                              sin(
                                                                                              final x10.lang.Complex z){
            
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55030 =
              z.
                im;
            
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55045 =
              ((double) t55030) ==
            ((double) 0.0);
            
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55045) {
                
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55031 =
                  z.
                    re;
                
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55032 =
                  java.lang.Math.sin(((double)(t55031)));
                
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55033 =
                  new x10.lang.Complex((java.lang.System[]) null).$init(t55032,
                                                                        ((double)(0.0)));
                
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55033;
            } else {
                
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55034 =
                  z.
                    re;
                
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55036 =
                  java.lang.Math.sin(((double)(t55034)));
                
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55035 =
                  z.
                    im;
                
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55037 =
                  java.lang.Math.cosh(((double)(t55035)));
                
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55042 =
                  ((t55036) * (((double)(t55037))));
                
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55038 =
                  z.
                    re;
                
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55040 =
                  java.lang.Math.cos(((double)(t55038)));
                
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55039 =
                  z.
                    im;
                
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55041 =
                  java.lang.Math.sinh(((double)(t55039)));
                
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55043 =
                  ((t55040) * (((double)(t55041))));
                
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55044 =
                  new x10.lang.Complex((java.lang.System[]) null).$init(t55042,
                                                                        t55043);
                
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55044;
            }
        }
        
        
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                              tan$O(
                                                                                              final double a){try {return java.lang.Math.tan(a);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static x10.lang.Complex
                                                                                              tan(
                                                                                              final x10.lang.Complex z){
            
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55046 =
              z.
                im;
            
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55058 =
              ((double) t55046) ==
            ((double) 0.0);
            
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55058) {
                
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55047 =
                  z.
                    re;
                
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55048 =
                  java.lang.Math.tan(((double)(t55047)));
                
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55049 =
                  new x10.lang.Complex((java.lang.System[]) null).$init(t55048,
                                                                        ((double)(0.0)));
                
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55049;
            } else {
                
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55050 =
                  ((x10.lang.Complex)(x10.lang.Complex.getInitialized$I()));
                
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55051 =
                  x10.lang.Complex.$times((double)(2.0),
                                          ((x10.lang.Complex)(t55050)));
                
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55052 =
                  t55051.$times(((x10.lang.Complex)(z)));
                
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex e2IZ =
                  x10.lang.Math.exp(((x10.lang.Complex)(t55052)));
                
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55055 =
                  e2IZ.$minus((double)(1.0));
                
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55053 =
                  ((x10.lang.Complex)(x10.lang.Complex.getInitialized$I()));
                
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55054 =
                  e2IZ.$plus((double)(1.0));
                
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55056 =
                  t55053.$times(((x10.lang.Complex)(t55054)));
                
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55057 =
                  t55055.$over(((x10.lang.Complex)(t55056)));
                
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55057;
            }
        }
        
        
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                              acos$O(
                                                                                              final double a){try {return java.lang.Math.acos(a);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static x10.lang.Complex
                                                                                              acos(
                                                                                              final x10.lang.Complex z){
            
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55059 =
              z.
                im;
            
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
boolean t55062 =
              ((double) t55059) ==
            ((double) 0.0);
            
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55062) {
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55060 =
                  z.
                    re;
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55061 =
                  x10.lang.Math.abs$O((double)(t55060));
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55062 = ((t55061) <= (((double)(1.0))));
            }
            
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55078 =
              t55062;
            
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55078) {
                
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55063 =
                  z.
                    re;
                
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55064 =
                  java.lang.Math.acos(((double)(t55063)));
                
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55065 =
                  new x10.lang.Complex((java.lang.System[]) null).$init(t55064,
                                                                        ((double)(0.0)));
                
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55065;
            } else {
                
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55066 =
                  x10.lang.Math.PI;
                
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55075 =
                  ((t55066) / (((double)(2.0))));
                
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55073 =
                  ((x10.lang.Complex)(x10.lang.Complex.getInitialized$I()));
                
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55067 =
                  ((x10.lang.Complex)(x10.lang.Complex.getInitialized$I()));
                
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55070 =
                  t55067.$times(((x10.lang.Complex)(z)));
                
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55068 =
                  z.$times(((x10.lang.Complex)(z)));
                
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55069 =
                  x10.lang.Complex.$minus((double)(1.0),
                                          ((x10.lang.Complex)(t55068)));
                
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55071 =
                  x10.lang.Math.sqrt(((x10.lang.Complex)(t55069)));
                
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55072 =
                  t55070.$plus(((x10.lang.Complex)(t55071)));
                
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55074 =
                  x10.lang.Math.log(((x10.lang.Complex)(t55072)));
                
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55076 =
                  t55073.$times(((x10.lang.Complex)(t55074)));
                
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55077 =
                  x10.lang.Complex.$plus((double)(t55075),
                                         ((x10.lang.Complex)(t55076)));
                
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55077;
            }
        }
        
        
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                              asin$O(
                                                                                              final double a){try {return java.lang.Math.asin(a);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static x10.lang.Complex
                                                                                              asin(
                                                                                              final x10.lang.Complex z){
            
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55079 =
              z.
                im;
            
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
boolean t55082 =
              ((double) t55079) ==
            ((double) 0.0);
            
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55082) {
                
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55080 =
                  z.
                    re;
                
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55081 =
                  x10.lang.Math.abs$O((double)(t55080));
                
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55082 = ((t55081) <= (((double)(1.0))));
            }
            
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55096 =
              t55082;
            
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55096) {
                
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55083 =
                  z.
                    re;
                
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55084 =
                  java.lang.Math.asin(((double)(t55083)));
                
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55085 =
                  new x10.lang.Complex((java.lang.System[]) null).$init(t55084,
                                                                        ((double)(0.0)));
                
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55085;
            } else {
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55086 =
                  ((x10.lang.Complex)(x10.lang.Complex.getInitialized$I()));
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55093 =
                  t55086.$minus();
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55087 =
                  ((x10.lang.Complex)(x10.lang.Complex.getInitialized$I()));
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55090 =
                  t55087.$times(((x10.lang.Complex)(z)));
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55088 =
                  z.$times(((x10.lang.Complex)(z)));
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55089 =
                  x10.lang.Complex.$minus((double)(1.0),
                                          ((x10.lang.Complex)(t55088)));
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55091 =
                  x10.lang.Math.sqrt(((x10.lang.Complex)(t55089)));
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55092 =
                  t55090.$plus(((x10.lang.Complex)(t55091)));
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55094 =
                  x10.lang.Math.log(((x10.lang.Complex)(t55092)));
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55095 =
                  t55093.$times(((x10.lang.Complex)(t55094)));
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55095;
            }
        }
        
        
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                              atan$O(
                                                                                              final double a){try {return java.lang.Math.atan(a);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static x10.lang.Complex
                                                                                              atan(
                                                                                              final x10.lang.Complex z){
            
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55097 =
              z.
                im;
            
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55122 =
              ((double) t55097) ==
            ((double) 0.0);
            
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55122) {
                
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55098 =
                  z.
                    re;
                
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55099 =
                  java.lang.Math.atan(((double)(t55098)));
                
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55100 =
                  new x10.lang.Complex((java.lang.System[]) null).$init(t55099,
                                                                        ((double)(0.0)));
                
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55100;
            } else {
                
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55101 =
                  ((x10.lang.Complex)(x10.lang.Complex.getInitialized$I()));
                
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55121 =
                  x10.rtt.Equality.equalsequals((z),(t55101));
                
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55121) {
                    
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55102 =
                      java.lang.Double.POSITIVE_INFINITY;
                    
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55103 =
                      new x10.lang.Complex((java.lang.System[]) null).$init(((double)(0.0)),
                                                                            ((double)(t55102)));
                    
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55103;
                } else {
                    
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55104 =
                      ((x10.lang.Complex)(x10.lang.Complex.getInitialized$I()));
                    
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55105 =
                      t55104.$minus();
                    
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55120 =
                      x10.rtt.Equality.equalsequals((z),(t55105));
                    
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55120) {
                        
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55106 =
                          java.lang.Double.NEGATIVE_INFINITY;
                        
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55107 =
                          new x10.lang.Complex((java.lang.System[]) null).$init(((double)(0.0)),
                                                                                ((double)(t55106)));
                        
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55107;
                    } else {
                        
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55108 =
                          ((x10.lang.Complex)(x10.lang.Complex.getInitialized$I()));
                        
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55117 =
                          t55108.$over((double)(2.0));
                        
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55109 =
                          ((x10.lang.Complex)(x10.lang.Complex.getInitialized$I()));
                        
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55110 =
                          t55109.$times(((x10.lang.Complex)(z)));
                        
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55111 =
                          x10.lang.Complex.$minus((double)(1.0),
                                                  ((x10.lang.Complex)(t55110)));
                        
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55115 =
                          x10.lang.Math.log(((x10.lang.Complex)(t55111)));
                        
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55112 =
                          ((x10.lang.Complex)(x10.lang.Complex.getInitialized$I()));
                        
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55113 =
                          t55112.$times(((x10.lang.Complex)(z)));
                        
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55114 =
                          x10.lang.Complex.$plus((double)(1.0),
                                                 ((x10.lang.Complex)(t55113)));
                        
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55116 =
                          x10.lang.Math.log(((x10.lang.Complex)(t55114)));
                        
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55118 =
                          t55115.$minus(((x10.lang.Complex)(t55116)));
                        
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55119 =
                          t55117.$times(((x10.lang.Complex)(t55118)));
                        
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55119;
                    }
                }
            }
        }
        
        
//#line 211 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                              atan2$O(
                                                                                              final double a,
                                                                                              final double b){try {return java.lang.Math.atan2(a,b);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                              cosh$O(
                                                                                              final double a){try {return java.lang.Math.cosh(a);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static x10.lang.Complex
                                                                                              cosh(
                                                                                              final x10.lang.Complex z){
            
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55140 =
              z.isNaN$O();
            
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55140) {
                
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55123 =
                  ((x10.lang.Complex)(x10.lang.Complex.getInitialized$NaN()));
                
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55123;
            } else {
                
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55124 =
                  z.
                    im;
                
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55139 =
                  ((double) t55124) ==
                ((double) 0.0);
                
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55139) {
                    
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55125 =
                      z.
                        re;
                    
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55126 =
                      java.lang.Math.cosh(((double)(t55125)));
                    
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55127 =
                      new x10.lang.Complex((java.lang.System[]) null).$init(t55126,
                                                                            ((double)(0.0)));
                    
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55127;
                } else {
                    
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55128 =
                      z.
                        re;
                    
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55130 =
                      java.lang.Math.cosh(((double)(t55128)));
                    
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55129 =
                      z.
                        im;
                    
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55131 =
                      java.lang.Math.cos(((double)(t55129)));
                    
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55136 =
                      ((t55130) * (((double)(t55131))));
                    
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55132 =
                      z.
                        re;
                    
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55134 =
                      java.lang.Math.sinh(((double)(t55132)));
                    
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55133 =
                      z.
                        im;
                    
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55135 =
                      java.lang.Math.sin(((double)(t55133)));
                    
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55137 =
                      ((t55134) * (((double)(t55135))));
                    
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55138 =
                      new x10.lang.Complex((java.lang.System[]) null).$init(t55136,
                                                                            t55137);
                    
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55138;
                }
            }
        }
        
        
//#line 233 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                              sinh$O(
                                                                                              final double a){try {return java.lang.Math.sinh(a);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 241 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static x10.lang.Complex
                                                                                              sinh(
                                                                                              final x10.lang.Complex z){
            
//#line 242 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55158 =
              z.isNaN$O();
            
//#line 242 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55158) {
                
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55141 =
                  ((x10.lang.Complex)(x10.lang.Complex.getInitialized$NaN()));
                
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55141;
            } else {
                
//#line 244 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55142 =
                  z.
                    im;
                
//#line 244 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55157 =
                  ((double) t55142) ==
                ((double) 0.0);
                
//#line 244 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55157) {
                    
//#line 245 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55143 =
                      z.
                        re;
                    
//#line 245 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55144 =
                      java.lang.Math.sinh(((double)(t55143)));
                    
//#line 245 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55145 =
                      new x10.lang.Complex((java.lang.System[]) null).$init(t55144,
                                                                            ((double)(0.0)));
                    
//#line 245 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55145;
                } else {
                    
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55146 =
                      z.
                        re;
                    
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55148 =
                      java.lang.Math.sinh(((double)(t55146)));
                    
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55147 =
                      z.
                        im;
                    
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55149 =
                      java.lang.Math.cos(((double)(t55147)));
                    
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55154 =
                      ((t55148) * (((double)(t55149))));
                    
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55150 =
                      z.
                        re;
                    
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55152 =
                      java.lang.Math.cosh(((double)(t55150)));
                    
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55151 =
                      z.
                        im;
                    
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55153 =
                      java.lang.Math.sin(((double)(t55151)));
                    
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55155 =
                      ((t55152) * (((double)(t55153))));
                    
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55156 =
                      new x10.lang.Complex((java.lang.System[]) null).$init(t55154,
                                                                            t55155);
                    
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55156;
                }
            }
        }
        
        
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                              tanh$O(
                                                                                              final double a){try {return java.lang.Math.tanh(a);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 259 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static x10.lang.Complex
                                                                                              tanh(
                                                                                              final x10.lang.Complex z){
            
//#line 260 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55160 =
              z.isNaN$O();
            
//#line 260 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55160) {
                
//#line 261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55159 =
                  ((x10.lang.Complex)(x10.lang.Complex.getInitialized$NaN()));
                
//#line 261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55159;
            }
            
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55161 =
              z.
                re;
            
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55162 =
              ((2.0) * (((double)(t55161))));
            
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55165 =
              java.lang.Math.cosh(((double)(t55162)));
            
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55163 =
              z.
                im;
            
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55164 =
              ((2.0) * (((double)(t55163))));
            
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55166 =
              java.lang.Math.cos(((double)(t55164)));
            
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double d =
              ((t55165) + (((double)(t55166))));
            
//#line 264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55167 =
              z.
                re;
            
//#line 264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55168 =
              ((2.0) * (((double)(t55167))));
            
//#line 264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55169 =
              java.lang.Math.sinh(((double)(t55168)));
            
//#line 264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55173 =
              ((t55169) / (((double)(d))));
            
//#line 264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55170 =
              z.
                im;
            
//#line 264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55171 =
              ((2.0) * (((double)(t55170))));
            
//#line 264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55172 =
              java.lang.Math.sin(((double)(t55171)));
            
//#line 264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55174 =
              ((t55172) / (((double)(d))));
            
//#line 264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55175 =
              new x10.lang.Complex((java.lang.System[]) null).$init(t55173,
                                                                    t55174);
            
//#line 264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55175;
        }
        
        
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                              sqrt$O(
                                                                                              final double a){try {return java.lang.Math.sqrt(a);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 277 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static x10.lang.Complex
                                                                                              sqrt(
                                                                                              final x10.lang.Complex z){
            
//#line 278 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55198 =
              z.isNaN$O();
            
//#line 278 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55198) {
                
//#line 279 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55176 =
                  ((x10.lang.Complex)(x10.lang.Complex.getInitialized$NaN()));
                
//#line 279 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55176;
            } else {
                
//#line 280 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55177 =
                  ((x10.lang.Complex)(x10.lang.Complex.getInitialized$ZERO()));
                
//#line 280 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55197 =
                  x10.rtt.Equality.equalsequals((z),(t55177));
                
//#line 280 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55197) {
                    
//#line 281 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55178 =
                      ((x10.lang.Complex)(x10.lang.Complex.getInitialized$ZERO()));
                    
//#line 281 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55178;
                } else {
                    
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55179 =
                      z.
                        re;
                    
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55180 =
                      x10.lang.Math.abs$O((double)(t55179));
                    
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55181 =
                      z.abs$O();
                    
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55182 =
                      ((t55180) + (((double)(t55181))));
                    
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55183 =
                      ((t55182) / (((double)(2.0))));
                    
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t =
                      java.lang.Math.sqrt(((double)(t55183)));
                    
//#line 284 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55184 =
                      z.
                        re;
                    
//#line 284 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55196 =
                      ((t55184) >= (((double)(0.0))));
                    
//#line 284 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55196) {
                        
//#line 285 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55185 =
                          z.
                            im;
                        
//#line 285 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55186 =
                          ((2.0) * (((double)(t))));
                        
//#line 285 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55187 =
                          ((t55185) / (((double)(t55186))));
                        
//#line 285 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55188 =
                          new x10.lang.Complex((java.lang.System[]) null).$init(((double)(t)),
                                                                                t55187);
                        
//#line 285 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55188;
                    } else {
                        
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55189 =
                          z.
                            im;
                        
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55190 =
                          x10.lang.Math.abs$O((double)(t55189));
                        
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55191 =
                          ((2.0) * (((double)(t))));
                        
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55193 =
                          ((t55190) / (((double)(t55191))));
                        
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55192 =
                          z.
                            im;
                        
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55194 =
                          java.lang.Math.signum(((double)(t))) == java.lang.Math.signum(((double)(t55192))) ? ((double)(t)) : -1 * ((double)(t));
                        
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55195 =
                          new x10.lang.Complex((java.lang.System[]) null).$init(t55193,
                                                                                t55194);
                        
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55195;
                    }
                }
            }
        }
        
        
//#line 293 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static float
                                                                                              sqrtf$O(
                                                                                              final float a){try {return (float)java.lang.Math.sqrt(a);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 298 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                              cbrt$O(
                                                                                              final double a){try {return java.lang.Math.cbrt(a);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 302 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                              erf$O(
                                                                                              final double a){try {return x10.core.MathUtils.erf(a);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                              erfc$O(
                                                                                              final double a){try {return x10.core.MathUtils.erfc(a);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 310 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                              hypot$O(
                                                                                              final double a,
                                                                                              final double b){try {return java.lang.Math.hypot(a,b);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 314 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                              log$O(
                                                                                              final double a){try {return java.lang.Math.log(a);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 319 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static float
                                                                                              logf$O(
                                                                                              final float a){try {return (float)java.lang.Math.log(a);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static x10.lang.Complex
                                                                                              log(
                                                                                              final x10.lang.Complex a){
            
//#line 331 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55200 =
              a.isNaN$O();
            
//#line 331 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55200) {
                
//#line 332 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55199 =
                  ((x10.lang.Complex)(x10.lang.Complex.getInitialized$NaN()));
                
//#line 332 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55199;
            }
            
//#line 334 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55201 =
              a.abs$O();
            
//#line 334 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55204 =
              java.lang.Math.log(((double)(t55201)));
            
//#line 334 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55202 =
              a.
                im;
            
//#line 334 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55203 =
              a.
                re;
            
//#line 334 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55205 =
              java.lang.Math.atan2(((double)(t55202)),((double)(t55203)));
            
//#line 334 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final x10.lang.Complex t55206 =
              new x10.lang.Complex((java.lang.System[]) null).$init(t55204,
                                                                    t55205);
            
//#line 334 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55206;
        }
        
        
//#line 337 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                              log10$O(
                                                                                              final double a){try {return java.lang.Math.log10(a);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 341 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                              log1p$O(
                                                                                              final double a){try {return java.lang.Math.log1p(a);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 345 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static int
                                                                                              max$O(
                                                                                              final int a,
                                                                                              final int b){
            
//#line 345 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55207 =
              ((a) < (((int)(b))));
            
//#line 345 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
int t55208 =
               0;
            
//#line 345 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55207) {
                
//#line 345 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55208 = b;
            } else {
                
//#line 345 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55208 = a;
            }
            
//#line 345 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final int t55209 =
              t55208;
            
//#line 345 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55209;
        }
        
        
//#line 346 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static int
                                                                                              min$O(
                                                                                              final int a,
                                                                                              final int b){
            
//#line 346 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55210 =
              ((a) < (((int)(b))));
            
//#line 346 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
int t55211 =
               0;
            
//#line 346 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55210) {
                
//#line 346 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55211 = a;
            } else {
                
//#line 346 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55211 = b;
            }
            
//#line 346 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final int t55212 =
              t55211;
            
//#line 346 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55212;
        }
        
        
//#line 347 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static int
                                                                                              max__0$u__1$u$O(
                                                                                              final int a,
                                                                                              final int b){
            
//#line 347 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55213 =
              x10.core.Unsigned.lt(a, ((int)(b)));
            
//#line 347 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
int t55214 =
               0;
            
//#line 347 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55213) {
                
//#line 347 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55214 = b;
            } else {
                
//#line 347 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55214 = a;
            }
            
//#line 347 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final int t55215 =
              t55214;
            
//#line 347 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55215;
        }
        
        
//#line 348 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static int
                                                                                              min__0$u__1$u$O(
                                                                                              final int a,
                                                                                              final int b){
            
//#line 348 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55216 =
              x10.core.Unsigned.lt(a, ((int)(b)));
            
//#line 348 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
int t55217 =
               0;
            
//#line 348 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55216) {
                
//#line 348 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55217 = a;
            } else {
                
//#line 348 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55217 = b;
            }
            
//#line 348 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final int t55218 =
              t55217;
            
//#line 348 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55218;
        }
        
        
//#line 349 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static long
                                                                                              max$O(
                                                                                              final long a,
                                                                                              final long b){
            
//#line 349 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55219 =
              ((a) < (((long)(b))));
            
//#line 349 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
long t55220 =
               0;
            
//#line 349 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55219) {
                
//#line 349 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55220 = b;
            } else {
                
//#line 349 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55220 = a;
            }
            
//#line 349 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final long t55221 =
              t55220;
            
//#line 349 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55221;
        }
        
        
//#line 350 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static long
                                                                                              min$O(
                                                                                              final long a,
                                                                                              final long b){
            
//#line 350 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55222 =
              ((a) < (((long)(b))));
            
//#line 350 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
long t55223 =
               0;
            
//#line 350 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55222) {
                
//#line 350 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55223 = a;
            } else {
                
//#line 350 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55223 = b;
            }
            
//#line 350 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final long t55224 =
              t55223;
            
//#line 350 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55224;
        }
        
        
//#line 351 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static long
                                                                                              max__0$u__1$u$O(
                                                                                              final long a,
                                                                                              final long b){
            
//#line 351 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55225 =
              x10.core.Unsigned.lt(a, ((long)(b)));
            
//#line 351 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
long t55226 =
               0;
            
//#line 351 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55225) {
                
//#line 351 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55226 = b;
            } else {
                
//#line 351 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55226 = a;
            }
            
//#line 351 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final long t55227 =
              t55226;
            
//#line 351 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55227;
        }
        
        
//#line 352 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static long
                                                                                              min__0$u__1$u$O(
                                                                                              final long a,
                                                                                              final long b){
            
//#line 352 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55228 =
              x10.core.Unsigned.lt(a, ((long)(b)));
            
//#line 352 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
long t55229 =
               0;
            
//#line 352 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55228) {
                
//#line 352 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55229 = a;
            } else {
                
//#line 352 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55229 = b;
            }
            
//#line 352 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final long t55230 =
              t55229;
            
//#line 352 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55230;
        }
        
        
//#line 353 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static float
                                                                                              max$O(
                                                                                              final float a,
                                                                                              final float b){
            
//#line 353 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55231 =
              ((a) < (((float)(b))));
            
//#line 353 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
float t55232 =
               0;
            
//#line 353 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55231) {
                
//#line 353 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55232 = b;
            } else {
                
//#line 353 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55232 = a;
            }
            
//#line 353 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final float t55233 =
              t55232;
            
//#line 353 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55233;
        }
        
        
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static float
                                                                                              min$O(
                                                                                              final float a,
                                                                                              final float b){
            
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55234 =
              ((a) < (((float)(b))));
            
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
float t55235 =
               0;
            
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55234) {
                
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55235 = a;
            } else {
                
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55235 = b;
            }
            
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final float t55236 =
              t55235;
            
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55236;
        }
        
        
//#line 355 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                              max$O(
                                                                                              final double a,
                                                                                              final double b){
            
//#line 355 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55237 =
              ((a) < (((double)(b))));
            
//#line 355 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
double t55238 =
               0;
            
//#line 355 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55237) {
                
//#line 355 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55238 = b;
            } else {
                
//#line 355 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55238 = a;
            }
            
//#line 355 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55239 =
              t55238;
            
//#line 355 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55239;
        }
        
        
//#line 356 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                              min$O(
                                                                                              final double a,
                                                                                              final double b){
            
//#line 356 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55240 =
              ((a) < (((double)(b))));
            
//#line 356 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
double t55241 =
               0;
            
//#line 356 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55240) {
                
//#line 356 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55241 = a;
            } else {
                
//#line 356 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55241 = b;
            }
            
//#line 356 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final double t55242 =
              t55241;
            
//#line 356 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55242;
        }
        
        
//#line 358 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static int
                                                                                              signum$O(
                                                                                              final int a){
            
//#line 358 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55245 =
              ((int) a) ==
            ((int) 0);
            
//#line 358 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
int t55246 =
               0;
            
//#line 358 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55245) {
                
//#line 358 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55246 = 0;
            } else {
                
//#line 358 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55243 =
                  ((a) > (((int)(0))));
                
//#line 358 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
int t55244 =
                   0;
                
//#line 358 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55243) {
                    
//#line 358 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55244 = 1;
                } else {
                    
//#line 358 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55244 = -1;
                }
                
//#line 358 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55246 = t55244;
            }
            
//#line 358 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final int t55247 =
              t55246;
            
//#line 358 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55247;
        }
        
        
//#line 359 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static int
                                                                                              signum$O(
                                                                                              final long a){
            
//#line 359 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55250 =
              ((long) a) ==
            ((long) 0L);
            
//#line 359 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
int t55251 =
               0;
            
//#line 359 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55250) {
                
//#line 359 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55251 = 0;
            } else {
                
//#line 359 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55248 =
                  ((a) > (((long)(0L))));
                
//#line 359 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
int t55249 =
                   0;
                
//#line 359 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55248) {
                    
//#line 359 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55249 = 1;
                } else {
                    
//#line 359 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55249 = -1;
                }
                
//#line 359 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55251 = t55249;
            }
            
//#line 359 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final int t55252 =
              t55251;
            
//#line 359 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55252;
        }
        
        
//#line 360 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static int
                                                                                              signum$O(
                                                                                              final float a){
            
//#line 360 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55255 =
              ((float) a) ==
            ((float) 0.0F);
            
//#line 360 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
int t55256 =
               0;
            
//#line 360 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55255) {
                
//#line 360 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55256 = 0;
            } else {
                
//#line 360 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55253 =
                  ((a) > (((float)(0.0F))));
                
//#line 360 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
int t55254 =
                   0;
                
//#line 360 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55253) {
                    
//#line 360 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55254 = 1;
                } else {
                    
//#line 360 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55254 = -1;
                }
                
//#line 360 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55256 = t55254;
            }
            
//#line 360 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final int t55257 =
              t55256;
            
//#line 360 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55257;
        }
        
        
//#line 361 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static int
                                                                                              signum$O(
                                                                                              final double a){
            
//#line 361 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55260 =
              ((double) a) ==
            ((double) 0.0);
            
//#line 361 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
int t55261 =
               0;
            
//#line 361 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55260) {
                
//#line 361 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55261 = 0;
            } else {
                
//#line 361 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55258 =
                  ((a) > (((double)(0.0))));
                
//#line 361 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
int t55259 =
                   0;
                
//#line 361 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55258) {
                    
//#line 361 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55259 = 1;
                } else {
                    
//#line 361 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55259 = -1;
                }
                
//#line 361 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
t55261 = t55259;
            }
            
//#line 361 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final int t55262 =
              t55261;
            
//#line 361 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55262;
        }
        
        
//#line 366 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static double
                                                                                              copySign$O(
                                                                                              final double a,
                                                                                              final double b){try {return java.lang.Math.signum(a) == java.lang.Math.signum(b) ? a : -1 * a;}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 370 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static int
                                                                                              nextPowerOf2$O(
                                                                                              final int p){
            
//#line 371 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55263 =
              ((int) p) ==
            ((int) 0);
            
//#line 371 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (t55263) {
                
//#line 371 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return 0;
            }
            
//#line 372 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
int pow2 =
              1;
            
//#line 373 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
while (true) {
                
//#line 373 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final int t55264 =
                  pow2;
                
//#line 373 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55267 =
                  ((t55264) < (((int)(p))));
                
//#line 373 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (!(t55267)) {
                    
//#line 373 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
break;
                }
                
//#line 374 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final int t55280 =
                  pow2;
                
//#line 374 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final int t55281 =
                  ((t55280) << (((int)(1))));
                
//#line 374 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
pow2 = t55281;
            }
            
//#line 375 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final int t55268 =
              pow2;
            
//#line 375 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55268;
        }
        
        
//#line 378 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static boolean
                                                                                              powerOf2$O(
                                                                                              final int p){
            
//#line 379 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final int t55269 =
              (-(p));
            
//#line 379 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final int t55270 =
              ((p) & (((int)(t55269))));
            
//#line 379 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55271 =
              ((int) t55270) ==
            ((int) p);
            
//#line 379 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55271;
        }
        
        
//#line 381 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static int
                                                                                              log2$O(
                                                                                              int p){
            
//#line 382 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
assert x10.lang.Math.powerOf2$O((int)(p));
            
//#line 383 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
int i =
              0;
            
//#line 384 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
while (true) {
                
//#line 384 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final int t55272 =
                  p;
                
//#line 384 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final boolean t55277 =
                  ((t55272) > (((int)(1))));
                
//#line 384 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
if (!(t55277)) {
                    
//#line 384 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
break;
                }
                
//#line 384 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final int t55282 =
                  p;
                
//#line 384 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final int t55283 =
                  ((t55282) / (((int)(2))));
                
//#line 384 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
p = t55283;
                
//#line 384 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final int t55284 =
                  i;
                
//#line 384 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final int t55285 =
                  ((t55284) + (((int)(1))));
                
//#line 384 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
i = t55285;
            }
            
//#line 385 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final int t55278 =
              i;
            
//#line 385 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55278;
        }
        
        
//#line 388 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
public static int
                                                                                              pow2$O(
                                                                                              final int i){
            
//#line 389 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final int t55279 =
              ((1) << (((int)(i))));
            
//#line 389 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return t55279;
        }
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
final public x10.lang.Math
                                                                                             x10$lang$Math$$x10$lang$Math$this(
                                                                                             ){
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
return x10.lang.Math.this;
        }
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"
// creation method for java code (1-phase java constructor)
        public Math(){this((java.lang.System[]) null);
                          $init();}
        
        // constructor for non-virtual call
        final public x10.lang.Math x10$lang$Math$$init$S() { {
                                                                    
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"

                                                                    
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Math.x10"

                                                                }
                                                                return this;
                                                                }
        
        // constructor
        public x10.lang.Math $init(){return x10$lang$Math$$init$S();}
        
        
        public static double
          getInitialized$E(
          ){
            return x10.lang.Math.E;
        }
        
        public static double
          getInitialized$PI(
          ){
            return x10.lang.Math.PI;
        }
    
}
