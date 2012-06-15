package x10.lang;


@x10.core.X10Generated final public class Configuration extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Configuration.class);
    
    public static final x10.rtt.RuntimeType<Configuration> $RTT = x10.rtt.NamedType.<Configuration> make(
    "x10.lang.Configuration", /* base class */Configuration.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Configuration $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Configuration.class + " calling"); } 
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Configuration $_obj = new Configuration((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        
    }
    
    // constructor just for allocation
    public Configuration(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final public static int PLATFORM_MAX_THREADS = java.lang.Integer.MAX_VALUE;
        
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final public static boolean DEFAULT_STATIC_THREADS = false;
        
        
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
public static x10.util.HashMap<java.lang.String, java.lang.String>
                                                                                                      loadEnv(
                                                                                                      ){try {return x10.runtime.impl.java.Runtime.loadenv();}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
public static boolean
                                                                                                      envOrElse$O(
                                                                                                      final java.lang.String s,
                                                                                                      final boolean b){
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
try {try {{
                
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final x10.util.HashMap<java.lang.String, java.lang.String> t51797 =
                  ((x10.util.HashMap)(x10.lang.Runtime.env));
                
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final java.lang.String v =
                  ((x10.util.HashMap<java.lang.String, java.lang.String>)t51797).getOrThrow__0x10$util$HashMap$$K$G(((java.lang.String)(s)));
                
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
boolean t51798 =
                  (v).equalsIgnoreCase("false");
                
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
if (!(t51798)) {
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
t51798 = (v).equalsIgnoreCase("f");
                }
                
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
boolean t51799 =
                  t51798;
                
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
if (!(t51799)) {
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
t51799 = (v).equals("0");
                }
                
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final boolean t51800 =
                  t51799;
                
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final boolean t51801 =
                  !(t51800);
                
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
return t51801;
            }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Exception $exc$) {throw x10.core.ThrowableUtilities.convertJavaException($exc$);}}catch (final x10.util.NoSuchElementException id$108) {
                
            }
            
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
return b;
        }
        
        
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
public static boolean
                                                                                                      strict_finish$O(
                                                                                                      ){
            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final boolean t51802 =
              x10.lang.Configuration.envOrElse$O(((java.lang.String)("X10_STRICT_FINISH")),
                                                 (boolean)(false));
            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
return t51802;
        }
        
        
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
public static boolean
                                                                                                      static_threads$O(
                                                                                                      ){
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final boolean t51803 =
              x10.lang.Configuration.DEFAULT_STATIC_THREADS;
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final boolean t51804 =
              x10.lang.Configuration.envOrElse$O(((java.lang.String)("X10_STATIC_THREADS")),
                                                 (boolean)(t51803));
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
return t51804;
        }
        
        
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
public static boolean
                                                                                                      warn_on_thread_creation$O(
                                                                                                      ){
            
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final boolean t51805 =
              x10.lang.Configuration.envOrElse$O(((java.lang.String)("X10_WARN_ON_THREAD_CREATION")),
                                                 (boolean)(false));
            
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
return t51805;
        }
        
        
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
public static boolean
                                                                                                      busy_waiting$O(
                                                                                                      ){
            
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final boolean t51806 =
              x10.lang.Configuration.envOrElse$O(((java.lang.String)("X10_BUSY_WAITING")),
                                                 (boolean)(false));
            
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
return t51806;
        }
        
        
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
public static int
                                                                                                      nthreads$O(
                                                                                                      ){
            
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
int v =
              0;
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
try {try {{
                
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final x10.util.HashMap<java.lang.String, java.lang.String> t51807 =
                  ((x10.util.HashMap)(x10.lang.Runtime.env));
                
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final java.lang.String t51808 =
                  ((x10.util.HashMap<java.lang.String, java.lang.String>)t51807).getOrThrow__0x10$util$HashMap$$K$G(((java.lang.String)("X10_NTHREADS")));
                
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final int t51809 =
                  java.lang.Integer.parseInt(t51808);
                
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
v = t51809;
            }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Exception $exc$) {throw x10.core.ThrowableUtilities.convertJavaException($exc$);}}catch (final x10.util.NoSuchElementException id$109) {
                
            }catch (final x10.lang.NumberFormatException id$110) {
                
            }
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final int t51810 =
              v;
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final boolean t51811 =
              ((t51810) <= (((int)(0))));
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
if (t51811) {
                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
v = 1;
            }
            
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final int t51812 =
              v;
            
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final int t51813 =
              x10.lang.Configuration.PLATFORM_MAX_THREADS;
            
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final boolean t51815 =
              ((t51812) > (((int)(t51813))));
            
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
if (t51815) {
                
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final int t51814 =
                  x10.lang.Configuration.PLATFORM_MAX_THREADS;
                
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
v = t51814;
            }
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final int t51816 =
              v;
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
return t51816;
        }
        
        
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
public static int
                                                                                                      max_threads$O(
                                                                                                      ){
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
int v =
              0;
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
try {try {{
                
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final x10.util.HashMap<java.lang.String, java.lang.String> t51817 =
                  ((x10.util.HashMap)(x10.lang.Runtime.env));
                
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final java.lang.String t51818 =
                  ((x10.util.HashMap<java.lang.String, java.lang.String>)t51817).getOrThrow__0x10$util$HashMap$$K$G(((java.lang.String)("X10_MAX_THREADS")));
                
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final int t51819 =
                  java.lang.Integer.parseInt(t51818);
                
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
v = t51819;
            }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Exception $exc$) {throw x10.core.ThrowableUtilities.convertJavaException($exc$);}}catch (final x10.util.NoSuchElementException id$111) {
                
            }catch (final x10.lang.NumberFormatException id$112) {
                
            }
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final int t51820 =
              v;
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final boolean t51822 =
              ((t51820) <= (((int)(0))));
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
if (t51822) {
                
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final int t51821 =
                  x10.lang.Configuration.nthreads$O();
                
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
v = t51821;
            }
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final boolean t51823 =
              x10.lang.Configuration.static_threads$O();
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
boolean t51825 =
              !(t51823);
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
if (t51825) {
                
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final int t51824 =
                  v;
                
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
t51825 = ((t51824) < (((int)(1000))));
            }
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final boolean t51826 =
              t51825;
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
if (t51826) {
                
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
v = 1000;
            }
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final int t51827 =
              v;
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final int t51828 =
              x10.lang.Configuration.PLATFORM_MAX_THREADS;
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final boolean t51830 =
              ((t51827) > (((int)(t51828))));
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
if (t51830) {
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final int t51829 =
                  x10.lang.Configuration.PLATFORM_MAX_THREADS;
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
v = t51829;
            }
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final int t51831 =
              v;
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
return t51831;
        }
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
final public x10.lang.Configuration
                                                                                                      x10$lang$Configuration$$x10$lang$Configuration$this(
                                                                                                      ){
            
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
return x10.lang.Configuration.this;
        }
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"
// creation method for java code (1-phase java constructor)
        public Configuration(){this((java.lang.System[]) null);
                                   $init();}
        
        // constructor for non-virtual call
        final public x10.lang.Configuration x10$lang$Configuration$$init$S() { {
                                                                                      
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"

                                                                                      
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Configuration.x10"

                                                                                  }
                                                                                  return this;
                                                                                  }
        
        // constructor
        public x10.lang.Configuration $init(){return x10$lang$Configuration$$init$S();}
        
        
        public static int
          getInitialized$PLATFORM_MAX_THREADS(
          ){
            return x10.lang.Configuration.PLATFORM_MAX_THREADS;
        }
        
        public static boolean
          getInitialized$DEFAULT_STATIC_THREADS(
          ){
            return x10.lang.Configuration.DEFAULT_STATIC_THREADS;
        }
    
}
