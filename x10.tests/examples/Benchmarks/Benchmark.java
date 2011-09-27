
@x10.core.X10Generated abstract public class Benchmark extends harness.x10Test implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Benchmark.class);
    
    public static final x10.rtt.RuntimeType<Benchmark> $RTT = new x10.rtt.NamedType<Benchmark>(
    "Benchmark", /* base class */Benchmark.class
    , /* parents */ new x10.rtt.Type[] {harness.x10Test.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Benchmark $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        harness.x10Test.$_deserialize_body($_obj, $deserializer);
        x10.io.Printer out = (x10.io.Printer) $deserializer.readRef();
        $_obj.out = out;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        return null;
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        if (out instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.out);
        } else {
        $serializer.write(this.out);
        }
        
    }
    
    // constructor just for allocation
    public Benchmark(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 29 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
abstract public double
                                                                                                  once$O(
                                                                                                  );
        
        
//#line 30 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
abstract public double
                                                                                                  expected$O(
                                                                                                  );
        
        
//#line 31 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
abstract public double
                                                                                                  operations$O(
                                                                                                  );
        
        
//#line 33 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
public double
                                                                                                  now$O(
                                                                                                  ){
            
//#line 37 . "/home/dgrove/x10-trunk/x10.dist/stdlib/x10.jar:x10/lang/System.x10"
final long t12356 =
              java.lang.System.nanoTime();
            
//#line 33 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double t12357 =
              ((double)(long)(((long)(t12356))));
            
//#line 33 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double t12358 =
              ((t12357) * (((double)(1.0E-9))));
            
//#line 33 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
return t12358;
        }
        
        
//#line 34 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
public x10.io.Printer out;
        
//#line 38 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final public static java.lang.String lg = "";
        
//#line 40 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final public static double WARMUP = 30.0;
        
//#line 41 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final public static double TIMING = 10.0;
        
        
//#line 43 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"

        // constructor for non-virtual call
        final public Benchmark Benchmark$$init$S() { {
                                                            
//#line 43 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
super.$init();
                                                            
//#line 43 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"

                                                            
//#line 44 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final x10.io.Printer t12359 =
                                                              ((x10.io.Printer)(x10.io.Console.OUT));
                                                            
//#line 44 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
this.out = ((x10.io.Printer)(t12359));
                                                        }
                                                        return this;
                                                        }
        
        // constructor
        public Benchmark $init(){return Benchmark$$init$S();}
        
        
        
//#line 47 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
public boolean
                                                                                                  run$O(
                                                                                                  ){
            
//#line 50 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final x10.io.Printer t12360 =
              ((x10.io.Printer)(out));
            
//#line 50 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
t12360.println(((java.lang.String)("functional check")));
            
//#line 51 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double warmup =
              this.now$O();
            
//#line 52 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double result =
              this.once$O();
            
//#line 53 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double t12361 =
              this.expected$O();
            
//#line 53 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final boolean t12367 =
              ((double) result) !=
            ((double) t12361);
            
//#line 53 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
if (t12367) {
                
//#line 54 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final x10.io.Printer t12365 =
                  ((x10.io.Printer)(out));
                
//#line 54 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final java.lang.String t12362 =
                  (("got ") + ((x10.core.Double.$box(result))));
                
//#line 54 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final java.lang.String t12363 =
                  ((t12362) + ("; expected "));
                
//#line 54 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double t12364 =
                  this.expected$O();
                
//#line 54 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final java.lang.String t12366 =
                  ((t12363) + ((x10.core.Double.$box(t12364))));
                
//#line 54 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
t12365.println(((java.lang.String)(t12366)));
                
//#line 55 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
return false;
            }
            
//#line 59 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final java.lang.String t12368 =
              ((java.lang.String)("java"));
            
//#line 59 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final boolean t12375 =
              (t12368).equals("java");
            
//#line 59 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
if (t12375) {
                
//#line 60 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final x10.io.Printer t12370 =
                  ((x10.io.Printer)(out));
                
//#line 60 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final java.lang.String t12369 =
                  "warmup for >30.0";
                
//#line 60 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final java.lang.String t12371 =
                  "warmup for >30.0s";
                
//#line 60 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
t12370.println(((java.lang.String)(t12371)));
                
//#line 61 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
while (true) {
                    
//#line 61 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double t12372 =
                      this.now$O();
                    
//#line 61 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double t12373 =
                      ((t12372) - (((double)(warmup))));
                    
//#line 61 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final boolean t12374 =
                      ((t12373) < (((double)(30.0))));
                    
//#line 61 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
if (!(t12374)) {
                        
//#line 61 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
break;
                    }
                    
//#line 62 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
this.once$O();
                }
            }
            
//#line 66 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final x10.io.Printer t12377 =
              ((x10.io.Printer)(out));
            
//#line 66 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final java.lang.String t12376 =
              "timing for >10.0";
            
//#line 66 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final java.lang.String t12378 =
              "timing for >10.0s";
            
//#line 66 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
t12377.println(((java.lang.String)(t12378)));
            
//#line 67 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
double avg =
              0.0;
            
//#line 68 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
double min =
              java.lang.Double.POSITIVE_INFINITY;
            
//#line 69 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
int count =
              0;
            
//#line 70 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
while (true) {
                
//#line 70 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double t12379 =
                  avg;
                
//#line 70 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final boolean t12387 =
                  ((t12379) < (((double)(10.0))));
                
//#line 70 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
if (!(t12387)) {
                    
//#line 70 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
break;
                }
                
//#line 71 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double start12411 =
                  this.now$O();
                
//#line 72 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
this.once$O();
                
//#line 73 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double t12412 =
                  this.now$O();
                
//#line 73 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double t12413 =
                  ((t12412) - (((double)(start12411))));
                
//#line 74 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double t12414 =
                  min;
                
//#line 74 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final boolean t12415 =
                  ((t12413) < (((double)(t12414))));
                
//#line 74 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
if (t12415) {
                    
//#line 75 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
min = t12413;
                }
                
//#line 76 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double t12416 =
                  avg;
                
//#line 76 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double t12417 =
                  ((t12416) + (((double)(t12413))));
                
//#line 76 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
avg = t12417;
                
//#line 77 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final int t12418 =
                  count;
                
//#line 77 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final int t12419 =
                  ((t12418) + (((int)(1))));
                
//#line 77 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
count = t12419;
            }
            
//#line 79 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double t12389 =
              avg;
            
//#line 79 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final int t12388 =
              count;
            
//#line 79 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double t12390 =
              ((double)(int)(((int)(t12388))));
            
//#line 79 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double t12391 =
              ((t12389) / (((double)(t12390))));
            
//#line 79 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
avg = t12391;
            
//#line 82 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double t12392 =
              this.operations$O();
            
//#line 82 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double t12393 =
              avg;
            
//#line 82 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double ops =
              ((t12392) / (((double)(t12393))));
            
//#line 83 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final x10.io.Printer t12396 =
              ((x10.io.Printer)(out));
            
//#line 83 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double t12397 =
              avg;
            
//#line 83 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final int t12398 =
              count;
            
//#line 83 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double t12394 =
              min;
            
//#line 83 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double t12395 =
              avg;
            
//#line 83 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double t12399 =
              ((t12394) / (((double)(t12395))));
            
//#line 83 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
t12396.printf(((java.lang.String)("time: %.3f; count: %d; min/time: %.2f\n")),
                                                                                                                  x10.core.Double.$box(t12397),
                                                                                                                  x10.core.Int.$box(t12398),
                                                                                                                  x10.core.Double.$box(t12399));
            
//#line 84 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final boolean t12407 =
              ((ops) < (((double)(1000000.0))));
            
//#line 84 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
if (t12407) {
                
//#line 84 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final x10.io.Printer t12400 =
                  ((x10.io.Printer)(out));
                
//#line 84 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double t12401 =
                  ((ops) / (((double)(1000.0))));
                
//#line 84 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
t12400.printf(((java.lang.String)("%.3g kop/s\n")),
                                                                                                                      x10.core.Double.$box(t12401));
            } else {
                
//#line 85 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final boolean t12406 =
                  ((ops) < (((double)(1.0E9))));
                
//#line 85 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
if (t12406) {
                    
//#line 85 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final x10.io.Printer t12402 =
                      ((x10.io.Printer)(out));
                    
//#line 85 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double t12403 =
                      ((ops) / (((double)(1000000.0))));
                    
//#line 85 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
t12402.printf(((java.lang.String)("%.3g Mop/s\n")),
                                                                                                                          x10.core.Double.$box(t12403));
                } else {
                    
//#line 86 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final x10.io.Printer t12404 =
                      ((x10.io.Printer)(out));
                    
//#line 86 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final double t12405 =
                      ((ops) / (((double)(1.0E9))));
                    
//#line 86 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
t12404.printf(((java.lang.String)("%.3g Gop/s\n")),
                                                                                                                          x10.core.Double.$box(t12405));
                }
            }
            
//#line 87 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final x10.io.Printer t12408 =
              ((x10.io.Printer)(out));
            
//#line 87 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final java.lang.String t12409 =
              x10.rtt.Types.typeName(this);
            
//#line 87 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final java.lang.String t12410 =
              ((java.lang.String)("java"));
            
//#line 87 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
t12408.printf(((java.lang.String)("test=%s lg=x10-%s ops=%g\n")),
                                                                                                                  ((java.lang.Object)(t12409)),
                                                                                                                  ((java.lang.Object)(t12410)),
                                                                                                                  x10.core.Double.$box(ops));
            
//#line 90 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
return true;
        }
        
        
//#line 27 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
final public Benchmark
                                                                                                  Benchmark$$Benchmark$this(
                                                                                                  ){
            
//#line 27 "/home/dgrove/x10-2-2-1-branch/x10.tests/examples/Benchmarks/Benchmark.x10"
return Benchmark.this;
        }
        
        public static java.lang.String
          getInitialized$lg(
          ){
            return "java";
        }
        
        public static double
          getInitialized$WARMUP(
          ){
            return Benchmark.WARMUP;
        }
        
        public static double
          getInitialized$TIMING(
          ){
            return Benchmark.TIMING;
        }
    
}
