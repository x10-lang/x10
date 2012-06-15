package x10.lang;


@x10.core.X10Generated public class Complex extends x10.core.Struct implements x10.lang.Arithmetic, x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Complex.class);
    
    public static final x10.rtt.RuntimeType<Complex> $RTT = x10.rtt.NamedType.<Complex> make(
    "x10.lang.Complex", /* base class */Complex.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Arithmetic.$RTT, x10.rtt.UnresolvedType.THIS), x10.rtt.Types.STRUCT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Complex $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Complex.class + " calling"); } 
        $_obj.re = $deserializer.readDouble();
        $_obj.im = $deserializer.readDouble();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Complex $_obj = new Complex((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.write(this.re);
        $serializer.write(this.im);
        
    }
    
    // zero value constructor
    public Complex(final java.lang.System $dummy) { this.re = 0.0; this.im = 0.0; }
    // constructor just for allocation
    public Complex(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    // dispatcher for method abstract public x10.lang.Arithmetic.operator+(that:T):T
    public java.lang.Object $plus(final java.lang.Object a1, final x10.rtt.Type t1) {
    return $plus((x10.lang.Complex)a1);
    }
    // dispatcher for method abstract public x10.lang.Arithmetic.operator-(that:T):T
    public java.lang.Object $minus(final java.lang.Object a1, final x10.rtt.Type t1) {
    return $minus((x10.lang.Complex)a1);
    }
    // dispatcher for method abstract public x10.lang.Arithmetic.operator*(that:T):T
    public java.lang.Object $times(final java.lang.Object a1, final x10.rtt.Type t1) {
    return $times((x10.lang.Complex)a1);
    }
    // dispatcher for method abstract public x10.lang.Arithmetic.operator/(that:T):T
    public java.lang.Object $over(final java.lang.Object a1, final x10.rtt.Type t1) {
    return $over((x10.lang.Complex)a1);
    }
    // bridge for method abstract public x10.lang.Arithmetic.operator+():T
    final public x10.lang.Complex
      $plus$G(){return $plus();}
    // bridge for method abstract public x10.lang.Arithmetic.operator-():T
    final public x10.lang.Complex
      $minus$G(){return $minus();}
    
        
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
/** The real component of this complex number. */
        public double re;
        
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
/** The imaginary component of this complex number. */
        public double im;
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
public static x10.lang.Complex ZERO;
        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
public static x10.lang.Complex ONE;
        
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
public static x10.lang.Complex I;
        
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
public static x10.lang.Complex INF;
        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
public static x10.lang.Complex NaN;
        
        
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
// creation method for java code (1-phase java constructor)
        public Complex(final double real,
                       final double imaginary){this((java.lang.System[]) null);
                                                   $init(real,imaginary);}
        
        // constructor for non-virtual call
        final public x10.lang.Complex x10$lang$Complex$$init$S(final double real,
                                                               final double imaginary) { {
                                                                                                
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
;
                                                                                                
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"

                                                                                                
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
this.re = real;
                                                                                                
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
this.im = imaginary;
                                                                                            }
                                                                                            return this;
                                                                                            }
        
        // constructor
        public x10.lang.Complex $init(final double real,
                                      final double imaginary){return x10$lang$Complex$$init$S(real,imaginary);}
        
        
        
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final public x10.lang.Complex
                                                                                                $plus(
                                                                                                final x10.lang.Complex that){
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51580 =
              re;
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51581 =
              that.
                re;
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51584 =
              ((t51580) + (((double)(t51581))));
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51582 =
              im;
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51583 =
              that.
                im;
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51585 =
              ((t51582) + (((double)(t51583))));
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final x10.lang.Complex t51586 =
              new x10.lang.Complex((java.lang.System[]) null).$init(t51584,
                                                                    t51585);
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51586;
        }
        
        
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final public static x10.lang.Complex
                                                                                                $plus(
                                                                                                final double x,
                                                                                                final x10.lang.Complex y){
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final x10.lang.Complex t51587 =
              y.$plus((double)(x));
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51587;
        }
        
        
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final public x10.lang.Complex
                                                                                                $plus(
                                                                                                final double that){
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51588 =
              re;
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51589 =
              ((t51588) + (((double)(that))));
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51590 =
              im;
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final x10.lang.Complex t51591 =
              new x10.lang.Complex((java.lang.System[]) null).$init(t51589,
                                                                    ((double)(t51590)));
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51591;
        }
        
        
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final public x10.lang.Complex
                                                                                                $minus(
                                                                                                final x10.lang.Complex that){
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51592 =
              re;
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51593 =
              that.
                re;
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51596 =
              ((t51592) - (((double)(t51593))));
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51594 =
              im;
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51595 =
              that.
                im;
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51597 =
              ((t51594) - (((double)(t51595))));
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final x10.lang.Complex t51598 =
              new x10.lang.Complex((java.lang.System[]) null).$init(t51596,
                                                                    t51597);
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51598;
        }
        
        
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final public static x10.lang.Complex
                                                                                                $minus(
                                                                                                final double x,
                                                                                                final x10.lang.Complex y){
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51599 =
              y.
                re;
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51601 =
              ((x) - (((double)(t51599))));
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51600 =
              y.
                im;
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51602 =
              (-(t51600));
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final x10.lang.Complex t51603 =
              new x10.lang.Complex((java.lang.System[]) null).$init(t51601,
                                                                    t51602);
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51603;
        }
        
        
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final public x10.lang.Complex
                                                                                                $minus(
                                                                                                final double that){
            
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51604 =
              re;
            
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51605 =
              ((t51604) - (((double)(that))));
            
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51606 =
              im;
            
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final x10.lang.Complex t51607 =
              new x10.lang.Complex((java.lang.System[]) null).$init(t51605,
                                                                    ((double)(t51606)));
            
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51607;
        }
        
        
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final public x10.lang.Complex
                                                                                                $times(
                                                                                                final x10.lang.Complex that){
            
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51608 =
              re;
            
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51609 =
              that.
                re;
            
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51612 =
              ((t51608) * (((double)(t51609))));
            
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51610 =
              im;
            
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51611 =
              that.
                im;
            
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51613 =
              ((t51610) * (((double)(t51611))));
            
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51620 =
              ((t51612) - (((double)(t51613))));
            
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51614 =
              re;
            
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51615 =
              that.
                im;
            
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51618 =
              ((t51614) * (((double)(t51615))));
            
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51616 =
              im;
            
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51617 =
              that.
                re;
            
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51619 =
              ((t51616) * (((double)(t51617))));
            
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51621 =
              ((t51618) + (((double)(t51619))));
            
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final x10.lang.Complex t51622 =
              new x10.lang.Complex((java.lang.System[]) null).$init(t51620,
                                                                    t51621);
            
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51622;
        }
        
        
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final public static x10.lang.Complex
                                                                                                 $times(
                                                                                                 final double x,
                                                                                                 final x10.lang.Complex y){
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final x10.lang.Complex t51623 =
              y.$times((double)(x));
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51623;
        }
        
        
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final public x10.lang.Complex
                                                                                                 $times(
                                                                                                 final double that){
            
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51624 =
              re;
            
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51626 =
              ((t51624) * (((double)(that))));
            
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51625 =
              im;
            
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51627 =
              ((t51625) * (((double)(that))));
            
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final x10.lang.Complex t51628 =
              new x10.lang.Complex((java.lang.System[]) null).$init(t51626,
                                                                    t51627);
            
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51628;
        }
        
        
//#line 121 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final public x10.lang.Complex
                                                                                                 $over(
                                                                                                 final x10.lang.Complex that){
            
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
boolean t51629 =
              this.isNaN$O();
            
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
if (!(t51629)) {
                
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
t51629 = that.isNaN$O();
            }
            
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final boolean t51631 =
              t51629;
            
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
if (t51631) {
                
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final x10.lang.Complex t51630 =
                  ((x10.lang.Complex)(x10.lang.Complex.getInitialized$NaN()));
                
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51630;
            }
            
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double c =
              that.
                re;
            
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double d =
              that.
                im;
            
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
boolean t51632 =
              ((double) c) ==
            ((double) 0.0);
            
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
if (t51632) {
                
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
t51632 = ((double) d) ==
                ((double) 0.0);
            }
            
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final boolean t51634 =
              t51632;
            
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
if (t51634) {
                
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final x10.lang.Complex t51633 =
                  ((x10.lang.Complex)(x10.lang.Complex.getInitialized$NaN()));
                
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51633;
            }
            
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
boolean t51636 =
              that.isInfinite$O();
            
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
if (t51636) {
                
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final boolean t51635 =
                  this.isInfinite$O();
                
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
t51636 = !(t51635);
            }
            
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final boolean t51638 =
              t51636;
            
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
if (t51638) {
                
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final x10.lang.Complex t51637 =
                  ((x10.lang.Complex)(x10.lang.Complex.getInitialized$ZERO()));
                
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51637;
            }
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51639 =
              x10.lang.Math.abs$O((double)(d));
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51640 =
              x10.lang.Math.abs$O((double)(c));
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final boolean t51678 =
              ((t51639) <= (((double)(t51640))));
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
if (t51678) {
                
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final boolean t51647 =
                  ((double) c) ==
                ((double) 0.0);
                
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
if (t51647) {
                    
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51641 =
                      im;
                    
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51644 =
                      ((t51641) / (((double)(d))));
                    
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51642 =
                      re;
                    
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51643 =
                      (-(t51642));
                    
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51645 =
                      ((t51643) / (((double)(c))));
                    
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final x10.lang.Complex t51646 =
                      new x10.lang.Complex((java.lang.System[]) null).$init(t51644,
                                                                            t51645);
                    
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51646;
                }
                
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double r =
                  ((d) / (((double)(c))));
                
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51648 =
                  ((d) * (((double)(r))));
                
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double denominator =
                  ((c) + (((double)(t51648))));
                
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51650 =
                  re;
                
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51649 =
                  im;
                
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51651 =
                  ((t51649) * (((double)(r))));
                
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51652 =
                  ((t51650) + (((double)(t51651))));
                
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51657 =
                  ((t51652) / (((double)(denominator))));
                
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51654 =
                  im;
                
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51653 =
                  re;
                
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51655 =
                  ((t51653) * (((double)(r))));
                
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51656 =
                  ((t51654) - (((double)(t51655))));
                
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51658 =
                  ((t51656) / (((double)(denominator))));
                
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final x10.lang.Complex t51659 =
                  new x10.lang.Complex((java.lang.System[]) null).$init(t51657,
                                                                        t51658);
                
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51659;
            } else {
                
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final boolean t51665 =
                  ((double) d) ==
                ((double) 0.0);
                
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
if (t51665) {
                    
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51660 =
                      re;
                    
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51662 =
                      ((t51660) / (((double)(c))));
                    
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51661 =
                      im;
                    
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51663 =
                      ((t51661) / (((double)(c))));
                    
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final x10.lang.Complex t51664 =
                      new x10.lang.Complex((java.lang.System[]) null).$init(t51662,
                                                                            t51663);
                    
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51664;
                }
                
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double r =
                  ((c) / (((double)(d))));
                
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51666 =
                  ((c) * (((double)(r))));
                
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double denominator =
                  ((t51666) + (((double)(d))));
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51667 =
                  re;
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51668 =
                  ((t51667) * (((double)(r))));
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51669 =
                  im;
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51670 =
                  ((t51668) + (((double)(t51669))));
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51675 =
                  ((t51670) / (((double)(denominator))));
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51671 =
                  im;
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51672 =
                  ((t51671) * (((double)(r))));
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51673 =
                  re;
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51674 =
                  ((t51672) - (((double)(t51673))));
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51676 =
                  ((t51674) / (((double)(denominator))));
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final x10.lang.Complex t51677 =
                  new x10.lang.Complex((java.lang.System[]) null).$init(t51675,
                                                                        t51676);
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51677;
            }
        }
        
        
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final public static x10.lang.Complex
                                                                                                 $over(
                                                                                                 final double x,
                                                                                                 final x10.lang.Complex y){
            
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final x10.lang.Complex t51679 =
              new x10.lang.Complex((java.lang.System[]) null).$init(((double)(x)),
                                                                    ((double)(0.0)));
            
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final x10.lang.Complex t51680 =
              t51679.$over(((x10.lang.Complex)(y)));
            
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51680;
        }
        
        
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final public x10.lang.Complex
                                                                                                 $over(
                                                                                                 final double that){
            
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51681 =
              re;
            
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51683 =
              ((t51681) / (((double)(that))));
            
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51682 =
              im;
            
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51684 =
              ((t51682) / (((double)(that))));
            
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final x10.lang.Complex t51685 =
              new x10.lang.Complex((java.lang.System[]) null).$init(t51683,
                                                                    t51684);
            
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51685;
        }
        
        
//#line 170 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final public x10.lang.Complex
                                                                                                 conjugate(
                                                                                                 ){
            
//#line 170 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51687 =
              re;
            
//#line 170 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51686 =
              im;
            
//#line 170 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51688 =
              (-(t51686));
            
//#line 170 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final x10.lang.Complex t51689 =
              new x10.lang.Complex((java.lang.System[]) null).$init(((double)(t51687)),
                                                                    t51688);
            
//#line 170 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51689;
        }
        
        
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final public x10.lang.Complex
                                                                                                 $plus(
                                                                                                 ){
            
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return this;
        }
        
        
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final public x10.lang.Complex
                                                                                                 $minus(
                                                                                                 ){
            
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final boolean t51694 =
              this.isNaN$O();
            
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
x10.lang.Complex t51695 =
               null;
            
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
if (t51694) {
                
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
t51695 = x10.lang.Complex.getInitialized$NaN();
            } else {
                
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51690 =
                  re;
                
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51692 =
                  (-(t51690));
                
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51691 =
                  im;
                
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51693 =
                  (-(t51691));
                
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
t51695 = new x10.lang.Complex((java.lang.System[]) null).$init(t51692,
                                                                                                                                                                      t51693);
            }
            
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final x10.lang.Complex t51696 =
              t51695;
            
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51696;
        }
        
        
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final public double
                                                                                                 abs$O(
                                                                                                 ){
            
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final boolean t51698 =
              this.isNaN$O();
            
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
if (t51698) {
                
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51697 =
                  java.lang.Double.NaN;
                
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51697;
            }
            
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final boolean t51700 =
              this.isInfinite$O();
            
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
if (t51700) {
                
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51699 =
                  java.lang.Double.POSITIVE_INFINITY;
                
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51699;
            }
            
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51701 =
              im;
            
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final boolean t51711 =
              ((double) t51701) ==
            ((double) 0.0);
            
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
if (t51711) {
                
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51702 =
                  re;
                
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51703 =
                  x10.lang.Math.abs$O((double)(t51702));
                
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51703;
            } else {
                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51704 =
                  re;
                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final boolean t51710 =
                  ((double) t51704) ==
                ((double) 0.0);
                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
if (t51710) {
                    
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51705 =
                      im;
                    
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51706 =
                      x10.lang.Math.abs$O((double)(t51705));
                    
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51706;
                } else {
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51707 =
                      re;
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51708 =
                      im;
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51709 =
                      java.lang.Math.hypot(((double)(t51707)),((double)(t51708)));
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51709;
                }
            }
        }
        
        
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final public boolean
                                                                                                 isNaN$O(
                                                                                                 ){
            
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51712 =
              re;
            
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
boolean t51714 =
              java.lang.Double.isNaN(t51712);
            
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
if (!(t51714)) {
                
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51713 =
                  im;
                
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
t51714 = java.lang.Double.isNaN(t51713);
            }
            
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final boolean t51715 =
              t51714;
            
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51715;
        }
        
        
//#line 222 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final public boolean
                                                                                                 isInfinite$O(
                                                                                                 ){
            
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final boolean t51716 =
              this.isNaN$O();
            
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
boolean t51720 =
              !(t51716);
            
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
if (t51720) {
                
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51717 =
                  re;
                
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
boolean t51719 =
                  java.lang.Double.isInfinite(t51717);
                
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
if (!(t51719)) {
                    
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51718 =
                      im;
                    
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
t51719 = java.lang.Double.isInfinite(t51718);
                }
                
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
t51720 = t51719;
            }
            
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final boolean t51721 =
              t51720;
            
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51721;
        }
        
        
//#line 230 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final public java.lang.String
                                                                                                 toString(
                                                                                                 ){
            
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51722 =
              re;
            
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final java.lang.String t51723 =
              (("") + ((x10.core.Double.$box(t51722))));
            
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final java.lang.String t51724 =
              ((t51723) + (" + "));
            
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51725 =
              im;
            
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final java.lang.String t51726 =
              ((t51724) + ((x10.core.Double.$box(t51725))));
            
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final java.lang.String t51727 =
              ((t51726) + ("i"));
            
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51727;
        }
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final public java.lang.String
                                                                                                typeName(
                                                                                                ){try {return x10.rtt.Types.typeName(this);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final public int
                                                                                                hashCode(
                                                                                                ){
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
int result =
              1;
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final int t51728 =
              result;
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final int t51730 =
              ((8191) * (((int)(t51728))));
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51729 =
              this.
                re;
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final int t51731 =
              x10.rtt.Types.hashCode(t51729);
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final int t51732 =
              ((t51730) + (((int)(t51731))));
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
result = t51732;
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final int t51733 =
              result;
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final int t51735 =
              ((8191) * (((int)(t51733))));
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51734 =
              this.
                im;
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final int t51736 =
              x10.rtt.Types.hashCode(t51734);
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final int t51737 =
              ((t51735) + (((int)(t51736))));
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
result = t51737;
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final int t51738 =
              result;
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51738;
        }
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final public boolean
                                                                                                equals(
                                                                                                java.lang.Object other){
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final java.lang.Object t51739 =
              other;
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final boolean t51740 =
              x10.lang.Complex.$RTT.isInstance(t51739);
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final boolean t51741 =
              !(t51740);
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
if (t51741) {
                
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return false;
            }
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final java.lang.Object t51742 =
              other;
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final x10.lang.Complex t51743 =
              ((x10.lang.Complex)x10.rtt.Types.asStruct(x10.lang.Complex.$RTT,t51742));
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final boolean t51744 =
              this.equals$O(((x10.lang.Complex)(t51743)));
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51744;
        }
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final public boolean
                                                                                                equals$O(
                                                                                                x10.lang.Complex other){
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51746 =
              this.
                re;
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final x10.lang.Complex t51745 =
              other;
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51747 =
              t51745.
                re;
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
boolean t51751 =
              ((double) t51746) ==
            ((double) t51747);
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
if (t51751) {
                
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51749 =
                  this.
                    im;
                
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final x10.lang.Complex t51748 =
                  other;
                
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51750 =
                  t51748.
                    im;
                
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
t51751 = ((double) t51749) ==
                ((double) t51750);
            }
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final boolean t51752 =
              t51751;
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51752;
        }
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final public boolean
                                                                                                _struct_equals$O(
                                                                                                java.lang.Object other){
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final java.lang.Object t51753 =
              other;
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final boolean t51754 =
              x10.lang.Complex.$RTT.isInstance(t51753);
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final boolean t51755 =
              !(t51754);
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
if (t51755) {
                
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return false;
            }
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final java.lang.Object t51756 =
              other;
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final x10.lang.Complex t51757 =
              ((x10.lang.Complex)x10.rtt.Types.asStruct(x10.lang.Complex.$RTT,t51756));
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final boolean t51758 =
              this._struct_equals$O(((x10.lang.Complex)(t51757)));
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51758;
        }
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final public boolean
                                                                                                _struct_equals$O(
                                                                                                x10.lang.Complex other){
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51760 =
              this.
                re;
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final x10.lang.Complex t51759 =
              other;
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51761 =
              t51759.
                re;
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
boolean t51765 =
              ((double) t51760) ==
            ((double) t51761);
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
if (t51765) {
                
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51763 =
                  this.
                    im;
                
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final x10.lang.Complex t51762 =
                  other;
                
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final double t51764 =
                  t51762.
                    im;
                
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
t51765 = ((double) t51763) ==
                ((double) t51764);
            }
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final boolean t51766 =
              t51765;
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return t51766;
        }
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
final public x10.lang.Complex
                                                                                                x10$lang$Complex$$x10$lang$Complex$this(
                                                                                                ){
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Complex.x10"
return x10.lang.Complex.this;
        }
        
        public static short fieldId$NaN;
        final public static x10.core.concurrent.AtomicInteger initStatus$NaN = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        public static short fieldId$INF;
        final public static x10.core.concurrent.AtomicInteger initStatus$INF = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        public static short fieldId$I;
        final public static x10.core.concurrent.AtomicInteger initStatus$I = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        public static short fieldId$ONE;
        final public static x10.core.concurrent.AtomicInteger initStatus$ONE = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        public static short fieldId$ZERO;
        final public static x10.core.concurrent.AtomicInteger initStatus$ZERO = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        
        public static void
          getDeserialized$ZERO(
          x10.x10rt.X10JavaDeserializer deserializer){
            x10.lang.Complex.ZERO = ((x10.lang.Complex)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
            x10.lang.Complex.initStatus$ZERO.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static x10.lang.Complex
          getInitialized$ZERO(
          ){
            if (((int) x10.lang.Complex.initStatus$ZERO.get()) ==
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                return x10.lang.Complex.ZERO;
            }
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0) &&
                  x10.lang.Complex.initStatus$ZERO.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                 (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                x10.lang.Complex.ZERO = new x10.lang.Complex((java.lang.System[]) null).$init(((double)(0.0)),
                                                                                              ((double)(0.0)));
                if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                      ((boolean) true)) {
                    x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.lang.Complex.ZERO")));
                }
                x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.lang.Complex.ZERO)),
                                                                          (short)(x10.lang.Complex.fieldId$ZERO));
                x10.lang.Complex.initStatus$ZERO.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                x10.runtime.impl.java.InitDispatcher.notifyInitialized();
            } else {
                if (((int) x10.lang.Complex.initStatus$ZERO.get()) !=
                    ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    while (((int) x10.lang.Complex.initStatus$ZERO.get()) !=
                           ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                    }
                    x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                }
            }
            return x10.lang.Complex.ZERO;
        }
        
        public static void
          getDeserialized$ONE(
          x10.x10rt.X10JavaDeserializer deserializer){
            x10.lang.Complex.ONE = ((x10.lang.Complex)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
            x10.lang.Complex.initStatus$ONE.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static x10.lang.Complex
          getInitialized$ONE(
          ){
            if (((int) x10.lang.Complex.initStatus$ONE.get()) ==
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                return x10.lang.Complex.ONE;
            }
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0) &&
                  x10.lang.Complex.initStatus$ONE.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                x10.lang.Complex.ONE = new x10.lang.Complex((java.lang.System[]) null).$init(((double)(1.0)),
                                                                                             ((double)(0.0)));
                if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                      ((boolean) true)) {
                    x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.lang.Complex.ONE")));
                }
                x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.lang.Complex.ONE)),
                                                                          (short)(x10.lang.Complex.fieldId$ONE));
                x10.lang.Complex.initStatus$ONE.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                x10.runtime.impl.java.InitDispatcher.notifyInitialized();
            } else {
                if (((int) x10.lang.Complex.initStatus$ONE.get()) !=
                    ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    while (((int) x10.lang.Complex.initStatus$ONE.get()) !=
                           ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                    }
                    x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                }
            }
            return x10.lang.Complex.ONE;
        }
        
        public static void
          getDeserialized$I(
          x10.x10rt.X10JavaDeserializer deserializer){
            x10.lang.Complex.I = ((x10.lang.Complex)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
            x10.lang.Complex.initStatus$I.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static x10.lang.Complex
          getInitialized$I(
          ){
            if (((int) x10.lang.Complex.initStatus$I.get()) ==
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                return x10.lang.Complex.I;
            }
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0) &&
                  x10.lang.Complex.initStatus$I.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                              (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                x10.lang.Complex.I = new x10.lang.Complex((java.lang.System[]) null).$init(((double)(0.0)),
                                                                                           ((double)(1.0)));
                if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                      ((boolean) true)) {
                    x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.lang.Complex.I")));
                }
                x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.lang.Complex.I)),
                                                                          (short)(x10.lang.Complex.fieldId$I));
                x10.lang.Complex.initStatus$I.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                x10.runtime.impl.java.InitDispatcher.notifyInitialized();
            } else {
                if (((int) x10.lang.Complex.initStatus$I.get()) !=
                    ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    while (((int) x10.lang.Complex.initStatus$I.get()) !=
                           ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                    }
                    x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                }
            }
            return x10.lang.Complex.I;
        }
        
        public static void
          getDeserialized$INF(
          x10.x10rt.X10JavaDeserializer deserializer){
            x10.lang.Complex.INF = ((x10.lang.Complex)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
            x10.lang.Complex.initStatus$INF.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static x10.lang.Complex
          getInitialized$INF(
          ){
            if (((int) x10.lang.Complex.initStatus$INF.get()) ==
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                return x10.lang.Complex.INF;
            }
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0) &&
                  x10.lang.Complex.initStatus$INF.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                x10.lang.Complex.INF = new x10.lang.Complex((java.lang.System[]) null).$init(((double)(java.lang.Double.POSITIVE_INFINITY)),
                                                                                             ((double)(java.lang.Double.POSITIVE_INFINITY)));
                if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                      ((boolean) true)) {
                    x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.lang.Complex.INF")));
                }
                x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.lang.Complex.INF)),
                                                                          (short)(x10.lang.Complex.fieldId$INF));
                x10.lang.Complex.initStatus$INF.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                x10.runtime.impl.java.InitDispatcher.notifyInitialized();
            } else {
                if (((int) x10.lang.Complex.initStatus$INF.get()) !=
                    ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    while (((int) x10.lang.Complex.initStatus$INF.get()) !=
                           ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                    }
                    x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                }
            }
            return x10.lang.Complex.INF;
        }
        
        public static void
          getDeserialized$NaN(
          x10.x10rt.X10JavaDeserializer deserializer){
            x10.lang.Complex.NaN = ((x10.lang.Complex)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
            x10.lang.Complex.initStatus$NaN.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static x10.lang.Complex
          getInitialized$NaN(
          ){
            if (((int) x10.lang.Complex.initStatus$NaN.get()) ==
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                return x10.lang.Complex.NaN;
            }
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0) &&
                  x10.lang.Complex.initStatus$NaN.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                x10.lang.Complex.NaN = new x10.lang.Complex((java.lang.System[]) null).$init(((double)(java.lang.Double.NaN)),
                                                                                             ((double)(java.lang.Double.NaN)));
                if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                      ((boolean) true)) {
                    x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.lang.Complex.NaN")));
                }
                x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.lang.Complex.NaN)),
                                                                          (short)(x10.lang.Complex.fieldId$NaN));
                x10.lang.Complex.initStatus$NaN.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                x10.runtime.impl.java.InitDispatcher.notifyInitialized();
            } else {
                if (((int) x10.lang.Complex.initStatus$NaN.get()) !=
                    ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    while (((int) x10.lang.Complex.initStatus$NaN.get()) !=
                           ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                    }
                    x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                }
            }
            return x10.lang.Complex.NaN;
        }
        
        static {
                   x10.lang.Complex.fieldId$ZERO = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.lang.Complex")),
                                                                                                                       ((java.lang.String)("ZERO")))))));
                   x10.lang.Complex.fieldId$ONE = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.lang.Complex")),
                                                                                                                      ((java.lang.String)("ONE")))))));
                   x10.lang.Complex.fieldId$I = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.lang.Complex")),
                                                                                                                    ((java.lang.String)("I")))))));
                   x10.lang.Complex.fieldId$INF = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.lang.Complex")),
                                                                                                                      ((java.lang.String)("INF")))))));
                   x10.lang.Complex.fieldId$NaN = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.lang.Complex")),
                                                                                                                      ((java.lang.String)("NaN")))))));
               }
    
}
